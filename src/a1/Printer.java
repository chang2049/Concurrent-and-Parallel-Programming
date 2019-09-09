package a1;

class Printer {
    public void print() {
        System.out.print("-");
        try { Thread.sleep(50); } catch (InterruptedException exn) { }
        System.out.print("|");
    }

    public static void main(String[] args) {
        final Printer p = new Printer();
        Thread t1 = new Thread(() -> {
            while (true){
                p.print();
            }
        });
        Thread t2 = new Thread(() -> {
            while (true){
                p.print();
            }
        });

        t1.start(); t2.start();
        try { t1.join(); t2.join(); }
        catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }

    }
}