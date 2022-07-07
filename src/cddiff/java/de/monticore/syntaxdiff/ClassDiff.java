package de.monticore.syntaxdiff;

  import de.monticore.ast.ASTNode;
  import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
  import de.monticore.cd4codebasis._ast.ASTCDConstructor;
  import de.monticore.cd4codebasis._ast.ASTCDMethod;
  import de.monticore.cd4codebasis._ast.ASTCDParameter;
  import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
  import de.monticore.cdbasis._ast.ASTCDAttribute;
  import de.monticore.cdbasis._ast.ASTCDBasisNode;
  import de.monticore.cdbasis._ast.ASTCDClass;
  import de.monticore.cdbasis._ast.ASTCDExtendUsage;
  import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
  import de.monticore.prettyprint.IndentPrinter;
  import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
  import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
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
  public class ClassDiff extends AbstractDiffType{
    protected final ASTCDClass cd1Element;

    protected final ASTCDClass cd2Element;

    protected double diffSize;

    protected List<FieldDiff<? extends ASTNode>> diffList;

    protected List<ElementDiff<ASTCDAttribute>> matchedAttributesList;
    protected List<ASTCDAttribute> deleletedAttributes;
    protected List<ASTCDAttribute> addedAttributes;

    protected List<ElementDiff<ASTCDMethod>> matchedMethodeList;
    protected List<ASTCDMethod> deleletedMethodes;
    protected List<ASTCDMethod> addedMethode;

    protected List<ElementDiff<ASTCDConstructor>> matchedConstructorList;
    protected List<ASTCDConstructor> deleletedConstructor;
    protected List<ASTCDConstructor> addedConstructor;

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

    public double getDiffSize() {
      return diffSize;
    }

    public List<FieldDiff<? extends ASTNode>> getDiffList() {
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
   * Name changes are weighted more and each member(attribute/methodes/...) add at most one to the size
   * @return Diff size as double
   */
    private double calculateDiffSize(){
      //Diff size of signature
      double size = diffList.size();

      // Diff size of attributes (amounts to max ~ 1)
      for (ElementDiff<ASTCDAttribute> i : matchedAttributesList){
        if (i.getCd1Element().isPresentInitial()){
          size += i.getDiffSize()/4.0;
        }else {
          size += i.getDiffSize()/3.0;
        }
      }
      size += deleletedAttributes.size() + addedAttributes.size();

      //Todo: Add Parameter Diff to size calculation
      //Diff size of methodes
      for (ElementDiff<ASTCDMethod> i : matchedMethodeList){
        if (i.getCd1Element().isPresentCDThrowsDeclaration()){
          size += i.getDiffSize()/4.0;
        }else {
          size += i.getDiffSize()/3.0;
        }
      }
      size += deleletedMethodes.size() + addedMethode.size();

      // Diff size of constructors
      for (ElementDiff<ASTCDConstructor> i : matchedConstructorList){
        if (i.getCd1Element().isPresentCDThrowsDeclaration()){
          size += i.getDiffSize()/4.0;
        }else {
          size += i.getDiffSize()/3.0;
        }
      }
      size += deleletedConstructor.size() + addedConstructor.size();
      size += addWeightToDiffSize(diffList);

      return size;
    }

  /**
   * Main method of this class, calculates the differences between both classes using checks between every field
   * and Members contained in the class like attributes and methods
   * @param cd1Class Class from the original model
   * @param cd2Class Class from the target(new) model
   */
  private void classDiff(ASTCDClass cd1Class, ASTCDClass cd2Class) {
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Class.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Class.getModifier());
    FieldDiff<ASTModifier> assoModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }

    // Class Name, non-optional (always a String therefore return the full class)
    if (!cd1Class.getName().equals(cd2Class.getName())){
      FieldDiff<ASTCDClass> className = new FieldDiff<>(SyntaxDiff.Op.CHANGE, Optional.of(cd1Class), Optional.of(cd2Class));
      diffs.add(className);
    }

    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Class.isPresentCDExtendUsage()) ? Optional.of(cd1Class.getCDExtendUsage()) : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Class.isPresentCDExtendUsage()) ? Optional.of(cd2Class.getCDExtendUsage()) : Optional.empty();
    FieldDiff<ASTCDExtendUsage> classExtended = new FieldDiff<>(cd1Extend, cd2Extend);
    if (classExtended.isPresent()){
      diffs.add(classExtended);
    }


    // Todo: add inherited methods and attributes

    // CDMember diffs, members are: Attributes, Methods, Constructors

    // Set the difflist to signature diffs(if any)
    this.diffList = diffs;

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(getElementDiffList(cd1Class.getCDAttributeList(), cd2Class.getCDAttributeList()));
    this.deleletedAttributes = absentElementList(matchedAttributesList, cd1Class.getCDAttributeList());
    this.addedAttributes = absentElementList(matchedAttributesList, cd2Class.getCDAttributeList());

    this.matchedMethodeList = getMatchingList(getElementDiffList(cd1Class.getCDMethodList(), cd2Class.getCDMethodList()));
    this.deleletedMethodes = absentElementList(matchedMethodeList, cd1Class.getCDMethodList());
    this.addedMethode= absentElementList(matchedMethodeList, cd2Class.getCDMethodList());

    this.matchedConstructorList = getMatchingList(getElementDiffList(cd1Class.getCDConstructorList(), cd2Class.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList, cd1Class.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList, cd2Class.getCDConstructorList());

  }



  /**
   * Help method for calculating the class diff because each class can contains multiple methodes which need to be matched
   * @param cd1ElementList List of methodes from the original model
   * @param cd2ElementList List of methodes from the target(new) model
   * @return Returns a difflist for each methodes, ordered by diffsize (small diff values == similar)
   */
  protected static <T> List<List<ElementDiff<T>>> getElementDiffList(List<T> cd1ElementList, List<T> cd2ElementList) {
    List<List<ElementDiff<T>>> diffs = new ArrayList<>();
    for (T cd1Element : cd1ElementList){
      List<ElementDiff<T>> cd1ElementMatches = new ArrayList<>();
      for (T cd2Element : cd2ElementList) {
        cd1ElementMatches.add(new ElementDiff<>(cd1Element, cd2Element, getElementDiff(cd1Element, cd2Element)));
      }
      // Sort by size of diffs, ascending
      cd1ElementMatches.sort(Comparator.comparing(ElementDiff::getDiffSize));
      diffs.add(cd1ElementMatches);
    }
    return diffs;
  }

  /**
   * Help methode caused by type erasure, manages all currently supported ElementalDiff type
   * @param cd1Element Element from CD1 (original Model)
   * @param cd2Element Element from CD2 (New Model)
   * @return List of field diffs between both elements e.g. signature differences
   * @param <T> Type of the element, must be equal
   */
  protected static <T> List<FieldDiff<? extends ASTNode>> getElementDiff(T cd1Element,T cd2Element) {
    if( cd1Element instanceof ASTCDMethod && cd2Element instanceof ASTCDMethod){
      return getMethodeDiff((ASTCDMethod) cd1Element, (ASTCDMethod) cd2Element);
    }
    if( cd1Element instanceof ASTCDParameter && cd2Element instanceof ASTCDParameter){
      return getParameterDiff((ASTCDParameter) cd1Element, (ASTCDParameter) cd2Element);
    }
    if( cd1Element instanceof ASTCDAttribute && cd2Element instanceof ASTCDAttribute){
      return getAttributeDiff((ASTCDAttribute) cd1Element, (ASTCDAttribute) cd2Element);
    }
    if( cd1Element instanceof ASTCDConstructor && cd2Element instanceof ASTCDConstructor){
      return getConstructorDiff((ASTCDConstructor) cd1Element, (ASTCDConstructor) cd2Element);
    }

    throw new UnsupportedOperationException();
  }


  /**
   * Help method for calculating the attribute diff
   * @return List of FieldDiffs between the provided attributes
   */
  //Attribute: Modifier MCType Name ("=" initial:Expression)? ";";
  protected static List<FieldDiff<? extends ASTNode>> getAttributeDiff(ASTCDAttribute cd1Member, ASTCDAttribute cd2Member) {
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Member.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Member.getModifier());
    FieldDiff<ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (attributeModifier.isPresent()){
      diffs.add(attributeModifier);
    }

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Member.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Member.getMCType());
    FieldDiff<ASTMCType> attributeType = new FieldDiff<>(cd1Type, cd2Type);
    if (attributeType.isPresent()){
      diffs.add(attributeType);
    }
    // Name, non-optional
    if (!cd1Member.getName().equals(cd2Member.getName())){
      FieldDiff<ASTCDAttribute> attributeName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, Optional.of(cd1Member), Optional.of(cd2Member));
      diffs.add(attributeName);
    }

    // Initial expression, optional
    Optional<ASTExpression> cd1Initial = (cd1Member.isPresentInitial()) ? Optional.of(cd1Member.getInitial()) : Optional.empty();
    Optional<ASTExpression> cd2Initial = (cd2Member.isPresentInitial()) ? Optional.of(cd2Member.getInitial()) : Optional.empty();
    FieldDiff<ASTExpression> attributeInital = new FieldDiff<>(cd1Initial, cd2Initial);
    if (attributeInital.isPresent()){
      diffs.add(attributeInital);
    }
    return diffs;
  }


  /**
   * Help method for calculating the methode signature diff
   * @return List of FieldDiffs between the provided methodes
   */
  //Methods: Modifier MCReturnType Name "(" (CDParameter || ",")* ")" CDThrowsDeclaration? ";";
  protected static List<FieldDiff<? extends ASTNode>> getMethodeDiff(ASTCDMethod cd1Member, ASTCDMethod cd2Member) {
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Member.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Member.getModifier());
    FieldDiff<ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (attributeModifier.isPresent()){
      diffs.add(attributeModifier);
    }

    // ASTMCReturnType, non-optional
    FieldDiff<ASTMCReturnType> methodeReturnType = new FieldDiff<>(
        Optional.of(cd1Member.getMCReturnType()),
        Optional.of(cd2Member.getMCReturnType()));
    if (methodeReturnType.isPresent()){
      diffs.add(methodeReturnType);
    }
    // Name, non-optional
    if (!cd1Member.getName().equals(cd2Member.getName())){
      FieldDiff<ASTCDMethod> methodeName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, Optional.of(cd1Member), Optional.of(cd2Member));
      diffs.add(methodeName);
    }

    // ThrowsDeclaration, optional
    Optional<ASTCDThrowsDeclaration> cd1ThrowDec = (cd1Member.isPresentCDThrowsDeclaration()) ? Optional.of(cd1Member.getCDThrowsDeclaration()) : Optional.empty();
    Optional<ASTCDThrowsDeclaration> cd2ThrowDec = (cd2Member.isPresentCDThrowsDeclaration()) ? Optional.of(cd2Member.getCDThrowsDeclaration()) : Optional.empty();
    FieldDiff<ASTCDThrowsDeclaration> throwDecl = new FieldDiff<>(cd1ThrowDec, cd2ThrowDec);
    if (throwDecl.isPresent()){
      diffs.add(throwDecl);
    }

    // Parameter List Diff, non-optional (List is empty is no Parameter is present)
    List<List<ElementDiff<ASTCDParameter>>> list = getElementDiffList(cd1Member.getCDParameterList(), cd2Member.getCDParameterList());
    // Todo: Save Parameter Diff in an appropriate way
    return diffs;
  }
  /**
   * Help method for calculating the constructor signature diff
   * @return List of FieldDiffs between the provided constructors
   */
  //Constructor: Modifier Name "(" (CDParameter || ",")* ")" CDThrowsDeclaration? ";";
  protected static List<FieldDiff<? extends ASTNode>> getConstructorDiff(ASTCDConstructor cd1Member, ASTCDConstructor cd2Member) {
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Member.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Member.getModifier());
    FieldDiff<ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (attributeModifier.isPresent()){
      diffs.add(attributeModifier);
    }

    // Name, non-optional
    if (!cd1Member.getName().equals(cd2Member.getName())){
      FieldDiff<ASTCDConstructor> constructorName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, Optional.of(cd1Member), Optional.of(cd2Member));
      diffs.add(constructorName);
    }

    // ThrowsDeclaration, optional
    Optional<ASTCDThrowsDeclaration> cd1ThrowDec = (cd1Member.isPresentCDThrowsDeclaration()) ? Optional.of(cd1Member.getCDThrowsDeclaration()) : Optional.empty();
    Optional<ASTCDThrowsDeclaration> cd2ThrowDec = (cd2Member.isPresentCDThrowsDeclaration()) ? Optional.of(cd2Member.getCDThrowsDeclaration()) : Optional.empty();
    FieldDiff<ASTCDThrowsDeclaration> throwDecl = new FieldDiff<>(cd1ThrowDec, cd2ThrowDec);
    if (throwDecl.isPresent()){
      diffs.add(throwDecl);
    }

    // Parameter List Diff, non-optional (List is empty is no Parameter is present)
    List<List<ElementDiff<ASTCDParameter>>> list = getElementDiffList(cd1Member.getCDParameterList(), cd2Member.getCDParameterList());
    // Todo: Save Parameter Diff in an appropriate way
    return diffs;
  }

  /**
   * Help method for calculating the class diff because each classcontains multiple attributes which need to be matched
   * @param cd1Member List of attributes from the original model
   * @param cd2Member List of attributes from the target(new) model
   * @return Returns a difflist for each attribute, ordered by diffsize (small diff values == similar)
   */

  //MCType (ellipsis:["..."])? Name ("=" defaultValue:Expression)?;
  protected static List<FieldDiff<? extends ASTNode>> getParameterDiff(ASTCDParameter cd1Member, ASTCDParameter cd2Member) {
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Member.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Member.getMCType());
    FieldDiff<ASTMCType> attributeType = new FieldDiff<>(cd1Type, cd2Type);
    if (attributeType.isPresent()){
      diffs.add(attributeType);
    }
    // Name, non-optional
    if (!cd1Member.getName().equals(cd2Member.getName())){
      FieldDiff<ASTCDParameter> attributeName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, Optional.of(cd1Member), Optional.of(cd2Member));
      diffs.add(attributeName);
    }

    // Default Value, optional
    Optional<ASTExpression> cd1Default = (cd1Member.isPresentDefaultValue()) ? Optional.of(cd1Member.getDefaultValue()) : Optional.empty();
    Optional<ASTExpression> cd2Default = (cd2Member.isPresentDefaultValue()) ? Optional.of(cd2Member.getDefaultValue()) : Optional.empty();
    FieldDiff<ASTExpression> parameterDefault = new FieldDiff<>(cd1Default, cd2Default);
    if (parameterDefault.isPresent()){
      diffs.add(parameterDefault);
    }
    return diffs;
  }


  /**
   * Print function for the class diff, used to output the diffs appropriately formated
   */
  public StringBuilder print() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    StringBuilder output = new StringBuilder();
    StringBuilder interpretation = new StringBuilder();
    interpretation.append("Interpretation: ");
    String cd1Class = pp.prettyprint(this.getCd1Element());
    String cd2Class = pp.prettyprint(this.getCd2Element());




    for (FieldDiff<? extends ASTNode> diff: diffList) {
      if (diff.isPresent()) {
        String colorCode = getColorCode(diff);

        if (diff.getCd1Value().isPresent() && diff.getCd1pp().isPresent()) {
          String cd1pp = diff.getCd1pp().get();
          if (cd1Class.contains(cd1pp)) {
            cd1Class = cd1Class.replaceFirst(cd1pp, colorCode + cd1pp + RESET);
          }
        }
        if (diff.getCd2Value().isPresent() && diff.getCd2pp().isPresent()) {
          String cd2pp = diff.getCd2pp().get();
          if (cd2Class.contains(cd2pp)) {
            cd2Class = cd2Class.replaceFirst(cd2pp, colorCode + cd2pp + RESET);
          }
        }

        // Build Interpretation
        if (diff.getInterpretation().isPresent()) {
          interpretation.append(diff.getInterpretation().get()).append(" ");
        }
      }
    }
    for (ElementDiff<ASTCDAttribute> x : matchedAttributesList){
      if (!x.getDiffList().isEmpty()){
        String elementCd1 = pp.prettyprint(x.getCd1Element());
        String elementCd2 = pp.prettyprint(x.getCd2Element());

        for (FieldDiff<? extends ASTNode> diff: x.getDiffList()){
          if (diff.isPresent()) {
            String colorCode = getColorCode(diff);
            if (diff.getCd1Value().isPresent() && diff.getCd1pp().isPresent()) {
              String cd1pp = diff.getCd1pp().get();
              if (cd1Class.contains(cd1pp)) {
                cd1Class = cd1Class.replace(elementCd1, elementCd1.replace(cd1pp, colorCode + cd1pp + RESET));
              }
            }
            if (diff.getCd2Value().isPresent() && diff.getCd2pp().isPresent()) {
              String cd2pp = diff.getCd2pp().get();
              if (cd2Class.contains(cd2pp)) {
                cd2Class = cd2Class.replace(elementCd2, elementCd2.replace(cd2pp, colorCode + cd2pp + RESET));
              }
            }
            // Build Interpretation
            if (diff.getInterpretation().isPresent()) {
              interpretation.append(diff.getInterpretation().get()).append(" ");
            }
          }
        }
      }
    }

    for (ElementDiff<ASTCDMethod> x : matchedMethodeList){
      if (!x.getDiffList().isEmpty()){
        String elementCd1 = pp.prettyprint((ASTCDBasisNode) x.getCd1Element());
        String elementCd2 = pp.prettyprint((ASTCDBasisNode) x.getCd2Element());

        for (FieldDiff<? extends ASTNode> diff: x.getDiffList()){
          if (diff.isPresent()) {
            String colorCode = getColorCode(diff);
            if (diff.getCd1Value().isPresent() && diff.getCd1pp().isPresent()) {
              String cd1pp = diff.getCd1pp().get();
              if (cd1Class.contains(cd1pp)) {
                cd1Class = cd1Class.replace(elementCd1, elementCd1.replace(cd1pp, colorCode + cd1pp + RESET));
              }
            }
            if (diff.getCd2Value().isPresent() && diff.getCd2pp().isPresent()) {
              String cd2pp = diff.getCd2pp().get();
              if (cd2Class.contains(cd2pp)) {
                cd2Class = cd2Class.replace(elementCd2, elementCd2.replace(cd2pp, colorCode + cd2pp + RESET));
              }
            }
            // Build Interpretation
            if (diff.getInterpretation().isPresent()) {
              interpretation.append(diff.getInterpretation().get()).append(" ");
            }
          }
        }
      }
    }
    for (ASTCDMethod x : deleletedMethodes) {
      String cd1pp = pp.prettyprint((ASTCDBasisNode) x);
      if (cd1Class.contains(cd1pp)) {
        cd1Class = cd1Class.replace(cd1pp, COLOR_DELETE + cd1pp + RESET);
      }
    }
    for (ASTCDMethod x : addedMethode) {
      String cd2pp = pp.prettyprint((ASTCDBasisNode) x);
      if (cd2Class.contains(cd2pp)) {
        cd2Class = cd2Class.replace(cd2pp, COLOR_ADD + cd2pp + RESET);
      }
    }

    for (ASTCDAttribute x : deleletedAttributes) {
      String cd1pp = pp.prettyprint(x);
      if (cd1Class.contains(cd1pp)) {
        cd1Class = cd1Class.replace(cd1pp, COLOR_DELETE + cd1pp + RESET);
      }
    }
    for (ASTCDAttribute x : addedAttributes) {
      String cd2pp = pp.prettyprint(x);
      if (cd2Class.contains(cd2pp)) {
        cd2Class = cd2Class.replace(cd2pp, COLOR_ADD + cd2pp + RESET);
      }
    }

    /*
    Split and merge the strings together, not working yet
    Todo: Add Side-by-Side view for diff output

    String[] cd1ClassSplit = cd1Class.split("\r?\n|\r");
    String[] cd2ClassSplit = cd2Class.split("\r?\n|\r");

    String[] mergedArray = new String[cd1ClassSplit.length+cd2ClassSplit.length];

    for(int i=0; i<mergedArray.length; i++){
      if(i%2==0 && i/2 < cd1ClassSplit.length){
        mergedArray[i]=cd1ClassSplit[i/2]+"         ";
      }
      if(i%2==1 && i/2 < cd2ClassSplit.length){
        mergedArray[i]=cd2ClassSplit[i/2]+"\n";
      }
    }
    StringBuilder merged = new StringBuilder();
    for (String x : mergedArray){
      merged.append(x);
    }
    System.out.println("The split class: ");
    System.out.println(merged);
    */

    output.append("Line Matched Classes with diff score ").append(getDiffSize())
      .append(System.lineSeparator())
      .append(this.getCd1Element().get_SourcePositionStart().getLine())
      .append("   ").append(cd1Class)
      .append(this.getCd2Element().get_SourcePositionStart().getLine())
      .append("   ").append(cd2Class)
      .append(interpretation)
      .append(System.lineSeparator());

    return output;
  }
  }
