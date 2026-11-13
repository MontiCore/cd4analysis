/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDConstructorCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDMethodCoCo;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssocLeftSideCoCo;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssocRightSideCoCo;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that 'sealed' and 'non-sealed' are only used for classes and interfaces. CDSealedModifier
 * extends Modifier, so the parser accepts them at every modifier position.
 *
 * <p>The association CoCos are registered in CD4CodeCoCos, since CD4CodeBasis does not include
 * CDAssociation.</p>
 */
public class CDSealedOnlyOnClassesAndInterfaces implements CDBasisASTCDDefinitionCoCo,
    CDBasisASTCDAttributeCoCo, CD4CodeBasisASTCDMethodCoCo, CD4CodeBasisASTCDConstructorCoCo,
    CDInterfaceAndEnumASTCDEnumCoCo, CDAssociationASTCDAssociationCoCo,
    CDAssociationASTCDAssocLeftSideCoCo, CDAssociationASTCDAssocRightSideCoCo {
  
  public static final String ERROR_CODE = "0xCDC97";
  
  @Override
  public void check(ASTCDDefinition node) {
    check(node.getModifier(), "class diagram", node.getName());
  }
  
  @Override
  public void check(ASTCDAttribute node) {
    check(node.getModifier(), "attribute", node.getName());
  }
  
  @Override
  public void check(ASTCDMethod node) {
    check(node.getModifier(), "method", node.getName());
  }
  
  @Override
  public void check(ASTCDConstructor node) {
    check(node.getModifier(), "constructor", node.getName());
  }
  
  @Override
  public void check(ASTCDEnum node) {
    check(node.getModifier(), "enum", node.getName());
  }
  
  @Override
  public void check(ASTCDAssociation node) {
    check(node.getModifier(), "association", node.isPresentName() ? node.getName() : node.getLeft()
        .getMCQualifiedType().printType() + " - " + node.getRight().getMCQualifiedType()
            .printType());
  }
  
  @Override
  public void check(ASTCDAssocLeftSide node) {
    check(node.getModifier(), "association end", node.getMCQualifiedType().printType());
  }
  
  @Override
  public void check(ASTCDAssocRightSide node) {
    check(node.getModifier(), "association end", node.getMCQualifiedType().printType());
  }
  
  protected void check(ASTModifier mod, String kind, String name) {
    if (CDSealedModifierCombinations.isSealed(mod) || CDSealedModifierCombinations.isNonSealed(
        mod)) {
      Log.error(String.format("%s: The %s '%s' must not be 'sealed' or 'non-sealed'.", ERROR_CODE,
          kind, name), mod.get_SourcePositionStart(), mod.get_SourcePositionEnd());
    }
  }
  
}
