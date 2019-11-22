package a11_cas;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static a10.TestStmHistogram.countFactors;
import static  a10.TestStmHistogram.dump;

interface Histogram {
    void increment(int bin);

    int getCount(int bin);

    int getSpan();

    int[] getBins();

    int getAndClear(int bin);

    void transferBins(Histogram hist);
}

public class TestCasHistogram {
    public static void main(String[] args) {
        countPrimeFactorWithCasHis();
    }
    public static void dump(Histogram histogram) {
        int totalCount = 0;
        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
            totalCount += histogram.getCount(bin);
        }
        System.out.printf("      %9d%n", totalCount);
    }

    public static void countPrimeFactorWithCasHis() {
        final Histogram histogram = new CasHistogram(30);
        final Histogram total = new CasHistogram(30);
        final int range = 4_000_000;
        final int threadCount = 10, perThread = range / threadCount;
        final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1),
                stopBarrier = startBarrier;
        final Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            threads[t] =
                    new Thread(() -> {
                        try {
                            startBarrier.await();
                        } catch (Exception exn) {
                        }
                        for (int p = from; p < to; p++)
                            histogram.increment(countFactors(p));
                        System.out.print("*");
                        try {
                            stopBarrier.await();
                        } catch (Exception exn) {
                        }
                    });
            threads[t].start();
        }
        try {
            startBarrier.await();
        } catch (Exception exn) {
        }
        Timer timer = new Timer();
        int doCount = 0;
        try{
            while (doCount<200){
                total.transferBins(histogram);
                doCount++;
                Thread.sleep(30);
            }}catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            stopBarrier.await();
        } catch (Exception exn) {
        }
        System.out.println(timer.check());
        dump(total);

    }


}

class CasHistogram implements Histogram{
    final AtomicInteger[] counts;
    public CasHistogram(int span){
        counts = new AtomicInteger[span];
        for(int i = 0; i<span; i++){
            counts[i] = new AtomicInteger();
        }
    }
    public void increment(int bin) {
        int old_val;
        int new_val;
        do {
            old_val = counts[bin].get();
            new_val = old_val + 1;
        }while(!counts[bin].compareAndSet(old_val,new_val));
    }

    public int getCount(int bin) {
        return counts[bin].get();
    }

    public int getSpan() {
        return counts.length;
    }

    public int[] getBins() {
        int[] counts_copied = new int[counts.length];
        int old_val;
        for(int i = 0; i< counts.length; i++){
            do {
                old_val = counts[i].get();
                counts_copied[i] = old_val;
            }while(!counts[i].compareAndSet(old_val,old_val));
        }
        return counts_copied;
    }

    public int getAndClear(int bin) {
        int old_val;
        do {
            old_val = counts[bin].get();
        }while(!counts[bin].compareAndSet(old_val,0));
        return old_val;
    }


    public void transferBins(Histogram hist) {
        int old_val;
        int new_val;
        for (int i = 0; i < counts.length; i++) {
            int add = hist.getAndClear(i);
            do {
                old_val = counts[i].get();
                new_val = old_val + add;
            } while (!counts[i].compareAndSet(old_val, new_val));
        }
    }
}
