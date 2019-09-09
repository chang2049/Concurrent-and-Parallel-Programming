package a2;// For week 2
// sestoft@itu.dk * 2014-09-04
// thdy@itu.dk * 2019

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.LongAdder;

interface Histogram {
    void increment(int bin);

    int getCount(int bin);

    int getSpan();
}

class SimpleHistogram {
    public static void main(String[] args) {
//        final Histogram histogram = new Histogram1(30);
//        histogram.increment(7);
//        histogram.increment(13);
//        histogram.increment(7);
//        dump(histogram);
        primeFactorCounterParallel(5_000_000,10);

    }

    public static void dump(Histogram histogram) {
        int totalCount = 0;
        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
            totalCount += histogram.getCount(bin);
        }
        System.out.printf("      %9d%n", totalCount);
    }

    private static void primeFactorCounterParallel(int range, int threadCount){
        final int perThread = range / threadCount;
        final Histogram5 his = new Histogram5(range);
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
}

class Histogram1 implements Histogram {
    private int[] counts;

    public Histogram1(int span) {
        this.counts = new int[span];
    }

    public void increment(int bin) {
        counts[bin] = counts[bin] + 1;
    }

    public int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }
}

class Histogram2 implements Histogram{
    final private  int[] counts;

    Histogram2(int span) {
        this.counts = new int[span];
    }


    public synchronized void increment(int bin) { counts[bin] =  counts[bin] + 1; }

    public synchronized int getCount(int bin) { return counts[bin]; }

    public int getSpan() { return counts.length; }

    public synchronized int[] getBins(){
        return counts.clone();
    }
}


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
    public int[] getBins(){
        int[] countsCopy = new int[counts.length];
        for(int i = 0; i<counts.length;i++){
            countsCopy[i]= counts[i].get();
        }
        return countsCopy;
    }
}

class Histogram4 implements Histogram{
    final private AtomicIntegerArray counts;

    Histogram4(int span) { counts = new AtomicIntegerArray(span); }

    public void increment(int bin) { counts.incrementAndGet(bin); }

    public int getCount(int bin) { return counts.get(bin); }

    public int getSpan() { return counts.length(); }

    public int[] getBins(){
        int[] countsCopy = new int[counts.length()];
        for(int i = 0; i<counts.length();i++){
            countsCopy[i]= counts.get(i);
        }
        return countsCopy;
    }
}

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


