package prog11;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintWriter;

/** This class represents the information stored in a file to record a word */
public class WordFile {
    public final String word;
    // Indices of pages that contain the word:
    List<Long> indices = new ArrayList<Long>();

    public WordFile (String word) {
	this.word = word;
    }

    public String toString () {
	return word + indices;
    }

    static class IO implements FileIO<WordFile> {
	public WordFile read (Scanner in) {
	    try {
		String word = in.nextLine();
		word = in.nextLine();
		WordFile file = new WordFile(word);
		int n = in.nextInt();
		for (int i = 0; i < n; i++)
		    file.indices.add(in.nextLong());
		return file;
	    } catch (Exception e) {
		System.out.println("Could not read WordFile.");
		return null;
	    }
	}

	public boolean write (WordFile file, PrintWriter out) {
	    try {
		out.println(file.word);
		out.println(file.indices.size());
		for (Long index : file.indices) 
		    out.println(index);
		return true;
	    } catch (Exception e) {
		System.out.println("Could not write WordFile.");
		return false;
	    }
	}
    }
}


    
