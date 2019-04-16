package edu.smith.cs.csc212.p8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class AliceSpelling {

	private static List<String> loadBook() {
		String words;
		List<String> output;
		long start = System.nanoTime();
		try {
			// Read from a file:
			words = Files.readString(new File("src/main/resources/alice.txt").toPath());
			output = WordSplitter.splitTextToWords(words);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find book.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + output.size() + " words in " + time +" seconds.");
		return output;
	}
	
	public static void main(String[] args) {
		List<String> dictionary = CheckSpelling.loadDictionary();
		List<String> bookList = loadBook();
		
		// --- Create a bunch of data structures for testing:
		TreeSet<String> treeOfWords = new TreeSet<>(dictionary);		
		HashSet<String> hashOfWords = new HashSet<>(dictionary);

		SortedStringListSet bsl = new SortedStringListSet(dictionary);
		CharTrie trie = new CharTrie();		
		for (String w : dictionary) {
			trie.insert(w);
		}
		LLHash hm100k = new LLHash(100000);
		for (String w : dictionary) {
			hm100k.add(w);
		}
		
		// --- Make sure that every word in the dictionary is in the dictionary:
		System.out.println("\nTIME LOOKUP");
		CheckSpelling.timeLookup(bookList, treeOfWords);
		CheckSpelling.timeLookup(bookList, hashOfWords);
		CheckSpelling.timeLookup(bookList, bsl);
		CheckSpelling.timeLookup(bookList, trie);
		CheckSpelling.timeLookup(bookList, hm100k);
	}

}
