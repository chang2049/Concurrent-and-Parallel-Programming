# Exercise 2.1

1. K starts at 2 and is checked if number is divisible by K. If it's divisible, the number will be divided by k continuously until it's not and increment k by 1, which guarantees that k is a prime number when it's divisible. 

   The bound on the running time: $\sqrt{p}$ , when p is a large prime number

2. It takes 0m7.274s

3.  

4.  MyAtomicInteger class:

   ```java
   class MyAtomicInteger{
     private int count = 0;
     public synchronized int addAndGet(int amount){
       count += amount;
       return count;
     }
     public synchronized int get(){
       return count;
     }
   }
   ```

5.  running the following programe by System.out.println(countPrimeFactorsParallel(5_000_000, 10));

   get the result 18703729 and takes  0m0.962s

   ```java
   public static int countPrimeFactorsParallel(int number, int n) {
     final MyAtomicInteger count = new MyAtomicInteger();
     List<Thread> threads = new ArrayList<>();
     for(int i =0; i<n;i++){
       final int ti = i;
       threads.add(new Thread(()->{
         for(int v = number/n*ti;
             v<(number/n*(ti+1)>number?number:number/n*(ti+1));v++){
           count.addAndGet(countPrimeFactors(v));
         }
       }));
       threads.get(threads.size()-1).start();
     }
     for (Thread t : threads) {
       try {
         t.join();
       } catch (InterruptedException e) {
         e.printStackTrace();
       }
     }
     return count.get();
   }
   ```

6. No. It has to be synchronized as multiple threads will read and write count

7. Same result without much difference in time. It should be declared as final

# Exercise 2.2

1. It won't work if volatile is removed, as volatile guarantees the visibility to all threads, which means other threads will always get the updated cache while one thread is writing cache.
2. final cannot be removed, final decalration makes sure the object is immutable once the instance is constructed

# Exercise 2.3

1. `increment` and `getCount` must be modified as synchronized to guarantee thread-safety and visibility between multiple threads.  counts can be declared as final.  `getSpan` doesn't have to be synchronized because the length of counts won't be changed once being constructed.

   ```java
   class Histogram2 implements Histogram{
       final private  int[] counts;
   
       Histogram2(int span) { this.counts = new int[span]; }
   
       public synchronized void increment(int bin) { counts[bin] =  counts[bin] + 1; }
   
       public synchronized int getCount(int bin) { return counts[bin]; }
   
       public int getSpan() { return counts.length; }
   }
   ```

2.  Code:

   ```java
       private static void countParallelN(int range, int threadCount){
           final int perThread = range / threadCount;
           final Histogram2 his = new Histogram2(range);
           Thread[] threads = new Thread[threadCount];
           for (int t = 0; t < threadCount; t++) {
               final int from = perThread * t,
                       to = (t + 1 == threadCount) ? range : perThread * (t + 1);
               threads[t] = new Thread(() -> {
                   for (int i = from; i < to; i++){
                       his.increment(TestCountFactors.countPrimeFactors(i));
                   }
               });
           }
           for (int t = 0; t < threadCount; t++)
               threads[t].start();
           try {
               for (int t = 0; t < threadCount; t++)
                   threads[t].join();
           } catch (InterruptedException exn) {
           }
           for(int i = 0; i<10;i++){
               System.out.printf(i+": %10d%n%n", his.getCount(i));
           }
   
       }
   ```

3. the reuslt is correct. Synchronized can be removed, when multiple threads try to access the same bin Integer, the AtomicInteger will let one have the lock while others will be suspended.

   ```java
   class Histogram3 implements Histogram{
       final private AtomicInteger[] counts;
   
       Histogram3(int span) {
           this.counts = new AtomicInteger[span];
           for(int i = 0; i<span;i++){
               this.counts[i] = new AtomicInteger(0);
           }
       }
   
       public void increment(int bin) { counts[bin].getAndIncrement(); }
   
       public int getCount(int bin) { return counts[bin].get(); }
   
       public int getSpan() { return counts.length; }
   }
   ```

4. Result is correct

   ```java
   
   class Histogram4 implements Histogram{
       final private AtomicIntegerArray counts;
   
       Histogram4(int span) { counts = new AtomicIntegerArray(span); }
   
       public void increment(int bin) { counts.incrementAndGet(bin); }
   
       public int getCount(int bin) { return counts.get(bin); }
   
       public int getSpan() { return counts.length(); }
   }
   ```

5.  

   ```java
   @Histogram2 
   public synchronized int[] getBins(){
   	return counts.clone();
   }
   // To avoid escape of counts, getBins() return a copy of counts. Since getBins is sychronized, all the others thread trying to access this instance will be suspended. It gives a fixed snapshot
   
   @Histogram3
   public int[] getBins(){
     int[] countsCopy = new int[counts.length];
     for(int i = 0; i<counts.length;i++){
       countsCopy[i]= counts[i].get();
     }
     return countsCopy;
   }
   // In the process of copying counts to countCopy, the thread will hold a specific AtomicInteger object lock in each loop while all the other AtomicInteger objects are still accessible and edible to others threads. It gives a live view of the bins
   
   @Histogram4
   public int[] getBins(){
     int[] countsCopy = new int[counts.length()];
     for(int i = 0; i<counts.length();i++){
       countsCopy[i]= counts.get(i);
     }
     return countsCopy;
   }
   // It's very similar to Histogram3 in the copying.It also gives a live view of the bins
   ```

6.  

   ```java
   class Histogram5 implements Histogram{
       private final LongAdder[] counts;
   
       Histogram5(int span) {
           counts = new LongAdder[span];
           for(int i = 0; i<span;i++){
               counts[i] = new LongAdder();
           }
       }
   
       public void increment(int bin) { counts[bin].increment(); }
   
       public int getCount(int bin) { return counts[bin].intValue(); }
   
       public int getSpan() { return counts.length; }
   
       public int[] getBins(){
           int[] countsCopy = new int[counts.length];
           for(int i = 0; i<counts.length;i++){
               countsCopy[i]= counts[i].intValue();
           }
           return countsCopy;
       } 
   }
   ```

   

# Exercise 2.4

1. Code:

   ```java
   private static void exerciseFactorizer(Computable<Long, long[]> f) {
     final int threadCount = 16;
     final long start = 10_000_000_000L, range = 20_000L;
     System.out.println(f.getClass());
     Thread[] threads = new Thread[threadCount];
     for(int t=0;t<threads.length;t++){
       final long start2 = 10_000_020_000L+t*5000;
       final long to2 = 10_000_039_999L+t*5000+1;
       threads[t] = new Thread(()->{
         for(long i = start; i< start+range;i++){
           try {
             f.compute(i);
           } catch (InterruptedException e) {
             e.printStackTrace();
           }
         }
         for(long i = start2;i<to2;i++){
           try {
             f.compute(i);
           } catch (InterruptedException e) {
             e.printStackTrace();
           }
         }
       });
     }
     for (int t = 0; t < threadCount; t++)
       threads[t].start();
     try {
       for (int t = 0; t < threadCount; t++)
         threads[t].join();
     } catch (InterruptedException exn) {
     }
   }
   ```
2. Yes, the number of calls to the factorizer is 115 000. The execution time takes 17399ns.
3. The factorizer called 143733 times, and it takes 9586ns. 
4. The factorizer called 116086 times, and it takes 8950ns.
5. The factorizer called 115000 times, and it takes 8987ns.
6. The factorizer called 115000 times, and it takes 8909ns.
..

