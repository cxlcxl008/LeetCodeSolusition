/**
 * @author xiaolongchen
 * @create 2019/11/8 16:37
 * 给定一个由 0 和 1 组成的数组 A，将数组分成 3 个非空的部分，使得所有这些部分表示相同的二进制值。
 */
public class Problem927 {
    public int[] threeEqualParts(int[] A) {
        if (A == null) {
            return new int[]{-1, -1};
        }
        int len = A.length;
        int cnt = 0;
        int[] index = new int[len + 1];
        for (int i = 0; i < len; i++) {
            if (A[i] == 1) {
                index[++cnt] = i;
            }
        }
        if (cnt == 0) {
            return new int[]{0, len - 1};
        }
        if (cnt % 3 != 0) {
            return new int[]{-1, -1};
        }
        int count_zero = len - index[cnt] - 1;
        int t = cnt / 3;
        //int result_i = index[t] + count_zero;
        //int result_j = index[2*t] + count_zero + 1;
        for (int i = index[1], j = index[t + 1], k = index[2 * t + 1]; k < len; i++, j++, k++) {
            if (A[i] != A[j] || A[j] != A[k]) {
                return new int[]{-1, -1};
            }
        }
        return new int[]{index[t] + count_zero, index[2 * t] + count_zero + 1};

    }
}
