/**
 * DNA record is a class that contain the information
 * of a record. The user can access the actual data
 * from memory manager given the DNA record
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 *
 */
class DNARecord {
    private int idStartPos;
    private int idLength; // unit is bit
    private int sqStartPos;
    private int sqLength;


    /**
     * Default Constructor
     */
    DNARecord() {
        idStartPos = 0;
        sqStartPos = 0;
        idLength = 0;
        sqLength = 0;
    }


    /**
     * Parameterize constructor
     * 
     * @param idStart
     *            id starting position
     * @param sqStart
     *            sequence starting position
     * @param idSize
     *            id size in bits
     * @param sqSize
     *            sequence size in bits
     */
    DNARecord(int idStart, int sqStart, int idSize, int sqSize) {
        idStartPos = idStart;
        sqStartPos = sqStart;
        idLength = idSize;
        sqLength = sqSize;
    }


    /**
     * Get the sequence starting position
     * 
     * @return starting position
     */
    public int getIdStartPos() {
        return idStartPos;
    }

    /**
     * Get starting position
     * @return starting position
     */
    public int getSeqStartPos() {
        return sqStartPos;
    }


    /**
     * Get id size in byte
     * 
     * @return id size in byte
     */
    public int idSizeByte() {
        return (int)Math.ceil(idLength / 8.0);
    }


    /**
     * Get id size in bit
     * 
     * @return id size in bit
     */
    public int idSizeBit() {
        return idLength;
    }
    
    /**
     * Get sequence size in bit
     * @return sequence size in bit
     */
    public int seqSizeBit() {
        return sqLength;
    }


    /**
     * get sequence size in byte
     * 
     * @return sequence size in byte
     */
    public int seqSizeByte() {
        return (int)Math.ceil(sqLength / 8.0);
    }


    /**
     * Get record size in byte
     * 
     * @return record size in byte
     */
    public int totalSizeByte() {
        return (int)Math.ceil((idLength + sqLength) / 8.0);
    }


    /**
     * Get record size in bit
     * 
     * @return record size in bit
     */
    public int totalSizeBit() {
        return idLength + sqLength;
    }
}
