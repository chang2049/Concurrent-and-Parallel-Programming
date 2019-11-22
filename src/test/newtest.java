package test;

public class newtest {
    public static void main(String[] args) {
        class Solution {
            public String solution(String S, int K) {
                // write your code in Java SE 8
                String[] week = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
                int original = -1;
                for(int i = 0; i < week.length; i++){
                    if(S == week[i]){
                        original = i;
                        break;
                    }
                }
                if(original == -1)
                    throw new IllegalArgumentException("day doesn't exits");
                int day_index = (original + K)/7;
                return week[day_index];
            }
        }
    }


}
