/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.MatchingStrategy;

/**
 * Specific {@link MatchingStrategy} interface for CD methods. A strategy for methods always needs
 * to know a reference type against whose methods it matches the concrete methods.
 */
public interface CDMethodMatchingStrategy extends ExternalCandidatesMatchingStrategy<ASTCDMethod> {
  
  void setReferenceType(ASTCDType refType);
  
}
