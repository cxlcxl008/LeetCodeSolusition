/**
 * @author xiaolongchen
 * @create 2019/11/8 17:13
 * 给定正整数 n，找到若干个完全平方数（比如 1, 4, 9, 16, ...）使得它们的和等于 n。你需要让组成和的完全平方数的个数最少。
 */
public class Problem279 {
    public static void main(String[] args) {
        System.out.println(new Problem279().numSquares(5));
    }

    public int numSquares(int n) {
        int dp[] = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i] = i;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 1; i + j * j <= n; j++) {
                dp[i + j * j] = Math.min(dp[i] + 1, dp[i + j * j]);
            }
        }
        return dp[n];
    }
}
