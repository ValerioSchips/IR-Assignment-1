package org.IR.ass1;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.io.File;

public class Indexer {

    private static String indexDirectory = Controller.indexDirectory;
    private static String dirToBeIndexed = Controller.dirToBeIndexed;

    /**
     * After the parameter are set in the controller this class perform the file indexing
     * @throws IOException
     */
    public Indexer() throws IOException {
        File indexDir = new File(indexDirectory);
        File dataDir = new File(dirToBeIndexed);
        int numIndexed = this.index(indexDir, dataDir);
        System.out.println("Total files indexed " + numIndexed);
    }

    /**
     * Perform the indexing
     * @param indexDir File Stream to the folder which will contain the index
     * @param dataDir File Stream to the folder which contain the files to index
     * @return The number of indexed documents
     * @throws IOException
     */
    private int index(File indexDir, File dataDir) throws IOException {
        Analyzer analyzer = new EnglishAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setSimilarity(new BM25Similarity(Controller.k1, Controller.b));
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(indexDir.toPath()), config);
        File[] files = dataDir.listFiles();

        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);

        int counter = 0;
        for (File f : files) {
            Document doc = new Document();
            doc.add(new Field("content", new ReadFile().readFile(f), type));
            // extract document number from the document path and saves it in the fileName
            String fileName = f.getName().split("/")[(f.getName().split("/").length)-1].replaceAll("[^0-9]", "");
            doc.add(new StoredField("fileName", fileName));
            doc.add(new StoredField("path", f.getCanonicalPath()));
            indexWriter.addDocument(doc);
            counter +=1;
            System.out.println(counter);
        }

        int numIndexed = indexWriter.maxDoc();
        indexWriter.close();
        return numIndexed;
    }
}
