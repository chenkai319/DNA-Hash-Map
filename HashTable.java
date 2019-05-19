import java.util.LinkedList;

/**
 * Generic Hash Table
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 *
 * @param <E>
 *            Data type of key
 * @param <T>
 *            Data type of item
 */
public class HashTable<E extends Comparable<E>, T extends Comparable<T>> {
    /**
     * The array of has table
     */
    protected Object[] hashTB;
    /**
     * The list of hash buckets
     */
    protected LinkedList<BucketInfo> buckets;


    /**
     * Default constructor
     */
    public HashTable() {
        hashTB = new Object[0];
        buckets = new LinkedList<BucketInfo>();
    }


    /**
     * Parameterize constructor
     * 
     * @param size
     *            size of the hash table
     */
    public HashTable(int size) {
        hashTB = new Object[size];
        buckets = new LinkedList<BucketInfo>();
        int begin = 0;
        for (int i = 0; i < size / 32; i++) {
            buckets.add(new BucketInfo(begin, begin + 32 - 1, 32));
            begin += 32;
        }
    }


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
    public boolean insert(E key, T input) throws Exception {
        int pos = (int)sfold(key.toString(), hashTB.length);
        BucketInfo curBucket = buckets.get(getBucketNum(pos));

        if (curBucket.isFull()) {
            System.out.println("Bucket full.Sequence " + key.toString()
                + " could not be inserted");
            return false;
        }

        for (int i = curBucket.getStartIndex(); i <= curBucket
            .getEndIndex(); i++) {
            if (hashTB[i] != null && hashTB[i] == input) {
                System.out.println("SequenceID " + key.toString() + " exists");
                return false;
            }
        }

        while (true) {
            if (hashTB[pos] == null) {
                hashTB[pos] = input;
                break;
            }

            pos++;
            if (pos > curBucket.getEndIndex()) {
                pos = curBucket.getStartIndex();
            }
        }

        curBucket.fillASlot();
        return true;
    }


    /**
     * Remove item from the hash table
     * 
     * @param key
     *            the key of the item
     * @return true if item is removed successfully
     * @throws Exception
     *             access file exception
     */
    public boolean remove(E key) throws Exception {
        int pos = (int)sfold(key.toString(), hashTB.length);
        BucketInfo curBucket = buckets.get(getBucketNum(pos));
        Integer itemPos = this.searchHelper(key);

        if (itemPos == null) {
            System.out.print("No Item exits");
            return false;
        }

        hashTB[itemPos] = null;
        curBucket.emptyASlot();
        return true;
    }


    /**
     * Search helper function
     * 
     * @param key
     *            the searching key
     * @return the index of the item inside the hash table
     * @throws Exception
     *             access file exception
     */
    public Integer searchHelper(E key) throws Exception {
        int pos = (int)sfold((String)key, hashTB.length);
        BucketInfo curBucket = buckets.get(getBucketNum(pos));
        for (int i = curBucket.getStartIndex(); i <= curBucket
            .getEndIndex(); i++) {
            if (hashTB[i] != null && hashTB[i].toString().compareTo(
                (String)key) == 0) {
                return i;
            }
        }
        return null;
    }


    /**
     * Search an item based on given key
     * 
     * @param key
     *            the key of the item
     * @return true if the item is found
     * @throws Exception
     *             access file exception
     */
    public boolean search(E key) throws Exception {
        Integer itemPos = searchHelper(key);
        if (itemPos == null) {
            System.out.print("Not Found");
            return false;
        }
        System.out.print("Found " + key.toString());
        return true;
    }


    /**
     * Get the bucket number based on item position
     * 
     * @param index
     *            item position
     * @return the bucket number
     */
    public int getBucketNum(int index) {
        return (int)Math.floor(index / 32.0);
    }


    /**
     * Hashing function takes a string and the size of hash table, then generate
     * an index number for that string
     * 
     * @param s
     *            the string input
     * @param m
     *            size of the hash table
     * @return the index of the string
     */
    public long sfold(String s, int m) {
        int intLength = s.length() / 4;
        long sum = 0;
        for (int j = 0; j < intLength; j++) {
            char[] c = s.substring(j * 4, (j * 4) + 4).toCharArray();
            long mult = 1;
            for (int k = 0; k < c.length; k++) {
                sum += c[k] * mult;
                mult *= 256;
            }
        }

        char[] c = s.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
        }

        sum = (sum * sum) >> 8;
        return (Math.abs(sum) % m);
    }

}
