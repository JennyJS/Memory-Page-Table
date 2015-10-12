import java.util.Scanner;

/**
 * Created by jenny on 10/11/15.
 */
public class ParseUtil {

    /**
     * translate from KB, MB... to 2^10, 2^20
     * */
    public static long convertUnit(String str) {
        long unit;
        if (str.contains("KB")){
            unit = 1 << 10;
        } else if (str.contains("M")){
            unit = 1 << 20;
        } else if (str.contains("G")){
            unit = 1 << 30;
        } else if (str.contains("T")){
            unit = 1 << 40;
        } else if (str.contains("B")){
            unit = 1L;
        } else {
            throw new IllegalArgumentException("Invalid memory unit:"  + str);
        }

        return unit;
    }

    /**
     * input wordSize(16GB), return 16GB
     * */
    public static String getStringInParentheses(String input){
        int s = input.indexOf("(") + 1;
        int e = input.indexOf(")");
        if (s < 0 || e < 0) {
            throw new IllegalArgumentException();
        } else {
            return input.substring(s, e);
        }
    }

    /**
     * input read(0x230, 8KB), read 0x230 and 8KB
     * */
    public static void parseOperationParameters(String input, Wrapper<Integer> address, Wrapper<Long> length){
        String str = getStringInParentheses(input);
        String[] readParameter = str.split(",");
        String addressStr = readParameter[0].trim();
        String lengthStr = readParameter[1].trim();
        String addressInHex = addressStr.split("0x")[1];
        address.t = Integer.parseInt(addressInHex, 16);

        Scanner in = new Scanner(lengthStr).useDelimiter("[^0-9]+");
        int integer = in.nextInt();

        String integerString = Integer.toString(integer);
        String unit = lengthStr.split(integerString)[1];

        length.t = integer * convertUnit(unit);
    }

    public static class Wrapper<T> {
        public T t;
    }
}
