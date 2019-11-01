/**
 * @author xiaolongchen
 * @create 2019/10/17
 */
public class Problem9 {
    public boolean isPalindrome(int x) {
        if(x < 0){
            return false;
        }
        String str = String.valueOf(x);
        int len = str.length();
        for(int i = 0;i<len/2;i++){
            if(str.charAt(i)!=str.charAt(len-i-1)){
                return false;
            }
        }
        return true;
    }
}
