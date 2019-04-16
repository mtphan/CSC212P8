package edu.smith.cs.csc212.p8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public class CheckSpelling {
	/**
	 * Read all lines from the UNIX dictionary.
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		long start = System.nanoTime();
		List<String> words;
		try {
			// Read from a file:
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time +" seconds.");
		return words;
	}
	
	/**
	 * This method looks for all the words in a dictionary.
	 * @param words - the "queries"
	 * @param dictionary - the data structure.
	 * @return 
	 */
	public static int timeLookup(List<String> words, Collection<String> dictionary) {
		long startLookup = System.nanoTime();
		
		int found = 0;
		for (String w : words) {
			if (dictionary.contains(w)) {
				found++;
			}
		}
		
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName()+": Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item");
		return nsPerItem;
	}

	public static void timeInsertFormat(List<String> words, Collection<String> inserted, long startTime, long endTime) {
		double fractionInserted = inserted.size()/words.size();
		double totalTime = endTime - startTime;
		int nsPerItem = (int) (totalTime / ((double) inserted.size()));
		double timeSecond = totalTime/1e9;
		System.out.println(inserted.getClass().getSimpleName()+": Items inserted="+fractionInserted
				+" time="+timeSecond+" seconds -> " + nsPerItem + " ns/item");
	}
	
	public static List<String> createMixedDataset(List<String> yesWords, int numSamples, double fractionYes) {
		System.out.println(fractionYes);
		// Hint to the ArrayList that it will need to grow to numSamples size:
		List<String> output = new ArrayList<>(numSamples);
		int noWords = 0;
		
		for (int i=0; i<numSamples; i++) {
			String word = yesWords.get(ThreadLocalRandom.current().nextInt(yesWords.size()));
			// Modify word - best way to ensure fractionYes comes out as exact as possible
			if (((double) noWords/(double) numSamples) < (1.0-fractionYes)) {
				// No words in the current dictionary ends with @, not sure about other dictionary though :(
				word = word + "@";
				noWords++;
			}
			output.add(word);
		}
		return output;
	}
	
	public static void main(String[] args) {
		// --- Load the dictionary.
		List<String> listOfWords = loadDictionary();
		
		// --- Create a bunch of data structures for testing:
		System.out.println("\nTIME INSERT");
		long startInsert, endInsert;
		
		startInsert = System.nanoTime();
		TreeSet<String> treeOfWords = new TreeSet<>(listOfWords);
		endInsert = System.nanoTime();
		timeInsertFormat(listOfWords, treeOfWords, startInsert, endInsert);
		
		System.out.print("ForLoop");
		startInsert = System.nanoTime();
		TreeSet<String> treeOfWordsLoop = new TreeSet<>();
		for (String w : listOfWords) {
			treeOfWordsLoop.add(w);
		}
		endInsert = System.nanoTime();
		timeInsertFormat(listOfWords, treeOfWordsLoop, startInsert, endInsert);
		
		startInsert = System.nanoTime();
		HashSet<String> hashOfWords = new HashSet<>(listOfWords);
		endInsert = System.nanoTime();
		timeInsertFormat(listOfWords, hashOfWords, startInsert, endInsert);
		
		System.out.print("ForLoop");
		startInsert = System.nanoTime();
		HashSet<String> hashOfWordsLoop = new HashSet<>();
		for (String w : listOfWords) {
			hashOfWordsLoop.add(w);
		}
		endInsert = System.nanoTime();
		timeInsertFormat(listOfWords, hashOfWordsLoop, startInsert, endInsert);
		
		startInsert = System.nanoTime();
		SortedStringListSet bsl = new SortedStringListSet(listOfWords);
		endInsert = System.nanoTime();
		timeInsertFormat(listOfWords, bsl, startInsert, endInsert);
		
		CharTrie trie = new CharTrie();		
		startInsert = System.nanoTime();
		for (String w : listOfWords) {
			trie.insert(w);
		}
		endInsert = System.nanoTime();
		timeInsertFormat(listOfWords, trie, startInsert, endInsert);
		
		LLHash hm100k = new LLHash(100_000);
		startInsert = System.nanoTime();
		for (String w : listOfWords) {
			hm100k.add(w);
		}
		endInsert = System.nanoTime();
		timeInsertFormat(listOfWords, hm100k, startInsert, endInsert);
		
		// --- Make sure that every word in the dictionary is in the dictionary:
		System.out.println("\nTIME LOOKUP");
		timeLookup(listOfWords, treeOfWords);
		timeLookup(listOfWords, hashOfWords);
		timeLookup(listOfWords, bsl);
		timeLookup(listOfWords, trie);
		timeLookup(listOfWords, hm100k);
		
		System.out.println("\nTIME LOOKUP MISS-ABLE");
		for (int i=0; i<10; i++) {
			// --- Create a dataset of mixed hits and misses with p=i/10.0
			List<String> hitsAndMisses = createMixedDataset(listOfWords, 10_000, i/10.0);
			
			// --- Time the data structures.
			timeLookup(hitsAndMisses, treeOfWords);
			timeLookup(hitsAndMisses, hashOfWords);
			timeLookup(hitsAndMisses, bsl);
			timeLookup(hitsAndMisses, trie);
			timeLookup(hitsAndMisses, hm100k);
		}
		
		// --- linear list timing:
		// Looking up in a list is so slow, we need to sample:
		System.out.println("Start of list: ");
		timeLookup(listOfWords.subList(0, 1000), listOfWords);
		System.out.println("End of list: ");
		timeLookup(listOfWords.subList(listOfWords.size()-100, listOfWords.size()), listOfWords);
		
	
		// --- print statistics about the data structures:
		System.out.println("Count-Nodes: "+trie.countNodes());
		System.out.println("Count-Items: "+hm100k.size());

		System.out.println("Count-Collisions[100k]: "+hm100k.countCollisions());
		System.out.println("Count-Used-Buckets[100k]: "+hm100k.countUsedBuckets());
		System.out.println("Load-Factor[100k]: "+hm100k.countUsedBuckets() / 100000.0);

		System.out.println("log_2 of listOfWords.size(): "+listOfWords.size());
		
		System.out.println("Done!");
	}
}
