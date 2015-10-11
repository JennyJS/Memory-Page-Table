import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

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
                        int s = input.indexOf("(") + 1;
                        int e = input.indexOf(")");
                        wordSize = Integer.parseInt(input.substring(s, e));
                        if (wordSize % 2 != 0){
                            System.err.println("Invalid wordSize " + wordSize);
                            return;
                        }
                    } catch (NumberFormatException e){
                        System.out.println("Error parsing wordSize " + input);
                        return;
                    }
                } else if (memorySize == -1){ // parse memorySize
                    try{
                        int s = input.indexOf("(") + 1;
                        int e = input.indexOf(")");
                        String str = input.substring(s, e);
                        long unit = convertUnit(str);
                        Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                        int integer = in.nextInt();
                        memorySize = integer * unit;


                        //check validation
                        if (memorySize % 2 != 0){
                            System.err.println("Invalid memorySize " + memorySize);
                            return;
                        }

                    } catch (NumberFormatException e){
                        System.out.println("Error parsing memorySize " + input);
                        return;
                    }
                } else if (pageSize == -1){ // parse pageSize
                    try{
                        int s = input.indexOf("(") + 1;
                        int e = input.indexOf(")");
                        String str = input.substring(s, e);
                        long unit  = convertUnit(str);
                        Scanner in = new Scanner(str).useDelimiter("[^0-9]+");
                        int integer = in.nextInt();
                        pageSize = integer * unit;

                    } catch (NumberFormatException e){
                        System.out.println("Error parsing pageSize " + input);
                        return;
                    }
                }



            }

        } catch (IOException io){
            io.printStackTrace();
            System.err.println("Error reading from IO");
            return;
        }


    }


    // translate from KB, MB... to 2^10, 2^20
    public static long convertUnit(String str) {
        long unit;
        if (str.contains("K")){
            unit = (int)Math.pow(2, 10);
        } else if (str.contains("M")){
            unit = (int)Math.pow(2, 20);
        } else if (str.contains("G")){
            unit = (int)Math.pow(2, 30);
        } else if (str.contains("T")){
            unit =  (int)Math.pow(2, 40);
        } else {
           unit = Integer.parseInt(str);
        }
        return unit;

    }
}
