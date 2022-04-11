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
import java.util.regex.Pattern;

import fr.fms.business.IBankBusinessImpl;

import fr.fms.entities.Current;
import fr.fms.entities.Customer;
import fr.fms.entities.Saving;
import fr.fms.entities.Transaction;

public class MyBankApp {

	private static Customer robert;
	private static Customer julie;		
	private static Current firstAccount;
	private static Saving secondAccount;	
	private static IBankBusinessImpl bankJob;	

	private static Pattern regExp = Pattern.compile("[0-9]+");


/**
 * méthode main
 * @param args
 */
	public static void main(String[] args) {
		//création de la bank avec 2 clients et 2 comptes
		initBank();
		// message de bienvenue
		welcome();

		Scanner scan = new Scanner(System.in); 

		while (true) {
			System.out.println("Tapez votre numéro de compte pour accéder au menu.");
			long accountId;
			
			// Verfie si le compte existe.
			accountId =scanLong(scan);

			mainFunction(accountId,scan);


		}

	}
/**
 * méthode qui scanne la saisie du numéro de compte et le retourne si existant
 * @param scan
 * @return
 */
	private static long scanLong(Scanner scan) {

		long accountId=0;

		while (scan.hasNext()) {
			if (scan.hasNext(regExp)) {
				accountId=scan.nextLong();
				try {
					if(	bankJob.consultAccount(accountId)!=null);
					break;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			} else {
				System.out.println("Un numéro de compte bancaire n'est constitué que de chiffres");
				scan.next();
			}

		}

		return accountId;
	}
/**
 * méthode qui initialise la bank
 */
	private static void initBank() {
		//représente l'activité de notre banque
		bankJob = new IBankBusinessImpl();

		//System.out.println("création de 2 comptes bancaires");

		robert = new Customer(1, "dupont", "robert", "robert.dupont@xmail.com");
		julie = new Customer(2, "jolie", "julie", "julie.jolie@xmail.com");		
		firstAccount = new Current(100200300, new Date(), 1500, 200 , robert);
		secondAccount = new Saving(200300400, new Date(), 2000, 5.5, julie);

		//notre banquier ajoute les comptes"
		bankJob.addAccount(firstAccount); 
		bankJob.addAccount(secondAccount);

		System.out.println("Liste des comptes dans ma banque");		
		bankJob.listAccounts().stream().forEach(c -> System.out.println(c));

	}
	/**
	 * affiche message bienvenue
	 */
	private static void welcome() {
		System.out.println();
		System.out.println("************************************");
		System.out.println("BIENVENU DANS VOTRE APPLI MYGOLDBANK");
		System.out.println("************************************");		
		System.out.println();
	}



	/**
	 * affichage du menu
	 */
	public static  void showMenu() {

		System.out.print("1.Versement - ");
		System.out.print("2.Retrait - ");
		System.out.print("3.Virement - ");
		System.out.print("4.Information du compte - ");
		System.out.print("5.Liste des opérations - ");
		System.out.print("6.Sortir \n");
	}
	
/**
 * méthode du menu gestion de compte client connecté
 * @param accountId
 * @param scan
 */
	public static void mainFunction(long accountId, Scanner scan) {

		int action=0;
		long amount;
		long accIdDest;

		String name= bankJob.consultAccount(accountId).getCustomer().getFirstName();
		System.out.println("Bienvenue "+name.toUpperCase()+" que souhaitez-vous faire ?");

		while(action != 6) {
			try {

				// Affichage du menu
				showMenu();

				action = scan.nextInt();

				switch(action) {
				case 1 : // versement sur le compte
					System.out.println("Tapez le montant à ajouter.");

					while(!scan.hasNextLong()) {
						System.out.println("La valeur rentrée est incorrecte, saisir une nouvelle entrée.");
						scan.next();
					}
					amount =scan.nextLong();
					bankJob.pay(accountId, amount);

					break;

				case 2 : // retrait
					System.out.println("Tapez le montant à retiré.");

					while(!scan.hasNextLong()) {
						System.out.println("La valeur rentrée est incorrecte, saisir une nouvelle entrée.");
						scan.next();
					}
					amount =scan.nextLong();
					bankJob.withdraw(accountId, amount);

					break;

				case 3 : 	// virement
					System.out.println("Tapez le montant à viré, ainsi que le numéro de compte destinaire.");

					while(!scan.hasNextLong()) {
						System.out.println("La valeur rentrée est incorrecte, saisir une nouvelle entrée.");
						scan.next();
					}
					amount =scan.nextLong();
					accIdDest=scan.nextLong();
					bankJob.transfert( accountId, accIdDest, amount);

					break;

				case 4 : 	// informations du compte
					System.out.println("Informations du compte : ");
					System.out.println("-------------------------------------------------------");
					System.out.println(bankJob.consultAccount(accountId));
					System.out.println("-------------------------------------------------------");

					break;

				case 5 : 	// liste des opérations
					System.out.println("Opérations du compte : ");
					System.out.println("-------------------------------------------------------");
					for(Transaction trans : bankJob.listTransactions(accountId))
						System.out.println(trans);

					System.out.println("-------------------------------------------------------");

					break;

				case 6 : // Exit account
					System.out.println("Exit account.");
					break;

				default : System.out.println("Mauvaise saisie, votre choix : "+action+" est inexistant dans le menu");
				}	


			} catch (Exception e) {
				System.out.println(e.getMessage());
			}


		}

	}

}
