import java.sql.*;
/*java -classpath /usr/share/java/postgresql.jar:. TIKO*/


public class Sessio {
   
    private final String VIRHE = "Tapahtui virhe.";
    private TietokantaToiminnot db;

    private int kayttajatunnus;
   
    public void suoritaSessio(){
        tulostaOtsikko();
      
        //Avataan db yhteys, jos epäonnistuu lopetetaan
        TietokantaToiminnot db = new TietokantaToiminnot();
        if(!db.avaaYhteys()){
            return;
        }

        //Onnistuuko käyttäjän kirjautuminen
        boolean kirjautunut = false; 
      
        //Kysytään käyttäjältä tunnuksia kunnes kirjautuminen onnistuu
        while(!kirjautunut){
            kirjautunut = kirjaus();
        }
      
        //Kirjautuminen onnistui
        if(kirjautunut){

            while(true){
                //Tulostetaan tehtavalistat
                tulostaListat();

                //Pyydetään valinta käyttäjältä ja toimitaan sen mukaan
                System.out.println("Mikäli haluat nähdä tehtävälistan, kirjoita 1. Mikäli haluat suorittaa tehtävälistan, kirjoita 2. Lopettaaksesi kirjoita 0");
                int valinta = In.readInt();
                
                //Haetaan tehtavalista
                if(valinta == 1){
                    boolean numeroOK = false;
                    int tehtavaListaNumero;
                    while(!numeroOK){
                      
                        System.out.println("Anna tehtävälistan numero:");
                        tehtavaListaNumero = In.readInt();
                      
                    if(db.onkoTehtavalistaOlemassa(tehtavaListaNumero)){

                        numeroOK = true;
                        ResultSet tehtavalista = db.haeTehtLista(tehtavaListaNumero);
                        /*tähän tulostukset*/
                        tulostaTLista(tehtavalista);
                      
                        }
                    }
                }
                //Suoritetaan tehtavalista
                else if(valinta == 2){
                
                    boolean numeroOK = false;
                    while(!numeroOK){

                        System.out.println("Anna tehtävälistan numero:");
                        int tehtavaListaNumero = In.readInt();
                        //Jos tehtavalista löytyy, suoritetaan se
                        if(db.onkoTehtavalistaOlemassa(tehtavaListaNumero)){

                            numeroOK = true;

                            //aloittaa session, eli luo sessiotauluun uuden entryn
                            //sessioID:hen tallennetaan uuden session ID
                            int sessioID = db.aloitaSessio(kayttajatunnus, tehtavaListaNumero);

                            suoritaTLista(tehtavaListaNumero);

                            //lopettaa session, eli päivittää äskettäin luotuun sessio-entryyn lopetusajan
                            //tästä puuttuu vielä yritysten lkm, joka täytyy tallentaa suoritaTListassa.
                            db.lopetaSessio(sessioID);
                           
                        }
                        else{
                            System.out.println("Virheellinen valinta. Yritä uudelleen.");
                        }
                   }
                }
                //Lopetetaan
                else if(valinta == 0){
                    break;
                }
            }
        }
        db.suljeYhteys();
    }
   
    //Tulostaa tehtavalistan
    public void tulostaTLista(ResultSet rs){
      
        int j = 0;
        /*
        try {
            while (rs.next()) {
                j++;
                System.out.println (rs.getString(j));
            }
        }
        catch (SQLException e) {
            System.out.println(VIRHE);
        }
        */
    }
   
    //Tulostaa tarjolla olevat tehtavalistat
    public void tulostaListat(){
      
        ResultSet rs = db.lahetaKysely("SELECT id, kuvaus FROM tehtavalista;");
        int j = 0;
        /*
        try {
            while (rs.next()) {
                j++;
                System.out.println (rs.getString(j));
            }
        }
        catch (SQLException e) {
            System.out.println(VIRHE);
        }
        */
   }
   
    //Tehtavalistan suorittaminen
    public void suoritaTLista(int tlNro, int sessioID){
      
        ResultSet tehtava = null; //Käsiteltävä tehtävä
        ResultSet vastaus = null;   //Oikea vastaus
        ResultSet tulos = null; //Kayttajan kyselyn tulos
        String vastausKysely = null;
        String kysely = null; // Kayttajan antama kysely
        boolean oikein = false; //Oliko kysely oikein
        boolean suoritettu = false; //Onko tehtävä suoritetttu
        int j = 0; //Laskuri
        int vaarin = 0; //Vaarien vastauksien määrä
        int tlPituus = db.haeTLkm(tlNro); //Tehtavalistan pituus
        java.sql.Time alkuaika = null;
        java.sql.Time loppuaika = null;
        int olikoOikein = 0;
      
        //Käydään listan tehtävät läpi
        for (int i = 1; i < tlPituus; i++){
            //Haetaan suoritettava tehtava
            tehtava = db.haeTehtava(tlNro, i);
            // Tulostetaan tehtävän kuvaus:
            try {
                tehtava.next();
                System.out.println (tehtava.getString(2));
            }
            catch (SQLException e) {
                System.out.println("Tehtävän hakemisessa tapahtui virhe.");
                e.printStackTrace();
            }

            //Kirjataan tehtävän aloituksen aika
            alkuaika = db.haeAika();

            //Kolme yritystä ratkaista tehtävä
            suoritettu = false;
            while (!suoritettu){
                //Pyydetään käyttäjältä vastaus ja lähetetään kysely
                kysely = In.readString();
                tulos = db.lahetaKysely(kysely);
                
                //Haetaan oikea vastaus kyselyyn
                try{
                    vastaus = db.lahetaKysely("SELECT esim_vastaus FROM tehtava WHERE id = " + i + ";");
                    vastaus.next();
                    vastausKysely = vastaus.getString(1);
                    vastaus = db.lahetaKysely(vastausKysely);
                }
                catch (SQLException e) {
                    System.out.println("VIRHE");
                    e.printStackTrace();
                }

                // Verrataan käyttäjän kyselyn tulosta vastaukseen
                oikein = db.vertaaTulokset(tulos, vastaus);
                // Väärällä vastauksella toistetaan ja lisätään väärälaskuria...
                if(!oikein){
                
                    System.out.println("Vastauksesi oli väärä.");
                    vaarin++;
                    //Vääriä vastauksia on kolme, siirrytään seuraavaan
                    if(vaarin == 3){
                        vaarin = 0;
                        olikoOikein = 0;
                        System.out.println("Vastasit väärin kolmesti. Siirrytään seuraavaan tehtävään.");
                        suoritettu = true;
                    }
                //... ja oikealla vastauksella siirrytään seuraavaan ja nollataan väärät
                }else{
                    System.out.println("Oikea vastaus. Siirrytään seuraavaan tehtävään.");
                    vaarin = 0;
                    olikoOikein = 1;
                    suoritettu = true;
                }
            }
            loppuaika = db.haeAika();
            lisaaTiedotKantaan(tlNro, i, sessioID, vaarin + 1, olikoOikein, alkuaika, loppuaika);
            
        }
      
    }

    //lisää tiedon tehtävän suorittamisesta tietokantaan
    public void lisaaTiedotKantaan(int tlNro, int tehtavaNro, int sessioID, int yritykset, int oikein, java.sql.Time alkuaika, java.sql.Time loppuaika){

        int tehtavaID = 0;
        //Haetaan tehtavan ID
        ResultSet tID = db.lahetaKysely("SELECT kuuluu.tehtava_id FROM tehtava INNER JOIN on kuuluu ON tehtava.id = kuuluu.tehtava_id INNER JOIN tehtavalista ON kuuluu.tehtavalista_id" +
            " = tehtavalista.id WHERE kuuluu.nro = " + tehtavaNro + " AND tehtavalista.id = " + tlNro + ";");

        //Noudetaan se resultsetistä
        try{
            tID.next();
            tehtavaID = tID.getInt(1); 
        }
        catch (SQLException e) {
            System.out.println("Tehtävän hakemisessa tapahtui virhe.");
            e.printStackTrace();
        }
        //Merkitään tiedot yrityksestä sessioon
        db.lahetaKysely("INSERT INTO tehdaan (tehtava_id, sessio_id, yritys_nro, oliko_oikein, alku, loppu) VALUES (" + tehtavaID + ", " + sessioID + ", " + yritykset + ", " + oikein + ", " + alkuaika + ", " + loppuaika + ");");

    }
   
    //Kirjaa käyttäjän sisään
    public boolean kirjaus(){
      
        System.out.println("Käyttäjätunnus:");
        kayttajatunnus = In.readInt();

        if(!db.onkoKayttajaOlemassa(kayttajatunnus)){ //jos arvoa ei löytynyt
            System.out.println("Käyttäjätunnusta ei löytynyt. Yritä uudelleen.");
            return false;
        }else{
            System.out.println("Sisäänkirjautuminen onnistui!");
            return true;
        }
    }

    /*oli tylsää, ajatus ei kulkenut*/
    public void tulostaOtsikko(){
        System.out.println("---------------------------------");
        System.out.println("######## #### ##    ##  #######  ");
        System.out.println("   ##     ##  ##   ##  ##     ## ");
        System.out.println("   ##     ##  ##  ##   ##     ## ");
        System.out.println("   ##     ##  #####    ##     ## ");
        System.out.println("   ##     ##  ##  ##   ##     ## ");
        System.out.println("   ##     ##  ##   ##  ##     ## ");
        System.out.println("   ##    #### ##    ##  #######  ");
        System.out.println("---------------------------------");
        System.out.println("By: Ossi Puustinen, Jenni Mansikka-aho & Cihan Bebek");
        System.out.println("---------------------------------");
    }
}
