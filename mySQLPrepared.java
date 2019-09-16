
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.io.*;

public class mySQLPrepared
{
	//static String  myDriver = "org.gjt.mm.mysql.Driver";
	static String myDriver = "com.mysql.jdbc.Driver";
    //static String myUrl = "jdbc:mysql://localhost/mytool";
    static String myUrl, user, password;
  public static void main(String[] args)
  {
	  
	  
    try
    {
      // create a mysql database connection
    	
    	
        //System.out.println("&&&&&&&&&&"+myUrl);
        //in.close();
      
      Class.forName(myDriver);
      Connection conn = DriverManager.getConnection(myUrl, user, password);
     
      // create a sql date object so we can use it in our INSERT statement
      Calendar calendar = Calendar.getInstance();
      java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
 
      // the mysql insert statement
      String query = " insert into users (first_name, last_name, date_created, is_admin, num_points)"
        + " values (?, ?, ?, ?, ?)";
 
      // create the mysql insert preparedstatement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString (1, "Barney");
      preparedStmt.setString (2, "Rubble");
      preparedStmt.setDate   (3, startDate);
      preparedStmt.setBoolean(4, false);
      preparedStmt.setInt    (5, 5000);
 
      // execute the preparedstatement
      preparedStmt.execute();
       
      conn.close();
    }
    catch (Exception e)
    {
      System.err.println("Got an exception!");
      System.err.println(e.getMessage());
    }
  }
  
  public Connection getConn() throws SQLException{
	  
	  
	  Connection conn = null;
	  try {
		  System.out.println(System.getProperty("user.dir"));
	        ArrayList<String> items =  new ArrayList<String>();
	        FileInputStream fstream = new FileInputStream(System.getProperty("user.dir") + "/file.txt");
	        DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String strLine;

	        while ((strLine = br.readLine()) != null)   {

	            // Print the content on the console
	        	System.out.print(":"+strLine.trim()+":");
	        	if(!(strLine.trim().equals(""))){
	        		List<String> container = Arrays.asList(strLine.split(","));
		        	//items =  (ArrayList<String>) Arrays.asList(strLine.split("\\s*,\\s*"));
		            System.out.println (container.get(0)+" "+container.get(1));
		            if((container.get(0)).equals("mysql_url")){
		            	myUrl = container.get(1);
		            }
		            else if((container.get(0)).equals("mysql_user")){
		            	user = container.get(1);
		            }
		            else if((container.get(0)).equals("mysql_pwd")){
		            	password = container.get(1);
		            }
	        	}
	        	else{
	        		System.out.println("#############IN ELSE"+strLine.trim()+"***");
	        	}
	        }

		Class.forName(myDriver);
		 conn = DriverManager.getConnection(myUrl, user, password);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
	  return conn;
  }
  
  
}

