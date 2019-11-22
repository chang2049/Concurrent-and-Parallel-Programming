package a13_messaging;

import java.io.*;
import java.util.Random;

import akka.actor.*;

// There might be collision that leads to a wrong result
//for example, a2 sent balance message to bank1 after a1 sent balance and before
//bank sends {set,new} message to a1. In the end, bank1 may send new a2 to a1

// -- MESSAGING --------------------------------------------------

class StartTransferMessage implements Serializable{
    public final ActorRef bank;
    public final ActorRef account_1;
    public final ActorRef account_2;

    StartTransferMessage(ActorRef bank, ActorRef account_1, ActorRef account_2) {
        this.bank = bank;
        this.account_1 = account_1;
        this.account_2 = account_2;
    }
}

class TransferMessage implements Serializable{
    final ActorRef from;
    final ActorRef to;
    final int amount;

    TransferMessage(ActorRef from, ActorRef to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}

class DepositMessage implements Serializable {
    final int amount;

    DepositMessage(int amount) {
        this.amount = amount;
    }
}

class PrintBalanceMessage implements Serializable {

}


// -- ACTORS --------------------------------------------------

class AccountActor extends UntypedActor {
    private int balance = 0;
    public void onReceive(Object o) throws Exception {
        if (o instanceof DepositMessage){
            DepositMessage message = (DepositMessage) o;
            this.balance += message.amount;
        }
        if ( o instanceof PrintBalanceMessage){
            System.out.println(balance);
        }

    }
}

class BankActor extends UntypedActor {
    public void onReceive(Object o) throws Exception {
        if (o instanceof TransferMessage){
            TransferMessage message = (TransferMessage) o;
            message.from.tell(new DepositMessage(-message.amount),getSelf());
            message.to.tell(new DepositMessage(message.amount),getSelf());
        }
    }


}

class ClerkActor extends UntypedActor {
    public void onReceive(Object o) throws Exception {
        if (o instanceof StartTransferMessage){
            StartTransferMessage message = (StartTransferMessage) o;
            Random rand = new Random();
            for( int i = 0; i<100; i++ ){
                int N = rand.nextInt(101);
                message.bank.tell(new TransferMessage(message.account_1, message.account_2, N),getSelf());
            }
        }
    }
}


// -- MAIN --------------------------------------------------

public class ABC { // Demo showing how things work:
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("ABCSystem");
        /* TODO (CREATE ACTORS AND SEND START MESSAGES) */
        final ActorRef clerk1 =
                system.actorOf(Props.create(ClerkActor.class),"clerk1");
        final ActorRef clerk2 =
                system.actorOf(Props.create(ClerkActor.class),"clerk2");
        final ActorRef bank1 =
                system.actorOf(Props.create(BankActor.class),"bank1");
        final ActorRef bank2 =
                system.actorOf(Props.create(BankActor.class),"bank2");
        final ActorRef acount1 =
                system.actorOf(Props.create(AccountActor.class),"acount1");
        final ActorRef acount2 =
                system.actorOf(Props.create(AccountActor.class),"acount2");
        clerk1.tell(new StartTransferMessage(bank1,acount1,acount2),ActorRef.noSender());
        clerk2.tell(new StartTransferMessage(bank2,acount2,acount1),ActorRef.noSender());
        try {
            System.out.println("Press return to inspect...");
            System.in.read();
            /* TODO (INSPECT FINAL BALANCES) */
            acount1.tell(new PrintBalanceMessage(),ActorRef.noSender());
            acount2.tell(new PrintBalanceMessage(),ActorRef.noSender());
            System.out.println("Press return to terminate...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            system.shutdown();
        }
    }
}


