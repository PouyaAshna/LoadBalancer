package org.example.loadbalancer.util;

public class PrinterUtil {

    private PrinterUtil() {
    }

    public static void print(String message, Object... args) {
        System.out.println("------------------------------");
        System.out.printf(message, args);
        System.out.println();
    }
}
