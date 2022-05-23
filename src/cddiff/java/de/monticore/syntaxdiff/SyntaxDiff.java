package de.monticore.syntaxdiff;

import de.monticore.cdassociation._ast.*;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.ast.ASTNode;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;


public class SyntaxDiff {

  public enum Op { CHANGE, ADD, DELETE}

  // AssocName
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




  public static List<FieldDiff<Op, ASTNode>> assoDiff(ASTCDAssociation cd1Asso,
      ASTCDAssociation cd2Asso) {

    List<FieldDiff<Op, ASTNode>> diffs = new ArrayList<>();

    // Todo: Rework Stereotype diff
    //FieldDiff<SyntaxDiff.Op, ASTStereotype, ASTStereotype> assoStereotype = SyntaxDiff.stereotypeDiff(cd1Asso, cd2Asso);
    //if (assoStereotype != null) { diffs.add(assoStereotype); }

    // Modifier
    Optional<ASTNode> cd1Modi = Optional.of(cd1Asso.getModifier());
    Optional<ASTNode> cd2Modi = Optional.of(cd2Asso.getModifier());
    FieldDiff<Op, ASTNode> assoModifier = getFieldDiff(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }


    FieldDiff<Op, ASTNode> assoType = getFieldDiff(Optional.of(cd1Asso.getCDAssocType()),
        Optional.of(cd2Asso.getCDAssocType()));
    if (assoType.isPresent()){
      diffs.add(assoType);
    }

    // Todo: Asso name is currently just a String, diff can only check and return String: find a usefull solution
    //FieldDiff<SyntaxDiff.Op, String, String> assoName = assocNameDiff(cd1Asso, cd2Asso);
    //if (assoName != null) { diffs.add(assoName); }

    List<FieldDiff<Op, ASTNode>> tmpOriginalDir = new ArrayList<>(
        assocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft()));

    FieldDiff<Op, ASTNode> assoDir1 = getFieldDiff(Optional.of(cd1Asso.getCDAssocDir()),
        Optional.of(cd2Asso.getCDAssocDir()));
    if (assoDir1.isPresent()){
      diffs.add(assoDir1);
    }

    tmpOriginalDir.addAll(assocSideDiff(cd1Asso.getRight(), cd2Asso.getRight()));


    List<FieldDiff<Op, ASTNode>> tmpReverseDir = new ArrayList<>();
    tmpReverseDir.addAll(assocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight()));

    // Todo: Add reversed AssoDir
    //FieldDiff<SyntaxDiff.Op, ASTCDAssocDir, ASTCDAssocDir> assoDir2 = SyntaxDiff.assocDirDiff(cd1Asso, cd2Asso);
    //if (assoDir2 != null) { tmpAssoDirection2.add(assoDir2); }

    tmpReverseDir.addAll(assocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft()));

    if (tmpOriginalDir.size() < tmpReverseDir.size()){
      diffs.addAll(tmpOriginalDir);
    } else {
      diffs.addAll(tmpReverseDir);
    }

    return diffs;
  }

  public static List<FieldDiff<Op, ASTNode >> assocSideDiff(ASTCDAssocSide cd1Side,
      ASTCDAssocSide cd2Side) {
    List<FieldDiff<Op, ASTNode>> diffs = new ArrayList<>();
    // Ordered
    Optional<ASTNode> cd1Ordered = (cd1Side.isPresentCDOrdered()) ? Optional.of(cd1Side.getCDOrdered()) : Optional.empty();
    Optional<ASTNode> cd2Ordered = (cd2Side.isPresentCDOrdered()) ? Optional.of(cd2Side.getCDOrdered()) : Optional.empty();
    FieldDiff<Op, ASTNode> assoOrdered = getFieldDiff(cd1Ordered, cd2Ordered);
    if (assoOrdered.isPresent()){
      diffs.add(assoOrdered);
    }

    // Cardinality
    Optional<ASTNode> cd1Card = (cd1Side.isPresentCDCardinality()) ? Optional.of(cd1Side.getCDCardinality()) : Optional.empty();
    Optional<ASTNode> cd2Card = (cd2Side.isPresentCDCardinality()) ? Optional.of(cd2Side.getCDCardinality()) : Optional.empty();
    FieldDiff<Op, ASTNode> assoCard = getFieldDiff(cd1Card, cd2Card);
    if (assoCard.isPresent()){
      diffs.add(assoCard);
    }

    // CDRole
    Optional<ASTNode> cd1Role = (cd1Side.isPresentCDRole()) ? Optional.of(cd1Side.getCDRole()) : Optional.empty();
    Optional<ASTNode> cd2Role = (cd2Side.isPresentCDRole()) ? Optional.of(cd2Side.getCDRole()) : Optional.empty();
    FieldDiff<Op, ASTNode> assoRole = getFieldDiff(cd1Role, cd2Role);
    if (assoRole.isPresent()){
      diffs.add(assoRole);
    }

    // CDQualifier
    Optional<ASTNode> cd1Quali = (cd1Side.isPresentCDQualifier()) ? Optional.of(cd1Side.getCDQualifier()) : Optional.empty();
    Optional<ASTNode> cd2Quali = (cd2Side.isPresentCDQualifier()) ? Optional.of(cd2Side.getCDQualifier()) : Optional.empty();
    FieldDiff<Op, ASTNode> assoQuali = getFieldDiff(cd1Quali, cd2Quali);
    if (assoQuali.isPresent()){
      diffs.add(assoQuali);
    }

    // QualifiedType is the participant in the association
    FieldDiff<Op, ASTNode> type = getFieldDiff(
                          Optional.of(cd1Side.getMCQualifiedType().getMCQualifiedName()),
        Optional.of(cd2Side.getMCQualifiedType().getMCQualifiedName()));

    if (type.isPresent()){
      diffs.add(type);
    }


    // Todo: Add Modifier check
    //modifier = SyntaxDiff.modifierDiff(cd1Side, cd1Side);

    return diffs;
  }

}
