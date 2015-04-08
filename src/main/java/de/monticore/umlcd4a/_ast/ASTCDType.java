/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a._ast;

import com.google.common.base.Optional;

/**
 * HW super type for classes, interfaces and enums
 *
 * @author Robert Heim, Galina Volkova
 */
public interface ASTCDType extends mc.ast.ASTNode, ASTCD4AnalysisBase {
  
  public String getName();
  
  public Optional<ASTModifier> getModifier();
  
}
