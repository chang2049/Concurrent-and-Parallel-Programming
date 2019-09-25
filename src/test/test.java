package test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class test {
    public static void main(String[] args) {
        String a = "hahah";
        IntStream b = a.chars();

    }

    protected static class Node<U> {
        public final U item;
        public  Node<U> next;

        public Node(U item, Node<U> next) {
            this.item = item;
            this.next = next;
        }
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


class a{
    private int a;
    public a(int b){
        a=b;
    }
    public void set(int b){
        a =b;
    }
}