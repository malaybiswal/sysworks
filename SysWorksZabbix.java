import static spark.Spark.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.json.JSONArray;
import org.json.JSONObject;

import sun.misc.BASE64Encoder;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/** Developed by Malay Biswal on 01-08-2016
 * This program reads webhook call on mentioned port#4567 , parses message. Insert record into mongodb json DB. create SNOW ticket. Put message in activemq as well.
 */
public class SysWorksZabbix {
	static String errorFileLocation="",mongohost="",mongoport="",mongoDB="",collection1="",serializeFileLocation="",collection_2="",SNOWUser="",SNOWpwd="",SNOWURLget="",SNOWURLupdate="",
			SNOWURL="",ActiveMQURL="",ACTIVEMQQUEUEName="",MySQLjdbc="",MySQLUser="",MySQLpwd="",collection3="",UpdateSNOW="";
	static String body="{\"created_at\":\"2015-08-15T20:02:16Z\",\"servers\":[\"a-brmmon01.ord1.corp.rackspace.com\"],\"account_name\":\"Foundation Utility Grid\",\"alert_policy_name\":\"dashboard(a-brmmon)\",\"severity\":\"critical\",\"message\":\"CPU > 80%\",\"short_description\":\"New alert on a-brmmon01.ord1.corp.rackspace.com\",\"long_description\":\"Alert opened: CPU > 80%\",\"alert_url\":\"https://rpm.newrelic.com/accounts/136850/incidents/16917992\",\"server_events\":[{\"server\":\"a-brmmon01.ord1.corp.rackspace.com\",\"created_at\":\"2015-08-15T20:02:16Z\",\"message\":\"CPU > 60%\"},{\"server\":\"a-brmmon01.ord1.corp.rackspace.com\",\"created_at\":\"2015-08-15T20:02:16Z\",\"message\":\"CPU > 80%\"}]}";
	static DateFormat dateFormat;// = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	static Date date;// = new Date();
	//System.out.println(dateFormat.format(date));
	static String alert;
	static String sysid=null;
	static HashMap ghm = null;
	public static void main(String[] args) {
try {
			
			FileInputStream fstream = new FileInputStream("/root/malay/javaProject/myZabbix.conf");
			DataInputStream in = new DataInputStream(fstream);
	          BufferedReader br = new BufferedReader(new InputStreamReader(in));
	          String str;
	          while((str = br.readLine()) != null){
	        	  String[] tokens = str.split(" ");
	        	  if(tokens[0].equals("errorFileLocation"))
	        		  errorFileLocation = tokens[1];
	        	  else if(tokens[0].equals("mongohost"))
	        		  mongohost = tokens[1];
	        	  else if (tokens[0].equals("mongoport"))
	        		  mongoport=tokens[1];
	        	  else if (tokens[0].equals("mongoDB"))
	        		  mongoDB = tokens[1];
	        	  else if (tokens[0].equals("collection1"))
	        		  collection1 = tokens[1];
	        	  else if (tokens[0].equals("serializeFileLocation"))
	        		  serializeFileLocation = tokens[1];
	        	  else if (tokens[0].equals("collection_2"))
	        		  collection_2 = tokens[1];
	        	  else if (tokens[0].equals("SNOWUser"))
	        		  SNOWUser = tokens[1];
	        	  else if (tokens[0].equals("SNOWpwd"))
	        		  SNOWpwd = tokens[1];
	        	  else if (tokens[0].equals("SNOWURL"))
	        		  SNOWURL = tokens[1];
	        	  else if (tokens[0].equals("SNOWURLupdate"))
	        		  SNOWURLupdate = tokens[1];
	        	  else if (tokens[0].equals("SNOWURLget"))
	        		  SNOWURLget = tokens[1];
	        	  else if (tokens[0].equals("ActiveMQURL"))
	        		  ActiveMQURL = tokens[1];
	        	  else if (tokens[0].equals("ACTIVEMQQUEUEName"))
	        		  ACTIVEMQQUEUEName = tokens[1];
	        	  else if (tokens[0].equals("MySQLjdbc"))
	        		  MySQLjdbc = tokens[1];
	        	  else if (tokens[0].equals("MySQLUser"))
	        		  MySQLUser = tokens[1];
	        	  else if (tokens[0].equals("MySQLpwd"))
	        		  MySQLpwd = tokens[1];
	        	  else if (tokens[0].equals("collection3"))
	        		  collection3 = tokens[1];
	        	  else if (tokens[0].equals("UpdateSNOW"))
	        		  UpdateSNOW = tokens[1];
	        	System.out.println("***UpdateSNOW:"+UpdateSNOW);
	          }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*@SuppressWarnings("deprecation")
		Mongo mongo = new Mongo("localhost", 27017);
		DB db = mongo.getDB("json");
		DBCollection collection = db.getCollection("trial");*/
    	//String body="";
          port(443); //<- Uncomment this if you want spark to listen on a port different than 4567
          int maxThreads = 200;
          int minThreads = 2;
          int timeOutMillis = 50000;
          threadPool(maxThreads, minThreads, timeOutMillis);



        get("/hello", (request, response) -> "Hello World!");

        post("/hello", (request, response) ->
            "Hello World: " + request.body()+ request.session()+request.ip()+request.url()
        );
        
        post("/xyz",( request,  response) ->{

                 body =  request.body() ;
                 System.out.println("BODY:"+body);
                 dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                 date = new Date();
                 System.out.println(dateFormat.format(date));
		 String snowinc="";                
                 HashMap hm2 = new HashMap();
                 HashMap verifyMap = new HashMap();
		 String key = null;
      			String value = null;
           	 String alert_policy_name="", application_name="", servers="", message="", short_description="", long_description="", created_at="", alert_url="", incnr="";

                 try{
                	 String bodyconvert = java.net.URLDecoder.decode(body, "UTF-8");
                	 System.out.println("*******"+bodyconvert);
                	 //String alert_policy_name="", application_name="", servers="", message="", short_description="", long_description="", created_at="", alert_url="", incnr="";
           			String incident_id[]={};
                	 String[] alerts = bodyconvert.split("alert=");
                	 if(alerts.length>=1){
                	 
                		 alert = alerts[1].trim();
                	 
                	 System.out.println("###############"+alert);
                	 
                     JSONObject obj = new JSONObject(alert);
                     if(obj.has("application_name")){
                    	 alert_policy_name = obj.getString("alert_policy_name");
                          application_name = obj.getString("application_name");
                          //servers = obj.getString("servers");
                          message = obj.getString("message");
                          short_description = obj.getString("short_description");
                          long_description = obj.getString("long_description");
                          created_at = obj.getString("created_at");
                          alert_url = obj.getString("alert_url");
                          incident_id=alert_url.split("incidents");//Getting Zabbix event_id
                          incnr = incident_id[1].substring(1);
                     }
                     else if(obj.has("servers")){
                    	 alert_policy_name = obj.getString("alert_policy_name");
                          //application_name = obj.getString("application_name");
                          servers = obj.getString("servers");
                          message = obj.getString("message");
                          short_description = obj.getString("short_description");
                          long_description = obj.getString("long_description");
                          created_at = obj.getString("created_at");
                          alert_url = obj.getString("alert_url");
                          incident_id=alert_url.split("incidents");
                          incnr = incident_id[1].substring(1);
                     }
                     /*String alert_policy_name = obj.getString("alert_policy_name");
                     String application_name = obj.getString("application_name");
                     String servers = obj.getString("servers");
                     String message = obj.getString("message");
                     String short_description = obj.getString("short_description");
                     String long_description = obj.getString("long_description");
                     String created_at = obj.getString("created_at");
                     String alert_url = obj.getString("alert_url");
                     String incident_id[]=alert_url.split("incidents");
                     String incnr = incident_id[1].substring(1);*/
		     String desc="";
                     if(long_description.contains("----")){
			String[] ldesc = format(long_description);
			String extra = "";
			for (int i=1;i<ldesc.length;i++){
				extra = extra+ldesc[i]+"\\n";
			}	
			desc = short_description+" Server And Issue is: "+ ldesc[0]+" AND Issue occured at: "+created_at+" UTC"+" EXTRA MESSAGE: "+extra; 
		     }
		     else{
		     	desc = short_description+" Server And Issue is: "+long_description+" AND Issue occured at: "+created_at+" UTC";
		     }
                     System.out.println("alert_policy_name:"+alert_policy_name+"servers:"+servers +"application_name:"+application_name +"message: " + message+"short_description: "+short_description+"Incident id: "+incident_id[1].substring(1));
                     obj.put("My_created_t", dateFormat.format(date));
                     String alertAppend = obj.toString();
                     System.out.println("alertAppend: "+alertAppend);
                     //DBObject dbObject = (DBObject)JSON.parse(alert);
                     DBObject dbObject = (DBObject)JSON.parse(alertAppend);
                     String nr = incident_id[1].substring(1);
                     String loc = errorFileLocation+incnr+".txt";
                     File file = new File(loc);
                     if (!file.exists()) {
         				file.createNewFile();
         			}
                     FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
         			BufferedWriter bw = new BufferedWriter(fw);
         			
                     try{
                    	 @SuppressWarnings("deprecation")
                 		 Mongo mongo = new Mongo(mongohost, Integer.parseInt(mongoport));
                 		 DB db = mongo.getDB(mongoDB);
                 		DBCollection collection = db.getCollection(collection1);
                    	 collection.insert(dbObject);
                     }catch(Exception e){
                    	 System.out.println("INSIDE CATCH BLOCK MAY BE MONGODB DOWN & alertappend: "+alertAppend);
                    	 bw.write(alertAppend);
              			bw.close();
                     }
                     String []tm = created_at.split("T");
            		 String tim = tm[1].substring(0,8);
            		 int index=0; String issue="", issue1="";
            		 if(servers.length()>0){
            			 index = message.indexOf(">");
            			 issue = message.substring(0,index);
            			 issue1 = issue.replaceAll("\\s+","");
            		 }
            		 else if(application_name.length()>0){
            			 boolean isitapdex = message.contains("<");
            			 if(isitapdex){
            				 index = message.indexOf("<");
            				 issue = message.substring(0,index);
            				 issue1 = issue.replaceAll("\\s+","");
            			 }
            			 else{
            				 index = message.indexOf(">");
            				 issue = message.substring(0,index);
            				 issue1 = issue.replaceAll("\\s+","");
            			 }
            		 }
            		//New Ticket 
                    	 if(short_description.substring(0,3).equals("New")){
                    		 System.out.println("message:"+message+" alert_policy_name:"+alert_policy_name+" nr:"+nr+" desc:"+desc);
                    		
                     		 verifyMap = token(alert); // Get required values to validate if there is existing ticket for same issue
                     		 
                         	 snowinc = existTicket(verifyMap.get("item_name1").toString(),verifyMap.get("app").toString(),verifyMap.get("issue").toString(),verifyMap.get("host_host1").toString(),verifyMap.get("host_ip1").toString()); 
                     		 
                     		 
                     		 //snowinc = existTicket(verifyMap.get("item_name1").toString(),verifyMap.get("app").toString(),verifyMap.get("issue").toString(),verifyMap.get("host_host1").toString(),verifyMap.get("host_ip1").toString());
                     		 System.out.println("---------alertpolicyname "+alert_policy_name+" issue "+issue+" and time "+tim);
                     		 System.out.println("****verifyMap SIZE:"+verifyMap.size());
                     		 int ex = verifyExclusion(alert_policy_name, issue, tim);
                     		 if((ex==0) && (snowinc.length()<=0)){
                     			hm2 = createTicket(alert,message,alert_policy_name,nr,desc,created_at);//nr variable stores incident number from NewRelic
                     			//hm2 has both NR INCIDENT# and SNOW INCIDENT#
                       		 
                     			Iterator i = hm2.keySet().iterator();
                    			String incsr=null;
                    			serialZabbix s = new serialZabbix();
                    			String queuetext=null;
                    			String sysid="";
                    			String eventid ="",snowINC="";
                       		 	while(i.hasNext())
                    			{	
                    			     key = i.next().toString();  
                    			     value = (String) hm2.get(key);
                     			    System.out.println(key + " --- " + value);// value is incident number for SNOW

                    			     if(key.equals("sys_id")){// This is storing sys_id to get the URL of SNOW.
                    			    	 sysid= value;
                    			     }
                    			    else{
                    			    	s.incnr = key ;
                         			    s.incsn = value;
                         			     eventid = key;
                         			     snowINC= value;
                    			    }
                    			   
                    			 //queuetext = key+"-----"+value+"-----"+alert;
                    			   queuetext = eventid+"#####"+snowINC+"#####"+sysid+"#####"+alert; //These 5 # will be used as separator in json passed to activemq. alert string has most of messages
                    			}
                       		 
                       		 	if(key.equals("0") && value.equals("0")){
                       		 		System.out.println("NOT SERIALIZING DATA AS KEY IS:"+key+" AND VALUE IS: "+value);
                       		 	}
                       		 	else{
                       		 		writeQueue(queuetext);
                       		 		String location = serializeFileLocation+eventid+issue1.replace("/","");// Key is incident# from NewRelic and issue1 is first element from message after spliting with > without white spaces
                       		 		System.out.println("SERIALIZED LOCATION: "+location);
                       		 		FileOutputStream fileOut = new FileOutputStream(location);
                       		 		ObjectOutputStream out = new ObjectOutputStream(fileOut);
                       		 		out.writeObject(s);
                       		 		out.close();
                       		 		fileOut.close();
                       		 		System.out.println("SERIALIZED DATA SAVED IN LOCATION: "+location);
                       		 	}
                       		 
                     		 }
                     		 else{
                     			 System.out.println("*****************EXCLUSION FIELD APPLIED as "+ex+" number of records for alertpolicyname "+alert_policy_name+" issue "+issue+" and time "+tim+"*******EXISTING TICKET WITH SAME ISSUE"+snowinc);
                     		     //insertExclusion(created_at,alert_policy_name,servers,message,short_description,long_description,alert_url,incnr);
                     			 
                     			 updateTicketForNew( snowinc, alert,message,alert_policy_name,nr,desc,created_at);
                     		 }
                    		 
                    	 }
                    	 else if((short_description.substring(0,5).equals("Ended")) ){
                    		 String[] alert1 = alert.split("----");
                    		 String triggerSeverity="",eventid="";	    
                    		 for(int j=0;j<alert1.length;j++){
                    		 	if(alert1[j].toUpperCase().contains("TRIGGER.SEVERITY" )){
                    		 		String[] s=alert1[j].split("=");
                    		 		if(s.length>=2){
                    		 			triggerSeverity=s[1];
                    		 		}
                    		 				
                    		 	}
                    		 	else if(alert1[j].toUpperCase().contains("EVENT.ID" )){
                    		 		String[] s=alert1[j].split("=");
                    		 		if(s.length>=2){
                    		 			eventid=s[1];
                    		 		}
                    		 	}
                    		 }

                    		 if(triggerSeverity.equals("Warning")){// This will not update ticket if severity is warning. This updates transactionszabbixwarning table with closed_t
                    		 			System.out.println("NOT UPDATING TICKET SINCE it's a Warning");
                    		 			java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
                    		 			System.out.println(sqlDate);
                    		 			System.out.println("Calling updateWrning()");
                    		 			updateWarningTable(sqlDate,eventid);		    	
                    		 }
                    		 else{
                    			 System.out.println("INSIDE ELSE IF ENDED UpdateSNOW: "+UpdateSNOW);
                        		 serialZabbix s = null;
                        		 String locationpick = serializeFileLocation+incnr+issue1.replace("/","");
                        		 FileInputStream fileIn = new FileInputStream(locationpick);
                        		 ObjectInputStream in = new ObjectInputStream(fileIn);
                        		 s = (serialZabbix) in.readObject();
                        		 in.close();
                                 fileIn.close();
                                 String incsr = s.incsn;
                                 //if there are multiple issues with same incnr then only one will be updated. Need to add message and incsr to generate file, so 
                                 //File name wil be different for different issues for same NewRElic incident
                        		 
                        		 String sys_id=getTicket(incsr);
                        		 verifyMap = token(alert);
                        		 System.out.println("****INSIDE METHOD SYS+ID GOT:"+sys_id);
                        		 System.out.println("****verifyMap SIZE:"+verifyMap.size());
                        		 //int index = message.indexOf(">");
                        		// String issue = message.substring(0,index);
                        		 System.out.println("updateClosedt passing values incsr:"+incsr+" issue:"+verifyMap.get("item_name1").toString());
                        		 updateClosedt(incsr,verifyMap.get("item_name1").toString());
                        		 if(UpdateSNOW.equals("Yes")){
                        			 //updateTicket(sys_id);
                        			 updateTicket(incsr,alert);
                        		 }
                    		 }
                    		 
                    		 
                    		 
                    	 }
                    	 else if((short_description.substring(0,3).equals("ACK")) ){
                    		 System.out.println("INSIDE ELSE IF ACK UpdateSNOW: "+UpdateSNOW);
                    		 serialZabbix s = null;
                    		 String locationpick = serializeFileLocation+incnr+issue1.replace("/","");
                    		 FileInputStream fileIn = new FileInputStream(locationpick);
                    		 ObjectInputStream in = new ObjectInputStream(fileIn);
                    		 s = (serialZabbix) in.readObject();
                    		 in.close();
                             fileIn.close();
                             String incsr = s.incsn;
                             //if there are multiple issues with same incnr then only one will be updated. Need to add message and incsr to generate file, so 
                             //File name will be different for different issues for same NewRElic incident
                    		 
                    		 String sys_id=getTicket(incsr);
                    		 System.out.println("****INSIDE METHOD SYS+ID GOT:"+sys_id);
                    		 
                    		 //int index = message.indexOf(">");
                    		// String issue = message.substring(0,index);
                    		 //System.out.println("updateClosedt passing values incsr:"+incsr+" issue:"+message);
                    		 //updateClosedt(incsr,message);
                    		 if(UpdateSNOW.equals("Yes")){
                    			 //updateTicket(sys_id);
                    			 updateACKTicket(incsr,alert);
                    		 }
                    		 	//CODE needs to be written here for ACK Alert
                    	 }
                     }
                	 else{
                		 
                		 System.out.println("****Not a Valid ALert: "+alert);
                	 }
                     
                     
                    
                 }catch(Exception e){e.printStackTrace();
                 @SuppressWarnings("deprecation")
         		Mongo mongo = new Mongo(mongohost, Integer.parseInt(mongoport));
         		DB db = mongo.getDB(mongoDB);
         		
                     DBCollection collection2 = db.getCollection(collection_2);
                     DBObject dbObject2 = (DBObject)JSON.parse(alert);
                     collection2.insert(dbObject2);
                     }
                
                return body;
                 
        }   
        
        );
        
        

        get("/private", (request, response) -> {
            response.status(401);
            return "Go Away!!!";
        });
        

        get("/users/:name", (request, response) -> "Selected user: " + request.params(":name"));

        get("/news/:section", (request, response) -> {
            response.type("text/xml");
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + request.params("section") + "</news>";
        });

        get("/protected", (request, response) -> {
            halt(403, "I don't think so!!!");
            return null;
        });

        get("/redirect", (request, response) -> {
            response.redirect("/news/world");
            return null;
        });

        get("/", (request, response) -> "root");

    }
	//Create Ticket Method
	public static HashMap createTicket(String alert, String message, String alert_policy_name, String incnr,String desc,String created_at){
		//incnr is Zabbix event_id
		String[] alert1 = alert.split("----");
		String inc=null,triggerName="",triggerSeverity="",url="",description="";
		String eventid="",timestamp="",age="";
		String rawTriggerName="",expression="";
		String desc1="",tags="",alertGroup="";
		//String inv="",itemName="",itemValue="",itemKeyOrig1="",itemKey1="",itemDescription1=""hostname="",hostname1="",ip1="",conn1="",os1="",description1="";
		ArrayList<String> inv = new ArrayList<String>();
		ArrayList<String> itemName= new ArrayList<String>();
		ArrayList<String> itemValue= new ArrayList<String>();
		ArrayList<String> itemKeyOrig1= new ArrayList<String>();
		ArrayList<String> itemKey1= new ArrayList<String>();
		ArrayList<String> itemDescription1= new ArrayList<String>();
		ArrayList<String> hostname= new ArrayList<String>();
		ArrayList<String> hostname1= new ArrayList<String>();
		ArrayList<String> ip1= new ArrayList<String>();
		ArrayList<String> conn1= new ArrayList<String>();
		ArrayList<String> os1= new ArrayList<String>();
		ArrayList<String> description1= new ArrayList<String>();
		
		HashMap hmcreate = new HashMap();
		HashMap ticketdetails = new HashMap();
		
		for(int j=0;j<alert1.length;j++){
			if(alert1[j].toUpperCase().contains("TRIGGER.NAME" )){
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$"+alert1[j]);
                		String[] s=alert1[j].split("=");
                		/*if(s.length>=2){
                			triggerName=s[1];
                		}*/
				
				if(s.length>=2 && s[0].equals("TRIGGER.NAME")){
                 			triggerName=s[1];
					System.out.println("$$$$$$$$$$$$$$$$$$TRIGGER.NAME:"+triggerName);
                 		}	
 				else if(s.length>=2 && s[0].equals("TRIGGER.NAME.ORIG")){
 					rawTriggerName=s[1];
					System.out.println("$$$$$$$$$$$$$$$$$$TRIGGER.NAME.ORIGINAL:"+rawTriggerName);
 				}
                
			}   
			else if(alert1[j].toUpperCase().contains("TRIGGER.SEVERITY" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					triggerSeverity=s[1];
				}
				
			}
			/*else if(alert1[j].toUpperCase().contains("TRIGGER.URL" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					url=s[1];
				}
				
			}*/
			else if(alert1[j].toUpperCase().contains("INVENTORY.SOFTWARE.APP.D" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					//inv=s[1];
					inv.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.TAGS" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					tags=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("TRIGGER.DESCRIPTION" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					description=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.ID" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					eventid=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.ID" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					eventid=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.AGE" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					age=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("ITEM.NAME" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					itemName.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("ITEM.VALUE" )){
				String[] s=alert1[j].split("=");String iValue="";
				if(s.length>=2){
					String itemValue1=s[1];
					String[] s1 = itemValue1.split(",");
					//itemValue=s1[0].substring(0,1);
					//itemValue=itemValue.substring(0,1);
					iValue=s1[0].substring(0,1);
					iValue=iValue.substring(0,1);
					itemValue.add(iValue);
				}
				System.out.println("\n>>>>>>>>>>>>>>>>"+alert1[j]+"<<<<<<<<<<<<<<<<<<<");
				System.out.println("\n>>>>>>>>>>>>>>>>"+itemValue+"<<<<<<<<<<<<<<<<<<<");
			}
			else if(alert1[j].toUpperCase().contains("ITEM.KEY.ORIG" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					itemKeyOrig1.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("ITEM.KEY" )){
				String[] s=alert1[j].split("=");
				int len = s.length; String iKey1="";
				if(s.length>=2){
					for(int i=1;i<len;i++){
						if(i<len-1)
							iKey1=iKey1+s[i]+"=";
						else
							iKey1=iKey1+s[i];
					}
					itemKey1.add(iKey1);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("ITEM.DESCRIPTION" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					itemDescription1.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("HOST.HOST" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					hostname.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("HOST.NAME" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					hostname1.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("HOST.IP" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					ip1.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("HOST.CONN" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					conn1.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("INVENTORY.OS" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					os1.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("HOST.DESCRIPTION" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					description1.add(s[1]);
				}
				
			}
			/*else if(alert1[j].toUpperCase().contains("TRIGGER.NAME.ORIG" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					rawTriggerName=s[1];
				}
				
			}*/
			else if(alert1[j].toUpperCase().contains("TRIGGER.EXPRESSION" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					expression=s[1];
				}
				
			}
			
			else if(alert1[j].toUpperCase().contains("HOST.GROUP" )){// This will parse the Alert Group such as PostgreSQL
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					alertGroup=s[1].substring(0,s[1].indexOf('"'));
				}
				
			}
			
		}
		System.out.println("\n incnr:"+incnr+" trigger_name:"+triggerName+" Trigger Severity"+triggerSeverity);
		
		if(triggerSeverity.equals("Warning")){// This will not create ticket if severity is warning
			System.out.println("NOT CREATING TICKET SINCE it's a Warning");
			int key=0,value=0;Integer i=0,j=0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			Date date=null;
            try{
                    date = (Date) sdf.parse(created_at);
               }catch(Exception e){e.printStackTrace();}
			java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
			System.out.println(sqlDate);
			System.out.println("--------------------------ALERT:"+alert);
			HashMap hmAlert = null;
			try{
				JSONObject obj = new JSONObject(alert);
				String long_description = obj.getString("long_description");
				 hmAlert = tokenAlert(long_description);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			insertDB(sqlDate,hmAlert.get("event_id").toString(),hmAlert.get("app").toString(),hmAlert.get("issue").toString(),hmAlert.get("event_status").toString(),hmAlert.get("event_date").toString(),hmAlert.get("event_time").toString(),hmAlert.get("event_age").toString(),hmAlert.get("event_ack_history").toString(),hmAlert.get("event_ack_status").toString(),hmAlert.get("host_host1").toString(),hmAlert.get("host_name1").toString(),hmAlert.get("host_dns1").toString(),hmAlert.get("host_ip1").toString(),hmAlert.get("host_conn1").toString(),hmAlert.get("host_description1").toString(),hmAlert.get("inventory_os1").toString(),hmAlert.get("inventory_software_app_d1").toString(),hmAlert.get("trigger_name").toString(),hmAlert.get("trigger_name_orig").toString(),hmAlert.get("trigger_description").toString(),hmAlert.get("trigger_status").toString(),hmAlert.get("trigger_severity").toString(),hmAlert.get("trigger_expression").toString(),hmAlert.get("trigger_hostgroup_name").toString(),hmAlert.get("item_name1").toString(),hmAlert.get("item_description1").toString(),hmAlert.get("item_key1").toString(),hmAlert.get("item_key_orig1").toString(),hmAlert.get("item_value1").toString(),hmAlert.get("action_name").toString(),hmAlert.get("host_group").toString());		    
			hmcreate.put("0", "0");
		    return hmcreate;
		}
		
		else{// This is for Ticket Creation if it's not Warning
			//desc1 = "#######################"+"\\n";
			desc1=desc1+boldUnder("Zabbix Alert Detail")+"\\n";
			desc1=desc1+"\\n"+boldUnderSmall("Trigger")+"\\n"+"\\n";
			System.out.println("\n"+"1.=========--------------------------**************************"+desc1);
			desc1=desc1+indentfirst("Trigger Name")+triggerName+"<br>";
			System.out.println("\n"+"2.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Trigger Severity")+triggerSeverity+"<br>";
			System.out.println("\n"+"3."+desc);

			//desc1=desc1+"\\n"+"Trigger URL........: "+url;
			//System.out.println("\n"+"4.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Trigger Description")+description+"</pre>[/code]"+"\\n"+"\\n"+boldUnderSmall("Metric Causing the Alert");
			System.out.println("\n"+"5.=========--------------------------**************************"+desc1);

			
			desc1=desc1+"\\n"+indentfirst("Zabbix Event ID")+eventid+"<br>";
			System.out.println("\n"+"6.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Event Start Time")+created_at+"<br>";
			System.out.println("\n"+"7.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Event Age")+age+"</pre>[/code]"+"\\n"+"\\n"+boldUnderSmall("Metric Causing the Alert")+"\\n";
			System.out.println("\n"+"8.=========--------------------------**************************"+desc1);

			int itemNameSize = itemName.size();
			for(int i=0;i<itemNameSize;i++){
				desc1=desc1+"\\n"+indentfirst("Metric Name"+(i+1))+""+itemName.get(i)+"<br>";
			}
			
			//desc1=desc1+"\\n"+indent("Metric Name:")+itemName;
			System.out.println("\n"+"9.=========--------------------------**************************"+desc1);
			int itemValueSize=itemValue.size();
			for(int i=0;i<itemValueSize;i++){
				desc1=desc1+"\\n"+indent("Metric Value")+itemValue.get(i)+"<br>";
			}
			
			System.out.println("\n"+"10.=========--------------------------**************************"+desc1);
			int itemKeyOrig1Size = itemKeyOrig1.size();
			for(int i=0;i<itemKeyOrig1Size;i++){
				desc1=desc1+"\\n"+indent("Raw Metric Key")+itemKeyOrig1.get(i)+"<br>";
			}
			System.out.println("\n"+"11.=========--------------------------**************************"+desc1);
			int itemKey1Size = itemKey1.size();
			for(int i=0;i<itemKey1Size;i++){
				desc1=desc1+"\\n"+indent("Metric Key")+itemKey1.get(i)+"<br>";
			}
			
			
			System.out.println("\n"+"12.=========--------------------------**************************"+desc1);
			int itemDescription1Size = itemDescription1.size();
			for(int i=0;i<itemDescription1Size;i++){
				desc1=desc1+"\\n"+indent("Metric Description")+itemDescription1.get(i)+"</pre>[/code]";
			}
			desc1 = desc1  +"\\n"+"\\n"+boldUnderSmall("Host Information");
			System.out.println("\n"+"13.=========--------------------------**************************"+desc1);
			
			int hostnameSize = hostname.size();
			for(int i=0;i<hostnameSize;i++){
				desc1=desc1+"\\n"+indentfirst("Agent Hostname")+hostname.get(i)+"<br>";
			}
			
			System.out.println("\n"+"14.=========--------------------------**************************"+desc1);
			int hostname1Size = hostname1.size();
			for(int i=0;i<hostname1Size;i++){
				desc1=desc1+"\\n"+indent("Hostname in Zabbix")+hostname1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"15.=========--------------------------**************************"+desc1);
			int ip1Size = ip1.size();
			for(int i=0;i<ip1Size;i++){
				desc1=desc1+"\\n"+indent("IP Address")+ip1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"16.=========--------------------------**************************"+desc1);
			int conn1Size = conn1.size();
			for(int i=0;i<conn1Size;i++){
				desc1=desc1+"\\n"+indent("Connection")+conn1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"17.=========--------------------------**************************"+desc1);
			int os1Size = os1.size();
			for(int i=0;i<os1Size;i++){
				desc1=desc1+"\\n"+indent("OS Info")+os1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"18.=========--------------------------**************************"+desc1);
			int invSize = inv.size();
			for(int i=0;i<invSize;i++){
				desc1=desc1+"\\n"+indent("Software Info")+inv.get(i)+"<br>";
			}
			
			System.out.println("\n"+"19.=========--------------------------**************************"+desc1);
			int description1Size = description1.size();
			for(int i=0;i<description1Size;i++){
				desc1=desc1+"\\n"+indent("Description")+description1.get(i)+"</pre>[/code]";
			}
			desc1 = desc1 +"\\n"+"\\n"+boldUnderSmall("Zabbix Debug Information");
			System.out.println("\n"+"20.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indentfirst("Raw Trigger Name")+rawTriggerName+"<br>";
			System.out.println("\n"+"21.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Trigger Expression")+expression+"<br>";
			System.out.println("\n"+"22.=========--------------------------**************************"+desc1);
			
			desc1=desc1+"\\n"+indent("Tag Info")+tags+"</pre>[/code]";
			System.out.println("\n"+"23.=========--------------------------**************************"+desc1);

			desc1=desc1.replace('{', '(');
			desc1=desc1.replace('}', ')');
			System.out.println("\n"+"=========--------------------------**************************"+"triggerName: "+triggerName+"Trigger Severity:"+triggerSeverity+"URL:"+url+"eventid:"+eventid+"starttime"+created_at+"IP Address"+ip1+"host:"+hostname+"description:"+itemDescription1+"\n");
					
			System.out.println("\n"+"=========--------------------------**************************"+"DESCRIPTION PASSING AS COMMENT: "+desc1);
			
			//String description=null;
			String sysid="";
			ticketdetails = selectDB(alert,message,alert_policy_name);//checking ticket values for this load
			String subject = "Zabbix Event# "+incnr+" : "+triggerName;
			System.out.println("***********************SUBJECT:"+subject);
			String contacttype = ticketdetails.get("contacttype").toString();
			//String assignmentgrp = ticketdetails.get("assignmentgrp").toString(); //Do not need this as Alert Group is coming in message body as HOST.GROUP= 
			String impact = ticketdetails.get("impact").toString();
			String category = ticketdetails.get("category").toString();
			String subcategory = ticketdetails.get("subcategory").toString();
			String openedby = ticketdetails.get("openedby").toString();
			String subsubcategory = ticketdetails.get("subsubcategory").toString();
			String urgency = ticketdetails.get("urgency").toString();
			String priority = ticketdetails.get("priority").toString();
			
			try{
				String name = SNOWUser;//"tool_api";
		        String password = SNOWpwd;//"toolapi";
		        String names=null;
		        String authString = name + ":" + password;
		        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
		        System.out.println("Base64 encoded auth string: " + authStringEnc);
		
				Client client = Client.create();
				System.out.println("SNOWURL:"+SNOWURL+"SNOWUSER:"+SNOWUser+"SNOWPWD:"+SNOWpwd);
	            WebResource webResource = client.resource(SNOWURL);
	            
	            System.out.println("-----------------ALERT GROUP:-------------------------"+alertGroup);
	            String input = "{\"contact_type\":\""+ contacttype+"\",\"assignment_group\": \""+alertGroup+"\", \"impact\":\""+impact+"\", \"category\": \""+category+"\",\"comments\":\""+ desc1+"\" ,\"short_description\":\""+ subject+"\", \"subcategory\": \""+subcategory+"\", \"u_opened_by\": \""+openedby+"\", \"u_sub_subcategory\": \""+subsubcategory+"\", \"urgency\": \""+urgency+"\", \"priority\": \""+priority+"\" }";

	            //String input = "{\"contact_type\":\""+ contacttype+"\",\"assignment_group\": \""+assignmentgrp+"\", \"impact\":\""+impact+"\", \"category\": \""+category+"\",\"comments\":\""+ desc1+"\" ,\"short_description\":\""+ subject+"\", \"subcategory\": \""+subcategory+"\", \"u_opened_by\": \""+openedby+"\", \"u_sub_subcategory\": \""+subsubcategory+"\", \"urgency\": \""+urgency+"\", \"priority\": \""+priority+"\" }";
	            //input = "\""+input+"\"";
	            System.out.println("--------CREATE TICKET INPUT STRING IS : "+input);
	            ClientResponse resp = webResource.accept("application/json").type("application/json").header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, input);
	            //ClientResponse resp = webResource.type("application/json").header("Authorization", "Basic " + authStringEnc).get(ClientResponse.class);
	            
	            if (resp.getStatus() != 201) {
	           	 throw new RuntimeException("Failed : HTTP error code : "+ resp.getStatus());
	           	 
	            }
	            System.out.println("Output from Server .... \n");
	    		String output = resp.getEntity(String.class);// output variable will have SNOW TICKET JSON
	    		System.out.println(output);
	    		//JSONObject obj = new JSONObject(output);
	             //sys_id = obj.getString("sys_id");
	    		HashMap hm = getJSON(output);
	    		Iterator i = hm.keySet().iterator();

	    		while(i.hasNext())
	    		{
	    		    String key = i.next().toString();  
	    		    String value = (String) hm.get(key);
	    		    System.out.println(key + " --- " + value);
	    		    if(key.equals("display_value")){
	    		    	inc=(String) hm.get(key);
	    		    	System.out.println("######inc: "+inc);
	    		    }
	    		    else if(key.equals("sys_id")){
	    		    	 sysid=(String) hm.get(key);
	    		    	System.out.println("######sys_id: "+sysid);
	    		    }
	    		}
			System.out.println("BEFORE INC VALUE CHECK IN IF CLAUSE INC VALUE IS:"+inc);
			if(inc == null){
				System.out.println("Got NULL SNOW incident#");			
				SendEmail se = new SendEmail();
				se.email(desc);
			}

			}catch(Exception e){
				e.printStackTrace();
				//System.out.println(e);
				System.out.println("BEFORE INC VALUE CHECK IN IF CLAUSE INC VALUE IS:"+inc+" inc string length"+inc.length());
	                	if(inc == null || inc.length()==0){
	                        System.out.println("Got NULL SNOW incident#");
	                        SendEmail s = new SendEmail();
	                        s.email(desc);
				}	
			}
			hmcreate.put(incnr,inc);
			hmcreate.put("sys_id",sysid);
			return hmcreate;
		}
		
	}
	
	//Update Ticket Method
		public static void updateACKTicket(String sys_id,String alert){
			String[] alert1 = alert.split("----");
			String ackHistory="";
			String desc1="";
			for(int j=0;j<alert1.length;j++){
				if(alert1[j].toUpperCase().contains("EVENT.ACK.HISTORY" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						ackHistory=s[1];
					}
					
				}
			}
			
			desc1=desc1+boldUnder("ALERT ACKNOWLDEGED...")+"\\n";
			desc1=desc1+"\\n"+"\\n"+"\\n"+boldUnder("Event Acknowledgements (from Zabbix) ")+"\\n"+"\\n"+ackHistory;
			System.out.println("\n"+"12.=========--------------------------**************************"+desc1);

			try{
						String name = SNOWUser;//"tool_api";
				        String password = SNOWpwd;//"toolapi";
				        String authString = name + ":" + password;
				        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
				        System.out.println("Base64 encoded auth string: " + authStringEnc);
				
						Client client = Client.create();
						String myURL = SNOWURLupdate;
						URL url = new URL(myURL);
					      String nullFragment = null;
					      URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
					      System.out.println("URI " + uri.toString() + " is OK");
						WebResource webResource = client.resource(uri.toString());
			            System.out.println("incsr:"+sys_id);
			            
			            String state="25";
			            String cmt = "Update by SysWorks API";
			            cmt=desc1;
			            String input = "{\"u_comments\":\""+ cmt+"\",\"u_number\": \""+sys_id+"\" , \"u_state\": \""+state+"\" }";
			            
			            ClientResponse resp = webResource.accept("application/json").type("application/json").header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, input);
			           
			            
			            if ((resp.getStatus() != 201) ) {
			           	 throw new RuntimeException("Failed : HTTP error code : "+ resp.getStatus());
			           	 
			            }
			            System.out.println("Output from Server .... \n");
			    		String output = resp.getEntity(String.class);
			    		System.out.println(output);
			    		
			    		JSONObject obj = new JSONObject(output);
			    		String import_set = obj.getString("import_set");
			    		 
			    		System.out.println("#####AFTER THE UPDATE CALL: "+"import_set: "+import_set);
					}catch(Exception e){e.printStackTrace();}

			
		}
	//Close Ticket Method
	public static void updateTicket(String sys_id,String alert){
		String[] alert1 = alert.split("----");
		String inc=null,triggerName="",triggerSeverity="",url1="",description="";
		String eventid="",dt="",tm="",age="",ers="";
		String itemKeyOrig1="",itemDescription1="";
		String ackHistory="",tags="";
		ArrayList<String> inv = new ArrayList<String>();
		ArrayList<String> itemName = new ArrayList<String>();
		ArrayList<String> itemKey1 = new ArrayList<String>();
		ArrayList<String> itemValue = new ArrayList<String>();
		//String inv="",itemName="",itemKey1="",itemValue="";
		//String hostname="",hostname1="",ip1="",conn1="",os1="",description1="";
		String rawTriggerName="",expression="";
		String desc1="";
		for(int j=0;j<alert1.length;j++){
			if(alert1[j].toUpperCase().contains("TRIGGER.NAME" )){
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$"+alert1[j]);
                		String[] s=alert1[j].split("=");
                		if(s.length>=2 && s[0].equals("TRIGGER.NAME")){
                			triggerName=s[1];
                		}

				if(s.length>=2 && s[0].equals("TRIGGER.NAME.ORIG")){
					rawTriggerName=s[1];
				}
                
			}   
			else if(alert1[j].toUpperCase().contains("TRIGGER.SEVERITY" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					triggerSeverity=s[1];
				}
				
			}
			/*else if(alert1[j].toUpperCase().contains("TRIGGER.URL" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					url1=s[1];
				}
				
			}*/
			else if(alert1[j].toUpperCase().contains("TRIGGER.DESCRIPTION" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					description=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.ID" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					eventid=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("INVENTORY.SOFTWARE.APP.D" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					//inv=s[1];
					inv.add(s[1]);
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.TAGS" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					tags=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.RECOVERY.STATUS" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					ers=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.RECOVERY.DATE" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					dt=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.RECOVERY.TIME" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					tm=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("EVENT.AGE" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					age=s[1];
				}
				
			}
			else if(alert1[j].toUpperCase().contains("ITEM.NAME" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					itemName.add(s[1]);
				}
				
			}
			
			else if(alert1[j].toUpperCase().contains("ITEM.KEY" )){
		        String[] s=alert1[j].split("=");
		        int len = s.length; String iKey1="";
		        if(s.length>=2){
		          for(int i=1;i<len;i++){
		            if(i<len-1)
		              iKey1=iKey1+s[i]+"=";
		            else
		              iKey1=iKey1+s[i];
		          }
		          itemKey1.add(iKey1);
		        }
		        
		      }
			
			else if(alert1[j].toUpperCase().contains("ITEM.VALUE" )){
				String[] s=alert1[j].split("=");String iValue="";
				if(s.length>=2){
					String itemValue1=s[1];
					String[] s1 = itemValue1.split(",");
					//itemValue=s1[0].substring(0,1);
					//itemValue=itemValue.substring(0,1);
					iValue=s1[0].substring(0,1);
					iValue=iValue.substring(0,1);
					itemValue.add(iValue);
				}
				System.out.println("\n>>>>>>>>>>>>>>>>"+alert1[j]+"<<<<<<<<<<<<<<<<<<<");
				System.out.println("\n>>>>>>>>>>>>>>>>"+itemValue+"<<<<<<<<<<<<<<<<<<<");
			}
			
			else if(alert1[j].toUpperCase().contains("EVENT.ACK.HISTORY" )){
				String[] s=alert1[j].split("=");
				if(s.length>=2){
					ackHistory=s[1];
				}
				
			}
			
		}
		
		//desc1 = "#######################"+"\\n";
		desc1=desc1+boldUnder("ALERT CLEARED")+"\\n";
		desc1=desc1+"\\n"+"\\n"+boldUnderSmall("Trigger")+"\\n"+"\\n";
		System.out.println("\n"+"1.=========--------------------------**************************"+desc1);
		desc1=desc1+indentfirst("Trigger Name")+triggerName+"<br>";
		System.out.println("\n"+"2.=========--------------------------**************************"+desc1);

		desc1=desc1+"\\n"+indent("Trigger Severity")+triggerSeverity+"<br>";
		System.out.println("\n"+"3."+desc1);

		//desc1=desc1+"\\n"+"Trigger URL........: "+url1;
		//System.out.println("\n"+"4.=========--------------------------**************************"+desc1);

		desc1=desc1+"\\n"+indent("Trigger Description")+description+"</pre>[/code]"+"\\n"+"\\n"+boldUnderSmall("Trigger Event");
		System.out.println("\n"+"5.=========--------------------------**************************"+desc1);

		
		desc1=desc1+"\\n"+indentfirst("Zabbix Event ID" )+eventid+"<br>";
		System.out.println("\n"+"6.=========--------------------------**************************"+desc1);

		desc1=desc1+"\\n"+indent("Event End Timestamp")+dt+" "+tm+"<br>";
		System.out.println("\n"+"7.=========--------------------------**************************"+desc1);

		desc1=desc1+"\\n"+indent("Event Age")+age+"</pre>[/code]"+"\\n"+"\\n"+boldUnderSmall("Metric Causing the Alert")+"\\n";
		System.out.println("\n"+"8.=========--------------------------**************************"+desc1);
		int itemNameSize = itemName.size();
		for(int i=0;i<itemNameSize;i++){
			desc1=desc1+"\\n"+indentfirst("Metric Name")+itemName+"<br>";
		}
		
		System.out.println("\n"+"9.=========--------------------------**************************"+desc1);
		int itemKey1Size = itemKey1.size();
		for(int i=0;i<itemKey1Size;i++){
			desc1=desc1+"\\n"+indent("Metric Key")+itemKey1.get(i)+"<br>";
		}
		//desc1=desc1+"\\n"+indent("Metric Key:")+itemKey1;
		System.out.println("\n"+"10.=========--------------------------**************************"+desc1);
		int itemValueSize = itemValue.size();
		for(int i=0;i<itemValueSize;i++){
			desc1=desc1+"\\n"+indent("Metric Value")+itemValue+"</pre>[/code]";
		}
		
		System.out.println("\n"+"11.=========--------------------------**************************"+desc1);

		

		

		desc1=desc1+"\\n"+"\\n"+"\\n"+boldUnder("Event Acknowledgements (from Zabbix) ")+"\\n"+"\\n"+ackHistory;
		System.out.println("\n"+"12.=========--------------------------**************************"+desc1);
		System.out.println("\n"+"=========--------------------------**************************"+"DESCRIPTION PASSING AS COMMENT: "+desc1);
		try{
			String name = SNOWUser;//"tool_api";
	        String password = SNOWpwd;//"toolapi";
	        String authString = name + ":" + password;
	        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
	        System.out.println("Base64 encoded auth string: " + authStringEnc);
	
			Client client = Client.create();
			//String myURL = "https://devrackspace.service-now.com/api/now/table/incident?sysparm_query=active=true^assignment_group.name=GETOPSPOC^short_descriptionLIKEtest";
			//String myURL = SNOWURLupdate+sys_id;
			String myURL = SNOWURLupdate;
			URL url = new URL(myURL);
		      String nullFragment = null;
		      URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
		      System.out.println("URI " + uri.toString() + " is OK");
			WebResource webResource = client.resource(uri.toString());
            System.out.println("incsr:"+sys_id);
            //String input = "{ \"u_comments\":\"Update by SysWorks API\",\"u_number\":\""+ sys_id+"\" ,\"u_state\":\"29\"}";
            String state="29";
            String cmt = "Update by SysWorks API";
            cmt=desc1;
            String input = "{\"u_comments\":\""+ cmt+"\",\"u_number\": \""+sys_id+"\" , \"u_state\": \""+state+"\" }";
            
            ClientResponse resp = webResource.accept("application/json").type("application/json").header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, input);
            //ClientResponse resp = webResource.accept("application/json").type("application/json").header("Authorization", "Basic " + authStringEnc).get(ClientResponse.class);
            
            if ((resp.getStatus() != 201) ) {
           	 throw new RuntimeException("Failed : HTTP error code : "+ resp.getStatus());
           	 
            }
            System.out.println("Output from Server .... \n");
    		String output = resp.getEntity(String.class);
    		System.out.println(output);
    		//int len = output.length();
    		//String l = output.substring(10,len-1);
    		
    		//System.out.println(l);
    		//JSONObject obj = new JSONObject(l);
    		JSONObject obj = new JSONObject(output);
    		String import_set = obj.getString("import_set");
    		 //String skills = obj.getString("skills");
    		 //String severity = obj.getString("severity");
            //String active = obj.getString("active");
            //String short_description = obj.getString("short_description");
            //System.out.println("#####AFTER THE UPDATE CALL: "+"skill: "+skills+" severity: "+severity+" active:"+active+"   short_description: "+short_description);
    		System.out.println("#####AFTER THE UPDATE CALL: "+"import_set: "+import_set);
		}catch(Exception e){e.printStackTrace();}
	}
	//Get Ticket Details. Needs INCIDENT NUMBER FROM SERVICE NOW
	public static String getTicket(String incident){
		String sys_id=null;
		try{
			String name = SNOWUser;//"tool_api";
	        String password = SNOWpwd;//"toolapi";
	        
	        String authString = name + ":" + password;
	        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
	        System.out.println("Base64 encoded auth string: " + authStringEnc);
	
			Client client = Client.create();
			//String myURL = "https://devrackspace.service-now.com/api/now/table/incident?sysparm_query=active=true^assignment_group.name=GETOPSPOC^short_descriptionLIKEtest";
			String myURL = SNOWURLget+incident;// This is incident number for SNOW
			URL url = new URL(myURL);
		      String nullFragment = null;
		      URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
		      System.out.println("URI " + uri.toString() + " is OK");
			WebResource webResource = client.resource(uri.toString());
            
            
            
            
            
            ClientResponse resp = webResource.accept("application/json").type("application/json").header("Authorization", "Basic " + authStringEnc).get(ClientResponse.class);
            
            if (resp.getStatus() != 200) {
           	 throw new RuntimeException("Failed : HTTP error code : "+ resp.getStatus());
           	 
            }
            System.out.println("Output from Server .... \n");
    		String output = resp.getEntity(String.class);
    		System.out.println(output);
    		int len = output.length();
    		JSONObject outerObject = new JSONObject(output);

    	    JSONArray jsonArray = outerObject.getJSONArray("result");
    	    
    	    for (int i = 0, size = jsonArray.length(); i < size; i++){
    	    	JSONObject objectInArray = jsonArray.getJSONObject(i);
    	    	String[] elementNames = JSONObject.getNames(objectInArray);
    		      System.out.printf("%d ELEMENTS IN CURRENT OBJECT from getTicket method:\n", elementNames.length);
    		      for (String elementName : elementNames){
    		    	  String value = objectInArray.getString(elementName);
    			        //System.out.printf("name=%s, value=%s\n", elementName, value);
    			        if(elementName.equals("sys_id")){
    			        	sys_id=value;
    			        }
    		      }
    		      System.out.println();
    	    }
            System.out.println(sys_id);
            
		}catch(Exception e){e.printStackTrace();}
		return sys_id;
		
	}
	public static HashMap getJSON(String str){
		String str1=null;
		HashMap myMap = new HashMap();
		try{
			
		JSONObject outerObject = new JSONObject(str);

	    JSONArray jsonArray = outerObject.getJSONArray("result");
	    
	    for (int i = 0, size = jsonArray.length(); i < size; i++)
	    {
	      JSONObject objectInArray = jsonArray.getJSONObject(i);

	      // "...and get thier component and thier value."
	      String[] elementNames = JSONObject.getNames(objectInArray);
	      System.out.printf("%d ELEMENTS IN CURRENT OBJECT FROM getJSON method:\n", elementNames.length);
	      for (String elementName : elementNames)
	      {
	        String value = objectInArray.getString(elementName);
	        System.out.printf("name=%s, value=%s\n", elementName, value);
	        myMap.put(elementName, value);
	      }
	      System.out.println();
	    }
	    
		}catch(Exception e){e.printStackTrace();}
		return myMap;
	}
	
	public static void writeQueue(String json) throws JMSException{
	    //String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		String url = ActiveMQURL;
	    String subject = ACTIVEMQQUEUEName;

	    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
	    Connection connection = connectionFactory.createConnection();
	    connection.start();
	    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	    Destination destination = session.createQueue(subject);
	    MessageProducer producer = session.createProducer(destination);
	    TextMessage message = session.createTextMessage(json);

	    producer.send(message);
	    System.out.println("Message SENT: '" + message.getText() + "'");
	    connection.close();
	    connection.close();
	    }
	//This method update closed_t in transactions table. 
		
	public static void updateClosedt(String sys_id,String issue){
		Calendar calendar = Calendar.getInstance();
	      java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
	      Timestamp ts_now = new Timestamp(startDate.getTime());
		String myDriver = "";
		PreparedStatement stmt =null;
	      String myUrl = "";
	      java.sql.Connection conn = null;
	      String sql ="update transactionszabbix set closed_t = ? where snowinc=? and item_name1=?";
	      ResultSet rs =  null;
	      try{
	    	  myDriver = "org.gjt.mm.mysql.Driver";
		       myUrl = MySQLjdbc;
		      Class.forName(myDriver);
		      conn = DriverManager.getConnection(myUrl, MySQLUser, MySQLpwd);
		      //stmt = conn.prepareStatement(sql);
		      stmt = conn.prepareStatement(sql);
		      stmt.setTimestamp (1, ts_now);
		      stmt.setString (2, sys_id);
		      stmt.setString   (3, issue);
		      stmt.execute();
	      }catch(Exception e){e.printStackTrace();}
	}
	
	public static void insertExclusion(String created_at,String alert_policy_name, String servers,String message,String short_description,String long_description,String alert_url,String incnr){
		String sql ="insert into exclusiontransactions(created_at, alert_policy_name, servers, message, short_description,long_description,alert_url,incnr)"
	        + " values (?, ?, ?, ?, ?, ?, ?, ?)";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		PreparedStatement stmt =null;
        String myDriver = "";
	      String myUrl = "";
	      java.sql.Connection conn = null;
	      ResultSet rs =  null;
	      try
		    {
	    	  Date date = (Date) sdf.parse(created_at);
	  		java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
		      // create a mysql database connection
		       myDriver = "org.gjt.mm.mysql.Driver";
		       myUrl = MySQLjdbc;
		      Class.forName(myDriver);
		      conn = DriverManager.getConnection(myUrl, MySQLUser, MySQLpwd);
		      stmt = conn.prepareStatement(sql);
		      stmt.setTimestamp(1,sqlDate);
		       stmt.setString(2,alert_policy_name);
		       stmt.setString(3,servers);
		       stmt.setString(4,message);
		       stmt.setString(5,short_description);
		       stmt.setString(6,long_description);
		       stmt.setString(7,alert_url);
		       stmt.setString(8,incnr);
		       stmt.execute();
		       
		       
		       stmt.close();
		       
		    }catch(Exception e){e.printStackTrace();}
	}
	//This method will select count of records from exclusion table if alertpolicyname, issue and timing matches. 
		//Count is returned. If count is 0, the create ticket method calls else no ticket gets created
	public static int verifyExclusion(String alert_policy_name, String issue, String tim){
		String sql ="select count(*) from exclusion where alertpolicyname=? and issue=? and ? BETWEEN start_time AND end_time";
		PreparedStatement stmt =null;
        String myDriver = "";
	      String myUrl = "";int rowCount=0;
	      java.sql.Connection conn = null;
	      ResultSet rs =  null;
	      try
		    {
		      // create a mysql database connection
		       myDriver = "org.gjt.mm.mysql.Driver";
		       myUrl = MySQLjdbc;
		      Class.forName(myDriver);
		      conn = DriverManager.getConnection(myUrl, MySQLUser, MySQLpwd);
		      stmt = conn.prepareStatement(sql);
		       stmt.setString(1,alert_policy_name);
		       stmt.setString(2,issue);
		       stmt.setString(3,tim);
		       rs = stmt.executeQuery();
		       rs.next();
		       rowCount = rs.getInt(1);
		       System.out.println(rowCount);
		       rs.close();
		       stmt.close();
		       
		    }catch(Exception e){e.printStackTrace();}
	      return rowCount;
	}
	//This method will select the values such as category, subcategory which will be passed in the ticket creation. 
	//Assumption is alertpolicyname and message(issue) will be unique
	public static HashMap selectDB(String alert, String issue, String alert_policy_name){
		System.out.println("INSIDE selectDB Method call ISSUE:"+issue+" alert_policy_name:"+alert_policy_name+" alert:"+alert);
		int index=0;
		boolean isitapdex = issue.contains("<");
		 if(isitapdex){
			 index = issue.indexOf("<");
			 issue = issue.substring(0,index);
		 }
		 else{
			 index = issue.indexOf(">");
			 issue = issue.substring(0,index);
		 }
		//int index = issue.indexOf(">");
		//issue = issue.substring(0,index);
		System.out.println("ISSUE:"+issue+"alert_policy_name"+alert_policy_name);
		HashMap<String,String> hm = new HashMap<String,String>(); 
		String contacttype="";
		String assignmentgrp="";
		String impact ="";
		String category = "";
		String subcategory ="";
		String openedby ="";
		String subsubcategory = "";
		String urgency="";
		String priority="";
		PreparedStatement stmt =null;
        String myDriver = "";
	      String myUrl = "";
	      java.sql.Connection conn = null;
	      String sql ="select contacttype,assignmentgrp,impact,category,subcategory,openedby,subsubcategory,urgency,priority from ticketdetails where alertpolicyname=? and issue=?";
	      ResultSet rs =  null;
	      
	      try
		    {
		      // create a mysql database connection
		       myDriver = "org.gjt.mm.mysql.Driver";
		       myUrl = MySQLjdbc;
		      Class.forName(myDriver);
		      conn = DriverManager.getConnection(myUrl, MySQLUser, MySQLpwd);
		      stmt = conn.prepareStatement(sql);
		       stmt.setString(1,alert_policy_name);
		       stmt.setString(2,issue);
		       rs = stmt.executeQuery();
		       if(!rs.next()){
                   System.out.println("ERROR!!! NO ENTRY IN ticketdetails table for alert policy: "+alert_policy_name+" & issue: "+issue);
           }
           else{
               System.out.println("INSIDE ELSE");
               
			      do{
			    	  contacttype  = rs.getString("contacttype");
			    	  assignmentgrp = rs.getString("assignmentgrp");
			    	  impact = rs.getString("impact");
			    	  category = rs.getString("category");
			    	  subcategory  = rs.getString("subcategory");
			    	  openedby  = rs.getString("openedby");
			    	  subsubcategory  = rs.getString("subsubcategory");
			    	  urgency  = rs.getString("urgency");
			    	  priority  = rs.getString("priority");
			    	  
			    	  hm.put("contacttype",contacttype);
			    	  hm.put("assignmentgrp",assignmentgrp);
			    	  hm.put("impact",impact);
			    	  hm.put("category",category);
			    	  hm.put("subcategory",subcategory);
			    	  hm.put("openedby",openedby);
			    	  hm.put("subsubcategory",subsubcategory);
			    	  hm.put("urgency",urgency);
			    	  hm.put("priority",priority);
			    	  System.out.print("contacttype: " + contacttype+" assignmentgrp:"+assignmentgrp+" impact:"+impact+" category:"+category);
			      }while(rs.next());
           }   
			      conn.close();
			      stmt.close();
			      rs.close();
		    } catch(Exception e){
		    	System.err.println("Got an exception!");
			      System.err.println(e.getMessage());
			      System.out.println("Inserting into MONGODB and collection errorDB: "+alert);
			      Mongo mongo = new Mongo(mongohost, Integer.parseInt(mongoport));
					DB db = mongo.getDB(mongoDB);
					DBCollection collection2 = db.getCollection(collection3);
		           DBObject dbObject2 = (DBObject)JSON.parse(alert);
		           collection2.insert(dbObject2);
		           System.out.println("Insertion to mongoDB collection errorDB done..");
		    } finally{
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
		      
		return hm;
	}
	
	public static String[] format(String long_description){
		String [] str = long_description.split("----");
		return str;
	}
	public static String bold(String str){
		//str = "[code]<b><font color=\"red\">"+str+"</font></b>[/code]";
		str = "[code]<b>"+str+"</b>[/code]";
		return str;
	}
	
	public static String italic(String str){
		str = "[code]<i>"+str+"</i>[/code]";
		return str;
	}
	public static String underline(String str){
		str = "[code]<u>"+str+"</u>[/code]";
		return str;
	}
	public static String boldUnder(String str){
		str = "[code]<h3><u>"+str+"</u></h3>[/code]";
		return str;
	}
	public static String boldUnderSmall(String str){
		str = "[code]<h4><u>"+str+"</u></h4>[/code]";
		return str;
	}
	public static String indentfirst(String str){
		int len = str.length();
		String pre ="[code]<pre>";
		String s="";
		str = pre+str;
		int length = 19;//Indentation for 35 characters
		for (int i=len;i<length;i++){
			s=s+".";
		}
		s=s+": ";
		str=str+s;
		return str;
	}


public static String indent(String str){
	int len = str.length();
	//String pre ="[code]<pre>";
	String s="";
	//str = pre+str;
	int length = 19;//Indentation for 35 characters
	for (int i=len;i<length;i++){
		s=s+".";
	}
	s=s+": ";
	str=str+s;
	return str;
}

// Verify if existing ticket for same issue against same host & IP exists and returns 1 if exist else returns 0
	public static String existTicket(String item_name1, String app, String issue, String host_host1, String host_ip1) throws SQLException{
		String existTicketSQL = "select snowinc from transactionszabbix where item_name1=? and app=? and issue=? and host_host1=? and host_ip1=? and closed_t ='0000-00-00 00:00:00'";
		
		String sqlcnt = "select count(*) from transactionszabbix where item_name1=? and app=? and issue=? and host_host1=? and host_ip1=? and closed_t ='0000-00-00 00:00:00'";
		PreparedStatement stmtcnt =null;
		ResultSet rscnt =  null;
		
		PreparedStatement stmt =null;
        	String myDriver = "";
	      	String myUrl = "";String incsnow="";
	     	java.sql.Connection conn = null;
	      	ResultSet rs =  null;
	      	int rowCount=0;
	      
	      
	      try
		    {
		      // create a mysql database connection
		       myDriver = "org.gjt.mm.mysql.Driver";
		       myUrl = MySQLjdbc;//"jdbc:mysql://10.12.107.204:3306/mytool";
		      Class.forName(myDriver);
		      conn = DriverManager.getConnection(myUrl, MySQLUser, MySQLpwd);
		      stmtcnt = conn.prepareStatement(sqlcnt);
		      stmtcnt.setString(1,item_name1);
		     
		       stmtcnt.setString(2,app);
		       stmtcnt.setString(3,issue);
		       stmtcnt.setString(4,host_host1);
		       stmtcnt.setString(5,host_ip1);
		       rscnt = stmtcnt.executeQuery();
		       rscnt.next();
		       rowCount = rscnt.getInt(1);
		      if(rowCount>0){
		    	       stmt = conn.prepareStatement(existTicketSQL);
			       stmt.setString(1,item_name1);
			       stmt.setString(2,app);
			       stmt.setString(3,issue);
			       stmt.setString(4,host_host1);
			       stmt.setString(5,host_ip1);
			       rs = stmt.executeQuery();
			       rs.next();
			       incsnow = rs.getString(1);
		      }
		      else{
		    	  incsnow="";
		      }
		      
		       System.out.println("INCSNOW"+incsnow);
		       
		       
		    }catch(Exception e){e.printStackTrace();}
	      finally {
	    		if(rscnt != null) 	rscnt.close();
		       if(stmtcnt!=null)	stmtcnt.close();
		       if(rs!=null)		rs.close();
		       if(stmt!=null)		stmt.close();
		       if(conn!=null)		conn.close();
	    }
	      return incsnow;
	}
//This method parses alert and get strings such as TRIGGER.NAME,HOST.HOST1,HOST.IP1,app,issue (from EVENT.TAGS)
	public static HashMap token(String long_description){
		String item_name1="";
		String app="";
		String issue="";
		String host_host1="";
		String host_ip1="";
		
		HashMap hm = new HashMap(); 
		String[] long_desc = long_description.split("----");
       // System.out.println(long_desc.length+" ***");
        for(int i=0;i<long_desc.length;i++){
        	System.out.println("$$$"+long_desc[i]+"$$$");
        	if(long_desc[i].contains("ITEM.NAME1")){
        		String[] itemname1 = long_desc[i].split("=");
        		System.out.println(">>>>"+long_desc[i]);
        		
        		
        		if(itemname1.length>=2 && itemname1[0].equals("ITEM.NAME1")){
        				item_name1=itemname1[1];
        				hm.put("item_name1", item_name1);
        				System.out.println("---------"+item_name1);
         		}	
        		
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
        	
        	else if(long_desc[i].contains("HOST.HOST1")){
        		String[] host = long_desc[i].split("=");
        		host_host1=host[1];
        		hm.put("host_host1", host_host1);
        	}
        	else if(long_desc[i].contains("HOST.IP1")){
        		String[] hostip1 = long_desc[i].split("=");
        		host_ip1=hostip1[1];
        		hm.put("host_ip1", host_ip1);
        	}
        	
        	
        }
        return hm;
	}
	
	//Update Ticket Method for Escalating severity
		public static void updateTicketForNew(String snowinc, String alert,String message, String alert_policy_name, String incnr,String desc,String created_at){
			String[] alert1 = alert.split("----");
			String inc=null,triggerName="",triggerSeverity="",description="";
			String eventid="",timestamp="",age="";
			String rawTriggerName="",expression="";
			String desc1="",tags="",alertGroup="";
			//String inv="",itemName="",itemValue="",itemKeyOrig1="",itemKey1="",itemDescription1=""hostname="",hostname1="",ip1="",conn1="",os1="",description1="";
			ArrayList<String> inv = new ArrayList<String>();
			ArrayList<String> itemName= new ArrayList<String>();
			ArrayList<String> itemValue= new ArrayList<String>();
			ArrayList<String> itemKeyOrig1= new ArrayList<String>();
			ArrayList<String> itemKey1= new ArrayList<String>();
			ArrayList<String> itemDescription1= new ArrayList<String>();
			ArrayList<String> hostname= new ArrayList<String>();
			ArrayList<String> hostname1= new ArrayList<String>();
			ArrayList<String> ip1= new ArrayList<String>();
			ArrayList<String> conn1= new ArrayList<String>();
			ArrayList<String> os1= new ArrayList<String>();
			ArrayList<String> description1= new ArrayList<String>();
			
			for(int j=0;j<alert1.length;j++){
				if(alert1[j].toUpperCase().contains("TRIGGER.NAME" )){
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$"+alert1[j]);
	                		String[] s=alert1[j].split("=");
	                		/*if(s.length>=2){
	                			triggerName=s[1];
	                		}*/
					
					if(s.length>=2 && s[0].equals("TRIGGER.NAME")){
	                 			triggerName=s[1];
						System.out.println("$$$$$$$$$$$$$$$$$$TRIGGER.NAME:"+triggerName);
	                 		}	
	 				else if(s.length>=2 && s[0].equals("TRIGGER.NAME.ORIG")){
	 					rawTriggerName=s[1];
						System.out.println("$$$$$$$$$$$$$$$$$$TRIGGER.NAME.ORIGINAL:"+rawTriggerName);
	 				}
	                
				}   
				else if(alert1[j].toUpperCase().contains("TRIGGER.SEVERITY" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						triggerSeverity=s[1];
					}
					
				}
				/*else if(alert1[j].toUpperCase().contains("TRIGGER.URL" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						url=s[1];
					}
					
				}*/
				else if(alert1[j].toUpperCase().contains("INVENTORY.SOFTWARE.APP.D" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						//inv=s[1];
						inv.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("EVENT.TAGS" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						tags=s[1];
					}
					
				}
				else if(alert1[j].toUpperCase().contains("TRIGGER.DESCRIPTION" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						description=s[1];
					}
					
				}
				else if(alert1[j].toUpperCase().contains("EVENT.ID" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						eventid=s[1];
					}
					
				}
				else if(alert1[j].toUpperCase().contains("EVENT.ID" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						eventid=s[1];
					}
					
				}
				else if(alert1[j].toUpperCase().contains("EVENT.AGE" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						age=s[1];
					}
					
				}
				else if(alert1[j].toUpperCase().contains("ITEM.NAME" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						itemName.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("ITEM.VALUE" )){
					String[] s=alert1[j].split("=");String iValue="";
					if(s.length>=2){
						String itemValue1=s[1];
						String[] s1 = itemValue1.split(",");
						//itemValue=s1[0].substring(0,1);
						//itemValue=itemValue.substring(0,1);
						iValue=s1[0].substring(0,1);
						iValue=iValue.substring(0,1);
						itemValue.add(iValue);
					}
					System.out.println("\n>>>>>>>>>>>>>>>>"+alert1[j]+"<<<<<<<<<<<<<<<<<<<");
					System.out.println("\n>>>>>>>>>>>>>>>>"+itemValue+"<<<<<<<<<<<<<<<<<<<");
				}
				else if(alert1[j].toUpperCase().contains("ITEM.KEY.ORIG" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						itemKeyOrig1.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("ITEM.KEY" )){
					String[] s=alert1[j].split("=");
					int len = s.length; String iKey1="";
					if(s.length>=2){
						for(int i=1;i<len;i++){
							if(i<len-1)
								iKey1=iKey1+s[i]+"=";
							else
								iKey1=iKey1+s[i];
						}
						itemKey1.add(iKey1);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("ITEM.DESCRIPTION" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						itemDescription1.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("HOST.HOST" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						hostname.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("HOST.NAME" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						hostname1.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("HOST.IP" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						ip1.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("HOST.CONN" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						conn1.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("INVENTORY.OS" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						os1.add(s[1]);
					}
					
				}
				else if(alert1[j].toUpperCase().contains("HOST.DESCRIPTION" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						description1.add(s[1]);
					}
					
				}
				/*else if(alert1[j].toUpperCase().contains("TRIGGER.NAME.ORIG" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						rawTriggerName=s[1];
					}
					
				}*/
				else if(alert1[j].toUpperCase().contains("TRIGGER.EXPRESSION" )){
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						expression=s[1];
					}
					
				}
				
				else if(alert1[j].toUpperCase().contains("HOST.GROUP" )){// This will parse the Alert Group such as PostgreSQL
					String[] s=alert1[j].split("=");
					if(s.length>=2){
						alertGroup=s[1].substring(0,s[1].indexOf('"'));
					}
					
				}
				
			}
			//System.out.println("\n incnr:"+incnr+" trigger_name:"+triggerName);
			//desc1 = "#######################"+"\\n";
			desc1=desc1+boldUnder("Zabbix Alert Detail")+"\\n";
			desc1=desc1+"\\n"+boldUnderSmall("Trigger")+"\\n"+"\\n";
			System.out.println("\n"+"1.=========--------------------------**************************"+desc1);
			desc1=desc1+indentfirst("Trigger Name")+triggerName+"<br>";
			System.out.println("\n"+"2.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Trigger Severity")+triggerSeverity+"<br>";
			//System.out.println("\n"+"3."+desc);

			//desc1=desc1+"\\n"+"Trigger URL........: "+url;
			//System.out.println("\n"+"4.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Trigger Description")+description+"</pre>[/code]"+"\\n"+"\\n"+boldUnderSmall("Metric Causing the Alert");
			System.out.println("\n"+"5.=========--------------------------**************************"+desc1);

			
			desc1=desc1+"\\n"+indentfirst("Zabbix Event ID")+eventid+"<br>";
			System.out.println("\n"+"6.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Event Start Time")+created_at+"<br>";
			System.out.println("\n"+"7.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Event Age")+age+"</pre>[/code]"+"\\n"+"\\n"+boldUnderSmall("Metric Causing the Alert")+"\\n";
			System.out.println("\n"+"8.=========--------------------------**************************"+desc1);

			int itemNameSize = itemName.size();
			for(int i=0;i<itemNameSize;i++){
				desc1=desc1+"\\n"+indentfirst("Metric Name"+(i+1))+""+itemName.get(i)+"<br>";
			}
			
			//desc1=desc1+"\\n"+indent("Metric Name:")+itemName;
			System.out.println("\n"+"9.=========--------------------------**************************"+desc1);
			int itemValueSize=itemValue.size();
			for(int i=0;i<itemValueSize;i++){
				desc1=desc1+"\\n"+indent("Metric Value")+itemValue.get(i)+"<br>";
			}
			
			System.out.println("\n"+"10.=========--------------------------**************************"+desc1);
			int itemKeyOrig1Size = itemKeyOrig1.size();
			for(int i=0;i<itemKeyOrig1Size;i++){
				desc1=desc1+"\\n"+indent("Raw Metric Key")+itemKeyOrig1.get(i)+"<br>";
			}
			System.out.println("\n"+"11.=========--------------------------**************************"+desc1);
			int itemKey1Size = itemKey1.size();
			for(int i=0;i<itemKey1Size;i++){
				desc1=desc1+"\\n"+indent("Metric Key")+itemKey1.get(i)+"<br>";
			}
			
			
			System.out.println("\n"+"12.=========--------------------------**************************"+desc1);
			int itemDescription1Size = itemDescription1.size();
			for(int i=0;i<itemDescription1Size;i++){
				desc1=desc1+"\\n"+indent("Metric Description")+itemDescription1.get(i)+"</pre>[/code]";
			}
			desc1 = desc1  +"\\n"+"\\n"+boldUnderSmall("Host Information");
			System.out.println("\n"+"13.=========--------------------------**************************"+desc1);
			
			int hostnameSize = hostname.size();
			for(int i=0;i<hostnameSize;i++){
				desc1=desc1+"\\n"+indentfirst("Agent Hostname")+hostname.get(i)+"<br>";
			}
			
			System.out.println("\n"+"14.=========--------------------------**************************"+desc1);
			int hostname1Size = hostname1.size();
			for(int i=0;i<hostname1Size;i++){
				desc1=desc1+"\\n"+indent("Hostname in Zabbix")+hostname1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"15.=========--------------------------**************************"+desc1);
			int ip1Size = ip1.size();
			for(int i=0;i<ip1Size;i++){
				desc1=desc1+"\\n"+indent("IP Address")+ip1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"16.=========--------------------------**************************"+desc1);
			int conn1Size = conn1.size();
			for(int i=0;i<conn1Size;i++){
				desc1=desc1+"\\n"+indent("Connection")+conn1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"17.=========--------------------------**************************"+desc1);
			int os1Size = os1.size();
			for(int i=0;i<os1Size;i++){
				desc1=desc1+"\\n"+indent("OS Info")+os1.get(i)+"<br>";
			}
			
			System.out.println("\n"+"18.=========--------------------------**************************"+desc1);
			int invSize = inv.size();
			for(int i=0;i<invSize;i++){
				desc1=desc1+"\\n"+indent("Software Info")+inv.get(i)+"<br>";
			}
			
			System.out.println("\n"+"19.=========--------------------------**************************"+desc1);
			int description1Size = description1.size();
			for(int i=0;i<description1Size;i++){
				desc1=desc1+"\\n"+indent("Description")+description1.get(i)+"</pre>[/code]";
			}
			desc1 = desc1 +"\\n"+"\\n"+boldUnderSmall("Zabbix Debug Information");
			System.out.println("\n"+"20.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indentfirst("Raw Trigger Name")+rawTriggerName+"<br>";
			System.out.println("\n"+"21.=========--------------------------**************************"+desc1);

			desc1=desc1+"\\n"+indent("Trigger Expression")+expression+"<br>";
			System.out.println("\n"+"22.=========--------------------------**************************"+desc1);
			
			desc1=desc1+"\\n"+indent("Tag Info")+tags+"</pre>[/code]";
			System.out.println("\n"+"23.=========--------------------------**************************"+desc1);

			desc1=desc1.replace('{', '(');
			desc1=desc1.replace('}', ')');
			System.out.println("\n"+"=========--------------------------**************************"+"triggerName: "+triggerName+"Trigger Severity:"+triggerSeverity+"eventid:"+eventid+"starttime"+created_at+"IP Address"+ip1+"host:"+hostname+"description:"+itemDescription1+"\n");
					
			System.out.println("\n"+"=========--------------------------**************************"+"DESCRIPTION PASSING AS COMMENT: "+desc1);
			
			//String description=null;
			
			
			try{
							String name = SNOWUser;//"tool_api";
					        String password = SNOWpwd;//"toolapi";
					        String authString = name + ":" + password;
					        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
					        System.out.println("Base64 encoded auth string: " + authStringEnc);
					
							Client client = Client.create();
							String myURL = SNOWURLupdate;
							URL url = new URL(myURL);
						      String nullFragment = null;
						      URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
						      System.out.println("URI " + uri.toString() + " is OK");
							WebResource webResource = client.resource(uri.toString());
				            System.out.println("incsr:"+snowinc);
				            
				            String state="25";
				            String cmt = "Update by SysWorks API";
				            cmt=desc1;
				            String input = "{\"u_comments\":\""+ cmt+"\",\"u_number\": \""+snowinc+"\" , \"u_state\": \""+state+"\" }";
				            
				            ClientResponse resp = webResource.accept("application/json").type("application/json").header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, input);
				           
				            
				            if ((resp.getStatus() != 201) ) {
				           	 throw new RuntimeException("Failed : HTTP error code : "+ resp.getStatus());
				           	 
				            }
				            System.out.println("Output from Server .... \n");
				    		String output = resp.getEntity(String.class);
				    		System.out.println(output);
				    		
				    		JSONObject obj = new JSONObject(output);
				    		String import_set = obj.getString("import_set");
				    		 
				    		System.out.println("#####AFTER THE UPDATE CALL: "+"import_set: "+import_set);
						}catch(Exception e){e.printStackTrace();}
		}
		
		//Inseert into Warning Table
		public static void insertDB(java.sql.Timestamp sqlDate,String event_id, String app, String issue, String event_status, String event_date, String event_time, String event_age, String event_ack_history, String event_ack_status, String host_host1, String host_name1, String host_dns1, String host_ip1, String host_conn1, String host_description1, String inventory_os1, String inventory_software_app_d1, String trigger_name, String trigger_name_orig, String trigger_description, String trigger_status, String trigger_severity, String trigger_expression, String trigger_hostgroup_name, String item_name1, String item_description1, String item_key1, String item_key_orig1, String item_value1, String action_name, String host_group){ 
			
	        PreparedStatement preparedStmt =null;
	        String myDriver = "";
		      String myUrl = "";
		      java.sql.Connection conn = null;
		      String query=""; String sql ="";
		      PreparedStatement stmt = null;
		      ResultSet rs =  null;
		      
		     
		      
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
		       query = " insert into transactionszabbixwarning (created_at, event_id, app, issue, event_status,event_date,event_time,event_age,event_ack_history,event_ack_status,host_host1,host_name1,host_dns1,host_ip1,host_conn1,host_description1,inventory_os1,inventory_software_app_d1,trigger_name,trigger_name_orig,trigger_description,trigger_status,trigger_severity,trigger_expression,trigger_hostgroup_name,item_name1,item_description1,item_key1,item_key_orig1,item_value1,action_name,host_group)"
		        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
		 System.out.println("inserting into transactionszabbixwarning TABLE" );
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
		      //preparedStmt.setString    (33, short_description);
		      //preparedStmt.setString    (34, snowinc);
		      
		      
		      
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
		
		// This method parses long_description and get strings such as EVENT.ID,HOST.HOST1
		public static HashMap tokenAlert(String long_description){
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
		// This Updates transactionszabbixwarning table closed_t column with current timestamp
		public static void updateWarningTable(Date sqlDate,String eventid){
			
			Calendar calendar = Calendar.getInstance();
		      java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
		      Timestamp ts_now = new Timestamp(startDate.getTime());
		      System.out.println("Updating transactionszabbixwarning table for event_id:"+eventid+" with closed_t to:"+ts_now);
			String myDriver = "";
			PreparedStatement stmt =null;
		      String myUrl = "";
		      java.sql.Connection conn = null;
		      String sql ="update transactionszabbixwarning set closed_t = ? where event_id=?";
		      ResultSet rs =  null;
		      try{
		    	  myDriver = "org.gjt.mm.mysql.Driver";
			       myUrl = MySQLjdbc;
			      Class.forName(myDriver);
			      conn = DriverManager.getConnection(myUrl, MySQLUser, MySQLpwd);
			      //stmt = conn.prepareStatement(sql);
			      stmt = conn.prepareStatement(sql);
			      stmt.setTimestamp (1, ts_now);
			      stmt.setString (2, eventid);
			      
			      stmt.execute();
		      }catch(Exception e){e.printStackTrace();}
		}

}

class serialZabbix implements java.io.Serializable
{
   public String incsn;
   public String incnr;
   
   
}
