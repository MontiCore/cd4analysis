/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/** Checks that an attribute assignment is compatible w.r.t. the attribute's type. */
public class CDAttributeInitialTypeCompatible implements CDBasisASTCDAttributeCoCo {
  
  public static final String ERROR_CODE = "0xCDC02";
  
  public static final String ERROR_MSG_FORMAT =
      "The initial value assignment for the attribute `%s` in class `%s` is not compatible to its type `%s`.";
  
  @Override
  public void check(ASTCDAttribute node) {
    if (node.isPresentInitial()) {
      String className = node.getSymbol().getEnclosingScope().getName();
      final SymTypeExpression symTypeExpressionOfInitial = TypeCheck3.typeOf(node.getInitial());
      if (symTypeExpressionOfInitial.isObscureType()) {
        // The error is already printed by the IDerive visitors, thus we would spam the log if we would log an error
        // again. Therefore, we only leave a note in the debug log.
        Log.debug(String.format(
            "0xCDC01: As the initial expression for the value of the attribute '%s' in class %s at %s is invalid, coco '%s' "
                + "will not be checked.", node.getName(), className, node.get_SourcePositionStart(),
            this.getClass().getSimpleName()), "Cocos");
      }
      else if (!SymTypeRelations.isSubTypeOf(symTypeExpressionOfInitial, node.getSymbol()
          .getType())) {
        Log.error(ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT, node.getName(), className, node
            .getSymbol().getType().print()), node.get_SourcePositionStart());
      }
    }
  }
  
}
