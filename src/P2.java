import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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


        Queue<Operation> operations = new LinkedList<>();
        try {
            ParseUtil.parseIntegerFromFile(wordSize, memorySize, pageSize, operations);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }


        int pageCount = (int)(memorySize.t / pageSize.t);
        PageKicker lruPageKicker = new LRUPageKicker();

        PageTable.init(pageCount, pageSize.t, lruPageKicker);

        System.out.println("***********************************");
        System.out.println("*************** LRU ***************");
        System.out.println("***********************************");
        for (Operation o : operations) {
            try {
                PageTable.getInstance().process(o, wordSize.t);
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                return;
            }
        }

        /**
         *  Optimal algorithm
         */

        PageKicker optPageKicker = new OPTPageKicker(operations, pageSize.t);
        PageTable.init(pageCount, pageSize.t, optPageKicker);
        System.out.println("***************************************");
        System.out.println("*************** Optimal ***************");
        System.out.println("***************************************");
        while (!operations.isEmpty()) {
            Operation o = operations.poll();
            try {
                PageTable.getInstance().process(o, wordSize.t);
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                return;
            }
        }
    }
}
