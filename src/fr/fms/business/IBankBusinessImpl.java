package fr.fms.business;

import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import fr.fms.entities.Account;
import fr.fms.entities.Current;
import fr.fms.entities.Customer;
import fr.fms.entities.Transaction;
import fr.fms.entities.Transfert;
import fr.fms.entities.withdrawal;

/**
 * @author El babili - 2022
 * Implémentation de la couche métier de l'appli bancaire
 */
public class IBankBusinessImpl implements IBankBusiness {
	private HashMap<Long,Account>	accounts;
	private HashMap<Long,Customer>	customers;
	
	private long numTransactions;

	public IBankBusinessImpl() {
		accounts = new HashMap<Long,Account>();		
		customers = new HashMap<Long,Customer>();
		numTransactions = 1;
	}

	/** méthode qui ajoute un compte bancaire instancié à partir d'un client existant
	 * @param Account est un compte bancaire appartenant à un client
	 */
	@Override
	public void addAccount(Account account) {
		accounts.put(account.getAccountId(), account);		// ajouter un compte à ma liste, s'il existe déjà, ça ne marche pas	
		Customer customer = account.getCustomer(); 			// s'agissant du client de ce compte -> ToDo s'il n'existe pas dans le compte ajouté !
		customers.put(customer.getCustomerId(), customer);  // je veux le rajouter à ma liste de clients s'il n'existe pas

		//l'étape suivante n'est pas indispensable ici puisque nous ajoutons le client à notre collection de clients ci-dessus
		//en revanche, compte tenu du diagramme de classe, un client dispose d'une liste de comptes
		addAccountToCustomer(customer, account);			// j'ajoute au client son nouveau compte bancaire uniquement s'il ne l'a pas déjà
	}

	/**
	 * méthode qui vérifie si un compte existe
	 * @return Account si existe, null sinon
	 */
	@Override
	public Account consultAccount(long accountId){		
		Account account = accounts.get(accountId);

		return account;
	}

	/**
	 * méthode qui effectue le versement d'un montant sur un compte déjà verifié
	 * @param amount correspond au montant à verser
	 */
	@Override
	public void pay(Account account, double amount) {				// versement
		account.setBalance(account.getBalance() + amount);
		System.out.println("Le montant de "+amount+" a bien été verser.");
		System.out.println("Nouveau solde du compte numéro : "+account.getAccountId()+" - "+account.getBalance());
		Transaction trans = new Transfert(numTransactions++,new Date(),amount,account.getAccountId());
		account.getListTransactions().add(trans);				// création + ajout d'une opération de versement
	}

	/**
	 * méthode qui effectue le retrait d'un montant sur un compte vérifié tout en gérant le découvert autorisé qqsoit le compte
	 * @param account correspond au compte sur lequel effectuer le retrait
	 * @param amount correspond au montant à retirer 
	 */
	@Override
	public boolean withdraw(Account account, double amount)throws Exception {			//retrait

		double capacity = 0;
		if(account instanceof Current) {
			capacity = account.getBalance() + ((Current)account).getOverdraft();	//solde + decouvert autorisé				
		}
		else capacity = account.getBalance();
		if(amount <= capacity) {
			account.setBalance(account.getBalance() - amount);
			System.out.println("Le montant de "+amount+" a bien été retiré.");
			System.out.println("Nouveau solde du compte numéro : "+account.getAccountId()+" - "+account.getBalance());
			Transaction trans = new withdrawal(numTransactions++,new Date(),amount,account.getAccountId());
			account.getListTransactions().add(trans);		// création + ajout d'une opération de retrait
		}
		else {
			throw new Exception("vous avez dépassé vos capacités de retrait !");
		}

		return true;	//retrait effectué
	}

	/**
	 * méthode qui effectue un virement d'un compte src vers un compte dest, décomposé en 2 étapes : retrait puis versement
	 * @param account correspond au compte source
	 * @param accIdSrc correspond à l'id du compte destinataire
	 * @param amount correspond au montant à virer
	 */
	@Override
	public void transfert(Account account, long accIdDest, double amount)throws Exception {	//virement
		// verif compte destinataire
		Account acc=consultAccount(accIdDest);
		if(acc == null) {
			throw new Exception("Inexistant Destinary Account");
		} else {
			// verif si compte identique
			if(account.getAccountId() == accIdDest)	throw new Exception("vous ne pouvez retirer et verser sur le même compte !");
			else {
				try {
					if(withdraw(account, amount)) {		//retrait si c'est possible
						pay(account, amount);				//alors versement
					}
					//else System.out.println("virement impossible");
				} catch (Exception e) {
					System.out.println(e.getMessage());

				}
			}
		}

	}

	/**
	 * Renvoi la liste des transactions sur un compte
	 * @param accountId 
	 * @return ArrayList<Transaction>
	 */
	@Override
	public ArrayList<Transaction> listTransactions(long accountId) {
		return consultAccount(accountId).getListTransactions();
	}

	/**
	 * Renvoi la liste des comptes de notre banque
	 * @return ArrayList<Account>
	 */
	public ArrayList<Account> listAccounts() {		
		return new ArrayList<Account> (accounts.values());
	}

	/**
	 * Ajoute un compte à l'objet client
	 * @param customer
	 * @param account
	 */
	private void addAccountToCustomer(Customer customer, Account account) {
		boolean exist = false;
		for(Account acc : customer.getListAccounts()) {
			if(acc.getAccountId() == account.getAccountId()) {
				exist = true;
				break;
			}
		}
		if(exist == false)	customer.getListAccounts().add(account);
	}
	/**
	 * Verfie si le compte existe.
	 */
	public void verifyAccount(long accountId,Scanner scan) throws Exception {

		Account acc=consultAccount(accountId);
		if(acc == null) {
			throw new Exception("Inexistant Account");
		} else {

			mainFunction(scan,acc);
		}
	}

	/** M�thode qui affiche le menu  */
	@Override
	public  void showMenu() {

		System.out.print("1.Versement - ");
		System.out.print("2.Retrait - ");
		System.out.print("3.Virement - ");
		System.out.print("4.Information du compte - ");
		System.out.print("5.Liste des opérations - ");
		System.out.print("6.Sortir \n");
	}
	/** M�thode principale qui s'execute dans le main */
	@Override
	public void mainFunction(Scanner scan,Account account) {

		int ans=0;
		long amount;
		long accIdDest;

		String name= account.getCustomer().getFirstName();
		System.out.println("Bienvenue "+name.toUpperCase()+" que souhaitez-vous faire ?");

		while(ans != 6) {
			// Affichage du menu
			showMenu();

			while(!scan.hasNextInt()) {
				System.out.println("La valeur rentrée n'était pas du type voulu");
				scan.next();
			}

			ans = scan.nextInt();

			switch(ans) {
			case 1 : // versement sur le compte
				System.out.println("Tapez le montant à ajouter.");

				while(!scan.hasNextLong()) {
					System.out.println("La valeur rentrée est incorrecte, saisir une nouvelle entrée.");
					scan.next();
				}
				amount =scan.nextLong();
				pay(account, amount);
				break;

			case 2 : // retrait
				System.out.println("Tapez le montant à retiré.");

				while(!scan.hasNextLong()) {
					System.out.println("La valeur rentrée est incorrecte, saisir une nouvelle entrée.");
					scan.next();
				}
				amount =scan.nextLong();
				try {
					withdraw(account, amount);
				} catch (Exception e) {
					System.out.println(e.getMessage());

				}
				break;

			case 3 : 	// virement
				System.out.println("Tapez le montant à viré, ainsi que le numéro de compte destinaire.");

				while(!scan.hasNextLong()) {
					System.out.println("La valeur rentrée est incorrecte, saisir une nouvelle entrée.");
					scan.next();
				}
				amount =scan.nextLong();
				accIdDest=scan.nextLong();
				try {
					transfert( account, accIdDest, amount);
				} catch (Exception e) {
					System.out.println(e.getMessage());

				}

				break;

			case 4 : 	// informations du compte
				System.out.println("Informations du compte : ");
				System.out.println("-------------------------------------------------------");
				System.out.println(account.toString());
				System.out.println("-------------------------------------------------------");

				break;

			case 5 : 	// liste des opérations
				System.out.println("Opérations du compte : ");
				System.out.println("-------------------------------------------------------");
				for(Transaction trans : listTransactions(account.getAccountId()))
					  System.out.println(trans);
					
				System.out.println("-------------------------------------------------------");
			
				break;

			case 6 : // Exit account
				System.out.println("Exit account.");
				break;

			default : System.out.println("Mauvaise saisie, votre choix : "+ans+" est inexistant dans le menu");
			}	
		}

	}

}
