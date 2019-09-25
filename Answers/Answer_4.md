# Exercise 4.1
1. code:

   ```java
   public FunList<T> remove (T x){
     return new FunList<T>(remove(x, this.first));
   }
   
   public static <T> Node<T> remove( T x, Node<T> xs){
     return xs==null? null:
     	(xs.item==x?remove(x,xs.next):new Node<T>(xs.item,remove(x,xs.next)));
   }
   ```

2. Code

   ```java
   public int count(Predicate<T> p){
     return count(p, this.first);
   }
   
   public static <T> int count(Predicate<T> p,Node<T> xs){
     int current = (p.test(xs.item))? 1:0 ;
     return xs.next==null? current : current+count(p, xs.next);
   }
   ```

3. Code:

   ```java
   public FunList<T> filter(Predicate<T> p){
     return new FunList<T>(filter(p, this.first));
   }
   public static <T> Node<T> filter(Predicate<T> p, Node<T> xs){
     return xs==null?null:
     	(p.test(xs.item)? new Node<T>(xs.item, filter(p,xs.next)) : filter(p,xs.next));
   }
   ```

   

4. Code:

   ```java
   public FunList<T> removeFun(T x){
     return this.filter(num ->  num==x? false:true);
   }
   ```

   

5. Code:

   ```java
   public static<T> FunList<T> flatten(FunList<FunList<T>> xss){
     return  new FunList<T>(flatten(xss.first.item.first,xss.first.next));
   }
   
   public static<T> Node<T> flatten(Node<T> xs, Node<FunList<T>> xss){
     return xs==null?
       ((xss==null)?null:new Node<T>(xss.item.first.item,flatten(xss.item.first,xss.next))):
     	new Node<T>(xs.item,flatten(xs.next,xss));
   }
   ```

6. Code:

   ```java
   public static<T> FunList<T> flattenFun(FunList<FunList<T>> xss){
     final FunList<T> newList= new FunList<T>(null);
     xss.reduce(newList,FunList::append);
     return newList;
   }
   ```

7. Code:

8. ```java
   //recursion implementation
   public <U> FunList<U> flatMap(Function<T,FunList<U>> f){
     return new FunList<U>(flatMap(f, this.first));
   }
   
   public static<T,U> Node<U> flatMap(Function<T,FunList<U>> f, Node<T> xs){
     if (xs == null){
       return null;
     }
     else{
       FunList<U> a = f.apply(xs.item);
       FunList.append(a.first,flatMap(f,xs.next));
       return a.first;
     }
   }
   // map and flatten implementation
   public <U> FunList<U> flatMapFun(Function<T,FunList<U>> f){
     return flatten(this.map(f));
   }
   ```

8. Code:

   ```java
   public FunList<T> scan(BinaryOperator<T> f){
     return new FunList<T>(scan(f, this.first, this.getCount()));
   }
   
   public Node<T> scan(BinaryOperator<T> f, Node<T> xs, int i){
     return  i==1? new Node<T>(xs.item,null): new Node<T>(xs.item,scan(f,xs.next,i-1));
   }
   ```

# Exercise 4.2

remove the `final` declaration of `next`, below the infNode is the start of infinite node list

```java
Node<Integer> infNode = new Node<Integer>(0,null);
final Node<Integer>[] nodeRecord = new Node[1];
nodeRecord[0] =infNode;
Stream.generate(()->{
  nodeRecord[0].next = new Node<Integer>(0,null);
  return nodeRecord[0]= nodeRecord[0].next;
});
```

# Exercise 4.3

1. Code:

   ```java
   private static int[] setArray(int len){
     int[] arr = new int[len];
     parallelSetAll(arr, index -> isPrime(index)?1:0);
     return arr;
   }
   
   private static boolean isPrime(int n) {
     int k = 2;
     while (k * k <= n && n % k != 0)
       k++;
     return n >= 2 && k * k > n;
   }
   ```

2.  

   ```java
    parallelPrefix(arr, (x,y)-> x+y);
   ```

   

3. Result:

    n =  1000000 ; ratio value = 1.084490 
   n =  2000000 ; ratio value = 1.080409 
   n =  3000000 ; ratio value = 1.077873 
   n =  4000000 ; ratio value = 1.076083 
   n =  5000000 ; ratio value = 1.075159 
   n =  6000000 ; ratio value = 1.073908 
   n =  7000000 ; ratio value = 1.073236 
   n =  8000000 ; ratio value = 1.072466 
   n =  9000000 ; ratio value = 1.071944 
   n = 10000000 ; ratio value = 1.071175 

   ```java
   for(int i = arr.length/10;i<=arr.length;i+=arr.length/10){
     System.out.printf("n = %8d ; ratio value = %f %n",i,arr[i]/(i*1.0/Math.log(i)));
   }
   ```

   

# Exercise 4.4

```java
//1.
public static Stream<String> readWords(String filename) {
  try {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    return reader.lines();
  } catch (IOException exn) {
    return Stream.<String>empty();
  }
}

//2.
readWords(filename).limit(100).forEach(System.out::println);

//3.
readWords(filename).filter(word ->word.length()>=22).forEach(System.out::println);

//4.
readWords(filename).filter(word ->word.length()>=22).limit(10).forEach(System.out::println);

//5.
public static boolean isPalindrome(String word) {
  for(int i =0 ; i < word.length(); i++){
    if (word.charAt(i)!= word.charAt(word.length()-1-i))
      return false;
  }
  return true;
}

readWords(filename).filter(word -> isPalindrome(word)).forEach(System.out::println);
//6.
readWords(filename).parallel()
  								 .filter(word -> isPalindrome(word))
  								 .forEach(System.out::println);

final BufferedReader reader = new BufferedReader(new FileReader(filename));
Mark7("sequential", i ->{
  reader.lines().filter(word -> isPalindrome(word));
  return filename.hashCode();
});
Mark7("parallel", i ->{
  reader.lines().parallel().filter(word -> isPalindrome(word));
  return reader.hashCode();
});
/**
Mark 7 is imported from previous exercise
sequential                           23.0 ns       0.50   16777216
parallel                             22.7 ns       0.58   16777216
Parallel is not faster than sequential. Problems like testing palindrome are small enough that they are in danger of being swamped by the parallel splitting overhead.
**/

//7.

IntStream iStream = readWords(filename).mapToInt(word -> word.length());
IntSummaryStatistics stats = iStream.summaryStatistics();

//8.
System.out.println(stats);
Map<Integer,List<String>> collectionWord = readWords(filename).collect(
  Collectors.groupingBy(String::length));

//9.
public static Map<Character,Integer> letters(String s) {
  final Map<Character,Integer> res = new TreeMap<>();
  // TO DO: Implement properly
  s = s.toLowerCase();
  s.chars().mapToObj(i -> (char)i).forEach(i->{
    res.putIfAbsent(i,0);
    res.put(i,res.get(i)+1);
  });
  return res;

Stream<Map<Character,Integer>> treeStream = readWords(filename).map(i->letters(i));
treeStream.limit(100).forEach(System.out::println);

//10.
Integer eCount = treeStream.map(i->i.get('e')==null?0:i.get('e')).reduce(0,Integer::sum);
  
//11.
  Map<Map<Character,Integer>,Set<String>> words= readWords(filename).collect(
    Collectors.groupingBy(s -> letters(s),Collectors.toSet())
  );
  
 //12.
    Map<Map<Character,Integer>,Set<String>> words= readWords(filename).collect(
    Collectors.ggroupingByConcurrent(s -> letters(s),Collectors.toSet())
  );
  //it's faster than sequential one

```



# Exercise 4.5

1. ` double sum = IntStream.rangeClosed(1,N).mapToDouble(i -> 1.0/i/i ).sum();`

2.  Sum =  -0.0000000010000001

   ```java
   final String s = "sequential", p = "parallel";
           Mark7(s, m->{
               double sum = IntStream.rangeClosed(1,N).mapToDouble(i -> 1.0/i/i ).sum();
               return s.hashCode();
           });
           Mark7(p, m->{
               double sum = IntStream.rangeClosed(1,N).mapToDouble(i -> 1.0/N/N).parallel().sum();
               return p.hashCode();
           });
   ```

   sequential                   9628019119.1 ns 375701188.40          2
   parallel                      901933954.6 ns 36621356.50          2

3.  Sum =  -0.0000000090136514

    classic loop sum             2656154510.0 ns 243396057.40          2

   ```java
   TestWordStream.Mark7("classic loop sum", item->{
     double sum_l = 0;
     for(double i =1; i<N+1;i++){
       sum_l+=1/i/i;
     }
     return s.hashCode();
   });
   ```

4. Sum =  -0.0000000010000001

   ```java
   int[] i = {0};
   double sum_gen = Stream.generate(()->++i[0])
     .mapToDouble(in>1./in/in).limit(N).sum();
   System.out.printf("Sum = %20.16f%n", sum_gen - Math.PI*Math.PI/6);
   
   //sequential                  12223739424.6 ns 113944575.55          2
   ```

5.  ` double sum_gen = Stream.generate(()->++i[0]).mapToDouble(in->1./in/in).limit(N).parallel().sum();`

   a few results:

   Sum =   0.1043992195961652
   Sum =   0.0108706588878920
   Sum =   0.0885867507185620
   Sum =   0.0015298871767406
   Sum =   1.8853207421691531
   Sum =   0.0024825149909982

   Gnerate creates an unordered infinite stream and limit() didn't provide size, it's not safe to make it parallel