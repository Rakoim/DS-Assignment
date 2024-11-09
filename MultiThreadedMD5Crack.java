import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.*;

public class MultiThreadedMD5Crack {
    private static boolean isFound = false; // To track if the password is found
    private static String targetHash;
    private static long startTime;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt for MD5 hash input
        while (true) {
            System.out.print("Enter MD5 hash to crack: ");
            targetHash = scanner.nextLine().trim();
            if (isValidMD5(targetHash)) {
                break;
            } else {
                System.out.println("Invalid MD5 hash. Please enter a valid 32-character hexadecimal string.");
            }
        }

        int numThreads;
        // Prompt for number of threads
        while (true) {
            System.out.print("Enter number of threads (1 to 10): ");
            numThreads = scanner.nextInt();
            scanner.nextLine(); // consume newline character
            if (numThreads >= 1 && numThreads <= 10) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a thread count between 1 and 10.");
            }
        }

        startTime = System.currentTimeMillis();
        System.out.println("Starting search with " + numThreads + " threads...");

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Attempt passwords from length 3 to 6
        for (int length = 3; length <= 6 && !isFound; length++) {
            int range = 94 / numThreads;
            for (int i = 0; i < numThreads; i++) {
                int start = 33 + i * range;
                int end = (i == numThreads - 1) ? 126 : start + range - 1;
                executor.submit(new MD5CrackWorker(start, end, length, i + 1));
            }
        }

        executor.shutdown();
        // Wait for threads to complete
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                System.out.println("Timeout reached before completion.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (!isFound) {
            System.out.println("Password not found.");
        }
    }

    private static boolean isValidMD5(String hash) {
        return hash.matches("^[a-fA-F0-9]{32}$");
    }

    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static class MD5CrackWorker implements Runnable {
        private final int start;
        private final int end;
        private final int length;
        private final int threadId;

        MD5CrackWorker(int start, int end, int length, int threadId) {
            this.start = start;
            this.end = end;
            this.length = length;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            crackPassword(new char[length], 0);
        }

        private synchronized void crackPassword(char[] chars, int pos) {
            if (pos == chars.length) {
                String attempt = new String(chars);
                if (getMd5(attempt).equals(targetHash) && !isFound) {
                    long endTime = System.currentTimeMillis();
                    isFound = true; // Set password found flag
                    System.out.println("Thread ID: " + threadId);
                    System.out.println("Password: " + attempt);
                    System.out.println("Time taken: " + (endTime - startTime) / 1000.0 + " seconds");
                }
                return;
            }

            // Try characters from 'start' to 'end' for each position
            for (int i = (pos == 0 ? start : 33); i <= (pos == 0 ? end : 126) && !isFound; i++) {
                chars[pos] = (char) i;
                crackPassword(chars, pos + 1);
            }
        }
    }
}