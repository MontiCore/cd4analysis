/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

/** Checks that an attribute assignment is compatible w.r.t. the attribute's type. */
public class CDAttributeInitialTypeCompatible implements CDBasisASTCDAttributeCoCo {

  final AbstractDerive calculator;

  public CDAttributeInitialTypeCompatible(AbstractDerive calculator) {
    this.calculator = calculator;
  }

  @Override
  public void check(ASTCDAttribute node) {
    if (node.isPresentInitial()) {
      String className = node.getSymbol().getEnclosingScope().getName();
      final TypeCheckResult symTypeExpressionOfInitial = calculator.deriveType(node.getInitial());
      if (!symTypeExpressionOfInitial.isPresentResult()) {
        Log.error(
            String.format(
                "0xCDC01: The type of the value of the attribute %s in class %s could not be calculated.",
                node.getName(), className),
            node.get_SourcePositionStart());
      }

      if (!TypeCheck.isSubtypeOf(
          symTypeExpressionOfInitial.getResult(), node.getSymbol().getType())) {
        Log.error(
            String.format(
                "0xCDC02: The initial value assignment for the attribute %s in class %s is not compatible to its type %s.",
                node.getName(), className, node.getSymbol().getType().print()),
            node.get_SourcePositionStart());
      }
    }
  }
}
