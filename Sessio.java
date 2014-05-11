import java.sql.*;


public class Sessio {
   
    private final String VIRHE = "Tapahtui virhe.";
    private TietokantaToiminnot db;

    private int kayttajatunnus;
   
    public void suoritaSessio(){
        tulostaOtsikko();

        //Onko käyttäjä kirjautunut sisään
        boolean kirjautunut = false; 
      
        //Avataan db yhteys, jos epäonnistuu lopetetaan
        db = new TietokantaToiminnot();
        if(!db.avaaYhteys()){
            return;
        }
      
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
    public void suoritaTLista(int tlNro){
      
        ResultSet tehtava = null; //Käsiteltävä tehtävä
        ResultSet vastaus = null;   //Oikea vastaus
        ResultSet tulos = null; //Kayttajan kyselyn tulos
        String kysely = null; // Kayttajan antama kysely
        boolean oikein = false; //Oliko kysely oikein
        int j = 0; //Laskuri
        int vaarin = 0; //Vaarien vastauksien määrä
        int tlPituus = db.haeTLkm(tlNro); //Tehtavalistan pituus
      
        //Käydään listan tehtävät läpi
        for (int i = 1; i < tlPituus; i++){
            //Haetaan suoritettava tehtava
            tehtava = db.haeTehtava(tlNro, i);
            // Tulostetaan tehtävän kuvaus:
            /*
            try {
                while (tehtava.next()) {
                    j++;
                    System.out.println (tehtava.getString(j));
                }
            }
            catch (SQLException e) {
                System.out.println("Tehtävän hakemisessa tapahtui virhe.");
                e.printStackTrace();
            }
            */
            //Kolme yritystä ratkaista tehtävä
            boolean suoritettu = false;
            while (!suoritettu){
                //Pyydetään käyttäjältä vastaus ja lähetetään kysely
                kysely = In.readString();
                tulos = db.lahetaKysely(kysely);
                
                /*
                    vastaus = HAE VASTAUS
                */
                
                // Verrataan käyttäjän kyselyn tuloskai vastaukseen
                oikein = db.vertaaTulokset(tulos, vastaus);
                // Väärällä vastauksella toistetaan ja lisätään väärälaskuria...
                if(!oikein){
                
                    System.out.println("Vastauksesi oli väärä.");
                    vaarin++;
                    //Vääriä vastauksia on kolme, siirrytään seuraavaan
                    if(vaarin == 3){
                        vaarin = 0;
                        System.out.println("Vastasit väärin kolmesti. Siirrytään seuraavaan tehtävään.");
                        suoritettu = true;
                    }
                //... ja oikealla vastauksella siirrytään seuraavaan ja nollataan väärät
                }else{
                    System.out.println("Oikea vastaus. Siirrytään seuraavaan tehtävään.");
                    vaarin = 0;
                    suoritettu = true;
                }
            }
            /*
            - Vastaanotetaan vastaus
            - Tarkistetaan vastaus
            - Lahetataan vastaus
            - Verrataan tuloksia

            - Merkitään tiedot suorituksesta
            */
      
        }
      
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
        System.out.println("By: Ossi Puustinen, Jenni Mansikka-Aho & Cihan Bebek");
        System.out.println("---------------------------------");
    }
}
