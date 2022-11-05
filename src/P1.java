import java.util.*;
import java.io.*;

public class P1 {
    public static void MainMenu() {
        Scanner scanner = new Scanner(System.in);
        String name = null;
        char gender = 0;
        int age = 0;
        int pin = 0;
        int id = 0;
        int input = 0;

        System.out.println("Welcome to the Self Service Banking System!");
        
        while(input != 3) {
            System.out.println("Main Menu\n"
                                + "1. New Customer\n"
                                + "2. Customer Login\n"
                                + "3. Exit");

            input = scanner.nextInt();
            scanner.nextLine();
            switch(input) {
                case 1:
                    System.out.println("Enter your name:");
                    name = scanner.nextLine();
                    while(Character.toUpperCase(gender) != 'M' && Character.toUpperCase(gender) != 'F') { //why does not || work?
                    System.out.println("Enter your gender(M/F):");
                    gender = Character.toUpperCase(scanner.next().trim().charAt(0));
                    }
                    System.out.println("Enter your age:");
                    age = scanner.nextInt();
                    while(String.valueOf(pin).length() != 4) {
                    System.out.println("Create 4 integer PIN:");
                    pin = scanner.nextInt();
                    }
                    BankingSystem.newCustomer(name, Character.toString(gender), String.valueOf(age), String.valueOf(pin));
                    break;
                
                case 2:
                    System.out.println("Enter your Customer ID:");
                    id = scanner.nextInt();
                    System.out.println("Enter your PIN:");
                    pin = scanner.nextInt();
                    
                    if(id == 0 && pin == 0)
                        AdminMainMenu();
                    else {
                      

                        CustomerMainMenu();
                    }
                    break;

                    
                    /*try {
                        Class.forName(BankingSystem.); 
                        String url = BankingSystem.init.url;
                        String username = BankingSystem.init.username;
                        String password = BankingSystem.init.password;                                                                  
                        Connection con = DriverManager.getConnection(url, username, password);                 
                        PreparedStatement execStat = con.prepareStatement("SELECT id, pin FROM p1.customer WHERE id = " + id + " AND pin = " + pin);
                        ResultSet rs = execStat.executeQuery();
                        while(rs.next()) {
                        System.out.println(rs.getInt(1));
                        System.out.println(rs.getInt(2));
                        CustomerMainMenu();
                        }
                        rs.close();                                           
                        execStat.close();                                                                           
                        con.close();                                                                            
                    } catch (Exception e) {
                        System.out.println("Exception in main()");
                        e.printStackTrace();
                    }*/
                    //If login is successful go to the customer main menu
                case 3:
                    scanner.close();
                    System.exit(0);
                    break;
            }
        }
    }//MainMenu

    public static void CustomerMainMenu() {
        Scanner scanner = new Scanner(System.in);
        int id = 0;
        char type = 0;
        int amount;
        int accNum = 0;
        int destAccNum = 0;
        int input = 0;
        
        while(input != 7) {
            System.out.println("Customer Main Menu\n"
            + "1. Open Account\n" 
            + "2. Close Account\n" 
            + "3. Deposit\n" 
            + "4. Withdraw\n" 
            + "5. Transfer\n" 
            + "6. Account Summary\n" 
            + "7. Exit");

            input = scanner.nextInt();
            scanner.nextLine();
            switch(input) {
                case 1: //Open Account
                    System.out.println("Enter a customer ID:"); 
                    id = scanner.nextInt();
                    scanner.nextLine();
                    while(Character.toUpperCase(type) != 'C' && Character.toUpperCase(type) != 'S') { 
                        System.out.println("Would you like to open a checking(C) or saving(S) account?(C/S):");
                        type = Character.toUpperCase(scanner.next().trim().charAt(0));
                        }
                    amount = -1;
                    while(amount < 0) {
                        System.out.println("Enter an initial amount:");
                        amount = scanner.nextInt();
                    }
                    BankingSystem.openAccount(String.valueOf(id), Character.toString(type), String.valueOf(amount));
                    //return account id 
                    break;
                case 2: //Close Account
                    System.out.println("Enter an account number:");
                    accNum = scanner.nextInt();
                    //check if an account exists and check if an accout belongs to the current customer id 
                    BankingSystem.closeAccount(String.valueOf(accNum));
                    break;
                case 3: //Deposit
                    System.out.println("Enter an account number:");
                    accNum = scanner.nextInt();
                    amount = -1;
                    while(amount < 0) {
                        System.out.println("Enter a deposit:");
                        amount = scanner.nextInt();
                    }
                    BankingSystem.deposit(String.valueOf(accNum), String.valueOf(amount));
                    break;
                case 4: //Withdraw
                    System.out.println("Enter an account number:");
                    accNum = scanner.nextInt();
                    amount = -1;
                    while(amount < 0) {
                        System.out.println("Enter a withdrawal amount:");
                        amount = scanner.nextInt();
                    }
                    BankingSystem.withdraw(String.valueOf(accNum), String.valueOf(amount));
                    break;
                case 5: //Transfer
                    System.out.println("Enter a source account number:");
                    accNum = scanner.nextInt();
                    System.out.println("Enter a destination account number:");
                    destAccNum = scanner.nextInt();
                    if(accNum == destAccNum)
                        System.out.println("THE SOURCE AND DESTINATION ACCOUNTS ARE IDENTICAL!");
                    else {
                        amount = -1;
                        while(amount < 0) {
                            System.out.println("Enter a transfer amount:");
                            amount = scanner.nextInt();
                        }
                        BankingSystem.transfer(String.valueOf(accNum), String.valueOf(destAccNum), String.valueOf(amount));
                    }
                    break;
                case 6: //Account Summary
                    System.out.println("Enter a customer ID:");
                    id = scanner.nextInt();
                    BankingSystem.accountSummary(String.valueOf(id));
                    break;
                case 7:
                    return;
            }
        }
    }//CustomerMainMenu
    
    public static void AdminMainMenu() {
        Scanner scanner = new Scanner(System.in);
        int id = 0;
        int min = 0;
        int max = 0;
        int input = 0;

        while(input != 4) {
            System.out.println("1. Account Summary for a Customer\n"
                            + "2. Report A :: Customer Information with Total Balance in Decreasing Order\n"
                            + "3. Report B :: Find the Average Total Balance Between Age Groups\n"
                            + "4. Exit\n");
            input = scanner.nextInt();
            switch(input) {
                case 1:

                    break;
                case 2:
                    BankingSystem.reportA();
                    break;
                case 3:
                    System.out.println("Enter a minimum age:");
                    min = scanner.nextInt();
                    System.out.println("Enter a maximum age:");
                    max = scanner.nextInt();
                    BankingSystem.reportB(String.valueOf(min), String.valueOf(max));
                    break;
                case 4:
                    return;
            }
        }
    }//AdminMainMenu
    
}
