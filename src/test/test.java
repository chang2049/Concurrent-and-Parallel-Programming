package test;

public class test {
    public static void main(String[] args) {
        a a_instance = new a(5);
        a_instance.set(6);
    }

    protected static class Node<U> {
        public final U item;
        public final Node<U> next;

        public Node(U item, Node<U> next) {
            this.item = item;
            this.next = next;
        }

        public static Node<Integer> add(Node<Integer> currentNode){
            return add(new Node<Integer>(0,currentNode));
        }

//        public static<U> Node<U> add(Node<U> currentNode){
//            Node<U> newNode = new Node(0,null);
//            currentNode.next = add(newNode);
//            return newNode;
//        }


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