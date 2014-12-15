/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

import java.util.Collection;
import java.util.HashSet;

import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cocos.ContextConditionProfile;

/**
 * TODO: This Class will probably be replaced when the
 * {@link ContextConditionProfile} is further implemented.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CoCoChecker extends ContextConditionProfile {
  
  private Collection<CoCoError> errors = new HashSet<CoCoError>();
  
  private Collection<ClassCoCo> classCoCos = new HashSet<ClassCoCo>();
  
  public void addClassCoCo(ClassCoCo contextCondition) {
    classCoCos.add(contextCondition);
  }
  
  /**
   * Checks all registered CoCos for the given node.
   * 
   * @param node the ast node which the check is performed on.
   * @return true if all CoCos succeeded, false if any registered CoCo fails.
   */
  private boolean checkAll(ASTCDClass node) {
    boolean success = true;
    for (ClassCoCo coco : classCoCos) {
      CoCoResult r = coco.check(node);
      if (!r.isSucceeded()) {
        success = false;
        errors.addAll(r.getErrors());
      }
    }
    return success;
  }
  
  /**
   * @return errors
   */
  public Collection<CoCoError> getErrors() {
    return this.errors;
  }
  
  public void clearErrors() {
    this.errors.clear();
  }
  
  /**
   * Checks all registered CoCos for the given node.
   * 
   * @param node the ast node for which the checks are performed.
   * @return true if all CoCos succeeded, false if any registered CoCo fails.
   */
  public boolean checkAll(ASTCDCompilationUnit cdDef) {
    boolean success = true;
    for (ASTCDClass node : cdDef.getCDDefinition().getCDClasses()) {
      if (!checkAll(node)) {
        success = false;
      }
    }
    
    // TODO check other ast-types and/or symbols
    
    return success;
  }
}
