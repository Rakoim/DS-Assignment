import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/*  
Use javac *.java
java OriMD5Crack hash_value 
to run.
Takes forever to complete though.
*/
public class OriMD5Crack {

    public static void main(String[] args) {

        // Record the start time of the cracking attempt
        Timestamp timesStart = new Timestamp(System.currentTimeMillis());
        boolean isFound = false;  // Flag to indicate if the password has been found

        // First loop: iterates over ASCII characters from 33 to 126 (printable characters)
        for (int i = 33; i <= 126; i++) {
            if (isFound) break;
            String strFirst = Character.toString((char) i);  // Convert ASCII code to character for the first position

            // Second loop: adds a second character to the combination
            for (int j = 33; j <= 126; j++) {
                if (isFound) break;
                String strSecond = strFirst + Character.toString((char) j);  // Combine first and second characters

                // Third loop: adds a third character to the combination
                for (int k = 33; k <= 126; k++) {
                    if (isFound) break;
                    String strThird = strSecond + Character.toString((char) k);  // Combine with third character

                    // Fourth loop: adds a fourth character
                    for (int m = 33; m <= 126; m++) {
                        if (isFound) break;
                        String strForth = strThird + Character.toString((char) m);  // Combine with fourth character

                        // Fifth loop: adds a fifth character
                        for (int n = 33; n <= 126; n++) {
                            if (isFound) break;
                            String strFifth = strForth + Character.toString((char) n);  // Combine with fifth character

                            // Sixth loop: adds a sixth character
                            for (int y = 33; y <= 126; y++) {
                                // Construct the final string to be tested
                                String strFinal = strFifth + Character.toString((char) y);
                                System.out.println(strFinal);  // Display the current combination being tested

                                // Compare the MD5 hash of the current combination to the target hash (args[0])
                                if (getMd5(strFinal).equals(args[0])) {
                                    // If a match is found, display the password and set isFound to true
                                    System.out.print("Password found:");
                                    System.out.println(strFinal);
                                    isFound = true;  // Stop further checking once the password is found
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Record the end time of the cracking attempt
        Timestamp timesEnd = new Timestamp(System.currentTimeMillis());
        System.out.println("Start Time: " + timesStart);  // Display start time
        System.out.println("End Time: " + timesEnd);      // Display end time
    }

    // Method to compute the MD5 hash of a given string
    public static String getMd5(String input) {
        try {
            // Create an MD5 MessageDigest instance
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Compute the hash as a byte array
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array to a BigInteger for easy manipulation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert the hash to hexadecimal format
            String hashtext = no.toString(16);

            // Pad with leading zeros if necessary to ensure it is 32 characters long
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // Return the hex-formatted MD5 hash
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);  // Handle any errors if MD5 algorithm is not found
        }
    }

}
