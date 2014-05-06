/*
* TIKO-kurssin harjoitustyön pääluokka
*
* author: Jenni Mansikka-Aho, Ossi Puustinen & Cihan Bebek
*/


import java.sql.*;
import java.util.Scanner;

public class TIKO {
	public static void main(String args[]){

			boolean kayttajatunnusOK = false;
			int kayttajatunnus;

			TietokantaToiminnot db = new TietokantaToiminnot();

			db.avaaYhteys();

		while(!kayttajatunnusOK){
			System.out.println("Käyttäjätunnus:");
			kayttajatunnus = In.readInt();

			if(!db.onkoKayttajaOlemassa(kayttajatunnus)){ //jos arvoa ei löytynyt

				System.out.println("Käyttäjätunnusta ei löytynyt. Yritä uudelleen.");

			}else{

				kayttajatunnusOK = true;
				System.out.println("Sisäänkirjautuminen onnistui!");

				//opiskelija (1), opettaja(2) vai ylläpitäjä(3)
				int oikeudet = db.haeOikeudet(kayttajatunnus);

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

								if(db.onkoTehtavalistaOlemassa(tehtavaListaNro)){

									numeroOK = true;
									ResultSet tehtavalista = db.haeTehtLista(tehtavaListaNro);
									/*tähän tulostukset ja jatkokyselyt*/

								}
							}
						}else if(valinta == 2){

							toimintoValintaOK = true;

							boolean numeroOK = false;
							while(!numeroOK){

								System.out.println("Tehtävän numero:");
								int tehtavaNro = In.readInt();

								if(db.onkoTehtavaOlemassa(tehtavaNro)){

									numeroOK = true;
									ResultSet tehtavalista = db.haeTehtava(tehtavaNro);
									/*tähän tulostukset ja jatkokyselyt*/

								}
							}
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

				db.suljeYhteys();
			}
		}
	}
}
