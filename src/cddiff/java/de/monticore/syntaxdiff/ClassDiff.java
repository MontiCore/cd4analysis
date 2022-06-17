package de.monticore.syntaxdiff;

  import de.monticore.ast.ASTNode;
  import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
  import de.monticore.cdassociation._ast.ASTCDAssociationNode;
  import de.monticore.cdbasis._ast.ASTCDAttribute;
  import de.monticore.cdbasis._ast.ASTCDClass;
  import de.monticore.cdbasis._ast.ASTCDExtendUsage;
  import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
  import de.monticore.prettyprint.IndentPrinter;
  import de.monticore.types.mcbasictypes._ast.ASTMCType;
  import de.monticore.umlmodifier._ast.ASTModifier;
  import de.monticore.syntaxdiff.FieldDiff;
  import de.monticore.syntaxdiff.SyntaxDiff;

  import java.util.ArrayList;
  import java.util.Comparator;
  import java.util.List;
  import java.util.Optional;

/**
 * Diff Type for Classes
 * Use the constructor to create a diff between two classes
 * This diff type contains information extracted from the provided classes
 */
  public class ClassDiff {
    protected final ASTCDClass cd1Element;

    protected final ASTCDClass cd2Element;

    protected int diffSize;

    protected List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> diffList;

    protected List<ElementDiff<ASTCDAttribute>> matchedAttributesList;

    protected List<ASTCDAttribute> deleletedAttributes;

    protected List<ASTCDAttribute> addedAttributes;

    public List<ElementDiff<ASTCDAttribute>> getMatchedAttributesList() {
    return matchedAttributesList;
  }

    public List<ASTCDAttribute> getDeleletedAttributes() {
    return deleletedAttributes;
  }

    public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

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


  /**
   * Constructor of the class diff type
   * @param cd1Element Class from the original model
   * @param cd2Element Class from the target(new) model
   */
    public ClassDiff(ASTCDClass cd1Element, ASTCDClass cd2Element) {
      this.cd1Element = cd1Element;
      this.cd2Element = cd2Element;

      // Set the required parts for diff size calculation
      classDiff(cd1Element, cd2Element);

      this.diffSize = calculateDiffSize();
    }
  /**
   * Calculation of the diff size between the given classes, automaticly calculated on object creation
   * Name changes are weighted more
   * @return Diff size as int
   */
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

  /**
   * Main method of this class, calculates the differences between both classes using checks between every field
   * and Members contained in the class like attributes and methods
   * @param cd1Class Class from the original model
   * @param cd2Class Class from the target(new) model
   */
  private void classDiff(ASTCDClass cd1Class, ASTCDClass cd2Class) {
    List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Class.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Class.getModifier());
    FieldDiff<SyntaxDiff.Op, ASTModifier> assoModifier = SyntaxDiff.getFieldDiff(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }

    // Class Name, non-optional (always a String therefore return the full class)
    if (!cd1Class.getName().equals(cd2Class.getName())){
      FieldDiff<SyntaxDiff.Op, ASTCDClass> className = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Class, cd2Class);
      diffs.add(className);
    }

    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Class.isPresentCDExtendUsage()) ? Optional.of(cd1Class.getCDExtendUsage()) : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Class.isPresentCDExtendUsage()) ? Optional.of(cd2Class.getCDExtendUsage()) : Optional.empty();
    FieldDiff<SyntaxDiff.Op, ASTCDExtendUsage> classExtended = SyntaxDiff.getFieldDiff(cd1Extend, cd2Extend);
    if (classExtended.isPresent()){
      diffs.add(classExtended);
    }


    // Todo: add inherited methods and attributes

    // CDMember diffs, members are: Attributes, Methods, Constructors
    List<List<ElementDiff<ASTCDAttribute>>> attributeDiffList = getAttributeDiffList(cd1Class.getCDAttributeList(),
                                                                                      cd2Class.getCDAttributeList());

    int threshold = 2;
    List<ASTCDAttribute> cd1matchedAttributes = new ArrayList<>();
    List<ASTCDAttribute> cd2matchedAttributes = new ArrayList<>();
    List<ElementDiff<ASTCDAttribute>> matchedAttributes = new ArrayList<>();
    List<ASTCDAttribute> deletedAttributes = new ArrayList<>();
    List<ASTCDAttribute> addedAttributes = new ArrayList<>();

    for (List<ElementDiff<ASTCDAttribute>> currentAttributeList: attributeDiffList){
      if (!currentAttributeList.isEmpty()) {
        ElementDiff<ASTCDAttribute> currentAttriDiff = currentAttributeList.get(0);
        ASTCDAttribute cd1Attri = currentAttriDiff.getCd1Element();
        ASTCDAttribute cd2Attri = currentAttriDiff.getCd2Element();
        if (!cd1matchedAttributes.contains(cd1Attri) && !cd2matchedAttributes.contains(cd2Attri)){
          int currentMinDiff = currentAttriDiff.getDiffSize();
          // Todo: Check if there is a match to the target attribute with a smaller diff size
          if (currentMinDiff < threshold){
            matchedAttributes.add(currentAttriDiff);
            cd1matchedAttributes.add(cd1Attri);
            cd2matchedAttributes.add(cd2Attri);
          }
        }
      }
    }

    for (ASTCDAttribute attr : cd1Class.getCDAttributeList()){
      if (!cd1matchedAttributes.contains(attr)){
        deletedAttributes.add(attr);
      }
    }
    for (ASTCDAttribute attr : cd2Class.getCDAttributeList()){
      if (!cd2matchedAttributes.contains(attr)){
        addedAttributes.add(attr);
      }
    }
    this.diffList = diffs;
    this.matchedAttributesList = matchedAttributes;
    this.deleletedAttributes = deletedAttributes;
    this.addedAttributes = addedAttributes;


    // Only difference between methods and constructors is the absent return type for constructors
    // cd1Class.getCDMethodList()
    // Methods
    //  CDMethod implements CDMethodSignature =
    //    Modifier
    //    MCReturnType
    //    Name "(" (CDParameter || ",")* ")"
    //    CDThrowsDeclaration?
    //    ";";


    // Conctructor
    // CDConstructor implements CDMethodSignature =
    //    Modifier
    //    Name "(" (CDParameter || ",")* ")"
    //    CDThrowsDeclaration?
    //    ";";
  }

  /**
   * Help method for calculating the class diff because each classcontains multiple attributes which need to be matched
   * @param cd1MemberList List of attributes from the original model
   * @param cd2MemberList List of attributes from the target(new) model
   * @return Returns a difflist for each attribute, ordered by diffsize (small diff values == similar)
   */
  public static List<List<ElementDiff<ASTCDAttribute>>> getAttributeDiffList(List<ASTCDAttribute> cd1MemberList, List<ASTCDAttribute> cd2MemberList) {
    List<List<ElementDiff<ASTCDAttribute>>> diffs = new ArrayList<>();
    for (ASTCDAttribute cd1Member : cd1MemberList){
      List<ElementDiff<ASTCDAttribute>> cd1MemberMatches = new ArrayList<>();
      for (ASTCDAttribute cd2Member : cd2MemberList) {
        cd1MemberMatches.add(new ElementDiff<>(cd1Member, cd2Member, getAttributeDiff(cd1Member, cd2Member)));
      }
      // Sort by size of diffs, ascending
      cd1MemberMatches.sort(Comparator.comparing(ElementDiff -> ElementDiff.getDiffList().size()));
      diffs.add(cd1MemberMatches);
    }
    return diffs;
  }

  /**
   * Help method for calculating the attribute diff
   * @return List of FieldDiffs between the provided attributes
   */
  //Attribute: Modifier MCType Name ("=" initial:Expression)? ";";
  private static List<FieldDiff<SyntaxDiff.Op, ? extends ASTNode>> getAttributeDiff(ASTCDAttribute cd1Member, ASTCDAttribute cd2Member) {
    List<FieldDiff<SyntaxDiff.Op,  ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Member.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Member.getModifier());
    FieldDiff<SyntaxDiff.Op, ASTModifier> attributeModifier = SyntaxDiff.getFieldDiff(cd1Modi, cd2Modi);
    if (attributeModifier.isPresent()){
      diffs.add(attributeModifier);
    }

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Member.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Member.getMCType());
    FieldDiff<SyntaxDiff.Op, ASTMCType> attributeType = SyntaxDiff.getFieldDiff(cd1Type, cd2Type);
    if (attributeType.isPresent()){
      diffs.add(attributeType);
    }
    // Name, non-optional
    if (!cd1Member.getName().equals(cd2Member.getName())){
      FieldDiff<SyntaxDiff.Op, ASTCDAttribute> attributeName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Member, cd2Member);
      diffs.add(attributeName);
    }

    // Initial expression, optional
    Optional<ASTExpression> cd1Initial = (cd1Member.isPresentInitial()) ? Optional.of(cd1Member.getInitial()) : Optional.empty();
    Optional<ASTExpression> cd2Initial = (cd2Member.isPresentInitial()) ? Optional.of(cd2Member.getInitial()) : Optional.empty();
    FieldDiff<SyntaxDiff.Op, ASTExpression> attributeInital = SyntaxDiff.getFieldDiff(cd1Initial, cd2Initial);
    if (attributeInital.isPresent()){
      diffs.add(attributeInital);
    }
    return diffs;
  }
  /**
   * Print function for the class diff, used to output the diffs appropriately formated
   */
  public void print() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    StringBuilder output = new StringBuilder();
    output.append("Matched Classes ")
      .append(this.getCd1Element().get_SourcePositionStart().getLine())
      .append(" and ")
      .append(this.getCd2Element().get_SourcePositionStart().getLine())
      .append(System.lineSeparator())
      .append("Diff size: ")
      .append(this.getDiffSize())
      .append(System.lineSeparator())
      .append(pp.prettyprint(this.getCd1Element()))
      .append(pp.prettyprint(this.getCd2Element()))
      .append(System.lineSeparator());

    for (FieldDiff<SyntaxDiff.Op, ?> diff : this.getDiffList()) {
      if (diff.isPresent()) {
        diff.getOperation().ifPresent(operation -> output.append(operation).append(": "));
        diff.getCd1Value().ifPresent(cd1v -> output.append(cd1v).append(" -> "));
        diff.getCd2Value().ifPresent(cd2v -> output.append(cd2v));
        output.append(System.lineSeparator());
      }
    }
    for (ElementDiff<ASTCDAttribute> matched : this.getMatchedAttributesList()) {
      for (FieldDiff<SyntaxDiff.Op, ?> diff : matched.getDiffList()) {
        if (diff.isPresent()) {
          diff.getOperation().ifPresent(operation -> output.append(operation).append(": "));
          diff.getCd1Value().ifPresent(cd1v -> output.append(cd1v).append(" -> "));
          diff.getCd2Value().ifPresent(cd2v -> output.append(cd2v));
          output.append(System.lineSeparator());
        }
      }
    }
    output.append("Deleted Attributes: ").append(System.lineSeparator());
    for (ASTCDAttribute a : this.getDeleletedAttributes()) {
      output.append(a.get_SourcePositionStart().getLine())
        .append(" ")
        .append(pp.prettyprint(a));
    }
    output.append("Added Attributes: ").append(System.lineSeparator());
    for (ASTCDAttribute a : this.getAddedAttributes()) {
      output.append(a.get_SourcePositionStart().getLine())
        .append(" ")
        .append(pp.prettyprint(a));
    }

    System.out.println(output);
  }
  }
