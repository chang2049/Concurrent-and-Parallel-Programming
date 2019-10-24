# Exercise 7.1

1.  

   ```java
   public V get(K k) {
     final int h = getHash(k), stripe = h%lockCount;
     synchronized (locks[stripe]){
       final int hash = h%buckets.length;
       ItemNode<K, V> searchedNode = ItemNode.search(buckets[hash],k);
       return searchedNode==null? null:searchedNode.v;
     }
   }
   ```

2.  locking stripe prevent reallocation of sizes when reading

   ```java
   public int size() {
     int result = 0;
     for(int i =0; i<sizes.length;i++){
       synchronized (locks[i]){
         result+= sizes[i];
       }
     }
     return result;
   }
   ```

3.  

   ```java
   public V putIfAbsent(K k, V v) {
     final int h = getHash(k), stripe = h%lockCount;
     synchronized (locks[stripe]){
       final int hash = h%buckets.length;
       final ItemNode<K,V> node = ItemNode.search(buckets[hash],k);
       if (node != null) return node.v;
       else {
         buckets[hash] = new ItemNode<K,V>(k,v,buckets[hash]);
         sizes[stripe]++;
         return null;
       }
     }
   }
   ```

4.   

  ```java
  public V putIfAbsent(K k, V v) {
    final int h = getHash(k), stripe = h%lockCount;
    if (sizes[stripe] > buckets.length/lockCount ){
      reallocateBuckets();
    }
    synchronized (locks[stripe]){
      final int hash = h%buckets.length;
      final ItemNode<K,V> node = ItemNode.search(buckets[hash],k);
      if (node != null) return node.v;
      else {
        buckets[hash] = new ItemNode<K,V>(k,v,buckets[hash]);
        sizes[stripe]++;
        return null;
      }
    }
  } 
  ```

5.  

   ```java
     public V remove(K k) {
       // TO DO: IMPLEMENT
       final int h = getHash(k), stripe = h%lockCount;
       synchronized (locks[stripe]){
         final int hash = h%buckets.length;
         ItemNode<K,V> curr = buckets[hash];
         if (curr == null) return  null;
         else if (curr.k == k){
           buckets[hash] = curr.next;
           sizes[stripe]--;
           return curr.v;
         }
         else {
           ItemNode<K,V> prev;
           while(curr.next != null){
             prev = curr;
             curr = curr.next;
             if (curr.k == k){
               sizes[stripe]--;
               prev.next = curr.next;
               return curr.v;
             }
           }
         }
       }
       return null;
     }
   ```

6. The second method is implemented in our case. As It only requires a single lock and other threads require other locks won't be suspended. In this way, it would run less time, which also means this for eachEach method will give a dynamic state of the buckets.

   ```java
   public void forEach(Consumer<K,V> consumer) {
     // TO DO: IMPLEMENT
     final int rounds = buckets.length/lockCount +1;
     for(int stripe = 0; stripe < lockCount; stripe++){
       synchronized (locks[stripe]){
         for(int round = 0; round < rounds;round++){
           int bucket = stripe + round*lockCount;
           if( bucket>= buckets.length) continue;
           ItemNode<K,V> curr = buckets[bucket];
           while (curr != null){
             consumer.accept(curr.k, curr.v);
             curr = curr.next;
           }
         }
       }
     }
   }
   ```

7. 

8. Result:

   OS:   Mac OS X; 10.15; x86_64

   JVM:  Oracle Corporation; 1.8.0_201

   CPU:  null; 8 "cores"

   Date: 2019-10-23T17:22:36+0200

   class SynchronizedMap

   class StripedMap

   class StripedWriteMap

   class WrapConcurrentHashMap
   SynchronizedMap       16         643784.9 us   32766.45          2
   99992.0
   StripedMap            16         158248.7 us   21624.63          2
   99992.0
   StripedWriteMap       16          54737.0 us    4888.92          8
   0.0
   WrapConcHashMap       16          73590.4 us    7622.54          4
   99992.0
   
9.  It would be faster when `reallocateBuckets` is called as less time would be cost in waiting for all locks. 

10.  The more stripes are used, the less likely the ongoing threads contend for the same the lock.

11.  It guarantees that a list of nodes that are put in the same bucket entry share the same lock. For example, 3 buckets and 2 stripes are used. k1 will go to buckets[2] and use the locks[1], k2 will go to buckets[2] and use the locks[0]



# Exercise 2

1.  

   ```java
   public V get(K k) {
     // TO DO: IMPLEMENT
     final ItemNode<K,V>[] bs = buckets;
     final int h = getHash(k), stripe = h % lockCount, hash = h % bs.length;
     final Holder<V> holder = new Holder<V>();
     return sizes.get(stripe) != 0 && ItemNode.search(bs[hash], k, holder) ?
       holder.get():null ;
   }
   ```

2.  

   ```java
   public int size() {
     int result = 0;
     for(int i = 0; i < lockCount; i++)
       result+=sizes.get(i);
     return result;
   }
   ```

3.  

   ```java
   public V putIfAbsent(K k, V v) {
     // TO DO: IMPLEMENT
     final int h = getHash(k), stripe = h % lockCount;
     final Holder<V> old = new Holder<V>();
     ItemNode<K,V>[] bs;
     int afterSize;
     synchronized (locks[stripe]){
       bs = buckets;
       final int hash = h % bs.length;
       final ItemNode<K,V> node = bs[hash];
       if (ItemNode.search(node,k,old)) return old.get();
       bs[hash] = new ItemNode<K,V>(k, v, bs[hash].next);
       afterSize = sizes.addAndGet(stripe,1 );
     }
     if (afterSize * lockCount > bs.length)
       reallocateBuckets(bs);
     return null;
   }
   ```

4.  

   ```java
   public V remove(K k) {
     final int h = getHash(k), stripe = h % lockCount;
     final Holder<V> old = new Holder<V>();
     ItemNode<K,V>[] bs;
     synchronized (locks[stripe]){
       bs = buckets;
       final int hash = h % bs.length;
       final ItemNode<K,V> node = bs[hash];
       ItemNode.delete(node,k,old);
     }
     return old.get();
   }
   ```

5.  

   ```java
   public void forEach(Consumer<K,V> consumer) {
     final ItemNode<K,V>[] bs = buckets;
     for(ItemNode<K,V> itemNode : bs) {
       ItemNode<K,V> node = itemNode;
       while (node != null) {
         consumer.accept(node.k, node.v);
         node = node.next;
       }
     }
   }
   ```

6.  

   OS:   Mac OS X; 10.15; x86_64

   JVM:  Oracle Corporation; 1.8.0_201

   CPU:  null; 8 "cores"

   Date: 2019-10-23T20:40:30+0200

   SynchronizedMap       16         633390.0 us   60440.31          2
   99992.0
   StripedMap            16         163043.0 us   29409.83          2
   99992.0
   StripedWriteMap       16          76837.4 us    6252.82          4
   99992.0
   WrapConcHashMap       16          77893.5 us    3968.65          4
   99992.0

   **Discussion: **

   The result looks reasonable, SynchronizedMap runs the most time as all operations share the same lock. StripedMap consumes less time than SychronizedMap because the lock is striped and multiple operations which requires different locks can take place concurrently. StripedWriteMap is even faster because it save time on reading operations and the performance is very close to WrapConcHashMap