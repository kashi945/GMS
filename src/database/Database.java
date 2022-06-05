
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author Kashif
 */
public class Database {
    
    
    private static String url = "jdbc:sqlite:src/GSM/GSM.sqlite";
    
    private static Connection con;
    
    public static Connection connection() throws java.sql.SQLException
    {
        try {
            
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(url);
            System.out.println("Successful");
           
        }
        
        catch(ClassNotFoundException ex){
            //ex.printStackTrace();
            System.out.println("unSuccessful");
            
        }
        catch (java.sql.SQLException e)
        {
            e.printStackTrace();
        }
        return con;
        
    }
    
}
