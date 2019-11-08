package test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SleepSort {


    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        int[] numbers = new int[]{2, 6, 5, 3, 8, 7};
        CyclicBarrier barrier = new CyclicBarrier(numbers.length+1);
        for (int number : numbers) {
            new Thread(()->{
                try {
                    barrier.await();
                    Thread.sleep(number);
                    System.out.println(number);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        barrier.await();
    }
}
