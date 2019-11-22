// COMPILE:
// javac -cp scala.jar:akka-actor.jar Broadcast.java 
// RUN:
// java -cp scala.jar:akka-actor.jar:akka-config.jar:. Broadcast

import java.util.*;
import java.io.*;
import akka.actor.*;

// -- MESSAGES --------------------------------------------------

class InitMessage implements Serializable {
    public final int n;
    public InitMessage(int n) {
	this.n = n;
    }
}

class IsPrimeMessage implements Serializable {
    public final int n;
    public IsPrimeMessage(int n) {
	this.n = n;
    }
}

// -- ACTORS --------------------------------------------------

class PrimeActor extends UntypedActor {
    List<ActorRef> slaves;

    private List<ActorRef> createSlaves(int n) {
	List<ActorRef> slaves = new ArrayList<ActorRef>();
	for (int i=0; i<n; i++) {
	    ActorRef slave = getContext().actorOf(Props.create(SlaveActor.class), "p" + i);
	    slaves.add(slave);
	}
	return slaves;
    }

    public void onReceive(Object o) throws Exception {
	if (o instanceof InitMessage) {
	    int n = ((InitMessage) o).n;
	    if (n<=0) throw new RuntimeException("*** non-positive number!");
	    slaves = createSlaves(n);
	    System.out.println("initialized (" + n + " slaves ready to work)!");
	} else if (o instanceof IsPrimeMessage) {
	    if (slaves==null) throw new RuntimeException("*** uninitialized!");
	    int n = ((IsPrimeMessage) o).n;
	    if (n<=0) throw new RuntimeException("*** non-positive number!");
	    int slave_id = n % slaves.size();
	    slaves.get(slave_id).tell(o, getSelf()); // optimizable: slave_id == 0 && slave_id > |slaves|.
	}
    }
}

class SlaveActor extends UntypedActor {
    private boolean isPrime(int n) {
	int k = 2;
	while (k * k <= n && n % k != 0) k++;
	return n >= 2 && k * k > n;
    }
    
    private int delay(int n) { int res = 0; for (int i=0; i<10000000*n; i++) { res = res + i; } return res;  }

    public void onReceive(Object o) throws Exception {
	if (o instanceof IsPrimeMessage) {
	    int p = ((IsPrimeMessage) o).n;

	    // if (delay(p)==p) System.out.println("will not happen: "); // delay actor!
	    if (isPrime(p)) {
		System.out.println("(" + p % Primer.P + ") " + p); // HACK
	    }
	}
    }
}

// -- MAIN --------------------------------------------------

public class Primer {
    public static int P; // HACK
    public static int MAX = 100;

    private static void spam(ActorRef primer, int min, int max) {
	for (int i=min; i<max; i++) {
	    primer.tell(new IsPrimeMessage(i), ActorRef.noSender());
	}
    }

    public static void main(String[] args) {
	if (args.length!=1) {
	    System.out.println("usage: Primer <P>");
	    System.exit(0);
	}
	P = Integer.parseInt(args[0]);
	final ActorSystem system = ActorSystem.create("PrimerSystem");
	final ActorRef primer = system.actorOf(Props.create(PrimeActor.class), "primer");
	primer.tell(new InitMessage(P), ActorRef.noSender());
      	try {
	    System.out.println("Press return to initiate...");
	    System.in.read();
	    spam(primer, 2, MAX);
	    System.out.println("Press return to terminate...");
	    System.in.read();
	} catch(IOException e) {
	    e.printStackTrace();
	} finally {
	    system.shutdown();
	}
    }
}
