/*
* TIKO-harkkatyön tietokannan luontilauseet
* author: Jenni Mansikka-Aho, Ossi Puustinen ja Cihan Bebek
*/

CREATE TABLE kayttaja (
	id INT, /*id = tunnus, tässä ja muissa tauluissa*/

	nimi varchar(40) NOT NULL,
	op_nro INT, /*opiskelijanumero*/
	paa_aine varchar(40),
	oikeudet INT, /*tän täytyy olla 1, 2, 3, eli opiskelija, opettaja ja ylläpitäjä*/

	PRIMARY KEY (id)
);

CREATE TABLE tehtava (
	id INT,

	luoja_id INT references kayttaja(id), /*käyttäjätunnus*/

	kys_tyyppi VARCHAR(30),
	kuvaus VARCHAR(200),
	esim_vastaus VARCHAR(200),
	luonti_pvm DATE,

	PRIMARY KEY (id)
);

CREATE TABLE tehtavalista (
	id INT,

	kuvaus VARCHAR(200),
	teht_lkm INT,
	luontipvm DATE,

	PRIMARY KEY (id)
);

CREATE TABLE kuuluu (
	tehtavanro INT,
	tehtava_id INT references tehtava(id),
	tehtavalista_id INT references tehtavalista(id)
);


CREATE TABLE sessio (
	id INT,

	kayt_id INT references kayttaja(id), /*käyttäjätunnus*/
	suoritettu_teht_lista INT references tehtavalista(id), /*tehtävälista*/

	sessio_alku TIMESTAMP,
	sessio_loppu TIMESTAMP,
	yritysnro INT,
	yritys_vastaus VARCHAR(200),

	PRIMARY KEY (id)
);

CREATE TABLE tehdaan (
	tehtava_id INT references tehtava(id),
	sessio_id INT references sessio(id),

	yritysnro INT,
	oliko_oikein BOOLEAN,
	aloitus_aika TIMESTAMP,
	lopetus_aika TIMESTAMPT

);

