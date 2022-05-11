/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class CD4CodeEnumConstantParameterMatchConstructorArguments
  implements CDInterfaceAndEnumASTCDEnumCoCo {

  final AbstractDerive calculator;

  public CD4CodeEnumConstantParameterMatchConstructorArguments(AbstractDerive calculator) {
    this.calculator = calculator;
  }

  @Override
  public void check(ASTCDEnum node) {
    boolean hasDefaultConstructor = node.getCDConstructorList().isEmpty() ||
      node.getCDConstructorList().stream().anyMatch(c -> c.isEmptyCDParameters());
    for (ASTCDEnumConstant enumConstant : node.getCDEnumConstantList()) {
      if (enumConstant instanceof ASTCD4CodeEnumConstant) {
        ASTCD4CodeEnumConstant cenumConstant = (ASTCD4CodeEnumConstant) enumConstant;
        ArrayList<SymTypeExpression> paramTypes = Lists.newArrayList();
        if (cenumConstant.isPresentArguments()) {
          for (ASTExpression expr : cenumConstant.getArguments().getExpressionList()) {
            TypeCheckResult paramType = calculator.deriveType(expr);
            paramTypes.add(paramType.getResult());
          }
          if (!matchConstructor(paramTypes, node.getCDConstructorList())) {
            logError(enumConstant, node.getName());
          }
        } else if (!hasDefaultConstructor) {
          logError(enumConstant, node.getName());
        }
      } else {
        if (!hasDefaultConstructor) {
          logError(enumConstant, node.getName());
        }
      }
    }
  }

  protected boolean matchConstructor(ArrayList<SymTypeExpression> paramTypes, List<ASTCDConstructor> cdConstructorList) {
    for (ASTCDConstructor constructor : cdConstructorList) {
      List<VariableSymbol> formalParams = constructor.getSymbol().getParameterList();
      if (paramTypes.size() != formalParams.size()) {
        return false;
      }
      boolean success = true;
      for (int i = 0; i < formalParams.size(); i++) {
        if (!TypeCheck.compatible(formalParams.get(i).getType(), paramTypes.get(i))) {
          success = false;
        }
      }
      if (success) {
        return true;
      }
    }
    return false;
  }

  protected void logError(ASTCDEnumConstant enumConstant, String name) {
    Log.error(
      String.format(
        "0xCDCD2: The enum constant %s uses a constructor which is incompatible with the available constructors of the enum %s.",
        enumConstant.getName(), name),
      enumConstant.get_SourcePositionStart());
  }
}
