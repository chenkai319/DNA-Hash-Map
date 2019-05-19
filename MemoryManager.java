import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * this class is a memory manager for memory file.
 * This class will communicate with the DNA Hash Table which
 * DNA record should be insert and which DNA record should be remove
 * or free space in the memory file so later it when there is a new dna
 * we can overwrite the free spaces.
 * 
 * @author Chenkai Ren
 * @author Tran Hung
 * @version 1.0
 */
class MemoryManager {

    private static RandomAccessFile raf;
    private LinkedList<FreeSpaceInFile> freeSpaces;
    private String memFilePath;
    private int endPointer;


    /**
     * The default constructor to create a freespace linked list
     */
    public MemoryManager() {
        freeSpaces = new LinkedList<FreeSpaceInFile>();
        endPointer = 0;
    }


    /**
     * Secondary Constructor for Memory Manager
     * Which takes in a file which is the memory file to do the insert
     * function.
     * 
     * @param fileAddr
     *            address to open up a file (argv[x])
     * @throws IOException
     *             if any thing goes wrong with fileaddr
     */
    public MemoryManager(String fileAddr) throws IOException {
        raf = new RandomAccessFile(fileAddr, "rw");
        memFilePath = fileAddr;
        if (raf == null) {
            throw new FileNotFoundException(
                "Memory File for Memory Manager is not found");
        }
        freeSpaces = new LinkedList<FreeSpaceInFile>();
        endPointer = 0;
    }


    /**
     * 
     * This method will insert a new DNA to file
     * it will passing the dna ID(ex. ACGT) seq(ACGT..) and their length
     * 1. we will return a DNA Record object for hash table to keep in that
     * specific slot
     * 2. convert given binary id and binary seq into bytes array
     * 3. check if there are free spaces in the linkedlist
     * 3a. if there is free space write to given space
     * 3b. update DNA record obj with correct position
     * 4. if there is no free space, start write begin the end of file
     * 
     * @param id
     *            the id string
     * @param seq
     *            the sequence string
     * @return dnaRecord information
     * @throws IOException
     *             if accessing file is permitted
     */
    public DNARecord insertDNAToFile(String id, String seq) throws IOException {

        return new DNARecord(insertStrToFile(id), insertStrToFile(seq), id
            .length() * 2, seq.length() * 2);

    }


    /**
     * Insert a string into memory file in away that use the least
     * memory
     * 
     * @param item
     *            string item
     * @return the starting position of that string in memory file
     * @throws IOException
     *             file access exception
     */
    public int insertStrToFile(String item) throws IOException {
        byte[] strBuf = strToBin(item);
        for (int i = 0; i < freeSpaces.size(); i++) {

            int startPos = (int)freeSpaces.get(i).getStaringPos();
            int blkSize = freeSpaces.get(i).getTotalSize();

            if (blkSize >= strBuf.length) {
                raf.seek(startPos);
                raf.write(strBuf);

                // see if free space size is now 0 (ex. size == 0)
                int newSize = blkSize - strBuf.length;
                if (newSize == 0) {
                    freeSpaces.remove(i);
                }

                else {
                    freeSpaces.get(i).changeTotalSize(newSize);
                    freeSpaces.get(i).setStartingPos((int)raf.getFilePointer());
                }
                freeSpaceSort(freeSpaces);
                return startPos;
            }
        }

        // seek to the end of the file;
        int startPos = endPointer;
        raf.seek(startPos);
        raf.write(strBuf);
        freeSpaceSort(freeSpaces);
        endPointer += strBuf.length;
        return startPos;
    }


    /**
     * this function to get the length of bytes based
     * on string.
     * 
     * @param input
     *            string input
     * @return the size of the string in size
     */
    public int getLengthByte(String input) {
        return (int)Math.ceil((input.length() * 2) / 8.0);
    }


    /**
     * remove method will simply add a free space in file object in the linked
     * list
     * But also do the merge for free list.
     * case 1. FreeSpaceInFile obj
     * 0-32 33-55 first obj has 33 elements and second obj has 23 elements
     * 0 + 33 + 1 = 34 which is not we wanted.
     * case 2. FreeSpaceInFile Obj
     * 5-10 11-15 first obj has 5 elements and second obj has 5 elements.
     * 
     * @param record
     *            the DNA Record to be remvoved
     * @throws Exception
     *             file access exception
     */
    public void removeDNAInFile(DNARecord record) throws Exception {

        freeSpaces.add(new FreeSpaceInFile(record.getIdStartPos(), record
            .idSizeByte()));
        freeSpaces.add(new FreeSpaceInFile(record.getSeqStartPos(), record
            .seqSizeByte()));

        freeSpaceSort(freeSpaces);
        int index = 0;

        while (index < freeSpaces.size() - 1) {
            int start = (int)freeSpaces.get(index).getStaringPos();
            int currSize = freeSpaces.get(index).getTotalSize();
            int nextSize = freeSpaces.get(index + 1).getTotalSize();
            int end = start + currSize;

            if (end == freeSpaces.get(index + 1).getStaringPos()) {
                freeSpaces.get(index).changeTotalSize(currSize + nextSize);
                freeSpaces.remove(index + 1);
            }
            else {
                index++;
            }
        }

        freeSpaceSort(freeSpaces);

        if (!freeSpaces.isEmpty()) {
            int startPos = (int)freeSpaces.get(freeSpaces.size() - 1)
                .getStaringPos();
            int size = (int)freeSpaces.get(freeSpaces.size() - 1)
                .getTotalSize();
            if (startPos + size == endPointer) {
                endPointer -= size;
                freeSpaces.remove(freeSpaces.size() - 1);

            }
        }
    }


    /**
     * This function is to sort the freespace linked list by
     * Starting index.
     * 
     * @param list
     *            the free space linked list
     */
    public void freeSpaceSort(LinkedList<FreeSpaceInFile> list) {

        for (int i = 0; i < list.size() - 1; i++) {
            for (int k = i + 1; k < list.size(); k++) {
                int start = (int)list.get(i).getStaringPos();
                int compare = (int)list.get(k).getStaringPos();
                if (start > compare) {
                    FreeSpaceInFile temp = list.get(k);
                    list.remove(k);
                    list.add(k, list.get(i));
                    list.remove(i);
                    list.add(i, temp);
                }
            }
        }

    }


    /**
     * This function will check if the given key matches with
     * the key in the dna record
     * 
     * @param key
     *            they given key
     * @param dna
     *            the dna record
     * @return true if the key matches with dna record
     * @throws IOException
     *             input output exception
     */
    public boolean matchKey(String key, DNARecord dna) throws IOException {
        raf.seek(dna.getIdStartPos());
        byte[] buffer = new byte[dna.idSizeByte()];
        raf.readFully(buffer);
        String resultBin = binToStr(buffer, dna.idSizeBit());

        return resultBin.equals(key);

    }


    /**
     * this function will return the String id based on a given DNARecord
     * obj.
     * 
     * @param record
     *            the input DNArecord obj
     * @return return the string
     * @throws Exception
     *             if any file doesn't seek correctly or read incorrectly
     */
    public String getId(DNARecord record) throws Exception {
        byte[] buffer = new byte[record.idSizeByte()];
        raf.seek(record.getIdStartPos());
        raf.readFully(buffer);
        return binToStr(buffer, record.idSizeBit());
    }


    /**
     * This function will return the string sequence based on the given
     * DNARecord
     * obj.
     * 
     * @param record
     *            the input DNArecord obj
     * @return return the sequence string
     * @throws Exception
     *             if any file doesn't seek correctly or read incorrectly
     */
    public static String getSeq(DNARecord record) throws Exception {
        byte[] seqBuf = new byte[record.seqSizeByte()];
        raf.seek(record.getSeqStartPos());
        raf.readFully(seqBuf);
        return binToStr(seqBuf, record.seqSizeBit());
    }


    /**
     * this class simply prints the free block based on
     * the free space size in the in free space in linked list
     */
    public void printFreeBlock() {
        if (freeSpaces.size() == 0) {
            System.out.println("Free Block List: none");
            return;
        }
        System.out.println("Free Block List:");
        for (int i = 0; i < freeSpaces.size(); i++) {
            int blockNum = i + 1;
            System.out.println("[Block " + blockNum + "]"
                + " Starting Byte Location: " + freeSpaces.get(i)
                    .getStaringPos() + ", Size " + freeSpaces.get(i)
                        .getTotalSize() + " bytes");
        }
    }


    /**
     * this class with clear the free space in the freelinked lis.
     * 
     * @throws Exception
     *             if any error occur when in the mem file
     */
    public void clear() throws Exception {
        freeSpaces.clear();
        DNAdbase.removeFile(memFilePath);
    }


    // -------------Helpler Method-----------------------------------
    /**
     * Convert a DNA string into byte array
     * 
     * @param input
     *            DNA string
     * @return Buffer array
     */
    public static byte[] strToBin(String input) {
        int bufSize;
        int length = input.length() * 2;
        if (length < 8) {
            bufSize = 1;
        }
        else {
            bufSize = (int)Math.ceil(length / 8.0);
        }

        byte[] buffer = new byte[bufSize];
        int charIndex = 0;
        for (int i = 0; i < bufSize; i++) {
            for (int z = 0; z < 4; z++) {
                char c;
                if (charIndex < input.length()) {
                    c = input.charAt(charIndex);
                }
                else {
                    c = 'Z';
                }

                if (c == 'A') {
                    buffer[i] |= 0;
                }
                else if (c == 'C') {
                    buffer[i] |= 1;
                }
                else if (c == 'G') {
                    buffer[i] |= 2;
                }
                else if (c == 'T') {
                    buffer[i] |= 3;
                }
                else {
                    buffer[i] |= 0;
                }

                if (z < 3) {
                    buffer[i] <<= 2;
                }
                charIndex++;
            }
        }
        return buffer;
    }


    /**
     * Convert Byte array into String for DNA
     * A = 00, C = 01, G = 10, T = 11
     * 
     * @param buffer
     *            the byte array buffer
     * @param length
     *            the length of the string
     * @return the DNA String
     */
    public static String binToStr(byte[] buffer, int length) {
        int totalLength = length;
        String result = "";
        for (int i = 0; i < length / 2; i++) {
            for (int z = 6; z >= 0; z -= 2) {
                byte num = (byte)((buffer[i] >>> z) & 3);
                switch (num) {
                    case 0:
                        result += 'A';
                        break;
                    case 1:
                        result += 'C';
                        break;
                    case 2:
                        result += 'G';
                        break;
                    case 3:
                        result += 'T';
                        break;
                    default:
                        result += "";
                        break;

                }
                totalLength -= 2;
                if (totalLength == 0) {
                    return result;
                }
            }
        }
        return "";
    }

}
