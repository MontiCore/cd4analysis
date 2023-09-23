package de.monticore.cddiff.syndiff.OD;

import de.monticore.ast.CommentBuilder;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.umlstereotype._ast.ASTStereoValueBuilder;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

public class DiffHelper {
  private int indexClass = 1;
  private int indexAssoc = 1;
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  private CDSyntaxDiff syntaxDiff;

  public DiffHelper() {
  }

  public DiffHelper(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    syntaxDiff.findOverlappingAssocs();
  }

  //multi-instance and simple - remove superclasses - done
  //if an association cannot be instatiated with the given classes, then it should be instantiated with the minDiffWitness - done
  //create such association
  public List<ASTODArtifact> generateODs(boolean staDiff) {
    List<ASTODArtifact> artifactList = new ArrayList<>();
    for (ASTCDAssociation association : syntaxDiff.addedAssocList()) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD());
      if (!helper.getNotInstanClassesSrc().contains(pair.a) && !helper.getNotInstanClassesSrc().contains(pair.b)) {
        ASTCDClass leftClass = pair.a;
        ASTCDClass rightClass = pair.b;
        if (pair.a.getModifier().isAbstract()) {
          leftClass = helper.minSubClass(pair.a);
        }
        if (pair.b.getModifier().isAbstract()) {
          rightClass = helper.minSubClass(pair.b);
        }
        String comment = "A new associations has been added to the diagram."
          + "\nThis association allows a new relation between the classes " + pair.a.getSymbol().getInternalQualifiedName() + " and " + pair.b.getSymbol().getInternalQualifiedName() + " and their subclasses";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(association),
          generateElements(association, Arrays.asList(1, 1), comment),
          null);
        if (astodArtifact != null) {
          artifactList.add(astodArtifact);
        }
      }
    }

    for (ASTCDClass astcdClass1 : syntaxDiff.addedClassList()) {
      ASTCDClass astcdClass = astcdClass1;
      if (astcdClass.getModifier().isAbstract()) {
        astcdClass = helper.minSubClass(astcdClass);
      }
      String comment = "A new class " + astcdClass1.getSymbol().getInternalQualifiedName() + " has been added to the diagram that is not abstract and couldn't be matched with any of the old classes.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass1),
        generateElements(astcdClass1, comment),
        null);
      if (astodArtifact != null) {
        artifactList.add(astodArtifact);
      }
    }

    for (Pair<ASTCDAssociation, ASTCDClass> pair : syntaxDiff.deletedAssocList()) {
      ASTCDClass astcdClass = pair.b;
      if (astcdClass.getModifier().isAbstract()) {
        astcdClass = helper.minSubClass(astcdClass);
      }
      String comment = "An association for the class " + pair.b.getSymbol().getInternalQualifiedName() + " has been removed from the diagram.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(pair.b),
        generateElements(pair.b, comment),
        null);
      if (astodArtifact != null) {
        artifactList.add(astodArtifact);
      }
    }

    for (ASTCDClass astcdClass : syntaxDiff.srcExistsTgtNot()) {
      String comment = "In tgtCD the class" + astcdClass.getSymbol().getInternalQualifiedName() + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, comment),
        null);
      if (astodArtifact != null) {
        artifactList.add(astodArtifact);
      }
    }

    //implement a function that
    for (TypeDiffStruc typeDiffStruc : syntaxDiff.changedTypes()) {
      if ((typeDiffStruc.getAstcdType() instanceof ASTCDClass)
        && !typeDiffStruc.getAstcdType().getModifier().isAbstract()) {
        StringBuilder comment = new StringBuilder("In the class " + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName() + " the following is changed: ");
        if (typeDiffStruc.getAddedAttributes() != null
          && !typeDiffStruc.getAddedAttributes().b.isEmpty()) {
          comment.append("\nadded attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruc.getAddedAttributes().b) {
            comment.append(attribute.getName());
          }
        }
        if (typeDiffStruc.getMemberDiff() != null) {
          comment.append("\nchanged attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruc.getMemberDiff().b) {
            comment.append(attribute.getName())
              .append(" from ")
              .append(getOldAtt(attribute, typeDiffStruc)
                .getMCType().printType()).append(" to ")
              .append(attribute.getMCType().printType());
          }
        }
        if (typeDiffStruc.getChangedStereotype() != null) {
          comment.append("\nchanged stereotype - ");
        }
        if (typeDiffStruc.getDeletedAttributes() != null
          && !typeDiffStruc.getDeletedAttributes().b.isEmpty()) {
          comment.append("\ndeleted attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruc.getDeletedAttributes().b) {
            comment.append(attribute.getName());
          }
        }
        ASTODArtifact astodArtifact;
        astodArtifact = generateArtifact(oDTitleForClass((ASTCDClass) typeDiffStruc.getAstcdType()),
          generateElements((ASTCDClass) typeDiffStruc.getAstcdType(), comment.toString()),
          null);
        if (astodArtifact != null) {
          artifactList.add(astodArtifact);
        }
      }
//      else {
//         ASTCDClass subClass = helper.minDiffWitness((ASTCDClass) typeDiffStruc.getAstcdType());
//         if (subClass != null){
//           StringBuilder comment = new StringBuilder("For the abstract class "
//             + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName()
//             + " the following is changed: ");
//           if (!typeDiffStruc.getAddedAttributes().b.isEmpty()) {
//             comment.append("\nadded attributes - ");
//             for (ASTCDAttribute attribute : typeDiffStruc.getAddedAttributes().b) {
//               comment.append(attribute.getName());
//             }
//           }
//           if (typeDiffStruc.getMemberDiff() != null) {
//             comment.append("\nchanged attributes - ");
//             for (ASTCDAttribute attribute : typeDiffStruc.getMemberDiff().b) {
//               comment.append(attribute.getName())
//                 .append(" from ")
//                 .append(getOldAtt(attribute, typeDiffStruc).getMCType().printType())
//                 .append(" to ")
//                 .append(attribute.getMCType().printType());
//             }
//           }
//           if (!typeDiffStruc.getDeletedAttributes().b.isEmpty()) {
//             comment.append("\ndeleted attributes - ");
//             for (ASTCDAttribute attribute : typeDiffStruc.getDeletedAttributes().b) {
//               comment.append(attribute.getName());
//             }
//           }
//           ASTODArtifact astodArtifact;
//             astodArtifact = generateArtifact(oDTitleForClass(subClass),
//               generateElements(subClass, comment.toString()), null);
//           artifactList.add(astodArtifact);
//         }
//      }
    }

    //implement a function that searches for an instantiatable class with enum attribute - done
    for (TypeDiffStruc typeDiffStruc : syntaxDiff.changedTypes()) {
      if (typeDiffStruc.getAddedConstants() != null) {
        for (ASTCDEnumConstant constant : typeDiffStruc.getAddedConstants().b) {
          ASTCDClass astcdClass = getClassForEnum((ASTCDEnum) typeDiffStruc.getAstcdType());
          if (astcdClass != null) {
            String comment = "In the enum " + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName() + " the following constant is added: " + constant.getName();
            ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
              generateElements(astcdClass, comment),
              null);
            if (astodArtifact != null) {
              artifactList.add(astodArtifact);
            }
          }
        }
      }
    }

    for (AssocDiffStruc assocDiffStruc : syntaxDiff.changedAssoc()) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiffStruc.getAssociation(), helper.getSrcCD());
      String comment = "In the association between " + pair.a.getSymbol().getInternalQualifiedName() + " and " + pair.b.getSymbol().getInternalQualifiedName() + " the following is changed: ";
      if (assocDiffStruc.isChangedDir()) {
        comment = comment + "\ndirection - " + Syn2SemDiffHelper.getDirection(assocDiffStruc.getAssociation()).toString();
      }
      if (assocDiffStruc.getChangedCard() != null
        && !assocDiffStruc.getChangedCard().isEmpty()) {
        comment = comment + "\ncardinalities - " + assocDiffStruc.getChangedCard().toString();
      }
      if (assocDiffStruc.getChangedRoleNames() != null
        && !assocDiffStruc.getChangedRoleNames().isEmpty()) {
        comment = comment + "\nrole name - " + assocDiffStruc.getChangedRoleNames().toString();
      }
      if (assocDiffStruc.getChangedTgt() != null) {
        comment = comment + "\nchanged target - " + assocDiffStruc.getChangedTgt().getSymbol().getInternalQualifiedName();
      }
      if (assocDiffStruc.getChangedSrc() != null) {
        comment = comment + "\nchanged source - " + assocDiffStruc.getChangedSrc().getSymbol().getInternalQualifiedName();
      }
      ArrayList<Integer> list = new ArrayList<>();
      //if both cardinalities are changed, then two ODs must be generated - done
      if (assocDiffStruc.getChangedCard() != null
        && assocDiffStruc.getChangedCard().isEmpty()) {
        list.add(1);
        list.add(1);
      } else if (assocDiffStruc.getChangedCard() != null
        && assocDiffStruc.getChangedCard().size() == 1) {
        if (assocDiffStruc.getChangedCard().get(0).a == ClassSide.Left) {
          list.add(assocDiffStruc.getChangedCard().get(0).b);
          list.add(1);
        } else {
          list.add(1);
          list.add(assocDiffStruc.getChangedCard().get(0).b);
        }
      } else if (assocDiffStruc.getChangedCard() != null) {
        if (assocDiffStruc.getChangedCard().get(0).a == ClassSide.Left) {
          list.add(assocDiffStruc.getChangedCard().get(0).b);
          list.add(assocDiffStruc.getChangedCard().get(1).b);
        } else {
          list.add(assocDiffStruc.getChangedCard().get(1).b);
          list.add(assocDiffStruc.getChangedCard().get(0).b);
        }
      }
      if (list.size() == 2) {
        if (list.get(0) == 0 && list.get(1) == 0) {
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(assocDiffStruc.getAssociation()),
            generateElements(assocDiffStruc.getAssociation(), list, comment),
            null);
          if (astodArtifact != null) {
            artifactList.add(astodArtifact);
          }
        } else {
          ArrayList<Integer> list1 = new ArrayList<>(list);
          list.set(1, 1);
            ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(assocDiffStruc.getAssociation()),
                generateElements(assocDiffStruc.getAssociation(), list1, comment),
                null);
            if (astodArtifact != null) {
                artifactList.add(astodArtifact);
            }
            ArrayList<Integer> list2 = new ArrayList<>(list);
            list.set(0, 1);
          ASTODArtifact astodArtifact2 = generateArtifact(oDTitleForAssoc(assocDiffStruc.getAssociation()),
            generateElements(assocDiffStruc.getAssociation(), list2, comment),
            null);
          if (astodArtifact != null) {
            artifactList.add(astodArtifact2);
          }
        }
      } else {
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(assocDiffStruc.getAssociation()),
          generateElements(assocDiffStruc.getAssociation(), list, comment),
          null);
        if (astodArtifact != null) {
          artifactList.add(astodArtifact);
        }
      }
    }

    for (CDAssocDiff assocDiff : syntaxDiff.getChangedAssocs()) {
      if (syntaxDiff.helper.srcAssocExistsTgtNot(assocDiff.getSrcElem(), assocDiff.getTgtElem())) {
        String comment = "An association between the classes "
          + Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          + " and " + Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          + " has been added from the diagram.";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(assocDiff.getSrcElem()),
          generateElements(assocDiff.getSrcElem(), Arrays.asList(1, 1), comment),
          null);
        if (astodArtifact != null) {
          artifactList.add(astodArtifact);
        }
      }
      if (syntaxDiff.helper.srcNotTgtExists(assocDiff.getSrcElem(), assocDiff.getTgtElem())) {
        String comment = "An association between the classes "
          + Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          + " and " + Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          + " has been removed from the diagram.";
        Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD());
        if (assocDiff.getSrcElem().getCDAssocDir().isDefinitiveNavigableRight()) {
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(pair.a),
            generateElements(pair.a, comment),
            null);
          if (astodArtifact != null) {
            artifactList.add(astodArtifact);
          }
        }
        if (assocDiff.getSrcElem().getCDAssocDir().isDefinitiveNavigableLeft()) {
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(pair.b),
            generateElements(pair.b, comment),
            null);
          if (astodArtifact != null) {
            artifactList.add(astodArtifact);
          }
        }
      }
    }

    AssocDiffs assocDiffs = syntaxDiff.getAssocDiffs();
    for (ASTCDClass astcdClass : assocDiffs.getAllInSrc()) {
      String comment = "The class " + astcdClass.getSymbol().getInternalQualifiedName() + " has associations that aren't in tgt.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, comment),
        null);
      if (astodArtifact != null) {
        artifactList.add(astodArtifact);
      }
    }
    for (ASTCDClass astcdClass : assocDiffs.getAllInTgt()) {
      String comment = "The class " + astcdClass.getSymbol().getInternalQualifiedName() + " has associations that aren't in src.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, comment),
        null);
      if (astodArtifact != null) {
        artifactList.add(astodArtifact);
      }
    }
    for (ASTCDClass astcdClass : assocDiffs.getMixed()) {
      String comment = "The class " + astcdClass.getSymbol().getInternalQualifiedName() + " has associations that aren't in src and associations that aren't in tgt.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, comment),
        null);
      if (astodArtifact != null) {
        artifactList.add(astodArtifact);
      }
    }

    for (ASTCDClass astcdClass : syntaxDiff.tgtExistsSrcNot()){
      String comment = "The class " + astcdClass.getSymbol().getInternalQualifiedName() + " can be instantiated without at least one association, because the associated class cannot be instantiated (overlapping).";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, comment),
        null);
      if (astodArtifact != null) {
        artifactList.add(astodArtifact);
      }
    }

    if (staDiff) {
      for (ASTCDClass astcdClass : syntaxDiff.getSTADiff()) {
        String comment = "The class " + astcdClass.getSymbol().getInternalQualifiedName() + " is part of a different inheritance tree.";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
          generateElements(astcdClass, comment),
          null);
        if (astodArtifact != null) {
          artifactList.add(astodArtifact);
        }
      }

      for (InheritanceDiff inheritanceDiff : syntaxDiff.mergeInheritanceDiffs()) {
        if (!helper.getNotInstanClassesSrc().contains(inheritanceDiff.getAstcdClasses().a)) {
          ASTCDClass astcdClass = inheritanceDiff.getAstcdClasses().a;
          if (inheritanceDiff.getAstcdClasses().a.getModifier().isAbstract()) {
            astcdClass = helper.minSubClass(inheritanceDiff.getAstcdClasses().a);
          }
          String comment = "For the class " + inheritanceDiff.getAstcdClasses().a.getSymbol().getInternalQualifiedName() + " the inheritance relations were changed";
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(inheritanceDiff.getAstcdClasses().a),
            generateElements(inheritanceDiff.getAstcdClasses().a, comment),
            null);
          if (astodArtifact != null) {
            artifactList.add(astodArtifact);
          }
        }
      }
    }
    if (!staDiff){
      helper.makeSimpleSem(artifactList);
    }
    return artifactList;
  }

  private ASTCDAttribute getOldAtt(ASTCDAttribute attribute, TypeDiffStruc diffStruc) {
    for (Pair<ASTCDAttribute, ASTCDAttribute> pair : diffStruc.getMatchedAttributes()) {
      if (pair.a.equals(attribute)) {
        return pair.b;
      }
    }
    return null;
  }

  private ASTCDClass getClassForEnum(ASTCDEnum astcdEnum) {
    for (ASTCDClass astcdClass : helper.getSrcCD().getCDDefinition().getCDClassesList()) {
      if (!astcdClass.getModifier().isAbstract()) {
        List<ASTCDAttribute> attributes = helper.getAllAttr(astcdClass).b;
        for (ASTCDAttribute attribute : attributes) {
          if (attribute.getMCType().printType().equals(astcdEnum.getName())) {
            return astcdClass;
          }
        }
      }
    }
    return null;
  }

  public List<ASTODElement> generateElements(ASTCDClass astcdClass,
                                             String comment) {
    Set<ASTODElement> elements;
    DiffWitnessGenerator oDHelper = new DiffWitnessGenerator(Math.max(helper.getSrcCD().getCDDefinition().getCDClassesList().size(), helper.getTgtCD().getCDDefinition().getCDClassesList().size()));
    System.out.println("start " + astcdClass.getName());
    elements = oDHelper.getObjForOD(astcdClass);
    if (elements.isEmpty()) {
      return new ArrayList<>();
    }
    ASTODObject matchedObject = null;
    for (ASTODElement element : elements) {
      if (element instanceof ASTODObject) {
        if (((ASTODObject) element).getMCObjectType().printType().equals(astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"))) {
          matchedObject = (ASTODObject) element;
        }
      }
    }

    ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
    valueBuilder.setName("diff");
    valueBuilder.setContent("diffClass");
    matchedObject.getModifier().getStereotype().addValues(valueBuilder.build());

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    matchedObject.set_PostCommentList(List.of(commentBuilder.build()));

    return new ArrayList<>(elements);
  }

  public List<ASTODElement> generateElements(ASTCDAssociation association,
                                             List<Integer> integers,
                                             String comment) {
    DiffWitnessGenerator oDHelper = new DiffWitnessGenerator(Math.max(helper.getSrcCD().getCDDefinition().getCDClassesList().size(), helper.getTgtCD().getCDDefinition().getCDClassesList().size()));
    System.out.println("start association");
    Pair<Set<ASTODElement>, ASTODElement> pair = oDHelper.getObjForOD(association, integers.get(0), integers.get(1));
    if (pair.a.isEmpty()){
      return new ArrayList<>();
    }
    Set<ASTODElement> elements;
    elements = pair.a;



    ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
    valueBuilder.setName("diff");
    valueBuilder.setContent("diffAssoc");
    if (pair.b instanceof ASTODLink) {
      ((ASTODLink) pair.b).setStereotype(OD4ReportMill.stereotypeBuilder().addValues(valueBuilder.build()).build());
    } else {
      ((ASTODObject) pair.b).getModifier().getStereotype().addValues(valueBuilder.build());
    }

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    pair.b.set_PostCommentList(List.of(commentBuilder.build()));
    return new ArrayList<>(elements);
  }

  public static ASTODArtifact generateArtifact(String name, List<ASTODElement> astodElementList, String stereotype) {
    if (astodElementList.isEmpty()) {
      return null;
    }
    ASTObjectDiagram astObjectDiagram =
      OD4ReportMill.objectDiagramBuilder()
        .setName(name)
        .setODElementsList(astodElementList)
        .setStereotype(
          OD4ReportMill.stereotypeBuilder()
            .addValues(
              OD4ReportMill.stereoValueBuilder()
                .setName("syntaxDiffCategory")
                .setContent("diff" + stereotype)
                .setText(
                  OD4ReportMill.stringLiteralBuilder().setSource("diff" + stereotype).build())
                .build())
            .build())
        .build();
    return OD4ReportMill.oDArtifactBuilder().setObjectDiagram(astObjectDiagram).build();
  }

  public String oDTitleForAssoc(ASTCDAssociation association) {
    String srcName;
    String tgtName;
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD());
    if (association.getCDAssocDir().isBidirectional()) {
      srcName = pair.a.getSymbol().getInternalQualifiedName();
      tgtName = pair.b.getSymbol().getInternalQualifiedName();
    } else {
      if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        srcName = pair.b.getSymbol().getInternalQualifiedName();
        tgtName = pair.a.getSymbol().getInternalQualifiedName();
      } else {
        srcName = pair.a.getSymbol().getInternalQualifiedName();
        tgtName = pair.b.getSymbol().getInternalQualifiedName();
      }
    }
    String stringBuilder = "AssocDiff_" + indexAssoc + srcName + "_" + tgtName;
    indexAssoc++;
    return stringBuilder;
  }

  public String oDTitleForClass(ASTCDClass astcdClass) {
    String stringBuilder = "ClassDiff_" + indexClass + astcdClass.getSymbol().getInternalQualifiedName();
    indexClass++;
    return stringBuilder;
  }

  public static String printOD(ASTODArtifact astodArtifact) {
    return OD4ReportMill.prettyPrint(astodArtifact, true);
  }

  public static List<String> printODs(List<ASTODArtifact> astODArtifacts) {
    List<String> result = new ArrayList<>();
    for (ASTODArtifact od : astODArtifacts) {
      result.add(OD4ReportMill.prettyPrint(od, true));
    }
    return result;
  }
}
