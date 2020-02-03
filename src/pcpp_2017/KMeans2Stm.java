package pcpp_2017;

import org.multiverse.api.references.TxnDouble;
import org.multiverse.api.references.TxnInteger;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.multiverse.api.StmUtils.*;

class KMeans2Stm implements KMeans {
    // Sequential version 2.  Data represention: An array points of
    // Points and a same-index array myCluster of the Cluster to which
    // each point belongs, so that points[pi] belongs to myCluster[pi],
    // for each Point index pi.  A Cluster holds a mutable mean field
    // and has methods for aggregation of its value.

    private final Point[] points;
    private final int k;
    private Cluster[] clusters;
    private int iterations;

    public KMeans2Stm(Point[] points, int k) {
        this.points = points;
        this.k = k;
    }

    public void findClusters(int[] initialPoints) {
        ExecutorService executor = Executors.newWorkStealingPool();
        final Cluster[] clusters = GenerateData.initialClusters(points, initialPoints, Cluster::new, Cluster[]::new);
        final Cluster[] myCluster = new Cluster[points.length];
        final AtomicBoolean converged = new AtomicBoolean(false);
        final int perRange = points.length/k;
        while (!converged.get()) {
            iterations++;
            {
                // Assignment step: put each point in exactly one cluster
                List<Callable<Void>> callableAssign = new ArrayList<Callable<Void>>();
                for(int t = 0; t<k; t++){
                    final int from = perRange*t;
                    final int to = t+1==k?points.length:perRange*(t+1);
                    callableAssign.add(()->{
                        for(int i = from; i<to;i++){
                            Point p = points[i];
                            Cluster best = null;
                            for (Cluster c : clusters)
                                if (best == null || p.sqrDist(c.mean) < p.sqrDist(best.mean))
                                    best = c;
                            myCluster[i] = best;
                        }
                        return null;
                    });
                }
                try {
                    List<Future<Void>> futures = executor.invokeAll(callableAssign);
                    for (Future<Void> fut :futures) fut.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println(myCluster[0]);
            {
                // Update step: recompute mean of each cluster
                for (Cluster c : clusters)
                    c.resetMean();

                List<Callable<Void>> callableUpdate = new ArrayList<Callable<Void>>();
                for(int t = 0; t<k; t++){
                    final int from = perRange*t;
                    final int to = t+1==k?points.length:perRange*(t+1);
                    callableUpdate.add(()->{
                        for(int i = from; i<to;i++){
                            myCluster[i].addToMean(points[i]);
                        }
                        return null;
                    });
                }
                try {
                    List<Future<Void>> futures = executor.invokeAll(callableUpdate);
                    for (Future<Void> fut :futures) fut.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                converged.set(true);

                for (Cluster c : clusters)
                    converged.set(c.computeNewMean() && converged.get());
            }
//             System.out.printf("[%d]", iterations); // To diagnose infinite loops
        }
        this.clusters = clusters;
    }

    public void print() {
        for (Cluster c : clusters)
            System.out.println(c);
        System.out.printf("Used %d iterations%n", iterations);
    }

    static class Cluster extends ClusterBase {
        private Point mean;
        private final TxnDouble sumx = newTxnDouble(0) ,
                sumy = newTxnDouble(0);
        private final TxnInteger count = newTxnInteger(0);

        public Cluster(Point mean) {
            this.mean = mean;
        }

        public void addToMean(Point p) {
            atomic(()->{
                sumx.getAndIncrement(p.x) ;
                sumy.getAndIncrement(p.y);
                count.getAndIncrement(1);
            });
        }

        // Recompute mean, return true if it stays almost the same, else false
        public boolean computeNewMean() {
            Point oldMean = this.mean;
            atomic(()->{
                this.mean = new Point(sumx.get()/count.get(), sumy.get()/count.get());
            });
            return oldMean.almostEquals(this.mean);
        }

        public void resetMean() {
            atomic(()->{
                sumx.set(0.0);
                sumy.set(0.0);
                count.set(0);
            });

        }

        @Override
        public Point getMean() {
            return mean;
        }
    }
}
