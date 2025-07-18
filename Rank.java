package prog11;

import prog02.GUI;
import java.util.*;

public class Rank {
    public static void main(String[] args) {
	//String pageDiskName = "pagedisk-mary.txt";
	//String wordDiskName = "worddisk-mary.txt";
	String pageDiskName = "pagedisk-1.txt";
	String wordDiskName = "worddisk-1.txt";

	Browser browser = new BetterBrowser();
	SearchEngine notGPT = new Newgle();
	Newgle g = (Newgle) notGPT;

	g.pageDisk.read(pageDiskName);
	for (Map.Entry<Long,PageFile> entry : g.pageDisk.entrySet())
	    g.urlToIndex.put(entry.getValue().url, entry.getKey().toString());

	g.wordDisk.read(wordDiskName);
	for (Map.Entry<Long,WordFile> entry : g.wordDisk.entrySet())
	    g.wordToIndex.put(entry.getValue().word, entry.getKey());

	System.out.println("map from URL to page index");
	System.out.println(g.urlToIndex);
	System.out.println("map from page index to page disk");
	System.out.println(g.pageDisk);
	System.out.println("map from word to word index");
	System.out.println(g.wordToIndex);
	System.out.println("map from word index to word file");
	System.out.println(g.wordDisk);

	notGPT.rank(false);
	System.out.println("page disk after slow rank");
	for (PageFile file : g.pageDisk.values())
	    System.out.println(file);
	
	g.pageDisk.write("slow.txt");

	for (PageFile file : g.pageDisk.values())
	    file.impact = 0.0;

	notGPT.rank(true);
	System.out.println("page disk after fast rank");
	for (PageFile file : g.pageDisk.values())
	    System.out.println(file);

	g.pageDisk.write("fast.txt");
    }
}
