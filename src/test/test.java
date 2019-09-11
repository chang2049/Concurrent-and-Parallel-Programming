package test;

public class test {
    public static void main(String[] args) {
        a a_instance = new a(5);
        a_instance.set(6);
    }
}

class a{
    private int a;
    public a(int b){
        a=b;
    }
    public void set(int b){
        a =b;
    }
}