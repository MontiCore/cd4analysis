package de.monticore.cddiff.syndiff.OD;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.datastructures.InheritanceDiff;
import de.monticore.cddiff.syndiff.datastructures.TypeDiffStruc;
import de.monticore.cddiff.syndiff.datastructures.AssocDiffStruc;
import de.monticore.cddiff.syndiff.imp.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteralBuilder;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.umlstereotype._ast.ASTStereoValueBuilder;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ODHelper {
  private int indexClass = 1;
  private int indexAssoc = 1;
  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  /**
   * Create a minimal set of associations and classes that are needed for deriving
   * an object diagram for a given class or association
   * @param astcdClass optional
   * @param astcdAssociation optional
   * @return minimal set of objects
   */
  public Set<ASTCDElement> getObjectsForOD(ASTCDClass astcdClass, ASTCDAssociation astcdAssociation){
    Set<ASTCDElement> set = new HashSet<>();
    return createChains(astcdClass, astcdAssociation, set);
  }

  public Set<ASTCDElement> createChains(ASTCDClass astcdClass, ASTCDAssociation astcdAssociation, Set<ASTCDElement> objectSet){
    if (astcdClass != null) {
      if (!objectSet.contains(astcdClass)) {
        objectSet.add(astcdClass);
        List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
        for (AssocStruct pair : list) { //Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair
          if (!objectSet.contains(pair.getAssociation())) {
            switch (pair.getSide()) {
              case Left:
                if (pair.getAssociation().getRight().getCDCardinality().isAtLeastOne()
                  || pair.getAssociation().getRight().getCDCardinality().isOne()) {
                  objectSet.add(pair.getAssociation());
                  objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b, null, objectSet));
                }
              case Right:
                if (pair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
                  || pair.getAssociation().getLeft().getCDCardinality().isOne()) {
                  objectSet.add(pair.getAssociation());
                  objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a, null, objectSet));
                }
            }
          }
        }
      }
    }
    else {
      if (!objectSet.contains(astcdAssociation)) {
        objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, helper.getSrcCD()).a, null, objectSet));
        objectSet.addAll(createChains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, helper.getSrcCD()).b, null, objectSet));
      }
    }
    return objectSet;
  }

  //TODO: number of associations from class to class
  //TODO: add checks if class is abstract - search for subclass (some functions already do this)
  public List<ASTODArtifact> generateODs(
    ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD){
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    List<ASTODArtifact> artifactList = new ArrayList<>();
    for (ASTCDAssociation association : syntaxDiff.addedAssocList()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
      String comment = "A new associations has been added to the diagram."
        + "\nThis association allows a new relation between the classes" + pair.a.getSymbol().getInternalQualifiedName() + "and" + pair.b.getSymbol().getInternalQualifiedName() + "and their subclasses";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(association),
        generateElements(association, null, "", "", "added association", comment),
        null);
      artifactList.add(astodArtifact);
    }

    for (ASTCDClass astcdClass : syntaxDiff.addedClassList()){
      String comment = "A new class " + astcdClass.getSymbol().getInternalQualifiedName() + " has been added to the diagram that is not abstract and couldn't be matched with any of the old classes.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(null, astcdClass, "", "", "", comment),
        null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDAssociation, ASTCDClass> association : syntaxDiff.deletedAssocList()){
      String comment = "The association between the classes" + association.b.getSymbol().getInternalQualifiedName() + "and" + association.b.getSymbol().getInternalQualifiedName() + "has been removed from the diagram.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(association.a),
        generateElements(association.a, null, "", "", "deleted association", comment),
        null);
      artifactList.add(astodArtifact);
    }

    for (InheritanceDiff inheritanceDiff: syntaxDiff.mergeInheritanceDiffs()){
      String comment = "For the class " + inheritanceDiff.getAstcdClasses().a.getSymbol().getInternalQualifiedName() + " the inheritance relations were changed";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(inheritanceDiff.getAstcdClasses().a),
        generateElements(null, inheritanceDiff.getAstcdClasses().a, "", "", "", comment),
        null);
      artifactList.add(astodArtifact);
    }
    for (ASTCDClass astcdClass : syntaxDiff.srcExistsTgtNot()){
      String comment = "In tgtCD the class" + astcdClass.getSymbol().getInternalQualifiedName() + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(null, astcdClass, "", "", "", comment),
        null);
      artifactList.add(astodArtifact);
    }

//    for (Pair<ASTCDClass, Set<ASTCDAttribute>> pair : syntaxDiff.allNewAttributes()){
//      String comment = "In srcCD the class" + pair.a + " is a now a new subclass of at least one other and because of that it has the following new attributes: "
//        +"\n" + pair.b.toString();
//      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.TGT_NOT_INSTANTIATABLE), generateElements(null, pair.a, "", "", "", comment), null);
//      artifactList.add(astodArtifact);
//    }

    //implement a function that
    for (TypeDiffStruc typeDiffStruc : syntaxDiff.changedTypes()){
      if (!typeDiffStruc.getAstcdType().getModifier().isAbstract()) {
        StringBuilder comment = new StringBuilder("In the class " + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName() + " the following is changed: ");
        if (typeDiffStruc.getAddedAttributes() != null) {
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
                .printType()).append(" to ")
              .append(attribute.printType());
          }
        }
        if (typeDiffStruc.getChangedStereotype() != null) {
          comment.append("\nchanged stereotype - ");
        }
        if (typeDiffStruc.getDeletedAttributes() != null) {
          comment.append("\ndeleted attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruc.getDeletedAttributes().b) {
            comment.append(attribute.getName());
          }
        }
        ASTODArtifact astodArtifact;
          astodArtifact = generateArtifact(oDTitleForClass((ASTCDClass)typeDiffStruc.getAstcdType()),
            generateElements(null, (ASTCDClass) typeDiffStruc.getAstcdType(), "", "", "", comment.toString()),
            null);
        artifactList.add(astodArtifact);
      }
      else {
         ASTCDClass subClass = helper.minDiffWitness((ASTCDClass) typeDiffStruc.getAstcdType());
         if (subClass != null){
           StringBuilder comment = new StringBuilder("For the abstract class "
             + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName()
             + " the following is changed: ");
           if (typeDiffStruc.getAddedAttributes() != null) {
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
                 .append(getOldAtt(attribute, typeDiffStruc).printType())
                 .append(" to ")
                 .append(attribute.printType());
             }
           }
           if (typeDiffStruc.getDeletedAttributes() != null) {
             comment.append("\ndeleted attributes - ");
             for (ASTCDAttribute attribute : typeDiffStruc.getDeletedAttributes().b) {
               comment.append(attribute.getName());
             }
           }
           ASTODArtifact astodArtifact;
             astodArtifact = generateArtifact(oDTitleForClass(subClass),
               generateElements(null, subClass, "", "", "", comment.toString()), null);
           artifactList.add(astodArtifact);
         }
      }
    }

    //implement a function that searches for an instantiatable class with enum attribute - done
    for (TypeDiffStruc typeDiffStruc : syntaxDiff.changedTypes()){
      if (typeDiffStruc.getAddedConstants() != null){
        for (ASTCDEnumConstant constant : typeDiffStruc.getAddedConstants().b){
          ASTCDClass astcdClass = getClassForEnum((ASTCDEnum) typeDiffStruc.getAstcdType());
          if (astcdClass != null){
            String comment = "In the enum " + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName() + " the following constant is added: " + constant.getName();
            ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
              generateElements(null, astcdClass, "", "", "", comment),
              null);
            artifactList.add(astodArtifact);
          }
        }
      }
    }

    for (AssocDiffStruc assocDiffStruc : syntaxDiff.changedAssoc()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiffStruc.getAssociation(), srcCD);
      String comment = "In the association between " + pair.a.getSymbol().getInternalQualifiedName() + " and " + pair.b.getSymbol().getInternalQualifiedName() + " the following is changed: ";
      if (assocDiffStruc.isChangedDir()){
        comment = comment + "\ndirection - " + Syn2SemDiffHelper.getDirection(assocDiffStruc.getAssociation()).toString();
      }
      if (assocDiffStruc.getChangedCard() != null){
        comment = comment + "\ncardinalities - " + assocDiffStruc.getChangedCard().toString();
      }
      if (assocDiffStruc.getChangedRoleNames() != null){
        comment = comment + "\nrole name - " + assocDiffStruc.getChangedRoleNames().toString();
      }
      if (assocDiffStruc.getChangedTgt() != null){
        comment = comment + "\nchanged target - " + assocDiffStruc.getChangedTgt().getSymbol().getInternalQualifiedName();
      }
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(assocDiffStruc.getAssociation()),
        generateElements(assocDiffStruc.getAssociation(), null, "", "", "", comment),
        null);
      artifactList.add(astodArtifact);
    }
    return artifactList;
  }
  //add function for STA semantics - done
  //TODO: add "diff" and instanceof to stereotype
  private ASTCDAttribute getOldAtt(ASTCDAttribute attribute, TypeDiffStruc diffStruc){
    for (Pair<ASTCDAttribute, ASTCDAttribute> pair : diffStruc.getMatchedAttributes()){
      if (pair.a.equals(attribute)){
        return pair.b;
      }
    }
    return null;
  }
  private ASTCDClass getClassForEnum(ASTCDEnum astcdEnum){
    for (ASTCDClass astcdClass : helper.getSrcCD().getCDDefinition().getCDClassesList()) {
      if (!astcdClass.getModifier().isAbstract()) {
        List<ASTCDAttribute> attributes = helper.getAllAttr(astcdClass).b;
        for (ASTCDAttribute attribute : attributes) {
          if (attribute.printType().equals(astcdEnum.getName())) {
            return astcdClass;
          }
        }
      }
    }
    return null;
  }
  public List<ASTODElement> generateElements(ASTCDAssociation association,
                                             ASTCDClass astcdClass,
                                             String content,
                                             String name,
                                             String text,
                                             String comment){
    Set<ASTCDElement> elements;
    if (association != null){
      elements = getObjectsForOD(null, association);
    } else {
      elements = getObjectsForOD(astcdClass, null);
    }
    Set<ASTCDAssociation> associations = elements.stream()
      .filter(ASTCDAssociation.class::isInstance)
      .map(ASTCDAssociation.class::cast)
      .collect(Collectors.toSet());

    Set<ASTCDClass> classes = elements.stream()
      .filter(ASTCDClass.class::isInstance)
      .map(ASTCDClass.class::cast)
      .collect(Collectors.toSet());

    if (association != null){
      associations.remove(association);
      ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
      valueBuilder.setContent(content);
      valueBuilder.setName(name);
      ASTStringLiteralBuilder literalBuilder = new ASTStringLiteralBuilder();
      valueBuilder.setText(literalBuilder.setSource(text).build());
    } else {
      classes.remove(astcdClass);
      ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
      valueBuilder.setContent(content);
      valueBuilder.setName(name);
      ASTStringLiteralBuilder literalBuilder = new ASTStringLiteralBuilder();
      valueBuilder.setText(literalBuilder.setSource(text).build());
    }

    for (ASTCDClass astcdClass1 : classes){
      //create ASTODObject
      List<ASTODAttribute> attributes;
      for (ASTCDAttribute attribute : helper.getAllAttr(astcdClass1).b){
        //create ASTODAttribute and add to attributes
      }
      //update Object and add to elementSet
    }

    for (ASTCDAssociation astcdAssociation : associations){
      //create ASTODLink and add to elementSet
    }
    Set<ASTODElement> elementSet = new HashSet<>();
    for (ASTODElement element : elementSet){

    }
    return new ArrayList<>(elementSet);
  }
  public static ASTODArtifact generateArtifact(String name, List<ASTODElement> astodElementList, String stereotype){
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
  public ASTODAttribute createAttribute(String type, String name, int value){
    return null;
  }
  public ASTODObject createObject(String id, String type, Collection<String> types, Collection<ASTODAttribute> attrs){
    return null;
  }
  public ASTODLink createLink(ASTODObject srcObj, String roleName, ASTODObject trgObj, String direction){
   return null;
  }

  public String oDTitleForAssoc(ASTCDAssociation association){
    String srcName;
    String tgtName;
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD());
    if (association.getCDAssocDir().isBidirectional()){
      srcName = pair.a.getSymbol().getInternalQualifiedName();
      tgtName = pair.b.getSymbol().getInternalQualifiedName();
    }
    else {
      if (association.getCDAssocDir().isDefinitiveNavigableLeft()){
        srcName = pair.b.getSymbol().getInternalQualifiedName();
        tgtName = pair.a.getSymbol().getInternalQualifiedName();
      }
      else {
        srcName = pair.a.getSymbol().getInternalQualifiedName();
        tgtName = pair.b.getSymbol().getInternalQualifiedName();
      }
    }
    String stringBuilder = "AssocDiff_" + indexAssoc + srcName + "_" + tgtName;
    indexAssoc++;
    return stringBuilder;
  }
  public String oDTitleForClass(ASTCDClass astcdClass){
    String stringBuilder = "ClassDiff_" + indexClass + astcdClass.getSymbol().getInternalQualifiedName();
    indexClass++;
    return stringBuilder;
  }
  public static String printOD(ASTODArtifact astodArtifact) {
    // pretty print the AST
    return OD4ReportMill.prettyPrint(astodArtifact, true);
  }
  public static List<String> printODs(List<ASTODArtifact> astODArtifacts) {
    // pretty print the AST
    List<String> result = new ArrayList<>();
    for (ASTODArtifact od : astODArtifacts) {
      result.add(OD4ReportMill.prettyPrint(od, true));
    }
    return result;
  }
}
