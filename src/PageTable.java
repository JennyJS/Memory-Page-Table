

import java.util.*;


/**
 * Created by jenny on 10/10/15.
 */
public class PageTable {

    /**
     * Page represents the memory unit in page table
     * */
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


    private static class PageComparator implements Comparator<Page>{
        @Override
        public int compare(Page p1, Page p2) {
            return p1.frequency - p2.frequency;
        }
    }
    
    final Page[] pageArr;
    final int pageSize;

    private static PageTable pageTable;

    private PageTable(int pageCount, int pageSize){
        pageArr = new Page[pageCount];
        this.pageSize = pageSize;
    }

    public static void init (int pageCount, int pageSize){
        pageTable = new PageTable(pageCount, pageSize);
    }

    public static PageTable getPageTable(){
        return pageTable;
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
        // check whether can read directly from main page table
        int readIndex = findPagesFromPageTable(pages);

        //if everything we need to read is in page table, update the frequencies of the pages
        if (readIndex == 0) {
            boolean keepClean = true;
            readFromPageTable(pages, keepClean);
            return;
        }

        //read from logical memory happens here...

        //check my many empty spot left in page table, if smaller than the number we need, call LRU to kick sb out, till enough space
        List<Integer> emptyList = findEmptyBlockInPageTable();
        List<Integer> kickList = new LinkedList<>();
        if (emptyList.size() < readIndex){
            kickList = findLRUPagesToKickOff(readIndex - emptyList.size());
        }

        //real kick
        kickPages(kickList);

        //write back to page table
        writeBackToPageTable(pages, true);

        //***************************

    }

    public void write(int address, int length){
        //translate the address and length to page numbers in logical memory
        List<Integer> pages = getPageIndexesInLogicalMem(address, length);

        // check whether can write directly on main page table
        int writeIndex = findPagesFromPageTable(pages);

        //if everything we need to read is in page table, update the frequencies of the pages
        if (writeIndex == 0) {
            boolean keepClean = false;
            readFromPageTable(pages, keepClean);
            return;
        }

        //read from logical memory happens here...

        //insert page to page table
        List<Integer> emptyList = findEmptyBlockInPageTable();
        List<Integer> kickList = new LinkedList<>();
        if (emptyList.size() < writeIndex){
            kickList = findLRUPagesToKickOff(writeIndex - emptyList.size());
        }

        //kick
        kickPages(kickList);

        //write back to page table
        writeBackToPageTable(pages, true);
    }


    /**
     * Find the pages to kick (LRU rule)
     * */
    public void kickPages(List<Integer> kickList){
        for (int i = 0; i < pageArr.length; i++) {
            for (Integer integer : kickList) {
                if (pageArr[i].pageNo == integer) {
                    if (!pageArr[i].isClean) {
                        System.out.println("Writing back to logical memory first for page " + pageArr[i].pageNo);
                    }
                    pageArr[i].reset();
                }
            }
        }

    }


    private List<Integer> findLRUPagesToKickOff(int numberToKick){
        List<Integer> kickList = new LinkedList<>();

        PageComparator pageComparator = new PageComparator();
        PriorityQueue<Page> queue = new PriorityQueue<>(10,pageComparator);
        for (int i = 0 ; i < pageArr.length; i++){
            queue.add(pageArr[i]);
        }

        for (int j = 0; j < numberToKick; j++){
            Page p = queue.peek();
            kickList.add(p.pageNo);
        }

        return kickList;
    }

    private void writeBackToPageTable (List<Integer> pages, boolean isClean){
        int indexOfPages = 0;
        for (int i = 0; i < pageArr.length; i++){
            if (pageArr[i].isEmpty()){
                pageArr[i].pageNo = pages.get(indexOfPages);
                indexOfPages++;
                pageArr[i].isClean = isClean;
            }
        }

        updateFrequency(pages, isClean);
    }

    /**
     * find the pages we need from page table and append 1 to the front of those pages,
     * append 0 to the rest
     **/

    public void updateFrequency (List<Integer> pages, boolean isClean) {
        for (int i = 0; i < pageArr.length; i++){
            for(Integer integer : pages){
                if (pageArr[i].pageNo == integer){
                    pageArr[i].isClean = isClean;
                    pageArr[i].frequency >>= 1;
                    pageArr[i].frequency |= 1 << (pageSize - 1);
                } else {
                    pageArr[i].frequency >>= 1;
                }
            }
        }
    }


    /**
     * Check if page table contains data need to read,
     * return 0 is page table contains all,
     * else return the number of pages need to lead from logical memory
     * */
    private int findPagesFromPageTable(List<Integer> pages){
        int num = pages.size();
        for (int i = 0; i < pageArr.length; i++){
            for (Integer integer : pages){
                if (pageArr[i].pageNo == integer){
                    num--;
                }

            }
        }

        return pages.size() - num;
    }

    /**
     * Magically get data from page table
     * Update pages frequency
     * */
    private void readFromPageTable(List<Integer> pages, boolean isClean) {

        updateFrequency(pages, isClean);
    }

    /**
     * Search in page table to see if we can find enough consecutive pages, return -1,if not found
     * */
    private List<Integer> findEmptyBlockInPageTable() {
        List<Integer> emptyPages = new LinkedList<>();
        for (int i = 0; i < pageArr.length; i++){
            if (pageArr[i].isEmpty()){
                emptyPages.add(pageArr[i].pageNo);
            }
        }

        return emptyPages;
    }
}
