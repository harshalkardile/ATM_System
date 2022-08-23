import java.util.Scanner;

public class ATM {

	public static void main(String[] args) {
		
		//init scannner 
		Scanner sc= new Scanner(System.in);
		
		//init Bank
		Bank theBank= new Bank("Bank of India");
		
		// add a user which also creates a savings account 
		User aUser = theBank.addUser("Harshal", "kardile", "1234");
		
		//add a checking account for a user
		Account newAccount = new Account("Current", aUser, theBank); 
		aUser.addAccount(newAccount);
		theBank.addAccount(newAccount);
		
		User curUser;
		while(true) {
			//stay in the login prompt until successful login
			curUser = ATM.mainMenuPrompt(theBank, sc);
			
			//stay in the main until user quits
			ATM.printUserMenu(curUser, sc);
		}
	}
	/**
	 * Print the ATM's login menu
	 * @param theBank	the bank object while account to use 
	 * @param sc	 	the scanner object to use 
	 * @return			the authenticated user object 
	 */
	public static User mainMenuPrompt(Bank theBank, Scanner sc) {
		
		//inits
		String userID;
		String pin;
		User authUser;
		
		// Prompt the user for user ID/pin combo until a correct one is reached 
		do {
			
			System.out.printf("\nWelcome to %s\n", theBank.getName());
			System.out.print("Enter user ID: ");
			userID = sc.nextLine();
			System.out.print("Enter pin: ");
			pin = sc.nextLine();
			
			// try to get the user object corresponding to the ID and pin combo 
			authUser = theBank.userLogin(userID, pin);
			if(authUser == null) {
				System.out.println("Incorrect user ID/pin combination. " + "Please try again.");
			}
		} while(authUser == null); // continue looping until successful login

		return authUser;
	}
	
	public static void printUserMenu(User theUser, Scanner sc) {
		
		//print summary of the users accounts
		theUser.printAccountSummary();
		
		//init 
		int choice;
		
		//user menu 
		do {
			
			System.out.printf("Welcome %s what would you like to do?\n",	 
						theUser.getFirstName());
			System.out.println("	1) Show account transaction history");
			System.out.println("	2) Withdraw");
			System.out.println("	3) Deposit");
			System.out.println("	4) Transfer");
			System.out.println("	5) Quit");
			System.out.println();
			System.out.print("Enter your choice: ");
			choice = sc.nextInt();
			
			if (choice < 1 || choice > 5) {
				System.out.println("Invalid choice. please choose 1-5 ");
			}
		} while(choice < 1 || choice > 5);
		
			//process the choice 
			switch (choice) {
			case 1:
				ATM.showTransHistory(theUser, sc);
				break;
			case 2: 
				 ATM.withdrawFunds(theUser, sc);
				break;
			case 3:
				ATM.depositFunds(theUser, sc);
				break;
			case 4:
				ATM.transferFunds(theUser, sc);
				break;
			case 5:
				//gobble up rest of previous input
				sc.nextLine();
				break;
			}
		
		// redisplay this menu unless the user wants to quit 
		if(choice != 5) {
			ATM.printUserMenu(theUser,sc);
		}
	}
	/**
	 * Show the transaction history for an account
	 * @param theUser	the logged-in user object
	 * @param sc		the Scanner object used for user input
	 */
	public static void showTransHistory(User theUser,Scanner sc) {
		
		int theAcct;
		
		//get account whose transaction history to look at
		do {
			System.out.printf("Enter the number (1-%d) of the account\n"+
							  "whose transactions you want to see:",
							   theUser.numAccounts());
			theAcct = sc.nextInt()-1;
			if(theAcct < 0 || theAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
				}
			}while(theAcct < 0 || theAcct >= theUser.numAccounts());
		
		// print transaction history
		theUser.printAcctTransHistory(theAcct);
		
	}
	
	/**
	 * Process transferring funds from one accoun to another
	 * @param theUser	the logged-in User object
	 * @param sc		the Scanner object user for user input
	 */
	public static void transferFunds(User theUser, Scanner sc) {
		
		//inits
		int fromAcct;
		int toAcct;
		double amount;
		double acctBal;
		
		// get the account to transfer from 
		do {
			System.out.printf("Enter the number (1-%d) of the account\n"+"to transfer from: ",
								theUser.numAccounts());
			fromAcct= sc.nextInt()-1;
			if(fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. please try again");
			}
		}while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
		acctBal= theUser.getAcctBalance(fromAcct);
		
		// get the account to transfer to
		do {
			System.out.printf("Enter the number (1-%d) of the account\n"+"to transfer to: ",
								theUser.numAccounts());
			toAcct= sc.nextInt()-1;
			if(toAcct < 0 || toAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. please try again");
			}
		}while(toAcct < 0 || toAcct >= theUser.numAccounts());
	
		//get the amount to transfer
		do {
			System.out.printf("Enter the amount to transfer (max ₹%.02f): ₹", acctBal);
			amount = sc.nextDouble();
			if(amount < 0) {
				System.out.println("Amount must be greater than zero.");
			}else if(amount > acctBal) {
				System.out.printf("Amount must not be greater than"+
				"balance of ₹%.02f.\n",acctBal);
			}
		}while (amount < 0 || amount > acctBal);
		
		//finally , do the transfer
		theUser.addAcctTransaction(fromAcct, -1*amount, String.format("Transfer to account %s",
									theUser.getAcctUUID(toAcct)));
		theUser.addAcctTransaction(toAcct, amount, String.format("Transfer to account %s",
				theUser.getAcctUUID(fromAcct)));
	}
	
	/**
	 * Process a fund withdraw from an account
	 * @param theUser	the logged-in User object
	 * @param sc		the scanner object user for user input
	 */
	public static void withdrawFunds(User theUser, Scanner sc) {
		
		//inits
		int fromAcct;
		double amount;
		double acctBal;
		String memo;
		
		// get the account to transfer from 
		do {
			System.out.printf("Enter the number (1-%d) of the account\n"+"to withdraw from: ",
								theUser.numAccounts());
			fromAcct= sc.nextInt()-1;
			if(fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. please try again");
			}
		}while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
		acctBal= theUser.getAcctBalance(fromAcct);
		
		//get the amount to transfer
		do {
			System.out.printf("Enter the amount to withdraw (max ₹%.02f): ₹", acctBal);
			amount = sc.nextDouble();
			if(amount < 0) {
				System.out.println("Amount must be greater than zero.");
			}else if(amount > acctBal) {
				System.out.printf("Amount must not be greater than \n"+
				"balance of ₹%.02f.\n",acctBal);
			}
		}while (amount < 0 || amount > acctBal);
		
		//gobble up rest of previous input 
		sc.nextLine();
		
		//get a memo
		System.out.print("Enter a memo: ");
		memo= sc.nextLine();
		
		// do the withdraw
		theUser.addAcctTransaction(fromAcct, -1*amount, memo);
		
	}
	
	/**
	 * Process a fund deposit to an account 
	 * @param theUser	the logged-in User object
	 * @param sc		the Scanner object used for used input 
	 */
	public static void depositFunds(User theUser, Scanner sc) {
		
		//inits
		int toAcct;
		double amount;
		double acctBal;
		String memo;
		
		// get the account to transfer from 
		do {
			System.out.printf("Enter the number (1-%d) of the account\n"+"to deposit in: ",
								theUser.numAccounts());
			toAcct= sc.nextInt()-1;
			if(toAcct < 0 || toAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. please try again");
			}
		}while(toAcct < 0 || toAcct >= theUser.numAccounts());
		acctBal= theUser.getAcctBalance(toAcct);
		
		//get the amount to transfer
		do {
			System.out.printf("Enter the amount to transfer (max ₹%.02f): ₹", acctBal);
			amount = sc.nextDouble();
			if(amount < 0) {
				System.out.println("Amount must be greater than zero.");
			}
		}while (amount < 0);
		
		//gobble up rest of previous input 
		sc.nextLine();
		
		//get a memo
		System.out.print("Enter a memo: ");
		memo= sc.nextLine();
		
		// do the withdraw
		theUser.addAcctTransaction(toAcct, amount, memo);
	}
}
