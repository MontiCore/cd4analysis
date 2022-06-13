package de.monticore.syntaxdiff;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.ast.ASTNode;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;



public class SyntaxDiff {



  public enum Op { CHANGE, ADD, DELETE}

  // Create the diff between two provided classdiagrams
  // CDs consists of Classes, Interfaces, Enums, Associations
  // Todo: Add json file creator
  // Todo: Extract printing
  // Todo: Add parser, change signature to two file paths
  public static void createCDDiff(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2){

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);


    // Create Lists for each type of CDElement to check

    // Classes
    List<ASTCDClass> cd1ClassesList = cd1.getCDDefinition().getCDClassesList();
    List<ASTCDClass> cd2ClassesList = cd2.getCDDefinition().getCDClassesList();

    List<List<ClassDiff>> classesDiffList = SyntaxDiff.getClassDiffList(cd1ClassesList, cd2ClassesList);

    // Associations
    List<ASTCDAssociation> cd1AssociationsList = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> cd2AssociationsList = cd2.getCDDefinition().getCDAssociationsList();

    List<List<ElementDiff<ASTCDAssociation>>> assosDiffList = SyntaxDiff.getAssoDiffList(cd1AssociationsList, cd2AssociationsList);


    // Create Class Match
    int classthreshold = 10;
    List<Integer> cd1matchedLines = new ArrayList<>();
    List<Integer> cd2matchedLines = new ArrayList<>();

    List<ClassDiff> matchedClasses = new ArrayList<>();
    List<ASTCDClass> deletedClasses = new ArrayList<>();
    List<ASTCDClass> addedClasses = new ArrayList<>();

    for (List<ClassDiff> currentClassList: classesDiffList){
      if (!currentClassList.isEmpty()) {
        ClassDiff currentClassDiff = currentClassList.get(0);
        int cd1Line = currentClassDiff.getCd1Element().get_SourcePositionStart().getLine();
        int cd2Line = currentClassDiff.getCd2Element().get_SourcePositionStart().getLine();
        if (!cd1matchedLines.contains(cd1Line) && !cd2matchedLines.contains(cd2Line)){
          int currentMinDiff = currentClassDiff.getDiffSize();
          //System.out.println("Matching Lines: " + cd1Line + ", " + cd2Line + ", size "+ currentMinDiff);
          // Todo: Check if there is a match to the target line(in cd2) with a smaller diff size
          if (currentMinDiff < classthreshold){
            matchedClasses.add(currentClassDiff);
            cd1matchedLines.add(cd1Line);
            cd2matchedLines.add(cd2Line);
          }
        }
      }

    }
    for (ASTCDClass cd1class : cd1ClassesList){
      if (!cd1matchedLines.contains(cd1class.get_SourcePositionStart().getLine())){
        deletedClasses.add(cd1class);
      }
    }
    for (ASTCDClass cd2class : cd2ClassesList){
      if (!cd2matchedLines.contains(cd2class.get_SourcePositionStart().getLine())){
        addedClasses.add(cd2class);
      }
    }

    for (ClassDiff x : matchedClasses) {
      StringBuilder output = new StringBuilder();
      // Source line of asso which are compared
      output.append("Matched class ")
        .append(x.getCd1Element().get_SourcePositionStart().getLine())
        .append(" and ")
        .append(x.getCd2Element().get_SourcePositionStart().getLine())
        .append(System.lineSeparator())
        .append("Diff size: ")
        .append(x.getDiffSize())
        .append(System.lineSeparator())
        .append(pp.prettyprint(x.getCd1Element()))
        .append(pp.prettyprint(x.getCd2Element()))
        .append(System.lineSeparator());

      for (FieldDiff<SyntaxDiff.Op, ?> diff : x.getDiffList()) {
        if (diff.isPresent()) {
          diff.getOperation().ifPresent(operation -> output.append(operation).append(": "));
          diff.getCd1Value().ifPresent(cd1v -> output.append(cd1v).append(" -> "));
          diff.getCd2Value().ifPresent(cd2v -> output.append(cd2v));
          output.append(System.lineSeparator());
        }
      }

      for (ElementDiff<ASTCDAttribute> matched : x.getMatchedAttributesList()) {
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
      for (ASTCDAttribute a : x.getDeleletedAttributes()) {
        output.append(a.get_SourcePositionStart().getLine())
          .append(" ")
          .append(pp.prettyprint(a));
      }
      output.append("Added Attributes: ").append(System.lineSeparator());
      for (ASTCDAttribute a : x.getAddedAttributes()) {
        output.append(a.get_SourcePositionStart().getLine())
          .append(" ")
          .append(pp.prettyprint(a));
      }
      System.out.println(output);
    }
    StringBuilder output = new StringBuilder();
    output.append("Deleted Classes: ").append(System.lineSeparator());
    for (ASTCDClass a : deletedClasses) {
      output.append(a.get_SourcePositionStart().getLine())
        .append(" ")
        .append(pp.prettyprint(a));
    }
    output.append("Added Classes: ").append(System.lineSeparator());
    for (ASTCDClass a : addedClasses) {
      output.append(a.get_SourcePositionStart().getLine())
        .append(" ")
        .append(pp.prettyprint(a));
    }
    System.out.println(output);


    // Create Association Match
    int assothreshold = 3;
    List<Integer> cd1AssomatchedLines = new ArrayList<>();
    List<Integer> cd2AssomatchedLines = new ArrayList<>();

    List<ElementDiff<ASTCDAssociation>> matchedAssos = new ArrayList<>();
    List<ASTCDAssociation> deletedAssos = new ArrayList<>();
    List<ASTCDAssociation> insertedAssos = new ArrayList<>();

    for (List<ElementDiff<ASTCDAssociation>> currentAssoList: assosDiffList){
      if (!currentAssoList.isEmpty()) {
        ElementDiff<ASTCDAssociation> currentAssoDiff = currentAssoList.get(0);
        int cd1Line = currentAssoList.get(0).getCd1Element().get_SourcePositionStart().getLine();
        int cd2Line = currentAssoList.get(0).getCd2Element().get_SourcePositionStart().getLine();
        if (!cd1AssomatchedLines.contains(cd1Line) && !cd2AssomatchedLines.contains(cd2Line)){
          int currentMinDiff = currentAssoDiff.getDiffSize();
          //System.out.println("Matching Lines: " + cd1Line + ", " + cd2Line + ", size "+ currentMinDiff);
          // Todo: Check if there is a match to the target line(in cd2) with a smaller diff size
          if (currentMinDiff < assothreshold){
            matchedAssos.add(currentAssoDiff);
            cd1AssomatchedLines.add(cd1Line);
            cd2AssomatchedLines.add(cd2Line);
          }
        }
      }

    }
    for (ASTCDAssociation cd1assos : cd1AssociationsList){
      if (!cd1AssomatchedLines.contains(cd1assos.get_SourcePositionStart().getLine())){
        deletedAssos.add(cd1assos);
      }
    }
    for (ASTCDAssociation cd2assos : cd2AssociationsList){
      if (!cd2AssomatchedLines.contains(cd2assos.get_SourcePositionStart().getLine())){
        insertedAssos.add(cd2assos);
      }
    }
    System.out.println("Matched Assos: CD1: "+cd1AssomatchedLines+" CD2: "+ cd1AssomatchedLines+ " Elements: "+ matchedAssos.size() );
    System.out.println("Deleted Elements: "+ deletedAssos.size());
    System.out.println("Added Elements: " + insertedAssos.size());


    // Create Interface Match

    // Create Enum Match
  }

  // Create a FieldDiff Object between the two given Fields
  public static <NodeType extends ASTNode> FieldDiff<Op, NodeType> getFieldDiff(Optional<NodeType> cd1Field,
      Optional<NodeType> cd2Field) {
    if (cd1Field.isPresent() && cd2Field.isPresent() && !cd1Field.get().deepEquals(cd2Field.get())) {
      // Diff reason: Value changed
      return new FieldDiff<>(Op.CHANGE, cd1Field.get(), cd2Field.get());

    } else if (cd1Field.isPresent() && !cd2Field.isPresent()) {
      // Diff reason: Value deleted
      return new FieldDiff<>(Op.DELETE, cd1Field.get(), null);

    } else if (!cd1Field.isPresent() && cd2Field.isPresent()) {
      // Diff reason: Value added
      return new FieldDiff<>(Op.ADD, null, cd2Field.get());

    } else {
      // No Diff reason: is equal
      return new FieldDiff<>();
    }
  }



  private static FieldDiff<Op, ASTStereotype> stereotypeDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
    if (cd1Asso.getModifier().isPresentStereotype() && cd2Asso.getModifier().isPresentStereotype()) {
      // Different amount of stereotype
      if (cd1Asso.getModifier().getStereotype().getValuesList().size() != cd2Asso.getModifier().getStereotype().getValuesList().size()) {
        // Stereotype diff reason: unequal amount of values
        return new FieldDiff<>(Op.CHANGE, cd1Asso.getModifier().getStereotype(), cd2Asso.getModifier().getStereotype());
      } else {
        for (ASTStereoValue cd1Stereotype : cd1Asso.getModifier().getStereotype().getValuesList()) {
          boolean foundStereotype = false;
          for (ASTStereoValue cd2Stereotype : cd2Asso.getModifier().getStereotype().getValuesList()) {
            //System.out.println("Check if " + cd1Stereotype.getName() +" and " + cd2Stereotype.getName() +" are equal");
            if (cd1Stereotype.getName().equals(cd2Stereotype.getName())) {
              foundStereotype = true;
              break;
            }
          }
          if (!foundStereotype) {
            // Stereotype diff reason: cd1Stereotype.getName() was not found in cd2Asso
            return new FieldDiff<>(Op.CHANGE, cd1Asso.getModifier().getStereotype(), cd2Asso.getModifier().getStereotype());
          }
        }
      }
    } else {
      // One of the CDs has no stereotype -> add stereotype diff
      if (cd1Asso.getModifier().isPresentStereotype()) {
        // Stereotype diff reason: cd1Asso has stereotype, but cd2Asso's is empty
        return new FieldDiff<>(Op.DELETE, cd1Asso.getModifier().getStereotype(), null);

      } else if (cd2Asso.getModifier().isPresentStereotype()) {
        // Stereotype diff reason: cd2Asso has stereotype, but cd1Asso's is empty
        return new FieldDiff<>(Op.ADD, null, cd2Asso.getModifier().getStereotype());
      }
    }
    // No diff, both empty or equal
    return null;
  }




  private static List<FieldDiff<Op, ? extends ASTNode>> assoDiff(ASTCDAssociation cd1Asso,
      ASTCDAssociation cd2Asso) {

    List<FieldDiff<Op, ? extends ASTNode>> diffs = new ArrayList<>();

    // Todo: Rework Stereotype diff
    //FieldDiff<SyntaxDiff.Op, ASTStereotype, ASTStereotype> assoStereotype = SyntaxDiff.stereotypeDiff(cd1Asso, cd2Asso);
    //if (assoStereotype != null) { diffs.add(assoStereotype); }

    // Modifier
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Asso.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Asso.getModifier());
    FieldDiff<Op, ASTModifier> assoModifier = getFieldDiff(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }

    // Asso Type Diff (composition or association)
    FieldDiff<Op, ASTCDAssocType> assoType = getFieldDiff(Optional.of(cd1Asso.getCDAssocType()),
        Optional.of(cd2Asso.getCDAssocType()));
    if (assoType.isPresent()){
      diffs.add(assoType);
    }

    // Todo: Assoc Name Diff, currently ignored



    // for each association the sides can be exchanged (Direction must be changed appropriately)
    // Original direction (is also prioritised for equal results)
    List<FieldDiff<Op, ? extends ASTNode>> tmpOriginalDir = new ArrayList<>(
      getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft()));

    FieldDiff<Op, ASTCDAssocDir> assoDir1 = getFieldDiff(Optional.of(cd1Asso.getCDAssocDir()),
        Optional.of(cd2Asso.getCDAssocDir()));
    if (assoDir1.isPresent()){
      diffs.add(assoDir1);
    }

    tmpOriginalDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getRight()));

    // Reversed direction (exchange the input and use the reveres direction, only for directed)
    List<FieldDiff<Op, ? extends ASTNode>> tmpReverseDir = new ArrayList<>();
    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight()));

    // Todo: Add reversed AssoDir

    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft()));

    if (tmpOriginalDir.size() < tmpReverseDir.size()){
      diffs.addAll(tmpOriginalDir);
    } else {
      diffs.addAll(tmpReverseDir);
    }

    return diffs;
  }

  private static List<FieldDiff<Op, ? extends ASTNode>> getAssocSideDiff(ASTCDAssocSide cd1Side, ASTCDAssocSide cd2Side) {
    List<FieldDiff<Op, ? extends ASTNode>> diffs = new ArrayList<>();
    // Ordered
    Optional<ASTCDOrdered> cd1Ordered = (cd1Side.isPresentCDOrdered()) ? Optional.of(cd1Side.getCDOrdered()) : Optional.empty();
    Optional<ASTCDOrdered> cd2Ordered = (cd2Side.isPresentCDOrdered()) ? Optional.of(cd2Side.getCDOrdered()) : Optional.empty();
    FieldDiff<Op, ASTCDOrdered> assoOrdered = getFieldDiff(cd1Ordered, cd2Ordered);
    if (assoOrdered.isPresent()){
      diffs.add(assoOrdered);
    }

    // Association side modifier
    FieldDiff<Op, ASTModifier> modifier = getFieldDiff(Optional.of(cd1Side.getModifier()),Optional.of(cd2Side.getModifier()));

    if (modifier.isPresent()){
      diffs.add(modifier);
    }

    // Cardinality
    Optional<ASTCDCardinality> cd1Card = (cd1Side.isPresentCDCardinality()) ? Optional.of(cd1Side.getCDCardinality()) : Optional.empty();
    Optional<ASTCDCardinality> cd2Card = (cd2Side.isPresentCDCardinality()) ? Optional.of(cd2Side.getCDCardinality()) : Optional.empty();
    FieldDiff<Op, ASTCDCardinality> assoCard = getFieldDiff(cd1Card, cd2Card);
    if (assoCard.isPresent()){
      diffs.add(assoCard);
    }

    // QualifiedType is the participant in the association
    FieldDiff<Op, ASTMCQualifiedName> type = getFieldDiff(
      Optional.of(cd1Side.getMCQualifiedType().getMCQualifiedName()),
      Optional.of(cd2Side.getMCQualifiedType().getMCQualifiedName()));

    if (type.isPresent()){
      diffs.add(type);
    }

    // CDQualifier
    Optional<ASTCDQualifier> cd1Quali = (cd1Side.isPresentCDQualifier()) ? Optional.of(cd1Side.getCDQualifier()) : Optional.empty();
    Optional<ASTCDQualifier> cd2Quali = (cd2Side.isPresentCDQualifier()) ? Optional.of(cd2Side.getCDQualifier()) : Optional.empty();
    FieldDiff<Op, ASTCDQualifier> assoQuali = getFieldDiff(cd1Quali, cd2Quali);
    if (assoQuali.isPresent()){
      diffs.add(assoQuali);
    }

    // CDRole
    Optional<ASTCDRole> cd1Role = (cd1Side.isPresentCDRole()) ? Optional.of(cd1Side.getCDRole()) : Optional.empty();
    Optional<ASTCDRole> cd2Role = (cd2Side.isPresentCDRole()) ? Optional.of(cd2Side.getCDRole()) : Optional.empty();
    FieldDiff<Op, ASTCDRole> assoRole = getFieldDiff(cd1Role, cd2Role);
    if (assoRole.isPresent()){
      diffs.add(assoRole);
    }

    return diffs;
  }



  // Returns a reduced list of diffs between all associatons from CD1 and all associations from CD2, reduced by unmatchable entries
  public static List<List<ElementDiff<ASTCDAssociation>>> getAssoDiffList(List<ASTCDAssociation> cd1AssoList, List<ASTCDAssociation> cd2AssoList) {
    List<List<ElementDiff<ASTCDAssociation>>> assoMatches = new ArrayList<>();

    for (ASTCDAssociation cd1Asso : cd1AssoList) {

      List<ElementDiff<ASTCDAssociation>> cd1AssoMatches = new ArrayList<>();
      for (ASTCDAssociation cd2Asso : cd2AssoList) {
        // Diff list for the compared assos
        cd1AssoMatches.add(new ElementDiff<>(cd1Asso, cd2Asso, assoDiff(cd1Asso, cd2Asso)));
      }
      // Sort by size of diffs, ascending
      cd1AssoMatches.sort(Comparator.comparing(ElementDiff::getDiffSize));

      // Average value of diffs for one association from CD1 compared to all associations in CD2
      OptionalDouble optAverage = cd1AssoMatches.stream()
        .mapToDouble(a -> a.getDiffList().size())
        .average();

      // Threshold for which a match is still likely
      // Todo: add more advanced threshold calculation
      double threshold = 0.4;

      // List is sorted by size of diffs (first entry is minimum, 0 if empty -> perfect match)
      int minDiff = cd1AssoMatches.get(0).getDiffList().size();
      // min/max threshold values which are possible
      double minToThreshold = (optAverage.isPresent()) ? minDiff / optAverage.getAsDouble() : 0;
      double maxToThreshold = (optAverage.isPresent()) ? threshold*optAverage.getAsDouble() : 0;

      // Add only entries below the given threshold, use a tmp list and iterate over the original list
      List<ElementDiff<ASTCDAssociation>> tmp = new ArrayList<>();
      for (ElementDiff<ASTCDAssociation> x : cd1AssoMatches){
        if (!(maxToThreshold > 0 && x.getDiffList().size()*2*threshold > maxToThreshold)){
          tmp.add(x);
        }
      }
      assoMatches.add(tmp);
    }
    return assoMatches;
  }

  // Class Diff + Interface
  // Modifier "class"     Name CDExtendUsage? CDInterfaceUsage? ( "{" CDMember* "}" | ";" );
  // Interface
  // Modifier "interface" Name CDExtendUsage?                   ( "{" CDMember* "}" | ";" );

  private static ClassDiff classDiff(ASTCDClass cd1Class, ASTCDClass cd2Class) {
    List<FieldDiff<Op, ? extends ASTNode>> diffs = new ArrayList<>();

    // Todo: Add separate Stereotype diff (currently included in Modifier)
    // Modifier
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Class.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Class.getModifier());
    FieldDiff<Op, ASTModifier> assoModifier = getFieldDiff(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }

    // Class Name Diff, is not optional (always a String therefore return the full class)
    if (!cd1Class.getName().equals(cd2Class.getName())){
      FieldDiff<SyntaxDiff.Op, ASTCDClass> className = new FieldDiff<>(Op.CHANGE, cd1Class, cd2Class);
      diffs.add(className);
    }

    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Class.isPresentCDExtendUsage()) ? Optional.of(cd1Class.getCDExtendUsage()) : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Class.isPresentCDExtendUsage()) ? Optional.of(cd2Class.getCDExtendUsage()) : Optional.empty();
    FieldDiff<Op, ASTCDExtendUsage> classExtended = getFieldDiff(cd1Extend, cd2Extend);
    if (classExtended.isPresent()){
      diffs.add(classExtended);
    }


    // Todo: inheritance helper? because I have to consider all the inherited methods and attributes

    // CDMember diffs, members are: Attributes, Methods, Constructors
    List<List<ElementDiff<ASTCDAttribute>>> attributeDiffList = getAttributeDiffList(cd1Class.getCDAttributeList(), cd2Class.getCDAttributeList());
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    List<Integer> cd1attributeLines = new ArrayList<>();
    for (ASTCDAttribute cd1attribute : cd1Class.getCDAttributeList()){
      cd1attributeLines.add(cd1attribute.get_SourcePositionStart().getLine());
    }
    //System.out.println(cd1attributeLines);
    List<Integer> cd2attributeLines = new ArrayList<>();
    for (ASTCDAttribute cd2attribute : cd2Class.getCDAttributeList()){
      cd2attributeLines.add(cd2attribute.get_SourcePositionStart().getLine());
    }
    //System.out.println(cd2attributeLines);

    int threshold = 2;
    List<Integer> cd1matchedLines = new ArrayList<>();
    List<Integer> cd2matchedLines = new ArrayList<>();

    List<ElementDiff<ASTCDAttribute>> matchedAttributes = new ArrayList<>();
    List<ASTCDAttribute> deletedAttributes = new ArrayList<>();
    List<ASTCDAttribute> insertedAttributes = new ArrayList<>();

    for (List<ElementDiff<ASTCDAttribute>> currentAttributeList: attributeDiffList){
      if (!currentAttributeList.isEmpty()) {
        ElementDiff<ASTCDAttribute> currentAttriDiff = currentAttributeList.get(0);
        int cd1Line = currentAttriDiff.getCd1Element().get_SourcePositionStart().getLine();
        int cd2Line = currentAttriDiff.getCd2Element().get_SourcePositionStart().getLine();
        if (!cd1matchedLines.contains(cd1Line) && !cd2matchedLines.contains(cd2Line)){
          int currentMinDiff = currentAttriDiff.getDiffSize();
          //System.out.println("Matching Lines: " + cd1Line + ", " + cd2Line + ", size "+ currentMinDiff);
          // Todo: Check if there is a match to the target line(in cd2) with a smaller diff size
          if (currentMinDiff < threshold){
            matchedAttributes.add(currentAttriDiff);
            cd1matchedLines.add(cd1Line);
            cd2matchedLines.add(cd2Line);
          }
        }
      }

    }

    for (ASTCDAttribute attr : cd1Class.getCDAttributeList()){
      if (!cd1matchedLines.contains(attr.get_SourcePositionStart().getLine())){
        deletedAttributes.add(attr);
      }
    }
    for (ASTCDAttribute attr : cd2Class.getCDAttributeList()){
      if (!cd2matchedLines.contains(attr.get_SourcePositionStart().getLine())){
        insertedAttributes.add(attr);
      }
    }
    return new ClassDiff(cd1Class, cd2Class, diffs, matchedAttributes, deletedAttributes, insertedAttributes);


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

  //Attribute Diff List
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
  //Attribute: Modifier MCType Name ("=" initial:Expression)? ";";
  private static List<FieldDiff<Op, ? extends ASTNode>> getAttributeDiff(ASTCDAttribute cd1Member, ASTCDAttribute cd2Member) {
    List<FieldDiff<Op,  ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Member.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Member.getModifier());
    FieldDiff<Op, ASTModifier> attributeModifier = getFieldDiff(cd1Modi, cd2Modi);
    if (attributeModifier.isPresent()){
      diffs.add(attributeModifier);
    }

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Member.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Member.getMCType());
    FieldDiff<Op, ASTMCType> attributeType = getFieldDiff(cd1Type, cd2Type);
    if (attributeType.isPresent()){
      diffs.add(attributeType);
    }
    // Name, non-optional
    if (!cd1Member.getName().equals(cd2Member.getName())){
      FieldDiff<Op, ASTCDAttribute> attributeName = new FieldDiff<>(Op.CHANGE, cd1Member, cd2Member);
      diffs.add(attributeName);
    }

    // Initial expression, optional
    Optional<ASTExpression> cd1Initial = (cd1Member.isPresentInitial()) ? Optional.of(cd1Member.getInitial()) : Optional.empty();
    Optional<ASTExpression> cd2Initial = (cd2Member.isPresentInitial()) ? Optional.of(cd2Member.getInitial()) : Optional.empty();
    FieldDiff<Op, ASTExpression> attributeInital = getFieldDiff(cd1Initial, cd2Initial);
    if (attributeInital.isPresent()){
      diffs.add(attributeInital);
    }
    return diffs;
  }

  // CDParameter implements Field =
  //    MCType (ellipsis:["..."])? Name ("=" defaultValue:Expression)?;
  //cd1Class.getCDMethodList().get(0).getCDParameterList().get(0).getMCType()
  //cd1Class.getCDMethodList().get(0).getCDParameterList().get(0).getName();
  //cd1Class.getCDMethodList().get(0).getCDParameterList().get(0).getDefaultValue()

  private static List<FieldDiff<Op, ? extends ASTNode>> getParameterDiff(List<ASTCDParameter> cd1ParaList, List<ASTCDParameter> cd2ParaList) {
    List<FieldDiff<Op, ? extends ASTNode>> diffs = new ArrayList<>();

    return diffs;
  }


  // Interface
  // scope CDInterface implements CDType =
  //    Modifier "interface" Name
  //    CDExtendUsage?
  //    ( "{"
  //        CDMember*
  //      "}"
  //    | ";" );

  // Enum
  // scope CDEnum implements CDType =
  //    Modifier "enum" Name
  //    CDInterfaceUsage?
  //    ( "{"
  //        (CDEnumConstant || ",")* ";"
  //        CDMember*
  //      "}"
  //    | ";" );

  public static List<List<ClassDiff>> getClassDiffList(List<ASTCDClass> cd1AssoList, List<ASTCDClass> cd2AssoList) {
    List<List<ClassDiff>> classMatches = new ArrayList<>();

    for (ASTCDClass cd1Class : cd1AssoList) {

      List<ClassDiff> cd1ClassMatches = new ArrayList<>();
      for (ASTCDClass cd2Class : cd2AssoList) {
        // Diff list for the compared assos
        cd1ClassMatches.add(classDiff(cd1Class, cd2Class));
      }
      // Sort by size of diffs, ascending
      cd1ClassMatches.sort(Comparator.comparing(ClassDiff::getDiffSize));

      classMatches.add(cd1ClassMatches);
    }
    return classMatches;
  }
}

