/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.matchresult;

import de.monticore.cdmerge.BaseTest;

/**
 * UnitTests for the MatchResult class.
 */
public class MatchResultTest extends BaseTest {

  //	public static final MatchNode<Integer, String> matchNode1 = new MatchNode<Integer, String>
  //	(1, "odd");
  //
  //	public static final MatchNode<Integer, String> matchNode2 = new MatchNode<Integer, String>
  //	(2, "even");
  //
  //	public static final MatchNode<Integer, String> matchNode3 = new MatchNode<Integer, String>
  //	(3, "odd");
  //
  //	public static final MatchNode<Integer, String> matchNode4 = new MatchNode<Integer, String>
  //	(4, "even");
  //
  //	public static final Match<Integer, String> match13 = new Match<Integer, String>(matchNode1,
  //	matchNode3);
  //
  //	public static final Match<Integer, String> match24 = new Match<Integer, String>(matchNode2,
  //	matchNode4);
  //
  //	public final List<String> parents;
  //
  //	public final MatchGraph<Integer, String> matchGraph;
  //
  //	public MatchResultTest() {
  //		// Create a MatchResult
  //		this.parents = new ArrayList<String>();
  //		this.parents.add("even");
  //		this.parents.add("odd");
  //
  //		this.matchGraph = new MatchGraph<Integer, String>(parents);
  //
  //		// Add matches to initial MatchResult
  //		this.matchGraph.addElement(1, "odd");
  //		this.matchGraph.addElement(2, "even");
  //		this.matchGraph.addElement(3, "odd");
  //		this.matchGraph.addElement(4, "even");
  //
  //	}
  //
  //	@Test
  //	public void addElementTest() {
  //		// Test if parents are added correctly
  //		assertTrue(this.matchGraph.hasParent("even"));
  //		assertTrue(this.matchGraph.hasParent("odd"));
  //		assertFalse(this.matchGraph.hasParent("zero"));
  //
  //		// Test if all added elements are contained and return the correct nodes
  //		// for odd matches
  //		List<MatchNode<Integer, String>> oddMatches = this.matchGraph.getAllNodesForParent("odd");
  //		assertTrue(oddMatches.size() == 2);
  //		for (MatchNode<Integer, String> matchNode : oddMatches) {
  //			assertTrue(matchNode.getElement() % 2 == 1);
  //			assertTrue(matchNode.getParent().equals("odd"));
  //		}
  //
  //		// for even matches
  //		List<MatchNode<Integer, String>> evenMatches = this.matchGraph.getAllNodesForParent
  //		("even");
  //		assertTrue(evenMatches.size() == 2);
  //		for (MatchNode<Integer, String> matchNode : evenMatches) {
  //			assertTrue(matchNode.getElement() % 2 == 0);
  //			assertTrue(matchNode.getParent().equals("even"));
  //		}
  //
  //		// Test if a new element can be added
  //		this.matchGraph.addElement(5, "odd");
  //		assertTrue(this.matchGraph.getAllNodesForParent("odd").get(2).getElement().equals(5));
  //
  //		// Test if adding an element of a different parent fails
  //		try {
  //			this.matchGraph.addElement(0, "zero");
  //			fail("Exception must be thrown.");
  //		} catch (IllegalArgumentException e) {
  //			// Exception expected with message:
  //			assertTrue(e.getMessage().equals("No such parent zero"));
  //		}
  //	}
  //
  //	@Test
  //	public void getNodeTest() {
  //		// Test if 1,odd is contained
  //		Optional<MatchNode<Integer, String>> node1odd = this.matchGraph.getNode(1, "odd");
  //		assertTrue(node1odd.isPresent());
  //		assertTrue(node1odd.get().getElement().equals(1));
  //		assertTrue(node1odd.get().getParent().equals("odd"));
  //
  //		// Test if 0 is not contained
  //		Optional<MatchNode<Integer, String>> node0even = this.matchGraph.getNode(0, "even");
  //		assertFalse(node0even.isPresent());
  //
  //		// Test if exception is thrown if parent is not present
  //		try {
  //			this.matchGraph.getNode(0, "zero");
  //			fail("Exception must be thrown.");
  //		} catch (IllegalArgumentException e) {
  //			// Exception expected with message:
  //			assertTrue(e.getMessage().equals("No such parent zero"));
  //		}
  //	}
  //
  //	@Test
  //	public void getParentsTest() {
  //		// Get all parents and compare them with the original parents
  //		ImmutableList<String> immutableParents = this.matchGraph.getParents();
  //		assertTrue(immutableParents.containsAll(parents));
  //	}
  //
  //	@Test
  //	public void getAllNodesForParentTest() {
  //		// Get all elements with parent odd
  //		List<MatchNode<Integer, String>> oddParent = this.matchGraph.getAllNodesForParent("odd");
  //
  //		// Test elements
  //		assertTrue(oddParent.size() == 2);
  //		assertTrue(oddParent.get(0).getElement().equals(1));
  //		assertTrue(oddParent.get(1).getElement().equals(3));
  //
  //		// Test if exception is thrown if parent is not present
  //		try {
  //			this.matchGraph.getAllNodesForParent("zero");
  //			fail("Exception must be thrown.");
  //		} catch (IllegalArgumentException e) {
  //			// Exception expected with message:
  //			assertTrue(e.getMessage().equals("No such parent zero"));
  //		}
  //	}
  //
  //	@Test
  //	public void getParentTest() {
  //		// Get parent of "odd" (which is quite useless)
  //		Predicate<String> test1 = a -> a.toString().equals("odd");
  //		Optional<String> parent = this.matchGraph.getParent(test1);
  //		assertTrue(parent.isPresent());
  //		assertTrue(parent.get().equals("odd"));
  //
  //		// Test for nonexistent parent
  //		Predicate<String> test2 = a -> a.toString().equals("zero");
  //		Optional<String> parent2 = this.matchGraph.getParent(test2);
  //		assertFalse(parent2.isPresent());
  //	}
  //
  //	@Test
  //	public void findElementsTest() {
  //		// Get all odd elements
  //		List<Integer> oddElements = this.matchGraph.findElements(e -> e % 2 == 1);
  //		assertTrue(oddElements.size() == 2);
  //
  //		// Get all even elements
  //		List<Integer> evenElements = this.matchGraph.findElements(e -> e % 2 == 0);
  //		assertTrue(evenElements.size() == 2);
  //
  //		// Get all elements equal to 0 (empty expected)
  //		List<Integer> zeroElements = this.matchGraph.findElements(e -> e == 0);
  //		assertTrue(zeroElements.size() == 0);
  //	}
  //
  //	@Test
  //	public void findNodesTest() {
  //		// Find all nodes with even parent and element
  //		List<MatchNode<Integer, String>> evenNodes = this.matchGraph
  //				.findNodes(m -> (m.getParent().equals("even")) && (m.getElement() % 2 == 0));
  //		assertTrue(evenNodes.size() == 2);
  //		for (MatchNode<Integer, String> matchNode : evenNodes) {
  //			assertTrue(matchNode.getElement() % 2 == 0);
  //			assertTrue(matchNode.getParent().equals("even"));
  //		}
  //
  //		// Get all nodes with elements equal to 0 (empty expected)
  //		List<MatchNode<Integer, String>> zeroElements = this.matchGraph.findNodes(m -> m
  //		.getElement() == 0);
  //		assertTrue(zeroElements.size() == 0);
  //	}
  //
  //	@Test
  //	public void hasParentTest() {
  //		assertTrue(this.matchGraph.hasParent(p -> p.equals("odd")));
  //		assertTrue(this.matchGraph.hasParent(p -> p.equals("even")));
  //		assertFalse(this.matchGraph.hasParent(p -> p.equals("zero")));
  //
  //		assertTrue(this.matchGraph.hasParent("odd"));
  //		assertTrue(this.matchGraph.hasParent("even"));
  //		assertFalse(this.matchGraph.hasParent("zero"));
  //	}
}
