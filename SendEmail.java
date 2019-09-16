import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendEmail
{
   public static void main(String [] args)
   { String desc="";
        email(desc);
        } // END OF Main method
public static void email(String desc){
        String to = "malay.biswal@rackspace.com";
       String from = "SNOWISSUE@rackspace.com";
       String subject = "ISSUE Creating SNOW Ticket";
       String text = "There is issue with creating SNOW ticket while trying to create this description...  "+desc;
      mail(to,from,subject,text);
}

public   static void  mail(String to,String from,String subject,String text){
         try{
                 String host = "smtp1.ord1.corp.rackspace.com";
             Properties properties = System.getProperties();
             properties.setProperty("mail.smtp.host", host);
             Session session = Session.getDefaultInstance(properties);
             MimeMessage message = new MimeMessage(session);
             message.setFrom(new InternetAddress(from));
             message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
             message.setSubject(subject);
             message.setText(text);
             Transport.send(message);
             System.out.println("Sent message successfully....");
         }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }


}
