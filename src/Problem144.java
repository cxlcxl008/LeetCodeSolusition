import java.util.*;

/**
 * @author xiaolongchen
 * @create 2019/11/7 16:03
 * 后序遍历 非递归
 */
public class Problem144 {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null){
            return result;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()){
            TreeNode cur = stack.pop();
            result.add(cur.val);
            if(cur.left != null){
                stack.push(cur.left);
            }
            if(cur.right != null){
                stack.push(cur.right);
            }
        }
        Collections.reverse(result);
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
