package de.monticore.syntaxdiff;

import java.util.List;

// Diff type which contains two ASTNodes (with their original type) and a list of field diffs between them
public class ElementDiff<ASTNodeType> {
  protected final ASTNodeType cd1Element;

  protected final ASTNodeType cd2Element;

  protected final List<FieldDiff<SyntaxDiff.Op, ?>> diffList;

  public ASTNodeType getCd1Element() {
    return cd1Element;
  }

  public ASTNodeType getCd2Element() {
    return cd2Element;
  }

  public List<FieldDiff<SyntaxDiff.Op, ?>> getDiffList() {
    return diffList;
  }

  public ElementDiff(ASTNodeType cd1Element, ASTNodeType cd2Element, List<FieldDiff<SyntaxDiff.Op, ?>> diffList) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;
    this.diffList = diffList;
  }

}
