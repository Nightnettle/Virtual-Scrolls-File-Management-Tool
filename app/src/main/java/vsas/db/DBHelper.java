package vsas.db;

// lazy import
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/*
 * This class is responsible for managing interactions with the
 * MySQL database.
 */
public class DBHelper {
    // Temporary database containing all the exchange rates
    private static final String DB_NAME = "vsas";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;

    // admin login creds to DB
    private static final String USERNAME = "dbUser";
    private static final String PASSWORD = "password";

    // make this singleton since we only need one instance of the database at any time
    private static DBHelper instance = null;

    private Connection connection;
    private ResultSet resultSet = null;

    public static final int MAX_PHONENUM_LEN = 10;

    private DBHelper(int inst_type) {
        try {
            // Load the MySQL JDBC driver
	        String useDB = String.valueOf(DB_URL);
	        if (inst_type == 1) { // Test database used
		        useDB = useDB + "_test";
	        }
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(useDB +
                    "?user=" + USERNAME + "&password=" + PASSWORD
                    + "&useSSL=false"
                    + "&allowPublicKeyRetrieval=true");
		
            // Check if tables exist, otherwise create them
            Statement stmt = conn.createStatement();
	        int i = 0;
            for (String query : getSQLQueries("create_DB.sql").split(";")) {
                stmt.executeUpdate(query + ";");
		        i += 1;
            }

            resultSet = stmt.executeQuery("select count(*) as c from users;");
            int count_u = 0;

            if (resultSet.next())
                count_u = resultSet.getInt("c");

            if (count_u < 2) {
                resetDatabaseToDefault(stmt);
            }

            stmt.close();
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBHelper getInstance(boolean prod) {
	    int inst_type = 1;
	    if (prod) inst_type -= 1;
        if(instance == null) {
            instance = new DBHelper(inst_type);
        }
        return instance;
    }


    /* Helper methods */

    // Get SHA-256 hash from string as a string
    public static String getHashString(String password) {
        byte[] out = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
            out = encodedhash;
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return bytesToHex(out);
    }

    // Turn given bytes to hex
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');  // Append a leading 0 if the hex string is only 1 character long
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Get password hash from username
    public String getPasswordFromDB(String userid) {
    	String pass = null;
        String qry = "SELECT password FROM users " +
		"where userid = ?;";
        
	try (Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(qry);
	    ){
		// Loop which iterates through each row of the result
	    pstmt.setString(1, userid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pass = rs.getString("password");
            }
	    rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pass;

    }
    
    // Get create queries
    private String getSQLQueries(String filename) {
        String out = "";
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                out = out + myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return out;
    }

    // Start a new connection to the DB
    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL +
                "?user=" + USERNAME + "&password=" + PASSWORD
                + "&useSSL=false"
                + "&allowPublicKeyRetrieval=true");
        return conn;
    }

    public Connection getDBConnection() throws SQLException {
        return getConnection();
    }

    /*
     * Helper method to display an error dialog box if wrong user selection choice
     */
    public void displayMsgBox(Alert.AlertType alertType, String title, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // List of all users
    public ArrayList<String> getAllUsers() {
        ArrayList<String> userList = new ArrayList<String>();
        String qry = "SELECT userid FROM users;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(qry)) {
            // Loop which iterates through each row of the result
            while (rs.next()) {
                String userid = rs.getString("userid");
                // Store each row as an ExchangeRate object (makes it easier for the UI later on)
                userList.add(userid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }


    // Check userid already exists
    public boolean checkUserIdExists(String userid) {
    	ArrayList<String> userList = getAllUsers();
	    return userList.contains(userid);
    }

    public boolean goodPhonenum(String phonenum) {
    	return (phonenum.length() <= MAX_PHONENUM_LEN);
    }

    // Add a new user - assumes that all args are valid or null
    // Make validation functions for all args if necessary
    // Make a check for userid being unique - necessary
    public void addUser(String userid, String password, String utype,
		                String phonenum, String email, String fullname) {
    	String qry = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(qry);) {
            pstmt.setString(1, userid);
            pstmt.setString(2, getHashString(password));
            pstmt.setString(3, utype);
            pstmt.setString(4, phonenum);
            pstmt.setString(5, email);
            pstmt.setString(6, fullname);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            displayMsgBox(Alert.AlertType.ERROR, "Database Error!", "Failed to register user.");
            e.printStackTrace();
        }
    }

    public String getUserType(String userid) {
    	String out = null;
        String qry = "SELECT utype FROM users WHERE userid = ?";
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(qry);
            ){
                pstmt.setString(1, userid);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.isBeforeFirst()) {
                        out = null;
                }
                else {
                        rs.next();
                        out = rs.getString("utype");
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        return out;
    }


    public HashMap<String,String> getUserFromDB(String userid) {
	HashMap<String,String> out = new HashMap<String,String>();
    	String qry = "SELECT * FROM users WHERE userid = ?";
	try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(qry);
            ){
		pstmt.setString(1, userid);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.isBeforeFirst()) {
			out = null;
		}
		else {
			rs.next();
			out.put("userid", rs.getString("userid"));
			out.put("password", rs.getString("password"));
			out.put("utype", rs.getString("utype"));
			out.put("phonenum", rs.getString("phonenum"));
                        out.put("email", rs.getString("email"));
                        out.put("fullname", rs.getString("fullname"));
		}
	    } catch(SQLException e) {
	    	e.printStackTrace();
	    }
	return out;
    } 

    // Update user with new data
    public void updateUser(String userid, String password, String utype,
                    String phonenum, String email, String fullname) {
        String qry = "UPDATE users SET password = ?, " +
		"utype = ?, phonenum = ?, email = ?, fullname = ? " +
		"WHERE userid = ?";
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(qry);
            ){
                pstmt.setString(6, userid);
                pstmt.setString(1, getHashString(password));
                pstmt.setString(2, utype);
                pstmt.setString(3, phonenum);
                pstmt.setString(4, email);
                pstmt.setString(5, fullname);

                pstmt.executeUpdate();
        } catch (SQLException e) {
                e.printStackTrace();
        }
    }

    // Update user id
    public void updateUserId(String userid, String new_uid) {
    	String qry = "UPDATE users SET userid = ? " +
                "WHERE userid = ?";
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(qry);
            ){
                pstmt.setString(2, userid);
                pstmt.setString(1, new_uid);

                pstmt.executeUpdate();
        } catch (SQLException e) {
                e.printStackTrace();
        }
    }

    // Delete user
    public void deleteUser(String userid) {
        String qry = "DELETE FROM users " +
                "WHERE userid = ?";
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(qry);
            ){
                pstmt.setString(1, userid);

                pstmt.executeUpdate();
        } catch (SQLException e) {
                e.printStackTrace();
        }
    }


    
    // Add a new currency pair with the exchange rate
    /*
    public void addCurrencyPairWithRate(String currency1, String currency2, double rate, Date setDate) throws SQLException {
        // Ensure both currencies exist in the Currency table before adding the exchange rate
        ensureCurrencyExists(currency1, null);
        ensureCurrencyExists(currency2, null);

        // Insert the new exchange rate into the ExchangeRate table
        String insertSQL = "INSERT INTO ExchangeRate (Currency1, Currency2, Rate, SetDate, HasChanged) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getDBConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, currency1);
            preparedStatement.setString(2, currency2);
            preparedStatement.setDouble(3, rate);
            preparedStatement.setDate(4, setDate);
            preparedStatement.setInt(5, 1);  // Assuming HasChanged is set to 1 for new entries

            preparedStatement.executeUpdate();
        }
    }
    */

    /*
    public void updateExchangeRate(String fromCurrency, String toCurrency, double exchangeRate, Date setDate, String hasChanged) {
    	try {
            // These checks are not really needed as the currencies are always going to be valid
            // ensureCurrencyExists(fromCurrency, null);
            // ensureCurrencyExists(toCurrency, null);
            String qry = "UPDATE ExchangeRate SET Rate = ?, HasChanged = ? WHERE Currency1 = ? and Currency2 = ? and SetDate = ?";
            try (Connection conn = getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement(qry)) {
                preparedStatement.setString(3, fromCurrency);
                preparedStatement.setString(4, toCurrency);
                preparedStatement.setDouble(1, exchangeRate);
                preparedStatement.setDate(5, setDate);
                preparedStatement.setString(2, hasChanged);
                preparedStatement.executeUpdate();

                System.out.println("Exchange rate updated: " + fromCurrency + " to " + toCurrency + " at rate " + exchangeRate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */

    /*
    public void addExchangeRate(String fromCurrency, String toCurrency, double exchangeRate, Date setDate, String hasChanged) {
        try {
            // These checks are not really needed as the currencies are always going to be valid
            // ensureCurrencyExists(fromCurrency, null);
            // ensureCurrencyExists(toCurrency, null);
            String qry = "INSERT INTO ExchangeRate (Currency1, Currency2, Rate, SetDate, hasChanged) values (?, ?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement(qry)) {
                preparedStatement.setString(1, fromCurrency);
                preparedStatement.setString(2, toCurrency);
                preparedStatement.setDouble(3, exchangeRate);
                preparedStatement.setDate(4, setDate);
                preparedStatement.setString(5, hasChanged);
                preparedStatement.executeUpdate();

                System.out.println("Exchange rate added: " + fromCurrency + " to " + toCurrency + " at rate " + exchangeRate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
/*
    public void addCurrency(String currencyCode, String currencyName) {
        try {
            // Adds the new Currency type if it doesn't already exist
            ensureCurrencyExists(currencyCode, currencyName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
    /*
    public void updatePopularCurrencies(List<String> popularCurrencies) throws SQLException {
        String clearSQL = "TRUNCATE TABLE PopCurrency";
        String insertSQL = "INSERT INTO PopCurrency (CurrencyCode) VALUES (?)";
        try (Connection conn = getDBConnection();
             Statement clearStmt = conn.createStatement();
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
            clearStmt.executeUpdate(clearSQL);
            for (String currency : popularCurrencies) {
                insertStmt.setString(1, currency);
                insertStmt.executeUpdate();
            }
        }
    } */

    private void resetDatabaseSchema(Statement stmt) throws SQLException {
        System.out.println("Resetting database schema...");

        stmt.executeUpdate("DROP TABLE scrolls;");
        stmt.executeUpdate("DROP TABLE users;");
        for (String query : getSQLQueries("create_DB.sql").split(";")) {
            if (!query.trim().isEmpty()) {  // Only execute if query is not empty
                stmt.executeUpdate(query + ";");
            }
        }
        //displayMsgBox("Database Schema Reset", "Database schema reset successfully!");
    }

    
    private void resetDatabaseToDefault(Statement stmt) throws SQLException {
        System.out.println("Resetting database to default state...");
	
        stmt.executeUpdate("TRUNCATE TABLE scrolls;");
        stmt.executeUpdate("TRUNCATE TABLE users;");
        for (String query : getSQLQueries("insert_DB.sql").split(";")) {
            if (!query.trim().isEmpty()) {  // Only execute if query is not empty
                stmt.executeUpdate(query + ";");
            }
        }
        //displayMsgBox("Database Reset", "Database reset successfully!");
    } 
    
   

    public void resetDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            resetDatabaseToDefault(stmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetSchema() {
    	try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            resetDatabaseSchema(stmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
