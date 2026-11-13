/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDClassCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDInterfaceCoCo;
import de.monticore.cd4codebasis._util.ICD4CodeBasisTypeDispatcher;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

/**
 * Checks the modifier combinations Java allows for classes and interfaces with respect to
 * {@code sealed}, {@code non-sealed}, {@code final}, and {@code abstract} (JLS 17, §8.1.1, §9.1.1).
 */
public class CDSealedModifierCombinations implements CD4CodeBasisASTCDClassCoCo,
    CD4CodeBasisASTCDInterfaceCoCo {
  
  public static final String ERROR_CODE_EXCLUSIVE = "0xCDCFB";
  
  public static final String ERROR_CODE_ABSTRACT_FINAL = "0xCDCFC";
  
  public static final String ERROR_CODE_FINAL_INTERFACE = "0xCDCFD";
  
  public static final String ERROR_CODE_PERMITS_WITHOUT_SEALED = "0xCDCFE";
  
  @Override
  public void check(ASTCDClass node) {
    checkExclusive(node);
    if (node.getModifier().isAbstract() && node.getModifier().isFinal()) {
      error(ERROR_CODE_ABSTRACT_FINAL, "Class '%s' must not be both 'abstract' and 'final'.", node);
    }
    if (node.isPresentCDPermitsUsage() && !isSealed(node.getModifier())) {
      error(ERROR_CODE_PERMITS_WITHOUT_SEALED,
          "Class '%s' has a 'permits' clause but is not 'sealed'.", node);
    }
  }
  
  @Override
  public void check(ASTCDInterface node) {
    checkExclusive(node);
    if (node.getModifier().isFinal()) {
      error(ERROR_CODE_FINAL_INTERFACE, "Interface '%s' must not be 'final'.", node);
    }
    if (node.isPresentCDPermitsUsage() && !isSealed(node.getModifier())) {
      error(ERROR_CODE_PERMITS_WITHOUT_SEALED,
          "Interface '%s' has a 'permits' clause but is not 'sealed'.", node);
    }
  }
  
  /**
   * 'final', 'sealed', and 'non-sealed' are mutually exclusive.
   */
  protected void checkExclusive(ASTCDType node) {
    ASTModifier mod = node.getModifier();
    int count = (mod.isFinal() ? 1 : 0) + (isSealed(mod) ? 1 : 0) + (isNonSealed(mod) ? 1 : 0);
    if (count > 1) {
      error(ERROR_CODE_EXCLUSIVE,
          "Type '%s' must have at most one of 'final', 'sealed', and 'non-sealed'.", node);
    }
  }
  
  protected void error(String code, String message, ASTCDType node) {
    Log.error(code + ": " + String.format(message, node.getName()), node.get_SourcePositionStart(),
        node.get_SourcePositionEnd());
  }
  
  protected static boolean isSealed(ASTModifier mod) {
    ICD4CodeBasisTypeDispatcher dispatcher = CD4CodeBasisMill.typeDispatcher();
    return dispatcher.isCD4CodeBasisASTCDSealedModifier(mod) && dispatcher
        .asCD4CodeBasisASTCDSealedModifier(mod).isSealed();
  }
  
  protected static boolean isNonSealed(ASTModifier mod) {
    ICD4CodeBasisTypeDispatcher dispatcher = CD4CodeBasisMill.typeDispatcher();
    return dispatcher.isCD4CodeBasisASTCDSealedModifier(mod) && dispatcher
        .asCD4CodeBasisASTCDSealedModifier(mod).isNonSealed();
  }
  
}
