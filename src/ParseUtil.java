import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * Created by jenny on 10/11/15.
 */
public class ParseUtil {

    public static class Wrapper<T> {
        public T t;
    }

    /**
     * translate from KB, MB... to 2^10, 2^20
     * */
    private static long convertUnit(String str) {
        long unit;
        if (str.contains("KB")){
            unit = 1L << 10;
        } else if (str.contains("M")){
            unit = 1L << 20;
        } else if (str.contains("G")){
            unit = 1L << 30;
        } else if (str.contains("T")){
            unit = 1L << 40;
        } else if (str.contains("B")){
            unit = 1L;
        } else {
            throw new IllegalArgumentException("Invalid memory unit:"  + str);
        }

        return unit;
    }

    private static boolean isPowerOf2(long num){
        return (num != 0 && (num & (num - 1)) == 0);
    }

    /**
     * input wordSize(16GB), return 16GB
     * */
    private static String getStringInParentheses(String input){
        int s = input.indexOf("(") + 1;
        int e = input.indexOf(")");
        if (s < 0 || e < 0) {
            throw new IllegalArgumentException("Invalid input str, expect parentheses around number:" + input);
        } else {
            return input.substring(s, e);
        }
    }

    /**
     * input read(0x230, 8KB), read 0x230 and 8KB
     * */
    private static void parseOperationParameters(String input, Wrapper<Long> address, Wrapper<Long> length) {
        String str = getStringInParentheses(input);
        String[] readParameter = str.split(",");
        if (readParameter.length != 2){
            throw new IllegalArgumentException("Invalid input operation parameter " + str);
        }
        String addressStr = readParameter[0].trim();
        String lengthStr = readParameter[1].trim();
        String addressInHex = addressStr.split("0x")[1];
        address.t = Long.parseLong(addressInHex, 16);

        Scanner in = new Scanner(lengthStr).useDelimiter("[^0-9]+");
        int integer = in.nextInt();

        String integerString = Integer.toString(integer);
        String unit = lengthStr.split(integerString)[1];

        length.t = integer * convertUnit(unit);
    }

    public static void parseIntegerFromFile(
            Wrapper<Integer> wordSize,
            Wrapper<Long> memorySize,
            Wrapper<Long> pageSize,
            Queue<Operation> operations
    ){
        try{
            BufferedReader br = new BufferedReader(new FileReader("/Users/jenny/Java_workspace/COEN283_P2/src/t24.dat"));
            String input;

            while((input=br.readLine())!=null){
                input = input.trim();

                if (input.startsWith("#")) {
                    // ignore line starting with #
                    continue;
                } else {
                    // Remove string after #
                    int index = input.indexOf('#');
                    if (index != -1) {
                        input = input.substring(0, index);
                        input = input.trim();
                    }
                }

                // parse wordSize
                if (wordSize.t == -1){
                    wordSize.t = Integer.parseInt(ParseUtil.getStringInParentheses(input));
                    if (wordSize.t != 16 && wordSize.t != 32 && wordSize.t != 64){
                        throw new IllegalArgumentException("Invalid wordSize: " + wordSize.t);
                    }
                } else if (memorySize.t == -1){ // parse memorySize
                    String str = ParseUtil.getStringInParentheses(input);
                    long unit = ParseUtil.convertUnit(str);
                    Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                    int integer = in.nextInt();
                    memorySize.t = integer * unit;


                    //check validation
                    if (!isPowerOf2(memorySize.t)){
                        throw new IllegalArgumentException("Invalid memorySize, should be power of 2: " + memorySize.t);
                    }
                } else if (pageSize.t == -1){ // parse pageSize
                    String str = ParseUtil.getStringInParentheses(input);
                    long unit  = ParseUtil.convertUnit(str);
                    Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                    int integer = in.nextInt();
                    pageSize.t = integer * unit;
                    if (!isPowerOf2(pageSize.t)) {
                        throw new IllegalArgumentException("Invalid pageSize " + memorySize);
                    }
                } else if (input.startsWith("read") || input.startsWith("write")){
                    ParseUtil.Wrapper<Long> address = new ParseUtil.Wrapper<>();
                    ParseUtil.Wrapper<Long> length = new ParseUtil.Wrapper<>();
                    ParseUtil.parseOperationParameters(input, address, length);

                    operations.add(new Operation(address.t, length.t, input.contains("read") ? Operation.Type.read : Operation.Type.write));
                } else {
                    throw new IllegalArgumentException("Invalid operation " + input);
                }
            }
        } catch (IOException io){
            System.err.println("Error reading from IO");
            return;
        }
        System.out.println();
        System.out.println("wordSize: " + wordSize.t + " memorySize: " + memorySize.t + " pageSize: " + pageSize.t);
        System.out.println();
    }
}
