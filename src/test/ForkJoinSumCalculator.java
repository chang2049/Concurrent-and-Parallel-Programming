package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ForkJoinSumCalculator{
    public static void main(String[] args) {
        int[]  a = {1,2,3};
        Stream<Object> b = Arrays.stream(a).boxed()
                .flatMap(n -> IntStream.range(0,n));


    }

}
