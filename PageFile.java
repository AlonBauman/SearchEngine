package prog11;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintWriter;

/** This class represents the information stored in a file to record a web page */
public class PageFile {
    public final String url;
    // Indices of pages this page links to:
    List<Long> indices = new ArrayList<Long>();
    public double impact = 0;
    public double impactTemp = 0;

    public PageFile (String url) {
	this.url = url;
    }

    public String toString () {
	return url + indices + impact;
    }

    static class IO implements FileIO<PageFile> {
	public PageFile read (Scanner in) {
	    try {
		System.out.println("Reading PageFile.");
		String url = in.nextLine();
		url = in.nextLine();
		System.out.println("url " + url);
		PageFile file = new PageFile(url);
		file.impact = in.nextDouble();
		System.out.println("impact " + file.impact);
		int n = in.nextInt();
		System.out.println("n " + n);
		for (int i = 0; i < n; i++)
		    file.indices.add(in.nextLong());
		return file;
	    } catch (Exception e) {
		System.out.println("Could not read PageFile.");
		return null;
	    }
	}

	public boolean write (PageFile file, PrintWriter out) {
	    try {
		out.println(file.url);
		out.println(file.impact);
		out.println(file.indices.size());
		for (Long index : file.indices) 
		    out.println(index);
		return true;
	    } catch (Exception e) {
		System.out.println("Could not write PageFile.");
		return false;
	    }
	}
    }
}


    
