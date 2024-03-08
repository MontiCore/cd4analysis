/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.matchresult;

import de.monticore.cdmerge.BaseTest;
import org.junit.Test;

/** Unit tests for the functionality of the MergeNode Class. */
public class MatchNodeTest extends BaseTest {

  //  public static final MatchNode<Integer, String> matchNode1 = new MatchNode<Integer, String>
  //  (1, "one");
  //  public static final MatchNode<Integer, String> matchNode2 = new MatchNode<Integer, String>
  //  (2, "two");

  public MatchNodeTest() {}

  @Test
  public void getElementTest() {}

  @Test
  public void getParentTest() {}

  @Test
  public void matchNodeTest() {
    // Test if no initial match was created
    // assertFalse(matchNode1.hasMatch());

    // Add a match
    // matchNode1.addMatch(matchNode2);

    // Test if match is present
    // assertTrue(matchNode1.hasMatch());
    // assertTrue(matchNode1.getMatches().size() == 1);

    // Test if correct match is found
    // assertTrue(matchNode1.getMatches().get(0).getNode1().equals(matchNode1));
    // assertTrue(matchNode1.getMatches().get(0).getNode2().equals(matchNode2));

    // Check if correct element is matched
    // List<Integer> expectedElementList = new ArrayList<Integer>();
    // expectedElementList.add(2);
    // assertTrue(matchNode1.getMatchedElements().equals(expectedElementList));

    // Check if correct match node is added
    // assertTrue(matchNode1.getMatchedNodes().size() == 1);
    // assertTrue(matchNode1.getMatchedNodes().get(0).equals(matchNode2));

    // Adding the same element again should not alter the list
    //    matchNode1.addMatch(matchNode2);
    //    assertTrue(matchNode1.getMatches().size() == 1);
    //    assertTrue(matchNode1.getMatchedNodes().size() == 1);

    // Add a self loop
    //    matchNode1.addMatch(matchNode1);

    // Test if 2 matches are present and correct
    //    assertTrue(matchNode1.getMatches().size() == 2);
    //    assertTrue(matchNode1.getMatchedNodes().size() == 2);
    //    assertTrue(matchNode1.getMatchedNodes().get(0).equals(matchNode2));
    //    assertTrue(matchNode1.getMatchedNodes().get(1).equals(matchNode1));

  }
}
