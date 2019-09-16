
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
 
 
public class sshTest {
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        String host="";
        String user="";
        String password="";
        //String command="sudo su - brmdash";
        //String command1="ls -ltr";
        String location="";//"/home/brmdash/kill.sh";
        ssh(host,user,password,location);
        
 
    }
   public static void  ssh(String host, String user, String password,String location){
    	List<String> commands = new ArrayList<String>();
        commands.add("sudo su - brmdash");
        //commands.add("kill -9 26812 26814");
    	commands.add(location);
        try{
             
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected...... and executing script at location: "+location);
             
            ChannelExec channel=(ChannelExec)session.openChannel("exec");
            channel.setOutputStream(System.out); 
            PrintStream shellStream = new PrintStream(channel.getOutputStream());
            channel.setCommand("sh "+location);
            channel.connect();
            for(String command: commands) {
                shellStream.println(command); 
                try{Thread.sleep(3000);}catch(Exception ee){}
                shellStream.flush();
            }
            
   
            
           /* ((ChannelExec)channel).setCommand(command+"\n"+command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
             
            InputStream in=channel.getInputStream();
            
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
              }
              if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
              }*/
              try{Thread.sleep(1000);}catch(Exception ee){}
            //}
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
 
}
