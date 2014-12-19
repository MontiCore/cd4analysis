/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos._tobegenerated;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import mc.ast.ASTNode;
import mc.ast.DDVisitor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._tool.CD4AnalysisBaseVisitor;
import de.monticore.cocos.CoCoChecker;
import de.monticore.cocos.CoCoError;
import de.monticore.cocos.ContextConditionResult;

/**
 * TODO: generate this file.
 *
 * @author Robert Heim
 */
public class CD4ACoCoChecker extends CD4AnalysisBaseVisitor implements CoCoChecker {
  
  private CD4ACoCoProfile profile;
  
  private Collection<ContextConditionResult> results = Sets.newHashSet();
  
  private Collection<CoCoError> errors = Sets.newHashSet();
  
  private DDVisitor visitor;
  
  public CD4ACoCoChecker(CD4ACoCoProfile profile) {
    this.profile = requireNonNull(profile);
    this.visitor = new DDVisitor();
    this.visitor.addClient(this);
  }
  
  @Override
  public Boolean checkAll(ASTNode root) {
    
    // check all CoCos (double dispatch visitor pattern)
    root.traverse(visitor);
    
    // accumulate results
    boolean succeeded = true;
    for (ContextConditionResult result : results) {
      if (!result.isSucceeded()) {
        succeeded = false;
        errors.addAll(result.getErrors());
      }
    }
    
    return succeeded;
  }
  @Override
  public void clear() {
    this.errors.clear();
    this.results.clear();
  }
  @Override
  public Collection<CoCoError> getErrors() {
    // defensive copy
    return ImmutableList.copyOf(this.errors);
  }
  
  /**
   * @return results
   */
  public Collection<ContextConditionResult> getResults() {
    // defensive copy
    return ImmutableList.copyOf(this.results);
  }
  
  @Override
  public void visit(ASTCDClass node) {
    for (CD4AClassCoCo coco : profile.getAstCDClassCocos()) {
      ContextConditionResult result = coco.check(node);
      results.add(result);
    }
  }
  
  @Override
  public void visit(ASTCDAttribute node) {
    for (CD4AAttributeCoCo coco : profile.getAstCDAttributeCocos()) {
      ContextConditionResult result = coco.check(node);
      results.add(result);
    }
  }
  
  // TODO generate all the other visit methods accordingly.
  
}
