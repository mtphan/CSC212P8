package edu.smith.cs.csc212.test;

import org.junit.Assert;
import org.junit.Test;

import edu.smith.cs.csc212.p8.CharTrie;

public class DataStructureTest {
	
	@Test
	public void testCharTrieCountEmpty() {
		CharTrie empty = new CharTrie();
		Assert.assertEquals(empty.countNodes(), 0);
	}
	
	@Test
	public void testCharTrieCount() {
		CharTrie data = new CharTrie();
		data.insert("a");
		Assert.assertEquals(1, data.countNodes());
		data.insert("alice");
		Assert.assertEquals(5, data.countNodes());
		data.insert("align");
		Assert.assertEquals(7, data.countNodes());
		data.insert("alignment");
		Assert.assertEquals(11, data.countNodes());
		data.insert("java");
		Assert.assertEquals(15, data.countNodes());
		data.insert("javelin");
		Assert.assertEquals(19, data.countNodes());
		data.insert("jail");
		Assert.assertEquals(21, data.countNodes());
	}
}
