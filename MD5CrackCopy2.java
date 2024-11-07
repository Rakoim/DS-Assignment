import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Scanner;

public class MD5CrackCopy2 {
    private static volatile boolean isFound = false;  // shared across threads
    private static String foundPassword = null;       // stores found password

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the MD5 hash: ");
        String targetHash = scanner.nextLine();

        System.out.print("Enter the number of threads (1-10): ");
        int numThreads = scanner.nextInt();
        numThreads = Math.max(1, Math.min(numThreads, 10)); // Limit between 1 and 10

        System.out.print("Enter the maximum password length (0 for infinite): ");
        int maxPasswordLength = scanner.nextInt();
        System.out.println("Starting password search with " + numThreads + " threads.");

        long startTime = System.currentTimeMillis(); // Start time in milliseconds

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadID = i;  // assign a final ID for each thread
            threads[i] = new Thread(() -> searchPassword(targetHash, threadID, maxPasswordLength), "Thread-" + threadID);
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis(); // End time in milliseconds
        long elapsedTime = endTime - startTime; // Elapsed time in milliseconds

        // Convert elapsed time to a readable format
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;

        if (isFound) {
            System.out.println("Password found: " + foundPassword);
        } else {
            System.out.println("Password not found.");
        }

        // Display elapsed time
        System.out.printf("Time spent: %02d:%02d:%02d\n", hours, minutes, seconds);
    }

    // Method for each thread to search for the password
    private static void searchPassword(String targetHash, int threadID, int maxPasswordLength) {
        StringBuilder charsetBuilder = new StringBuilder();
        // Generate all printable ASCII characters from 32 to 126
        for (int i = 32; i <= 126; i++) {
            charsetBuilder.append((char) i);
        }
        String charset = charsetBuilder.toString(); // using extended character set

        int length = 1; // Start from length 1
        while (!isFound && (maxPasswordLength == 0 || length <= maxPasswordLength)) {
            generateCombinations(charset, "", length, targetHash);
            length++; // Increase length for the next iteration
        }
    }

    // Method to generate combinations of characters
    private static void generateCombinations(String charset, String prefix, int length, String targetHash) {
        if (length == 0) {
            checkPassword(prefix, targetHash);
            return;
        }

        for (int i = 0; i < charset.length(); i++) {
            if (isFound) return; // Exit if password found
            generateCombinations(charset, prefix + charset.charAt(i), length - 1, targetHash);
        }
    }

    // Method to check MD5 hash
    private static void checkPassword(String candidate, String targetHash) {
        if (getMd5(candidate).equals(targetHash)) {
            synchronized (MD5Crack.class) { // synchronize to prevent race conditions
                if (!isFound) { // check again to avoid duplicate messages
                    isFound = true;
                    foundPassword = candidate;
                    System.out.println("Thread Name: " + Thread.currentThread().getName() +
                                       ", Password: " + foundPassword +
                                       ", Time: " + new Timestamp(System.currentTimeMillis()));
                }
            }
        }
    }

    // MD5 Hashing function
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
