
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
 
import org.json.JSONObject;
 
 
public class sendJsonTest {
	public static void main(String[] args) {
		String string = "";
		try {
 
			// Step1: Let's 1st read file from fileSystem
			// Change SyntheticJSON.txt path here
			InputStream crunchifyInputStream = new FileInputStream("/root/malay/javaProject/JSON.txt");
			//InputStream crunchifyInputStream = new FileInputStream("/Users/mala0858/Documents/workspace/JSONEND.txt");
			InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
			BufferedReader br = new BufferedReader(crunchifyReader);
			String line;
			while ((line = br.readLine()) != null) {
				string += line + "\n";
			}
 
			//JSONObject jsonObject = new JSONObject(string);
			//System.out.println(jsonObject);
 
			// Step2: Now pass JSON File Data to REST Service
			try {
				URL url = new URL("http://10.12.107.81:4567/xyz");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				//out.write(jsonObject.toString());
				out.write(string);
				out.close();
 
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
 
				while (in.readLine() != null) {
				}
				System.out.println("\nSynthetic REST Service Invoked Successfully..");
				in.close();
			} catch (Exception e) {
				System.out.println("\nError while calling Synthetic REST Service");
				System.out.println(e);
			}
 
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

