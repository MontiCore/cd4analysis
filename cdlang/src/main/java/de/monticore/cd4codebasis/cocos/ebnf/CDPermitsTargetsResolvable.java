/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDPermitsUsage;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDClassCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDInterfaceCoCo;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that all types listed in a {@code permits} clause can be resolved.
 */
public class CDPermitsTargetsResolvable implements CD4CodeBasisASTCDClassCoCo,
    CD4CodeBasisASTCDInterfaceCoCo {
  
  public static final String ERROR_CODE = "0xCDCF9";
  
  @Override
  public void check(ASTCDClass clazz) {
    if (clazz.isPresentCDPermitsUsage()) {
      checkPermits(clazz.getName(), clazz.getCDPermitsUsage(), clazz.getSymbol()
          .getEnclosingScope());
    }
  }
  
  @Override
  public void check(ASTCDInterface iface) {
    if (iface.isPresentCDPermitsUsage()) {
      checkPermits(iface.getName(), iface.getCDPermitsUsage(), iface.getSymbol()
          .getEnclosingScope());
    }
  }
  
  protected void checkPermits(String declarerName, ASTCDPermitsUsage permits,
      IOOSymbolsScope scope) {
    for (ASTMCObjectType type : permits.getPermittedList()) {
      String name = rawTypeName(type);
      if (scope.resolveOOType(name).isEmpty()) {
        Log.error(String.format("%s: Permitted type '%s' of '%s' cannot be resolved.", ERROR_CODE,
            name, declarerName), type.get_SourcePositionStart(), type.get_SourcePositionEnd());
      }
    }
  }
  
  /**
   * @return the type name without type arguments, e.g., {@code F} for {@code F<X>}
   */
  protected static String rawTypeName(ASTMCObjectType t) {
    String printed = t.printType();
    int lt = printed.indexOf('<');
    return lt < 0 ? printed : printed.substring(0, lt);
  }
  
}
