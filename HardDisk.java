package prog11;

import java.io.*;
import java.util.*;

/** This class simulates a hard disk.  Requesting a new file returns
 * its location on the disk, which is the (long integer) block number
 * of its first block.  Since files can be different sizes, each file
 * starts at a block number between 1 and 4 blocks later than the
 * previous file. F is the type of information stored in the file.
 * HardDisk maps a block number to the information by extending
 * TreeMap, which implements the Map interface. */
public class HardDisk<F> extends TreeMap<Long, F> {
    FileIO<F> io;

    HardDisk (FileIO<F> io) { this.io = io; }

    public Long newFile () {
	Long index = nextIndex;
	nextIndex += 1 + random.nextInt(4);
	return index;
    }

    public boolean write (String fileName) {
	try {

	    PrintWriter out = new PrintWriter(new FileWriter(fileName));

	    for (Map.Entry<Long, F> entry : entrySet()) {
		out.println(entry.getKey());
		F file = entry.getValue();
		io.write(file, out);
	    }

	    out.close();
	} catch (Exception ex) {
	    System.err.println("Could not write to " + fileName);
	    return false;
	}
	
	return true;
    }

    public boolean read (String fileName) {
	clear();
	try {
	    Scanner in = new Scanner(new File(fileName));

	    while (in.hasNextLong()) {
		Long index = in.nextLong();
		F file = io.read(in);
		put(index, file);
	    }

	    in.close();
	} catch (FileNotFoundException ex) {

	    System.out.println("Could not read " + fileName);
	    return false;
	}

	return true;
    }

    private Long nextIndex = 0L;
    private Random random = new Random(0);
}
