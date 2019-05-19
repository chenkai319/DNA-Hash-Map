import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The database system will include a disk-based hash table using a simple
 * bucket
 * hash, to
 * support searches by sequence identifier. The bulk of the database will be
 * stored in a
 * binary file on disk, with a memory manager that stores both sequences and
 * sequenceIDs.
 * As with Project 2, define DNA sequences to be strings on the alphabet A, C,
 * G, and T. In
 * this project, you will store data records consisting of two parts. The first
 * part will be the
 * identifier. The identifier is a relatively short string of characters from
 * the A, C, G, T
 * alphabet. The second part is the sequence.
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 *
 */
public class DNAdbase {

    /**
     * The name of the program is DNAdbase. Parameter command-file is the name
     * of the input file that holds the commands to be processed by the program.
     * Parameter hashfile is the name of the file that holds the hash table.
     * Parameter hash-table-size defines the
     * size of the hash table. This number must be a multiple of 32. The hash
     * table never
     * changes in size once the program starts.
     * 
     * @param args
     *            <command-file> <hash-file> <hash-table-size>
     *            <memory-file>
     * @throws Exception
     *             file access exception
     */
    public static void main(String[] args) throws Exception {
// String comPath = "/home/hung/Desktop/comFile.txt";
// String memPath = "/home/hung/Desktop/memFile.bin";
// int hashTbSize = 64;
        String comPath = args[0];
        // String hashPath = args[1];
        String memPath = args[3];
        int hashTbSize = Integer.parseInt(args[2]);
        removeFile(memPath);
        BufferedReader commandFile = new BufferedReader(new FileReader(
            comPath));
        String[] userInput = readLine(commandFile);

        DNATable dnaTable = new DNATable(hashTbSize, memPath);
        String id;
        String dnaSeq;
        while (userInput != null) {

            if (userInput[0].compareTo("insert") == 0) {
                if (isLetter(userInput[1])) {
                    id = userInput[1];
                    userInput = readLine(commandFile);
                    dnaSeq = userInput[0];
                    dnaTable.insert(id, dnaSeq);
                }
            }

            else if (userInput[0].compareTo("remove") == 0) {
                if (isLetter(userInput[1])) {
                    dnaTable.remove(userInput[1]);
                }
            }

            else if (userInput[0].compareTo("search") == 0) {
                dnaTable.search(userInput[1]);
            }

            else if (userInput[0].compareTo("print") == 0) {
                dnaTable.print();
            }
            userInput = readLine(commandFile);
        }

        commandFile.close();
    }


    /**
     * Read line method that read command line from file
     * and covert it to string
     * 
     * @param file
     *            path of command file
     * @return the command line
     * @throws Exception
     *             file access exception
     */
    public static String[] readLine(BufferedReader file) throws Exception {
        String text = file.readLine();

        if (text == null) {
            return null;
        }

        text = text.replaceAll("^\\s+", "");

        while (text.equals("")) {
            text = file.readLine();
            if (text == null) {
                return null;
            }
            text = text.replaceAll("^\\s+", "");
        }

        return text.split("\\s+");
    }


    /**
     * Remove the file given the path
     * 
     * @param dataPath
     *            path of the file
     * @throws IOException
     *             file access exception
     */
    public static void removeFile(String dataPath) throws IOException {
        FileWriter fw = new FileWriter(dataPath, false);
        fw.close();
    }


    /**
     * this method checks if the input only contains
     * ACTG letters
     * 
     * @param word
     *            , the input string of the DNA
     * @return true if it's a proper DNA letters
     */
    public static boolean isLetter(String word) {
        return word.matches("[ACGT]*");
    }

}
