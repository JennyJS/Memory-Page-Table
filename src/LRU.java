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
            boolean keepClean = true;
            readFromPageTable(readIndex, length, keepClean);
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

        writeBackToPageTable(pages, insertIndex, insertIndex + length/pageSize, true);
    }

    public void write(int address, int length){
        //translate the address and length to page numbers in logical memory
        List<Integer> pages = getPageIndexesInLogicalMem(address, length);

        // check whether can write directly on main page table
        int writeIndex = findPagesFromPageTable(pages);
        if (writeIndex > 0) {
            boolean keepClean = false;
            readFromPageTable(writeIndex, length, keepClean);
            return;
        }

        //read from logical memory happens here...

        //insert page to page table
        int insertIndex = findEmptyBlockInPageTable(pages);

        if (insertIndex < 0){
            // not enough empty space, need to kick off according LRU algorithm
            insertIndex = findLRUPagesToKickOff(pages);
            kickPages(pages, insertIndex);
        }

        writeBackToPageTable(pages, insertIndex, insertIndex + length/pageSize, false);
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

    private void writeBackToPageTable (List<Integer> pages, int start, int end, boolean isClean){
        int indexOfPages = 0;
        for (int i = start; i <= end; i++){
            pageArr[i].pageNo = pages.get(indexOfPages);
            indexOfPages++;
            pageArr[i].isClean = isClean;
        }
        updateFrequency(true, start, end, isClean);
        updateFrequency(false, start, end, isClean);

    }

    /**
     * if outDate is true, appending 1 to the front; if false, appending 0 to the rest.
     **/

    public void updateFrequency (boolean outDate, int start, int end, boolean isClean) {
        if (outDate){
            for (int i = start; i < end; i++){
                pageArr[i].isClean = isClean;
                pageArr[i].frequency >>= 1;
                pageArr[i].frequency |= 1 << (pageSize - 1);
            }
        } else {
            for (int m = 0; m < start; m++){
                pageArr[m].frequency = pageArr[m].frequency >> 1;
            }

            for (int n = end; n < pageArr.length; n++){
                pageArr[n].frequency = pageArr[n].frequency >> 1;
            }
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

    /**
     * Magically get data from page table
     * Update pages frequency
     * */
    private void readFromPageTable(int startIndex, int length, boolean isClean) {

        updateFrequency(true, startIndex, startIndex + length/pageSize, isClean);
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
