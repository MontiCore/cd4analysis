package de.monticore.cddiff.syndiff.OD;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cddiff.syndiff.AssocStruct;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.imp.*;
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
  private CDSyntaxDiff syntaxDiff;
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

//  /**
//   * Find all differences (with additional information) in a pair of changed types
//   *
//   * @param typeDiff pair of new and old type
//   * @return list of changes with information about it
//   */
//  public List<Pair<ASTCDClass, Object>> findTypeDiff(CDTypeDiff typeDiff){
//    List<Pair<ASTCDClass, Object>> list = new ArrayList<>();
//    for (DiffTypes types : typeDiff.getBaseDiffs()){
//      switch (types){
//        case CHANGED_ATTRIBUTE: if (typeDiff.changedAttribute(getSrcCD()) != null){ for (Pair<ASTCDClass, ASTCDAttribute> pair : typeDiff.changedAttribute(getSrcCD())){list.add(new Pair<>(pair.a, pair.b));} }
//        case STEREOTYPE_DIFFERENCE: if (typeDiff.isClassNeeded() != null){ list.add(new Pair<>((ASTCDClass) typeDiff.isClassNeeded(), null)); }
//        case REMOVED_ATTRIBUTE: for (Pair<ASTCDClass, ASTCDAttribute> pair : typeDiff.deletedAttributes(getSrcCD())){list.add(new Pair<>(pair.a, pair.b));}
//        case ADDED_ATTRIBUTE: for (Pair<ASTCDClass, ASTCDAttribute> pair : typeDiff.addedAttributes(getSrcCD())){list.add(new Pair<>(pair.a, pair.b));}
//        case ADDED_CONSTANTS: for (Pair<ASTCDClass, ASTCDEnumConstant> pair : typeDiff.newConstants()){list.add(new Pair<>(pair.a, pair.b));}
//          //other cases?
//      }
//    }
//    return list;
//  }

  public List<ASTODArtifact> generateODs(
    ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD){
    syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    List<ASTODArtifact> artifactList = new ArrayList<>();
    //TODO: when there are multiple changes to a class, they must be shown together!!!
    for (ASTCDAssociation association : syntaxDiff.addedAssocList()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
      String comment = "A new associations has been added to the diagram."
        + "\nThis association allows a new relation between the classes" + pair.a.getName() + "and" + pair.b.getName() + "and their subclasses";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.ADDED_ASSOCIATION), generateElements(association, null, "", "", "added association", comment), null);
      artifactList.add(astodArtifact);
    }

    for (ASTCDClass astcdClass : syntaxDiff.addedClassList()){
      String comment = "A new class has been added to the diagram that is not abstract and couldn't be matched with any of the old classes.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.ADDED_CLASS), generateElements(null, astcdClass, "", "", "", comment), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, ASTCDAttribute> pair : syntaxDiff.addedAttributeList()){
      String comment = "A new attribute" + pair.b.getName() + " has been added to the class" + pair.a.getName() + "."
        + "\nThis attribute couldn't be found in any of the old superclasses or in all of the old subclasses";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.ADDED_ATTRIBUTE), generateElements(null, pair.a, "", "", "", comment), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, ASTCDAttribute> pair : syntaxDiff.deletedAttributeList()){
      String comment = "The old attribute " + pair.b.getName() + " has been removed from the class" + pair.a.getName() + "."
        + "\nThis attribute couldn't be found in any of the new superclasses or in all of the new subclasses";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.REMOVED_ATTRIBUTE), generateElements(null, pair.a, "", "", "", comment), null);
      artifactList.add(astodArtifact);
    }

    for (EnumStruc enumStruc : syntaxDiff.addedConstantsList()){
      String comment = "In the Enum " + enumStruc.getAttribute().printType() + " the constant " + enumStruc.getEnumConstant().toString() + " has been added."
        + "\nNow we can create an object for example of type " + enumStruc.getAstcdClass().getName() + " where the attribute has the constant";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.ADDED_CONSTANTS), generateElements(null, enumStruc.getAstcdClass(), "", "", "", comment), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, ASTCDAttribute> pair : syntaxDiff.changedAttributeList()){
      String comment = "The type of the attribute" + pair.b.getName() + " in the class" + pair.a.getName() + " has been changed to " + pair.b.printType() + ".";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.CHANGED_ATTRIBUTE), generateElements(null, pair.a, "", "as", "", comment), null);
      artifactList.add(astodArtifact);
    }

    for (ASTCDClass astcdClass : syntaxDiff.srcExistsTgtNot()){
      String comment = "In tgtCD the class" + astcdClass.getName() + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.TGT_NOT_INSTANTIATABLE), generateElements(null, astcdClass, "", "", "", comment), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, Set<ASTCDAttribute>> pair : syntaxDiff.allNewAttributes()){
      String comment = "In srcCD the class" + pair.a + " is a now a new subclass of at least one other and because of that it has the following new attributes: "
        +"\n" + pair.b.toString();
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.TGT_NOT_INSTANTIATABLE), generateElements(null, pair.a, "", "", "", comment), null);
      artifactList.add(astodArtifact);
    }

    for (AssocDiffStruc assocDiffStruc : syntaxDiff.changedAssoc()){
      String comment = "In the association the following is changed: ";
      if (assocDiffStruc.isChangedDir()){
        comment = comment + "\ndirection - " + CDAssocDiff.getDirection(assocDiffStruc.getAssociation()).toString();
      }
      if (assocDiffStruc.getChangedCard() != null){
        comment = comment + "\ncardinalities - " + assocDiffStruc.getChangedCard().toString();
      }
      if (assocDiffStruc.getChangedRoleNames() != null){
        comment = comment + "\nrole name - " + assocDiffStruc.getChangedRoleNames().toString();
      }
      if (assocDiffStruc.getChangedTgt() != null){
        comment = comment + "\nchanged target - " + assocDiffStruc.getChangedTgt().getName();
      }
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.TGT_NOT_INSTANTIATABLE), generateElements(assocDiffStruc.getAssociation(), null, "", "", "", comment), null);
      artifactList.add(astodArtifact);
    }
    return artifactList;
  }
  public List<ASTODElement> generateElements(ASTCDAssociation association, ASTCDClass astcdClass, String content, String name, String text, String comment){
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
      for (ASTCDAttribute attribute : syntaxDiff.getAllAttr(astcdClass1).b){
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
              .setContent(stereotype)
              .setText(
                OD4ReportMill.stringLiteralBuilder().setSource(stereotype).build())
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
  public String oDTitleForAssoc(DiffTypes diffType){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("AssocDiff").append(indexAssoc).append(diffType.toString());
    indexAssoc++;
    return stringBuilder.toString();
  }
  public String oDTitleForClass(DiffTypes diffType){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("ClassDiff").append(indexClass).append(diffType.toString());
    indexClass++;
    return stringBuilder.toString();
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
