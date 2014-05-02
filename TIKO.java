import java.sql.*;
import java.util.Scanner;

public class TIKO {
/*	private static final String AJURI = "org.postgresql.Driver";
	private static final String PROTOKOLLA = "jdbc:postgresql:";
	private static final String PALVELIN = "dbstud.sis.uta.fi";
	private static final int PORTTI = 5432;
	private static final String TIETOKANTA = "";	// tähän oma käyttäjätunnus
	private static final String KAYTTAJA = "";	// tähän oma käyttäjätunnus
	private static final String SALASANA = "";	// tähän tietokannan salasana*/

	public static void main(String args[]){
/*		try {
			Class.forName(AJURI);
		} catch (ClassNotFoundException poikkeus) {
			System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
			return;
		}*/

		//Connection con = null;

		try{
			//con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);

			boolean kayttajatunnusOK = false;
			int kayttajatunnus;

			//käytetään tarkistamaan löytyykö annettu käyttäjätunnus (id) tietokannasta
			final String tunnusTarkistus = "SELECT count(*) FROM kayttaja WHERE id = ?;";

			//final PreparedStatement tunnusTarkistusPS = con.prepareStatement(tunnusTarkistus);

			//käytetään tarkistamaan käyttäjän oikeudet. opiskelija (1), opettaja(2) vai ylläpitäjä(3)
			final String oikeudetTarkistus = "SELECT oikeudet FROM kayttaja WHERE id = ?;";

			//final PreparedStatement oikeudetTarkistusPS = con.prepareStatement(oikeudetTarkistus);


			while(!kayttajatunnusOK){
				System.out.println("Käyttäjätunnus:");
				kayttajatunnus = In.readInt();

				//asetetaan annettu käyttäjätunnus PS:ään
				//tunnusTarkistusPS.setInt(1, kayttajatunnus);

				//suoritetaan PS ja otetaan tulokset ylös
				ResultSet tunnusTarkitusRS = TietokantaToiminnot.lahetaKysely(tunnusTarkistus);
				tunnusTarkitusRS.next();
				
				int oikeudet = tunnusTarkitusRS.getInt(1);

				if(oikeudet == 0){
					System.out.println("Käyttäjätunnusta ei löytynyt. Yritä uudelleen.");

				}else{
					kayttajatunnusOK = true;
					System.out.println("Sisäänkirjautuminen onnistui!");

					//opiskelija (1), opettaja(2) vai ylläpitäjä(3)
					//oikeudetTarkistusPS.setInt(1, kayttajatunnus);
					ResultSet oikeudetTarkitusRS = TietokantaToiminnot.lahetaKysely(oikeudetTarkistus);
					oikeudetTarkitusRS.next();

					boolean valintaOK = false;

					if(oikeudet == 1){ //opiskelija
						while(!valintaOK){
							System.out.println("Olet opiskelija. Mikäli haluat nähdä tehtävälistan, kirjoita 1. Mikäli haluat suorittaa tehtävälistan, kirjoita 2.");
							System.out.println("Valinta:");

							int valinta = In.readInt();

							if(valinta == 1){
								valintaOK = true;



								/*TÄHÄN TOIMINNALLISUUTTA Opiskelija.java:sta*/

							}else if(valinta == 2){
								valintaOK = true;

								/*TÄHÄN TOIMINNALLISUUTTA Opiskelija.java:sta*/
								
							}else{
								System.out.println("Virheellinen valinta! Yritä uudelleen.");
							}
						}

					}else if(oikeudet == 2){ //opettaja
						System.out.println("Olet opettaja. ");

					}else if(oikeudet == 3){ //ylläpitäjä
						System.out.println("Olet ylläpitäjä. ");

					}
				}


			}

		}catch(SQLException poikkeus){

			System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");
			return;
		}
	}
}
