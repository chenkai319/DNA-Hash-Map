/**
 * BucketInfo is an object that hold information of
 * each bucket of the hash table. It provides starting index
 * ending index, current index of the bucket and few more
 * methods
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 */
class BucketInfo {
    private int startIndex;
    private int endIndex;
    private int freeSlots;


    /**
     * Default constructor
     */
    BucketInfo() {
        startIndex = 0;
        endIndex = 0;
        freeSlots = 0;
    }


    /**
     * Parameterize constructor
     * 
     * @param startIndx
     *            bucket starting index
     * @param endIndx
     *            bucket ending index
     * @param slots
     *            number of slots in a bucket
     */
    BucketInfo(int startIndx, int endIndx, int slots) {
        startIndex = startIndx;
        endIndex = endIndx;
        freeSlots = slots;
    }


    /**
     * Check to see if bucket is full
     * 
     * @return true if bucket is full
     */
    boolean isFull() {
        return freeSlots == 0;
    }


    /**
     * Get starting index
     * 
     * @return the starting index
     */
    int getStartIndex() {
        return startIndex;
    }


    /**
     * Get ending index
     * 
     * @return get the ending index
     */
    int getEndIndex() {
        return endIndex;
    }


    /**
     * subtract the total number of slot by 1
     */
    void fillASlot() {
        freeSlots--;
    }


    /**
     * Increase the total number of available slot by 1
     */
    void emptyASlot() {
        freeSlots++;
    }
}
