import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by jenny on 10/10/15.
 */
public class LRU {
    
    // use an array of Nodes to represent the physical memory

    private static class Page {
        boolean isClean = true;
        boolean isEmpty = true;
        int pageNo;
        int frequency;

    }
    
    final Page[] pageArr;
    int pageSize;
    
    private LRU (int pageCount, int pageSize){
        pageArr = new Page[pageCount];
        this.pageSize = pageSize;
    }

    private List<Integer> getPageIndexes(int address, int length) {
        List<Integer> pages = new LinkedList<>();
        int start = (int) Math.floor(address / pageSize);
        int end = (int) (Math.ceil((address + length)/pageSize) - 1);
        for (int i = start; i < end; i++){
            pages.add(i);
        }
        return pages;
    }
    
    public void read(int address, int length){
        boolean canFindInPageTable = false;

        List<Integer> pages = getPageIndexes(address, length);

        for (int i = 0; i < pageArr.length; i++){
            int j = 0;
            int tmpI = i;
            while (pageArr[i].pageNo == pages.get(j) && j < pages.size()){
                i++;
                j++;
            }


            if (j == pages.size()){
                System.out.println("Reading from " + tmpI + " in page table");
                canFindInPageTable = true;
            } else {
                i = tmpI;
            }
        }


        // can't find from physical memory, then copy from logical memory
        if (!canFindInPageTable){
            // check whether need to call kick function or not
            int requiredPage = (int)Math.ceil(length / pageSize);
            int numOfEmpty = 0;
            for (int i = 0; i < pageArr.length; i++){
                // find the max consecutive empty pages
                if (pageArr[i].isEmpty){
                    numOfEmpty++;
                } else {
                    numOfEmpty = 0;
                }
            }

            // if not enough space, we need to tick someone out
            if (numOfEmpty < requiredPage){

            }
        }
        // call put to put on physical memory

        // keep it clean
    }

    public void write(){
        // check whether can write successfully

        // check whether need to call kick function or not

        // call put to write on physical memory

        // keep it dirty

    }

    public void kick(int requiredPages){
        // check the one which is LRU, and kick it

        // if more than one, kick the one which is clean
        // if all clean or dirty, randomly kick

//        Page LRUPage = pageArr[0];
//
//        for (Page p : pageArr){
//            if (p.frequency < LRUPage.frequency){
//                LRUPage = p;
//            } else if(p.frequency == LRUPage.frequency){
//                if (p.isClean && !LRUPage.isClean){
//                    LRUPage = p;
//                } else if ((!p.isClean && !LRUPage.isClean) || (p.isClean && LRUPage.isClean)){
//                    Random rand = new Random();
//                    LRUPage = (rand.nextInt() % 2 == 0) ? p : LRUPage;
//                }
//            }
//        }



    }

    public void put(){
        // find the empty space

        // put it there
    }


}
