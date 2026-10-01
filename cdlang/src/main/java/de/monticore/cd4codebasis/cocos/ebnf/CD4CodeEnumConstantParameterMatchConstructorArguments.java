/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDConstructorTOP;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;

public class CD4CodeEnumConstantParameterMatchConstructorArguments implements
    CDInterfaceAndEnumASTCDEnumCoCo {
  
  public CD4CodeEnumConstantParameterMatchConstructorArguments() {
  }
  
  @Override
  public void check(ASTCDEnum node) {
    boolean hasDefaultConstructor = node.getCDConstructorList().isEmpty() || node
        .getCDConstructorList().stream().anyMatch(ASTCDConstructorTOP::isEmptyCDParameters);
    for (ASTCDEnumConstant enumConstant : node.getCDEnumConstantList()) {
      if (enumConstant instanceof ASTCD4CodeEnumConstant cenumConstant) {
        ArrayList<SymTypeExpression> paramTypes = Lists.newArrayList();
        if (cenumConstant.isPresentArguments()) {
          for (ASTExpression expr : cenumConstant.getArguments().getExpressionList()) {
            paramTypes.add(TypeCheck3.typeOf(expr));
          }
          if (!matchConstructor(paramTypes, node.getCDConstructorList())) {
            logError(enumConstant, node.getName());
          }
        }
        else if (!hasDefaultConstructor) {
          logError(enumConstant, node.getName());
        }
      }
      else {
        if (!hasDefaultConstructor) {
          logError(enumConstant, node.getName());
        }
      }
    }
  }
  
  protected boolean matchConstructor(ArrayList<SymTypeExpression> paramTypes,
      List<ASTCDConstructor> cdConstructorList) {
    for (ASTCDConstructor constructor : cdConstructorList) {
      List<VariableSymbol> formalParams = constructor.getSymbol().getParameterList();
      if (paramTypes.size() != formalParams.size()) {
        continue;
      }
      boolean success = true;
      for (int i = 0; i < formalParams.size(); i++) {
        if (!SymTypeRelations.isCompatible(formalParams.get(i).getType(), paramTypes.get(i))) {
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
    Log.error(String.format(
        "0xCDCD2: The enum constant %s uses a constructor which is incompatible with the available constructors of the enum %s.",
        enumConstant.getName(), name), enumConstant.get_SourcePositionStart());
  }
  
}
