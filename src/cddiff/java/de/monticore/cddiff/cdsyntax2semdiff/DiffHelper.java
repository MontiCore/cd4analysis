package de.monticore.cddiff.cdsyntax2semdiff;

import de.monticore.ast.CommentBuilder;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cddiff.syndiff.semdiff.CDAssocDiff;
import de.monticore.cddiff.syndiff.semdiff.CDSyntaxDiff;
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
  private final Syn2SemDiffHelper helper;
  private final CDSyntaxDiff syntaxDiff;
  private int diffLimit = 0;
  private int diffSize = 0;
  private boolean analyseOverlapping = true;
  public Syn2SemDiffHelper getHelper() {
    return helper;
  }

  /**
   * Create a DiffHelper to compare the two diagrams without
   * limitations on the generated objects.
   * @param srcCD source diagram
   * @param tgtCD target diagram
   */
  public DiffHelper(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    this.helper = syntaxDiff.getHelper();
  }

  /**
   * Create a DiffHelper to compare the two diagrams with
   * a limit on the number of generated objects.
   * @param srcCD source diagram
   * @param tgtCD target diagram
   * @param diffLimit maximum number of generated diagrams
   * @param diffSize maximum number of objects in the generated diagrams
   * @param analyseOverlapping if true, overlapping associations are analysed (for Open-World diff)
   * If diffLimit or diffSize is 0, then there is no limit.
   */
  public DiffHelper(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, int diffLimit, int diffSize, boolean analyseOverlapping) {
    this.syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    this.helper = syntaxDiff.getHelper();
    this.diffLimit = diffLimit;
    this.diffSize = diffSize;
    if (analyseOverlapping){
      syntaxDiff.findOverlappingAssocs();
    } else {
      this.analyseOverlapping = false;
    }
  }


  /**
   * Generates a list of ODs for the given diagrams.
   * For each change in the diagrams, the element (class or association) is
   * annotated with <<diff>> and a comment is added.
   * The diffLimit and diffSize are respected.
   * @param staDiff if true, the ODs are generated under Multi-Instance semantics
   * @return list of ODs.
   */
  public List<ASTODArtifact> generateODs(boolean staDiff) {
    List<ASTODArtifact> artifactList = new ArrayList<>();
    for (Pair<ASTCDAssociation, List<ASTCDClass>> association : syntaxDiff.addedAssocList()) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association.a, helper.getSrcCD());
      if (!helper.getNotInstClassesSrc().contains(pair.a) && !helper.getNotInstClassesSrc().contains(pair.b)) {
        String comment = "//A new associations has been added to the diagram."
          + "\n//This association allows a new relation between the classes " + pair.a.getSymbol().getInternalQualifiedName() + " and " + pair.b.getSymbol().getInternalQualifiedName() + " and their subclasses";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(association.a),
          generateElements(association.a, Arrays.asList(1, 1), comment)
        );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
      }
    }

    for (Pair<ASTCDClass, ASTCDClass> astcdClass1 : syntaxDiff.addedClassList()) {
      String comment = "//A new class " + astcdClass1.a.getSymbol().getInternalQualifiedName() + " has been added and now there is a change in the class "
        + astcdClass1.b.getSymbol().getInternalQualifiedName() + ".";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass1.b),
        generateElements(astcdClass1.b, comment, null)
      );
      if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
        artifactList.add(astodArtifact);
        if (artifactList.size() == diffLimit) {
          return artifactList;
        }
      } else if (astodArtifact != null && diffLimit == 0) {
        artifactList.add(astodArtifact);
      }
    }

    for (Pair<ASTCDAssociation, List<ASTCDClass>> pair : syntaxDiff.deletedAssocList()) {
      List<ASTCDClass> list = pair.b;
      for (ASTCDClass astcdClass : list) {
        if (astcdClass.getModifier().isAbstract()) {
          astcdClass = helper.minSubClass(astcdClass);
        }
        String comment = "//An association for the class " + astcdClass.getSymbol().getInternalQualifiedName() + " has been removed from the diagram.";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
          generateElements(astcdClass, comment, null)
        );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
      }
    }

    for (TypeDiffStruct typeDiffStruct : syntaxDiff.changedTypes()) {
      if ((typeDiffStruct.getAstcdType() instanceof ASTCDClass)
        && !typeDiffStruct.getAstcdType().getModifier().isAbstract()) {
        StringBuilder comment = new StringBuilder("//In the class " + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName() + " the following is changed: ");
        if (typeDiffStruct.getAddedAttributes() != null
          && !typeDiffStruct.getAddedAttributes().b.isEmpty()) {
          comment.append("\n//added attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruct.getAddedAttributes().b) {
            comment.append(attribute.getName());
          }
        }
        if (typeDiffStruct.getMemberDiff() != null) {
          comment.append("\n//changed attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruct.getMemberDiff().b) {
            comment
              .append(attribute.getName())
              .append(" from ")
              .append(getOldAtt(attribute, typeDiffStruct).getMCType().printType())
              .append(" to ")
              .append(attribute.getMCType().printType());
          }
        }
        if (typeDiffStruct.getChangedStereotype() != null) {
          comment.append("\n//changed stereotype");
        }
        if (typeDiffStruct.getDeletedAttributes() != null
          && !typeDiffStruct.getDeletedAttributes().b.isEmpty()) {
          comment.append("\n//deleted attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruct.getDeletedAttributes().b) {
            comment.append(attribute.getName());
          }
        }
        ASTODArtifact astodArtifact;
        astodArtifact =
          generateArtifact(
            oDTitleForClass((ASTCDClass) typeDiffStruct.getAstcdType()),
            generateElements(
              (ASTCDClass) typeDiffStruct.getAstcdType(), comment.toString(), null)
          );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
      }
    }

    // implement a function that searches for an instantiatable class with enum attribute - done
    for (TypeDiffStruct typeDiffStruct : syntaxDiff.changedTypes()) {
      if (typeDiffStruct.getAddedConstants() != null) {
        for (ASTCDEnumConstant constant : typeDiffStruct.getAddedConstants().b) {
          Pair<ASTCDClass, ASTCDAttribute> astcdClass = getClassForEnum((ASTCDEnum) typeDiffStruct.getAstcdType());
          if (astcdClass != null) {
            String comment =
                "//In the enum "
                    + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                    + " the following constant is added: "
                    + constant.getName();
            ASTODArtifact astodArtifact =
                generateArtifact(
                    oDTitleForClass(astcdClass.a),
                    generateElements(
                        astcdClass.a,
                        comment,
                        new Pair<>(
                                astcdClass.b, constant.getName()))
                );
            if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
              artifactList.add(astodArtifact);
              if (artifactList.size() == diffLimit) {
                return artifactList;
              }
            } else if (astodArtifact != null && diffLimit == 0) {
              artifactList.add(astodArtifact);
            }
          }
        }
      }
    }

    for (AssocDiffStruct assocDiffStruct : syntaxDiff.changedAssoc()) {
      Pair<ASTCDClass, ASTCDClass> pair =
          Syn2SemDiffHelper.getConnectedClasses(assocDiffStruct.getAssociation(), helper.getSrcCD());
      String comment =
          "//In the association between "
              + pair.a.getSymbol().getInternalQualifiedName()
              + " and "
              + pair.b.getSymbol().getInternalQualifiedName()
              + " the following is changed: ";
      if (assocDiffStruct.isChangedDir()) {
        comment =
            comment
                + "\n//direction - "
                + Syn2SemDiffHelper.getDirection(assocDiffStruct.getAssociation()).toString();
      }
      if (assocDiffStruct.getChangedCard() != null && !assocDiffStruct.getChangedCard().isEmpty()) {
        comment = comment + "\n//cardinalities - " + assocDiffStruct.getChangedCard().toString();
      }
      if (assocDiffStruct.getChangedRoleNames() != null
          && !assocDiffStruct.getChangedRoleNames().isEmpty()) {
        comment = comment + "\n//role name - ";
        for (Pair<ClassSide, ASTCDRole> pair1 :
            assocDiffStruct.getChangedRoleNames()) {
          comment = comment + "[" + pair1.a.toString() + ", " + pair1.b.getName() + "] ";
        }
      }
      if (assocDiffStruct.getChangedTgt() != null) {
        comment =
            comment
                + "\n//changed target - "
                + assocDiffStruct.getChangedTgt().getSymbol().getInternalQualifiedName();
      }
      if (assocDiffStruct.getChangedSrc() != null) {
        comment =
            comment
                + "\n//changed source - "
                + assocDiffStruct.getChangedSrc().getSymbol().getInternalQualifiedName();
      }
      ArrayList<Integer> list = new ArrayList<>();
      if (assocDiffStruct.getChangedCard() != null && assocDiffStruct.getChangedCard().isEmpty()) {
        list.add(1);
        list.add(1);
      } else if (assocDiffStruct.getChangedCard() != null
          && assocDiffStruct.getChangedCard().size() == 1) {
        if (assocDiffStruct.getChangedCard().get(0).a == ClassSide.Left) {
          list.add(assocDiffStruct.getChangedCard().get(0).b);
          list.add(1);
        } else {
          list.add(1);
          list.add(assocDiffStruct.getChangedCard().get(0).b);
        }
      } else if (assocDiffStruct.getChangedCard() != null) {
        if (assocDiffStruct.getChangedCard().get(0).a == ClassSide.Left) {
          list.add(assocDiffStruct.getChangedCard().get(0).b);
          list.add(assocDiffStruct.getChangedCard().get(1).b);
        } else {
          list.add(assocDiffStruct.getChangedCard().get(1).b);
          list.add(assocDiffStruct.getChangedCard().get(0).b);
        }
      }
      if (list.isEmpty()) {
        list.add(1);
        list.add(1);
      }
      if ((list.get(0) == 0 && list.get(1) == 0)
        || (list.get(0) == 0 && list.get(1) == 2)
        || (list.get(0) == 2 && list.get(1) == 0)) {
        List<Integer> list1 = new ArrayList<>(list);
        list1.set(0, 1);
        ASTODArtifact astodArtifact =
            generateArtifact(
                oDTitleForAssoc(assocDiffStruct.getAssociation()),
                generateElements(assocDiffStruct.getAssociation(), list1, comment)
            );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
        List<Integer> list2 = new ArrayList<>(list);
        list2.set(1, 1);
        ASTODArtifact astodArtifact2 =
            generateArtifact(
                oDTitleForAssoc(assocDiffStruct.getAssociation()),
                generateElements(assocDiffStruct.getAssociation(), list2, comment)
            );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact2);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact2);
        }
      } else {//TODO: add other cases
        ASTODArtifact astodArtifact =
            generateArtifact(
                oDTitleForAssoc(assocDiffStruct.getAssociation()),
                generateElements(assocDiffStruct.getAssociation(), list, comment)
            );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
      }
    }

    for (CDAssocDiff assocDiff : syntaxDiff.getChangedAssocs()) {
      if (helper.srcAssocExistsTgtNot(assocDiff.getSrcElem(), assocDiff.getTgtElem())) {
        String comment =
            "//An association between the classes "
                + Syn2SemDiffHelper.getConnectedClasses(
                        assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD())
                    .a
                    .getSymbol()
                    .getInternalQualifiedName()
                + " and "
                + Syn2SemDiffHelper.getConnectedClasses(
                        assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD())
                    .b
                    .getSymbol()
                    .getInternalQualifiedName()
                + " has been added from the diagram.";
        ASTODArtifact astodArtifact =
            generateArtifact(
                oDTitleForAssoc(assocDiff.getSrcElem()),
                generateElements(assocDiff.getSrcElem(), Arrays.asList(1, 1), comment)
            );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
      }

//      if (helper.srcNotTgtExists(assocDiff.getSrcElem(), assocDiff.getTgtElem())) {
//        String comment =
//            "//An association between the classes "
//                + Syn2SemDiffHelper.getConnectedClasses(
//                        assocDiff.getTgtElem(), helper.getTgtCD())
//                    .a
//                    .getSymbol()
//                    .getInternalQualifiedName()
//                + " and "
//                + Syn2SemDiffHelper.getConnectedClasses(
//                        assocDiff.getTgtElem(), helper.getTgtCD())
//                    .b
//                    .getSymbol()
//                    .getInternalQualifiedName()
//                + " has been removed from the diagram.";
//        Pair<ASTCDClass, ASTCDClass> pair =
//            Syn2SemDiffHelper.getConnectedClasses(
//                assocDiff.getSrcElem(), helper.getSrcCD());
//        if (assocDiff.getSrcElem().getCDAssocDir().isDefinitiveNavigableRight()) {
//          ASTODArtifact astodArtifact =
//              generateArtifact(
//                  oDTitleForClass(pair.a), generateElements(pair.a, comment, null));
//          if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
//            artifactList.add(astodArtifact);
//            if (artifactList.size() == diffLimit) {
//              return artifactList;
//            }
//          } else if (astodArtifact != null && diffLimit == 0) {
//            artifactList.add(astodArtifact);
//          }
//        }
//        if (assocDiff.getSrcElem().getCDAssocDir().isDefinitiveNavigableLeft()) {
//          ASTODArtifact astodArtifact =
//              generateArtifact(
//                  oDTitleForClass(pair.b), generateElements(pair.b, comment, null));
//          if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
//            artifactList.add(astodArtifact);
//            if (artifactList.size() == diffLimit) {
//              return artifactList;
//            }
//          } else if (astodArtifact != null && diffLimit == 0) {
//            artifactList.add(astodArtifact);
//          }
//        }
//      }
    }

    if (analyseOverlapping) {
    AssocDiffs assocDiffs = syntaxDiff.getAssocDiffs();
    for (ASTCDClass astcdClass : assocDiffs.getAllInSrc()) {
      String comment =
          "//The class "
              + astcdClass.getSymbol().getInternalQualifiedName()
              + " has associations that aren't in tgt.";
      ASTODArtifact astodArtifact =
          generateArtifact(
              oDTitleForClass(astcdClass), generateElements(astcdClass, comment, null));
      if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
        artifactList.add(astodArtifact);
        if (artifactList.size() == diffLimit) {
          return artifactList;
        }
      } else if (astodArtifact != null && diffLimit == 0) {
        artifactList.add(astodArtifact);
      }
    }
    for (ASTCDClass astcdClass : assocDiffs.getAllInTgt()) {
      String comment =
          "//The class "
              + astcdClass.getSymbol().getInternalQualifiedName()
              + " has associations that aren't in src.";
      ASTODArtifact astodArtifact =
          generateArtifact(
              oDTitleForClass(astcdClass), generateElements(astcdClass, comment, null));
      if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
        artifactList.add(astodArtifact);
        if (artifactList.size() == diffLimit) {
          return artifactList;
        }
      } else if (astodArtifact != null && diffLimit == 0) {
        artifactList.add(astodArtifact);
      }
    }
    for (ASTCDClass astcdClass : assocDiffs.getMixed()) {
      String comment =
          "//The class "
              + astcdClass.getSymbol().getInternalQualifiedName()
              + " has associations that aren't in src and associations that aren't in tgt.";
      ASTODArtifact astodArtifact =
          generateArtifact(
              oDTitleForClass(astcdClass), generateElements(astcdClass, comment, null));
      if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
        artifactList.add(astodArtifact);
        if (artifactList.size() == diffLimit) {
          return artifactList;
        }
      } else if (astodArtifact != null && diffLimit == 0) {
        artifactList.add(astodArtifact);
      }
    }
      for (ASTCDClass astcdClass : syntaxDiff.srcExistsTgtNot()) {
        String comment = "//In tgtCD the class " + astcdClass.getSymbol().getInternalQualifiedName() + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
          generateElements(astcdClass, comment, null)
        );
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
      }
    }

    if (staDiff) {
      for (ASTCDClass astcdClass : syntaxDiff.hasDiffSuper()) {
        String comment =
            "//The class "
                + astcdClass.getSymbol().getInternalQualifiedName()
                + " is part of a different inheritance tree.";
        ASTODArtifact astodArtifact =
            generateArtifact(
                oDTitleForClass(astcdClass), generateElements(astcdClass, comment, null));
        if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact);
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact != null && diffLimit == 0) {
          artifactList.add(astodArtifact);
        }
      }

      for (InheritanceDiff inheritanceDiff : syntaxDiff.mergeInheritanceDiffs()) {
        if (!helper.getNotInstClassesSrc().contains(inheritanceDiff.getAstcdClasses().a)) {
          String comment =
              "//For the class "
                  + inheritanceDiff.getAstcdClasses().a.getSymbol().getInternalQualifiedName()
                  + " the inheritance relations were changed";
          ASTODArtifact astodArtifact =
              generateArtifact(
                  oDTitleForClass(inheritanceDiff.getAstcdClasses().a),
                  generateElements(inheritanceDiff.getAstcdClasses().a, comment, null)
              );
          if (astodArtifact != null && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact);
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact != null && diffLimit == 0) {
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

  private ASTCDAttribute getOldAtt(ASTCDAttribute attribute, TypeDiffStruct diffStruct) {
    for (Pair<ASTCDAttribute, ASTCDAttribute> pair : diffStruct.getMatchedAttributes()) {
      if (pair.a.equals(attribute)) {
        return pair.b;
      }
    }
    return null;
  }

  private Pair<ASTCDClass, ASTCDAttribute> getClassForEnum(ASTCDEnum astcdEnum) {
    for (ASTCDClass astcdClass : helper.getSrcCD().getCDDefinition().getCDClassesList()) {
      if (!astcdClass.getModifier().isAbstract()) {
        List<ASTCDAttribute> attributes = helper.getAllAttr(astcdClass).b;
        for (ASTCDAttribute attribute : attributes) {
          if (attribute.getMCType().printType().equals(astcdEnum.getName())) {
            return new Pair<>(astcdClass, attribute);
          }
        }
      }
    }
    return null;
  }

  /**
   * Generate an object diagram for the given class.
   * The class is annotated with <<diff>> and a comment is added.
   * The pair contains the attribute and the name of the enum constant.
   * It is used if the diff-witness is related to an added constant.
   * If the pair is null, then the diff-witness is not
   * related to an added constant.
   * @param astcdClass class that causes the diff.
   * @param comment comment for the diff.
   * @param pair attribute and name of the enum constant.
   * @return list of OD elements.
   */
  public List<ASTODElement> generateElements(
      ASTCDClass astcdClass, String comment, Pair<ASTCDAttribute, String> pair) {
    Set<ASTODElement> elements;
    DiffWitnessGenerator oDHelper;
    if (diffSize == 0) {
      oDHelper = new DiffWitnessGenerator(Math.max(helper.getSrcCD().getCDDefinition().getCDClassesList().size(), helper.getTgtCD().getCDDefinition().getCDClassesList().size()), helper);
    } else {
      oDHelper = new DiffWitnessGenerator(diffSize, helper);
    }
    elements = oDHelper.getObjForOD(astcdClass, pair);
    if (elements.isEmpty()) {
      return new ArrayList<>();
    }
    ASTODObject matchedObject = null;
    for (ASTODElement element : elements) {
      if (element instanceof ASTODObject) {
        if (((ASTODObject) element)
            .getMCObjectType()
            .printType()
            .equals(astcdClass.getSymbol().getInternalQualifiedName())) {
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
    matchedObject.set_PreCommentList(List.of(commentBuilder.build()));

    return new ArrayList<>(elements);
  }

  /**
   * Generate an object diagram for the given association.
   * The association is annotated with <<diff>> and a comment is added.
   * @param association association that causes the diff.
   * @param association association that causes the diff.
   * @param integers cardinalities for the diff.
   * @param comment comment for the diff.
   * @return list of OD elements.
   */
  public List<ASTODElement> generateElements(ASTCDAssociation association,
                                             List<Integer> integers,
                                             String comment) {
    DiffWitnessGenerator oDHelper;
    if (diffSize == 0) {
      oDHelper = new DiffWitnessGenerator(Math.max(helper.getSrcCD().getCDDefinition().getCDClassesList().size(), helper.getTgtCD().getCDDefinition().getCDClassesList().size()), helper);
    } else {
      oDHelper = new DiffWitnessGenerator(diffSize, helper);
    }
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
      ((ASTODLink) pair.b)
          .setStereotype(OD4ReportMill.stereotypeBuilder().addValues(valueBuilder.build()).build());
    } else {
      ((ASTODObject) pair.b).getModifier().getStereotype().addValues(valueBuilder.build());
    }

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    pair.b.set_PreCommentList(List.of(commentBuilder.build()));
    return new ArrayList<>(elements);
  }

  /**
   * Generate an object diagram for a given diff-witness.
   * @param name name of the OD.
   * @param astodElementList list of OD elements.
   * @return object diagram.
   */
  public static ASTODArtifact generateArtifact(
      String name, List<ASTODElement> astodElementList) {
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
                            .setContent("diff")
                            .setText(
                                OD4ReportMill.stringLiteralBuilder()
                                    .setSource("diff")
                                    .build())
                            .build())
                    .build())
            .build();
    return OD4ReportMill.oDArtifactBuilder().setObjectDiagram(astObjectDiagram).build();
  }

  /**
   * Generate a title for OD for the given association.
   * @param association association that causes the diff.
   * @return title for OD.
   */
  public String oDTitleForAssoc(ASTCDAssociation association) {
    String srcName;
    String tgtName;
    Pair<ASTCDClass, ASTCDClass> pair =
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD());
    if (association.getCDAssocDir().isBidirectional()) {
      srcName = pair.a.getSymbol().getInternalQualifiedName().replace(".", "_");
      tgtName = pair.b.getSymbol().getInternalQualifiedName().replace(".", "_");
    } else {
      if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        srcName = pair.b.getSymbol().getInternalQualifiedName().replace(".", "_");
        tgtName = pair.a.getSymbol().getInternalQualifiedName().replace(".", "_");
      } else {
        srcName = pair.a.getSymbol().getInternalQualifiedName().replace(".", "_");
        tgtName = pair.b.getSymbol().getInternalQualifiedName().replace(".", "_");
      }
    }
    String stringBuilder = "AssocDiff_" + indexAssoc + srcName + "_" + tgtName;
    indexAssoc++;
    return stringBuilder;
  }

  /**
   * Generate a title for OD for the given class.
   * @param astcdClass class that causes the diff.
   * @return title for OD.
   */
  public String oDTitleForClass(ASTCDClass astcdClass) {
    String stringBuilder =
        "ClassDiff_"
            + indexClass
            + astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_");
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
