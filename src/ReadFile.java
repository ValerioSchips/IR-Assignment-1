package org.IR.ass1;

import java.io.*;
import java.util.*;

public class ReadFile {


    /**
     * Read Queries CSV and return a map with the queries enumerated
     * @param pathToCsv path to the queries file
     * @param sep separator used in the csv file
     * @return map of queries enumerated
     * @throws IOException
     */
    public static List<Map<Integer, String>> readCSV(String pathToCsv, String sep) throws IOException {
        if (sep == null){
            sep = "\\s+";
        }
        Map<Integer, String> csvFile1 = new HashMap<>(); //Will save Query_num - Query association
        Map<Integer, String> csvFile2 = new HashMap<>(); //If present will save Query_num - Result Association
        List<Map<Integer, String>> retValue = new ArrayList<>();
        BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
        String row = csvReader.readLine();
        while (row != null) {
            String[] data = row.split(sep);
            try{
                Integer.valueOf(data[0]);
            }catch (Exception e){
                row = csvReader.readLine();
                continue;
            }
            csvFile1.put( Integer.valueOf(data[0]), String.valueOf(data[1]));
            try{
                csvFile2.put( Integer.valueOf(data[0]), String.valueOf(data[2]));
            }catch(Exception e) {
                csvFile2.put( Integer.valueOf(data[0]), String.valueOf(0));
            }
            row = csvReader.readLine();
        }
        retValue.add(csvFile1);
        retValue.add(csvFile2);
        csvReader.close();
        return retValue;
    }

    /**
     * Used to read the files to be indexed
     * @param file File to be read
     * @return file as string
     */
    public String readFile(File file) {
        String readedFile = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                readedFile = readedFile + data;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readedFile;
    }

    /**
     * Creates a json file and writes on it the search result
     * @param pathToWrite path where to locate the file
     * @param jsonFile String to write, formatted as json
     */
    public static void createFileAndWrite(String pathToWrite, String jsonFile){
        try {
            File myObj = new File(pathToWrite);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(pathToWrite);
            myWriter.write(jsonFile);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
