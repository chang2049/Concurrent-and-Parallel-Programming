package test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class test {
    public static void main(String[] args) {
        Node<Integer> infNode = new Node<Integer>(0,null);
        final Node<Integer>[] nodeRecord = new Node[1];
        nodeRecord[0] =infNode;
        Stream.generate(()->{
            nodeRecord[0].next = new Node<Integer>(0,null);
            return nodeRecord[0]= nodeRecord[0].next;
        });

    }

    protected static class Node<U> {
        public final U item;
        public  Node<U> next;

        public Node(U item, Node<U> next) {
            this.item = item;
            this.next = next;
        }
    }




//        public static<U> Node<U> add(Node<U> currentNode){
//            Node<U> newNode = new Node(0,null);
//            currentNode.next = add(newNode);
//            return newNode;
//        }


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