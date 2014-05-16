/*
* TIKO-kurssin harjoitustyön pääluokka
*
* author: Jenni Mansikka-aho, Ossi Puustinen & Cihan Bebek
*/


import java.sql.*;
import java.util.Scanner;

public class TIKO {
    public static void main(String args[]){
    	tulostaOtsikko();
        Sessio sessio = new Sessio();
        sessio.suoritaSessio();
    }

    /*oli tylsää, ajatus ei kulkenut*/
    public static void tulostaOtsikko(){
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
