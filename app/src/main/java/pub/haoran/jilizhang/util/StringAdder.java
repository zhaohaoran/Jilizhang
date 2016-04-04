package pub.haoran.jilizhang.util;

/**
 * Created by zhaohaoran on 6/11/15.
 */
public class StringAdder {

//
//
//    public static void main(String[] agr0){
//
////		int[] a = {9,0,9};
////		int[] b = {1};
////		int[] s = arrayAdder(a,b);
////		for(int i=s.length-1;i>=0;i--){
////			System.out.print(s[i]);
////		}
////
//        String a = "1";
//        String b = "0090.1";
//        System.out.print(Add(a,b));
//
//
//
//    }

    public  String Add(String adder1, String adder2){
        String sum = "";
        int[] sumBeforeDot;
        int[] sumAfterDot;

        int[] adder1BeforeDot;
        int[] adder1AfterDot;
        int[] adder2BeforeDot;
        int[] adder2AfterDot;

        int[] afterDotTemp;

        boolean ifDotted = true;

        adder1BeforeDot = getBeforeDot(adder1);
        adder2BeforeDot = getBeforeDot(adder2);

        adder1AfterDot = getAfterDot(adder1);
        adder2AfterDot = getAfterDot(adder2);


        sumBeforeDot = arrayAdder(adder1BeforeDot,adder2BeforeDot);

        if(null != adder1AfterDot & null != adder2AfterDot){
            //
            if(adder1AfterDot.length > adder2AfterDot.length){
                afterDotTemp = new int[adder1AfterDot.length];
                for(int i=adder1AfterDot.length-1;i>=0;i--){
                    if(i< adder2AfterDot.length){
                        afterDotTemp[i] = adder2AfterDot[i];
                    }
                    else afterDotTemp[i]=0;

                }

                sumAfterDot = arrayAdder(adder1AfterDot,afterDotTemp);
            }
            else if(adder1AfterDot.length < adder2AfterDot.length){
                afterDotTemp = new int[adder2AfterDot.length];
                for(int i=adder2AfterDot.length-1;i>=0;i--){
                    if(i< adder1AfterDot.length){
                        afterDotTemp[adder2AfterDot.length-1-i] = adder1AfterDot[i];
                    }
                    else afterDotTemp[adder2AfterDot.length-1-i]=0;

                }
                sumAfterDot = arrayAdder(afterDotTemp,adder2AfterDot);

            }
            else sumAfterDot = arrayAdder(adder1AfterDot,adder2AfterDot);

            if(sumAfterDot.length>adder1AfterDot.length & sumAfterDot.length > adder2AfterDot.length){
                int[] c = {1};
                sumBeforeDot = arrayAdder(sumBeforeDot,c);
                int[] trying = new int[sumAfterDot.length-1];
                for(int k=0;k<sumAfterDot.length-1;k++){
                    trying[k]=sumAfterDot[k];
                }
                sumAfterDot = trying;
            }

        }
        else if(null != adder1AfterDot & null == adder2AfterDot){
            sumAfterDot = adder1AfterDot;
        }
        else if(null == adder1AfterDot & null != adder2AfterDot){
            sumAfterDot = adder2AfterDot;
        }
        else{
            ifDotted = false;
            sumAfterDot = null;
        }

        sum = arrayToString(sumBeforeDot,sumAfterDot,ifDotted);



        return sum;
    }


    public int ifHaveDot(String num){
        int dotIndex = -1;
        dotIndex = num.indexOf(".");
        return dotIndex;
    }


    public  int[] getBeforeDot(String num){
        int[] beforeDot = null;
        String beforeDotStr;
        int indexOfDot = num.indexOf(".");
        if(-1 == indexOfDot){
            beforeDot = new int[num.length()];
            beforeDotStr = num;
        }
        else{
            beforeDot = new int[indexOfDot];
            beforeDotStr = num.substring(0,indexOfDot);
        }




        for(int i=0;i< beforeDotStr.length();i++){
            if(0==i){
                beforeDot[i] = Integer.parseInt(beforeDotStr.substring(beforeDotStr.length()-i-1),10);
            }
            else
                beforeDot[i] = Integer.parseInt(beforeDotStr.substring(beforeDotStr.length()-i-1,beforeDotStr.length()-i),10);
        }


        if(0 == beforeDotStr.length()){
            beforeDot = new int[1];
            beforeDot[0] = 0;
        }

        return beforeDot;
    }


    public int[] getAfterDot(String num){
        int[] afterDot;
        String afterDotStr;
        int indexOfDot = num.indexOf(".");
        if(-1 == indexOfDot){
            afterDot = new int[num.length()];
            afterDotStr = "";
            afterDot = null;
        }
        else{
            while('0' == num.charAt(num.length()-1)){
                num = num.substring(0,num.length()-1);}


            afterDot = new int[num.length()-1-indexOfDot];
            afterDotStr = num.substring(indexOfDot+1);
            for(int i=0;i< afterDotStr.length();i++){

                afterDot[i] = Integer.parseInt(afterDotStr.substring(afterDotStr.length()-i-1,afterDotStr.length()-i),10);
            }
        }
        return afterDot;
    }


    public int[] arrayAdder(int[] adder1, int[] adder2){
        int length = adder1.length > adder2.length ? adder1.length : adder2.length;
        int[] sumArrayTemp = new int[length];
        int[] sum = null;
        int carrier = 0;
        int tempSum = 0;
//	    	if(adder1.length >adder2.length)
//	    		length = adder1.length;
//	    	else length = adder2.length;
//
        for(int i=0;i<length;i++){
            if(i<=adder1.length-1 &i<=adder2.length-1){
                tempSum = adder1[i]+adder2[i]+carrier;
                if(tempSum>=10){
                    carrier = 1;
                    sumArrayTemp[i] = tempSum-10;
                }
                else {
                    sumArrayTemp[i] = tempSum;
                    carrier = 0;
                }
            }
            else if(i<=adder1.length-1 & i>adder2.length-1){
                tempSum = adder1[i]+carrier;
                if(tempSum>=10){
                    carrier = 1;
                    sumArrayTemp[i] = tempSum-10;
                }
                else {
                    sumArrayTemp[i] = tempSum;
                    carrier = 0;
                }
            }
            else if(i>adder1.length-1 & i<=adder2.length-1){
                tempSum = adder2[i]+carrier;
                if(tempSum>=10){
                    carrier = 1;
                    sumArrayTemp[i] = tempSum-10;
                }
                else {
                    sumArrayTemp[i] = tempSum;
                    carrier = 0;
                }
            }
        }

        if(1 == carrier){
            sum = new int[length+1];
            for(int i=0;i<length+1;i++){
                if(i<length){
                    sum[i]=sumArrayTemp[i];
                }
                else sum[i] = 1;
            }
        }
        else if(0==carrier){
            sum = sumArrayTemp;
        }


        return sum;
    }

    public String arrayToString(int[] beforeDot, int[] afterDot,boolean ifDotted){
        String strResult = "";
        if(null != beforeDot){
            for(int i=0;i<beforeDot.length;i++){
                strResult += beforeDot[beforeDot.length-i-1]+"";
            }
        }else strResult+=0+"";

        if(ifDotted)
            strResult+=".";


        if(null != afterDot){
            for(int j=0;j<afterDot.length;j++){
                strResult += afterDot[afterDot.length-j-1]+"";
            }
        }

        if(ifDotted)
            while('0' == strResult.charAt(strResult.length()-1)){
                strResult = strResult.substring(0,strResult.length()-1);
            }

        while('0' == strResult.charAt(0)){
            strResult = strResult.substring(1,strResult.length());
        }



        strResult+="0";
        return strResult;
    }

}
