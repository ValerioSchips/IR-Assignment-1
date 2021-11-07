package org.IR.ass1;
import java.util.Scanner;



public class Controller {
    public static String docsFolder = "Docs_large"; // Selection of the folder we want work with (small or large)
    public static String generalPath = "~/Information Retrivial/Ass1/Docs/"+docsFolder+"/";
    public static String indexDirectory = generalPath +"Index_n"; // Path to the folder of the index
    public static String dirToBeIndexed = generalPath + "full_docs";  // Path to the folder containing files to be indexed
    public static String queryPath = generalPath + "main_queries.csv"; // Path to the queries file
    public static String resultJsonPath = generalPath + "answer_main.json"; // Path where the queries result will be saved
    public static Float k1 = (float) 2.0; // parameter for BM25Sim
    public static Float b = (float) 0.6; // parameter for BM25Sim

    public static void main(String[] args) throws Exception {
        int end = 0;
        while (end != 1){
            System.out.println("Press:\n0 - to start search\n9 - to start indexing\n1 - to close");
            Scanner sc = new Scanner(System.in);
            end = sc.nextInt();
            int i = end;
            if (i == 0){
                query();
            }else if(i!=1){
                indexing();
            }
        }
    }

    public static void indexing() throws Exception {
        new Indexer();
    }

    public static void query() throws Exception {
        new Searcher();
    }
}
