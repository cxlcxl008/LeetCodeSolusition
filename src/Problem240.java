/**
 * @author xiaolongchen
 * @create 2019/11/6 19:06
 * 编写一个高效的算法来搜索 m x n 矩阵 matrix 中的一个目标值 target。该矩阵具有以下特性：
 * <p>
 * 每行的元素从左到右升序排列。
 * 每列的元素从上到下升序排列。
 */
public class Problem240 {
    public int[][] mat;
    public int x;

    public static void main(String[] args) {
        //int[][] i = new int[][]{{3, 6, 9, 12, 17, 22}, {5, 11, 11, 16, 22, 26}, {10, 11, 14, 16, 24, 31}, {10, 15, 17, 17, 29, 31}, {14, 17, 20, 23, 34, 37}, {19, 21, 22, 28, 37, 40}, {22, 22, 24, 32, 37, 43}};
        //System.out.println(searchMatrix(i, 20));
        //int[][] i = new int[][]{{5}, {6}};
        //System.out.println(searchMatrix(i, 6));
        int[][] i = new int[][]{{1, 4, 7, 11, 15}, {2, 5, 8, 12, 19}, {3, 6, 9, 16, 22}, {10, 13, 14, 17, 24}, {18, 21, 23, 26, 30}};
        System.out.println(new Problem240().searchMatrix(i, 5));
    }

    public boolean find(int left, int right, int up, int down) {
        if (left > right || up > down) {
            return false;
        } else if (x < mat[up][left] || x > mat[down][right]) {
            return false;
        }
        int m_r = up + (down - up) / 2;
        int m_c = binarySearch(m_r, left, right);
        if (m_c < 0) {
            return find(m_c + 1, right, up, m_r - 1);
        }

        if (mat[m_r][m_c] == x) {
            return true;
        } else {
            return find(left, m_c, m_r + 1, down) || find(m_c + 1, right, up, m_r);
        }
    }

    public int binarySearch(int m_r, int left, int right) {
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (mat[m_r][mid] == x) {
                return mid;
            }
            if (mat[m_r][mid] > x) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        if (mat[m_r][left] > x) {
            left--;
        }
        return left;


    }

    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        mat = matrix;
        x = target;
        int m = matrix.length;
        int n = matrix[0].length;

        return find(0, n - 1, 0, m - 1);
    }
}
