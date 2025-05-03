// Main.java - TreeNode tabanlı parser ile tüm inputları işler, doğru parse tree'yi yazdırır
//Yusuf Buğra KILIÇ- B231202355
//Kayra Kaan KABAKÇIOĞLU- B231202350
//Dilay Gülru ÖZAK- B231202371
package parser;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String actionFile = "ActionTable.txt";
        String gotoFile = "GotoTable.txt";
        String grammarFile = "Grammar.txt";

        for (int i = 1; i <= 9; i++) {
            String inputFile = "input" + i + ".txt";
            Parser parser = new Parser(actionFile, gotoFile, grammarFile);

            List<String> tokens = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    Main.class.getClassLoader().getResourceAsStream(inputFile)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    tokens.addAll(Arrays.asList(line.trim().split("\\s+")));
                }
            }

            List<String> result = parser.parse(tokens);
//hedef dosyanın konumu
            File outputDir = new File("target/output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
//dosyayı yazar
            File outFile = new File(outputDir, "output" + i + ".txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
                for (String line : result) {
                    pw.println(line);
                }
            }
// target/Output/output.. konumuna bir txt dosyası olarak yazdırır.
            System.out.println("output" + i + ".txt written successfully. (target/Output/output"+i+")");
        }
    }
}