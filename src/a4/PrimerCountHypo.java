package a4;

import static java.util.Arrays.parallelPrefix;
import static java.util.Arrays.parallelSetAll;

public class PrimerCountHypo {
    public static void main(String[] args) {
        int[] arr = setArray(10_000_001);
        parallelPrefix(arr, (x,y)-> x+y);

        for(int i = arr.length/10;i<=arr.length;i+=arr.length/10){
            System.out.printf("n = %8d ; ratio value = %f %n",i,arr[i]/(i*1.0/Math.log(i)));
        }

    }

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
}
