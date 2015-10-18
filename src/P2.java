import java.util.LinkedList;
import java.util.List;

/**
 * Created by jenny on 10/11/15.
 */
public class P2 {

    public static void main(String args[]){


        ParseUtil.Wrapper<Integer> wordSize = new ParseUtil.Wrapper<>();
        wordSize.t = -1;

        ParseUtil.Wrapper<Long> memorySize = new ParseUtil.Wrapper<>();
        memorySize.t = -1L;

        ParseUtil.Wrapper<Long> pageSize = new ParseUtil.Wrapper<>();
        pageSize.t = -1L;


        List<Operation> operations = new LinkedList<>();
        ParseUtil.parseIntegerFromFile(wordSize, memorySize, pageSize, operations);

        int pageCount = (int)(memorySize.t/pageSize.t);
        PageTable pageTable = new PageTable(pageCount, pageSize);

        for (int i = 0; i < operations.size(); i++){
            int address = operations.get(i).address;
            long length = operations.get(i).length;
            if (operations.get(i).type.equals("read")){
                
            }

        }






    }


}
