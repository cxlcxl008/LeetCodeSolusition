/**
 * @author xiaolongchen
 * @create 2019/11/12 17:35
 * 给出二叉搜索树的根节点，该二叉树的节点值各不相同，修改二叉树，使每个节点 node 的新值等于原树中大于或等于 node.val 的值之和。
 */
public class Problem1038 {
    int value;

    public void dfs(TreeNode node) {
        if (node.left == null && node.right == null) {
            node.val = node.val + value;
            value = node.val;
            return;
        }

        if (node.right != null) {
            dfs(node.right);
        }
        node.val = node.val + value;
        value = node.val;
        if (node.left != null) {
            dfs(node.left);
        }
    }

    public TreeNode bstToGst(TreeNode root) {
        value = 0;
        dfs(root);
        return root;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }
}
