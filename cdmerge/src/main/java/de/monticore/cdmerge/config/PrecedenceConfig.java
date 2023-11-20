/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.config;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdmerge.util.AssociationDirection;
import de.monticore.cdmerge.util.CDMergeUtils;
import java.util.*;

/** Handles user-precedence overrides for the merging process */
public class PrecedenceConfig {
  private final Set<String> precedenceCDs = new HashSet<>(); // CD

  private final Set<String> precedenceTypes = new HashSet<>(); // CD.Type

  private final Set<String> precedenceAssocs = new HashSet<>(); // CD.association

  private final Set<String> precedenceFields = new HashSet<>(); // CD.Type.field

  /**
   * Adds a precedence for a model element (user-precedence override)
   *
   * @param precedence the preferred model element in the form of "CD", "CD.Type", "CD.association"
   *     or "CD.Type.field"
   */
  public void addPrecedence(String precedence) {
    String[] precedenceSplit = precedence.split("\\.");
    if (precedenceSplit.length == 1) { // precedence for a CD
      precedenceCDs.add(precedence);
    } else if (precedenceSplit.length == 2) {
      String element = precedenceSplit[1];
      if (Character.isUpperCase(element.charAt(0))) { // precedence for a
        // type
        precedenceTypes.add(precedence);
      } else { // precedence for an association
        precedenceAssocs.add(precedence);
      }
    } else if (precedenceSplit.length == 3) { // precedence for a field
      precedenceFields.add(precedence);
    }
  }

  public boolean conflictsPresent() {
    // Contradiction if CD A overrides CD B and CD B overrides CD A
    if (precedenceCDs.size() > 1) {
      return true;
    }

    // Split precedences at first "." if present
    List<String> suffixes = new ArrayList<>();

    for (String suffix : precedenceTypes) {
      suffixes.add(suffix.split("\\.", 2)[suffix.split("\\.", 2).length - 1]);
    }
    for (String suffix : precedenceFields) {
      suffixes.add(suffix.split("\\.", 2)[suffix.split("\\.", 2).length - 1]);
    }

    // Contradiction if A.Person overrides B.Person and B.Person overrides
    // A.Person
    // Contradiction if A.Person.name overrides B.Person.name and
    // B.Person.name
    // overrides A.Person.name
    for (int i = 0; i < suffixes.size() - 1; i++) {
      for (int j = i + 1; j < suffixes.size(); j++) {
        if (suffixes.get(i).equals(suffixes.get(j))) {
          return true;
        }
      }
    }

    suffixes = new ArrayList<>();
    for (String suffix : precedenceAssocs) {
      suffixes.add(suffix.split("\\.", 2)[suffix.split("\\.", 2).length - 1]);
    }
    // Contradiction if A.association overrides B.association and
    // B.association
    // overrides A.association
    for (int i = 0; i < suffixes.size() - 1; i++) {
      for (int j = i + 1; j < suffixes.size(); j++) {
        if (suffixes.get(i).equals(suffixes.get(j))) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean cdHasPrecedence(ASTCDDefinition cd) {
    return precedenceCDs.contains(cd.getName());
  }

  public boolean hasPrecedence(
      ASTCDType precedenceType, ASTCDType otherType, ASTCDDefinition cd1, ASTCDDefinition cd2) {
    return precedenceTypes.contains(cd1.getName() + "." + precedenceType.getName())
        || (precedenceCDs.contains(cd1.getName())
            && !precedenceTypes.contains(cd2.getName() + "." + otherType.getName()));
  }

  public boolean hasPrecedence(
      ASTCDAssociation precedenceAssoc,
      ASTCDAssociation otherAssoc,
      ASTCDDefinition cd1,
      ASTCDDefinition cd2) {
    if (precedenceAssoc.isPresentName()) {
      if (otherAssoc.isPresentName()) {
        return (precedenceAssocs.contains(cd1.getName() + "." + precedenceAssoc.getName())
            || (precedenceCDs.contains(cd1.getName())
                && !precedenceTypes.contains(cd2.getName() + "." + otherAssoc.getName())));
      } else {
        return precedenceAssocs.contains(cd1.getName() + "." + precedenceAssoc.getName());
      }
    } else {
      if (otherAssoc.isPresentName()) {
        return precedenceAssocs.contains(cd1.getName())
            && !precedenceAssocs.contains(cd2.getName() + "." + otherAssoc.getName());
      } else {
        // No names of either association
        return false;
      }
    }
  }

  public List<String> getPrecedenceTypesForCD(ASTCDDefinition cd) {
    List<String> typeNames = new ArrayList<>();
    for (String typeName : precedenceTypes) {
      if (typeName.startsWith(cd.getName() + ".")) {
        typeNames.add(typeName.split("\\.")[1]);
      }
    }
    for (String typeName : precedenceFields) {
      if (typeName.startsWith(cd.getName() + ".")) {
        typeNames.add(typeName.split("\\.")[1]);
      }
    }
    return typeNames;
  }

  public List<String> getPrecedenceAssociationsForCD(ASTCDDefinition cd) {
    List<String> assocNames = new ArrayList<>();
    for (String assocName : precedenceAssocs) {
      if (assocName.startsWith(cd.getName() + ".")) {
        assocNames.add(assocName.split("\\.")[1]);
      }
    }
    return assocNames;
  }

  public List<String> getPrecedenceAttributesForClass(ASTCDClass clazz, ASTCDDefinition cd) {
    List<String> attrNames = new ArrayList<>();
    for (String fieldName : precedenceFields) {
      if (fieldName.startsWith(cd.getName() + "." + clazz.getName() + ".")) {
        attrNames.add(fieldName.split("\\.")[2]);
      }
    }
    return attrNames;
  }

  public List<String> getPrecedenceConstantsForEnum(ASTCDEnum astEnum, ASTCDDefinition cd) {
    List<String> constNames = new ArrayList<>();
    for (String fieldName : precedenceFields) {
      if (fieldName.startsWith(cd.getName() + "." + astEnum.getName() + ".")) {
        constNames.add(fieldName.split("\\.")[2]);
      }
    }
    return constNames;
  }

  public boolean hasConflictWithPrecedenceType(
      ASTCDAssociation precedenceAssoc, ASTCDAssociation otherAssoc, ASTCDDefinition cd) {
    Optional<ASTCDAssociation> association2 =
        CDMergeUtils.tryAlignAssociation(precedenceAssoc, otherAssoc);

    String leftType = precedenceAssoc.getLeftReferenceName().toString();
    String rightType = precedenceAssoc.getRightReferenceName().toString();
    if (association2.isPresent()) {

      AssociationDirection directionPrecedence = AssociationDirection.getDirection(precedenceAssoc);
      AssociationDirection directionOther = AssociationDirection.getDirection(association2.get());

      // Precedence Types in CD1

      if (this.precedenceTypes.contains(cd.getName() + "." + leftType)) {
        if ((directionPrecedence == AssociationDirection.RightToLeft)
            && (directionOther == AssociationDirection.LeftToRight
                || directionOther == AssociationDirection.BiDirectional)) {
          // We would merge LEFT <- X with LEFT X-> which would modify
          // the precedence type "Left"
          return true;
        }
      }
      if (this.precedenceTypes.contains(cd.getName() + "." + rightType)) {
        if ((directionPrecedence == AssociationDirection.LeftToRight)
            && (directionOther == AssociationDirection.RightToLeft
                || directionOther == AssociationDirection.BiDirectional)) {
          // We would merge LEFT -> X with LEFT <-X which would modify
          // the precedence type "Left"
          return true;
        }
      }
    }
    return false;
  }
}
