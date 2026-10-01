/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/** Checks that classes do only extend other classes. */
public class CDClassExtendsOnlyClasses implements CDBasisASTCDClassCoCo {
  
  // TODO SVa: provide printer for the types,
  //  so that a user can provide their own printer
  
  public static final String ERROR_CODE = "0xCDC08";
  
  @Override
  public void check(ASTCDClass clazz) {
    OOTypeSymbol symbol = clazz.getSymbol();
    
    if (!clazz.isPresentCDExtendUsage()) {
      return;
    }
    
    for (ASTMCObjectType typeRef : clazz.getCDExtendUsage().getSuperclassList()) {
      SymTypeExpression steRef = TypeCheck3.symTypeFromAST(typeRef);
      if (steRef.hasTypeInfo()) {
        if (!CoCoHelper.isClass(steRef.getTypeInfo()))
          Log.error(String.format(
              "%s: Class %s cannot extend %s %s. A class may only extend classes.", ERROR_CODE,
              clazz.getName(), steRef.getTypeInfo().getClass().getSimpleName(), steRef.getTypeInfo()
                  .getName()), clazz.get_SourcePositionStart());
      }
      else {
        Log.error(String.format(
            "%s: Class %s cannot extend <missing type info>. A class may only extend classes.",
            ERROR_CODE, clazz.getName()), clazz.get_SourcePositionStart());
      }
    }
  }
  
}
