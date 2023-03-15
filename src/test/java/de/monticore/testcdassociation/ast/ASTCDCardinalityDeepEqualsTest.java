/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.ast;

import de.monticore.cdassociation._ast.ASTCDCardAtLeastOne;
import de.monticore.cdassociation._ast.ASTCDCardMult;
import de.monticore.cdassociation._ast.ASTCDCardOne;
import de.monticore.cdassociation._ast.ASTCDCardOpt;
import de.monticore.testcdassociation.TestCDAssociationMill;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class ASTCDCardinalityDeepEqualsTest {

  @Test
  public void testDeepEqualsOpt() throws IOException {
    Optional<ASTCDCardOpt> optCard = TestCDAssociationMill.parser().parse_StringCDCardOpt("[0..1]");
    assertTrue(optCard.isPresent());
    ASTCDCardOpt card1 = optCard.get();
    ASTCDCardOpt cardClone = card1.deepClone();
    assertTrue(card1.deepEquals(cardClone));
  }

  @Test
  public void testDeepEqualsOne() throws IOException {
    Optional<ASTCDCardOne> optCard = TestCDAssociationMill.parser().parse_StringCDCardOne("[1]");
    assertTrue(optCard.isPresent());
    ASTCDCardOne card1 = optCard.get();
    ASTCDCardOne cardClone = card1.deepClone();
    assertTrue(card1.deepEquals(cardClone));
  }

  @Test
  public void testDeepEqualsAtLeastOne() throws IOException {
    Optional<ASTCDCardAtLeastOne> optCard = TestCDAssociationMill.parser().parse_StringCDCardAtLeastOne("[1..*]");
    assertTrue(optCard.isPresent());
    ASTCDCardAtLeastOne card1 = optCard.get();
    ASTCDCardAtLeastOne cardClone = card1.deepClone();
    assertTrue(card1.deepEquals(cardClone));
  }

  @Test
  public void testDeepEqualsMult() throws IOException {
    Optional<ASTCDCardMult> optCard = TestCDAssociationMill.parser().parse_StringCDCardMult("[*]");
    assertTrue(optCard.isPresent());
    ASTCDCardMult card1 = optCard.get();
    ASTCDCardMult cardClone = card1.deepClone();
    assertTrue(card1.deepEquals(cardClone));
  }


}
