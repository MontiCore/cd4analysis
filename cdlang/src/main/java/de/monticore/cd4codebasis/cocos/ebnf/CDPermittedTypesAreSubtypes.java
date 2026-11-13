/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDPermitsUsage;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDClassCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDInterfaceCoCo;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Checks that all types listed in a {@code permits} clause directly extend or implement the
 * sealed type declaring the clause. Unresolvable types are reported by
 * {@link CDPermitsTargetsResolvable}.
 */
public class CDPermittedTypesAreSubtypes implements CD4CodeBasisASTCDClassCoCo,
    CD4CodeBasisASTCDInterfaceCoCo {
  
  public static final String ERROR_CODE = "0xCDCFA";
  
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
    IOOSymbolsScope scope = declarer.getEnclosingScope();
    
    for (ASTMCObjectType type : permits.getPermittedList()) {
      Optional<OOTypeSymbol> permitted = scope.resolveOOType(CDPermitsTargetsResolvable.rawTypeName(
          type));
      
      if (permitted.isPresent() && !directSupertypeNamesOf(permitted.get()).contains(declarer
          .getFullName())) {
        Log.error(String.format(
            "%s: Permitted type '%s' does not directly extend or implement the sealed type '%s'.",
            ERROR_CODE, permitted.get().getName(), declarer.getName()), type
                .get_SourcePositionStart(), type.get_SourcePositionEnd());
      }
    }
  }
  
  /**
   * Reads the direct supertypes from the AST if available, so that the check also works before
   * the symbol table completer has run. Types without AST (e.g., loaded from a symbol file) use
   * the supertypes stored in their symbol.
   *
   * @return full names of the directly extended or implemented types
   */
  protected Set<String> directSupertypeNamesOf(OOTypeSymbol sym) {
    Set<String> result = new HashSet<>();
    
    if (sym.isPresentAstNode() && sym.getAstNode() instanceof ASTCDType) {
      ASTCDType type = (ASTCDType) sym.getAstNode();
      List<ASTMCObjectType> supertypes = new ArrayList<>(type.getSuperclassList());
      supertypes.addAll(type.getInterfaceList());
      
      IOOSymbolsScope scope = sym.getEnclosingScope();
      for (ASTMCObjectType supertype : supertypes) {
        scope.resolveOOType(CDPermitsTargetsResolvable.rawTypeName(supertype)).ifPresent(s -> result
            .add(s.getFullName()));
      }
    }
    else {
      for (SymTypeExpression supertype : sym.getSuperTypesList()) {
        if (supertype.hasTypeInfo()) {
          result.add(supertype.getTypeInfo().getFullName());
        }
      }
    }
    
    return result;
  }
  
}
