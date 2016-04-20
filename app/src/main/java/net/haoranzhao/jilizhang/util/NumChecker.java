package net.haoranzhao.jilizhang.util;

/**
 * Created by zhaohaoran on 3/24/16.
 */
public class NumChecker {

    public String getNumStr(String input){
        String res=null;

        input.trim();

        input.replace(" ","");

        for(int i=0;i<input.length();i++){
            char c=input.charAt(i);
            if(47<(int)c && (int)c<58){

            }else{
                input.replace(input.substring(i,i+1),"");
            }
        }

        return res;
    }

}
