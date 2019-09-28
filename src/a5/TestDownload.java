package a5;
// For week 5
// sestoft@itu.dk * 2014-09-19

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class TestDownload {
  private static final ExecutorService executor
          = Executors.newWorkStealingPool();

  private static final String[] urls = 
  { "http://www.itu.dk", "http://www.di.ku.dk", "http://www.miele.de",
    "http://www.ku.dk", "http://www.sspai.com", "http://www.ubi.com",
    "http://www.douban.com", "http://www.weibo.com", "http://www.google.com",
    "http://www.bilibili.com", "http://www.dtu.dk", "http://www.stackoverflow.com",
    "http://www.bbc.co.uk", "http://www.steamcommunity.com", "http://www.sony.co.jp",
    "http://www.linecorp.com", "http://www.battle.net", "http://www.heise.de", "http://www.wsj.com",
    "http://www.bbc.co.uk", "http://www.dsb.dk", "http://www.bmw.com", "https://www.spotify.com"
  };

  public static void main(String[] args) throws IOException {
//    String url = "https://www.wikipedia.org/";
//    String page = getPage(url, 10);
//    System.out.printf("%-30s%n%s%n", url, page);
    double[] timeRecord = new double[5];
    for(int i =0; i<5;i++) {
      Timer timer = new Timer();
      Map<String, String> pagesMap = getPagesN(urls, 200);
      double runTime = timer.check();
      System.out.println(runTime);
    }
//    Stream.of(timeRecord).forEach(System.out::println);


//    Map<String, String> pagesMapN = getPagesN(urls,200);

//    double runTime = timer.check();
//    System.out.println(runTime);

//+" (N): "+pagesMapN.get(url).length()
//    Stream.of(urls).map(url -> url+" : "+pagesMap.get(url).length()).forEach(System.out::println);
  }

  public static String getPage(String url, int maxLines) throws IOException {
    // This will close the streams after use (JLS 8 para 14.20.3):
    try (BufferedReader in 
         = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
      StringBuilder sb = new StringBuilder();
      for (int i=0; i<maxLines; i++) {
        String inputLine = in.readLine();
        if (inputLine == null)
          break;
        else
          sb.append(inputLine).append("\n");
      }
      return sb.toString();
    }
  }

  public static Map<String,String> getPages(String[] urls, int maxLines) throws IOException {
    Map<String,String> result = new HashMap<>();
    for(String url: urls){
      BufferedReader in
              = new BufferedReader(new InputStreamReader(new URL(url).openStream())) ;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<maxLines; i++) {
          String inputLine = in.readLine();
          if (inputLine == null)
            break;
          else
            sb.append(inputLine).append("\n");
        }
        result.put(url,sb.toString());
      }
    return result;
    }

  public static Map<String,String> getPagesN(String[] urls, int maxLines) throws IOException {
    final Map<String,String> result = new HashMap<>();
    List<Callable<UrlContent>> tasks = new ArrayList<Callable<UrlContent>>();
    for(String _url:urls){
      final String url = _url;
      tasks.add(()->{
        UrlContent uc = new UrlContent(url);
        BufferedReader in
                = new BufferedReader(new InputStreamReader(new URL(url).openStream())) ;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<maxLines; i++) {
          String inputLine = in.readLine();
          if (inputLine == null)
            break;
          else
            sb.append(inputLine).append("\n");
        }
        uc.setContent(sb.toString());
        return  uc;
      });
    }
    try {
      List<Future<UrlContent>> futures = executor.invokeAll(tasks);
      for (Future<UrlContent> fut : futures)
        result.put(fut.get().url,fut.get().content);
    } catch (InterruptedException exn) {
      System.out.println("Interrupted: " + exn);
    } catch (ExecutionException exn) {
      throw new RuntimeException(exn.getCause());
    }
    return result;
  }

}
class UrlContent{
  final String url;
  String content;

  UrlContent(String url) {
    this.url = url;
  }
  void setContent(String content){
    this.content = content;
  }
}




