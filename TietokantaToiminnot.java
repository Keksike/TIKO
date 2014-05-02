/*

Pohjana on käytetty kurssilla tarjottua JDBC esimerkkiä.

Ohje:
1. kopioi kotihakemistoon shell.sis.uta.fi:ssa
2. kääntö shell.sis.fi:ssa komennolla: javac Testi.java
3. ajo shell.sis.uta.fi:ssa komennolla: java -classpath /usr/share/java/postgresql.jar:. Testi
*/


import java.sql.*;
import java.util.Scanner;

public class TietokantaToiminnot {

      // tietokannan ja käyttäjän tiedot

    private static final String AJURI = "org.postgresql.Driver";
    private static final String PROTOKOLLA = "jdbc:postgresql:";
    private static final String PALVELIN = "dbstud.sis.uta.fi";
    private static final int PORTTI = 5432;
    private static final String TIETOKANTA = "tiko2014db29";  // tähän oma käyttäjätunnus
    private static final String KAYTTAJA = "";  // tähän oma käyttäjätunnus
    private static final String SALASANA = "";  // tähän tietokannan salasana

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
    public static ResultSet lahetaKysely(String kysely){

        ResultSet rs = null; // Kyselyn tulokset

        if (tarkistaSyntaksi(kysely)) { //tsiigataan onko kyselyn syntaksi oikeellinen
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
                if(tarkistaSyntaksi(kysely)){

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
            System.out.println("Kyselyssä on virheellinen syntaksi. Tarkista mahdolliset kirjoitusvirheet.");
            return null;
        }
    }

    //Tarkistaa kyselyn syntaksivirheiden varalta
    public static boolean tarkistaSyntaksi(String kysely){

        char merkki;
        int avausLaskuri = 0;
        int sulkuLaskuri = 0;
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
            System.out.println("Kyselyssä täytyy olla parillinen määrä kaarisulkeita.");
            palautus = false;
        }

        merkki = kysely.charAt(kysely.length() - 1);

        if(merkki != ';'){
            System.out.println("Kyselyn täytyy päättyä puolipisteeseen.");
            palautus = false;
        }

        return palautus;
    }

    //Vertaa kahta ResultSettiä.
    public boolean vertaaTulokset(ResultSet rs, ResultSet esim){

        String tulos;
        String vastaus;
        int i;
        boolean palautus;

        i = 0;
        palautus = true;
        try {
        while(rs.next() | esim.next()){
            
            i++;
            
            tulos = rs.getString(i);
            vastaus = esim.getString(i);

            if(!(tulos.equals(vastaus))){
                palautus = false;
            }

        }

        //Vastaus oli väärä.
        if (palautus == false) {
            
            System.out.println("Kyselyssä oli looginen virhe.\nVastauksesi palautti tuloksen:\n");
            
            i = 0;

            while(rs.next()){
                i++;
                System.out.println(rs.getString(i));

            }

            System.out.println("Kyselyn tulisi tuottaa tulos:\n");
            
            i = 0;

            while(esim.next()){
                i++;
                System.out.println(esim.getString(i));

            }

            return palautus;
        }
        }
        catch (SQLException poikkeus) {
            System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());     
        }       

        return palautus;

    }

    //tarkistaa löytyykö parametrina annettu käyttäjätunnus tietokannasta
/*    public boolean tarkistaKayttajatunnus(int kId){
        ResultSet rs = null; // Kyselyn tulokset

        // Vaihe 1: tietokanta-ajurin lataaminen
        try {
            Class.forName(AJURI);
        } catch (ClassNotFoundException poikkeus) {
            System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
            return null;
        }



        if (con != null) try {     // jos yhteyden luominen ei onnistunut, con == null
            con.close();
        } catch(SQLException poikkeus) {
            System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");

            return null; //NULL
        }

    }*/

}