/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class CDNoOutgoingAssocs4LibraryTypes implements CDAssociationASTCDAssociationCoCo {

  public static final String ERROR_CODE = "0xCDCE7";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + ": Cannot add outgoing associations to imported library type %s.";

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
    // Resolve the symbol for the type and check if it does not have an ASTNode, i.e., is imported
    final SymTypeExpression typeExpression = TypeCheck3.symTypeFromAST(type);
    if (typeExpression.hasTypeInfo() && !typeExpression.getTypeInfo().isPresentAstNode()) {
      Log.error(String.format(ERROR_MESSAGE, type.printType()),
        context.get_SourcePositionStart(), context.get_SourcePositionEnd());
    }
  }

}
