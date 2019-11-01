/**
 * @author xiaolongchen
 * @create 2019/10/12
 */
public class Problem6 {
    public String convert(String s, int numRows) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < numRows; i++) {
            int mod = (numRows * 2 - 2);
            int j = i;
            while (j < len) {
                sb.append(s.charAt(j));
                if (i != 0 && i != numRows - 1 && j + 2*(numRows - i - 1) < len) {
                    sb.append(s.charAt(j + 2*(numRows - i - 1)));
                }
                j += mod;
            }
        }
        return sb.toString();
    }
}
