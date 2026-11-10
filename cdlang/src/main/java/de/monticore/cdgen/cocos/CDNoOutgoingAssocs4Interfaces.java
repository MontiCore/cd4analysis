/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types3.TypeCheck3;
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
    final SymTypeExpression typeExpression = TypeCheck3.symTypeFromAST(type);
    if (typeExpression.isObjectType() && typeExpression.hasTypeInfo() && typeExpression
        .getTypeInfo() instanceof OOTypeSymbol && ((OOTypeSymbol) typeExpression.getTypeInfo())
            .isIsInterface()) {
      Log.error(String.format(ERROR_MESSAGE, type.printType()), context.get_SourcePositionStart(),
          context.get_SourcePositionEnd());
    }
  }
  
}
