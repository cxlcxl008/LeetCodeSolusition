package utils;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author xiaolongchen
 * @create 2019/4/24
 */
public class TextDiffUtil {
    private static final String P_REGEX_PATTERN = "<p.*?</p>";

    private static List<String> textToList(String text) {
        Pattern p = Pattern.compile(P_REGEX_PATTERN);
        Matcher m = p.matcher(text);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    private static String listToText(List<String> list) {
        return String.join("", list);
    }

    public static Pair<String, String> getDiff(String before, String after) {
        if (!before.startsWith("<p>"))
            before = "<p>" + before + "</p>";
        List<String> beforeList = textToList(before);
        List<String> afterList = textToList(after);
        int[][] array = new int[beforeList.size() + 1][afterList.size() + 1];//此处的棋盘长度要比字符串长度多加1，需要多存储一行0和一列0

        for (int j = 0; j < array[0].length; j++) {//第0行第j列全部赋值为0
            array[0][j] = 0;
        }
        for (int i = 0; i < array.length; i++) {//第i行，第0列全部为0
            array[i][0] = 0;
        }

        for (int m = 1; m < array.length; m++) {//利用动态规划将数组赋满值
            for (int n = 1; n < array[m].length; n++) {
                if (beforeList.get(m - 1).equals(afterList.get(n - 1))) {
                    array[m][n] = array[m - 1][n - 1] + 1;//动态规划公式一
                } else {
                    array[m][n] = Math.max(array[m - 1][n], array[m][n - 1]);//动态规划公式二
                }
            }
        }
//		for(int m = 0; m < array.length; m++){//将数组赋满值,这样可以从右下角开始遍历找出最大公共子序列
//			for(int n = 0; n < array[m].length; n++){
//				System.out.print(array[m][n]);
//			}
//			System.out.println();
//		}
        List<String> lcs = new ArrayList<>();
        int i = beforeList.size() - 1;
        int j = afterList.size() - 1;

        while ((i >= 0) && (j >= 0)) {
            if (beforeList.get(i).equals(afterList.get(j))) {//字符串从后开始遍历，如若相等，下一步
                lcs.add(beforeList.get(i));
                i--;
                j--;
            } else {
                if (array[i + 1][j] > array[i][j + 1]) {//如果字符串的字符不同，则在数组中找相同的字符，注意：数组的行列要比字符串中字符的个数大1，因此i和j要各加1
                    j--;
                } else {
                    i--;
                }
            }
        }
        beforeList = processBeforeText(beforeList, lcs);
        afterList = processAfterText(afterList, lcs);


        Pair<String, String> result = new Pair<>(listToText(beforeList), listToText(afterList));
        return result;
    }

    private static List<String> processAfterText(List<String> afterList, List<String> lcs) {
        List<String> result = new ArrayList<>();
        int i = 0;
        int j = lcs.size() - 1;
        while (j >= 0) {
            if (!afterList.get(i).equals(lcs.get(j))) {
                result.add(afterList.get(i).replace("<p", "<p style=\"background:greenyellow\""));
            } else {
                result.add(afterList.get(i));
                j--;
            }
            i++;
        }
        while (i < afterList.size()) {
            result.add(afterList.get(i).replace("<p", "<p style=\"background:greenyellow\""));
            i++;
        }
        return result;
    }

    private static List<String> processBeforeText(List<String> beforeList, List<String> lcs) {
        List<String> result = new ArrayList<>();
        int i = 0;
        int j = lcs.size() - 1;
        while (j >= 0) {
            if (!beforeList.get(i).equals(lcs.get(j))) {
                result.add(beforeList.get(i).replace("<p", "<p style=\"background:pink\""));
            } else {
                result.add(beforeList.get(i));
                j--;
            }
            i++;
        }
        while (i < beforeList.size()) {
            result.add(beforeList.get(i).replace("<p", "<p style=\"background:pink\""));
            i++;
        }
        return result;
    }
}
