/**
 * @author xiaolongchen
 * @create 2019/10/17
 */
public class Problem10 {
    //dp
    public boolean isMatch(String s, String p) {
        if (p.isEmpty()) {
            return s.isEmpty();
        }
        int[][] dp = new int[s.length() + 1][p.length() + 1];
        dp[0][0] = 1;
        for (int j = 2; j <= p.length(); j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= p.length(); j++) {
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);
                if (sc == pc || pc == '.') {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (pc == '*' && j >= 2) {
                    if (sc == p.charAt(j - 2) || p.charAt(j - 2) == '.') {
                        dp[i][j] = (dp[i - 1][j] + dp[i][j - 1] + dp[i][j - 2]) > 0 ? 1 : 0;
                    } else {
                        dp[i][j] = dp[i][j - 2];
                    }
                }
            }
        }
        return dp[s.length()][p.length()] == 1;
    }
}
