/*
* TIKO-kurssin harjoitustyön luokka, jossa on tietokannallinen toiminnallisuus
*
* author: Jenni Mansikka-aho, Ossi Puustinen & Cihan Bebek
*
* Pohjana on käytetty kurssilla tarjottua JDBC esimerkkiä.
*
*/


import java.sql.*;

public class TietokantaToiminnot {

    // Tietokannan ja käyttäjän tiedot

    private final String AJURI = "org.postgresql.Driver";
    private final String PROTOKOLLA = "jdbc:postgresql:";
    private final String PALVELIN = "dbstud.sis.uta.fi";
    private final int PORTTI = 5432;
    private final String TIETOKANTA = "tiko2014db29";  // tähän tietokannan nimi
    private final String KAYTTAJA = "op96382";  // tähän oma käyttäjätunnus
    private final String SALASANA = "lol";  // tähän tietokannan salasana

    private Connection con;
    private Statement stmt;

    // Yhteyden avaaminen
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
            System.out.println("Yhteyden avaamisessa tapahtui seuraava virhe: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;

    }


   // Yhteyden sulkeminen
   public boolean suljeYhteys(){

        // Jos yhteyden luominen ei onnistunut, con == null
        if (con != null) { 

            try {     
                // Sulkee myös tulosjoukon
                stmt.close();    
                con.close();
                return true;

            } catch(SQLException e) {
                System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");
                e.printStackTrace();
                return false;
            }

        }else{
            System.out.println("Yhteyttä tietokantaan ei ollut. Yhteyden sulkeminen tietokantaan ei onnistunut.");
            return false;
        }
    }


    // Haetaan tehtävälista
    public ResultSet haeTehtLista(int listaNro){
        // Kyselyn tulokset
        ResultSet rs = null; 

        try {
        
            String lause = "SELECT id, kuvaus FROM tehtavalista WHERE id = " + listaNro + ";";
            rs = lahetaKysely(lause);

        } catch (Exception e) {
            System.out.println("Tehtävälistan haussa tapahtui virhe.");
            e.printStackTrace();
            return null;
        }

	//Palautetaan tulosjoukko
        return rs; 
   }

    public ResultSet haeTehtListat(){
        ResultSet rs = null;

        try{
            rs = lahetaKysely("SELECT * FROM tehtavalista;");
            
            if(rs == null){
                System.out.println("Tehtävälistoja ei löytynyt.");
            }

            return rs;
        }catch(Exception e){
            System.out.println("Tehtävälistojen haussa tapahtui virhe");
            e.printStackTrace();
            return null;
        }
    }

    // Haetaan tehtävä
    public ResultSet haeTehtavasarja(int tehtLista){

	// Kyselyn tulokset
        ResultSet rs = null; 
        
        try {            
            String lause = "SELECT tehtava.id, tehtava.kuvaus " + 
            "FROM tehtava, kuuluu, tehtavalista " + 
            " WHERE tehtavalista.id = " + tehtLista + 
            " AND tehtava.id = kuuluu.tehtava_id AND kuuluu.tehtavalista_id = tehtavalista.id;";

            rs = lahetaKysely(lause);

        } catch (Exception e) {
            System.out.println("Tehtävän haussa tapahtui virhe.");
            e.printStackTrace();
            return null;
        }
        
        //Palautetaan tulosjoukko
        return rs;
   }

    // Hae esimerkkitietokanta
    public ResultSet haeEsimKanta(){

	// Kyselyn tulokset
        ResultSet rs = null; 

        
        try {
            String lause = "SELECT DISTINCT TABLE_NAME AS taulu, COLUMN_NAME AS attribuutti " +
			"FROM INFORMATION_SCHEMA.COLUMNS " +
			"WHERE TABLE_NAME = 'opiskelijat' OR TABLE_NAME = 'suoritukset' OR TABLE_NAME = 'kurssit' " +
			"ORDER BY TABLE_NAME;";

            rs = lahetaKysely(lause);

        } catch (Exception e) {
            System.out.println("Esimerkkikannan haussa tapahtui virhe.");
            e.printStackTrace();
            return null;
        }

	// Palautetaan tulosjoukko
        return rs; 
   }
    // Hakee tehtävälistan tehtävien lukumäärän
    public int haeTLkm(int tLista){
      
        int palautus = 0;
        ResultSet rs = lahetaKysely("SELECT teht_lkm FROM tehtavalista WHERE id = " + tLista + ";");
        
        try{
            rs.next();
            palautus = rs.getInt(1);
        }
        catch (SQLException e) {
            System.out.println("Virhe");
            return 0;
        }
      
        return palautus;

    }

    // Hakee kysymystyypin
    public String haeKysTyyppi(int tehtNro, int tlNro){

        String palautus = "";

        ResultSet rs = lahetaKysely("SELECT tehtava.kys_tyyppi FROM tehtava INNER JOIN kuuluu ON tehtava.id = kuuluu.tehtava_id INNER JOIN tehtavalista ON kuuluu.tehtavalista_id" +
            " = tehtavalista.id WHERE kuuluu.tehtavanro = " + tehtNro + " AND tehtavalista.id = " + tlNro + ";");
        
        try{
            rs.next();
            palautus = rs.getString(1);
        }
        catch (SQLException e) {
            System.out.println("Virhe");
            return null;
        }

        return palautus;
    }


    // Lähetä kysely
    public ResultSet lahetaKysely(String kysely){

        // Kyselyn tulokset
        ResultSet rs = null; 
        
        // Tarkistetaan onko kyselyn syntaksi oikeellinen
        if (tarkistaSyntaksi(kysely)) {
            try {
                stmt = con.createStatement();

                // Suoritetaan kysely
                rs = stmt.executeQuery(kysely);
                return rs;

            // Toiminta mahdollisessa virhetilanteessa
            } catch (SQLException e) {
                System.out.println("Kyselyn lähetyksessä tapahtui seuraava SQL-virhe: " + e.getMessage());
                return null;
            }

        } else {
            return null;
        }
    }

    /* Metodi lähettää parametrina annetun SQL-INSERT:in tai UPDATE:n
     * Palauttaa true lähetys onnistuu, palauttaa false jos ei onnistu
     */
    public boolean lahetaKasky(String kaskySQL){

        if(tarkistaSyntaksi(kaskySQL)){
            try{
                stmt = con.createStatement();

                stmt.executeUpdate(kaskySQL);

                return true;
            }catch(SQLException se){
                System.out.println("SQL-käskyn lähetyksessä tapahtui virhe.");
                se.printStackTrace();
                return false;
            }catch(Exception e){
                System.out.println("Käskyn lähetyksessä tapahtui virhe.");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // Tarkistaa kyselyn syntaksivirheiden varalta
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
        if(!palautus){
            System.out.println("Kyselyssä on virheellinen syntaksi. Tarkista mahdolliset kirjoitusvirheet.");
        }

      return palautus;
    }
    
    // Tulostaa resultSetin
    public boolean tulostaRs(ResultSet rs){

        if(rs != null){

            try {

                ResultSetMetaData rsmd = rs.getMetaData();
                int columnit = rsmd.getColumnCount();

                while(rs.next()){

                    for (int i = 1; i <= columnit; i++) {
                        if (i > 1) 
                            System.out.print("  |  ");
                        System.out.print(rs.getString(i));
    		
                    }
                    System.out.println("");
                }
                return true;
            }
            catch (SQLException e) {
                System.out.println("ResultSetin tulostuksessa tapahtui virhe: " + e.getMessage());
                e.printStackTrace();
                return false; 
            }
        }
        else{
            System.out.println("VIRHE: tulosjoukko on tyhjä.");
            return false;
        }
    }

    // Vertaa kahta ResultSettiä.
    public boolean vertaaTulokset(ResultSet rs, ResultSet esim){

        String tulos = "";
        String vastaus = "";
        boolean palautus = true;

        // Tarkistetaan ettei kumpikaan parametri ole tyhjä
        if(rs == null | esim == null){
            return false;
        }
        
        try {
            // Hankitaan tietoja resultSeteistä.
            ResultSetMetaData rsMeta = rs.getMetaData();
            ResultSetMetaData esimMeta = esim.getMetaData();

            int rsColumnit = rsMeta.getColumnCount();
            int esimColumnit = esimMeta.getColumnCount();
			
            String kokovastaus = "";
            String kokotulos = "";
			
            // Jatketaan kunnes molemmat setit loppuvat
            System.out.println("\nOikean vastauksen tuottama tulos:");
            while(esim.next()){
                for (int i = 1; i <= esimColumnit; i++) {
                    vastaus = esim.getString(i);
                    System.out.println(vastaus);
                    kokovastaus += vastaus;
                }
            }
            System.out.println("\nVastauksesi tuottama tulos:");
            while(rs.next()){
                for (int i = 1; i <= rsColumnit; i++) {
                   tulos = rs.getString(i);
                   System.out.println(tulos);
                   kokotulos += tulos;
                }
            }
            if(!kokovastaus.equals(kokotulos)){
                palautus = false;
            }
            
            return palautus;
        
        } catch (SQLException e) {
            System.out.println("Tulosten vertauksessa tapahtui seuraava virhe: " + e.getMessage());
            e.printStackTrace();
            return false;
        }       

   }

    // Parametrinä tehtävälistan id
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

        }catch(SQLException e){
            System.out.println("Tehtävälistan olemassaolon tarkistuksessa tapahtui virhe.");
            e.printStackTrace();
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
        }catch(SQLException e){
            System.out.println("Tehtävän olemassaolon tarkistuksen aikana tapahtui virhe.");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Parametrinä käyttäjätunnus, eli id
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

    // Palauttaa 0 jos ei löydy
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

    /* Parametrinä käyttäjätunnus: k_id ja tehtävälistatunnus: t_id
     * Palauttaa uuden session ID:n. Jos bugaa palauttaa 0.
     */
    public int aloitaSessio(int k_id, int t_id){

        try{

            // Hakee isoimman sessionin id:n resultsettiin
            ResultSet rs = lahetaKysely("SELECT max(id) FROM sessio;");

            // Tähän tallennetaan uusi ID
            int uusiID = 1;

            // Jos vanhaa ID:tä ei löydy, ID on 1
            if(rs.next()){ 
                // Kasvatetaan vanhaa isointa ID:tä yhellä, niin saadaan uusi ID
                uusiID = rs.getInt(1)+1;
            }

            // Ja tähän tämän hetken aika
            java.sql.Time aika = haeAika();

            // id, kayt_id, suoritettu_teht_lista, sessio_alku, sessio_loppu
            String sessioAloitus = "INSERT INTO sessio VALUES (" + uusiID + ", " + k_id + ", " + t_id + ", '" + aika + "', '" + aika + "');";

            lahetaKasky(sessioAloitus);

            return uusiID;

        }catch(SQLException e){
            System.out.println("Session tallennuksessa tapahtui SQL-virhe.");
            e.printStackTrace();
            return 0;
        }
    }

    /*
    * Metodi asettaa session lopetusajaksi tämänhetkisen ajan.
    * Parametrina annetaan session id: s_id
    * Palauttaa true jos session lopetus onnistuu, false jos ei
    */
    public boolean lopetaSessio(int s_id){

        try{
            java.sql.Time aika = haeAika();

            String sessioLopetusSQL = "UPDATE sessio SET sessio_loppu = '" + aika + "' WHERE id = " + s_id + ";";

            return lahetaKasky(sessioLopetusSQL);
        }catch(Exception e){
            System.out.println("Session lopetuksen tallennuksessa tapahtui virhe.");
            e.printStackTrace();
            return false;
        }
    }

    // Hakee ja palauttaa tämänhetkisen ajan, eli pelkän ajan, ei pvm
    public java.sql.Time haeAika(){
        java.util.Date a = new java.util.Date();
        long aikaL = a.getTime();

        java.sql.Time aika = new java.sql.Time(aikaL);

        return aika;
    }
}
