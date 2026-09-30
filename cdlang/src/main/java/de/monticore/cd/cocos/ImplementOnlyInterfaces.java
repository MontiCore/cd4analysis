/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/** Checks that only interfaces are implemented. */
public abstract class ImplementOnlyInterfaces {
  
  public static final String CLASS_ERROR_CODE = "0xCDCF4";
  public static final String ENUM_ERROR_CODE = "0xCDCF5";
  public static final String INTERFACE_ERROR_CODE = "0xCDCF6";
  
  /**
   * Actual check that the class's interfaces are really interfaces.
   *
   * @param node the node to check.
   */
  public void check(ASTCDClass node) {
    OOTypeSymbol symbol = node.getSymbol();
    
    if (!node.isPresentCDInterfaceUsage()) {
      return;
    }
    
    for (ASTMCObjectType typeRef : node.getCDInterfaceUsage().getInterfaceList()) {
      SymTypeExpression steRef = TypeCheck3.symTypeFromAST(typeRef);
      if (steRef.hasTypeInfo()) {
        if (!CoCoHelper.isInterface(steRef.getTypeInfo()))
          Log.error(String.format(
              "%s: Class %s cannot implement %s %s. A class may only implement interfaces.",
              CLASS_ERROR_CODE, node.getName(), CDMill.cDTypeKindPrinter().print(steRef
                  .getTypeInfo()), steRef.getTypeInfo().getName()), node.get_SourcePositionStart());
        
      }
      else {
        Log.error(String.format(
            "%s: Class %s cannot implement <missing type info>. A class may only implement interfaces.",
            CLASS_ERROR_CODE, node.getName()), node.get_SourcePositionStart());
        
      }
    }
  }
  
  /**
   * Actual check that the enums interfaces are really interfaces.
   *
   * @param node the node to check.
   */
  public void check(ASTCDEnum node) {
    OOTypeSymbol symbol = node.getSymbol();
    if (!node.isPresentCDInterfaceUsage()) {
      return;
    }
    symbol.streamSuperTypes().filter(i -> !CoCoHelper.isInterface(i.getTypeInfo())).forEach(e -> Log
        .error(String.format(
            "%s: The %s %s cannot implement %s %s. Only interfaces may be implemented.",
            ENUM_ERROR_CODE, CDMill.cDTypeKindPrinter().print(node), symbol.getName(), CDMill
                .cDTypeKindPrinter().print(e.getTypeInfo()), e.getTypeInfo().getName()), node
                    .get_SourcePositionStart()));
  }
  
  /**
   * Actual check that the node's interfaces are really interfaces.
   *
   * @param node the node to check.
   */
  public void check(ASTCDInterface node) {
    OOTypeSymbol symbol = node.getSymbol();
    if (!node.isPresentCDExtendUsage()) {
      return;
    }
    symbol.streamSuperTypes().filter(i -> !CoCoHelper.isInterface(i.getTypeInfo())).forEach(e -> Log
        .error(String.format("%s: The %s %s cannot extend %s %s. Only interfaces may be extended.",
            INTERFACE_ERROR_CODE, CDMill.cDTypeKindPrinter().print(node), symbol.getName(), CDMill
                .cDTypeKindPrinter().print(e.getTypeInfo()), e.getTypeInfo().getName()), node
                    .get_SourcePositionStart()));
  }
  
}
