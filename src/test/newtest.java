package test;

import java.util.concurrent.CompletableFuture;

public class newtest {
    public static void main(String[] args) {
        Validator numericValidator =
                new Validator((String s) -> s.matches("[a-z]+"));
        boolean b1 = numericValidator.validate("aaaa"); Validator lowerCaseValidator =
                new Validator((String s) -> s.matches("\\d+"));
        boolean b2 = lowerCaseValidator.validate("bbbb");
    }
    public static class test{

    }

}
@FunctionalInterface
interface ValidationStrategy {
    boolean execute(String s);
}

class Validator {
    private final ValidationStrategy strategy;
    public Validator(ValidationStrategy v) {
        this.strategy = v; }
    public boolean validate(String s) {
        return strategy.execute(s);
    }
}
