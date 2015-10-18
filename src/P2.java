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
        try {
            ParseUtil.parseIntegerFromFile(wordSize, memorySize, pageSize, operations);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }


        int pageCount = (int)(memorySize.t / pageSize.t);

        PageTable.init(pageCount, pageSize.t);
        for (Operation o : operations) {
            if ((o.address + o.length) > Math.pow(2, wordSize.t)){
                System.err.println("Address : 0X" + Integer.toHexString(o.address) + " out of bound!");
                continue;
            }
            PageTable.getInstance().process(o);

        }
    }
}
