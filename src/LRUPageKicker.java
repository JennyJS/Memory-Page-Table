import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by jenny on 10/18/15.
 */
public class LRUPageKicker extends PageKicker {

    public static class LRUPageComparator implements Comparator<PageTable.Page> {
        @Override
        public int compare(PageTable.Page p1, PageTable.Page p2) {
            if (p1.frequency < p2.frequency){
                return -1;
            } else if (p1.frequency > p2.frequency){
                return 1;
            } else {
                if (p1.isClean && !p2.isClean){
                    return -1;
                } else if (!p1.isClean && p2.isClean){
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    List<Integer> findPagesToKickOff(int numberToKick) {
        List<Integer> kickList = new LinkedList<>();
        LRUPageComparator LRUPageComparator = new LRUPageComparator();
        PriorityQueue<PageTable.Page> queue = new PriorityQueue<>(PageTable.getInstance().pageArr.length, LRUPageComparator);
        for (int i = 0 ; i < PageTable.getInstance().pageArr.length; i++){
            queue.add(PageTable.getInstance().pageArr[i]);
        }

        for (int j = 0; j < numberToKick; j++){
            PageTable.Page p = queue.remove();
            kickList.add(p.pageNo);
        }
        return kickList;
    }
}
