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

	private static final String AJURI = "org.postgresql.Driver";
	private static final String PROTOKOLLA = "jdbc:postgresql:";
	private static final String PALVELIN = "dbstud.sis.uta.fi";
	private static final int PORTTI = 5432;
	private static final String TIETOKANTA = "tiko2014db29";  // tähän oma käyttäjätunnus
	private static final String KAYTTAJA = "jm96400";  // tähän oma käyttäjätunnus
	private static final String SALASANA = "tiko";  // tähän tietokannan salasana

	private static Connection con;
	private static Statement stmt;

	//Yhteyden avaaminen
	public boolean avaaYhteys(){


		// Vaihe 1: tietokanta-ajurin lataaminen
		try {
			Class.forName(AJURI);
		} catch (ClassNotFoundException poikkeus) {
			System.out.println("Ajurin lataaminen ei onnistunut. Lopetetaan ohjelman suoritus.");
			return false;
		}

		// Vaihe 2: yhteyden ottaminen tietokantaan
		con = null;
		try {
			con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":" + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
		  
			stmt = con.createStatement();

		// Toiminta mahdollisessa virhetilanteessa
		} catch (SQLException poikkeus) {
			System.out.println("Tapahtui seuraava virhe: " + poikkeus.getMessage());
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

			} catch(SQLException poikkeus) {
				System.out.println("Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");

				return false; //null
			}

		}else{
			System.out.println("Yhteyttä tietokantaan ei ollut. Yhteyden sulkeminen tietokantaan ei onnistunut.");
			return false;
		}

	}


	//Haetaan tehtavalista
	public static ResultSet haeTehtLista(int listaNro){

		ResultSet rs = null; // Kyselyn tulokset

		try {
			
			String lause = "SELECT id, kuvaus FROM tehtavalista WHERE id = " + listaNro + ";";

			rs = lahetaKysely(lause);

		} catch (Exception poikkeus) {
			System.out.println("Jokin meni pieleen.");
			return null;
		}

		return rs; //Palautetaan tulosjoukko

	}

	//Haetaan tehtävä
	public static ResultSet haeTehtava(int tehtNro){

		ResultSet rs = null; // Kyselyn tulokset


		try {
			
			String lause = "SELECT id, kuvaus, esim_vastaus FROM tehtava WHERE id = " + tehtNro + ";";

			rs = lahetaKysely(lause);

		} catch (Exception poikkeus) {
			System.out.println("Jokin meni pieleen.");
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

		} catch (Exception poikkeus) {
			System.out.println("Jokin meni pieleen.");
			return null;
		}

		return rs; //Palautetaan tulosjoukko

	}

	//Laheta Kysely
	public static ResultSet lahetaKysely(String kysely){

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

	/*Parametrinä tehtävälistan id*/
	public static boolean onkoTehtavalistaOlemassa(int id){

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
			System.out.println("Tapahtui bugi.");
			return false;
		}
	}

	public static boolean onkoTehtavaOlemassa(int id){
		try{

			final String tehtavaTarkistus = "SELECT count(*) FROM tehtava WHERE id = " + id + ";";

			ResultSet rs = lahetaKysely(tehtavaTarkistus);

			if(rs.next()){

				if(rs.getInt(1) > 0){
					return true;
				}
			}
			return false;

		}catch(SQLException poikkeus){
			System.out.println("Tapahtui bugi.");
			return false;
		}
	}

	/*Parametrinä käyttäjätunnus, eli id*/
	public static boolean onkoKayttajaOlemassa(int id){

		try{

			final String tunnusTarkistus = "SELECT count(*) FROM kayttaja WHERE id = " + id + ";";

			ResultSet rs = lahetaKysely(tunnusTarkistus);

			if(rs.next()){

				if(rs.getInt(1) > 0){
					return true;
				}
			}
			return false;

		}catch(SQLException poikkeus){
			System.out.println("Tapahtui bugi.");
			return false;
		}
	}

	/*palauttaa 0 jos ei löydy*/
	public static int haeOikeudet(int id){

		try{

			String oikeudetTarkistus = "SELECT oikeudet FROM kayttaja WHERE id = " + id + ";";

			ResultSet rs = lahetaKysely(oikeudetTarkistus);

			if(rs.next()){

				return rs.getInt(1);
			}
			return 0;

		}catch(SQLException poikkeus){
			System.out.println("Tapahtui bugi.");
			return 0;
		}
	}
}
