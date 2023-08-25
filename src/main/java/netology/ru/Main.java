package netology.ru;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static final BlockingQueue<String> queueOne = new ArrayBlockingQueue<>(100);
    public static final BlockingQueue<String> queueTwo = new ArrayBlockingQueue<>(100);
    public static final BlockingQueue<String> queueThree = new ArrayBlockingQueue<>(100);
    public static final int maxCount = 10_000;
    public static final int stringLength = 100_000;
    public static final String charString = "abc";

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger countA = new AtomicInteger();
        AtomicInteger countB = new AtomicInteger();
        AtomicInteger countC = new AtomicInteger();
        AtomicReference<String> maxA = new AtomicReference<>("");
        AtomicReference<String> maxB = new AtomicReference<>("");
        AtomicReference<String> maxC = new AtomicReference<>("");
        List<Thread> threads = new ArrayList<>();

        threads.add(new Thread(() -> {
            for (int i = 0; i < maxCount; i++) {
                String string = generateText(charString, stringLength);
                try {
                    queueOne.put(string);
                    queueTwo.put(string);
                    queueThree.put(string);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }));
        threads.add(new Thread(() -> countLogic('a', queueOne, countA, maxA)));
        threads.add(new Thread(() -> countLogic('b', queueTwo, countB, maxB)));
        threads.add(new Thread(() -> countLogic('c', queueThree, countC, maxC)));
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        printCharCount('a', countA, maxA);
        printCharCount('b', countB, maxB);
        printCharCount('c', countC, maxC);
    }

    private static void countLogic(char ch, BlockingQueue<String> queue, AtomicInteger charCount, AtomicReference<String> string) {
        for (int i = maxCount; i > 0; i--) {
            try {
                String s = queue.take();
                int thisCount = countChar(ch, s);
                if (thisCount > charCount.get()) {
                    charCount.set(thisCount);
                    string.set(s);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private static void printCharCount(char ch, AtomicInteger count, AtomicReference<String> s) {
        System.out.println("Наибольшее количество символов '" + ch + "': " + count + ". Строка: " + s.toString().substring(1, 100) + "...");
    }

    private static int countChar(char ch, String string) {
        int length = string.length();
        int count = 0;
        for (int i = 0; i < length; i++) {
            if (string.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}