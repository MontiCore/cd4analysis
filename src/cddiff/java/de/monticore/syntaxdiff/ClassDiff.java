package de.monticore.syntaxdiff;

  import de.monticore.ast.ASTNode;
  import de.monticore.cd4codebasis._ast.ASTCDMethod;
  import de.monticore.cdbasis._ast.ASTCDAttribute;
  import de.monticore.cdbasis._ast.ASTCDClass;

  import java.util.ArrayList;
  import java.util.List;

  // Diff type which contains two ASTNodes (with their original type) and a list of field diffs between them
  public class ClassDiff {
    protected final ASTCDClass cd1Element;

    protected final ASTCDClass cd2Element;

    protected int diffSize;

    protected final List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> diffList;

    protected final List<ElementDiff<ASTCDAttribute>> matchedAttributesList;

    public List<ElementDiff<ASTCDAttribute>> getMatchedAttributesList() {
      return matchedAttributesList;
    }

    public List<ASTCDAttribute> getDeleletedAttributes() {
      return deleletedAttributes;
    }

    public List<ASTCDAttribute> getAddedAttributes() {
      return addedAttributes;
    }

    protected final List<ASTCDAttribute> deleletedAttributes;

    protected final List<ASTCDAttribute> addedAttributes;

    public ASTCDClass getCd1Element() {
      return cd1Element;
    }

    public ASTCDClass getCd2Element() {
      return cd2Element;
    }

    public int getDiffSize() {
      return diffSize;
    }

    public List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> getDiffList() {
      return diffList;
    }


    // Class CD1, Class CD2, DiffList(Signature), DiffListAttributes, (DiffListMethods, DiffListConstructors)
    public ClassDiff(ASTCDClass cd1Element,
                     ASTCDClass cd2Element,
                     List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> diffList,
                     List<ElementDiff<ASTCDAttribute>> matchedAttributesList,
                     List<ASTCDAttribute> deleletedAttributes,
                     List<ASTCDAttribute> addedAttributes
    ) {
      this.cd1Element = cd1Element;
      this.cd2Element = cd2Element;
      this.diffList = diffList;
      this.matchedAttributesList = matchedAttributesList;
      this.deleletedAttributes = deleletedAttributes;
      this.addedAttributes = addedAttributes;
      this.diffSize = calculateDiffSize();
    }

    private int calculateDiffSize(){
      int size = diffList.size();

      List<Integer> cd1attributeLines = new ArrayList<>();
      for (ASTCDAttribute cd1attribute : cd1Element.getCDAttributeList()){
        cd1attributeLines.add(cd1attribute.get_SourcePositionStart().getLine());
      }

      List<Integer> cd2attributeLines = new ArrayList<>();
      for (ASTCDAttribute cd2attribute : cd2Element.getCDAttributeList()){
        cd2attributeLines.add(cd2attribute.get_SourcePositionStart().getLine());
      }
      size += cd1attributeLines.size()-matchedAttributesList.size()+cd2attributeLines.size()-matchedAttributesList.size();
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
