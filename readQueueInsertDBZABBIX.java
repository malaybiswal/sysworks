
import java.sql.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
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


public class readQueueInsertDBZABBIX {
// URL of the JMS server
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
// default broker URL is : tcp://localhost:61616"

// Name of the queue we will receive messages from
	//public static String subject = "MALAY";
	
	//public static String url = "tcp://10.12.244.249:61616";
	public static String subject = "ZABBIX";
	public static int msgcount=0;

	public static void main(String[] args)   {
		//while(true){
		String mixedjson = "";
		HashMap hm = new HashMap();
		String nrinc = "";
		String snowinc = "";
		String json = "";
		String sysid= "";
		String snowurl="";
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
				
				//String[] parts = mixedjson.split("-----");//Five dashes - acts as separator
				 String[] parts = mixedjson.split("#####");
				 nrinc = parts[0];
				 snowinc = parts[1];
				 sysid = parts[2];
				 json = parts[3];
				 snowurl="https://devrackspace.service-now.com/nav_to.do?uri=%2Fincident.do%3Fsys_id%3D"+sysid+"%26sysparm_stack%3D%26sysparm_view%3D";
				 
					try{
						jsn = new JSONObject(json);
						jsn.put("snow", snowinc);
						jsn.put("nr", nrinc);
						jsn.put("sysid",sysid);
						jsonnew = jsn.toString();
						System.out.println("jsonnw:"+jsonnew);
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
							System.out.println("long_description:"+long_description);
							System.out.println("-----------------------------------------------------------");
							alert_url = obj.getString("alert_url");
							alert_policy_name = obj.getString("alert_policy_name");
							severity = obj.getString("severity");
							server_events = obj.getString("server_events");
							incident_id=alert_url.split("incidents");
							incnr = incident_id[1].substring(1);
							desc = short_description+" Server And Issue is: "+long_description;
							//application_name = obj.getString("application_name");
							System.out.println("long_description:"+long_description);
							System.out.println("-----------------------------------------------------------");
							 hm = token(long_description);//Parse long_description to get individual values such as HOST.HOST1
							
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
						//long_description = escape(long_description);
						System.out.println("LONGDESCRIPTION:"+long_description);
						System.out.println("insertDB("+sqlDate+","+snowurl+","+servers+","+account_name+","+message+","+short_description+","+long_description+","+alert_url+","+snowinc+","+nrinc+","+alert_policy_name+","+severity+","+server_events+","+application_name+","+jsonnew+");");

						insertDB(sqlDate,long_description,hm.get("event_id").toString(),hm.get("app").toString(),hm.get("issue").toString(),hm.get("event_status").toString(),hm.get("event_date").toString(),hm.get("event_time").toString(),hm.get("event_age").toString(),hm.get("event_ack_history").toString(),hm.get("event_ack_status").toString(),hm.get("host_host1").toString(),hm.get("host_name1").toString(),hm.get("host_dns1").toString(),hm.get("host_ip1").toString(),hm.get("host_conn1").toString(),hm.get("host_description1").toString(),hm.get("inventory_os1").toString(),hm.get("inventory_software_app_d1").toString(),hm.get("trigger_name").toString(),hm.get("trigger_name_orig").toString(),hm.get("trigger_description").toString(),hm.get("trigger_status").toString(),hm.get("trigger_severity").toString(),hm.get("trigger_expression").toString(),hm.get("trigger_hostgroup_name").toString(),hm.get("item_name1").toString(),hm.get("item_description1").toString(),hm.get("item_key1").toString(),hm.get("item_key_orig1").toString(),hm.get("item_value1").toString(),hm.get("action_name").toString(),hm.get("host_group").toString(),short_description,snowinc,snowurl);
						System.out.println("AFTER inserting into MySQL DB...");
					}catch(Exception e){
						System.out.println("Inside Catch Block of readQueueInsert...");
						e.printStackTrace();
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
	
	// This method will add escape character before #, ' etc..
	/*public static String escape(String s){
		String str="";
		for(int i=0;i<s.length();i++){
			if(s.charAt(i)=='#')
				str = str+"\\#";
			else if(s.charAt(i)=='\'')
				str = str+"''";
		}
		return str;
			
	}*/

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

	
	public static void insertDB(java.sql.Timestamp sqlDate,String long_description,String event_id, String app, String issue, String event_status, String event_date, String event_time, String event_age, String event_ack_history, String event_ack_status, String host_host1, String host_name1, String host_dns1, String host_ip1, String host_conn1, String host_description1, String inventory_os1, String inventory_software_app_d1, String trigger_name, String trigger_name_orig, String trigger_description, String trigger_status, String trigger_severity, String trigger_expression, String trigger_hostgroup_name, String item_name1, String item_description1, String item_key1, String item_key_orig1, String item_value1, String action_name, String host_group, String short_description, String snowinc, String snowurl){ 
		
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
	       query = " insert into transactionszabbix(created_at, event_id, app, issue, event_status,event_date,event_time,event_age,event_ack_history,event_ack_status,host_host1,host_name1,host_dns1,host_ip1,host_conn1,host_description1,inventory_os1,inventory_software_app_d1,trigger_name,trigger_name_orig,trigger_description,trigger_status,trigger_severity,trigger_expression,trigger_hostgroup_name,item_name1,item_description1,item_key1,item_key_orig1,item_value1,action_name,host_group,short_description,snowinc,snowurl)"
	        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?,?,?)";
	 
	      // create the mysql insert preparedstatement
	       preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setTimestamp (1, sqlDate);
	      preparedStmt.setString 	(2, event_id);
	      preparedStmt.setString   	(3, app);
	      preparedStmt.setString	(4, issue);
	      preparedStmt.setString    (5, event_status);
	      preparedStmt.setString    (6, event_date);
	      preparedStmt.setString    (7, event_time);
	      preparedStmt.setString    (8, event_age);
	      preparedStmt.setString    (9, event_ack_history);
	      preparedStmt.setString    (10, event_ack_status);
	      preparedStmt.setString    (11, host_host1);
	      preparedStmt.setString    (12, host_name1);
	      preparedStmt.setString    (13, host_dns1);
	      preparedStmt.setString 	(14, host_ip1);
	      preparedStmt.setString   	(15, host_conn1);
	      preparedStmt.setString	(16, host_description1);
	      preparedStmt.setString    (17, inventory_os1);
	      preparedStmt.setString    (18, inventory_software_app_d1);
	      preparedStmt.setString    (19, trigger_name);
	      preparedStmt.setString    (20, trigger_name_orig);
	      preparedStmt.setString    (21, trigger_description);
	      preparedStmt.setString    (22, trigger_status);
	      preparedStmt.setString    (23, trigger_severity);
	      preparedStmt.setString    (24, trigger_expression);
	      preparedStmt.setString    (25, trigger_hostgroup_name);
	      preparedStmt.setString    (26, item_name1);
	      preparedStmt.setString 	(27, item_description1);
	      preparedStmt.setString   	(28, item_key1);
	      preparedStmt.setString	(29, item_key_orig1);
	      preparedStmt.setString    (30, item_value1);
	      preparedStmt.setString    (31, action_name);
	      preparedStmt.setString    (32, host_group);
	      preparedStmt.setString    (33, short_description);
	      preparedStmt.setString    (34, snowinc);
	      preparedStmt.setString    (35, snowurl);
	      
	      
	      
	      // execute the preparedstatement
	      preparedStmt.execute();
	      
	      
	      
	      
	      
	      conn.close();
	      // Instantiatinh sshtest class
	      //ssh s = new ssh();
	     
	      
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	      //System.out.println("Inserting into MONGODB and collection errorDB: "+jsonnew);
	      Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("json");
			DBCollection collection2 = db.getCollection("errorDB");
           DBObject dbObject2 = (DBObject)JSON.parse(long_description);
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
	
	// This method aparses long_description and get strings such as EVENT.ID,HOST.HOST1
	public static HashMap token(String long_description){
		String event_id="";
		String event_tags="",app="",issue="";
		String event_status="";
		String event_date="";
		String event_time="";
		String event_age="";
		String event_ack_history="";
		String event_ack_status="";
		String host_host1="";
		String host_name1="";
		String host_dns1="";
		String host_ip1="";
		String host_conn1="";
		String host_description1="";
		String inventory_os1="";
		String inventory_software_app_d1="";
		String trigger_name="";
		String trigger_name_orig="";
		String trigger_description="";
		String trigger_status="";
		String trigger_severity="";
		String trigger_expression="";
		String trigger_hostgroup_name="";
		String item_name1="";
		String item_description1="";
		String item_key1="";
		String item_key_orig1="";
		String item_value1="";
		String action_name="";
		String host_group="";
		HashMap hm = new HashMap(); 
		System.out.println("LONG DESCRIPTION:"+long_description);
		String[] long_desc = long_description.split("----");
       // System.out.println(long_desc.length+" ***");
        for(int i=0;i<long_desc.length;i++){
        	System.out.println("$$$"+long_desc[i]+"$$$");
        	//System.out.println(long_desc[i]);
        	 if(long_desc[i].contains("TRIGGER.NAME")){
        		String[] triggername = long_desc[i].split("=");
        		System.out.println(">>>>"+long_desc[i]);
        		
        		
        		if(triggername.length>=2 && triggername[0].equals("TRIGGER.NAME")){
        				trigger_name=triggername[1];
        				hm.put("trigger_name", trigger_name);
        				System.out.println("---------"+trigger_name);
         		}	
        		else if(triggername.length>=2 && triggername[0].equals("TRIGGER.NAME.ORIG")){
        				trigger_name_orig=triggername[1];
        				hm.put("trigger_name_orig", trigger_name_orig);
        				System.out.println("---------"+trigger_name_orig);
        		}
        		
        		
        	}
        	
        	else if(long_desc[i].contains("EVENT.ID")){
        		String[] eventid = long_desc[i].split("=");
        		event_id=eventid[1];
        		hm.put("event_id", event_id);
        	}
        		
        	else if(long_desc[i].contains("EVENT.TAGS")){
        		String[] s=long_desc[i].split(",");
        		String[] appname=s[0].split("=");
        		app=appname[1].trim();
        		hm.put("app", app);
        		String[] iss = s[1].split(":");
        		issue=iss[1].trim();
        		hm.put("issue", issue);
        	}
        	else if(long_desc[i].contains("EVENT.STATUS")){
        		String[] eventstatus = long_desc[i].split("=");
        		event_status=eventstatus[1];
        		hm.put("event_status", event_status);
        	}
        	else if(long_desc[i].contains("EVENT.ID")){
        		String[] eventid = long_desc[i].split("=");
        		event_id=eventid[1];
        		hm.put("event_id", event_id);
        	}
        	else if(long_desc[i].contains("EVENT.DATE")){
        		String[] eventdt = long_desc[i].split("=");
        		event_date=eventdt[1];
        		hm.put("event_date", event_date);
        	}
        	else if(long_desc[i].contains("EVENT.TIME")){
        		String[] eventtm = long_desc[i].split("=");
        		event_time=eventtm[1];
        		hm.put("event_time", event_time);
        	}
        	else if(long_desc[i].contains("EVENT.AGE")){
        		String[] eventage = long_desc[i].split("=");
        		event_age=eventage[1];
        		hm.put("event_age", event_age);
        	}
        	else if(long_desc[i].contains("EVENT.ACK.HISTORY")){
        		
        		String[] eventackhistory = long_desc[i].split("=");
        		if(eventackhistory.length>1){
        			event_ack_history=eventackhistory[1];
        		}
        		hm.put("event_ack_history", event_ack_history);
        	}
        	else if(long_desc[i].contains("EVENT.ACK.STATUS")){
        		String[] eventackstatus = long_desc[i].split("=");
        		event_ack_status=eventackstatus[1];
        		hm.put("event_ack_status", event_ack_status);
        	}
        	else if(long_desc[i].contains("HOST.HOST1")){
        		String[] host = long_desc[i].split("=");
        		host_host1=host[1];
        		hm.put("host_host1", host_host1);
        	}
        	else if(long_desc[i].contains("HOST.NAME1")){
        		String[] hostname = long_desc[i].split("=");
        		host_name1=hostname[1];
        		hm.put("host_name1", host_name1);
        	}
        	else if(long_desc[i].contains("HOST.DNS1")){
        		String[] hostdns = long_desc[i].split("=");
        		host_dns1=hostdns[1];
        		hm.put("host_dns1", host_dns1);
        	}
        	else if(long_desc[i].contains("HOST.IP1")){
        		String[] hostip1 = long_desc[i].split("=");
        		host_ip1=hostip1[1];
        		hm.put("host_ip1", host_ip1);
        	}
        	else if(long_desc[i].contains("HOST.CONN1")){
        		String[] hostconn1 = long_desc[i].split("=");
        		host_conn1=hostconn1[1];
        		hm.put("host_conn1", host_conn1);
        	}
        	else if(long_desc[i].contains("HOST.DESCRIPTION1")){
        		//String[] hostdescription1 = long_desc[i].split("=");
        		//host_description1=hostdescription1[1];
        		if(long_desc[i].length()>18){
        			String[] hostdescription1 = long_desc[i].split("=");
        			host_description1=hostdescription1[1];
        		}
        		else{
        			host_description1="N/A";
        		}
			hm.put("host_description1", host_description1);
        	}
        	else if(long_desc[i].contains("INVENTORY.OS1")){
        		String[] inventoryos1 = long_desc[i].split("=");
        		inventory_os1=inventoryos1[1];
        		hm.put("inventory_os1", inventory_os1);
        	}
        	else if(long_desc[i].contains("INVENTORY.SOFTWARE.APP.D1")){
        		String[] inventorysoftwareappd1 = long_desc[i].split("=");
        		inventory_software_app_d1=inventorysoftwareappd1[1];
        		hm.put("inventory_software_app_d1", inventory_software_app_d1);
        	}
        	
        	/*else if(long_desc[i].contains("TRIGGER.NAME.ORIG")){
        		String[] triggernameorig = long_desc[i].split("=");
        		trigger_name_orig=triggernameorig[1];
        		System.out.println("DEBUG:"+trigger_name_orig);
        		hm.put("trigger_name_orig", trigger_name_orig);
        	}*/
        	else if(long_desc[i].contains("TRIGGER.DESCRIPTION")){
        		String[] triggerdescription = long_desc[i].split("=");
        		trigger_description=triggerdescription[1];
        		hm.put("trigger_description", trigger_description);
        	}
        	else if(long_desc[i].contains("TRIGGER.STATUS")){
        		String[] triggerstatus = long_desc[i].split("=");
        		trigger_status=triggerstatus[1];
        		hm.put("trigger_status", trigger_status);
        	}
        	else if(long_desc[i].contains("TRIGGER.SEVERITY")){
        		String[] triggerseverity = long_desc[i].split("=");
        		trigger_severity=triggerseverity[1];
        		hm.put("trigger_severity", trigger_severity);
        	}
        	else if(long_desc[i].contains("TRIGGER.EXPRESSION")){
        		String[] triggerexpression = long_desc[i].split("=");
        		trigger_expression=triggerexpression[1];
        		hm.put("trigger_expression", trigger_expression);
        	}
        	else if(long_desc[i].contains("TRIGGER.HOSTGROUP.NAME")){
        		String[] triggerhostgroupname = long_desc[i].split("=");
        		trigger_hostgroup_name=triggerhostgroupname[1];
        		hm.put("trigger_hostgroup_name", trigger_hostgroup_name);
        	}
        	else if(long_desc[i].contains("ITEM.NAME1")){
        		String[] itemname1 = long_desc[i].split("=");
        		item_name1=itemname1[1];
        		hm.put("item_name1", item_name1);
        	}
        	else if(long_desc[i].contains("ITEM.DESCRIPTION1")){
        		String[] itemdescription1 = long_desc[i].split("=");
        		item_description1=itemdescription1[1];
        		hm.put("item_description1", item_description1);
        	}
        	else if(long_desc[i].contains("ITEM.KEY1")){
        		String[] itemkey1 = long_desc[i].split("=");
        		if(itemkey1.length>2){
        			for(int j=0;j<itemkey1.length;j++){
        				if(j>=1){
        					item_key1=item_key1+itemkey1[j]+"=";
        				}
        				else{
        					item_key1=item_key1+itemkey1[j];
        				}
        				
        			}
        		}
        		else{
        			item_key1=itemkey1[1];
        		}
        		hm.put("item_key1", item_key1);
        		
        	}
        	else if(long_desc[i].contains("ITEM.KEY.ORIG1")){
        		String[] itemkeyorig1 = long_desc[i].split("=");
        		item_key_orig1=itemkeyorig1[1];
        		hm.put("item_key_orig1", item_key_orig1);
        	}
        	else if(long_desc[i].contains("ITEM.VALUE1")){
        		String[] itemvalue1 = long_desc[i].split("=");
        		item_value1=itemvalue1[1];
        		hm.put("item_value1", item_value1);
        	}
        	else if(long_desc[i].contains("ACTION.NAME")){
        		String[] actionname = long_desc[i].split("=");
        		action_name=actionname[1];
        		hm.put("action_name", action_name);
        	}
        	else if(long_desc[i].contains("HOST.GROUP")){
        		String[] hostgroup = long_desc[i].split("=");
        		host_group=hostgroup[1];
        		hm.put("host_group", host_group);
        	}
        	
        	
        	
        }
        return hm;
	}
	
}



