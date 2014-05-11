import java.sql.*;

public class Sessio {
   
	private TietokantaToiminnot db;
   
	public void suoritaSessio(){
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
		  
				/*
				 * tulostaListat();
				 */
			 
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
						if(db.onkoTehtavaOlemassa(tehtavaListaNumero)){
							numeroOK = true;
							suoritaTLista(tehtavaListaNumero);
						}
					}
				}
			}
		}
		db.suljeYhteys();
	}
	
	//Tulostaa tehtavalistan
	public void tulostaTLista(ResultSet rs){

	}
   
	//Tulostaa tarjolla olevat tehtavalistat
	public void tulostaListat(){

	}


	//Tehtavalistan suorittaminen
	public void suoritaTLista(int tlNro){
      
		ResultSet tehtavalista = null;
		int tlPituus = db.haeTLkm(tlNro);
		//Käydään listan tehtävät läpi
		for (int i = 0; i < tlPituus; i++){
         
			tehtavalista = db.haeTehtava(i, tlNro);
			/*tähän tulostukset ja jatkokyselyt*/
 
			int j = 0;
			// Tulostetaan tehtävän kuvaus:
			try {
				while (tehtavalista.next()) {
					j++;
					System.out.println (tehtavalista.getString(j));
				}
			}
			catch (SQLException e) {
				System.out.println("Tapahtui virhe.");
			}

		}
	}

	//Kirjaa käyttäjän sisään
	public boolean kirjaus(){

		int kayttajatunnus = 0;
      
		System.out.println("Käyttäjätunnus:");
		kayttajatunnus = In.readInt();

		if(!db.onkoKayttajaOlemassa(kayttajatunnus)){ //jos arvoa ei löytynyt
			System.out.println("Käyttäjätunnusta ei löytynyt. Yritä uudelleen.");
			return false;

		}
		else {
			System.out.println("Sisäänkirjautuminen onnistui!");
			return true;
		}
	}
}
