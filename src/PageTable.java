

import java.util.*;


/**
 * Created by jenny on 10/10/15.
 */
public class PageTable {

    final Page[] pageArr;
    private final long pageSize;
    private final PageKicker kicker;

    private static PageTable pageTable;

    private PageTable(int pageCount, long pageSize, PageKicker kicker){
        pageArr = new Page[pageCount];
        for (int i = 0; i < pageCount; i++){
            Page p = new Page();
            p.reset();
            pageArr[i] = p;
        }
        this.pageSize = pageSize;
        this.kicker = kicker;
    }


    public static void init (int pageCount, long pageSize, PageKicker kicker){
        pageTable = new PageTable(pageCount, pageSize, kicker);
    }

    public static PageTable getInstance(){
        return pageTable;
    }

    /**
     * Page represents the memory unit in page table
     * */
    public static class Page {
        boolean isClean = true;
        int pageNo;
        long frequency;

        private void reset() {
            isClean = true;
            pageNo = -1;
            frequency = 0;
        }

        private boolean isEmpty() {
            return pageNo < 0;
        }
    }


    public void process(Operation o, int wordSize) {
        System.out.println(o.type.name() + "(0x" + Long.toHexString(o.address) + " , " + o.length + "B)");
        if ((o.address + o.length) > Math.pow(2, wordSize)){
            throw new IllegalArgumentException("Address : 0X" + Long.toHexString(o.address) + " Length : 0X" + Long.toHexString(o.length) + " out of bound!");
        }
        if (o.type == Operation.Type.read) {
            PageTable.getInstance().read(o.address, o.length);
        } else {
            PageTable.getInstance().write(o.address, o.length);
        }
        System.out.println();
    }


    private void read(long address, long length){
        List<Integer> pages = getPageIndexesInLogicalMem(address, length);
        // check whether can read directly from main page table
        int readIndex = findPagesFromPageTable(pages);

        //if everything we need to read is in page table, update the frequencies of the pages
        if (readIndex == 0) {
            readFromPageTable(pages, true);
            return;
        }

        //read from logical memory happens here...

        //check my many empty spot left in page table, if smaller than the number we need, call LRU to kick sb out, till enough space
        List<Integer> emptyList = findEmptyBlockInPageTable();
        List<Integer> kickList;
        if (emptyList.size() < readIndex){
            kickList = kicker.findPagesToKickOff(readIndex - emptyList.size());
            //real kick
            kickPages(kickList);
        }

        //write back to page table
        writeBackToPageTable(pages, true);
    }

    private void write(long address, long length){
        //translate the address and length to page numbers in logical memory
        List<Integer> pages = getPageIndexesInLogicalMem(address, length);

        // check whether can write directly on main page table
        int writeIndex = findPagesFromPageTable(pages);

        //if everything we need to read is in page table, update the frequencies of the pages
        if (writeIndex == 0) {
            readFromPageTable(pages, false);
            return;
        }

        //read from logical memory happens here...

        //insert page to page table
        List<Integer> emptyList = findEmptyBlockInPageTable();
        List<Integer> kickList;
        if (emptyList.size() < writeIndex){
            kickList = kicker.findPagesToKickOff(writeIndex - emptyList.size());
            //kick
            kickPages(kickList);
        }

        //write back to page table
        writeBackToPageTable(pages, false);
    }

    /**
     * Get logical page indexes
     * */
    private List<Integer> getPageIndexesInLogicalMem(long address, long length) {
        List<Integer> pages = new LinkedList<>();
        int start = (int) Math.floor(address / pageSize);
        int end = (int) (Math.ceil((address + length)/pageSize));
        for (int i = start; i <= end; i++){
            pages.add(i);
        }

        if (pages.size() > pageArr.length){
            throw new IllegalArgumentException("Trying to get " + pages.size() + " pages" + " that exceeds physical memory size (" + pageArr.length + " pages)!");
        }
        return pages;
    }

    /**
     * Find the pages to kick (LRU rule)
     * */
    private void kickPages(List<Integer> kickList){
        for (int i = 0; i < pageArr.length; i++) {
            for (Integer integer : kickList) {
                if (pageArr[i].pageNo == integer) {
                    if (!pageArr[i].isClean) {
                        System.out.println("\tWROTE_BACK \t: Logical_Page_No." + pageArr[i].pageNo + " to logical memory");
                    }
                    System.out.println("\tKICKED \t: Logical_Page_No." + pageArr[i].pageNo + " \tPhysical_Page_Index: " + i);
                    pageArr[i].reset();
                }
            }
        }
    }

    private void writeBackToPageTable (List<Integer> pages, boolean isClean){
        int indexOfPages = 0;
        for (Page aPageArr : pageArr) {
            if (aPageArr.isEmpty()) {
                aPageArr.pageNo = pages.get(indexOfPages);
                indexOfPages++;
                aPageArr.isClean = isClean;
                break;
            }
        }

        updateFrequency(pages, isClean);
    }

    /**
     * find the pages we need from page table and append 1 to the front of those pages,
     * append 0 to the rest
     **/

    private void updateFrequency(List<Integer> pages, boolean isClean) {
        for (Integer integer : pages){
            for(int i = 0; i < pageArr.length; i++){
                if (pageArr[i].pageNo == integer){
                    if (!isClean) {
                        pageArr[i].isClean = false;
                    }
                    pageArr[i].frequency >>= 1;
                    pageArr[i].frequency |= 1 << (pageArr.length - 1);
                    if (isClean){
                        System.out.println("\tREAD \t: Logical_Page_No." + integer + " \tPhysical_Page_Index: " + i + " :" + (pageArr[i].isClean ? "CLEAN" : "DIRTY"));
                    } else {
                        System.out.println("\tWROTE \t: Logical_Page_No." + integer + " \tPhysical_Page_Index: " + i + " :" + (pageArr[i].isClean ? "CLEAN" : "DIRTY"));
                    }
                } else {
                    pageArr[i].frequency >>= 1;
                }
            }
        }
    }


    /**
     * Check if page table contains data need to read,,
     * return the number of pages need to lead from logical memory
     * if we don't need to read load from logical memory, return 0
     * */
    private int findPagesFromPageTable(List<Integer> pages){
        int num = pages.size();
        for (Page aPageArr : pageArr) {
            for (Integer integer : pages) {
                if (aPageArr.pageNo == integer) {
                    num--;
                }
            }
        }

        return num;
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
        for (Page aPageArr : pageArr) {
            if (aPageArr.isEmpty()) {
                emptyPages.add(aPageArr.pageNo);
            }
        }

        return emptyPages;
    }
}
