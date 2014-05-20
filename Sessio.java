import java.sql.*;
/*java -classpath /usr/share/java/postgresql.jar:. TIKO*/
/*psql tiko2014db29*/


public class Sessio {
   
    private final String VIRHE = "Tapahtui virhe.";
    private TietokantaToiminnot db = new TietokantaToiminnot();

    private int kayttajatunnus;
   
    public void suoritaSessio(){
      
        // Avataan db yhteys, jos epäonnistuu lopetetaan
        if(!db.avaaYhteys()){
            return;
        }

        // Onnistuuko käyttäjän kirjautuminen
        boolean kirjautunut = false; 
      
        // Kysytään käyttäjältä tunnuksia kunnes kirjautuminen onnistuu
        while(!kirjautunut){
            kirjautunut = kirjaus();
        }
      
        // Kirjautuminen onnistui
        if(kirjautunut){

            while(true){
                // Tulostetaan tehtavalistat
                tulostaTListat();

                System.out.println("Valitse tehtavalista jonka haluat suorittaa kirjoittamalla sen numero.");
                System.out.println("\nLopettaaksesi kirjoita 0\n");
                int syote = In.readInt();
                
                // Lopetetaan
                if(syote == 0){
                    break;
                }
                // Suoritetaan tehtävälista
                else{
                    boolean numeroOK = false;
                    while(!numeroOK){

                        // Jos tehtävälista löytyy, suoritetaan se
                        if(db.onkoTehtavalistaOlemassa(syote)){

                            numeroOK = true;

                            // Aloittaa session, eli luo sessiotauluun uuden entryn
                            // SessioID:hen tallennetaan uuden session ID
                            int sessioID = db.aloitaSessio(kayttajatunnus, syote);

                            suoritaTLista(syote, sessioID);

                            // Lopettaa session, eli päivittää äskettäin luotuun sessio-entryyn lopetusajan
                            // Tästä puuttuu vielä yritysten lkm, joka täytyy tallentaa suoritaTListassa.
                            db.lopetaSessio(sessioID);
                           
                        }
                        else{
                            System.out.println("Virheellinen valinta. Yritä uudelleen.");
                        }
                   }
                }
            }
        }
        db.suljeYhteys();
    }
   
    // Tulostaa tarjolla olevat tehtävälistat
    public void tulostaTListat(){

        try{
      
            ResultSet rs = db.lahetaKysely("SELECT id, kuvaus FROM tehtavalista;");

            while(rs.next()){
                System.out.print("Tehtavasarja " + rs.getString("id") + ": ");
                System.out.println(rs.getString("kuvaus"));
            }

        }catch(Exception e){
            System.out.println("Tehtävälistan tulostuksessa tapahtui virhe.");
            e.printStackTrace();
            return;
        }
    }
   
    // Tehtävälistan suorittaminen
    public boolean suoritaTLista(int tlNro, int sessioID){
        
        ResultSet esimkanta = null; 
        String vastausKysely = null;
        String kysely = null; // Kayttajan antama kysely
        boolean suoritettu = false; // Onko tehtävä suoritetttu
        int i = 0; // Monesko tehtävä
        int vaarin = 0; // Väärien vastauksien määrä
        int tlPituus = db.haeTLkm(tlNro); // Tehtavalistan pituus
        java.sql.Time alkuaika = null;
        java.sql.Time loppuaika = null;
        boolean olikoOikein = false;
        // Tämä muuttuu falseksi jos jokin tehtävä menee kolmesti väärin
        boolean listaSuoritettu = true; 
        ResultSet vastaus = null;   // Oikea vastaus
        ResultSet tulos = null; // Kayttajan kyselyn tulos
        ResultSet tehtava = null; // Käsiteltävä tehtävä
        
        try {
            // Haetaan suoritettava tehtavasarja
            tehtava = db.haeTehtavasarja(tlNro);

            // Käydään listan tehtävät läpi
            while(tehtava.next()){
                olikoOikein = false;
                // Tulostetaan tietokannan rakenne.
                esimkanta = db.haeEsimKanta();
                System.out.println("Tietokannan rakenne:");
                db.tulostaRs(esimkanta);
                System.out.println("");
                // Tulostetaan tehtävän kuvaus:  
                System.out.println(tehtava.getString(2));

                // Kirjataan tehtävän aloituksen aika
                alkuaika = db.haeAika();

                i++;

                // Kolme yritystä ratkaista tehtävä
                suoritettu = false;
                while (!suoritettu){

                    // Pyydetään käyttäjältä vastaus
                    kysely = In.readString();
                    // Kysely
                    if(db.haeKysTyyppi(i, tlNro).equals("Kysely")){
                        tulos = db.lahetaKysely(kysely);
                        
                        // Haetaan oikea vastaus kyselyyn
                        vastaus = db.lahetaKysely("SELECT tehtava.esim_vastaus FROM tehtava INNER JOIN kuuluu ON tehtava.id = kuuluu.tehtava_id INNER JOIN tehtavalista ON kuuluu.tehtavalista_id" +
                            " = tehtavalista.id WHERE kuuluu.tehtavanro = " + i + " AND tehtavalista.id = " + tlNro + ";");
                        vastaus.next();
                        vastausKysely = vastaus.getString(1);
                        vastaus = db.lahetaKysely(vastausKysely);

                        // Verrataan käyttäjän kyselyn tulosta vastaukseen
                        olikoOikein = db.vertaaTulokset(tulos, vastaus);
                    }
                    // Insert tai delete
                    else{

                        vastaus = db.lahetaKysely("SELECT tehtava.esim_vastaus FROM tehtava INNER JOIN kuuluu ON tehtava.id = kuuluu.tehtava_id INNER JOIN tehtavalista ON kuuluu.tehtavalista_id" +
                            " = tehtavalista.id WHERE kuuluu.tehtavanro = " + i + " AND tehtavalista.id = " + tlNro + ";");
                        vastaus.next();
                        vastausKysely = vastaus.getString(1);
                        // Verrataan käyttäjän kyselyn tulosta vastaukseen
                        if(kysely.equals(vastausKysely)){
                            olikoOikein = true;
                            db.lahetaKasky(kysely);
                        }
                        else{
                            olikoOikein = false;
                        }
                    }

                    // Väärällä vastauksella toistetaan ja lisätään väärälaskuria...
                    if(!olikoOikein){
                        System.out.println("Vastauksesi oli väärin.");
                        vaarin++;
                        // Tulostetaan kanta näkyviin
                        System.out.println("\nTietokannan rakenne:");
                        esimkanta = db.haeEsimKanta();
                        db.tulostaRs(esimkanta);
                        System.out.println("");

                        // Vääriä vastauksia on kolme, siirrytään seuraavaan
                        if(vaarin == 3){
                            listaSuoritettu = false;
                            vaarin = 0;
                            olikoOikein = false;
                            System.out.println("Vastasit väärin kolmesti. Siirrytään seuraavaan tehtävään.\n");
                            System.out.println("Oikea vastaus olisi ollut: " + vastausKysely);
                            System.out.println();
                            suoritettu = true;
                        }
                    //... ja oikealla vastauksella siirrytään seuraavaan ja nollataan väärät
                    }else{
                        System.out.println("Oikea vastaus. Siirrytään seuraavaan tehtävään.\n");
                        vaarin = 0;
                        olikoOikein = true;
                        suoritettu = true;
                    }
                }
                // Merkitään suorituksen tiedot kantaan
                loppuaika = db.haeAika();
                lisaaTiedotKantaan(tlNro, i, sessioID, vaarin + 1, olikoOikein, alkuaika, loppuaika);
            }
            // Suljetaan resultSetit.
            if(vastaus != null){
                vastaus.close();
            }
            if(tulos != null){
                tulos.close();
            }
            if(tehtava != null){
                tehtava.close();
            }
        }
        catch (SQLException e) {
            System.out.println("Tehtävän hakemisessa tapahtui virhe.");
            e.printStackTrace();
        }

        return listaSuoritettu;
    }

    // Lisää tiedon tehtävän suorittamisesta tietokantaan
    public void lisaaTiedotKantaan(int tlNro, int tehtavaNro, int sessioID, int yritykset, boolean oikein, java.sql.Time alkuaika, java.sql.Time loppuaika){

        int tehtavaID = 0;
        // Haetaan tehtävän ID
        ResultSet tID = db.lahetaKysely("SELECT kuuluu.tehtava_id FROM tehtava INNER JOIN kuuluu ON tehtava.id = kuuluu.tehtava_id INNER JOIN tehtavalista ON kuuluu.tehtavalista_id" +
            " = tehtavalista.id WHERE kuuluu.tehtavanro = " + tehtavaNro + " AND tehtavalista.id = " + tlNro + ";");

        // Noudetaan se resultsetistä
        try{
            tID.next();
            tehtavaID = tID.getInt(1);
        }
        catch (SQLException e) {
            System.out.println("Tehtävän hakemisessa tapahtui virhe.");
            e.printStackTrace();
        }
        // Merkitään tiedot yrityksestä sessioon
	// tehtava_id, sessio_id, yritys_nro, oliko_oikein, alku, loppu
        db.lahetaKasky("INSERT INTO tehdaan VALUES (" + tehtavaID + ", " + sessioID + ", " + yritykset + ", " + oikein + ", '" + alkuaika + "', '" + loppuaika + "');");

    }
   
    // Kirjaa käyttäjän sisään
    public boolean kirjaus(){
    
        try{
            System.out.println("Käyttäjätunnus:");
            kayttajatunnus = In.readInt();
            
            // Jos arvoa ei löytynyt
            if(db.onkoKayttajaOlemassa(kayttajatunnus)){ 
                System.out.println("\nSisäänkirjautuminen onnistui!");
                return true;
            }else{
                System.out.println("\nKäyttäjätunnusta ei löytynyt. Yritä uudelleen.");
                return false;
            }
        }catch(Exception e){
            System.out.println("\nKäyttäjätunnuksen tarkistuksessa tapahtui virhe.");
            e.printStackTrace();
            return false;
        }
    }
}
