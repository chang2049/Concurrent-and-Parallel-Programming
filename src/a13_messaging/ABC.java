package a13_messaging;

import java.util.*;
import java.io.*;
import akka.actor.*;



// -- MESSAGING ----

class StartTransferMessage implements Serializable{

}

class TransferMessage implements Serializable{

}

class DepositMessage implements Serializable {
    /* TODO */ }

class PrintBalanceMessage implements Serializable {
    /* TODO */ }


public class ABC { // Demo showing how things work: public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("ABCSystem");
    /* TODO (CREATE ACTORS AND SEND START MESSAGES) */
    try {
        System.out.println("Press return to inspect..."); System.in.read();
        /* TODO (INSPECT FINAL BALANCES) */
        System.out.println("Press return to terminate...");
        System.in.read();
    } catch(IOException e) { e.printStackTrace();
    } finally {
        system.shutdown();}
}


