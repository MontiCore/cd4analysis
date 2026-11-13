/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDPermitsUsage;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that all types listed in a {@code permits} clause can be resolved.
 *
 * <p>Applies to sealed classes and sealed interfaces.</p>
 */
public class CDPermitsTargetsResolvable implements CDBasisASTCDClassCoCo,
    CDInterfaceAndEnumASTCDInterfaceCoCo {
  
  /** Error code for unresolved permitted types. */
  public static final String ERROR_CODE = "0xCDCF9";
  
  /**
   * Checks the {@code permits} clause of a class, if present.
   *
   * @param clazz class to check
   */
  @Override
  public void check(ASTCDClass clazz) {
    if (clazz.isPresentCDPermitsUsage()) {
      checkPermits(clazz.getName(), clazz.getCDPermitsUsage(), clazz.getSymbol()
          .getEnclosingScope());
    }
  }
  
  /**
   * Checks the {@code permits} clause of an interface, if present.
   *
   * @param iface interface to check
   */
  @Override
  public void check(ASTCDInterface iface) {
    if (iface.isPresentCDPermitsUsage()) {
      checkPermits(iface.getName(), iface.getCDPermitsUsage(), iface.getSymbol()
          .getEnclosingScope());
    }
  }
  
  /**
   * Resolves all types listed in the given {@code permits} clause.
   *
   * @param declarerName name of the class or interface declaring the clause
   * @param permits permits clause to check
   * @param scope scope used for resolving permitted types
   */
  protected void checkPermits(String declarerName, ASTCDPermitsUsage permits,
      IOOSymbolsScope scope) {
    for (ASTMCObjectType type : permits.getPermittedList()) {
      String name = rawTypeName(type);
      if (scope.resolveOOType(name).isEmpty()) {
        Log.error(String.format("%s: Permitted type %s of %s cannot be resolved.", ERROR_CODE, name,
            declarerName), type.get_SourcePositionStart());
      }
    }
  }
  
  /**
   * Returns the raw type name without generic type arguments.
   *
   * @param t object type
   * @return raw type name, e.g., {@code F} for {@code F<X>}
   */
  protected static String rawTypeName(ASTMCObjectType t) {
    String printed = t.printType();
    int lt = printed.indexOf('<');
    return lt < 0 ? printed : printed.substring(0, lt);
  }
  
}
