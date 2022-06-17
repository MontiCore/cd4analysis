package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDAttribute;

import java.util.List;

/**
 * Diff Type for Elements (all-purpose usage for ASTNodes)
 * Use the constructor to create a diff between two ASTNode Elements (classes, associations enums...)
 * This diff type contains information extracted from the provided elements
 */
public class ElementDiff<ASTNodeType> {
  protected final ASTNodeType cd1Element;

  protected final ASTNodeType cd2Element;

  protected int diffSize;

  protected final List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> diffList;

  public ASTNodeType getCd1Element() {
    return cd1Element;
  }

  public ASTNodeType getCd2Element() {
    return cd2Element;
  }

  public int getDiffSize() {
    return diffSize;
  }

  public List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> getDiffList() {
    return diffList;
  }
  /**
   * Constructor of the element diff type
   * @param cd1Element Element from the original model
   * @param cd2Element Element from the target(new) model
   * @param diffList List of diffs between the elements
   */
  public ElementDiff(ASTNodeType cd1Element, ASTNodeType cd2Element, List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> diffList) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;
    this.diffList = diffList;
    this.diffSize = calculateDiffSize();
  }
  private int calculateDiffSize(){
    int size = diffList.size();
    for (FieldDiff<SyntaxDiff.Op, ? extends ASTNode> diff : diffList){
      if (diff.isPresent() && diff.getCd1Value().isPresent()){
        // Name Diffs are weighted doubled compared to every other diff
        // Parent Object in FieldDiff when we check the name of it (when there is no specific node for the name)
        if (diff.getCd1Value().get().getClass().getSimpleName().equals("ASTCDAttribute")
          || diff.getCd1Value().get().getClass().getSimpleName().equals("ASTMCQualifiedName")
          || diff.getCd1Value().get().getClass().getSimpleName().equals("ASTCDClass")

        ) {
          size += 1;
        }
      }
    }
    return size;
  }
}
