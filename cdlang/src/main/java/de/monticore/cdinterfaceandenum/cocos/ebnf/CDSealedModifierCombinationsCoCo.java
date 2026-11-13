/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._util.ICD4AnalysisTypeDispatcher;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the modifiers of a class or interface form a combination that Java allows.
 *
 * <p>Covers mutual exclusion of {@code final}, {@code sealed} and {@code non-sealed}
 * keywords, the contradiction between {@code abstract} and {@code final}, {@code final}
 * keyword with interfaces, and the modifiers that declare an enclosing type.</p>
 *
 * <p>Distinction between enclosing scopes cannot be made currently
 * so it is supplied with {@link #topLevel}</p>
 */
public class CDSealedModifierCombinationsCoCo implements CDBasisASTCDClassCoCo,
    CDInterfaceAndEnumASTCDInterfaceCoCo {
  
  public static final String ERROR_CODE = "[ERROR]:";
  
  protected static final String EXCLUSIVE = ERROR_CODE
      + " Type '%s' must have at most one of 'final', 'sealed' and 'non-sealed'.";
  
  protected static final String ABSTRACT_FINAL = ERROR_CODE
      + " Type '%s' must not be both 'abstract' and 'final'.";
  
  protected static final String FINAL_INTERFACE = ERROR_CODE
      + " Interface '%s' must not be 'final'.";
  
  protected static final String PERMITS_WITHOUT_SEALED = ERROR_CODE
      + " Type '%s' has a 'permits' clause but is not declared 'sealed'.";
  
  protected static final String TOP_LEVEL_VISIBILITY = ERROR_CODE
      + " Top-level type '%s' must not be '%s'.";
  
  /** true if the checked model represents top-level types, false for nested ones. */
  protected final boolean topLevel;
  
  public CDSealedModifierCombinationsCoCo(boolean topLevel) {
    this.topLevel = topLevel;
  }
  
  /**
   * Checks the {@code permits} clause of a class, if present.
   *
   * @param node class to check
   */
  @Override
  public void check(ASTCDClass node) {
    checkModifiers(node, false);
    checkPermits(node.isPresentCDPermitsUsage(), node);
  }
  
  /**
   * Checks the modifiers and {@code permits} clause of an interface.
   *
   * @param node interface to check
   */
  @Override
  public void check(ASTCDInterface node) {
    checkModifiers(node, true);
    checkPermits(node.isPresentCDPermitsUsage(), node);
  }
  
  /**
   * Checks the modifier combinations Java permits on a class or interface declaration.
   *
   * <p> {@code final}, {@code sealed} and {@code non-sealed} have the same rules regardless of
   * enclosing scope. {@code private}, {@code protected} and {@code static} are legal for nested
   * types only.
   * The distinction of enclosing scope is supplied through {@link #topLevel} </p>
   *
   * @param node class or interface whose modifiers are checked
   * @param isInterface true if the node is an interface, false for a class
   */
  protected void checkModifiers(ASTCDType node, boolean isInterface) {
    ASTModifier mod = node.getModifier();
    boolean sealed = isSealed(mod);
    boolean nonSealed = isNonSealed(mod);
    String name = node.getName();
    
    /**
     * Checks inconsistencies in the usage of 'sealed', 'final' and 'non-sealed'
     *
     * 'sealed', 'final' and 'non-sealed' are mutually exclusive and cannot occur together
     */
    if ((mod.isFinal() && sealed) || (mod.isFinal() && nonSealed) || (sealed && nonSealed)) {
      Log.error(String.format(EXCLUSIVE, name), node.get_SourcePositionStart());
    }
    
    /**
     * Checks inconsistencies in the usage of 'abstract' and 'final'
     *
     * 'abstract' and 'final' are mutually exclusive and cannot occur together
     */
    if (mod.isAbstract() && mod.isFinal()) {
      Log.error(String.format(ABSTRACT_FINAL, name), node.get_SourcePositionStart());
    }
    
    /**
     * Checks inconsistencies in the usage of interfaces
     *
     * interfaces have to be implemented or extended and can never be final
     */
    if (isInterface && mod.isFinal()) {
      Log.error(String.format(FINAL_INTERFACE, name), node.get_SourcePositionStart());
    }
    
    /**
     * Checks inconsistencies in the visibility of the top-level class
     *
     * A top-level class can only ever be public and non-static
     *
     * @param topLevel indicates if a class is top-level or nested
     */
    if (topLevel) {
      if (mod.isPrivate()) {
        Log.error(String.format(TOP_LEVEL_VISIBILITY, name, "private"), node
            .get_SourcePositionStart());
      }
      if (mod.isProtected()) {
        Log.error(String.format(TOP_LEVEL_VISIBILITY, name, "protected"), node
            .get_SourcePositionStart());
      }
      if (mod.isStatic()) {
        Log.error(String.format(TOP_LEVEL_VISIBILITY, name, "static"), node
            .get_SourcePositionStart());
      }
    }
  }
  
  /**
   * Checks that a {@code permits} clause only occurs on a sealed type.
   *
   * @param hasPermits true if the declaration has a permits clause
   * @param node class or interface to check
   */
  protected void checkPermits(boolean hasPermits, ASTCDType node) {
    if (hasPermits && !isSealed(node.getModifier())) {
      Log.error(String.format(PERMITS_WITHOUT_SEALED, node.getName()), node
          .get_SourcePositionStart());
    }
  }
  
  /**
   * Returns whether the given modifier has the {@code sealed} keyword.
   *
   * @param mod modifier to inspect
   * @return true if the modifier is a sealed modifier declared sealed
   */
  protected boolean isSealed(ASTModifier mod) {
    ICD4AnalysisTypeDispatcher dispatcher = CD4AnalysisMill.typeDispatcher();
    return dispatcher.isCDBasisASTCDSealedModifier(mod) && dispatcher.asCDBasisASTCDSealedModifier(
        mod).isSealed();
  }
  
  /**
   * Returns whether the given modifier has the {@code non-sealed} keyword.
   *
   * @param mod modifier to inspect
   * @return true if the modifier is a sealed modifier declared non-sealed
   */
  protected boolean isNonSealed(ASTModifier mod) {
    ICD4AnalysisTypeDispatcher dispatcher = CD4AnalysisMill.typeDispatcher();
    return dispatcher.isCDBasisASTCDSealedModifier(mod) && dispatcher.asCDBasisASTCDSealedModifier(
        mod).isNonSealed();
  }
  
}
