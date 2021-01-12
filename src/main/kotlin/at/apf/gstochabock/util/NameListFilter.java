package at.apf.gstochabock.util;

import java.io.*;
import java.util.Scanner;

public class NameListFilter {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("NameListFilter {inputPath} {outputPath}");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        try {
            Scanner scanner = new Scanner(new FileInputStream(new File(inputPath)));
            PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(outputPath)));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase();
                if (urlConform(line)) {
                    printer.println(line);
                }
            }
        } catch (IOException e) {

        }
    }

    private static boolean urlConform(String str) {
        if (str.length() < 3) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < 'a' || str.charAt(i) > 'z') {
                return false;
            }
        }
        return true;
    }

}
