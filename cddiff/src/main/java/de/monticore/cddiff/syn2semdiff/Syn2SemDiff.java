package de.monticore.cddiff.syn2semdiff;

import de.monticore.ast.CommentBuilder;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syn2semdiff.datastructures.*;
import de.monticore.cddiff.syn2semdiff.helpers.ODGenHelper;
import de.monticore.cddiff.syn2semdiff.odgen.ODGenerator;
import de.monticore.cddiff.syn2semdiff.odgen.Syn2SemDiffHelper;
import de.monticore.cddiff.syndiff.CDSyntaxDiff;
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

public class Syn2SemDiff {
  private int indexClass = 1;
  private int indexAssoc = 1;
  private final Syn2SemDiffHelper helper;
  private final ODGenHelper odGenHelper;
  private final CDSyntaxDiff syntaxDiff;
  private int diffLimit = 0;
  private int diffSize = 0;
  private boolean analyseOverlapping = true;

  public Syn2SemDiffHelper getHelper() {
    return helper;
  }

  /**
   * Create a DiffHelper to compare the two diagrams without limitations on the generated objects.
   *
   * @param srcCD source diagram
   * @param tgtCD target diagram
   */
  public Syn2SemDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    ReductionTrafo.handleAssocDirections(srcCD, tgtCD);
    this.syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    this.helper = syntaxDiff.getHelper();
    this.odGenHelper = new ODGenHelper(srcCD, helper);
    helper.findDuplicatedAssocs();
    syntaxDiff.findOverlappingAssocs();
  }

  public Syn2SemDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, List<MatchingStrategy> matchingStrategies) {
    ReductionTrafo.handleAssocDirections(srcCD, tgtCD);
    this.syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD, matchingStrategies);
    this.helper = syntaxDiff.getHelper();
    this.odGenHelper = new ODGenHelper(srcCD, helper);
    helper.findDuplicatedAssocs();
    syntaxDiff.findOverlappingAssocs();
  }

  /**
   * Create a DiffHelper to compare the two diagrams with a limit on the number of generated
   * objects.
   *
   * @param srcCD source diagram
   * @param tgtCD target diagram
   * @param diffLimit maximum number of generated diagrams
   * @param diffSize maximum number of objects in the generated diagrams
   * @param analyseOverlapping if true, overlapping associations are analysed (for Open-World diff)
   *     If diffLimit or diffSize is 0, then there is no limit.
   */
  public Syn2SemDiff(
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      int diffLimit,
      int diffSize,
      boolean analyseOverlapping) {
    ReductionTrafo.handleAssocDirections(srcCD, tgtCD);
    this.syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    this.helper = syntaxDiff.getHelper();
    this.odGenHelper = new ODGenHelper(srcCD, helper);
    this.diffLimit = diffLimit;
    this.diffSize = diffSize;
    if (analyseOverlapping) {
      helper.findDuplicatedAssocs();
      syntaxDiff.findOverlappingAssocs();
    } else {
      this.analyseOverlapping = false;
    }
  }

  /**
   * Generates a list of ODs for the given diagrams. For each change in the diagrams, the element
   * (class or association) is annotated with <<diff>> and a comment is added. The diffLimit and
   * diffSize are respected.
   *
   * @param staDiff if true, the ODs are generated under Multi-Instance semantics.
   * @return list of ODs.
   */
  public List<ASTODArtifact> generateODs(boolean staDiff) {
    List<ASTODArtifact> artifactList = new ArrayList<>();
    for (Pair<ASTCDAssociation, List<ASTCDType>> association : syntaxDiff.addedAssocList()) {
      Pair<ASTCDType, ASTCDType> pair =
          Syn2SemDiffHelper.getConnectedTypes(association.a, helper.getSrcCD());
      if (!helper.getNotInstClassesSrc().contains(pair.a)
          && !helper.getNotInstClassesSrc().contains(pair.b)) {
        String comment =
            "// A new associations has been added to the diagram."
                + System.lineSeparator()
                + "// This association allows a new relation between the classes "
                + pair.a.getSymbol().getInternalQualifiedName()
                + " and "
                + pair.b.getSymbol().getInternalQualifiedName()
                + " and their subclasses.";
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForAssoc(association.a, Arrays.asList(1, 1)),
                generateElements(association.a, Arrays.asList(1, 1), comment));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
    }

    for (Pair<ASTCDType, ASTCDType> astcdClass1 : syntaxDiff.addedClassList()) {
      String comment =
          "// A new class "
              + astcdClass1.a.getSymbol().getInternalQualifiedName()
              + " has been added and now there is a change in the class "
              + astcdClass1.b.getSymbol().getInternalQualifiedName()
              + ".";
      Optional<ASTODArtifact> astodArtifact =
          generateArtifact(
              oDTitleForClass(astcdClass1.b), generateElements(astcdClass1.b, comment, null));
      if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
        artifactList.add(astodArtifact.get());
        if (artifactList.size() == diffLimit) {
          return artifactList;
        }
      } else if (astodArtifact.isPresent() && diffLimit == 0) {
        artifactList.add(astodArtifact.get());
      }
    }

    for (Pair<ASTCDAssociation, List<ASTCDType>> pair : syntaxDiff.deletedAssocList()) {
      List<ASTCDType> list = pair.b;
      for (ASTCDType astcdClass : list) {
        String comment =
            "// The association between "
                + Syn2SemDiffHelper.getConnectedTypes(pair.a, helper.getTgtCD())
                    .a
                    .getSymbol()
                    .getInternalQualifiedName()
                + " and "
                + Syn2SemDiffHelper.getConnectedTypes(pair.a, helper.getTgtCD())
                    .b
                    .getSymbol()
                    .getInternalQualifiedName()
                + "for the class "
                + astcdClass.getSymbol().getInternalQualifiedName()
                + " has been removed from the diagram.";
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForClass(astcdClass), generateElements(astcdClass, comment, null));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
    }

    for (TypeDiffStruct typeDiffStruct : syntaxDiff.changedTypes()) {
      if (typeDiffStruct.getAddedDeletedAttributes() != null
          && !typeDiffStruct.getAddedDeletedAttributes().isEmpty()) {
        for (Pair<ASTCDClass, AddedDeletedAtt> attribute :
            typeDiffStruct.getAddedDeletedAttributes()) {
          StringBuilder comment =
              new StringBuilder(
                  "// Because of the class "
                      + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                      + " the attribute/s ");
          for (ASTCDAttribute attribute1 : attribute.b.getAddedAttributes()) {
            comment.append(System.lineSeparator()).append("// ").append(attribute1.getName());
          }
          comment.append(System.lineSeparator()).append("// is/are added and the attributes");
          for (ASTCDAttribute attribute1 : attribute.b.getDeletedAttributes()) {
            comment.append(System.lineSeparator()).append("// ").append(attribute1.getName());
          }
          comment
              .append(System.lineSeparator())
              .append("// is/are deleted in ")
              .append(attribute.a.getSymbol().getInternalQualifiedName());
          Optional<ASTODArtifact> astodArtifact = generateArtifact(attribute.a, comment);
          if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact.get());
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact.isPresent() && diffLimit == 0) {
            artifactList.add(astodArtifact.get());
          }
        }
      }
      if (typeDiffStruct.getAddedAttributes() != null
          && !typeDiffStruct.getAddedAttributes().isEmpty()) {
        for (Pair<ASTCDClass, List<ASTCDAttribute>> attribute :
            typeDiffStruct.getAddedAttributes()) {
          StringBuilder comment =
              new StringBuilder(
                  "// Because of the class "
                      + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                      + " the attribute/s ");
          for (ASTCDAttribute attribute1 : attribute.b) {
            comment.append(System.lineSeparator()).append("// ").append(attribute1.getName());
          }
          comment
              .append(System.lineSeparator())
              .append("// is/are added in ")
              .append(attribute.a.getSymbol().getInternalQualifiedName());
          Optional<ASTODArtifact> astodArtifact = generateArtifact(attribute.a, comment);
          if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact.get());
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact.isPresent() && diffLimit == 0) {
            artifactList.add(astodArtifact.get());
          }
        }
      }
      if (typeDiffStruct.getMemberDiff() != null) {
        for (Pair<ASTCDClass, ASTCDAttribute> attribute : typeDiffStruct.getMemberDiff()) {
          StringBuilder comment =
              new StringBuilder(
                  "// Because of the class "
                      + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                      + " the attribute "
                      + attribute.b.getName()
                      + " is changed in "
                      + attribute.a.getSymbol().getInternalQualifiedName());
          comment
              .append(System.lineSeparator())
              .append(" from ")
              .append(getOldAtt(attribute.b, typeDiffStruct).getMCType().printType())
              .append(" to ")
              .append(attribute.b.getMCType().printType());
          Optional<ASTODArtifact> astodArtifact = generateArtifact(attribute.a, comment);
          if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact.get());
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact.isPresent() && diffLimit == 0) {
            artifactList.add(astodArtifact.get());
          }
        }
      }
      if (typeDiffStruct.getChangedStereotype()) {
        String comment =
            "// In the class "
                + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                + " the stereotype is changed from abstract";
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForClass(typeDiffStruct.getAstcdType()),
                generateElements(typeDiffStruct.getAstcdType(), comment, null));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
      if (typeDiffStruct.getDeletedAttributes() != null
          && !typeDiffStruct.getDeletedAttributes().isEmpty()) {
        for (Pair<ASTCDClass, List<ASTCDAttribute>> attribute :
            typeDiffStruct.getDeletedAttributes()) {
          StringBuilder comment =
              new StringBuilder(
                  "// Because of the class "
                      + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                      + " the attribute/s ");
          for (ASTCDAttribute attribute1 : attribute.b) {
            comment.append(System.lineSeparator()).append("// ").append(attribute1.getName());
          }
          comment
              .append(System.lineSeparator())
              .append("// is/are deleted in ")
              .append(attribute.a.getSymbol().getInternalQualifiedName());
          Optional<ASTODArtifact> astodArtifact = generateArtifact(attribute.a, comment);
          if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact.get());
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact.isPresent() && diffLimit == 0) {
            artifactList.add(astodArtifact.get());
          }
        }
      }
      if (typeDiffStruct.isOnlySingletonChanged()) {
        StringBuilder comment =
            new StringBuilder(
                "// In the class "
                    + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                    + " the class is changed from singleton to non-singleton");
        if (typeDiffStruct.isChangedSingleton()) {
          Optional<ASTODArtifact> astodArtifact2;
          astodArtifact2 =
              generateArtifact(
                  oDTitleForClass(typeDiffStruct.getAstcdType()),
                  generateElements(typeDiffStruct.getAstcdType(), comment.toString()));
          if (astodArtifact2.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact2.get());
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact2.isPresent() && diffLimit == 0) {
            artifactList.add(astodArtifact2.get());
          }
        }
      }
    }

    for (TypeDiffStruct typeDiffStruct : syntaxDiff.changedTypes()) {
      if (typeDiffStruct.getAddedConstants() != null) {
        for (ASTCDEnumConstant constant : typeDiffStruct.getAddedConstants().b) {
          Pair<ASTCDClass, ASTCDAttribute> astcdClass =
              getClassForEnum((ASTCDEnum) typeDiffStruct.getAstcdType());
          if (astcdClass != null) {
            String comment =
                "// In the enum "
                    + typeDiffStruct.getAstcdType().getSymbol().getInternalQualifiedName()
                    + " the following constant is added: "
                    + constant.getName()
                    + ".";
            Optional<ASTODArtifact> astodArtifact =
                generateArtifact(
                    oDTitleForClass(astcdClass.a),
                    generateElements(
                        astcdClass.a, comment, new Pair<>(astcdClass.b, constant.getName())));
            if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
              artifactList.add(astodArtifact.get());
              if (artifactList.size() == diffLimit) {
                return artifactList;
              }
            } else if (astodArtifact.isPresent() && diffLimit == 0) {
              artifactList.add(astodArtifact.get());
            }
          }
        }
      }
    }

    List<AssocMatching> assocDiffs = syntaxDiff.getAssocDiffs();
    for (AssocMatching assocMatching : assocDiffs) {
      if (!assocMatching.getNotMatchedAssocsInSrc().isEmpty()
          && !assocMatching.getNotMatchedAssocsInTgt().isEmpty()) {
        for (AssocStruct assocStruct : assocMatching.getNotMatchedAssocsInSrc()) {
          StringBuilder comment =
              new StringBuilder(
                  "// The class "
                      + assocMatching.getClassToInstantiate().getSymbol().getInternalQualifiedName()
                      + " has an association that isn't in srcCD: "
                      + Syn2SemDiffHelper.getConnectedTypes(
                              assocStruct.getAssociation(), helper.getSrcCD())
                          .a
                          .getSymbol()
                          .getInternalQualifiedName()
                      + " - "
                      + Syn2SemDiffHelper.getConnectedTypes(
                              assocStruct.getAssociation(), helper.getSrcCD())
                          .b
                          .getSymbol()
                          .getInternalQualifiedName()
                      + System.lineSeparator()
                      + "// and association/s that isn't/aren't in tgtCD:");
          for (AssocStruct tgtAssocs : assocMatching.getNotMatchedAssocsInTgt()) {
            comment
                .append(System.lineSeparator())
                .append("// ")
                .append(
                    Syn2SemDiffHelper.getConnectedTypes(
                            tgtAssocs.getAssociation(), helper.getTgtCD())
                        .a
                        .getSymbol()
                        .getInternalQualifiedName())
                .append(" ")
                .append(
                    Syn2SemDiffHelper.getConnectedTypes(
                            tgtAssocs.getAssociation(), helper.getTgtCD())
                        .b
                        .getSymbol()
                        .getInternalQualifiedName());
          }
          Optional<ASTODArtifact> astodArtifact =
              generateArtifact(
                  oDTitleForAssoc(assocStruct.getAssociation(), Arrays.asList(1, 1)),
                  generateElements(
                      assocStruct.getAssociation(), Arrays.asList(1, 1), comment.toString()));
          if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact.get());
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact.isPresent() && diffLimit == 0) {
            artifactList.add(astodArtifact.get());
          }
        }
      } else if (!assocMatching.getNotMatchedAssocsInSrc().isEmpty()) {
        for (AssocStruct assocStruct : assocMatching.getNotMatchedAssocsInSrc()) {
          String comment =
              "// The class "
                  + assocMatching.getClassToInstantiate().getSymbol().getInternalQualifiedName()
                  + " has an association that isn't in tgtCD: "
                  + Syn2SemDiffHelper.getConnectedTypes(
                          assocStruct.getAssociation(), helper.getSrcCD())
                      .a
                      .getSymbol()
                      .getInternalQualifiedName()
                  + " - "
                  + Syn2SemDiffHelper.getConnectedTypes(
                          assocStruct.getAssociation(), helper.getSrcCD())
                      .b
                      .getSymbol()
                      .getInternalQualifiedName()
                  + ".";
          Optional<ASTODArtifact> astodArtifact =
              generateArtifact(
                  oDTitleForAssoc(assocStruct.getAssociation(), Arrays.asList(1, 1)),
                  generateElements(assocStruct.getAssociation(), Arrays.asList(1, 1), comment));
          if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
            artifactList.add(astodArtifact.get());
            if (artifactList.size() == diffLimit) {
              return artifactList;
            }
          } else if (astodArtifact.isPresent() && diffLimit == 0) {
            artifactList.add(astodArtifact.get());
          }
        }
      } else {
        StringBuilder comment =
            new StringBuilder(
                "// The class "
                    + assocMatching.getClassToInstantiate().getSymbol().getInternalQualifiedName()
                    + " has association/s that isn't/aren't in src:");
        for (AssocStruct assocStruct : assocMatching.getNotMatchedAssocsInTgt()) {
          comment
              .append(System.lineSeparator())
              .append("// ")
              .append(
                  Syn2SemDiffHelper.getConnectedTypes(
                          assocStruct.getAssociation(), helper.getTgtCD())
                      .a
                      .getSymbol()
                      .getInternalQualifiedName())
              .append(" - ")
              .append(
                  Syn2SemDiffHelper.getConnectedTypes(
                          assocStruct.getAssociation(), helper.getTgtCD())
                      .b
                      .getSymbol()
                      .getInternalQualifiedName());
        }
        comment.append(".");
        Optional<ASTODArtifact> astodArtifact;
        astodArtifact =
            generateArtifact(
                oDTitleForClass(assocMatching.getClassToInstantiate()),
                generateElements(assocMatching.getClassToInstantiate(), comment.toString(), null));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
    }

    for (AssocDiffStruct assocDiffStruct : syntaxDiff.changedAssoc()) {
      Pair<ASTCDType, ASTCDType> pair =
          Syn2SemDiffHelper.getConnectedTypes(assocDiffStruct.getAssociation(), helper.getSrcCD());
      StringBuilder comment =
          new StringBuilder(
              "// In the association between "
                  + pair.a.getSymbol().getInternalQualifiedName()
                  + " and "
                  + pair.b.getSymbol().getInternalQualifiedName()
                  + " the following is changed: ");
      if (assocDiffStruct.isChangedDir()) {
        comment
            .append(System.lineSeparator())
            .append("// direction - ")
            .append(Syn2SemDiffHelper.getDirection(assocDiffStruct.getAssociation()).toString());
      }
      if (assocDiffStruct.getChangedCard() != null && !assocDiffStruct.getChangedCard().isEmpty()) {
        comment
            .append(System.lineSeparator())
            .append("// cardinalities - ")
            .append(assocDiffStruct.getChangedCard().toString());
      }
      if (assocDiffStruct.getChangedRoleNames() != null
          && !assocDiffStruct.getChangedRoleNames().isEmpty()) {
        comment.append(System.lineSeparator()).append("// role name - ");
        for (Pair<ClassSide, ASTCDRole> pair1 : assocDiffStruct.getChangedRoleNames()) {
          comment
              .append("[")
              .append(pair1.a.toString())
              .append(", ")
              .append(pair1.b.getName())
              .append("] ");
        }
      }
      if (assocDiffStruct.getChangedTgt() != null) {
        comment
            .append(System.lineSeparator())
            .append("// changed target - ")
            .append(assocDiffStruct.getChangedTgt().getSymbol().getInternalQualifiedName());
      }
      if (assocDiffStruct.getChangedSrc() != null) {
        comment
            .append(System.lineSeparator())
            .append("// changed source - ")
            .append(assocDiffStruct.getChangedSrc().getSymbol().getInternalQualifiedName());
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
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForAssoc(assocDiffStruct.getAssociation(), list1),
                generateElements(assocDiffStruct.getAssociation(), list1, comment.toString()));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
        List<Integer> list2 = new ArrayList<>(list);
        list2.set(1, 1);
        Optional<ASTODArtifact> astodArtifact2 =
            generateArtifact(
                oDTitleForAssoc(assocDiffStruct.getAssociation(), list2),
                generateElements(assocDiffStruct.getAssociation(), list2, comment.toString()));
        if (astodArtifact2.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact2.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact2.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact2.get());
        }
      } else {
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForAssoc(assocDiffStruct.getAssociation(), list),
                generateElements(assocDiffStruct.getAssociation(), list, comment.toString()));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
    }

    for (InheritanceDiff inheritanceDiff : syntaxDiff.mergeInheritanceDiffs()) {
      if (!helper.getNotInstClassesSrc().contains(inheritanceDiff.getAstcdClasses().a)) {
        String comment =
            "// For the class "
                + inheritanceDiff.getAstcdClasses().a.getSymbol().getInternalQualifiedName()
                + " the inheritance relations were changed.";
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForClass(inheritanceDiff.getAstcdClasses().a),
                generateElements(inheritanceDiff.getAstcdClasses().a, comment, null));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
    }

    if (analyseOverlapping) {
      for (ASTCDType astcdClass : syntaxDiff.srcExistsTgtNot()) {
        String comment =
            "// In tgtCD the class "
                + astcdClass.getSymbol().getInternalQualifiedName()
                + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.";
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForClass(astcdClass), generateElements(astcdClass, comment, null));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
    }

    if (staDiff) {
      for (ASTCDType astcdClass : syntaxDiff.hasDiffSuper()) {
        String comment =
            "// The class "
                + astcdClass.getSymbol().getInternalQualifiedName()
                + " is part of a different inheritance tree.";
        Optional<ASTODArtifact> astodArtifact =
            generateArtifact(
                oDTitleForClass(astcdClass), generateElements(astcdClass, comment, null));
        if (astodArtifact.isPresent() && diffLimit != 0 && artifactList.size() < diffLimit) {
          artifactList.add(astodArtifact.get());
          if (artifactList.size() == diffLimit) {
            return artifactList;
          }
        } else if (astodArtifact.isPresent() && diffLimit == 0) {
          artifactList.add(astodArtifact.get());
        }
      }
    }
    if (!staDiff) {
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
   * Generate an object diagram for the given class. The class is annotated with <<diff>> and a
   * comment is added. The pair contains the attribute and the name of the enum constant. It is used
   * if the diff-witness is related to an added constant. If the pair is null, then the diff-witness
   * is not related to an added constant.
   *
   * @param astcdClass class that causes the diff.
   * @param comment comment for the diff.
   * @param pair attribute and name of the enum constant.
   * @return list of OD elements.
   */
  public List<ASTODElement> generateElements(
      ASTCDType astcdClass, String comment, Pair<ASTCDAttribute, String> pair) {
    Set<ASTODElement> elements;
    ODGenerator oDHelper;
    if (diffSize == 0) {
      oDHelper =
          new ODGenerator(
              Math.max(
                  helper.getSrcCD().getCDDefinition().getCDClassesList().size(),
                  helper.getTgtCD().getCDDefinition().getCDClassesList().size()),
            helper,
            odGenHelper
            );
    } else {
      oDHelper = new ODGenerator(diffSize, helper, odGenHelper);
    }
    elements = oDHelper.getObjForODGeneral(astcdClass, null, pair, 1, 1).a;
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
    assert matchedObject != null;
    matchedObject.getModifier().getStereotype().addValues(valueBuilder.build());

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    matchedObject.set_PreCommentList(List.of(commentBuilder.build()));

    return new ArrayList<>(elements);
  }

  /**
   * Generate an object diagram for the given association. The association is annotated with
   * <<diff>> and a comment is added.
   *
   * @param association association that causes the diff.
   * @param integers cardinalities for the diff.
   * @param comment comment for the diff.
   * @return list of OD elements.
   */
  public List<ASTODElement> generateElements(
      ASTCDAssociation association, List<Integer> integers, String comment) {
    ODGenerator oDHelper;
    if (diffSize == 0) {
      oDHelper =
          new ODGenerator(
              Math.max(
                  helper.getSrcCD().getCDDefinition().getCDClassesList().size(),
                  helper.getTgtCD().getCDDefinition().getCDClassesList().size()),
              helper,
              odGenHelper
            );
    } else {
      oDHelper = new ODGenerator(diffSize, helper, odGenHelper);
    }
    System.out.println(comment);
    System.out.println(integers);
    Pair<Set<ASTODElement>, Optional<ASTODElement>> pair =
        oDHelper.getObjForODGeneral(null, association, null, integers.get(0), integers.get(1));
    if (pair.a.isEmpty()) {
      return new ArrayList<>();
    }
    Set<ASTODElement> elements;
    elements = pair.a;

    ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
    valueBuilder.setName("diff");
    valueBuilder.setContent("diffAssoc");
    if (pair.b.isPresent() && pair.b.get() instanceof ASTODLink) {
      ((ASTODLink) pair.b.get())
          .setStereotype(OD4ReportMill.stereotypeBuilder().addValues(valueBuilder.build()).build());
    } else
        pair.b.ifPresent(astodElement -> ((ASTODObject) astodElement).getModifier().getStereotype().addValues(valueBuilder.build()));

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    pair.b.get().set_PreCommentList(List.of(commentBuilder.build()));
    return new ArrayList<>(elements);
  }

  /**
   * This is used to generate the diagram for the case when the class is changed from
   * singleton to non-singleton. To reduce the code and make use of existing parameters,
   * the two existing ones cardinalityLeft and cardinalityRight. They are set to -1, as there
   * should be no case where that int is used.
   * @param astcdClass class that causes the diff.
   * @param comment comment for the diff.
   * @return list of OD elements.
   */
  public List<ASTODElement> generateElements(ASTCDType astcdClass, String comment) {
    Set<ASTODElement> elements;
    ODGenerator oDHelper;
    if (diffSize == 0) {
      oDHelper =
          new ODGenerator(
              Math.max(
                  helper.getSrcCD().getCDDefinition().getCDClassesList().size(),
                  helper.getTgtCD().getCDDefinition().getCDClassesList().size()),
              helper,
              odGenHelper
            );
    } else {
      oDHelper = new ODGenerator(diffSize, helper, odGenHelper);
    }
    elements = oDHelper.getObjForODGeneral(astcdClass, null, null, -1, -1).a;
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
    assert matchedObject != null;
    matchedObject.getModifier().getStereotype().addValues(valueBuilder.build());

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    matchedObject.set_PreCommentList(List.of(commentBuilder.build()));

    return new ArrayList<>(elements);
  }

  /**
   * Generate an object diagram for a given diff-witness.
   *
   * @param name name of the OD.
   * @param astodElementList list of OD elements.
   * @return object diagram.
   */
  public static Optional<ASTODArtifact> generateArtifact(
      String name, List<ASTODElement> astodElementList) {
    if (astodElementList.isEmpty()) {
      return Optional.empty();
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
                            .setText(OD4ReportMill.stringLiteralBuilder().setSource("diff").build())
                            .build())
                    .build())
            .build();
    return Optional.ofNullable(
        OD4ReportMill.oDArtifactBuilder().setObjectDiagram(astObjectDiagram).build());
  }

  /**
   * Generate a title for OD for the given association.
   *
   * @param association association that causes the diff.
   * @return title for OD.
   */
  public String oDTitleForAssoc(ASTCDAssociation association, List<Integer> sides) {
    String srcName;
    String tgtName;
    Pair<ASTCDType, ASTCDType> pair =
        Syn2SemDiffHelper.getConnectedTypes(association, helper.getSrcCD());
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
    String stringBuilder = "AssocDiff_" + indexAssoc + "_" + srcName + "_" + tgtName + "_" + sides.toString();
    indexAssoc++;
    return stringBuilder;
  }

  /**
   * Generate a title for OD for the given class.
   *
   * @param astcdClass class that causes the diff.
   * @return title for OD.
   */
  public String oDTitleForClass(ASTCDType astcdClass) {
    String stringBuilder =
        "ClassDiff_"
            + indexClass
            + astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_");
    indexClass++;
    return stringBuilder;
  }

  public Optional<ASTODArtifact> generateArtifact(ASTCDClass astcdClass, StringBuilder comment) {
    Optional<ASTODArtifact> astodArtifact;
    astodArtifact =
        generateArtifact(
            oDTitleForClass(astcdClass), generateElements(astcdClass, comment.toString(), null));
    return astodArtifact;
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
