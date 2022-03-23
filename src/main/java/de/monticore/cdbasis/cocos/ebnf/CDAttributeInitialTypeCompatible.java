/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis.typescalculator.DeriveSymTypeOfCD4CodeBasis;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that an attribute assignment is compatible w.r.t. the attribute's
 * type.
 */
public class CDAttributeInitialTypeCompatible
  implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute node) {
    if (node.isPresentInitial()) {
      String className = node.getSymbol().getEnclosingScope().getName();

      String typeName = node.getMCType().printType(MCCollectionTypesMill.mcCollectionTypesPrettyPrinter());
      final CD4CodeFullPrettyPrinter initialPrinter = new CD4CodeFullPrettyPrinter();
      node.getInitial().accept(initialPrinter.getTraverser());

      final DeriveSymTypeOfCDBasis deriveSymTypeOfCDBasis = new DeriveSymTypeOfCDBasis();
      final TypeCheckResult symTypeExpressionOfType = deriveSymTypeOfCDBasis.synthesizeType(node.getMCType());
      final TypeCheckResult symTypeExpressionOfInitial = deriveSymTypeOfCDBasis.deriveType(node.getInitial());

      if (symTypeExpressionOfType.isPresentCurrentResult()) {
        Log.error(
            String
                .format(
                    "0xCDC00: The type of the attribute %s (%s) in class %s could not be calculated.",
                    node.getName(), typeName, className),
            node.get_SourcePositionStart());
      }
      if (symTypeExpressionOfInitial.isPresentCurrentResult()) {
        Log.error(
            String
                .format(
                    "0xCDC01: The type of the value (%s) of the attribute %s in class %s could not be calculated.",
                    initialPrinter.getPrinter().getContent(), node.getName(), className),
            node.get_SourcePositionStart());
      }

      if (symTypeExpressionOfType.isPresentCurrentResult() && symTypeExpressionOfInitial.isPresentCurrentResult()) {
        if (!TypeCheck.isSubtypeOf(symTypeExpressionOfInitial.getCurrentResult(), symTypeExpressionOfType.getCurrentResult())) {
          Log.error(
              String
                  .format(
                      "0xCDC02: The initial value assignment (%s) for the attribute %s in class %s is not compatible to its type %s.",
                      initialPrinter.getPrinter().getContent(), node.getName(), className, typeName),
              node.get_SourcePositionStart());
        }
      }
    }
  }
}
