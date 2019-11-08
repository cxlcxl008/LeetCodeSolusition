import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaolongchen
 * @create 2019/10/12
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 */
public class Problem3 {
    public static void main(String[] args) {
        Problem3 problem3 = new Problem3();
        System.out.println(problem3.lengthOfLongestSubstring("abba"));
    }

    public int lengthOfLongestSubstring(String s) {
        int[] map= new int[128];
        int result = 0;
        int start = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            start = Math.max(start,map[c]);
            result = Math.max(result, i + 1 - start);
            map[c]= i + 1;
        }
        return result;
    }
}
