/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._util.ICD4AnalysisTypeDispatcher;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDPermitsUsage;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Checks that all resolvable types listed in a {@code permits} clause directly
 * extend or implement the sealed type declaring the clause.
 *
 * <p>Unresolvable targets are ignored here and handled by
 * {@link CDPermitsTargetsResolvable}.</p>
 */
public class CDPermittedTypesAreSubtypes implements CDBasisASTCDClassCoCo,
    CDInterfaceAndEnumASTCDInterfaceCoCo {
  
  /** Error code for permitted types that are not direct subtypes. */
  public static final String ERROR_CODE = "0xCDCFA";
  
  /**
   * Checks the {@code permits} clause of a class, if present.
   *
   * @param clazz class to check
   */
  @Override
  public void check(ASTCDClass clazz) {
    if (clazz.isPresentCDPermitsUsage()) {
      checkPermits(clazz.getSymbol(), clazz.getCDPermitsUsage());
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
      checkPermits(iface.getSymbol(), iface.getCDPermitsUsage());
    }
  }
  
  /**
   * Checks whether all permitted types directly extend or implement the
   * declaring sealed type.
   *
   * @param declarer symbol of the sealed class or interface
   * @param permits permits clause to check
   */
  protected void checkPermits(OOTypeSymbol declarer, ASTCDPermitsUsage permits) {
    IOOSymbolsScope scope = declarer.getEnclosingScope();
    
    for (ASTMCObjectType type : permits.getPermittedList()) {
      Optional<OOTypeSymbol> permitted = scope.resolveOOType(CDPermitsTargetsResolvable.rawTypeName(
          type));
      
      if (permitted.isEmpty()) {
        continue; // handled by CDPermitsTargetsResolvable
      }
      
      if (!directSupertypeNamesOf(permitted.get()).contains(declarer.getFullName())) {
        Log.error(String.format(
            "%s: Permitted type %s does not extend or implement its sealed declarer %s.",
            ERROR_CODE, permitted.get().getName(), declarer.getName()), type
                .get_SourcePositionStart());
      }
    }
  }
  
  /**
   * Returns the full names of the direct supertypes of the given type.
   *
   * <p>The supertypes are read from the AST, so this works before a completer
   * run.</p>
   *
   * @param sym type symbol whose direct supertypes are collected
   * @return full names of directly extended or implemented types
   */
  protected Set<String> directSupertypeNamesOf(OOTypeSymbol sym) {
    Set<String> out = new HashSet<>();
    
    if (!sym.isPresentAstNode()) {
      return out;
    }
    
    List<ASTMCObjectType> targets = new ArrayList<>();
    
    ICD4AnalysisTypeDispatcher typeDispatcher = CD4AnalysisMill.typeDispatcher();
    if (typeDispatcher.isCDBasisASTCDClass(sym.getAstNode())) {
      ASTCDClass clazz = typeDispatcher.asCDBasisASTCDClass(sym.getAstNode());
      if (clazz.isPresentCDExtendUsage()) {
        targets.addAll(clazz.getCDExtendUsage().getSuperclassList());
      }
      if (clazz.isPresentCDInterfaceUsage()) {
        targets.addAll(clazz.getCDInterfaceUsage().getInterfaceList());
      }
    }
    else if (typeDispatcher.isCDInterfaceAndEnumASTCDInterface(sym.getAstNode())) {
      var iface = typeDispatcher.asCDInterfaceAndEnumASTCDInterface(sym.getAstNode());
      if (iface.isPresentCDExtendUsage()) {
        targets.addAll(iface.getCDExtendUsage().getSuperclassList());
      }
    }
    else if (typeDispatcher.isCDInterfaceAndEnumASTCDEnum(sym.getAstNode())) {
      var cdEnum = typeDispatcher.asCDInterfaceAndEnumASTCDEnum(sym.getAstNode());
      if (cdEnum.isPresentCDInterfaceUsage()) {
        targets.addAll(cdEnum.getCDInterfaceUsage().getInterfaceList());
      }
    }
    
    IOOSymbolsScope sc = sym.getEnclosingScope();
    for (ASTMCObjectType t : targets) {
      sc.resolveOOType(CDPermitsTargetsResolvable.rawTypeName(t)).ifPresent(s -> out.add(s
          .getFullName()));
    }
    
    return out;
  }
  
}
