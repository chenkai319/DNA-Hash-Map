import java.io.IOException;

/**
 * DNA Table is an extension of hash table
 * that takes the key and item as string data type
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 */
public class DNATable extends HashTable<String, String> {
    private MemoryManager memMgr;


    /**
     * Default constructor
     */
    public DNATable() {
        hashTB = new DNARecord[0];
        memMgr = new MemoryManager();
    }


    /**
     * Parameterize constructor
     * 
     * @param size
     *            size of the hash table
     * @param binFileAddr
     *            path of memory file
     * @throws IOException
     *             file access exception
     */
    public DNATable(int size, String binFileAddr) throws IOException {
        hashTB = new DNARecord[size];
        memMgr = new MemoryManager(binFileAddr);
        int begin = 0;
        for (int i = 0; i < size / 32; i++) {
            buckets.add(new BucketInfo(begin, begin + 32 - 1, 32));
            begin += 32;
        }
    }


    @Override
    /**
     * Insert item into hash table
     * 
     * @param key
     *            the key of the item
     * @param input
     *            the item
     * @return true if insert successful
     * @throws Exception
     *             file access exception
     */
    public boolean insert(String key, String dnaSeq) throws Exception {
        int pos = (int)sfold(key, hashTB.length);
        BucketInfo bucket = buckets.get(getBucketNum(pos));
        for (int i = bucket.getStartIndex(); i <= bucket.getEndIndex(); i++) {
            if (hashTB[i] != null && memMgr.matchKey(key,
                (DNARecord)hashTB[i])) {
                System.out.println("SequenceID " + key + " exists");
                return false;
            }
        }
        if (bucket.isFull()) {
            System.out.println("Bucket full.Sequence " + key
                + " could not be inserted");
            return false;
        }

        DNARecord record = memMgr.insertDNAToFile(key, dnaSeq);
        while (true) {
            if (hashTB[pos] == null) {
                hashTB[pos] = record;
                break;
            }

            pos++;
            if (pos > bucket.getEndIndex()) {
                pos = bucket.getStartIndex();
            }
        }
        bucket.fillASlot();

        return true;
    }


    @Override
    /**
     * Remove item from the hash table
     * 
     * @param key
     *            the key of the item
     * @return true if item is removed successfully
     * @throws Exception
     *             access file exception
     */
    public boolean remove(String key) throws Exception {
        int pos = (int)sfold(key, hashTB.length);
        BucketInfo bucket = buckets.get(getBucketNum(pos));
        Integer itemPos = this.searchHelper(key);

        if (itemPos == null) {
            System.out.println("SequenceID " + key + " not found");
            return false;
        }

        System.out.println("Sequence Removed " + key + ":");
        DNARecord record = (DNARecord)hashTB[itemPos];
        System.out.println(memMgr.getSeq(record));
        memMgr.removeDNAInFile(record);
        hashTB[itemPos] = null;
        bucket.emptyASlot();
        return true;
    }


    @Override
    /**
     * Search helper function
     * 
     * @param key
     *            the searching key
     * @return the index of the item inside the hash table
     * @throws Exception
     *             access file exception
     */
    public Integer searchHelper(String key) throws Exception {
        int pos = (int)sfold(key, hashTB.length);
        BucketInfo bucket = buckets.get(getBucketNum(pos));
        for (int i = bucket.getStartIndex(); i <= bucket.getEndIndex(); i++) {
            if (hashTB[i] != null && memMgr.matchKey(key,
                (DNARecord)hashTB[i])) {
                return i;
            }
        }
        return null;
    }


    @Override
    /**
     * Search an item based on given key
     * 
     * @param key
     *            the key of the item
     * @return true if the item is found
     * @throws Exception
     *             access file exception
     */
    public boolean search(String key) throws Exception {
        Integer itemPos = searchHelper(key);
        if (itemPos == null) {
            System.out.println("SequenceID " + key + " not found");
            return false;
        }
        System.out.println("Sequence Found: " + memMgr.getSeq(
            (DNARecord)hashTB[itemPos]));
        return true;
    }


    /**
     * Print function that print out hash table
     * and memory file information
     * 
     * @throws Exception
     *             file access exception
     */
    public void print() throws Exception {
        System.out.println("Sequence IDs:");
        for (int i = 0; i < hashTB.length; i++) {
            if (hashTB[i] != null) {
                DNARecord record = (DNARecord)hashTB[i];
                System.out.println(memMgr.getId(record) + ": hash slot " + "["
                    + i + "]");
            }
        }
        memMgr.printFreeBlock();
    }


    /**
     * Clear the hash table and memory file
     * 
     * @throws Exception
     *             file access exception
     */
    public void clear() throws Exception {
        for (int i = 0; i < hashTB.length; i++) {
            hashTB[i] = null;
        }
        memMgr.clear();
    }
}
