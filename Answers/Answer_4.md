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

   

5. 