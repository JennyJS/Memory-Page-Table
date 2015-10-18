import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
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

    public static void parseIntegerFromFile(
            Wrapper<Integer> wordSize,
            Wrapper<Long> memorySize,
            Wrapper<Long> pageSize,
            List<Operation> operations
    ){
        try{
            BufferedReader br = new BufferedReader(new FileReader("/Users/jenny/Java_workspace/COEN283_P2/src/t20.dat"));
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
                    try{
                        wordSize.t = Integer.parseInt(ParseUtil.getStringInParentheses(input));
                        if (wordSize.t % 2 != 0){
                            System.err.println("Invalid wordSize " + wordSize);
                            return;
                        }
                    } catch (IllegalArgumentException e){
                        System.out.println("Error parsing wordSize " + input);
                        return;
                    }
                } else if (memorySize.t == -1){ // parse memorySize
                    try{
                        String str = ParseUtil.getStringInParentheses(input);
                        long unit = ParseUtil.convertUnit(str);
                        Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                        int integer = in.nextInt();
                        memorySize.t = integer * unit;


                        //check validation
                        if (memorySize.t % 2 != 0){
                            System.err.println("Invalid memorySize " + memorySize);
                            return;
                        }

                    } catch (IllegalArgumentException e){
                        System.out.println("Error parsing memorySize " + input);
                        return;
                    }
                } else if (pageSize.t == -1){ // parse pageSize
                    try{
                        String str = ParseUtil.getStringInParentheses(input);
                        long unit  = ParseUtil.convertUnit(str);
                        Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                        int integer = in.nextInt();
                        pageSize.t = integer * unit;
                        if (pageSize.t % 2 != 0) {
                            System.err.println("Invalid pageSize " + memorySize);
                            return;
                        }

                    } catch (IllegalArgumentException e){
                        System.out.println("Error parsing pageSize " + input);
                        return;
                    }
                } else if (input.contains("read") || input.contains("write")){

                    ParseUtil.Wrapper<Integer> address = new ParseUtil.Wrapper<>();
                    ParseUtil.Wrapper<Long> length = new ParseUtil.Wrapper<>();
                    ParseUtil.parseOperationParameters(input, address, length);

                    //call read or write function

                    Operation o = new Operation();
                    if (input.contains("read")) {
                        // call read
                        o.type = Operation.Type.read;
                    } else {
                        // call write
                        o.type = Operation.Type.write;
                    }

                    o.address = address.t;
                    o.length = length.t;
                    // add each operation to the operations list
                    operations.add(o);
                }
            }

        } catch (IOException io){
            io.printStackTrace();
            System.err.println("Error reading from IO");
            return;
        }

        System.out.println("wordSize: " + wordSize.t + " memorySize: " + memorySize.t + " pageSize: " + pageSize.t);
    }
}
