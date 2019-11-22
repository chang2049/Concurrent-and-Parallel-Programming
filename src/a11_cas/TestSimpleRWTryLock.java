package a11_cas;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

public class TestSimpleRWTryLock {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
       dualThreadTest();
    }

    public static void dualThreadTest() throws BrokenBarrierException, InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(3);
        final SimpleRWTryLock lock = new SimpleRWTryLock();
        Thread[] threads = new Thread[2];
        for(int i = 0; i< threads.length;i++){
            threads[i] = new Thread(()->{
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                lock.readerTryLock();
                lock.readerUnlock();
                lock.writerTryLock();
                lock.readerUnlock();
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                lock.writerTryLock();
                lock.writerUnlock();
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();

        }

        barrier.await();
        barrier.await();
        barrier.await();


    }

    public static void seqTest(){
        SimpleRWTryLock lock = new SimpleRWTryLock();
        lock.writerTryLock();
        assert !lock.writerTryLock();
        assert !lock.readerTryLock();
        lock.writerUnlock();
        assert lock.readerTryLock();
        assert !lock.readerTryLock();
        lock.readerUnlock();
        assert lock.writerTryLock();
    }


}
class SimpleRWTryLock{
    private final AtomicReference<Holders> holders = new AtomicReference<Holders>();

    public boolean writerTryLock(){
        final Thread current = Thread.currentThread();
        return holders.compareAndSet(null, new Writer(current));
    }

    public void writerUnlock(){
        final Thread current = Thread.currentThread();
        Holders old = holders.get();
        if (old.getThread() == current){
            if(!holders.compareAndSet(old,null))
                throw new RuntimeException("not lock holder");;
        }else{
            throw new RuntimeException("not lock holder");
        }
    }


    public boolean readerTryLock(){
        final Thread current = Thread.currentThread();
        Holders old = holders.get();
        if (old == null)
            return holders.compareAndSet(null,new ReaderList(current,null));
        if (old.getClass().getSimpleName().equals("ReaderList")){
            if (old.containsThread(current)) return false;
            else {
                ReaderList newHolder = new ReaderList(current, (ReaderList) old);
                return holders.compareAndSet(old,newHolder);
            }
        }
        return false;
    }

    public void readerUnlock(){
        final Thread current = Thread.currentThread();
        Holders old = holders.get();
        if (old.getClass().getSimpleName().equals("Writer"))
           throw new RuntimeException("not lock holder");
        if (old.containsThread(current)){
            ReaderList old_r = (ReaderList) old;
            ReaderList new_r = old_r.deleteNode(old_r,current);
            holders.compareAndSet(old, new_r);
        }
        else throw new RuntimeException("not lock holder");
    }


    private static abstract class Holders{
        public abstract Thread getThread();
        public abstract boolean containsThread(Thread thread);
    }

    private static class ReaderList extends Holders{
        private final Thread thread;
        private final ReaderList next;

        private ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }
        public Thread getThread(){
            return thread;
        }
        public boolean containsThread(Thread thread){
            Holders curr = this;
            while (curr!=null){
                if (curr.getThread() == thread)
                    return true;
            }
            return false;
        }

        public  ReaderList deleteNode(ReaderList currNode, Thread thread){
            if (currNode==null) return  null;
            if (currNode.getThread()==thread) return currNode.next;
            return new ReaderList(currNode.thread, deleteNode(currNode.next,thread));
        }

    }

    private static class Writer extends Holders{
        private final Thread thread;
        private Writer(Thread thread) {
            this.thread = thread;
        }
        public Thread getThread(){
            return thread;
        }

        public boolean containsThread(Thread thread) {
            return this.thread==thread;
        }

    }


}
