package common.functions;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class UploadCSV {
    public void extractingData(ArrayList<String> scrapeTopics, String fileName) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("./" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.useDelimiter(",");
        while (scanner.hasNext()) {
            scrapeTopics.add(scanner.next());
        }
        scanner.close();
    }
}
