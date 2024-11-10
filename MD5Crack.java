public class MD5Crack implements Runnable {
    private final int start;
    private final int end;
    private final int length;
    private final int threadId;

    MD5Crack(int start, int end, int length, int threadId) {
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
            if (Main.getMd5(attempt).equals(Main.getTargetHash()) && !Main.isFound()) {
                long endTime = System.currentTimeMillis();
                Main.setFound(true); // Set password found flag
                System.out.println("Thread ID: " + threadId);
                System.out.println("Password: " + attempt);
                System.out.println("Time taken: " + (endTime - Main.getStartTime()) / 1000.0 + " seconds");
            }
            return;
        }

        // Try characters from 'start' to 'end' for each position
        for (int i = (pos == 0 ? start : 33); i <= (pos == 0 ? end : 126) && !Main.isFound(); i++) {
            chars[pos] = (char) i;
            crackPassword(chars, pos + 1);
        }
    }
}
