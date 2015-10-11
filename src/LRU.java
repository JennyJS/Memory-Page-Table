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
    final int pageSize;
    
    private LRU (int pageCount, int pageSize){
        pageArr = new Page[pageCount];
        this.pageSize = pageSize;
    }

    /**
     * Get logical page indexes
     * */
    private List<Integer> getPageIndexesInLogicalMem(int address, int length) {
        List<Integer> pages = new LinkedList<>();
        int start = (int) Math.floor(address / pageSize);
        int end = (int) (Math.ceil((address + length)/pageSize) - 1);
        for (int i = start; i <= end; i++){
            pages.add(i);
        }
        return pages;
    }

    
    public void read(int address, int length){

        List<Integer> pages = getPageIndexesInLogicalMem(address, length);
        int readIndex = findPagesFromPageTable(pages);
        if (readIndex >= 0) {
            readFromPageTable(readIndex, length);
            return;
        }

        // read from logical memory code magically happens here ...

        // Insert pages to page table
        int insertIndex = findEmptyBlockInPageTable(pages);

        if (insertIndex < 0){
            // We don't have enough empty space in page table, need to kick off LRU
            insertIndex = findLRUPagesToKickOff(pages);
            kickPages(pages, insertIndex);
        }

        writeBackToPageTable(pages, insertIndex, insertIndex + length);
    }

    public void write(){
        // check whether can write successfully

        // check whether need to call kick function or not

        // call put to write on physical memory

        // keep it dirty

    }


    /**
     * Find consecutive pages to kick (LRU rule) and return starting index kick
     * */
    public int kickPages(List<Integer> pages, int kickStartIndex){
        for (int i = kickStartIndex; i < pages.size(); i++){
            if (!pageArr[i].isClean){
                System.out.println("Writing back to logical memory first for page " + pageArr[i].pageNo);
                pageArr[i].reset();
            }
        }

        return kickStartIndex;
    }


    private int findLRUPagesToKickOff(List<Integer> pages){
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
        for (int i = start; i <= end; i++){
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


    /**
     * Check if page table contains data need to read
     * */
    private int findPagesFromPageTable(List<Integer> pages){
        for (int i = 0; i < pageArr.length - pages.size(); i++){
            boolean found = true;
            for (int j = 0; j < pages.size(); j++){
                if (pageArr[i + j].pageNo != pages.get(j)){
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }

        return -1;
    }

    private void readFromPageTable(int startIndex, int length) {
        // Update frequency
    }

    /**
     * Search in page table to see if we can find enough consecutive pages, return -1,if not found
     * */
    private int findEmptyBlockInPageTable(List<Integer> pages) {

        int startingIndex = -1;
        int numOfEmpty = 0;
        for (int i = 0; i < pageArr.length; i++){
            // find the max consecutive empty pages
            if (pageArr[i].isEmpty()){
                if (startingIndex == -1){
                    startingIndex = i;
                }
                numOfEmpty++;

                if (numOfEmpty >= pages.size()){
                    return startingIndex;
                }

            } else {
                numOfEmpty = 0;
                startingIndex = -1;
            }
        }
        return -1;
    }
}
