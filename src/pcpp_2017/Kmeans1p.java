package pcpp_2017;

public class Kmeans1p implements KMeans {
    private  Cluster[] clusters;
    private final int k;
    private final Point[] points;


    public void findClusters(int[] initialPoints) {


    }

    public void print() {

    }

    static class Cluster extends ClusterBase {

        @Override
        public Point getMean() {
            return null;
        }
    }
}
