/**
 * @author xiaolongchen
 * @create 2019/10/12
 */
public class Problem1219 {
    private int maxValue;
    private int[][] grid;
    private int m;
    private int n;

    public void dfs(int x, int y, int currentValue) {
        currentValue = currentValue + grid[x][y];
        if (currentValue > maxValue) {
            maxValue = currentValue;
        }
        int t = grid[x][y];
        grid[x][y] = 0;
        if (x + 1 < m && grid[x + 1][y] > 0) {
            dfs(x + 1, y, currentValue);
        }
        if (x - 1 >= 0 && grid[x - 1][y] > 0) {
            dfs(x - 1, y, currentValue);
        }
        if (y + 1 < n && grid[x][y + 1] > 0) {
            dfs(x, y + 1, currentValue);
        }
        if (y - 1 >= 0 && grid[x][y - 1] > 0) {
            dfs(x, y - 1, currentValue);
        }
        grid[x][y] = t;
    }

    public int getMaximumGold(int[][] grid) {
        this.maxValue = 0;
        this.m = grid.length;
        this.n = grid[0].length;
        this.grid = grid;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] > 0) {
                    dfs(i, j, 0);
                }
            }
        }
        return maxValue;
    }
}
