import java.util.Map;

/**
 * @author xiaolongchen
 * @create 2019/10/17
 */
public class Problem12 {
    //贪心
    public String intToRoman(int num) {
        int[] numbers = new int[]{1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] strs = new String[]{"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while (num > 0 && index < 13){
            if(num > numbers[index]){
                int times = num/numbers[index];
                for(int i=0;i<times;i++){
                    sb.append(strs[i]);
                }
                num -= times*numbers[index];
            }
            index++;
        }
        return sb.toString();
    }
}
