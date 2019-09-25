package a4;// Week 3
// sestoft@itu.dk * 2015-09-09

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestWordStream {
  public static void main(String[] args) throws FileNotFoundException {
    final String filename = "/usr/share/dict/words";



//    readWords(filename).limit(10).forEach(System.out::println);
//    readWords(filename).filter(word -> word.length()>=22).limit(10).forEach(System.out::println);
//
//
//    readWords(filename).filter(word -> isPalindrome(word)).forEach(System.out::println);
//    readWords(filename).parallel().filter(word -> isPalindrome(word)).forEach(System.out::println);

//    final BufferedReader reader = new BufferedReader(new FileReader(filename));
//    Mark7("sequential", i ->{
//      reader.lines().filter(word -> isPalindrome(word));
//      return filename.hashCode();
//    });
//    Mark7("parallel", i ->{
//      reader.lines().parallel().filter(word -> isPalindrome(word));
//      return filename.hashCode();
//    });

    //find min, max, avg
//    IntStream intStream = readWords(filename).mapToInt(word -> word.length());
//    IntSummaryStatistics stats = intStream.summaryStatistics();
//
//    System.out.println(stats);

//    Map<Integer,List<String>> collectionWord = readWords(filename).collect(
//            Collectors.groupingBy(String::length));

//    Map<Character,Integer> letterCount = readWords(filename).map(String::toLowerCase)
//                                                        .flatMap(s -> s.chars().mapToObj(i -> (char)i)).parallel()
//                                                        .collect(Collectors.groupingBy(i->i,Collectors.summingInt(i->1)));

//    Map<Character,Integer> a = letters("Persistent");
//    Stream<Map<Character,Integer>> treeStream = readWords(filename).map(i->letters(i));
//    treeStream.limit(100).forEach(System.out::println);

//
//    Integer eCount = treeStream.map(i->i.get('e')==null?0:i.get('e')).reduce(0,Integer::sum);
//    System.out.println(eCount);


    Map<Map<Character,Integer>,Set<String>> words= readWords(filename).collect(
            Collectors.groupingByConcurrent(s -> letters(s),Collectors.mapping(s->s,Collectors.toSet()))
    );



        System.out.println((int) 'z');
  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      // TO DO: Implement properly
      return reader.lines();
    } catch (IOException exn) {
      return Stream.<String>empty();
    }
  }



  public static boolean isPalindrome(String word) {
    for(int i =0 ; i < word.length(); i++){
      if (word.charAt(i)!= word.charAt(word.length()-1-i))
        return false;
    }
    return true;
  }

  public static Map<Character,Integer> letters(String s) {
    final Map<Character,Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    s = s.toLowerCase();
    s.chars().mapToObj(i -> (char)i).forEach(i->{
      res.putIfAbsent(i,0);
      res.put(i,res.get(i)+1);
    });
    return res;
  }

  public static double Mark7(String msg, IntToDoubleFunction f) {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do {
//        System.out.println(count);
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++)
          dummy += f.applyAsDouble(i);
        runningTime = t.check();
        double time = runningTime * 1e9 / count; // nanoseconds
        st += time;
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
    System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
    return dummy / totalCount;
  }
}
class Timer {
  private long start = 0, spent = 0;
  public Timer() { play(); }
  public double check() { return (start==0 ? spent : System.nanoTime()-start+spent)/1e9; }
  public void pause() { if (start != 0) { spent += System.nanoTime()-start; start = 0; } }
  public void play() { if (start == 0) start = System.nanoTime(); }
}
