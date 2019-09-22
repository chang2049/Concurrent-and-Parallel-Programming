package test;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class streamTest {
    public static void main(String[] args) {

        Function<String, Function<String,String>> prefix = s1 -> s2 -> s1 + s2;
        Function<String,String> addDollar = prefix.apply("$");
        BiFunction<Function<String,String>,String,String> twice = (f, s) -> f.apply(f.apply(s));
        Function<String,String> addTwoDollars = s -> twice.apply(addDollar, s);
        System.out.println(addTwoDollars.apply("100"));

    }

}
