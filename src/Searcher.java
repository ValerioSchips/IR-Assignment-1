package org.IR.ass1;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import com.fasterxml.jackson.databind.ObjectMapper;



import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.*;

public class Searcher {
    private static String generalPath = Controller.generalPath;
    private static String indexDirectory = Controller.indexDirectory;
    private static String queryPath = Controller.queryPath;
    private static String resultJsonPath = Controller.resultJsonPath;
    public int founded = 0;

    /**
     * After the parameter are set in the controller and the index is created this class perform the search
     * @throws Exception
     */
    public Searcher() throws Exception {
        //Instantiate IndexSearcher and QueryParse which are going to be passed as parameter
        File indexDir = new File(indexDirectory);
        Directory directory = FSDirectory.open(indexDir.toPath());
        IndexReader  indexReader  = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        searcher.setSimilarity(new BM25Similarity(Controller.k1, Controller.b));
        Analyzer analyzer = new EnglishAnalyzer();
        QueryParser parser = new QueryParser("content", analyzer);

        searchAndWrite(searcher, parser);
    }

    /**
     * Perform the search and writes the result in a json file
     * @param indexSearcher IndexSearcher
     * @param qParser QueryParser
     * @throws Exception
     */
    public void searchAndWrite(IndexSearcher indexSearcher, QueryParser qParser) throws Exception {
        List<Map<Integer, String>> queryAnswerCSV = ReadFile.readCSV(queryPath, "\t"); // Reads the query csv file

        Map<Integer, String> queryCSV = queryAnswerCSV.get(0);
        Map<Integer, Map <Integer, Double>> finalResult = new HashMap<>(); // Will keep in memory the result of each query

        int numExecutedQuery = 0;
        for (Integer queryNum: queryCSV.keySet()){ //Scroll each query

            // Query is performed and then inserted in finalResult dict
            Map<Integer, Map <Integer, Double>> queryRes = this.execQuery(qParser, indexSearcher, queryNum, queryCSV.get(queryNum), 10);
            finalResult.putAll(queryRes);
            numExecutedQuery += 1;
            System.out.println(String.valueOf(numExecutedQuery));
        }

        // Writes the query result on a json file
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(finalResult);
        ReadFile.createFileAndWrite(resultJsonPath, json);
    }

    /**
     * Convert ScoreDoc[] to Map <Doc_num, Score>
     * @param queryRes ScoredDoc array
     * @param topNDoc Number of wanted top documents
     * @param searcher IndexSearcher
     * @return Convert the doc list from array to a map
     * @throws Exception
     */
    private Map <Integer, Double> getMaxScoredDoc(ScoreDoc[] queryRes, int topNDoc, IndexSearcher searcher) throws Exception {
        Map <Integer, Double> tmpDocMap = new HashMap<>();
        for (int x = 0; x < topNDoc; x++){
            try {
                if (queryRes[x] == null) {
                    continue;
                }
            }catch(Exception e) {
                break;
            }
            double docScore = 0;
            int docIdMaxC = 0;
            int docIdIndex = 0;


            docScore = queryRes[x].score;
            docIdIndex = x;
            docIdMaxC = Integer.parseInt(this.getDocFileName(queryRes[x].doc, searcher));

            queryRes[docIdIndex] = null;
            tmpDocMap.put(docIdMaxC, docScore);
            System.out.println(docScore);
        }
        return tmpDocMap;
    }


    /**
     * Execute the queryStr and then saves the result in a dictionary with <queryNum, <docNum, score>>
     * @param qParser QueryParser
     * @param searcher IndexSearcher
     * @param queryNum ID of the queryStr
     * @param queryStr String of the query
     * @param topNDoc Number of wanted top documents
     * @return documents result of the query with scores
     * @throws Exception
     */
    private Map <Integer, Map<Integer, Double>> execQuery(QueryParser qParser, IndexSearcher searcher, int queryNum, String queryStr, int topNDoc) throws Exception {
        ScoreDoc[] scoredDocs = this.searchQuery(queryStr, qParser, searcher, topNDoc);
        Map <Integer, Map<Integer, Double>> rankedQueryRes = new HashMap<>();
        rankedQueryRes.put(queryNum, getMaxScoredDoc(scoredDocs, topNDoc, searcher));
        return rankedQueryRes;
    }

    /**
     * Execute the search and return the topN scored doc
     * @param queryStr String of the query
     * @param qParser QueryParser
     * @param searcher IndexSearcher
     * @param topNDoc Number of wanted top documents
     * @return ScoredDoc results of the query
     * @throws Exception
     */
    private ScoreDoc[] searchQuery(String queryStr, QueryParser qParser, IndexSearcher searcher, int topNDoc)
            throws Exception {
        Query parser = qParser.parse(QueryParser.escape(queryStr));
        if (parser == null){
            return new ScoreDoc[0];
        }
        TopDocs topDocs =searcher.search(parser, topNDoc);
        ScoreDoc[] hits = topDocs.scoreDocs;
        return hits;
    }

    /**
     * Returns the doc fileName
     * @param docId
     * @param searcher
     * @return doc filename
     * @throws IOException
     */
    private String getDocFileName(int docId, IndexSearcher searcher) throws IOException {
        Document d = searcher.doc(docId);
        return d.get("fileName");
    }




}
