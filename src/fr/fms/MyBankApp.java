/**
 * Version 1.0 

 * d'une appli bancaire simplifiée offrant la possibilitée de créer des clients, des comptes bancaires associés et des opérations ou
 * transactions bancaires sur ceux-ci telles que : versement, retrait ou virement 
 * + permet d'afficher l'historique des transactions sur un compte
 * 
 * **************************************************
 * Version 1.2 
 * Dans cette version, nous souhaitons dorénavant établir une interaction avec l’utilisateur de sorte.
 * qu’il pourra saisir un numéro de compte bancaire, s’il existe, il aura accès à la liste des opérations 
 * sur ce compte, sinon un message remonté via le mécanisme des exceptions indiquera que le compte 
 * n’existe pas. De même, la gestion des cas particuliers se fera dorénavant via les exceptions 
 * capturées et gérées permettant la continuité de l’appli tout en informant sur les problèmes 
 * rencontrés : « retrait impossible, solde insuffisant ».
 * 
 * @author El babili - 2022
 * 
 */

package fr.fms;

import java.util.Date;
import java.util.Scanner;

import fr.fms.business.IBankBusinessImpl;
import fr.fms.entities.Current;
import fr.fms.entities.Customer;
import fr.fms.entities.Saving;

public class MyBankApp {

	protected static Scanner scan = new Scanner(System.in); 

	public static void main(String[] args) {
		//représente l'activité de notre banque
		IBankBusinessImpl bankJob = new IBankBusinessImpl();

		//System.out.println("création de 2 comptes bancaires");

		Customer robert = new Customer(1, "dupont", "robert", "robert.dupont@xmail.com");
		Customer julie = new Customer(2, "jolie", "julie", "julie.jolie@xmail.com");		
		Current firstAccount = new Current(100200300, new Date(), 1500, 200 , robert);
		Saving secondAccount = new Saving(200300400, new Date(), 2000, 5.5, julie);

		//notre banquier ajoute les comptes"
		bankJob.addAccount(firstAccount); 
		bankJob.addAccount(secondAccount);
		
		System.out.println("Bonjour, bienvenue dans votre application MyGoldBank.");

		int rep=0;
		long accountId;

		while(rep != 2) {

			System.out.println("1 : Pour gérer votre compte.");
			System.out.println("2 : Quitter MyGoldBank.");

			while(!scan.hasNextInt()) {
				System.out.println("La valeur rentrée n'était pas du type voulue, saisir une nouvelle entrée.");
				scan.next();
			}
			rep = scan.nextInt();

			switch(rep) {
			case 1 : // Modification d'une quantit�
				System.out.println("Tapez votre numéro de compte pour accéder au menu.");

				while(!scan.hasNextLong()) {
					System.out.println("La valeur rentrée est incorrecte, saisir une nouvelle entrée.");
					scan.next();
				}
				accountId =scan.nextLong();
				try {
					bankJob.verifyAccount(accountId,scan);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case  2: // Exit application
				System.out.println("A bientôt.");
				break;

			default : System.out.println("mauvaise saisie, votre choix : "+rep+" est inexistant dans le menu");
			}	
		}
		scan.close();
	}

}
