package prog11;
import prog08.ExternalSort;
import prog08.TestExternalSort;
import prog09.BTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class Newgle implements SearchEngine {

    HardDisk<PageFile> pageDisk = new HardDisk<PageFile>(new PageFile.IO()) ;
    Map<String,String> urlToIndex = new BTree(128);
    HardDisk<WordFile> wordDisk = new HardDisk<WordFile>(new WordFile.IO());
    Map<String,Long> wordToIndex = new HashMap<String,Long>();



    public Newgle(){

        }


    public long indexPage(String url){
        System.out.print("indexing url " + url);
        long index = pageDisk.newFile();
        PageFile pageFile = new PageFile(url);
        pageDisk.put(index, pageFile);
        urlToIndex.put(url, Long.toString(index));
        System.out.println(" index " + index + " file " + pageFile);
        return index;
    }

    public long indexWord(String word){
        System.out.print("indexing word " + word);
        long index = wordDisk.newFile();
        WordFile wordFile = new WordFile(word);
        wordDisk.put(index, wordFile);
        wordToIndex.put(word, index);
        System.out.println(" index " + index + " file " + wordFile);
        return index;
    }


    @Override
    public void collect (Browser browser, List<String> startingURLs){

        Queue<Long> queue = new ArrayDeque<Long>();

        System.out.println("starting pages " + startingURLs);
        for(String url: startingURLs){
            if(urlToIndex.get(url) == null){
               Long index = indexPage(url);
               queue.offer(index);
            }
        }
        while(!queue.isEmpty()){
            Set<String> urlsOnPage = new TreeSet<String>();


            System.out.println("queue " + queue);
            long dequeIndex = queue.poll();
            PageFile dequeFile = pageDisk.get(dequeIndex);
            String urlFind = dequeFile.url;
            System.out.println("dequeued " + dequeFile);

            if (browser.loadPage(urlFind)){
                List<String> newUrls = browser.getURLs();
                System.out.println("urls " +newUrls);

                for(String newUrl: newUrls){
                    if (urlToIndex.get(newUrl) == null) {
                        Long newIndex = indexPage(newUrl);
                        queue.offer(newIndex);
                    }

                    String visitedIndexStr = urlToIndex.get(newUrl);
                    Long visitedIndex = Long.parseLong(visitedIndexStr);

                    if(!urlsOnPage.contains(newUrl)) {
                        urlsOnPage.add(newUrl);
                        dequeFile.indices.add(visitedIndex);

                        pageDisk.put(dequeIndex, dequeFile);

                    }
                }

                System.out.println("updated page file " + dequeFile);


                List<String> words = browser.getWords();
                Set<String> wordsOnPage = new TreeSet<String>();

                System.out.println("words " + words);

                for(String word: words){
                    if (wordToIndex.get(word) == null){
                        indexWord(word);
                    }

                    if(!wordsOnPage.contains(word)){
                        wordsOnPage.add(word);
                        wordDisk.get(wordToIndex.get(word)).indices.add(Long.parseLong(urlToIndex.get(dequeFile.url)));


                        System.out.println("updated word " + word + " index " + wordToIndex.get(word) + " file " + word + (wordDisk.get(wordToIndex.get(word)).indices));
                    }
                }
            }
        }








    }

    void rankSlow () {
        double zeroLinkImpact = 0.0;
        int numPages = pageDisk.size();

        for (Map.Entry<Long,PageFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            PageFile file = entry.getValue();

            if(file.indices.isEmpty()){
                zeroLinkImpact += file.impact;
            }
        }


        zeroLinkImpact /= numPages;

        for (Map.Entry<Long,PageFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            PageFile file = entry.getValue();


                double impactPerIndex = file.impact / file.indices.size();

                for (Long indexNow : file.indices) {
                    PageFile targetFile = pageDisk.get(indexNow);
                    targetFile.impactTemp += impactPerIndex;
                }

        }

        for (Map.Entry<Long,PageFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            PageFile file = entry.getValue();
            file.impact = file.impactTemp + zeroLinkImpact;
            file.impactTemp = 0.0;
        }


    }

    void rankFast () {
        double zeroLinkImpact = 0.0;
        int numPages = pageDisk.size();

        for (Map.Entry<Long, PageFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            PageFile file = entry.getValue();

            if (file.indices.isEmpty()) {
                zeroLinkImpact += file.impact;
            }
        }


        zeroLinkImpact /= numPages;


        try {
            PrintWriter out = new PrintWriter("unsorted-votes.txt");

            for (Map.Entry<Long, PageFile> entry : pageDisk.entrySet()) {
                long index = entry.getKey();
                PageFile file = entry.getValue();

                double impactPerIndex = file.impact / file.indices.size();

                for (Long votee : file.indices) {
                    Vote v = new Vote(votee, impactPerIndex);
                    out.println(v);
                }


            }

            out.close();

            VoteScanner voteScanner1 = new VoteScanner();
            prog08.ExternalSort<Vote> externalSort1 = new ExternalSort<>(voteScanner1, new VoteComparator());
            externalSort1.sort("unsorted-votes.txt", "sorted-votes.txt");

        } catch (FileNotFoundException e) {

        }

        VoteScanner sortedScanner = new VoteScanner();
        Iterator<Vote> iter = sortedScanner.iterator("sorted-votes.txt");
        Vote v = iter.next();



        for (Map.Entry<Long,PageFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            PageFile file = entry.getValue();
            double voteSum = 0;

            while(iter.hasNext() && v.index == index){
                voteSum += v.vote;
                v = iter.next();
            }

            if (iter.hasNext()){
                file.impact = voteSum + zeroLinkImpact;
            }
            else{
                voteSum += v.vote;
                file.impact = voteSum + zeroLinkImpact;
            }
        }

    }


    @Override
    public void rank (boolean fast){

        for (Map.Entry<Long,PageFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            PageFile file = entry.getValue();
            file.impact = 1.0;
            file.impactTemp = 0.0;
        }

            for(int i =0; i<20; i++) {

                if(fast) {
                    rankFast();
                }
                else {
                    rankSlow();
                }

            }
        }


    /** Check if all elements in an array of long are equal.
     @param array an array of numbers
     @return true if all are equal, false otherwise
     */
    private boolean allEqual (long[] array) {

        for(int i =0; i < array.length - 1; i++){
            if(array[i] != array[i+1]){
                return false;
            }
        }

        return true;
    }

        /** Get the largest element of an array of long.
         @param array an array of numbers
         @return largest element
         */
        private long getLargest (long[] array) {
            long largest = array[0];

            for(int i =1; i < array.length; i++){
                if(array[i] > largest){
                    largest = array[i];
                }
            }

            return largest;
        }

    /** If all the elements of currentPageIndices are equal,
     set each one to the next() of its Iterator,
     but if any Iterator hasNext() is false, just return false.

     Otherwise, do that for every element not equal to the largest element.

     Return true.

     @param currentPageIndices array of current page indices
     @param pageIndexIterators array of iterators with next page indices
     @return true if all page indices are updated, false otherwise
     */
    private boolean getNextPageIndices (long[] currentPageIndices, Iterator<Long>[] pageIndexIterators) {
        if(allEqual(currentPageIndices)){
            for(int i =0; i<currentPageIndices.length; i++){
                if(!pageIndexIterators[i].hasNext()){
                    return false;
                }
                currentPageIndices[i] = pageIndexIterators[i].next();
            }
        }

        else{
            long largest = getLargest(currentPageIndices);

            for(int i =0; i<currentPageIndices.length; i++){
                if(currentPageIndices[i] != largest){
                    if(!pageIndexIterators[i].hasNext()){
                        return false;
                    }
                    currentPageIndices[i] = pageIndexIterators[i].next();
                }
            }

        }

        return true;
    }



    @Override
    public String[] search (List<String> searchWords, int numResults){
        Iterator<Long>[] pageIndexIterators = (Iterator<Long>[]) new Iterator[searchWords.size()];
        long[] currentPageIndices;
        PriorityQueue<Long> bestPageIndices = new PriorityQueue<>(new PageIndexComparator());
        String[] resultsArray;


        for(int i = 0; i < searchWords.size(); i++){

            String word = searchWords.get(i);
            Long wordIndex = wordToIndex.get(word);
            WordFile fileOfWord = wordDisk.get(wordIndex);
            List<Long> fileIndicies = fileOfWord.indices;

            pageIndexIterators[i] = fileIndicies.iterator();
        }

        currentPageIndices = new long[searchWords.size()];


        while(getNextPageIndices(currentPageIndices, pageIndexIterators)){
            if(allEqual(currentPageIndices)){
                long currentPageIndex = currentPageIndices[0];
                String matchedUrl = pageDisk.get(currentPageIndex).url;
                System.out.println(matchedUrl);

                if(bestPageIndices.size() < numResults){
                    bestPageIndices.offer(currentPageIndex);
                }
                else {
                    Long worstIndex = bestPageIndices.peek();
                    double worstImpact = pageDisk.get(worstIndex).impact;
                    double currentImpact = pageDisk.get(currentPageIndex).impact;

                    if(currentImpact > worstImpact) {
                        bestPageIndices.poll();
                        bestPageIndices.offer(currentPageIndex);
                    }
                }

            }
        }

        resultsArray = new String[bestPageIndices.size()];

        for(int i = resultsArray.length - 1; i>=0; i--){
            if(!bestPageIndices.isEmpty()) {
                long index = bestPageIndices.poll();
                resultsArray[i] = pageDisk.get(index).url;
            }
        }

        return resultsArray;

    }

    public static void main(String[] args) {
        Newgle newgle = new Newgle();
        System.out.println("Newgle testing.");
    }



    public class Vote {
        public Long index;
        public double vote;

        public Vote(Long index, double vote) {
            this.index = index;
            this.vote = vote;
        }

            public String toString() {
                return index + " " + vote;
            }

        }


    public class VoteComparator implements Comparator<Vote>{

        public int compare(Vote v1, Vote v2){


            int indexCmp = Long.compare(v1.index, v2.index);


            if(indexCmp != 0){
                return indexCmp;
            }


            return Double.compare(v1.vote, v2.vote);

        }

    }

     class VoteScanner implements ExternalSort.EScanner<Vote> {
        class Iter implements Iterator<Vote> {
            Scanner in;

            Iter (String fileName) {
                try {
                    in = new Scanner(new File(fileName));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            public boolean hasNext () {
                return in.hasNext();
            }

            public Vote next () {
                Long nextLong = in.nextLong();
                double nextDouble = in.nextDouble();

                Vote newVote = new Vote(nextLong, nextDouble);
                return newVote;
            }
        }

        public Iterator<Vote> iterator (String fileName) {
            return new VoteScanner.Iter(fileName);
        }
    }

    public class PageIndexComparator implements Comparator<Long>{

        @Override
        public int compare(Long pageIndex1, Long pageIndex2){
        double impact1 = pageDisk.get(pageIndex1).impact;
        double impact2 = pageDisk.get(pageIndex2).impact;

        return Double.compare(impact1, impact2);

        }

    }



    }
