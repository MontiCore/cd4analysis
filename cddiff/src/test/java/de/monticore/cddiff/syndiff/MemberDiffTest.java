/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberDiffTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    SynDiffTestBasis.dir = "src/test/resources/de/monticore/cddiff/syndiff/MemberDiff/";
  }

  @Test
  public void testMember1() {
    parseModels("Source1.cd", "Target1.cd");

    ASTCDClass cNew = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("A", tgt.getCDDefinition());

    Assertions.assertNotNull(cNew);
    Assertions.assertNotNull(cOld);

    ASTNode attributeNew = CDTestHelper.getAttribute(cNew, "a");
    ASTNode attributeOld = CDTestHelper.getAttribute(cOld, "a");

    CDMemberDiff attrDiff = new CDMemberDiff(attributeNew, attributeOld);

    assertEquals(new HashSet<>(attrDiff.getBaseDiff()), Set.of(DiffTypes.CHANGED_ATTRIBUTE_TYPE, DiffTypes.CHANGED_ATTRIBUTE_MODIFIER));
  }

}
