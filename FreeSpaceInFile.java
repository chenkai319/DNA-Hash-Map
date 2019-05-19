/**
 * This class is used for free spaces in the file
 * Used by the linkedList in memory manager class
 * 
 * @author Chenkai Ren
 * @author Tran Hung
 * @version 1.0
 */
public class FreeSpaceInFile {

    private long start;
    private int size;


    /**
     * this is the constructor which tells the starting
     * position in the file and total length avaliable
     * 
     * @param startingIndex
     *            start position
     * @param length
     *            the total length available
     */
    public FreeSpaceInFile(long startingIndex, int length) {
        start = startingIndex;
        size = length;
    }


    /**
     * Set the starting position for the free space
     * 
     * @param pos
     *            starting position
     */
    public void setStartingPos(int pos) {
        start = pos;
    }


    /**
     * this method returns the starting position in the file
     * 
     * @return start
     */
    public long getStaringPos() {
        return start;
    }


    /**
     * this method return the total size
     * 
     * @return size
     */
    public int getTotalSize() {
        return size;
    }


    /**
     * this method can change the size for this segment in the file
     * 
     * @param sz
     *            the shrinked size
     */
    public void changeTotalSize(int sz) {
        size = sz;
    }

}
