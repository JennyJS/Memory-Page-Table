import java.util.*;

/**
 * Created by jenny on 10/18/15.
 */
public class OPTPageKicker extends PageKicker {
    List<Operation> operations = new LinkedList<>();
    long pageSize;

    @Override
    List<Integer> findPagesToKickOff(int numberToKick){
        List<Integer> pagesNumsToBeReferenced = pageNumsToBeReferenced(operations);
        List<Integer> kickList = new LinkedList<>();

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < PageTable.getInstance().pageArr.length; i++){
            int key = -1;
            for (int j = 0; j < pagesNumsToBeReferenced.size(); i++){
                if (pagesNumsToBeReferenced.get(j) == PageTable.getInstance().pageArr[i].pageNo){
                    key = j;
                    map.put(key, PageTable.getInstance().pageArr[i].pageNo);
                    break;
                }
            }
            // if can't find the pageNo from the future operations, then assign the Max value
            if (key == -1){
                key = Integer.MAX_VALUE;
                map.put(key, PageTable.getInstance().pageArr[i].pageNo);
            }
        }

        //buid up a max heap
        PriorityQueue<Integer> queue = new PriorityQueue<>(PageTable.getInstance().pageArr.length, Collections.reverseOrder());
        Set<Integer> keys = map.keySet();
        queue.addAll(keys);

        // remove numToKick number of element from Maxheap
        for (int m = 0; m < numberToKick; m++){
            //pop max from the top of the MaxHeap
            int tmpKey = queue.remove();
            //add its value(pageNumber) to the list and return
            kickList.add(map.get(tmpKey));
        }

        return kickList;
    }

    public OPTPageKicker(List<Operation> operations, long pageSize){
        this.operations = operations;
        this.pageSize = pageSize;
    }

    public List<Integer> pageNumsToBeReferenced (List<Operation> operations){
        List<Integer> res = new LinkedList<>();
        for (Operation o : operations){
            int pageNo = (int)(o.address / pageSize);
            res.add(pageNo);
        }
        return res;
    }
}
