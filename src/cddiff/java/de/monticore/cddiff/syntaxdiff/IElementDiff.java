package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;

import java.util.List;

public interface IElementDiff {


  StringBuilder getInterpretation();

  void setInterpretation(StringBuilder builder);

  int getBreakingChange();

  List<CDSyntaxDiff.Interpretation> getInterpretationList();

  void setInterpretationList(List<CDSyntaxDiff.Interpretation> newInterpretationList);

  double getDiffSize();

  void addDiffSize(int value);

  List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> getDiffList();

  String combineWithoutNulls(List<String> stringList);
}
