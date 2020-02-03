package pcpp_2017;

import net.jcip.annotations.GuardedBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class KMeans1P implements KMeans {
    // Sequential version 1.  A Cluster has an immutable mean field, and
    // a mutable list of immutable Points.

    private final Point[] points;
    private final int k;
    private Cluster[] clusters;
    private int iterations;

    public KMeans1P(Point[] points, int k) {
        this.points = points;
        this.k = k;

    }

    public void findClusters(int[] initialPoints) {
        Cluster[] clusters = GenerateData.initialClusters(points, initialPoints, Cluster::new, Cluster[]::new);
        ExecutorService executor = Executors.newCachedThreadPool();
        AtomicBoolean converged = new AtomicBoolean(false);
        while (!converged.get()) {
            iterations++;
            { // Assignment step: put each point in exactly one cluster
                final int perRange = points.length/k;
                List<Callable<Void>> callables_assign = new ArrayList<Callable<Void>>();
                for(int i =0; i< k; i++){
                    final int from = perRange*i,
                            to = i+1 == k? points.length : perRange*(i+1);
                    final Cluster[] finalClusters = clusters;
                    callables_assign.add( ()->{
                        for(int idx = from; idx<to; idx++){
                            int bestIDX = -1;
                            Point p = points[idx];
                            for(int cidx = 0; cidx< finalClusters.length; cidx++){
                                Cluster c = finalClusters[cidx];
                                Cluster best = bestIDX == -1?null:finalClusters[bestIDX];
                                if (best == null ||p.sqrDist(c.mean) < p.sqrDist(best.mean)){
                                    bestIDX = cidx;
                                }
                            }
                            finalClusters[bestIDX].add(p);
                        }
                        return null;
                    });
                }
                try {
                    List<Future<Void>> futures = executor.invokeAll(callables_assign);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            { // Update step: recompute mean of each cluster

                ArrayList<Cluster> newClusters = new ArrayList<>();
                List<Callable<Cluster>> callables_update = new ArrayList<Callable<Cluster>>();
                converged.set(true);
                for (Cluster c : clusters) {
                    callables_update.add(()->{
                        Point mean = c.computeMean();
//                        System.out.println("compute mean = "+mean);
                        if (!c.mean.almostEquals(mean))
                            converged.set(false);
                        if (mean != null)
                            return new Cluster(mean);
                        else
                            return null;
                    });
                }
                try {
                    List<Future<Cluster>> futures = executor.invokeAll(callables_update);
                    for(Future<Cluster> fut :futures){
                        Cluster c = fut.get();
                        if (c!=null) newClusters.add(c);
                    }


                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                clusters = newClusters.toArray(new Cluster[newClusters.size()]);
            }
        }
        this.clusters = clusters;
    }

    public void print() {
        for (Cluster c : clusters)
            System.out.println(c);
        System.out.printf("Used %d iterations%n", iterations);
    }

    static class Cluster extends ClusterBase {
        private final ArrayList<Point> points = new ArrayList<>();
        private final Point mean;

        public Cluster(Point mean) {
            this.mean = mean;
        }

        @Override
        public Point getMean() {
            return mean;
        }

        public synchronized void add(Point p) {
            points.add(p);
        }

        public Point computeMean() {
            double sumx = 0.0, sumy = 0.0;
            for (Point p : points) {
                sumx += p.x;
                sumy += p.y;
            }
            int count = points.size();
            return count == 0 ? null : new Point(sumx/count, sumy/count);
        }
    }
}