package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.ArrayList;
import java.util.Set;

public interface MatchingStrategy<T> {
  // Set with the matched elements in which we have the matched element, the concrete class diagram
  // and the reference class diagram
  Set<T> getMatchedElements(T srcElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD);

  // We have a List with the matched elements
  default ArrayList<T> getMatchedElementsList(
      T srcElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    return new ArrayList<T>(getMatchedElements(srcElem, srcCD, tgtCD));
  }

  // We check if the combinations of elements are a match, or not
  boolean isMatched(T srcElem, T tgtElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD);
}
