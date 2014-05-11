/*
* TIKO-kurssin harjoitustyön luokka, jossa on tietokannallinen toiminnallisuus
*
* author: Jenni Mansikka-Aho, Ossi Puustinen & Cihan Bebek
*
* Pohjana on käytetty kurssilla tarjottua JDBC esimerkkiä.
*
* Ohje:
* 1. kopioi kotihakemistoon shell.sis.uta.fi:ssa
* 2. kääntö shell.sis.fi:ssa komennolla: javac Testi.java
* 3. ajo shell.sis.uta.fi:ssa komennolla: java -classpath /usr/share/java/postgresql.jar:. Testi
*/


import java.sql.*;
import java.util.Scanner;

public class TietokantaToiminnot {

    // tietokannan ja käyttäjän tiedot

    private final String AJURI = "org.postgresql.Driver";
    private final String PROTOKOLLA = "jdbc:postgresql:";
    private final String PALVELIN = "dbstud.sis.uta.fi";
    private final int PORTTI = 5432;
    private final String TIETOKANTA = "tiko2014db29";  // tähän oma käyttäjätunnus
    private final String KAYTTAJA = "cb96337";  // tähän oma käyttäjätunnus
    private final String SALASANA = "";  // tähän tietokannan salasana
   
    private final String VIRHE = "Tapahtui virhe.";

    private Connection con;
    private Statement stmt;

    //Yhteyden avaaminen
    public boolean avaaYhteys(){


    // Vaihe 1: tietokanta-ajurin lataaminen
    try {
        Class.forName(AJURI);
    } catch (ClassNotFoundException e) {
        System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
        e.printStackTrace();
        return false;
    }

    // Vaihe 2: yhteyden ottaminen tietokantaan
    con = null;
    try {
        con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
        
        stmt = con.createStatement();

    // Toiminta mahdollisessa virhetilanteessa
    } catch (SQLException e) {
        System.out.println("Tapahtui seuraava virhe: " + e.getMessage());
        e.printStackTrace();
        return false;
    }

        return true;

    }


   //Yhteyden sulkeminen
   public boolean suljeYhteys(){

        // yhteyden sulkeminen 
    
        if (con != null) { // jos yhteyden luominen ei onnistunut, con == null

            try {     
                con.close();
                stmt.close(); // Sulkee myös tulosjoukon    
                return true;

            } catch(SQLException e) {
                System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");
                e.printStackTrace();
                return false; //null
            }

        }else{
            System.out.println("Yhteyttä tietokantaan ei ollut. Yhteyden sulkeminen tietokantaan ei onnistunut.");
            return false;
        }
    }


    //Haetaan tehtavalista
    public ResultSet haeTehtLista(int listaNro){

        ResultSet rs = null; // Kyselyn tulokset

        try {
        
            String lause = "SELECT id, kuvaus FROM tehtavalista WHERE id = " + listaNro + ";";
            rs = lahetaKysely(lause);

        } catch (Exception e) {
            System.out.println("Tehtävälistan haussa tapahtui virhe.");
            e.printStackTrace();
            return null;
        }

        return rs; //Palautetaan tulosjoukko

   }

    //Haetaan tehtävä
    public ResultSet haeTehtava(int tehtNro, int tehtLista){

        ResultSet rs = null; // Kyselyn tulokset
        try {            
        String lause = "SELECT tehtava.id, tehtava.kuvaus " + 
            "FROM tehtava, kuuluu, tehtavalista " + 
            "WHERE tehtava.id = " + tehtNro + 
            " AND tehtavalista.id = " + tehtLista + 
            " AND tehtava.id = kuuluu.tehtava_id AND kuuluu.tehtavalista_id = tehtavalista.id;";

        rs = lahetaKysely(lause);

        } catch (Exception e) {
            System.out.println("Tehtävän haussa tapahtui virhe.");
            e.printStackTrace();
            return null;
        }
        
        return rs; //Palautetaan tulosjoukko

   }

    //Hae esimerkkitietokanta
    public ResultSet haeEsimKanta(){

        ResultSet rs = null; // Kyselyn tulokset

        
        try {
         
        String lause = "SELECT * " + "FROM esimkanta;";

        rs = lahetaKysely(lause);

        } catch (Exception e) {
            System.out.println("Esimerkkikannan haussa tapahtui virhe.");
            e.printStackTrace();
            return null;
        }

        return rs; //Palautetaan tulosjoukko

   }
    //Hakee tehtävälistan tehtävien lukumäärän
    public int haeTLkm(int tLista){
      
        int palautus = 0;
        ResultSet rs = lahetaKysely("SELECT teht_lkm FROM tehtavalista WHERE id = " + tLista + ";");
        /*
        try{
            palautus = rs.getInt("teht_lkm");
        }
        catch (SQLException e) {
            System.out.println(VIRHE);
            return 0;
        }
      
        return palautus;
        */

        /*PLACEHOLDER:*/
        return 0;
    }

    //Laheta Kysely
    public ResultSet lahetaKysely(String kysely){

        ResultSet rs = null; // Kyselyn tulokset

        if (tarkistaSyntaksi(kysely)) { //tsiigataan onko kyselyn syntaksi oikeellinen

            try {

                stmt = con.createStatement();

                // Tarkistetaan onko kyselyssä syntaksivirheitä
                if(tarkistaSyntaksi(kysely)){

                    rs = stmt.executeQuery(kysely);
                    return rs;

                }else {
                    rs = null;
                    return rs;
                }

            // Toiminta mahdollisessa virhetilanteessa
            } catch (SQLException poikkeus) {
                System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());     
                return null;
            }

        } else {
            System.out.println("Kyselyssä on virheellinen syntaksi. Tarkista mahdolliset kirjoitusvirheet.");
            return null;
        }
    }

    /*Metodi lähettää parametrina annetun SQL-INSERT:in tai UPDATE:n
    * Palauttaa true lähetys onnistuu, palauttaa false jos ei onnistu
    */
    public boolean lahetaKasky(String kaskySQL){

        if(tarkistaSyntaksi(kaskySQL)){
            try{
                stmt = con.createStatement();

                stmt.executeUpdate(kaskySQL);

                System.out.println("Tietokanta päivitetty.");

                return true;
            }catch(SQLException se){
                se.printStackTrace();
                return false;
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    //Tarkistaa kyselyn syntaksivirheiden varalta
    public boolean tarkistaSyntaksi(String kysely){

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

    /*Parametrinä tehtävälistan id*/
    public boolean onkoTehtavalistaOlemassa(int id){

        try {

            final String tehtavalistaTarkistus = "SELECT count(*) FROM tehtavalista WHERE id = " + id + ";";

            ResultSet rs = lahetaKysely(tehtavalistaTarkistus);

            if (rs.next()) {
                if(rs.getInt(1) > 0){
                    return true;
                }
            }

        return false;

        }catch(SQLException poikkeus){
            System.out.println(VIRHE);
            return false;
        }
    }

    public boolean onkoTehtavaOlemassa(int id){
        try{

            final String tehtavaTarkistus = "SELECT count(*) FROM tehtava WHERE id = " + id + ";";

            ResultSet rs = lahetaKysely(tehtavaTarkistus);
    
            if(rs.next()){
                if(rs.getInt(1) > 0){
                return true;
                }
            }

            return false;

        }catch(SQLException e){
            System.out.println("Tehtävän olemassaolon tarkistuksen aikana tapahtui virhe.");
            e.printStackTrace();
            return false;
        }
    }

    /*Parametrinä käyttäjätunnus, eli id*/
    public boolean onkoKayttajaOlemassa(int id){

        try{

            final String tunnusTarkistus = "SELECT count(*) FROM kayttaja WHERE id = " + id + ";";

            ResultSet rs = lahetaKysely(tunnusTarkistus);

            if(rs.next()){
                if(rs.getInt(1) > 0){
                return true;
            }
        }
        return false;

        }catch(SQLException e){
            System.out.println("Käyttäjän olemassaolon tarkistuksen aikana tapahtui virhe.");
            e.printStackTrace();
            return false;
        }
    }

    /*palauttaa 0 jos ei löydy*/
    public int haeOikeudet(int id){

        try{
            String oikeudetTarkistus = "SELECT oikeudet FROM kayttaja WHERE id = " + id + ";";
            ResultSet rs = lahetaKysely(oikeudetTarkistus);

            if(rs.next()){
                return rs.getInt(1);
            }
            return 0;

        }catch(SQLException e){
            System.out.println("Käyttäjän oikeuksien haun aikana tapahtui virhe");
            e.printStackTrace();
            return 0;
        }
    }

    /*Parametrinä käyttäjätunnus: k_id ja tehtävälistatunnus: t_id*/
    /*Palauttaapi uuden session ID:n. Jos bugaa palauttaa 0.*/
    public int aloitaSessio(int k_id, int t_id){

        try{

            //hakee isoimman sessionin id:n resultsettiin
            ResultSet rs = lahetaKysely("SELECT max(id) FROM sessio;");

            //tähän tallennetaan uusi ID
            int uusiID = 1;

            if(rs.next()){ //jos vanhaa ID:tä ei löydy, ID on 1
                //kasvatetaan vanhaa isointa ID:tä yhellä, niin saadaan uusi ID
                uusiID = rs.getInt(1)+1;
            }

            //ja tähän tämän hetken aika
            java.sql.Time aika = haeAika();

            //puuttuu yritysten nro
            String sessioAloitus = "INSERT INTO sessio (id, kayt_id, suoritettu_teht_lista, sessio_alku, sessio_loppu) VALUES (" + uusiID + ", " + k_id + ", " + t_id + ", " + aika + ", " + aika + ");";

            lahetaKasky(sessioAloitus);

            return uusiID;

        }catch(SQLException e){
            System.out.println("Session tallenuksessa tapahtui bugi.");
            e.printStackTrace();
            return 0;
        }
    }

    /*
    * Metodi asettaa session lopetusajaksi tämänhetkisen ajan.
    * Parametrina annetaan session id: s_id
    * Palauttaa true jos session loeptus onnistuu, false jos ei
    */
    public boolean lopetaSessio(int s_id){

        java.sql.Time aika = haeAika();

        String sessioLopetusSQL = "UPDATE sessio SET sessio_loppu = " + aika + " WHERE id = " + s_id;

        return lahetaKasky(sessioLopetusSQL);
    }

    /*hakee ja palauttaa tämänhetkisen ajan, eli pelkän ajan, ei pvm*/
    public java.sql.Time haeAika(){
        java.util.Date a = new java.util.Date();
        long aikaL = a.getTime();

        java.sql.Time aika = new java.sql.Time(aikaL);

        return aika;
    }
}
