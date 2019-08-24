/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos;

import de.monticore.types.mcbasictypes._ast.MCBasicTypesNodeFactory;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link CD4ACoCoHelper}.
 *
 * @author Robert Heim
 */
public class CD4ACoCoHelperTest {
  @Test
  public void testAssocDirections() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setName("expectedName");
    assoc.setLeftReferenceName(MCBasicTypesNodeFactory.createASTMCQualifiedName(Arrays.asList("left",
        "TypeName")));
    assoc.setRightReferenceName(MCBasicTypesNodeFactory.createASTMCQualifiedName(Arrays.asList("right",
        "TypeName")));

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
    assoc.setLeftReferenceName(MCBasicTypesNodeFactory.createASTMCQualifiedName(Arrays.asList("left",
        "TypeName")));
    assoc.setBidirectional(true);
    assoc.setRightReferenceName(MCBasicTypesNodeFactory.createASTMCQualifiedName(Arrays.asList("right",
        "TypeName")));

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
    assoc.setLeftReferenceName(MCBasicTypesNodeFactory.createASTMCQualifiedName(Arrays.asList("left",
        "TypeName")));
    assoc.setBidirectional(true);
    assoc.setRightReferenceName(MCBasicTypesNodeFactory.createASTMCQualifiedName(Arrays.asList("right",
        "TypeName")));

    assertEquals("(left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    
    assoc.setName("expectedName");
    assertEquals("expectedName (left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
  }
}
