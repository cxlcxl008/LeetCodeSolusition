/**
 * @author xiaolongchen
 * @create 2019/11/7 11:13
 * 中序遍历 非递归
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Problem94 {
    public static void main(String[] args) {
        Problem94.TreeNode root = new Problem94.TreeNode(1);
        root.left = new Problem94.TreeNode(2);
        List result = new Problem94().inorderTraversal(root);
        System.out.println(result);
    }

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList();
        Stack<TreeNode> stack = new Stack<>();
        if (root == null) {
            return result;
        }
        TreeNode temp = root;
        while (temp != null) {
            stack.push(temp);
            temp = temp.left;
        }
        while (!stack.empty()) {
            TreeNode current = stack.pop();
            result.add(current.val);
            if (current.right != null) {
                current = current.right;
                while (current != null) {
                    stack.push(current);
                    current = current.left;
                }
            }
        }

        return result;
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }
}
