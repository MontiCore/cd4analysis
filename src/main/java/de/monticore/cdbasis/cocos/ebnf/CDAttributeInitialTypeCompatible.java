/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis.typescalculator.DeriveSymTypeOfCD4CodeBasis;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

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

      final DeriveSymTypeOfCD4CodeBasis deriveSymTypeOfCD4CodeBasis = new DeriveSymTypeOfCD4CodeBasis();
      final Optional<SymTypeExpression> symTypeExpressionOfType = deriveSymTypeOfCD4CodeBasis.calculateType(node.getMCType());
      final Optional<SymTypeExpression> symTypeExpressionOfInitial = deriveSymTypeOfCD4CodeBasis.calculateType(node.getInitial());

      if (!symTypeExpressionOfType.isPresent()) {
        Log.error(String.format("0xCDC00: The type of the attribute %s (%s) in class %s could not be calculated.",
          node.getName(), typeName, className), node.get_SourcePositionStart());
      }
      if (!symTypeExpressionOfInitial.isPresent()) {
        Log.error(String.format("0xCDC01: The type of the value (%s) of the attribute %s in class %s could not be calculated.",
          initialPrinter.getPrinter().getContent(), node.getName(), className), node.get_SourcePositionStart());
      }
      if (!TypeCheck.isSubtypeOf(symTypeExpressionOfInitial.get(), symTypeExpressionOfType.get())) {
        Log.error(String.format("0xCDC02: The initial value assignment (%s) for the attribute %s in class %s is not compatible to its type %s.",
          initialPrinter.getPrinter().getContent(), node.getName(), className, typeName), node.get_SourcePositionStart());
      }
    }
  }
}
