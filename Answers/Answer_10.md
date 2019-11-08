# Exercise 10.2

1.  

   ```java
   public void increment(int bin) {
     atomic(()->counts[bin].getAndIncrement(1));
   }
   
   public int getCount(int bin) {
     return atomic(()->counts[bin].get());
   }
   
   public int getSpan() {
     return atomic(()->counts.length);
   }
   ```

2. The result is correct.

3.  

   ```java
       public int[] getBins() {
           final int[] bins = new int[counts.length];
           atomic(()->{
               for(int idx = 0; idx<counts.length; idx++)
                   bins[idx] = counts[idx].get();
           });
           return bins;
       }
   ```

4.  

   ```java
       public int getAndClear(int bin) {
           return atomic(()->{
               int count = counts[bin].get();
               counts[bin].set(0);
               return count;
           });
       }
   ```

5.  

   ```java
       public void transferBins(Histogram hist) {
           atomic(()->{
              if (this.getSpan() < hist.getSpan())
                  throw new IllegalArgumentException("new hist is larger than current one");
              for(int idx = 0; idx < hist.getSpan(); idx++)
                  counts[idx].getAndIncrement(hist.getAndClear(idx));
           });
       }
   ```

6. No, it's not the case when `transferBins` is only called before all prime counting threads reach the second `await()`.

7.  it will run successfully and total is not changed. Inside the tansferBins, getAndClear change count[hash] to 0 and recover it .Each getAndClear can be seen as a nested atomic, it will reuse the outer transaction. Therefore, it would commit successfully as long as total has not been changed by other threads.

#  Exercise 10.3

1.  

   ```java
       public V get(K k) {
           ItemNode<K,V> node = atomic(()->{
               final TxnRef<ItemNode<K, V>>[] bs = buckets.get();
               final int hash = getHash(k) % bs.length;
               return bs[hash].get();
           });
           final Holder<V> holder = new Holder<>();
           ItemNode.search(node, k, holder);
           return holder.value;
       }
   ```

2.  

   ```java
       public void forEach(Consumer<K, V> consumer) {
           ItemNode<K,V>[] nodeList = atomic(()->{
               final TxnRef<ItemNode<K, V>>[] bs = buckets.get();
               final ItemNode<K,V>[] bucketNodes = new ItemNode[bs.length];
               for(int i = 0; i<bs.length;i++)
                   bucketNodes[i] = bs[i].get();
               return bucketNodes;
           });
           for(int i = 0; i<nodeList.length;i++){
               ItemNode<K,V> node = nodeList[i];
               while (node != null) {
                   consumer.accept(node.k, node.v);
                   node = node.next;
               }
           }
       }
   ```

3.  All the writes to map would either change the buckets or the entries in the buckets. The operations in atomic() guarantees that tracked variables are changed internally when it's trying to commit the changes.

   ```java
    public V put(K k, V v) {
            final int h = getHash(k);
            final Holder<V> old = new Holder<V>();
            atomic(()->{
                final TxnRef<ItemNode<K, V>>[] bs = buckets.get();
                final int hash = h % bs.length;
                final ItemNode<K,V> node =  bs[hash].get(),
                        newNode = ItemNode.delete(node, k, old);
                bs[hash].set(newNode);
                if (old.value==null) cachedSize.getAndIncrement(1);
            });
            return old.get();
    
        }
    
        // Put v at key k only if absent.
        public V putIfAbsent(K k, V v) {
            final int h = getHash(k);
            final Holder<V> old = new Holder<V>();
            return atomic(()->{
                final TxnRef<ItemNode<K, V>>[] bs = buckets.get();
                final int hash = h % bs.length;
                if(ItemNode.search(bs[hash].get(),k,old))
                    return old.value;
                final ItemNode<K,V> node =  bs[hash].get(),
                        newNode = ItemNode.delete(node, k, old);
                bs[hash].set(newNode);
                cachedSize.getAndIncrement(1);
                return null;
            });
        }
    
        // Remove and return the value at key k if any, else return null
        public V remove(K k) {
            final int h = getHash(k);
            final Holder<V> old = new Holder<V>();
            return atomic(()->{
                final TxnRef<ItemNode<K, V>>[] bs = buckets.get();
                final int hash = h % bs.length;
                if(ItemNode.search(bs[hash].get(),k,old)){
                    final ItemNode<K,V> node =  bs[hash].get(),
                            newNode = ItemNode.delete(node, k, old);
                    bs[hash].set(newNode);
                    cachedSize.decrement();
                    return old.value;
                }
                return null;
            });
        }
   ```

4.   

   ```java
       public int size() {
           return atomic(()->cachedSize.get());
       }
   ```

   

5.  I'm confused on this question.

   But I would use Semaphore() with capacity of 1. semaphore would be acquired and released adjacently after the code block of calling reallocate in put(), putIfAbsent(), remove(). And all operations in reallocate() will be put between semaphore acquire and release.

