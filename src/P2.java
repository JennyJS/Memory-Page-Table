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
        PageTable.init(pageCount, pageSize.t);

        for (int i = 0; i < operations.size(); i++){
            Operation o = operations.get(i);
            if (o.type == Operation.Type.read){
                PageTable.getInstance().read(o.address, o.length);
            } else {
                PageTable.getInstance().write(o.address, o.length);
            }
        }
    }
}
