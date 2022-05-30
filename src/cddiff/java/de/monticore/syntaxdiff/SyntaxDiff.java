package de.monticore.syntaxdiff;

import de.monticore.cdassociation._ast.*;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.tr.cdassociationtr._ast.ASTCDAssociation_List;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.ast.ASTNode;

import java.util.*;


public class SyntaxDiff {

  public enum Op { CHANGE, ADD, DELETE}

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


  public static FieldDiff<Op, ASTStereotype> stereotypeDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
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




  public static List<FieldDiff<Op, ?>> assoDiff(ASTCDAssociation cd1Asso,
      ASTCDAssociation cd2Asso) {

    List<FieldDiff<Op, ?>> diffs = new ArrayList<>();

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


    FieldDiff<Op, ASTCDAssocType> assoType = getFieldDiff(Optional.of(cd1Asso.getCDAssocType()),
        Optional.of(cd2Asso.getCDAssocType()));
    if (assoType.isPresent()){
      diffs.add(assoType);
    }

    // Todo: Asso name is currently just a String, diff can only check and return String: find a usefull solution
    //FieldDiff<SyntaxDiff.Op, String, String> assoName = assocNameDiff(cd1Asso, cd2Asso);
    //if (assoName != null) { diffs.add(assoName); }

    List<FieldDiff<Op, ?>> tmpOriginalDir = new ArrayList<>(
        assocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft()));

    FieldDiff<Op, ASTCDAssocDir> assoDir1 = getFieldDiff(Optional.of(cd1Asso.getCDAssocDir()),
        Optional.of(cd2Asso.getCDAssocDir()));
    if (assoDir1.isPresent()){
      diffs.add(assoDir1);
    }

    tmpOriginalDir.addAll(assocSideDiff(cd1Asso.getRight(), cd2Asso.getRight()));


    List<FieldDiff<Op, ?>> tmpReverseDir = new ArrayList<>();
    tmpReverseDir.addAll(assocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight()));

    // Todo: Add reversed AssoDir

    tmpReverseDir.addAll(assocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft()));

    if (tmpOriginalDir.size() < tmpReverseDir.size()){
      diffs.addAll(tmpOriginalDir);
    } else {
      diffs.addAll(tmpReverseDir);
    }

    return diffs;
  }

  public static List<FieldDiff<Op, ?>> assocSideDiff(ASTCDAssocSide cd1Side, ASTCDAssocSide cd2Side) {
    List<FieldDiff<Op, ?>> diffs = new ArrayList<>();
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
      cd1AssoMatches.sort(Comparator.comparing(ElementDiff -> ElementDiff.getDiffList().size()));

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
}

