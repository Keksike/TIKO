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

			boolean kayttajatunnusOK = false;
			int kayttajatunnus;

		while(!kayttajatunnusOK){
			System.out.println("Käyttäjätunnus:");
			kayttajatunnus = In.readInt();

			if(TietokantaToiminnot.onkoKayttajaOlemassa(kayttajatunnus)){ //jos arvoa ei löytynyt

				System.out.println("Käyttäjätunnusta ei löytynyt. Yritä uudelleen.");

			}else{

				kayttajatunnusOK = true;
				System.out.println("Sisäänkirjautuminen onnistui!");

				//opiskelija (1), opettaja(2) vai ylläpitäjä(3)
				int oikeudet = TietokantaToiminnot.haeOikeudet(kayttajatunnus);

				boolean toimintoValintaOK = false;

				if(oikeudet == 1){ //opiskelija
					while(!toimintoValintaOK){

						System.out.println("Olet opiskelija. Mikäli haluat nähdä tehtävälistan, kirjoita 1. Mikäli haluat suorittaa tehtävälistan, kirjoita 2.");
						System.out.println("Valinta:");

						int valinta = In.readInt();

						if(valinta == 1){
							toimintoValintaOK = true;

							boolean numeroOK = false;
							while(!numeroOK){

								System.out.println("Tehtävälistan numero:");
								int tehtavaListaNro = In.readInt();

								if(TietokantaToiminnot.onkoTehtavalistaOlemassa(tehtavaListaNro)){

									numeroOK = true;
									ResultSet tehtavalista = TietokantaToiminnot.haeTehtLista(tehtavaListaNro);
									//tähän tulostukset ja kyselyt
								}
							}
						}else if(valinta == 2){

							toimintoValintaOK = true;

							/*TÄHÄN TOIMINNALLISUUTTA Opiskelija.java:sta*/
								
						}else{

							System.out.println("Virheellinen valinta! Yritä uudelleen.");
							
						}
					}
				}else if(oikeudet == 2){ //opettaja

					System.out.println("Olet opettaja. ");

				}else if(oikeudet == 3){ //ylläpitäjä

					System.out.println("Olet ylläpitäjä. ");

				}else{

					System.out.println("Oikeuksia ei löytynyt. Ohjelma sammuu.");

				}
			}
		}
	}
}
