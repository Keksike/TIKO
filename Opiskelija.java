/*

Pohjana on käytetty kurssilla tarjottua JDBC esimerkkiä.

Ohje:
1. kopioi kotihakemistoon shell.sis.uta.fi:ssa
2. kääntö shell.sis.fi:ssa komennolla: javac Testi.java
3. ajo shell.sis.uta.fi:ssa komennolla: java -classpath /usr/share/java/postgresql.jar:. Testi
*/


import java.sql.*;
import java.util.Scanner;

public class Opiskelija {

      // tietokannan ja käyttäjän tiedot

    private final String AJURI = "org.postgresql.Driver";
    private final String PROTOKOLLA = "jdbc:postgresql:";
    private final String PALVELIN = "dbstud.sis.uta.fi";
    private final int PORTTI = 5432;
    private final String TIETOKANTA = "";  // tähän oma käyttäjätunnus
    private final String KAYTTAJA = "";  // tähän oma käyttäjätunnus
    private final String SALASANA = "";  // tähän tietokannan salasana

    //Haetaan tehtavalista
    public ResultSet haeTehtLista(int listaNro){

        ResultSet rs = null; // Kyselyn tulokset

        // Vaihe 1: tietokanta-ajurin lataaminen
        try {
            Class.forName(AJURI);
        } catch (ClassNotFoundException poikkeus) {
            System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
            return null;
        }

        // Vaihe 2: yhteyden ottaminen tietokantaan
        Connection con = null;
        try {
            con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
          
            Statement stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT id, kuvaus " + "FROM teht_lista + WHERE id = " + listaNro + ";");

            stmt.close();  // sulkee automaattisesti myös tulosjoukon rset

        // Toiminta mahdollisessa virhetilanteessa
        } catch (SQLException poikkeus) {
            System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());     
        }       

        // Vaihe 3: yhteyden sulkeminen 
     
        if (con != null) try {     // jos yhteyden luominen ei onnistunut, con == null
            con.close();
        } catch(SQLException poikkeus) {
            System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");

            return null; //NULL
        }

        return rs; //Palautetaan tulosjoukko

    }

    //Haetaan tehtävä
    public ResultSet haeTehtava(int tehtNro){

        ResultSet rs = null; // Kyselyn tulokset

        // Vaihe 1: tietokanta-ajurin lataaminen
        try {
            Class.forName(AJURI);
        } catch (ClassNotFoundException poikkeus) {
            System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
            return null;
        }

        // Vaihe 2: yhteyden ottaminen tietokantaan
        Connection con = null;
        try {
            con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
          
            Statement stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT id, kuvaus, esim_vastaus " + "FROM tehtava + WHERE id = " + tehtNro + ";");

            stmt.close();  // sulkee automaattisesti myös tulosjoukon rset

        // Toiminta mahdollisessa virhetilanteessa
        } catch (SQLException poikkeus) {
            System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());     
        }       

        // Vaihe 3: yhteyden sulkeminen 
     
        if (con != null) try {     // jos yhteyden luominen ei onnistunut, con == null
            con.close();
        } catch(SQLException poikkeus) {
            System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");

            return null; //NULL
        }

        return rs; //Palautetaan tulosjoukko

    }

    //Hae esimerkkitietokanta
    public ResultSet haeEsimKanta(){

        ResultSet rs = null; // Kyselyn tulokset

        // Vaihe 1: tietokanta-ajurin lataaminen
        try {
            Class.forName(AJURI);
        } catch (ClassNotFoundException poikkeus) {
            System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
            return null;
        }

        // Vaihe 2: yhteyden ottaminen tietokantaan
        Connection con = null;
        try {
            con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
          
            Statement stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * " + "FROM esimkanta;"); //HUOM!!!!!!!!!!!!!!!!!!!!

            stmt.close();  // sulkee automaattisesti myös tulosjoukon rset

        // Toiminta mahdollisessa virhetilanteessa
        } catch (SQLException poikkeus) {
            System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());     
        }       

        // Vaihe 3: yhteyden sulkeminen 
     
        if (con != null) try {     // jos yhteyden luominen ei onnistunut, con == null
            con.close();
        } catch(SQLException poikkeus) {
            System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");

            return null; //NULL
        }

        return rs; //Palautetaan tulosjoukko

    }

    //Laheta Kysely
    public ResultSet lahetaKysely(String kysely){

        ResultSet rs = null; // Kyselyn tulokset

        boolean kyselyOikein = tarkistaKysely(kysely);

        if (kyselyOikein) {
            // Vaihe 1: tietokanta-ajurin lataaminen
            try {
                Class.forName(AJURI);
            } catch (ClassNotFoundException poikkeus) {
                System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
                return null;
            }

            // Vaihe 2: yhteyden ottaminen tietokantaan
            Connection con = null;
            try {
                con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
              
                Statement stmt = con.createStatement();

                // Tarkistetaan onko kyselyssä syntaksivirheitä
                if(tarkistaSyntaksi()){

                    rs = stmt.executeQuery(kysely);
                }else {
                    rs = null;
                }

                stmt.close();  // sulkee automaattisesti myös tulosjoukon rset

            // Toiminta mahdollisessa virhetilanteessa
            } catch (SQLException poikkeus) {
                System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());     
            }       

            // Vaihe 3: yhteyden sulkeminen 
         
            if (con != null) try {     // jos yhteyden luominen ei onnistunut, con == null
                con.close();
            } catch(SQLException poikkeus) {
                System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");

                return null; //null
            }

            return rs; //Palautetaan tulosjoukko
        }
        else
        {
            return null;
        }
    }

    //Tarkistaa kyselyn syntaksivirheiden varalta
    public boolean tarkistaSyntaksi(String kysely){

        char merkki;
        int avausLaskuri;
        int sulkuLaskuri;
        boolean palautus;

        palautus = true;

        for(int i = 0; i < kysely.length(); i++){

            merkki = kysely.charAt(i);

            if (merkki == '(') {
                avausLaskuri++;  
            }
            if (merkki == ')') {
                sulkuLaskuri++;
            }
        }

        if (avausLaskuri != sulkuLaskuri) {
            System.out.println("Kyselyssä täytyy olla parillinen määrä kaarisulkeita.")
            palautus = false;
        }

        merkki = kysely.charAt(kysely.length() - 1);

        if(merkki != ';'){
            System.out.println("Kyselyn täytyy päättyä puolipisteeseen.");
            palautus = false;
        }

        return palautus;
    }


}
