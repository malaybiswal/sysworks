
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
public class jsonParser {
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
			
			FileInputStream fstream = new FileInputStream("/root/malay/javaProject/my.conf");
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
        //  port(5678); <- Uncomment this if you want spark to listen on a port different than 4567

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
                 
                 HashMap hm2 = new HashMap();
                 String key = null;
      			String value;
      			
                 try{
                	 String bodyconvert = java.net.URLDecoder.decode(body, "UTF-8");
                	 System.out.println("*******"+bodyconvert);
                	 String alert_policy_name="", application_name="", servers="", message="", short_description="", long_description="", created_at="", alert_url="", incnr="";
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
                          incident_id=alert_url.split("incidents");
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
                     String desc = short_description+" Server And Issue is: "+long_description+" AND Issue occured at: "+created_at+" UTC";
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
            		 /*int index = message.indexOf(">");
             		 String issue = message.substring(0,index);
                     String issue1 = issue.replaceAll("\\s+","");*/
                    	 if(short_description.substring(0,3).equals("New")){
                    		 System.out.println("message:"+message+" alert_policy_name:"+alert_policy_name+" nr:"+nr+" desc:"+desc);
                    		 /*String []tm = created_at.split("T");
                    		 String tim = tm[1].substring(0,8);
                    		 int index = message.indexOf(">");
                     		 String issue = message.substring(0,index);*/
                     		System.out.println("---------alertpolicyname "+alert_policy_name+" issue "+issue+" and time "+tim);
                     		 int ex = verifyExclusion(alert_policy_name, issue, tim);
                     		 if(ex==0){
                     			hm2 = createTicket(alert,message,alert_policy_name,nr,desc);//nr variable stores incident number from NewRelic
                       		 //hm2 has both NR INCIDENT# and SNOW INCIDENT#
                       		 
                       		 Iterator i = hm2.keySet().iterator();
                    			String incsr=null;
                    			serial s = new serial();
                    			String queuetext=null;
                    			
                       		 while(i.hasNext())
                    			{
                    			     key = i.next().toString();  
                    			     value = (String) hm2.get(key);
                    			    System.out.println(key + " --- " + value);// value is incident number for SNOW
                    			   s.incnr = key ;
                    			   s.incsn = value;
                    			   queuetext = key+"-----"+value+"-----"+alert; 
                    			}
                       		 
                       		 writeQueue(queuetext);
                       		 String location = serializeFileLocation+key+issue1;// Key is incident# from NewRelic and issue1 is first element from message after spliting with > without white spaces
                       		 System.out.println("SERIALIZED LOCATION: "+location);
                       		 FileOutputStream fileOut = new FileOutputStream(location);
                       		 ObjectOutputStream out = new ObjectOutputStream(fileOut);
                       		 out.writeObject(s);
                       		 out.close();
                       		 fileOut.close();
                       		 System.out.println("SERIALIZED DATA SAVED IN LOCATION: "+location);
                     		 }
                     		 else{
                     			 System.out.println("*****************EXCLUSION FIELD APPLIED as "+ex+" number of records for alertpolicyname "+alert_policy_name+" issue "+issue+" and time "+tim);
                     		     insertExclusion(created_at,alert_policy_name,servers,message,short_description,long_description,alert_url,incnr);
                     		 }
                    		 
                    	 }
                    	 else if((short_description.substring(0,5).equals("Ended")) ){
                    		 System.out.println("INSIDE ELSE IF UpdateSNOW: "+UpdateSNOW);
                    		 serial s = null;
                    		 String locationpick = serializeFileLocation+incnr+issue1;
                    		 FileInputStream fileIn = new FileInputStream(locationpick);
                    		 ObjectInputStream in = new ObjectInputStream(fileIn);
                    		 s = (serial) in.readObject();
                    		 in.close();
                             fileIn.close();
                             String incsr = s.incsn;
                             //if there are multiple issues with same incnr then only one will be updated. Need to add message and incsr to generate file, so 
                             //File name wil be different for different issues for same NewRElic incident
                    		 
                    		 String sys_id=getTicket(incsr);
                    		 System.out.println("****INSIDE METHOD SYS+ID GOT:"+sys_id);
                    		 
                    		 //int index = message.indexOf(">");
                    		// String issue = message.substring(0,index);
                    		 System.out.println("updateClosedt passing values incsr:"+incsr+" issue:"+message);
                    		 updateClosedt(incsr,message);
                    		 if(UpdateSNOW.equals("Yes")){
                    			 //updateTicket(sys_id);
                    			 updateTicket(incsr);
                    		 }
                    		 
                    	 }
                    	 else{
                    		 	System.out.println("UpdateSNOW: "+UpdateSNOW);
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
	public static HashMap createTicket(String alert, String message, String alert_policy_name, String incnr,String desc){
		String inc=null;
		//String description=null;
		HashMap hmcreate = new HashMap();
		HashMap ticketdetails = new HashMap();
		ticketdetails = selectDB(alert,message,alert_policy_name);//checking ticket values for this load
		
		String contacttype = ticketdetails.get("contacttype").toString();
		String assignmentgrp = ticketdetails.get("assignmentgrp").toString();
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
            
            
            //String input = "{\"assignment_group\": \"GETOPSPOC\", \"impact\": \"1\", \"category\": \"inc_sw_3party_desktop_app\", \"short_description\": \"test\", \"subcategory\": \"inc_symantec\", \"u_opened_by\": \"pank1223\", \"u_sub_subcategory\": \"inc_virus\", \"urgency\": \"1\" }";
            //String input = "{\"assignment_group\": \"GET Ops Tier1\", \"impact\": \"1\", \"category\": \"inc_cat_other\", \"short_description\":\""+ desc+"\", \"subcategory\": \"inc_sub_other\", \"u_opened_by\": \"pank1223\", \"u_sub_subcategory\": \"inc_subsub_other\", \"urgency\": \"1\" }";
            //String input = "{\"contact_type\": \"Email\",\"assignment_group\": \"GET Ops Tier1\", \"impact\": \"1\", \"category\": \"inc_sw_3party_enterprise_app\", \"short_description\":\""+ desc+"\", \"subcategory\": \"inc_EBI_ETL\", \"u_opened_by\": \"pank1223\", \"u_sub_subcategory\": \"inc_EBI_Data_issue\", \"urgency\": \"1\" }";
            //String input = "{\"contact_type\": \"Email\",\"assignment_group\": \"GET Ops Tier1\", \"impact\": \"2\", \"category\": \"inc_rackspace\", \"short_description\":\""+ desc+"\", \"subcategory\": \"inc_ums_application\", \"u_opened_by\": \"mala0858\", \"u_sub_subcategory\": \"inc_system_issue\", \"urgency\": \"2\", \"priority\": \"2\" }";
            //String input = "{\"contact_type\":\""+ contacttype+"\",\"assignment_group\": \""+assignmentgrp+"\", \"impact\":\""+impact+"\", \"category\": \""+category+"\", \"short_description\":\""+ desc+"\", \"subcategory\": \""+subcategory+"\", \"u_opened_by\": \""+openedby+"\", \"u_sub_subcategory\": \""+subsubcategory+"\", \"urgency\": \""+urgency+"\", \"priority\": \""+priority+"\" }";
            String input = "{\"contact_type\":\""+ contacttype+"\",\"assignment_group\": \""+assignmentgrp+"\", \"impact\":\""+impact+"\", \"category\": \""+category+"\",\"comments\":\""+ desc+"\" ,\"short_description\":\""+ desc+"\", \"subcategory\": \""+subcategory+"\", \"u_opened_by\": \""+openedby+"\", \"u_sub_subcategory\": \""+subsubcategory+"\", \"urgency\": \""+urgency+"\", \"priority\": \""+priority+"\" }";
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
    		} 
		System.out.println("BEFORE INC VALUE CHECK IN IF CLAUSE INC VALUE IS:"+inc+" inc string length"+inc.length());
		if(inc == null){
                        System.out.println("Got NULL SNOW incident#");
                        SendEmail se = new SendEmail();
                        se.email(desc);
                }
		}catch(Exception e){
			//e.printStackTrace();
			System.out.println(e);
		        System.out.println("IN EXCEPTION LINE 435 BEFORE INC VALUE CHECK IN IF CLAUSE INC VALUE IS:"+inc);
                        if(inc == null){
                        System.out.println("IN Exception Got NULL SNOW incident#");
                        SendEmail s = new SendEmail();
                        s.email(desc);
                        }
			}
		hmcreate.put(incnr,inc);
		return hmcreate;
	}
	//Update Ticket Method
	public static void updateTicket(String sys_id){
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
    		      System.out.printf("%d ELEMENTS IN CURRENT OBJECT:\n", elementNames.length);
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
	      System.out.printf("%d ELEMENTS IN CURRENT OBJECT:\n", elementNames.length);
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
	      String sql ="update transactions set closed_t = ? where snowinc=? and message=?";
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
}

class serial implements java.io.Serializable
{
   public String incsn;
   public String incnr;
   
   
}

