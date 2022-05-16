package stockanalyzer.ctrl.downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// In Anlehnung an https://howtodoinjava.com/java/multi-threading/java-callable-future-example/
// sowie https://www.callicoder.com/java-callable-and-future-tutorial/
// und https://jenkov.com/tutorials/java-util-concurrent/executorservice.html



public class ParallelDownloader extends Downloader{
    @Override
    public int process(List<String> tickers) {

        int count = 0;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        List<Future<String>> resultList = new ArrayList<>();

        for (String ticker: tickers)
        {
            Future<String> result = executor.submit(() -> {
                return saveJson2File(ticker);
            });
            resultList.add(result);
        }


        //Test
        for(Future<String> result : resultList)
        {
            try
            {
                System.out.println("Test:" + result.get() + " Task done" + result.isDone());
                count++;
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        return 0;

    }
}
