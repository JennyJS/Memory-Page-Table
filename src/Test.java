import java.util.Scanner;

/**
 * Created by jenny on 10/11/15.
 */
public class Test {

    @org.junit.Test
    public void test(){
        String testStr = "wordSize(16)";
        int s = testStr.indexOf("(") + 1;
        int e = testStr.indexOf(")");
        String res = testStr.substring(s, e);
        System.out.println(String.valueOf(res));
    }

    @org.junit.Test
    public void test2(){
        Scanner in = new Scanner("32KB").useDelimiter("[^0-9]+");
        int integer = in.nextInt();
        System.out.println(integer);
        int a = Integer.parseInt("e6630", 16);
        System.out.println(a);

        String str = "0x6350";
        String[] strArr = str.split("0x");
        System.out.println(Integer.parseInt(strArr[1]));
    }
}
