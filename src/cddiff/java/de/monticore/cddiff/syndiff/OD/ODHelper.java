package de.monticore.cddiff.syndiff.OD;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cddiff.syndiff.AssocStruct;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.ClassSide;
import de.monticore.cddiff.syndiff.imp.EnumStruc;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  public List<ASTODArtifact> generateODs(
    ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD){
    syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    List<ASTODArtifact> artifactList = new ArrayList<>();

    for (ASTCDAssociation association : syntaxDiff.addedAssocList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.ADDED_ASSOCIATION), generateElements(association, null), null);
      artifactList.add(astodArtifact);
    }

    for (ASTCDClass astcdClass : syntaxDiff.addedClassList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.ADDED_CLASS), generateElements(null, astcdClass), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, ASTCDAttribute> pair : syntaxDiff.addedAttributeList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.ADDED_ATTRIBUTE), generateElements(null, pair.a), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, ASTCDAttribute> pair : syntaxDiff.deletedAttributeList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.REMOVED_ATTRIBUTE), generateElements(null, pair.a), null);
      artifactList.add(astodArtifact);
    }

    for (EnumStruc enumStruc : syntaxDiff.addedConstantsList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.ADDED_CONSTANTS), generateElements(null, enumStruc.getAstcdClass()), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, ASTCDAttribute> pair : syntaxDiff.changedAttributeList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.CHANGED_ATTRIBUTE), generateElements(null, pair.a), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>> pair : syntaxDiff.changedRoleNameList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.CHANGED_ASSOCIATION_ROLE), generateElements(pair.a, null), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDAssociation, Pair<ClassSide, Integer>> pair : syntaxDiff.changedCardinalityList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.CHANGED_ASSOCIATION_MULTIPLICITY), generateElements(pair.a, null), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDAssociation, ASTCDClass> pair : syntaxDiff.changedTargetList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.CHANGED_TARGET), generateElements(pair.a, null), null);
      artifactList.add(astodArtifact);
    }

    for (ASTCDAssociation association : syntaxDiff.changedDirectionList()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(DiffTypes.CHANGED_ASSOCIATION_DIRECTION), generateElements(association, null), null);
      artifactList.add(astodArtifact);
    }

    for (ASTCDClass astcdClass : syntaxDiff.srcExistsTgtNot()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.TGT_NOT_INSTANTIATABLE), generateElements(null, astcdClass), null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDClass, Set<ASTCDAttribute>> pair : syntaxDiff.allNewAttributes()){
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(DiffTypes.TGT_NOT_INSTANTIATABLE), generateElements(null, pair.a), null);
      artifactList.add(astodArtifact);
    }
    return artifactList;
  }
  //TODO: add stereotype and comments - here and in ODBuilder
  public List<ASTODElement> generateElements(ASTCDAssociation association, ASTCDClass astcdClass){
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
  public static ASTODAttribute createAttribute(){
    return null;
  }
  public ASTODObject createObject(){
    return null;
  }
  public ASTODLink createLink(){
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
