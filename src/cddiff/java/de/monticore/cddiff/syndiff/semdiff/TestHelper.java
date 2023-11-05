package de.monticore.cddiff.syndiff.semdiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.cdsyntax2semdiff.odgen.Syn2SemDiffHelper;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.AssocDiffStruct;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.ClassSide;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.InheritanceDiff;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.TypeDiffStruct;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TestHelper {

  private final CDSyntaxDiff syntaxDiff;

  private Syn2SemDiffHelper helper;

  public TestHelper(CDSyntaxDiff syntaxDiff, Syn2SemDiffHelper helper) {
    this.syntaxDiff = syntaxDiff;
    this.helper = helper;
  }

  public void addedAssocs() {
    for (Pair<ASTCDAssociation, List<ASTCDType>> association : syntaxDiff.addedAssocList()) {
      Pair<ASTCDType, ASTCDType> classes =
          Syn2SemDiffHelper.getConnectedTypes(association.a, syntaxDiff.getSrcCD());
      System.out.println(
          "The association between the classes "
              + classes.a.getSymbol().getInternalQualifiedName()
              + " and "
              + classes.b.getSymbol().getInternalQualifiedName()
              + " has been added to the diagram.");
      System.out.println("=======================================================");
    }
  }

  public void addedClasses() {
    for (Pair<ASTCDType, ASTCDType> astcdClass : syntaxDiff.addedClassList()) {
      System.out.println(
          "A new class "
              + astcdClass.a.getSymbol().getInternalQualifiedName()
              + " has been added to the diagram and there is a change in the class "
              + astcdClass.b.getSymbol().getInternalQualifiedName()
              + ".");
      System.out.println("=======================================================");
    }
  }

  public void inheritanceDiffs() {
    for (InheritanceDiff inheritanceDiff : syntaxDiff.mergeInheritanceDiffs()) {
      ASTCDType astcdClass = inheritanceDiff.getAstcdClasses().a;
      //      if
      // (!syntaxDiff.getHelper().getNotInstanClassesSrc().contains(inheritanceDiff.getAstcdClasses().a)) {
      //        astcdClass = syntaxDiff.helper.minSubClass(inheritanceDiff.getAstcdClasses().a);
      //      }
      if (astcdClass != null) {
        System.out.println(
            "For the class "
                + astcdClass.getSymbol().getInternalQualifiedName()
                + " the inheritance relations were changed");
        System.out.println("=======================================================");
      }
    }
  }

  public void srcExistsTgtNot() {
    for (ASTCDType astcdClass : syntaxDiff.srcExistsTgtNot()) {
      System.out.println(
          "In tgtCD the class"
              + astcdClass.getSymbol().getInternalQualifiedName()
              + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.");
      System.out.println("=======================================================");
    }
  }

  public void changedTypes() {
    for (TypeDiffStruct typeDiffStruct : syntaxDiff.changedTypes()) {
      if (!(typeDiffStruct.getAstcdType() instanceof ASTCDEnum)) {
        if (!typeDiffStruct.getAstcdType().getModifier().isAbstract()) {
          StringBuilder comment =
              new StringBuilder(
                  "In the class "
                      + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                      + " the following is changed: ");
          if (typeDiffStruct.getAddedAttributes() != null
              && !typeDiffStruct.getAddedAttributes().b.isEmpty()) {
            comment.append("\nadded attributes - ");
            for (ASTCDAttribute attribute : typeDiffStruct.getAddedAttributes().b) {
              comment.append(attribute.getName());
            }
          }
          if (typeDiffStruct.getMemberDiff() != null) {
            comment.append("\nchanged attributes - ");
            for (ASTCDAttribute attribute : typeDiffStruct.getMemberDiff().b) {
              comment
                  .append(attribute.getName())
                  .append(" from ")
                  .append(
                      Objects.requireNonNull(getOldAtt(attribute, typeDiffStruct))
                          .getMCType()
                          .printType())
                  .append(" to ")
                  .append(attribute.getMCType().printType());
            }
          }
          if (typeDiffStruct.getChangedStereotype() != null) {
            comment.append("\nchanged stereotype - ");
          }
          if (typeDiffStruct.getDeletedAttributes() != null
              && !typeDiffStruct.getDeletedAttributes().b.isEmpty()) {
            comment.append("\ndeleted attributes - ");
            for (ASTCDAttribute attribute : typeDiffStruct.getDeletedAttributes().b) {
              comment.append(attribute.getName());
            }
          }
          System.out.println(comment);
          System.out.println("=======================================================");
        } else {
          Optional<ASTCDClass> subClass =
              syntaxDiff.helper.minSubClass(typeDiffStruct.getAstcdType());
          if (subClass.isPresent()) {
            StringBuilder comment =
                new StringBuilder(
                    "For the abstract class "
                        + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                        + " the following is changed: ");
            if (typeDiffStruct.getAddedAttributes() != null) {
              comment.append("\nadded attributes - ");
              for (ASTCDAttribute attribute : typeDiffStruct.getAddedAttributes().b) {
                comment.append(attribute.getName());
              }
            }
            if (typeDiffStruct.getMemberDiff() != null) {
              comment.append("\nchanged attributes - ");
              for (ASTCDAttribute attribute : typeDiffStruct.getMemberDiff().b) {
                comment
                    .append(attribute.getName())
                    .append(" from ")
                    .append(getOldAtt(attribute, typeDiffStruct).getMCType().printType())
                    .append(" to ")
                    .append(attribute.getMCType().printType());
              }
            }
            if (typeDiffStruct.getDeletedAttributes() != null) {
              comment.append("\ndeleted attributes - ");
              for (ASTCDAttribute attribute : typeDiffStruct.getDeletedAttributes().b) {
                comment.append(attribute.getName());
              }
            }
            System.out.println(comment);
            System.out.println("=======================================================");
          }
        }
      }
    }
  }

  public void addedConstants() {
    for (TypeDiffStruct typeDiffStruct : syntaxDiff.changedTypes()) {
      if (typeDiffStruct.getAddedConstants() != null) {
        for (ASTCDEnumConstant constant : typeDiffStruct.getAddedConstants().b) {
          ASTCDClass astcdClass = getClassForEnum((ASTCDEnum) typeDiffStruct.getAstcdType());
          if (astcdClass != null) {
            System.out.println(
                "In the enum "
                    + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                    + " the following constant is added: "
                    + constant.getName());
            System.out.println("=======================================================");
          }
        }
      }
    }
  }

  public void changedAssocs() {
    for (AssocDiffStruct assocDiffStruct : syntaxDiff.changedAssoc()) {
      Pair<ASTCDType, ASTCDType> pair =
          Syn2SemDiffHelper.getConnectedTypes(
              assocDiffStruct.getAssociation(), syntaxDiff.getSrcCD());
      String comment =
          "In the association between "
              + pair.a.getSymbol().getInternalQualifiedName()
              + " and "
              + pair.b.getSymbol().getInternalQualifiedName()
              + " the following is changed: ";
      if (assocDiffStruct.isChangedDir()) {
        comment =
            comment
                + "\ndirection - "
                + Syn2SemDiffHelper.getDirection(assocDiffStruct.getAssociation()).toString();
      }
      if (assocDiffStruct.getChangedCard() != null && !assocDiffStruct.getChangedCard().isEmpty()) {
        comment = comment + "\ncardinalities - " + assocDiffStruct.getChangedCard().toString();
      }
      if (assocDiffStruct.getChangedRoleNames() != null
          && !assocDiffStruct.getChangedRoleNames().isEmpty()) {
        comment = comment + "\nrole name -";
        Iterator<Pair<ClassSide, ASTCDRole>> roleName =
            assocDiffStruct.getChangedRoleNames().iterator();
        while (roleName.hasNext()) {
          Pair<ClassSide, ASTCDRole> pair1 = roleName.next();
          comment = comment + " (" + pair1.a + " to " + pair1.b.getName() + ")";
        }
      }
      if (assocDiffStruct.getChangedTgt() != null) {
        comment =
            comment
                + "\nchanged target - "
                + assocDiffStruct.getChangedTgt().getSymbol().getInternalQualifiedName();
      }
      if (assocDiffStruct.getChangedSrc() != null) {
        comment =
            comment
                + "\nchanged source - "
                + assocDiffStruct.getChangedSrc().getSymbol().getInternalQualifiedName();
      }
      System.out.println(comment);
      System.out.println("=======================================================");
    }
//    for (CDAssocDiff assocDiff : syntaxDiff.getChangedAssocs()) {
//      if (syntaxDiff.helper.srcAssocExistsTgtNot(assocDiff.getSrcElem(), assocDiff.getTgtElem())) {
//        System.out.println(
//            "An association between the classes "
//                + Syn2SemDiffHelper.getConnectedTypes(
//                        assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD())
//                    .a
//                    .getSymbol()
//                    .getInternalQualifiedName()
//                + " and "
//                + Syn2SemDiffHelper.getConnectedTypes(
//                        assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD())
//                    .b
//                    .getSymbol()
//                    .getInternalQualifiedName()
//                + " has been added from the diagram.");
//        System.out.println("=======================================================");
//      }
//      if (syntaxDiff.helper.srcNotTgtExists(assocDiff.getSrcElem(), assocDiff.getTgtElem())) {
//        System.out.println(
//            "An association between the classes "
//                + Syn2SemDiffHelper.getConnectedTypes(
//                        assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD())
//                    .a
//                    .getSymbol()
//                    .getInternalQualifiedName()
//                + " and "
//                + Syn2SemDiffHelper.getConnectedTypes(
//                        assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD())
//                    .b
//                    .getSymbol()
//                    .getInternalQualifiedName()
//                + " has been removed from the diagram.");
//        System.out.println("=======================================================");
//      }
//    }
  }

  public void staDiff() {
    for (ASTCDType astcdClass : syntaxDiff.hasDiffSuper()) {
      System.out.println(
          "The class "
              + astcdClass.getSymbol().getInternalQualifiedName()
              + " has changed direct superclasses.");
      System.out.println("=======================================================");
    }
  }

  public void deletedAssocs() {
    for (Pair<ASTCDAssociation, List<ASTCDType>> pair : syntaxDiff.deletedAssocList()) {
      List<ASTCDType> list = pair.b;
        Pair<ASTCDType, ASTCDType> connectedClasses =
            Syn2SemDiffHelper.getConnectedTypes(pair.a, helper.getTgtCD());
        System.out.println(
            "The association between the classes "
                + connectedClasses.a.getSymbol().getInternalQualifiedName()
                + connectedClasses.b.getSymbol().getInternalQualifiedName()
                + " has been removed from the diagram.");
        System.out.println("=======================================================");
      }
  }

  private ASTCDAttribute getOldAtt(ASTCDAttribute attribute, TypeDiffStruct diffStruc) {
    for (Pair<ASTCDAttribute, ASTCDAttribute> pair : diffStruc.getMatchedAttributes()) {
      if (pair.a.equals(attribute)) {
        return pair.b;
      }
    }
    return null;
  }

  private ASTCDClass getClassForEnum(ASTCDEnum astcdEnum) {
    for (ASTCDClass astcdClass :
        syntaxDiff.helper.getSrcCD().getCDDefinition().getCDClassesList()) {
      if (!astcdClass.getModifier().isAbstract()) {
        List<ASTCDAttribute> attributes = syntaxDiff.helper.getAllAttr(astcdClass).b;
        for (ASTCDAttribute attribute : attributes) {
          if (attribute.getMCType().printType().equals(astcdEnum.getName())) {
            return astcdClass;
          }
        }
      }
    }
    return null;
  }
}
