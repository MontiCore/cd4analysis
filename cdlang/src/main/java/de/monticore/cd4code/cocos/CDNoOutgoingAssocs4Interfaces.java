/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

public class CDNoOutgoingAssocs4Interfaces implements CDAssociationASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xCDCE5";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + ": Interface %s must not have outgoing associations.";
  
  @Override
  public void check(ASTCDAssociation node) {
    // An association is outgoing from the left side if it is navigable from left to right
    if (node.getCDAssocDir().isDefinitiveNavigableRight()) {
      checkSide(node.getLeft().getMCQualifiedType(), node);
    }
    // An association is outgoing from the right side if it is navigable from right to left
    if (node.getCDAssocDir().isDefinitiveNavigableLeft()) {
      checkSide(node.getRight().getMCQualifiedType(), node);
    }
    
  }
  
  protected void checkSide(ASTMCQualifiedType type, ASTCDAssociation context) {
    // Resolve the symbol for the type and check if it's an interface
    context.getEnclosingScope().resolveCDType(type.printType()).ifPresent(symbol -> {
      if (symbol.getAstNode() instanceof ASTCDInterface) {
        Log.error(String.format(ERROR_MESSAGE, symbol.getName()), context
            .get_SourcePositionStart());
      }
    });
  }
  
}
