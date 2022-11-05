import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem extends Exception {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);		// Create a new FileInputStream object using our filename parameter
			props.load(input);											// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");					// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//init
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	  }//testConnection

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
		System.out.println("\n:: CREATE NEW CUSTOMER - RUNNING");
        try {                                                                  
        	con = DriverManager.getConnection(url, username, password);                 
          	stmt = con.createStatement(); 
          	stmt.executeUpdate("INSERT INTO p1.customer(name, gender, age, pin) VALUES('" + name + "', '" + gender + "'," 
		  					+ Integer.valueOf(age) + "," + Integer.valueOf(pin) + ")");
		  	rs = stmt.executeQuery("SELECT IDENTITY_VAL_LOCAL() FROM p1.customer LIMIT 1");
		  	while(rs.next())
		  		System.out.println("YOUR ID: " + rs.getInt(1));  
			rs.close();
        	stmt.close();                                                                           
        	con.close();
			System.out.println(":: CREATE NEW CUSTOMER - SUCCESS\n");                                                                       
        } catch (Exception e) {
          System.out.println("Exception in newCustomer()");
          e.printStackTrace();
        }
	}//newCustomer

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		System.out.println("\n:: OPEN ACCOUNT - RUNNING");
		try {                                                                  
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement();

			//Check if a customer exists 
			rs = stmt.executeQuery("SELECT 1 FROM p1.customer WHERE id = " + Integer.valueOf(id) + " LIMIT 1");
			if(!rs.next())
				throw new AccountNotExistException("CUSTOMER NOT FOUND\n");
			
			//Exectute the openAccount operation
			stmt.executeUpdate("INSERT INTO p1.account(id, balance, type, status) VALUES(" + Integer.valueOf(id) + ", " + Integer.valueOf(amount) + ", '" + type + "', 'A')");
			
			//Print an account number
			rs = stmt.executeQuery("SELECT IDENTITY_VAL_LOCAL() FROM p1.account LIMIT 1");
		  	while(rs.next())
		  		System.out.println("YOUR ACCOUNT NUMBER: " + rs.getInt(1)); 
			rs.close();                                         
			stmt.close();                                                                    
			con.close();
			System.out.println(":: OPEN ACCOUNT - SUCCESS\n");                                                                       
		  }catch(AccountNotExistException ex) {
			System.out.println(ex.getMessage());
		  }catch (Exception e) {
			System.out.println("Exception in openAccount()");
			e.printStackTrace();
		  }
	}//openAccount

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		System.out.println("\n:: CLOSE ACCOUNT - RUNNING");
		try {                                                                  
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement(); 
			
			//Check if an account exists
			rs = stmt.executeQuery("SELECT 1 FROM p1.account WHERE number = " + Integer.valueOf(accNum) + " LIMIT 1");
			if(!rs.next())
				throw new AccountNotExistException("ACCOUNT NOT FOUND\n");

			//Check if an account is active
			rs = stmt.executeQuery("SELECT status FROM p1.account WHERE number = " + Integer.valueOf(accNum));
			while(rs.next())
				if(rs.getString(1).charAt(0) == 'I')
					throw new InactiveAccountException("THE ACCOUNT IS ALREADY CLOSED\n");
			
			//rs = stmt.executeQuery("SELECT id, number FROM p1.account WHERE ");

			//Execute the closeAccount operation
			stmt.executeUpdate("UPDATE p1.account SET status = 'I', balance = 0 WHERE number = " + Integer.valueOf(accNum));

			rs.close();                                         
			stmt.close();                                                                           
			con.close(); 
			System.out.println(":: CLOSE ACCOUNT - SUCCESS\n");                                                                           
		}catch(AccountNotExistException ex) {
			System.out.println(ex.getMessage());
		}catch(InactiveAccountException ex) {
			System.out.println(ex.getMessage());
		}catch (Exception e) {
			System.out.println("Exception in closeAccount()");
			e.printStackTrace();
		}
	}//closeAccount

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		System.out.println("\n:: DEPOSIT - RUNNING");
		try {                                                             
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement();

			//Check if an account exists 
			rs = stmt.executeQuery("SELECT 1 FROM p1.account WHERE number = " + Integer.valueOf(accNum) + " LIMIT 1");
			if(!rs.next())
				throw new AccountNotExistException("ACCOUNT NOT FOUND\n");

			//Check if an account is active
			rs = stmt.executeQuery("SELECT status FROM p1.account WHERE number = " + Integer.valueOf(accNum));
			while(rs.next())
				if(rs.getString(1).charAt(0) == 'I')
					throw new InactiveAccountException("ACCOUNT IS INACTIVE\n");
			
			//Execute a deposit operation
			stmt.executeUpdate("UPDATE p1.account SET balance = balance + " 
							+ Integer.valueOf(amount) + " WHERE number = " + Integer.valueOf(accNum)); 
			rs.close();                                  
			stmt.close();                                                                           
			con.close(); 
			System.out.println(":: DEPOSIT - SUCCESS\n");                                                                          
		  }catch(AccountNotExistException ex) {
			System.out.println(ex.getMessage());
		  }catch(InactiveAccountException ex) {
			System.out.println(ex.getMessage());
		  }catch (Exception e) {
			System.out.println("Exception in deposit()");
			e.printStackTrace();
		  }
	}//deposit

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount)
	{
		System.out.println("\n:: WITHDRAW - RUNNING");
		try {                                                                  
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement(); 

			//Check if an account exists 
			rs = stmt.executeQuery("SELECT 1 FROM p1.account WHERE number = " + Integer.valueOf(accNum) + " LIMIT 1");
			if(!rs.next())
				throw new AccountNotExistException("ACCOUNT NOT FOUND\n");

			//Check if an account is active and possess a required amount
			rs = stmt.executeQuery("SELECT balance, status FROM p1.account WHERE number = " + Integer.valueOf(accNum));
			while(rs.next()) {
				if(rs.getInt(1) < Integer.valueOf(amount))
					throw new InsufficientBalanceException("INSUFFICIENT BALANCE\n");
				else if(rs.getString(2).charAt(0) == 'I')
					throw new InactiveAccountException("ACCOUNT IS INACTIVE\n");                                             
			}

			//Execute a withdrawal operation
			stmt.executeUpdate("UPDATE p1.account SET balance = balance - " + Integer.valueOf(amount) + 
								" WHERE status <> 'I' AND number = " + Integer.valueOf(accNum));    
			                                            
			rs.close();
			stmt.close();                                                                           
			con.close(); 
			System.out.println(":: WITHDRAW - SUCCESS\n");                                                                          
		  }catch(AccountNotExistException ex) {
			System.out.println(ex.getMessage());
		  }catch(InsufficientBalanceException ex) {
			System.out.println(ex.getMessage());
		  }catch(InactiveAccountException ex) {
			System.out.println (ex.getMessage());
		  }catch (Exception e) {
			System.out.println("Exception in withdraw()");
			e.printStackTrace();
		  }

	}//withdraw

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
		System.out.println("\n:: TRANSFER - RUNNING");
		try {                                                                  
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement();

			//Check if the source account exists 
			rs = stmt.executeQuery("SELECT 1 FROM p1.account WHERE number = " + Integer.valueOf(srcAccNum) + " LIMIT 1");
			if(!rs.next())
				throw new AccountNotExistException("SOURCE ACCOUNT NOT FOUND\n");

			//Check if the source account is active and possess a required amount 
			rs = stmt.executeQuery("SELECT balance, status FROM p1.account WHERE number = " + Integer.valueOf(srcAccNum));
			while(rs.next()) {
				if(rs.getInt(1) < Integer.valueOf(amount))
					throw new InsufficientBalanceException("INSUFFICIENT BALANCE\n");
				else if(rs.getString(2).charAt(0) == 'I')
					throw new InactiveAccountException("SOURCE ACCOUNT IS INACTIVE\n");                                             
			}

			//Check if the destination account exists 
			rs = stmt.executeQuery("SELECT 1 FROM p1.account WHERE number = " + Integer.valueOf(destAccNum) + " LIMIT 1");
			if(!rs.next())
				throw new AccountNotExistException("DESTINATION ACCOUNT NOT FOUND\n");

			//Check if the destination account is active
			rs = stmt.executeQuery("SELECT status FROM p1.account WHERE number = " + Integer.valueOf(destAccNum));
			while(rs.next())
				if(rs.getString(1).charAt(0) == 'I')
					throw new InactiveAccountException("DESTINATION ACCOUNT IS INACTIVE\n");

			//Execute a transfer operation
			stmt.executeUpdate("UPDATE p1.account SET balance = balance - " + Integer.valueOf(amount) + 
								" WHERE status <> 'I' AND number = " + Integer.valueOf(srcAccNum));
			stmt.executeUpdate("UPDATE p1.account SET balance = balance + " 
								+ Integer.valueOf(amount) + " WHERE number = " + Integer.valueOf(destAccNum));   
			                        
			rs.close();
			stmt.close();                                                                           
			con.close(); 
			System.out.println(":: TRANSFER - SUCCESS\n");                                                                          
		  }catch(AccountNotExistException ex) {
			System.out.println(ex.getMessage());
		  }catch(InsufficientBalanceException ex) {
			System.out.println(ex.getMessage());
		  }catch(InactiveAccountException ex) {
			System.out.println (ex.getMessage());
		  }catch (Exception e) {
			System.out.println("Exception in withdraw()");
			e.printStackTrace();
		  }
	}//transfer

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
		int totalBalance = 0;
		System.out.println("\n:: ACCOUNT SUMMARY - RUNNING");
		try {                                                                  
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); 

			//Check if a customer exists 
			rs = stmt.executeQuery("SELECT 1 FROM p1.account WHERE id = " + Integer.valueOf(cusID) + " LIMIT 1");
			if(!rs.next())
				throw new AccountNotExistException("CUSTOMER NOT FOUND\n");
			
			rs = stmt.executeQuery("SELECT number, balance FROM p1.account WHERE status <> 'I' AND id = " + Integer.valueOf(cusID));
			if(!rs.next())
				System.out.println("NO ACCOUNT EXISTS FOR A CUSTOMER: " + cusID + "\n");
			else {
				rs.beforeFirst();
				System.out.println("NUMBER\tBALANCE");
				while(rs.next()) {
					System.out.println(rs.getInt(1) + "\t" + rs.getInt(2));
					totalBalance += rs.getInt(2);
				}
				System.out.println("TOTAL BALANCE: " + totalBalance + "\n");
				System.out.println(":: ACCOUNT SUMMARY - SUCCESS\n");
			}
			rs.close();                                     
			stmt.close();                                                                           
			con.close();                                                                          
		  }catch(AccountNotExistException ex) {
			System.out.println(ex.getMessage());
		  }catch (Exception e) {
			System.out.println("Exception in accountSummary()");
			e.printStackTrace();
		  }	
	}//accountSummary

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		System.out.println(":: REPORT A - RUNNING");
		try {                                                                   
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement(); 
			rs = stmt.executeQuery("SELECT SUM(balance) FROM p1.account WHERE status <> 'I' GROUP BY id ORDER BY SUM(balance) DESC");
			while(rs.next()) {
				System.out.println(rs.getInt(1));
			}
			rs.close();
			stmt.close();
			con.close(); 
			System.out.println(":: REPORT A - SUCCESS\n");                                                                   
		  } catch (Exception e) {
			System.out.println("Exception in reportA()");
			e.printStackTrace();
		  }		
	}//reportA

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		System.out.println(":: REPORT B - RUNNING");
		try {                                                                  
			con = DriverManager.getConnection(url, username, password);                 
			stmt = con.createStatement(); 
			
			rs = stmt.executeQuery("SELECT AVG(balance) FROM p1.account INNER JOIN p1.customer ON p1.account.id = p1.customer.id" 
							+ " AND p1.account.status <> 'I' AND p1.customer.age >= " + Integer.valueOf(min) + " AND p1.customer.age <= " + Integer.valueOf(max));
			while(rs.next())
				System.out.println("AVERAGE BALANCE: " + rs.getInt(1));
			
				rs.close();
			stmt.close();
			con.close(); 
			System.out.println(":: REPORT B - SUCCESS\n");                                                                     
		  } catch (Exception e) {
			System.out.println("Exception in reportB()");
			e.printStackTrace();
		  }		
	}//reportB
}

class InsufficientBalanceException extends Exception {
	public InsufficientBalanceException(String s) {
		super(s);
	}
}
class InactiveAccountException extends Exception {
	public InactiveAccountException(String s) {
		super(s);
	}
}

class AccountNotExistException extends Exception {
	public AccountNotExistException(String s) {
		super(s);
	}
}
