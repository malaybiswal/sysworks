
import java.sql.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

//import tool.sshTest;


public class readQueueInsertDB {
// URL of the JMS server
	//public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
// default broker URL is : tcp://localhost:61616"

// Name of the queue we will receive messages from
	//public static String subject = "MALAY";
	
	public static String url = "tcp://10.12.244.249:61616";
	public static String subject = "jsontoolmsg";
	public static int msgcount=0;

	public static void main(String[] args)   {
		//while(true){
		String mixedjson = "";
		 
		String nrinc = "";
		String snowinc = "";
		String json = "";
		
		String created_at = "";
		String servers = "";
		String account_name = "";
		String message = "";
		String short_description = "";
		String long_description = "";
		String alert_url = "";
		String alert_policy_name = "";
		String severity = "";
		String server_events = "";
		String incident_id[]=new String[2];
		String incnr = "";
		String desc = "";
		int sec = 0;
		String application_name = "";
		int loopcount =0;
		JSONObject jsn;
		String jsonnew="";
//Below code will check if messages in queue. If messages in queue, then sec will be 1 sec else it's 60 sec
		while(true){
			loopcount++;
			try{
			//Thread.sleep(60000);
			msgcount = getMessageCount( url, subject);
			if(msgcount>=1){
				sec=1000;
			}
			else{
				sec=60000;
			}
			Thread.sleep(sec);
			}catch(Exception e1){System.out.println("Thread Exception"+e1);}
			java.util.Date date1= new java.util.Date();
			System.out.println("queue " + " has " + msgcount + " messages" + "**** Printing loop# "+loopcount+" Timestamp:"+new Timestamp(date1.getTime()));
			if(msgcount>0){
				
				try{
				mixedjson = readQueue(url,subject);
				}catch(Exception e2){System.out.println("Mixedjson exception:"+e2);}
				
				String[] parts = mixedjson.split("-----");//Five dashes - acts as separator
				 nrinc = parts[0];
				 snowinc = parts[1];
				 json = parts[2];
				 
					try{
						jsn = new JSONObject(json);
						jsn.put("snow", snowinc);
						jsn.put("nr", nrinc);
						jsonnew = jsn.toString();
						System.out.println(jsonnew);
			        	System.out.println("JSON: "+json);
						JSONObject obj = new JSONObject(json);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
						sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
						if(obj.has("servers")) {
							created_at = obj.getString("created_at");
							created_at = validateTimestamp(created_at);//validates if Z is there else it converts Timestamp to Z format
							servers = obj.getString("servers");
							account_name = obj.getString("account_name");
							message = obj.getString("message");
							short_description = obj.getString("short_description");
							long_description = obj.getString("long_description");
							alert_url = obj.getString("alert_url");
							alert_policy_name = obj.getString("alert_policy_name");
							severity = obj.getString("severity");
							server_events = obj.getString("server_events");
							incident_id=alert_url.split("incidents");
							incnr = incident_id[1].substring(1);
							desc = short_description+" Server And Issue is: "+long_description;
							//application_name = obj.getString("application_name");
						}
						else if(obj.has("application_name")){
							created_at = obj.getString("created_at");
							created_at = validateTimestamp(created_at);//validates if Z is there else it converts Timestamp to Z format
							//servers = obj.getString("servers");
							account_name = obj.getString("account_name");
							message = obj.getString("message");
							short_description = obj.getString("short_description");
							long_description = obj.getString("long_description");
							alert_url = obj.getString("alert_url");
							alert_policy_name = obj.getString("alert_policy_name");
							severity = obj.getString("severity");
							//server_events = obj.getString("server_events");
							//Commented as it was failing, no server_events in application policy
							server_events = "NA";
							incident_id=alert_url.split("incidents");
							incnr = incident_id[1].substring(1);
							desc = short_description+" Server And Issue is: "+long_description;
							application_name = obj.getString("application_name");
						}
						Date date = (Date) sdf.parse(created_at);
						java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
						System.out.println(sqlDate);
						//System.out.println("created_at: "+created_at+"servers: "+servers + "account_name: "+account_name+"message: " + message+"short_description: "+short_description+"Incident id: "+incident_id[1].substring(1)+" application name:"+application_name);
						System.out.println("Before inserting into MySQL DB...");
						System.out.println("insertDB("+sqlDate+","+servers+","+account_name+","+message+","+short_description+","+long_description+","+alert_url+","+snowinc+","+nrinc+","+alert_policy_name+","+severity+","+server_events+","+application_name+","+jsonnew+");");

						insertDB(sqlDate,servers,account_name,message,short_description,long_description,alert_url,snowinc,nrinc,alert_policy_name,severity,server_events,application_name,jsonnew);
						System.out.println("AFTER inserting into MySQL DB...");
					}catch(Exception e){
						System.out.println("Inside Catch Block of readQueueInsert...");
						//e.printStackTrace();
						System.out.println("Main Exception:"+e);
						
						/*Mongo mongo = new Mongo("localhost", 27017);
						DB db = mongo.getDB("json");
						DBCollection collection2 = db.getCollection("errorDB");
	                     DBObject dbObject2 = (DBObject)JSON.parse(jsonnew);
	                     collection2.insert(dbObject2);
	                     System.out.println("Inserting into moongoDB and collection errorDB: "+jsonnew);*/
					}
			}
			
				
		        
		        else{
		        	System.out.println("#############No Message to Read, so no Action Taken");
		        }
				
				
			
		}
		
		//}
	}

	// This method checks if Z is not there in created_at, then it adds Z to timestamp
	//SOmetimes NewRelic sends time in 2016-05-03T18:21:15-05:00 format it needs to be converted to 2016-05-03T18:21:15Z
	public static String validateTimestamp(String created_at){
		System.out.println("Got created_at: "+created_at);
		String mod_created_at="";
		int len = created_at.length();
		System.out.println(created_at.charAt(len-1));
		char c = created_at.charAt(len-1);
		if(c=='Z'||c=='z'){
			System.out.println("created_t in right format");
		}
		else{
			String[] s = created_at.split("-");
			for(int i=0;i<s.length-1;i++){
				mod_created_at = mod_created_at+s[i]+"-";
			}
			created_at = mod_created_at.substring(0,mod_created_at.length()-1)+"Z";
		}
		System.out.println("created_at modified to: "+created_at);
		return created_at;
		
	}
	
	public static String readQueue(String url,String subject) throws JMSException{
	
		// Getting JMS connection from the server
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Creating session for seding messages
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = session.createQueue(subject);

		// MessageConsumer is used for receiving (consuming) messages
		MessageConsumer consumer = session.createConsumer(destination);

		// Here we receive the message.
		// By default this call is blocking, which means it will wait
		// for a message to arrive on the queue.
		Message message = consumer.receive();
		TextMessage textMessage = null;
		// There are many types of Message and TextMessage
		// is just one of them. Producer sent us a TextMessage
		// so we must cast to it to get access to its .getText() method

		if (message instanceof TextMessage) {
			textMessage = (TextMessage) message;
			System.out.println("Receivd Message: " + textMessage.getText() + "'");
		}

		connection.close();
		return textMessage.getText();
	}
	
	public static int getMessageCount(String url,String subject){
		int numMsgs = 0;
		 try{
		        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
				Connection connection = connectionFactory.createConnection();
				connection.start();

				// Creating session for seding messages
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				Destination destination = session.createQueue(subject);

				// MessageConsumer is used for receiving (consuming) messages
				MessageConsumer consumer = session.createConsumer(destination);

		        // create a queue browser
		        QueueBrowser queueBrowser = session.createBrowser((Queue)destination);
		        //Message message = consumer.receive();

		        // start the connection
		        //queueConn.start();

		        // browse the messages
		        Enumeration e = queueBrowser.getEnumeration();
		        

		        // count number of messages
		        while (e.hasMoreElements()) {
		            Message message = (Message) e.nextElement();
		            numMsgs++;
		        }

		        

		        // close the queue connection
		        connection.close();
		        }catch(Exception e){System.out.println("Queue Exception Custom Message:"+e);}
		 return numMsgs;
	}

	
	public static void insertDB(java.sql.Timestamp sqlDate,String servers,String account_name,String message,
			String short_description,String long_description,String alert_url,String snowinc, String nrinc, 
			String alert_policy_name, String severity,String server_events,String application_name, String jsonnew){ 
		String config_type  = "";
        String config_name = "";
        String config_policy_name = "";
        String config_account_name = "";
        String config_issue_type  = "";
        String config_server  = "";
        String config_login  = "";
        String config_pwd  = "";
        String config_port  = "";
        String config_location  = "";
        String config_application ="";
        String config_message = "";
        PreparedStatement preparedStmt =null;
        String myDriver = "";
	      String myUrl = "";
	      java.sql.Connection conn = null;
	      String query=""; String sql ="";
	      PreparedStatement stmt = null;
	      ResultSet rs =  null;
	      
	      /*ArrayList config_types= new ArrayList();
	      ArrayList config_names= new ArrayList();
	      ArrayList config_policy_names= new ArrayList();
	      ArrayList config_account_names= new ArrayList();
	      ArrayList config_issue_types= new ArrayList();
	      ArrayList config_servers= new ArrayList();
	      ArrayList config_logins= new ArrayList();
	      ArrayList config_pwds= new ArrayList();
	      ArrayList config_ports= new ArrayList();
	      ArrayList config_locations= new ArrayList();
	      ArrayList config_application_name = new ArrayList();*/
	      
		try
	    {
	      // create a mysql database connection
	       myDriver = "org.gjt.mm.mysql.Driver";
	       myUrl = "jdbc:mysql://localhost/mytool";
	      Class.forName(myDriver);
	      conn = DriverManager.getConnection(myUrl, "malay", "malay123");
	     
	      // create a sql date object so we can use it in our INSERT statement
	      Calendar calendar = Calendar.getInstance();
	      java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
	 
	      // the mysql insert statement
	       query = " insert into transactions (created_at, servers, account_name, message, short_description,long_description,alert_url,snowinc,nrinc,alert_policy_name,severity,server_events,application_name)"
	        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	 
	      // create the mysql insert preparedstatement
	       preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setTimestamp (1, sqlDate);
	      preparedStmt.setString (2, servers);
	      preparedStmt.setString   (3, account_name);
	      preparedStmt.setString(4, message);
	      preparedStmt.setString    (5, short_description);
	      preparedStmt.setString    (6, long_description);
	      preparedStmt.setString    (7, alert_url);
	      preparedStmt.setString    (8, snowinc);
	      preparedStmt.setString    (9, nrinc);
	      preparedStmt.setString    (10, alert_policy_name);
	      preparedStmt.setString    (11, severity);
	      preparedStmt.setString    (12, server_events);
	      preparedStmt.setString    (13, application_name);
	      
	      String srvr = servers.substring(2,servers.length()-2);
	      // execute the preparedstatement
	      preparedStmt.execute();
	      String msg = message.replaceAll("\\s+","").replaceAll("\"", "");
	      System.out.println("Parameter passing to SELECT:"+alert_policy_name+"*"+account_name+"*"+srvr+"* "+application_name.trim()+"*"+"msg:"+msg+"*");
	      if(application_name.trim()==""){
	    	  sql = "select type,name, policy_name,account_name,issue_type,server,login,pwd,port,location from config where policy_name=? and account_name=? and server=? and upper(issue_type)=?";
	    	  stmt = conn.prepareStatement(sql);
		       stmt.setString(1,alert_policy_name);
		       stmt.setString(2,account_name);
		       stmt.setString(3,srvr);
		       stmt.setString(4,msg);
		       //stmt.setString(4,application_name);
	      }
	       else if(servers.trim()==""){
	    	  sql = "select type,name, policy_name,account_name,issue_type,server,login,pwd,port,location from config where policy_name=? and account_name=? and application_name=? and upper(issue_type)=?"; 
	    	  stmt = conn.prepareStatement(sql);
		       stmt.setString(1,alert_policy_name);
		       stmt.setString(2,account_name);
		       //stmt.setString(3,srvr);
		       stmt.setString(3,application_name);
		       stmt.setString(4,msg);
	       }
	       rs = stmt.executeQuery();
	      while(rs.next()){
	          //Retrieve by column name
	           config_type  = rs.getString("type");
	           config_name = rs.getString("name");
	           config_policy_name = rs.getString("policy_name");
	           config_account_name = rs.getString("account_name");
	           config_issue_type  = rs.getString("issue_type");
	           config_server  = rs.getString("server");
	           config_login  = rs.getString("login");
	           config_pwd  = rs.getString("pwd");
	           config_port  = rs.getString("port");
	           config_location  = rs.getString("location");

	          //Display values
	          System.out.print("config_type: " + config_type);
	          System.out.print(", config_name: " + config_name);
	          System.out.print(", config_policy_name: " + config_policy_name);
	          System.out.println(", config_account_name: " + config_account_name);
	       }
	      
	      
	      conn.close();
	      // Instantiatinh sshtest class
	      //ssh s = new ssh();
	      sshTest s = new sshTest();
	      
	      if(config_type.length()>0){
	    	  System.out.println("-------- Before Executing SSH connection...."+config_server+" "+config_login+" "+config_pwd+" "+config_location+"****");
	    	  //Calling SSH script to execute autohealing script
	    	  s.ssh(config_server,config_login,config_pwd,config_location);
	      }
	      else{
	    	  System.out.println("*************NO AUTOHEALING SCRIPT IN SYSTEM.. SEND EMAIL TO CONCERNED TEAM. This is from program readQueueInsertDB");
	      }
	      
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	      System.out.println("Inserting into MONGODB and collection errorDB: "+jsonnew);
	      Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("json");
			DBCollection collection2 = db.getCollection("errorDB");
           DBObject dbObject2 = (DBObject)JSON.parse(jsonnew);
           collection2.insert(dbObject2);
           System.out.println("Insertion to mongoDB collection errorDB done..");
	    }finally{
	        //finally block used to close resources
	        try{
	           if(stmt!=null)
	              conn.close();
	        }catch(SQLException se){
	        }// do nothing
	        try{
	           if(conn!=null)
	              conn.close();
	        }catch(SQLException se){
	           se.printStackTrace();
	        }//end finally try
	}
}
}

