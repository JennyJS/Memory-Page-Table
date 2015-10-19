import java.util.*;

/**
 * Created by jenny on 10/18/15.
 */
public class OPTPageKicker extends PageKicker {
    final Queue<Operation> operations;
    final long pageSize;

    public OPTPageKicker(Queue<Operation> operations, long pageSize){
        this.operations = operations;
        this.pageSize = pageSize;
    }

    @Override
    List<Integer> findPagesToKickOff(int numberToKick){
        List<Integer> pagesNumsToBeReferenced = pageNumsToBeReferenced();
        List<Integer> kickList = new LinkedList<>();

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < PageTable.getInstance().pageArr.length; i++){
            int key = -1;
            for (int j = 0; j < pagesNumsToBeReferenced.size(); j++){
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
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(PageTable.getInstance().pageArr.length, Collections.reverseOrder());
        maxHeap.addAll(map.keySet());

        // remove numToKick number of element from Maxheap
        for (int m = 0; m < numberToKick; m++){
            //pop max from the top of the MaxHeap
            //add its value(pageNumber) to the list and return
            kickList.add(map.get(maxHeap.poll()));
        }

        return kickList;
    }



    public List<Integer> pageNumsToBeReferenced (){
        List<Integer> res = new LinkedList<>();
        for (Operation o : operations){
            int pageNo = (int)(o.address / pageSize);
            res.add(pageNo);
        }
        return res;
    }
}
