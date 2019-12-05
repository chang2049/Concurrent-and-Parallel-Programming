package test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class streamTest {
    public static void main(String[] args) {

    }
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (root==null) return result;
        LinkedList<TreeNode> current;
        LinkedList<TreeNode> next = new LinkedList<TreeNode>();
        next.add(root);
        while(!next.isEmpty()){
            current = next;
            next = new LinkedList<TreeNode>();
            List<Integer> tmp = new ArrayList<Integer>();
            while(!current.isEmpty()){
                TreeNode curNode = next.poll();
                tmp.add(curNode.val);
                if (curNode.left != null) next.add(curNode.left);
                if(curNode.right != null) next.add(curNode.right);
            }
            if(tmp.size()>0) result.add(tmp);
        }
        return result;
    }

      public class TreeNode {
          int val;
          TreeNode left;
          TreeNode right;
          TreeNode(int x) { val = x; }
      }


}
