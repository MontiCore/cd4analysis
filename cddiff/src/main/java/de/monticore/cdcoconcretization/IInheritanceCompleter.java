package de.monticore.cdcoconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;

public interface IInheritanceCompleter {
  void completeInheritance(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD);

  void setTypeMatcher(MatchingStrategy<ASTCDType> typeMatcher);
}
