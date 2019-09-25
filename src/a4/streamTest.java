package a4;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class streamTest {
    public static void main(String[] args) {
        final int N = 1_000_000_000;
//        double sum = IntStream.rangeClosed(1,N).mapToDouble(i -> 1.0/i/i ).sum();
//        System.out.printf("Sum = %20.16f%n", sum - Math.PI*Math.PI/6);

        final String s = "sequential", p = "parallel";
//        TestWordStream.Mark7(s, m->{
//            double sum = IntStream.rangeClosed(1,N).mapToDouble(i -> 1.0/i/i ).sum();
//            return s.hashCode();
//        });
//
//        TestWordStream.Mark7(p, m->{
//            double sum = IntStream.rangeClosed(1,N).mapToDouble(i -> 1.0/N/N ).parallel().sum();
//            return p.hashCode();
//        });


//        double sum_loop = 0;
//        for(double i =1; i<N+1;i++){
//            sum_loop+=1/i/i;
//        }
//        System.out.printf("Sum = %20.16f%n", sum_loop - Math.PI*Math.PI/6);
//        TestWordStream.Mark7("classic loop sum", item->{
//            double sum_l = 0;
//            for(double i =1; i<N+1;i++){
//                sum_l+=1/i/i;
//            }
//            return s.hashCode();
//        });

        final int[] i = {0};
        for(int iter =0;iter<=5;iter++){
            double sum_gen = Stream.generate(()->++i[0]).mapToDouble(in->1./in/in).limit(N).parallel().sum();
            System.out.printf("Sum = %20.16f%n", sum_gen - Math.PI*Math.PI/6);
            i[0] = 0;
        }
//        TestWordStream.Mark7("sequential",m->{
//            Stream.generate(()->++i[0]).mapToDouble(in->1./in/in).limit(N).sum();
//            i[0] = 0;
//            return i.hashCode();
//        });




    }


}
