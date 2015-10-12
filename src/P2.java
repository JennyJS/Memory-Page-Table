import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by jenny on 10/11/15.
 */
public class P2 {

    public static void main(String args[]){
        int wordSize = -1;
        long memorySize = -1;
        long pageSize = -1;
        try{

//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

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
                if (wordSize == -1){
                    try{
                        wordSize = Integer.parseInt(ParseUtil.getStringInParentheses(input));
                        if (wordSize % 2 != 0){
                            System.err.println("Invalid wordSize " + wordSize);
                            return;
                        }
                    } catch (IllegalArgumentException e){
                        System.out.println("Error parsing wordSize " + input);
                        return;
                    }
                } else if (memorySize == -1){ // parse memorySize
                    try{
                        String str = ParseUtil.getStringInParentheses(input);
                        long unit = ParseUtil.convertUnit(str);
                        Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                        int integer = in.nextInt();
                        memorySize = integer * unit;


                        //check validation
                        if (memorySize % 2 != 0){
                            System.err.println("Invalid memorySize " + memorySize);
                            return;
                        }

                    } catch (IllegalArgumentException e){
                        System.out.println("Error parsing memorySize " + input);
                        return;
                    }
                } else if (pageSize == -1){ // parse pageSize
                    try{
                        String str = ParseUtil.getStringInParentheses(input);
                        long unit  = ParseUtil.convertUnit(str);
                        Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                        int integer = in.nextInt();
                        pageSize = integer * unit;
                        if (pageSize % 2 != 0) {
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

                    //call read function
                    //...

                    if (input.contains("read")) {
                        // call read
                    } else {
                        // call write
                    }
                }

            }

        } catch (IOException io){
            io.printStackTrace();
            System.err.println("Error reading from IO");
            return;
        }

        System.out.println("wordSize: " + wordSize + " memorySize: " + memorySize + " pageSize: " + pageSize);

    }


}
