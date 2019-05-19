import java.io.FileWriter;
import java.io.IOException;
import student.TestCase;

/**
 * Testing all the functionality of DNA Table class
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 */
public class DNATableTest extends TestCase {
    private DNATable dnaTable;


    /**
     * Initial test set up
     */
    public void setUp() throws Exception {
        clearMemory();
        dnaTable = new DNATable(64, "memFile.bin");
    }


    /**
     * Test array buffer to string
     * and string to array buffer
     * 00 = A, 01 = C, 10 = G, 11 = T
     */
    public void testConversion() {
        // Convert 2 char string to bin
        byte[] case1 = MemoryManager.strToBin("AC");
        // Convert 3 char string to bin
        byte[] case2 = MemoryManager.strToBin("ACT");
        // Convert 8 characters string to bin
        byte[] case3 = MemoryManager.strToBin("ACGGTACT");
        // Convert 15 char string to bin
        byte[] case4 = MemoryManager.strToBin("ACTCCATTGAAACCC");
        // Convert 38 character string to bin
        byte[] case5 = MemoryManager.strToBin(
            "CCTTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT");
        // Convert 39 character string to bin
        byte[] case6 = MemoryManager.strToBin(
            "CCTTTTCCCCGGGGCCCCCCCCGGGGGGGGTTTTTTTTA");
        // Convert 2 char bin to string
        assertEquals("AC", MemoryManager.binToStr(case1, 4));
        // Convert 3 characters bin to string
        assertEquals("ACT", MemoryManager.binToStr(case2, 6));
        // Convert 8 character bin to string
        assertEquals("ACGGTACT", MemoryManager.binToStr(case3, 16));
        // Convert 15 char string to bin
        assertEquals("ACTCCATTGAAACCC", MemoryManager.binToStr(case4, 30));
        // Convert 38 character string to bin
        assertEquals("CCTTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT", MemoryManager
            .binToStr(case5, 76));
        // Convert 39 character string to bin
        assertEquals("CCTTTTCCCCGGGG" + "CCCCCCCCGGGGGGGGTTTTTTTTA",
            MemoryManager.binToStr(case6, 78));
    }


    /**
     * Test function function
     * 
     * @throws Exception
     *             file access exception
     */
    public void testInsert() throws Exception {
        clearMemory();
        // Test Insert simple element -- pos 22
        assertTrue(dnaTable.insert("ACC", "ACGT"));
        assertTrue(dnaTable.search("ACC"));
        int userPos = dnaTable.searchHelper("ACC");
        int correctPos = (int)dnaTable.sfold("ACC", 64);
        assertEquals(userPos, correctPos);

        // Insert 4 bits item -- pos 17
        assertTrue(dnaTable.insert("C", "G"));
        assertTrue(dnaTable.search("C"));
        userPos = dnaTable.searchHelper("C");
        correctPos = (int)dnaTable.sfold("C", 64);
        assertEquals(userPos, correctPos);

        // Insert 6 bits item -- pos 19
        assertTrue(dnaTable.insert("G", "AC"));
        assertTrue(dnaTable.search("G"));
        userPos = dnaTable.searchHelper("G");
        correctPos = (int)dnaTable.sfold("G", 64);
        assertEquals(userPos, correctPos);

        // Insert 38 bits item -- pos 35
        assertTrue(dnaTable.insert("CCTTT",
            "TCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT"));
        assertTrue(dnaTable.search("CCTTT"));
        userPos = dnaTable.searchHelper("CCTTT");
        correctPos = (int)dnaTable.sfold("CCTTT", 64);
        assertEquals(userPos, correctPos);

        // Insert 39 bits item -- pos 60
        assertTrue(dnaTable.insert("CCTTAT",
            "TCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT"));
        assertTrue(dnaTable.search("CCTTAT"));
        userPos = dnaTable.searchHelper("CCTTAT");
        correctPos = (int)dnaTable.sfold("CCTTAT", 64);
        assertEquals(userPos, correctPos);

        // Insert duplicated key but different sequence
        assertFalse(dnaTable.insert("ACC", "ACGACT"));

        // Insert duplicated sequence but different key -- pos 59
        assertTrue(dnaTable.insert("TTAA", "ACGT"));
        assertTrue(dnaTable.search("TTAA"));
        userPos = dnaTable.searchHelper("TTAA");
        correctPos = (int)dnaTable.sfold("TTAA", 64);
        assertEquals(userPos, correctPos);

        // Insert item at index 0 -- pos 0;
        assertTrue(dnaTable.insert("AAAAAAAAAAAAATA", "ACGT"));
        userPos = dnaTable.searchHelper("AAAAAAAAAAAAATA");
        assertEquals(userPos, 0);

        // Insert item with different id but at the same slot
        // Due to linear probing, position now is 1 -- pos 1
        assertTrue(dnaTable.insert("GGGGGGGGGCGGTCGG", "ACGG"));
        userPos = dnaTable.searchHelper("GGGGGGGGGCGGTCGG");
        assertTrue(dnaTable.search("GGGGGGGGGCGGTCGG"));
        assertEquals(userPos, 1);

        // Insert item at position 31 -- pos 31
        assertTrue(dnaTable.insert("AAAAGAATGACTAAAT", "ACAGG"));
        userPos = dnaTable.searchHelper("AAAAGAATGACTAAAT");
        assertTrue(dnaTable.search("AAAAGAATGACTAAAT"));
        assertEquals(userPos, 31);

        // Insert item with different id but at the same slot in the end of
        // bucket
        // Due to linear probing, position now is 1 -- pos 2
        assertTrue(dnaTable.insert("GGGGGCGTTCAGTAGG", "ACAGG"));
        userPos = dnaTable.searchHelper("GGGGGCGTTCAGTAGG");
        assertTrue(dnaTable.search("GGGGGCGTTCAGTAGG"));
        assertEquals(userPos, 2);

        // fill up one bucket
        // Insert pos -- 3
        assertTrue(dnaTable.insert("GGGGGGGGCAGGTCGG", "ACGG"));
        userPos = dnaTable.searchHelper("GGGGGGGGCAGGTCGG");
        assertTrue(dnaTable.search("GGGGGGGGCAGGTCGG"));
        assertEquals(userPos, 3);

        // Insert pos -- 4
        assertTrue(dnaTable.insert("GGGGGAGGACGGTTGG", "ACGG"));
        userPos = dnaTable.searchHelper("GGGGGAGGACGGTTGG");
        assertTrue(dnaTable.search("GGGGGGGGCAGGTCGG"));
        assertEquals(userPos, 4);

        // Insert pos -- 5
        assertTrue(dnaTable.insert("AAAACAAAGCAAGCAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACAAAGCAAGCAA");
        assertTrue(dnaTable.search("AAAACAAAGCAAGCAA"));
        assertEquals(userPos, 5);

        // Insert pos -- 6
        assertTrue(dnaTable.insert("AAAACCATACATTTAT", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACCATACATTTAT");
        assertTrue(dnaTable.search("AAAACCATACATTTAT"));
        assertEquals(userPos, 6);

        // Insert pos -- 7
        assertTrue(dnaTable.insert("AAAACCATTGAATTAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACCATTGAATTAA");
        assertTrue(dnaTable.search("AAAACCATTGAATTAA"));
        assertEquals(userPos, 7);

        // Insert pos -- 8
        assertTrue(dnaTable.insert("AAAACCATTGATAGAT", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACCATTGATAGAT");
        assertTrue(dnaTable.search("AAAACCATTGATAGAT"));
        assertEquals(userPos, 8);

        // Insert pos -- 9
        assertTrue(dnaTable.insert("AAAACCATTTATTAAT", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACCATTTATTAAT");
        assertTrue(dnaTable.search("AAAACCATTTATTAAT"));
        assertEquals(userPos, 9);

        // Insert pos -- 10
        assertTrue(dnaTable.insert("AAAACGAAAGAATTAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACGAAAGAATTAA");
        assertTrue(dnaTable.search("AAAACGAAAGAATTAA"));
        assertEquals(userPos, 10);

        // Insert pos -- 11
        assertTrue(dnaTable.insert("AAAACTAAGCAATCAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTAAGCAATCAA");
        assertTrue(dnaTable.search("AAAACTAAGCAATCAA"));
        assertEquals(userPos, 11);

        // Insert pos -- 12
        assertTrue(dnaTable.insert("AAAACTAATCAAATAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTAATCAAATAA");
        assertTrue(dnaTable.search("AAAACTAATCAAATAA"));
        assertEquals(userPos, 12);

        // Insert pos -- 13
        assertTrue(dnaTable.insert("AAAACTACGAAAGTAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTACGAAAGTAA");
        assertTrue(dnaTable.search("AAAACTACGAAAGTAA"));
        assertEquals(userPos, 13);

        // Insert pos -- 14
        assertTrue(dnaTable.insert("AAAACTATAAATTAAT", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTATAAATTAAT");
        assertTrue(dnaTable.search("AAAACTATAAATTAAT"));
        assertEquals(userPos, 14);

        // Insert pos -- 15
        assertTrue(dnaTable.insert("AAAACTATGAAATCAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTATGAAATCAA");
        assertTrue(dnaTable.search("AAAACTATGAAATCAA"));
        assertEquals(userPos, 15);

        // Insert pos -- 16
        assertTrue(dnaTable.insert("AAAACTATGAACAGAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTATGAACAGAA");
        assertTrue(dnaTable.search("AAAACTATGAACAGAA"));
        assertEquals(userPos, 16);

        // Insert pos -- 18
        assertTrue(dnaTable.insert("AAAACTATTAAACAAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTATTAAACAAA");
        assertTrue(dnaTable.search("AAAACTATTAAACAAA"));
        assertEquals(userPos, 18);

        // Insert pos -- 20
        assertTrue(dnaTable.insert("AAAACTATTAACCTAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTATTAACCTAA");
        assertTrue(dnaTable.search("AAAACTATTAACCTAA"));
        assertEquals(userPos, 20);
        // Insert pos -- 21

        assertTrue(dnaTable.insert("AAAACTCAGCAAGTAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTCAGCAAGTAA");
        assertTrue(dnaTable.search("AAAACTCAGCAAGTAA"));
        assertEquals(userPos, 21);

        // Insert pos -- 23
        assertTrue(dnaTable.insert("AAAACTCATAAATGAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTCATAAATGAA");
        assertTrue(dnaTable.search("AAAACTCATAAATGAA"));
        assertEquals(userPos, 23);

        // Insert pos -- 24
        assertTrue(dnaTable.insert("AAAACTCATGAACTAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTCATGAACTAA");
        assertTrue(dnaTable.search("AAAACTCATGAACTAA"));
        assertEquals(userPos, 24);

        // Insert pos -- 25
        assertTrue(dnaTable.insert("AAAACTCTTCATTCAT", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTCTTCATTCAT");
        assertTrue(dnaTable.search("AAAACTCTTCATTCAT"));
        assertEquals(userPos, 25);

        // Insert pos -- 26
        assertTrue(dnaTable.insert("AAAACTCTTGAACGAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTCTTGAACGAA");
        assertTrue(dnaTable.search("AAAACTCTTGAACGAA"));
        assertEquals(userPos, 26);
        // Insert pos -- 27
        assertTrue(dnaTable.insert("AAAACTGTGGATGTAT", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTGTGGATGTAT");
        assertTrue(dnaTable.search("AAAACTGTGGATGTAT"));
        assertEquals(userPos, 27);

        // Insert pos -- 28
        assertTrue(dnaTable.insert("AAAACTGTGGCACTAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAACTGTGGCACTAA");
        assertTrue(dnaTable.search("AAAACTGTGGCACTAA"));
        assertEquals(userPos, 28);

        // Insert pos -- 29
        assertTrue(dnaTable.insert("AAAAGAAAAGAATGAA", "ACGG"));
        userPos = dnaTable.searchHelper("AAAAGAAAAGAATGAA");
        assertTrue(dnaTable.search("AAAAGAAAAGAATGAA"));
        assertEquals(userPos, 29);

        // Insert pos -- 30
        assertTrue(dnaTable.insert("AAAAGAATGAATTAAT", "ACGG"));
        userPos = dnaTable.searchHelper("AAAAGAATGAATTAAT");
        assertTrue(dnaTable.search("AAAAGAATGAATTAAT"));
        assertEquals(userPos, 30);

        
        // Insert item into a full bucket
        assertFalse(dnaTable.insert("GGGGGCGTTGGGGAGG", "ACGG"));
        assertFalse(dnaTable.search("GGGGGCGTTGGGGAGG"));
        
        // Remove 1 item in the full bucket then insert it again
        assertTrue(dnaTable.remove("AAAAGAAAAGAATGAA"));
        assertTrue(dnaTable.insert("AAAAGAAAAGAATGAA", "ACGG"));
        assertTrue(dnaTable.search("AAAAGAAAAGAATGAA"));
        System.out.print("\nEnd Insert Test \n \n \n");
        

        
    }


    /**
     * Test remove function
     * 
     * @throws Exception
     *             file access exception
     */
    public void testRemove() throws Exception {
        dnaTable.clear();
        // Remove non existing item
        assertFalse(dnaTable.remove("AAAAAAAAAAAAATAA"));

        // remove item with a table that contain slot 0
        assertTrue(dnaTable.insert("AAAAAAAAAAAAATAA", "ACGG"));
        assertTrue(dnaTable.search("AAAAAAAAAAAAATAA"));
        assertTrue(dnaTable.remove("AAAAAAAAAAAAATAA"));
        assertFalse(dnaTable.search("AAAAAAAAAAAAATAA"));
        dnaTable.print();
        System.out.print('\n');

        // remove slot 1 with a table that contain slot 0, 1
        assertTrue(dnaTable.insert("AAAAAAAAAAAAATAA", "ACGA"));
        assertTrue(dnaTable.insert("AAAAAAAAGAAAGTAA", "ACGG"));
        assertTrue(dnaTable.search("AAAAAAAAAAAAATAA"));
        assertTrue(dnaTable.search("AAAAAAAAGAAAGTAA"));
        assertTrue(dnaTable.remove("AAAAAAAAGAAAGTAA"));
        assertTrue(dnaTable.search("AAAAAAAAAAAAATAA"));
        assertFalse(dnaTable.search("AAAAAAAAGAAAGTAA"));
        dnaTable.print();
        System.out.print('\n');

        // remove slot 1 with a table that contain slot 0, 1, 2
        dnaTable.clear();
        assertTrue(dnaTable.insert("AAAAAAAAAAAAATAA", "ACCC"));
        assertTrue(dnaTable.insert("AAAAAAAAGAAAGTAA", "AGGA"));
        assertTrue(dnaTable.insert("AAAAACAAAGAATTAA", "ACTT"));
        assertTrue(dnaTable.remove("AAAAAAAAGAAAGTAA"));
        assertFalse(dnaTable.search("AAAAAAAAGAAAGTAA"));
        assertTrue(dnaTable.search("AAAAAAAAAAAAATAA"));
        assertTrue(dnaTable.search("AAAAACAAAGAATTAA"));
        dnaTable.print();
        System.out.print('\n');

        // Remove all the remain items
        assertTrue(dnaTable.remove("AAAAAAAAAAAAATAA"));
        assertTrue(dnaTable.remove("AAAAACAAAGAATTAA"));
        assertFalse(dnaTable.search("AAAAAAAAAAAAATAA"));
        assertFalse(dnaTable.search("AAAAACAAAGAATTAA"));
        dnaTable.print();
        System.out.print('\n');

        // Insert Item on slot 2
        assertTrue(dnaTable.insert("AAAAACAAAGAATTAA", "ACTT"));
        dnaTable.print();
        System.out.print('\n');

        // Remove slot 0 and slot 5 then insert item in slot 5 again
        dnaTable.clear();
        assertTrue(dnaTable.insert("AAAAAAAAAAAAATAA", "ACCC"));
        assertTrue(dnaTable.insert("AAAAAAAAGAAAGTAA", "AGGA"));
        assertTrue(dnaTable.insert("AAAAACAAAGAATTAA", "ACTT"));
        assertTrue(dnaTable.insert("AAAAACAAGGAATTAA", "ACCC"));
        assertTrue(dnaTable.insert("AAAAACAAGGACAAAA", "AGGA"));
        assertTrue(dnaTable.insert("AAAACAAAGCAAGCAA", "AC"));
        assertTrue(dnaTable.remove("AAAAAAAAAAAAATAA"));
        assertTrue(dnaTable.remove("AAAACAAAGCAAGCAA"));
        assertTrue(dnaTable.insert("AAAACAAAGCAAGCAA", "ACCCC"));
        dnaTable.print();

        // Insert back slot 0 then check for the free space
        assertTrue(dnaTable.insert("AAAAAAAAAAAAATAA", "ACCC"));
        dnaTable.print();

        System.out.print("\n \n");
        dnaTable.clear();
        dnaTable.print();
    }


    /**
     * More test remove function
     * 
     * @throws Exception
     *             file access exception
     */
    public void testRemove1() throws Exception {
        System.out.print(
            "----------------start testing---------------------------\n");

        System.out.print("below should show no element in the"
            + " hashtable. <1 element in, 1 element out>");
        dnaTable.insert("AAAA", "AAAATTTTCCAAAATTTTCCAA"); // total 1 + 6
        assertTrue(dnaTable.remove("AAAA"));
        dnaTable.print();
        System.out.print("-------------------------\n");
        System.out.print(
            "below should show 5 bytes in free space. <1 element in>\n");
        dnaTable.insert("AAAC", "AAAATTTTCCAAAATTTTCCAAAATTTTCCAA"
            + "AATTTTCCAAAATTTTCCAAAATTTTCCAAAATTTTC"
            + "CAAAATTTTCCAAAATTTTCCAAAATTTTCC");
        dnaTable.print(); // this should have free space 5 bytes start at 1 byte
                          // in the mem file
        // pointer at 26 in mem file
        System.out.println("-------------------------");
        System.out.println("below should show 31 bytes in"
            + " free space. <1 element in, remove 1 element>");
        dnaTable.insert("AAA", "AAAAAAAAA"); // total 1 + 3
        dnaTable.remove("AAAC");
        dnaTable.print();

        System.out.println("-------------------------");
        System.out.println(
            "below should show 5 bytes in free space. <1 element in>");
        dnaTable.insert("AAAC", "AAAATTTTCCAAAATTTTCCAAAATTTTCCAA"
            + "AATTTTCCAAAATTTTCCAAAATTTTCCAA"
            + "AATTTTCCAAAATTTTCCAAAATTTTCCAAAATTTTCC");
        dnaTable.print();

        System.out.println("-------------------------");
        System.out.println("below should show 27 bytes in free"
            + " space. <1 element remove, insert 1 element>");
        dnaTable.remove("AAAC");
        dnaTable.insert("AAA", "AAAAAAAAA");
        dnaTable.print();
    }


    /**
     * Test insert, remove, check memory file 1
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile1() throws Exception {
        System.out.println("\n\n__________ Test Mem File 1___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("AAGT", "CCACGTACGTT"));
        assertTrue(dnaTable.insert("CAACGA", "GT"));
        assertTrue(dnaTable.insert("T", "T"));
        assertTrue(dnaTable.insert("ACCAAC", "CT"));
        assertTrue(dnaTable.search("AAGT"));
        assertTrue(dnaTable.search("CAACGA"));
        assertTrue(dnaTable.search("T"));
        assertTrue(dnaTable.search("ACCAAC"));
        assertTrue(dnaTable.remove("AAGT"));
        assertTrue(dnaTable.remove("T"));
        assertFalse(dnaTable.search("T"));
        assertFalse(dnaTable.search("AAGT"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 4 byte");
        System.out.println("[Block 2] Starting Byte Location: 7, Size 2 byte");
        System.out.println("--End--");
        assertTrue(dnaTable.insert("CATCG", "GTAAT"));
        assertTrue(dnaTable.search("CATCG"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 7, Size 2 byte");
        System.out.println("      ...........End.............");
    }


    /**
     * Test insert, remove, check memory file 2
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile2() throws Exception {
        System.out.println("\n\n__________ Test Mem File 2___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("AAGT", "CCACGTACGTT"));
        assertTrue(dnaTable.insert("CAACGA", "GT"));
        assertTrue(dnaTable.insert("T", "T"));
        assertTrue(dnaTable.insert("ACCAAC", "CT"));
        assertTrue(dnaTable.remove("AAGT"));
        assertTrue(dnaTable.remove("T"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 4 byte");
        System.out.println("[Block 2] Starting Byte Location: 7, Size 2 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.insert("ACCAACAGT", "CTACT"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 3, Size 1 byte");
        System.out.println("      ...........End.............");
    }


    /**
     * Test insert, remove, check memory file 3
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile3() throws Exception {
        System.out.println("\n\n__________ Test Mem File 3___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("A", "C"));
        assertTrue(dnaTable.insert("CCAAC", "GT"));
        assertTrue(dnaTable.insert("AAGT", "CCACGTACGTT"));
        assertTrue(dnaTable.insert("AC", "GT"));
        assertTrue(dnaTable.remove("A"));
        assertTrue(dnaTable.remove("AAGT"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 2 byte");
        System.out.println("[Block 2] Starting Byte Location: 5, Size 4 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.insert("ACCGTTTAA", "T"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 1, Size 1 byte");
        System.out.println("[Block 2] Starting Byte Location: 8, Size 1 byte");
        System.out.println("      ...........End.............");
    }


    /**
     * Test insert, remove, check memory file 4
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile4() throws Exception {
        System.out.println("\n\n__________ Test Mem File 4___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("CGGTTTTCC", "CAAAT"));
        assertTrue(dnaTable.insert("CGGAATTCC", "AAATT"));
        assertTrue(dnaTable.insert("CGGAATTAA", "CCAAG"));
        assertTrue(dnaTable.insert("CGGACCTCC", "AATTT"));
        assertTrue(dnaTable.insert("CGGAATCAA", "CCTCG"));
        assertTrue(dnaTable.remove("CGGAATTCC"));
        assertTrue(dnaTable.remove("CGGACCTCC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 5, Size 5 byte");
        System.out.println(
            "[Block 2] Starting Byte Location: 15, Size 5 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("CGGAATCAA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 5, Size 5 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("CGGAATTAA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("CGGTTTTCC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");
    }


    /**
     * Test insert, remove, check memory file 5
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile5() throws Exception {
        System.out.println("\n\n__________ Test Mem File 5___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("CGGTTTTCC", "CAAAT"));
        assertTrue(dnaTable.insert("CGGAATTCC", "AAATT"));
        assertTrue(dnaTable.insert("CGGAATTAA", "CCAAG"));
        assertTrue(dnaTable.insert("CGGACCTCC", "AATTT"));
        assertTrue(dnaTable.insert("CGGAATCAA", "CCTCG"));
        assertTrue(dnaTable.remove("CGGAATTAA"));
        assertTrue(dnaTable.remove("CGGACCTCC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println(
            "[Block 1] Starting Byte Location: 10, Size 10 byte");
        System.out.println("      ...........End.............");

    }


    /**
     * Test insert, remove, check memory file 6
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile6() throws Exception {
        System.out.println("\n\n__________ Test Mem File 6___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("GGG", "GGGGGGGG"));
        assertTrue(dnaTable.insert("GGA", "GGGGGGGG"));
        assertTrue(dnaTable.insert("GGC", "GGGGGGGG"));
        assertTrue(dnaTable.insert("GGT", "GGGGGGGG"));
        assertTrue(dnaTable.insert("GAG", "GGGGGGGG"));
        assertTrue(dnaTable.remove("GGG"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 3 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("GGA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 6 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("GGT"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 6 byte");
        System.out.println("[Block 2] Starting Byte Location: 9, Size 3 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("GGC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 12 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("GAG"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");
    }


    /**
     * Test insert, remove, check memory file 7
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile7() throws Exception {
        System.out.println("\n\n__________ Test Mem File 7___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("GGG", "AT"));
        assertTrue(dnaTable.insert("GGA", "AC"));
        assertTrue(dnaTable.insert("GGC", "AG"));
        assertTrue(dnaTable.insert("GGT", "AAA"));
        assertTrue(dnaTable.insert("GAG", "CCC"));
        assertTrue(dnaTable.insert("GAA", "G"));
        assertTrue(dnaTable.insert("GAACT", "G"));
        assertTrue(dnaTable.remove("GGA"));
        assertTrue(dnaTable.remove("GGT"));
        assertTrue(dnaTable.remove("GAA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 2, Size 2 byte");
        System.out.println("[Block 2] Starting Byte Location: 6, Size 2 byte");
        System.out.println("[Block 3] Starting Byte Location: 10, Size 2 byte");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.remove("GAG"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 2, Size 2 byte");
        System.out.println("[Block 2] Starting Byte Location: 6, Size 6 byte");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.insert("GAACTAAGT", "G"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 3, Size 1 byte");
        System.out.println("[Block 2] Starting Byte Location: 9, Size 3 byte");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.insert("A", "GAACTAAGT"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.insert("GGACA", "GAA"));
        assertTrue(dnaTable.remove("A"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 3, Size 1 byte");
        System.out.println("[Block 2] Starting Byte Location: 9, Size 3 byte");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.remove("GAACT"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 3, Size 1 byte");
        System.out.println("[Block 2] Starting Byte Location: 9, Size 6 byte");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.remove("GGACA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 3, Size 1 byte");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.remove("GGC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 3, Size 3 byte");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.remove("GAACTAAGT"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");
    }


    /**
     * Test insert, remove, check memory file 8
     * 
     * @throws Exception
     *             file access exception
     */
    public void testMemFile8() throws Exception {
        System.out.println("\n\n__________ Test Mem File 8___________");
        dnaTable.clear();
        assertTrue(dnaTable.insert("AAAA", "AAAATTTT"));
        assertTrue(dnaTable.remove("AAAA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.insert("AAAA", "AAAATTTT"));
        assertTrue(dnaTable.insert("CCCC", "AAAATTTT"));
        assertTrue(dnaTable.remove("AAAA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 0, Size 3 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("CCCC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.insert("AAAA", "AAAATTTT"));
        assertTrue(dnaTable.insert("CCCC", "AAAATTTT"));
        assertTrue(dnaTable.remove("CCCC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("AAAA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");

        assertTrue(dnaTable.insert("AAAA", "AAAATTTT"));
        assertTrue(dnaTable.insert("CCCC", "AAAATTTT"));
        assertTrue(dnaTable.insert("GGGG", "AAAATTTT"));
        assertTrue(dnaTable.remove("CCCC"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("[Block 1] Starting Byte Location: 3, Size 3 byte");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("GGGG"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");
        assertTrue(dnaTable.remove("AAAA"));
        dnaTable.print();
        System.out.println(".............Correct Answer..............");
        System.out.println("Free Block List: none");
        System.out.println("      ...........End.............");

    }

    /**
     * Testing the main file with command input file
     * 
     * @throws IOException
     *             thow input output exception
     * 
     */
    public void testMain() throws Exception {
        String[] args = { "comFileTest.txt", "hashFile.txt", "64",
            "memFile.bin", };
        DNAdbase.main(args);
        assertFalse(dnaTable.search("AA"));
    }


    /**
     * Clear the memory file
     * 
     * @throws Exception
     *             file access exception
     */
    void clearMemory() throws Exception {
        FileWriter fw = new FileWriter("memFile.bin", false);
        fw.close();
    }
}
