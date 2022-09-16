package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;

import java.util.Optional;

public interface IASTNodeDiff<T1 extends ASTNode, T2 extends ASTNode> {
  boolean isPresent();

  Optional<CDSyntaxDiff.Interpretation> getInterpretation();

  Optional<CDSyntaxDiff.Op> getOperation();

  Optional<T1> getCd1Value();

  Optional<T2> getCd2Value();
}
