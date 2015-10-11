import java.util.*;

/**
 * Created by jenny on 10/10/15.
 */
public class LRU {
    
    // use an array of Nodes to represent the physical memory

    private static class Page {
        boolean isClean = true;
        int pageNo;
        int frequency;

        private void reset() {
            isClean = true;
            pageNo = -1;
            frequency = -1;
        }

        private boolean isEmpty() {
            return pageNo < 0;
        }
    }
    
    final Page[] pageArr;
    int pageSize;
    
    private LRU (int pageCount, int pageSize){
        pageArr = new Page[pageCount];
        this.pageSize = pageSize;
    }

    /**
     * Get logical page indexes
     * */
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
        int requiredPage;

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
            requiredPage = (int)Math.ceil(length / pageSize);
            int numOfEmpty = 0;
            for (int i = 0; i < pageArr.length; i++){
                // find the max consecutive empty pages
                if (pageArr[i].isEmpty()){
                    numOfEmpty++;
                } else {
                    numOfEmpty = 0;
                }
            }

            // if not enough space, we need to tick someone out, and put sb in
            // call put to put on physical memory
            // keep it clean

            if (numOfEmpty < requiredPage){
                int startingIndex = kick(pages);
                writeBackToPageTable(pages, startingIndex,startingIndex + requiredPage );
            }
        }

    }

    public void write(){
        // check whether can write successfully

        // check whether need to call kick function or not

        // call put to write on physical memory

        // keep it dirty

    }

    public int kick(List<Integer> pages){
        // check the one which is LRU, and kick it

        // if more than one, kick the one which is clean
        // if all clean or dirty, randomly kick

        int startingIndex = findStartIndexToKickOff(pages);

        //if dirty, write back
        for (int i = startingIndex; i < pages.size(); i++){
            if (!pageArr[i].isClean){
                System.out.println("Writing back to logical memory first for page " + pageArr[i].pageNo);
                pageArr[i].reset();
            }
        }

        return startingIndex;

        // Now we know to kick from startIndexToKick
    }


    public int findStartIndexToKickOff(List<Integer> pages){
        int leastFrequency = 0;
        int cleanCount = 0;
        final int count = pages.size();
        int startIndexToKick = -1;


        // Calculate initial frequency count;
        for (int i = 0; i < count; i++) {
            leastFrequency += pageArr[i].frequency;
            if (pageArr[i].isClean) {
                cleanCount++;
            }
        }

        int tmpFreq = leastFrequency;
        int tmpCleanCount = cleanCount;

        int tail = count;
        while(tail < pageArr.length) {
            tmpFreq -= pageArr[tail - count].frequency;
            tmpCleanCount -= pageArr[tail - count].isClean ? 1 : 0;
            tmpFreq += pageArr[tail].frequency;
            tmpCleanCount += pageArr[tail].isClean ? 1 : 0;

            if (tmpFreq < leastFrequency || (tmpFreq == leastFrequency && tmpCleanCount < cleanCount)) {
                startIndexToKick = tail - count + 1;
                leastFrequency = tmpFreq;
            }

            tail++;
        }

        return startIndexToKick;

    }

    private void writeBackToPageTable (List<Integer> pages, int start, int end){
        int indexOfPages = 0;
        for (int i = start; i < end; i++){
            pageArr[i].pageNo = pages.get(indexOfPages);
            indexOfPages++;
            pageArr[i].frequency = 1 << 31; //appending 1
        }


        //appending 0 to the rest of the pageArr

        for (int m = 0; m < start; m++){
            pageArr[m].frequency = pageArr[m].frequency >> 1;
        }

        for (int n = end; n < pageArr.length; n++){
            pageArr[n].frequency = pageArr[n].frequency >> 1;
        }

    }


}
