## Exercise 1.1

1. Count is **10588601** and should be 20000000

2. The correct result shows that two threads didn't read the variable at the same time. It may be because the computational cost is not too large. The software is not correct. Though the result is right in most time when $counts$  is small, it cannot guarantee it always gets the correct result.

3.  It won't make difference, as operations like `count += 1, count++`​ are not atomic.

4. The expected result should be 0. `Synchronized​` should be declared in both class methods. Though threads are calling different class methods, two class may read the variable `count​` at the same time when one of the 2 methods or both methods are not synchronized.

5. (i)without `synchronized` on both methods: Count is -32267 and should be 0!

   this happens when both methods read the variable at the same time and decrement writes the number after increment
   
   (ii)with only `decrement` being synchronized: Count is 97477 and should be 0!
   
   There is visibility problem when increment and decrement read the variable at the same time increment can only read the number before the lock, so the result is very likely to be a positive number.
   
   (iii)with only `increment` being synchronized: Count is -516758 and should be 0!
   
   Just like (ii), increment and decrement are swapt and result is negative as expected.
   
   (iv)with both being synchronized: Count is 0 and should be 0! 
   
   The class is thread-safe

## Exercise 1.2

1. When one thread steps into sleep state after printing a dash, the other thread is simutaneously just running the step of printing a dash. 

2. When one thread enters the synchronized block of printing, it acquares a lock and enter the monitor.  The other thread which attends to access the block will be suspened until the previous one exits the locked monitor.

3. 

4. ```java
   class Printer {
       public  void print () {
           synchronized (this) {
               System.out.print("-");
               try {
                   Thread.sleep(50);
               } catch (InterruptedException exn) {
               }
               System.out.print("|");
           }
       }
   ```



4. Code:

   ```java
   class Printer {
           public static void print () {
               synchronized (Printer.class) {
                   System.out.print("-");
                   try {
                       Thread.sleep(50);
                   } catch (InterruptedException exn) {
                   }
                   System.out.print("|");
               }
           }
   }
   ```



## Exercise 1.3

1. Yes, the thread loops forever because the change of mi.value is not visible to it.
2. Yes, the thread t terminates as expected.
3.  No, it loops forever, because the thread t does not update its mi.value from the storage of main thread.
4. Thread t terminates as expected when value is declare to be volatile. It's sufficient to use volatile in this case as `get()` is only called in thread t, and `set()` is only called in main() thread, volatile guarantees the variable visible to other threads.

## Exercise 1.4

1. Sequential version run time: real    1m8.581s; user    1m9.137s; sys     0m0.320s
2. 10 threads version run time:real    0m15.869s; user    1m42.526s; sys     0m0.276s
3. I get result: 2998906 for this run, which is smaller than correct result. Two threads may call the increment method as the same time but the count variable will only increase by 1, therefore, the wrong answer is smaller than the correct one.
4. In this case, `print`  doesn't have to be `synchronized`, as `t.join()` was called before `lc.get()`,`lc.get()` is in `main()` thread and will be suspemded until all other joined threads come to end. 

## Exercise 1.5

1.Result: 

Sum is 1890757,000000 and should be 2000000,000000

Sum is 1885518,000000 and should be 2000000,000000

Sum is 1864511,000000 and should be 2000000,000000

The results indicate that Mystery is not thread-safe

2. Though two methods are sychronized, they lock different things. Synchronized static locks the class object while synchronized method locks the instance.

3. I would create a new object, lock, and add synchronized block in two methods, so only one of the two threads has the lock and is able to change the value of sum.

   ```java
   public static final Object lock = new Object();
   
   public static synchronized void addStatic(double x) {
     synchronized(lock) {
       sum += x;
     }
   }
   
   public synchronized void addInstance(double x) {
     synchronized(lock) {
       sum += x;
     }
   }
   ```

## Exercise 1.6

1. Add synchronized to all the methods

2. Poorly, only one of add, get, set will have the lock and others have to wait

3. Thread safety?: no. because `add` and `set` could modify the array at same time

   Visibility?: no. add, set , get are synchronized separately, therefore get() cannot retrieve the written data before other locks are released

## Exercise 1.7

1. create a totalsize lock: `public static final Object totalSizeLock = new Object();`and change all `totalsize++` to `synchronized(totalSizeLock) { totalsize++}`

2. create a synchronized static method to implement the add operation and call it in the constructor

   ```java
   public DoubleArrayList() {
     setHash(this);
   }
   
   private synchronized static void setHash( DoubleArrayList currentDAL) {
     allLists.add(currentDAL);
   }
   ```

## Exercise 1.8

1. increment() is locking MysteryA class object and increment4() is locking MysteryB class object
2. adding a  static new Object as lock, and put `count++;` and `count += 4;`in sychronized(lock) block