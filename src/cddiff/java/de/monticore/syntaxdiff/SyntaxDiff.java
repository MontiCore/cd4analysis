package de.monticore.syntaxdiff;

import de.monticore.cdassociation._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;


public class SyntaxDiff {

  public enum Op { CHANGE, ADD, DELETE}

  // AssocName
  public static FieldDiff<Op, String, String> assocNameDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
    if (cd1Asso.isPresentName() && cd2Asso.isPresentName() && !cd1Asso.getName().equals(cd2Asso.getName())) {
      // AssocName diff reason: name changed
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getName(), cd2Asso.getName());

    } else if (cd1Asso.isPresentName() && !cd2Asso.isPresentName()) {
      // AssocName diff reason: name deleted
      return new FieldDiff<>(Op.DELETE, cd1Asso.getName(), null);

    } else if (!cd1Asso.isPresentName() && cd2Asso.isPresentName()) {
      // AssocName diff reason: name added
      return new FieldDiff<>(Op.ADD, null, cd2Asso.getName());

    } else {
      // AssocName is equal -> no name diff
      return null;
    }
  }

  // AssocType
  public static FieldDiff<Op, ASTCDAssocType, ASTCDAssocType> associationTypeDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
    if (!cd1Asso.getCDAssocType().deepEquals(cd2Asso.getCDAssocType())) {
      // AssocType diff reason: (type changed), AssocType must always be present
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getCDAssocType(), cd2Asso.getCDAssocType());
    } else {
      // AssocType is equal
      return null;
    }
  }

  // Ordered
  public static FieldDiff<Op, ASTCDOrdered, ASTCDOrdered> orderedDiff(ASTCDAssocSide cd1Asso, ASTCDAssocSide cd2Asso) {
    if (cd1Asso.isPresentCDOrdered() && !cd2Asso.isPresentCDOrdered()) {
      // CDCardinality diff reason: cardinality deleted
      return new FieldDiff<>(Op.DELETE, cd1Asso.getCDOrdered(), null);

    } else if (!cd1Asso.isPresentCDOrdered() && cd2Asso.isPresentCDOrdered()) {
      // CDCardinality diff reason: cardinality added
      return new FieldDiff<>(Op.ADD, null, cd2Asso.getCDOrdered());

    } else {
      // CDCardinality is equal -> no cardinality diff
      return null;
    }
  }

  // CDCardinality
  public static FieldDiff<Op, ASTCDCardinality, ASTCDCardinality> cardinalityDiff(ASTCDAssocSide cd1Asso, ASTCDAssocSide cd2Asso) {
    if (cd1Asso.isPresentCDCardinality() && cd2Asso.isPresentCDCardinality()
      && !cd1Asso.getCDCardinality().deepEquals(cd2Asso.getCDCardinality())) {
      // CDCardinality diff reason: cardinality changed
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getCDCardinality(), cd2Asso.getCDCardinality());

    } else if (cd1Asso.isPresentCDCardinality() && !cd2Asso.isPresentCDCardinality()) {
      // CDCardinality diff reason: cardinality deleted
      return new FieldDiff<>(Op.DELETE, cd1Asso.getCDCardinality(), null);

    } else if (!cd1Asso.isPresentCDCardinality() && cd2Asso.isPresentCDCardinality()) {
      // CDCardinality diff reason: cardinality added
      return new FieldDiff<>(Op.ADD, null, cd2Asso.getCDCardinality());

    } else {
      // CDCardinality is equal -> no cardinality diff
      return null;
    }
  }

  // CDQualifier
  public static FieldDiff<Op, ASTCDQualifier, ASTCDQualifier> cdQualifierDiff(ASTCDAssocSide cd1Asso, ASTCDAssocSide cd2Asso) {
    if (cd1Asso.isPresentCDQualifier() && cd2Asso.isPresentCDQualifier()
      && !cd1Asso.getCDQualifier().deepEquals(cd2Asso.getCDQualifier())) {
      // CDQualifier diff reason: attribute changed
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getCDQualifier(), cd2Asso.getCDQualifier());

    } else if (cd1Asso.isPresentCDQualifier() && !cd2Asso.isPresentCDQualifier()) {
      // CDQualifier diff reason: attribute deleted
      return new FieldDiff<>(Op.DELETE, cd1Asso.getCDQualifier(), null);

    } else if (!cd1Asso.isPresentCDQualifier() && cd2Asso.isPresentCDQualifier()) {
      // CDQualifier diff reason: attribute added
      return new FieldDiff<>(Op.ADD, null, cd2Asso.getCDQualifier());

    } else {
      // CDQualifier is equal -> no attribute diff
      return null;
    }
  }

  // CDRole
  public static FieldDiff<Op, ASTCDRole, ASTCDRole> cdRoleDiff(ASTCDAssocSide cd1Asso, ASTCDAssocSide cd2Asso) {
    if (cd1Asso.isPresentCDRole() && cd2Asso.isPresentCDRole()
      && !cd1Asso.getCDRole().deepEquals(cd2Asso.getCDRole())) {
      // CDRole diff reason: name changed
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getCDRole(), cd2Asso.getCDRole());

    } else if (cd1Asso.isPresentCDRole() && !cd2Asso.isPresentCDRole()) {
      // CDRole diff reason: name deleted
      return new FieldDiff<>(Op.DELETE, cd1Asso.getCDRole(), null);

    } else if (!cd1Asso.isPresentCDRole() && cd2Asso.isPresentCDRole()) {
      // CDRole diff reason: name added
      return new FieldDiff<>(Op.ADD, null, cd2Asso.getCDRole());

    } else {
      // CDRole is equal -> no name diff
      return null;
    }
  }

  // QualifiedName
  public static FieldDiff<Op, ASTMCQualifiedName, ASTMCQualifiedName> qualifiedNameDiff(ASTCDAssocSide cd1Asso, ASTCDAssocSide cd2Asso) {
    if (!cd1Asso.getMCQualifiedType().getMCQualifiedName().deepEquals(cd2Asso.getMCQualifiedType().getMCQualifiedName())) {
      // QName diff reason: (name changed), QName must always be present
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getMCQualifiedType().getMCQualifiedName(), cd2Asso.getMCQualifiedType().getMCQualifiedName());
    } else {
      // Name is equal
      return null;
    }
  }


  public static FieldDiff<Op, ASTStereotype, ASTStereotype> stereotypeDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
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

  // Modifier
  public static FieldDiff<Op, ASTModifier, ASTModifier> modifierDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
    if (!cd1Asso.getModifier().deepEquals(cd2Asso.getModifier())) {
      // Diff: Modifier changed
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getModifier(), cd2Asso.getModifier());
    } else {
      // Modifiers are equal
      return null;
    }
  }

  // AssocDirection
  public static FieldDiff<Op, ASTCDAssocDir, ASTCDAssocDir> assocDirDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
    if (!cd1Asso.getCDAssocDir().deepEquals(cd2Asso.getCDAssocDir())) {
      // Diff: Directions changed
      return new FieldDiff<>(Op.CHANGE, cd1Asso.getCDAssocDir(), cd2Asso.getCDAssocDir());
    } else {
      // Directions are equal
      return null;
    }
  }
}
