package utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaolongchen
 * @create 2019/4/17
 */
public class SensitiveWordUtil {
    private static final String regEx_html = "<[^>]+>";
    private static final String regEx_special = "[^a-zA-Z0-9\\u2E80-\\u9FFF]";
    /**
     * 最小匹配规则
     */
    public static int minMatchTYpe = 1;
    /**
     * 最大匹配规则
     */
    public static int maxMatchType = 2;
    private Map sensitiveWordMap;
    private int sensitiveWordNum = 0;

    public Set<String> getSensitiveWord(String txt, int matchType) {
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(txt);
        txt = m_html.replaceAll("");
        Pattern p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
        Matcher m_special = p_special.matcher(txt);
        txt = m_special.replaceAll("");
        Set<String> sensitiveWordList = new HashSet<>();

        for (int i = 0; i < txt.length(); i++) {
            //判断是否包含敏感字符
            int length = CheckSensitiveWord(txt, i, matchType);
            //存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                //减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }

    private int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
        //敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        //匹配标识数默认为0
        int matchFlag = 0;
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            //获取指定key
            nowMap = (Map) nowMap.get(word);
            //存在，则判断是否为最后一个
            if (nowMap != null) {
                //找到相应key，匹配标识+1
                matchFlag++;
                //如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {
                    //结束标志位为true
                    flag = true;
                    //最小规则，直接返回,最大规则还需继续查找
                    if (minMatchTYpe == matchType) {
                        break;
                    }
                }

            } else {
                //不存在，直接返回
                break;
            }
        }
        //长度必须大于等于1，为词
        if (matchFlag < 1 || !flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

    public void init() {
        //读取敏感词库
        Set<String> keyWordSet = new HashSet<>();
        if (sensitiveWordNum == keyWordSet.size() && sensitiveWordMap != null) {
            return;
        }
        sensitiveWordNum = keyWordSet.size();
        //将敏感词库加入到HashMap中
        Map newMap = addSensitiveWordToHashMap(keyWordSet);
        sensitiveWordMap = newMap;
    }

    private Map addSensitiveWordToHashMap(Set<String> keyWordSet) {
        //初始化敏感词容器，减少扩容操作
        Map newMap = new HashMap(keyWordSet.size());
        String key;
        Map nowMap;
        Map<String, String> newWorMap;
        //迭代keyWordSet
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext()) {
            //关键字
            key = iterator.next();
            nowMap = newMap;
            for (int i = 0; i < key.length(); i++) {
                //转换成char型
                char keyChar = key.charAt(i);
                //获取
                Object wordMap = nowMap.get(keyChar);
                //如果存在该key，直接赋值
                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<>(1);
                    //不是最后一个
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    //最后一个
                    nowMap.put("isEnd", "1");
                }
            }
        }
        return newMap;
    }
}
