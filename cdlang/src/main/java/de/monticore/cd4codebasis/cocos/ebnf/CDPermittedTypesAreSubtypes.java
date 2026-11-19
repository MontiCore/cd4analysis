/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDPermitsUsage;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDClassCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDInterfaceCoCo;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks on symbol level that all types of the subtype restriction (the {@code permits} clause)
 * directly extend or implement the sealed type declaring the clause, and that no type is listed
 * twice (JLS 17, §8.1.6). Unresolvable types are not part of the restriction, the type check
 * reports them (0xA0324).
 */
public class CDPermittedTypesAreSubtypes implements CD4CodeBasisASTCDClassCoCo,
    CD4CodeBasisASTCDInterfaceCoCo {
  
  public static final String ERROR_CODE = "0xCDC91";
  
  public static final String ERROR_CODE_DUPLICATE = "0xCDC9A";
  
  @Override
  public void check(ASTCDClass clazz) {
    if (clazz.isPresentCDPermitsUsage()) {
      checkPermits(clazz.getSymbol(), clazz.getCDPermitsUsage());
    }
  }
  
  @Override
  public void check(ASTCDInterface iface) {
    if (iface.isPresentCDPermitsUsage()) {
      checkPermits(iface.getSymbol(), iface.getCDPermitsUsage());
    }
  }
  
  protected void checkPermits(OOTypeSymbol declarer, ASTCDPermitsUsage permits) {
    Set<String> seen = new HashSet<>();
    
    for (SymTypeExpression permitted : declarer.getSubTypeRestrictionList()) {
      if (!permitted.hasTypeInfo()) {
        continue;
      }
      String permittedName = permitted.getTypeInfo().getFullName();
      
      if (!seen.add(permittedName)) {
        error(ERROR_CODE_DUPLICATE, "Permitted type '%s' is listed more than once.", permits,
            permitted.getTypeInfo().getName());
      }
      else if (!directlyExtendsOrImplements(permitted, declarer)) {
        error(ERROR_CODE,
            "Permitted type '%s' does not directly extend or implement the sealed type '%s'.",
            permits, permitted.getTypeInfo().getName(), declarer.getName());
      }
    }
  }
  
  protected static boolean directlyExtendsOrImplements(SymTypeExpression subtype,
      OOTypeSymbol supertype) {
    return subtype.getTypeInfo().getSuperTypesList().stream().anyMatch(s -> s.hasTypeInfo() && s
        .getTypeInfo().getFullName().equals(supertype.getFullName()));
  }
  
  protected void error(String code, String message, ASTCDPermitsUsage node, Object... args) {
    Log.error(code + ": " + String.format(message, args), node.get_SourcePositionStart(), node
        .get_SourcePositionEnd());
  }
  
}
