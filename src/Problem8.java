/**
 * @author xiaolongchen
 * @create 2019/10/17
 */
public class Problem8 {
    private boolean isValid(char c) {
        return (c >= '0' && c <= '9');
    }


    public int myAtoi(String str) {
        int result = 0;
        int flag = 1;
        str = str.trim();
        int len = str.length();

        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!isValid(c)) {
                if (i == 0 && c == '-') {
                    flag = -flag;
                } else if (i == 0 && c == '+') {
                    continue;
                } else {
                    return result;
                }
            } else {
                int temp = Integer.parseInt(String.valueOf(c)) * flag;
                if (result > Integer.MAX_VALUE / 10 || (result == Integer.MAX_VALUE / 10 && temp > 7)) {
                    return Integer.MAX_VALUE;
                }
                if (result < Integer.MIN_VALUE / 10 || (result == Integer.MIN_VALUE / 10 && temp < -8)) {
                    return Integer.MIN_VALUE;
                }
                result = result * 10 + temp;

            }
        }
        return result;
    }
}
