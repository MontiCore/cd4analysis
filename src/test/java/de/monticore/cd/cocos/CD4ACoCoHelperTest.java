/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link CD4ACoCoHelper}.
 *
 */
public class CD4ACoCoHelperTest {
  @Test
  public void testAssocDirections() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setName("expectedName");
    assoc.setLeftReferenceName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList("left",
        "TypeName")).build());
    assoc.setRightReferenceName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList("right",
            "TypeName")).build());

    assoc.setLeftToRight(true);
    assertEquals("expectedName (left.TypeName -> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setLeftToRight(false);

    assoc.setBidirectional(true);
    assertEquals("expectedName (left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setBidirectional(false);

    assoc.setRightToLeft(true);
    assertEquals("expectedName (left.TypeName <- right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setRightToLeft(false);

    assoc.setUnspecified(true);
    assertEquals("expectedName (left.TypeName -- right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setUnspecified(false);

  }

  @Test
  public void testAssocRoles() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setName("expectedName");
    assoc.setLeftReferenceName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList("left",
        "TypeName")).build());
    assoc.setBidirectional(true);
    assoc.setRightReferenceName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList("right",
            "TypeName")).build());

    assoc.setLeftRole("leftRole");
    assertEquals("expectedName (left.TypeName (leftRole) <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));

    assoc.setRightRole("rightRole");
    assertEquals("expectedName (left.TypeName (leftRole) <-> (rightRole) right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));

    assoc.setLeftRole(null);
    assertEquals("expectedName (left.TypeName <-> (rightRole) right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
  }

  @Test
  public void testAssocName() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setLeftReferenceName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList((Arrays.asList("left",
        "TypeName"))).build());
    assoc.setBidirectional(true);
    assoc.setRightReferenceName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList((Arrays.asList("right",
            "TypeName"))).build());

    assertEquals("(left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    
    assoc.setName("expectedName");
    assertEquals("expectedName (left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
  }
}
