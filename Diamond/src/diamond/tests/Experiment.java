package diamond.tests;

import java.io.File;

import diamond.managers.NativeStoreStorageManager;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;

public class Experiment {

    private static final String FS = File.separator;
    private static final String DIR_QUERIES = "test" + FS + "Berlin-Benchmark" + FS + "queries" + FS;
    private static final String DIR_DATA = "test" + FS + "Berlin-Benchmark" + FS;
    
    public static void main(String[] args) {
        
        String query1 = DIR_QUERIES + "query01.rq";
        String query2 = DIR_QUERIES + "query02.rq";
        String query3 = DIR_QUERIES + "query03.rq";
        String query4 = DIR_QUERIES + "query04.rq";
        String query5 = DIR_QUERIES + "query05.rq";
        String query6 = DIR_QUERIES + "query06.rq";
        String query7 = DIR_QUERIES + "query07.rq";
        String query8 = DIR_QUERIES + "query08.rq";
        String query9 = DIR_QUERIES + "query09.rq";
        String query10 = DIR_QUERIES + "query10.rq";
        String query11 = DIR_QUERIES + "query11.rq";
        
        String dataScale2 = DIR_DATA + "2pc_2203_triples.nt";
        String dataScale4 = DIR_DATA + "4pc_2895_triples.nt";
        String dataScale8 = DIR_DATA + "8pc_4299_triples.nt";
        String dataScale16 = DIR_DATA + "16pc_7086_triples.nt";
        String dataScale32 = DIR_DATA + "32pc_14187_triples.nt";
        String dataScale64 = DIR_DATA + "64pc_27693_triples.nt";
        String dataScale128 = DIR_DATA + "128pc_50241_triples.nt";
        String dataScale256 = DIR_DATA + "256pc_100426_triples.nt";
        String dataScale512 = DIR_DATA + "512pc_195751_triples.nt";
        String dataScale1024 = DIR_DATA + "1024pc_383335_triples.nt";
        
        experiment(query1, dataScale2); // warm-up
        
        experiment(query1, dataScale2);
        experiment(query1, dataScale4);
        experiment(query1, dataScale8);
        experiment(query1, dataScale16);
        experiment(query1, dataScale32);
        experiment(query1, dataScale64);
        experiment(query1, dataScale128);
        experiment(query1, dataScale256);
        experiment(query1, dataScale512);
        experiment(query1, dataScale1024);
        
        experiment(query2, dataScale2);
        experiment(query2, dataScale4);
        experiment(query2, dataScale8);
        experiment(query2, dataScale16);
        experiment(query2, dataScale32);
        experiment(query2, dataScale64);
        experiment(query2, dataScale128);
        experiment(query2, dataScale256);
        experiment(query2, dataScale512);
        experiment(query2, dataScale1024);
        
        experiment(query3, dataScale2);
        experiment(query3, dataScale4);
        experiment(query3, dataScale8);
        experiment(query3, dataScale16);
        experiment(query3, dataScale32);
        experiment(query3, dataScale64);
        experiment(query3, dataScale128);
        experiment(query3, dataScale256);
        experiment(query3, dataScale512);
        experiment(query3, dataScale1024);
        
        experiment(query4, dataScale2);
        experiment(query4, dataScale4);
        experiment(query4, dataScale8);
        experiment(query4, dataScale16);
        experiment(query4, dataScale32);
        experiment(query4, dataScale64);
        experiment(query4, dataScale128);
        experiment(query4, dataScale256);
        experiment(query4, dataScale512);
        experiment(query4, dataScale1024);
        
        experiment(query5, dataScale2);
        experiment(query5, dataScale4);
        experiment(query5, dataScale8);
        experiment(query5, dataScale16);
        experiment(query5, dataScale32);
        experiment(query5, dataScale64);
        experiment(query5, dataScale128);
        experiment(query5, dataScale256);
        experiment(query5, dataScale512);
        experiment(query5, dataScale1024);
        
        experiment(query6, dataScale2);
        experiment(query6, dataScale4);
        experiment(query6, dataScale8);
        experiment(query6, dataScale16);
        experiment(query6, dataScale32);
        experiment(query6, dataScale64);
        experiment(query6, dataScale128);
        experiment(query6, dataScale256);
        experiment(query6, dataScale512);
        experiment(query6, dataScale1024);
        
        experiment(query7, dataScale2);
        experiment(query7, dataScale4);
        experiment(query7, dataScale8);
        experiment(query7, dataScale16);
        experiment(query7, dataScale32);
        experiment(query7, dataScale64);
        experiment(query7, dataScale128);
        experiment(query7, dataScale256);
        experiment(query7, dataScale512);
        experiment(query7, dataScale1024);
        
        experiment(query8, dataScale2);
        experiment(query8, dataScale4);
        experiment(query8, dataScale8);
        experiment(query8, dataScale16);
        experiment(query8, dataScale32);
        experiment(query8, dataScale64);
        experiment(query8, dataScale128);
        experiment(query8, dataScale256);
        experiment(query8, dataScale512);
        experiment(query8, dataScale1024);
        
        experiment(query9, dataScale2);
        experiment(query9, dataScale4);
        experiment(query9, dataScale8);
        experiment(query9, dataScale16);
        experiment(query9, dataScale32);
        experiment(query9, dataScale64);
        experiment(query9, dataScale128);
        experiment(query9, dataScale256);
        experiment(query9, dataScale512);
        experiment(query9, dataScale1024);
        
        experiment(query10, dataScale2);
        experiment(query10, dataScale4);
        experiment(query10, dataScale8);
        experiment(query10, dataScale16);
        experiment(query10, dataScale32);
        experiment(query10, dataScale64);
        experiment(query10, dataScale128);
        experiment(query10, dataScale256);
        experiment(query10, dataScale512);
        experiment(query10, dataScale1024);
        
        experiment(query11, dataScale2);
        experiment(query11, dataScale4);
        experiment(query11, dataScale8);
        experiment(query11, dataScale16);
        experiment(query11, dataScale32);
        experiment(query11, dataScale64);
        experiment(query11, dataScale128);
        experiment(query11, dataScale256);
        experiment(query11, dataScale512);
        experiment(query11, dataScale1024);
    }

    private static double experiment(String queryFileName, String dataFileName) {
        double averageExecutionTimeToReturn = 0;
        for(int i = 0; i < 13; i++) {
            try {
                QueryProcessor queryProcessor = new FileQueryProcessor(new File(queryFileName), false);
                queryProcessor.process();
                NativeStoreStorageManager nativeStore = new NativeStoreStorageManager(queryProcessor,
                        new File(dataFileName));
                if(i < 3) continue; // warm-up
                averageExecutionTimeToReturn += nativeStore.executeQuery(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        averageExecutionTimeToReturn /= 10;
        System.out.println("Average Runtime for Query " + queryFileName + " on dataFile " + dataFileName + " is "
                + averageExecutionTimeToReturn + " seconds.");
        return averageExecutionTimeToReturn;
    }
}
