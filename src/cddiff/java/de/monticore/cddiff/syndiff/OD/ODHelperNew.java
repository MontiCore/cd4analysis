package de.monticore.cddiff.syndiff.OD;

import de.monticore.ast.CommentBuilder;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
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
import org.antlr.v4.runtime.misc.MultiMap;

import java.util.*;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.sameAssociationType;

public class ODHelperNew {
  private int indexClass = 1;
  private int indexAssoc = 1;
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  private final ODBuilder odBuilder = new ODBuilder();
  private Map<ASTCDClass, Integer> map = new HashMap<>();

  private CDSyntaxDiff syntaxDiff;

  private int maxNumberOfClasses = Integer.MAX_VALUE;

  public ODHelperNew() {
  }
  public ODHelperNew(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    this.maxNumberOfClasses = Math.max(helper.getSrcCD().getCDDefinition().getCDClassesList().size(), helper.getTgtCD().getCDDefinition().getCDClassesList().size());
  }

  public static Set<ASTODObject> findUnprocessedObjects(Set<Package> packages) {
    Map<ASTODObject, Set<Boolean>> unprocessedMap = new HashMap<>();

    for (Package pack : packages) {
      if (pack.getLeftObject() != null) {
        unprocessedMap.computeIfAbsent(pack.getLeftObject(), k -> new HashSet<>()).add(pack.isProcessedLeft());
      }
      if (pack.getRightObject() != null) {
        unprocessedMap.computeIfAbsent(pack.getRightObject(), k -> new HashSet<>()).add(pack.isProcessedRight());
      }
    }

    Set<ASTODObject> unprocessedObjects = new HashSet<>();
    for (Map.Entry<ASTODObject, Set<Boolean>> entry : unprocessedMap.entrySet()) {
      if (!entry.getValue().contains(true) && entry.getValue().contains(false)) { // Object unprocessed in only one side
        unprocessedObjects.add(entry.getKey());
      }
    }
    return unprocessedObjects;
  }

  public static Set<ASTODObject> findProcessedObjects(Set<Package> packages){
Map<ASTODObject, Set<Boolean>> processedMap = new HashMap<>();

    for (Package pack : packages) {
      if (pack.getLeftObject() != null) {
        processedMap.computeIfAbsent(pack.getLeftObject(), k -> new HashSet<>()).add(pack.isProcessedLeft());
      }
      if (pack.getRightObject() != null) {
        processedMap.computeIfAbsent(pack.getRightObject(), k -> new HashSet<>()).add(pack.isProcessedRight());
      }
    }

    Set<ASTODObject> processedObjects = new HashSet<>();
    for (Map.Entry<ASTODObject, Set<Boolean>> entry : processedMap.entrySet()) {
      if (entry.getValue().contains(true)) {
        processedObjects.add(entry.getKey());
      }
    }

    return processedObjects;
  }

  public Set<Pair<Package, ClassSide>> getContainingPackages(ASTODObject astodObject, Set<Package> objectSet) {
    Set<Pair<Package, ClassSide>> containingPackages = new HashSet<>();
    for (Package pack : objectSet) {
      if (pack.getLeftObject() == astodObject) {
        containingPackages.add(new Pair<>(pack, ClassSide.Left));
      } else if (pack.getRightObject() == astodObject) {
        containingPackages.add(new Pair<>(pack, ClassSide.Right));
      }
    }
    return containingPackages;
  }

  public Set<Package> createChains(ASTCDAssociation association, int cardinalityLeft, int cardinalityRight){
    Set<Package> objectSet = new HashSet<>();
    if (cardinalityLeft == 1 && cardinalityRight == 1){
      Package pack = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a),
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b),
        association, null, false, false);
      objectSet.add(pack);
    } else if (cardinalityLeft == 2 && cardinalityRight == 1) {
      Package pack1 = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a),
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b),
        association, null, false, false);
      Package pack2 = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a),
        pack1.getRightObject(),
        association, null, false, false);
      objectSet.add(pack1);
      objectSet.add(pack2);
    } else if (cardinalityLeft == 1 && cardinalityRight == 2) {
      Package pack1 = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a),
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b),
        association, null, false, false);
      Package pack2 = new Package(pack1.getLeftObject(),
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b),
        association, null, false, false);
      objectSet.add(pack1);
      objectSet.add(pack2);
    }
    return objectSet;
  }

  //Get objects for class
  public Set<ASTODElement> getObjForOD(ASTCDClass astcdClass) {
    Set<ASTODElement> set = new HashSet<>();
    Map<ASTODObject, AssocStruct> map = new HashMap<>();
    Set<Package> packages = createChainsForNewClass(astcdClass, new HashSet<>(), map);
    if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()){
      return null;
    }
    while (!findUnprocessedObjects(packages).isEmpty()) {
      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
        packages.addAll(createChainsForExistingObj(astodObject, packages));
      }
      if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()){
        return null;
      }
    }
    map.clear();
    for (Package pack : packages) {
      //unfold packages into set
      if (pack.getAssociation() != null) {
        set.add(pack.getAssociation());
        set.add(pack.getRightObject());
      }
      set.add(pack.getLeftObject());
    }
    return set;
  }
  //Get objects for association
  public Pair<Set<ASTODElement>, ASTODLink> getObjForOD(ASTCDAssociation association , int cardinalityLeft, int cardinalityRight) {
    Set<ASTODElement> set = new HashSet<>();
    Set<Package> packages = createChains(association, cardinalityLeft, cardinalityRight);
    if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()){
      return null;
    }
    ASTODLink link = packages.iterator().next().getAssociation();
    while (!findUnprocessedObjects(packages).isEmpty()) {
      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
        packages.addAll(createChainsForExistingObj(astodObject, packages));
      }
      if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()){
        return null;
      }
    }
    map.clear();
    for (Package pack : packages) {
      //unfold packages into set
      if (pack.getAssociation() != null) {
        set.add(pack.getAssociation());
        set.add(pack.getRightObject());
      }
      set.add(pack.getLeftObject());
    }
    return new Pair<>(set, link);
  }

  public Set<Package> createChainsForNewClass(ASTCDClass astcdClass, Set<Package> packages, Map<ASTODObject, AssocStruct> map) {
    ASTODObject srcObject = odBuilder.buildObj(getNameForClass(astcdClass), astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"),
      helper.getSuperClasses(astcdClass),
      helper.getAttributesOD(astcdClass));
    if (helper.getSrcMap().get(astcdClass).isEmpty()){
      Package pack = new Package(srcObject);
      packages.add(pack);
    }
    boolean mustHaveAdded = false;
    boolean hasAdded = false;
    for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)) {
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        mustHaveAdded = true;
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {

          tgtObject = getTgtObject(astcdClass, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, map);
        }
        if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = getSubTgtObject(astcdClass, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, map);
        }
        if (tgtObject == null
          && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b));
        } else if (tgtObject == null && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b) != null) {
          tgtObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)));
        }
        if (tgtObject != null) {
          hasAdded = true;
          map.put(tgtObject, assocStruct);
          Package pack = new Package(srcObject, tgtObject, assocStruct.getAssociation(), ClassSide.Left, false, false);
          packages.add(pack);
        }

      }

      else if (assocStruct.getSide().equals(ClassSide.Right)
        && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        mustHaveAdded = true;
        if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = getTgtObject(astcdClass, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, map);
        }
        if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = getSubTgtObject(astcdClass, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, map);
        }
        if (tgtObject == null
          && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a));
        } else if (tgtObject == null
          && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a) != null) {
          tgtObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)));
        }
        if (tgtObject != null) {
          hasAdded = true;
          map.put(tgtObject, assocStruct);
          Package pack = new Package(tgtObject, srcObject, assocStruct.getAssociation(), ClassSide.Right, false, true);
          packages.add(pack);
        }
      }
    }
    if (mustHaveAdded && !hasAdded) {
      Package pack = new Package(srcObject);
      packages.add(pack);
    }
    for (AssocStruct assocStruct : getTgtAssocs(astcdClass, packages)){
      ASTODObject realSrcObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(astcdClass, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, packages);
        } else if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          realSrcObject = getSubRealSrc(astcdClass, helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b), packages);
        }

        if (realSrcObject == null && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          realSrcObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a));
        } else if (realSrcObject == null && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a) != null) {
          realSrcObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)));
        }

        if (realSrcObject != null) {
          map.put(realSrcObject, assocStruct);
          Package pack = new Package(realSrcObject, srcObject, assocStruct.getAssociation(), ClassSide.Right, false, true);
          packages.add(pack);
        }

      } else {
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()){
          realSrcObject = getRealSrc(astcdClass, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, packages);
        } else if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()){
          realSrcObject = getSubRealSrc(astcdClass, helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a), packages);
        }

        if (realSrcObject == null && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()){
          realSrcObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b));
        } else if (realSrcObject == null && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b) != null) {
          realSrcObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)));
        }

        if (realSrcObject != null) {
          map.put(realSrcObject, assocStruct);
          Package pack = new Package(srcObject, realSrcObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
          packages.add(pack);
        }
        }
      }
    return packages;
  }

  public Set<Package> createChainsForExistingObj(ASTODObject object, Set<Package> packages){
    return packages;
  }

  public ASTODObject getTgtObject(ASTCDClass srcClass, ASTCDClass tgtToFind, Map<ASTODObject, AssocStruct> map){
    List<ASTODObject> typeObjects =  getObjectsOfType(tgtToFind, map);
    return null;
  }

  public ASTODObject getSubTgtObject(ASTCDClass srcClass, ASTCDClass tgtToFind, Map<ASTODObject, AssocStruct> map){
    List<ASTODObject> typeObjects =  getObjectsOfType(tgtToFind, map);
    return null;
  }

  public ASTODObject getRealSrc(ASTCDClass srcClass, ASTCDClass srcToFind, Set<Package> packages){
    for (Package pack : packages){
      if (pack.getLeftObject() != null && pack.getLeftObject().getMCObjectType().printType().equals(srcToFind.getSymbol().getInternalQualifiedName())){
        return pack.getLeftObject();
      }
    }
    return null;
  }

  public List<AssocStruct> getTgtAssocs(ASTCDClass astcdClass, Set<Package> packages){
    List<AssocStruct> assocStructs = new ArrayList<>();
    for (Package pack : packages){
      if (pack.getLeftObject() != null && pack.getLeftObject().getMCObjectType().printType().equals(astcdClass.getSymbol().getInternalQualifiedName())){
      }
    }
    return assocStructs;
  }

  public ASTODObject getSubRealSrc(ASTCDClass srcClass, ASTCDClass srcToFind, Set<Package> packages){
    for (Package pack : packages){
      if (pack.getLeftObject() != null && pack.getLeftObject().getMCObjectType().printType().equals(srcToFind.getSymbol().getInternalQualifiedName())){
        return pack.getLeftObject();
      }
    }
    return null;
  }

  public List<ASTODObject> getObjectsOfType(ASTCDClass astcdClass, Map<ASTODObject, AssocStruct> map){
    List<ASTODObject> objects = new ArrayList<>();
    for (ASTODObject astodObject : map.keySet()){
      if (astodObject.getMCObjectType().printType().equals(astcdClass.getSymbol().getInternalQualifiedName())){
        objects.add(astodObject);
      }
    }
    return objects;
  }

  //TODO: if an association cannot be instatiated with the given classes, then it should be instantiated with the minDiffWitness
  //create such association
  public List<ASTODArtifact> generateODs(
    ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, boolean staDiff){
    //CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    List<ASTODArtifact> artifactList = new ArrayList<>();
    for (ASTCDAssociation association : syntaxDiff.addedAssocList()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
      if (!helper.getNotInstanClassesSrc().contains(pair.a) && !helper.getNotInstanClassesSrc().contains(pair.b)) {
        ASTCDClass leftClass = pair.a;
        ASTCDClass rightClass = pair.b;
        if (pair.a.getModifier().isAbstract()) {
          leftClass = helper.minDiffWitness(pair.a);
        }
        if (pair.b.getModifier().isAbstract()) {
          rightClass = helper.minDiffWitness(pair.b);
        }
        String comment = "A new associations has been added to the diagram."
          + "\nThis association allows a new relation between the classes" + pair.a.getSymbol().getInternalQualifiedName() + "and" + pair.b.getSymbol().getInternalQualifiedName() + "and their subclasses";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(association),
          generateElements(association, Arrays.asList(1, 1) , comment),
          null);
        artifactList.add(astodArtifact);
      }
    }

    for (ASTCDClass astcdClass1 : syntaxDiff.addedClassList()){
      ASTCDClass astcdClass = astcdClass1;
      if (astcdClass.getModifier().isAbstract()){
        astcdClass = helper.minDiffWitness(astcdClass);
      }
      String comment = "A new class " + astcdClass1.getSymbol().getInternalQualifiedName() + " has been added to the diagram that is not abstract and couldn't be matched with any of the old classes.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass1),
        generateElements(astcdClass1, comment),
        null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDAssociation, ASTCDClass> pair : syntaxDiff.deletedAssocList()){
      ASTCDClass astcdClass = pair.b;
      if (astcdClass.getModifier().isAbstract()){
        astcdClass = helper.minDiffWitness(astcdClass);
      }
      String comment = "An association for the class " + pair.b.getSymbol().getInternalQualifiedName()  + " has been removed from the diagram.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(pair.b),
        generateElements(pair.b, comment),
        null);
      artifactList.add(astodArtifact);
    }

    for (ASTCDClass astcdClass : syntaxDiff.srcExistsTgtNot()){
      String comment = "In tgtCD the class" + astcdClass.getSymbol().getInternalQualifiedName() + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, comment),
        null);
      artifactList.add(astodArtifact);
    }

    //implement a function that
    for (TypeDiffStruc typeDiffStruc : syntaxDiff.changedTypes()){
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
          astodArtifact = generateArtifact(oDTitleForClass((ASTCDClass)typeDiffStruc.getAstcdType()),
            generateElements((ASTCDClass) typeDiffStruc.getAstcdType(), comment.toString()),
            null);
        artifactList.add(astodArtifact);
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
    for (TypeDiffStruc typeDiffStruc : syntaxDiff.changedTypes()){
      if (typeDiffStruc.getAddedConstants() != null){
        for (ASTCDEnumConstant constant : typeDiffStruc.getAddedConstants().b){
          ASTCDClass astcdClass = getClassForEnum((ASTCDEnum) typeDiffStruc.getAstcdType());
          if (astcdClass != null){
            String comment = "In the enum " + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName() + " the following constant is added: " + constant.getName();
            ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
              generateElements(astcdClass, comment),
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
      if (assocDiffStruc.getChangedCard() != null
        && !assocDiffStruc.getChangedCard().isEmpty()){
        comment = comment + "\ncardinalities - " + assocDiffStruc.getChangedCard().toString();
      }
      if (assocDiffStruc.getChangedRoleNames() != null
        && !assocDiffStruc.getChangedRoleNames().isEmpty()){
        comment = comment + "\nrole name - " + assocDiffStruc.getChangedRoleNames().toString();
      }
      if (assocDiffStruc.getChangedTgt() != null){
        comment = comment + "\nchanged target - " + assocDiffStruc.getChangedTgt().getSymbol().getInternalQualifiedName();
      }
      ArrayList<Integer> list = new ArrayList<>();
      if (assocDiffStruc.getChangedCard() != null
        && assocDiffStruc.getChangedCard().isEmpty()){
        list.add(1);
        list.add(1);
      }
      else if (assocDiffStruc.getChangedCard() != null
        && assocDiffStruc.getChangedCard().size() == 1){
        list.add(assocDiffStruc.getChangedCard().get(0).b);
        list.add(1);
      }
      else if (assocDiffStruc.getChangedCard() != null){
        list.add(assocDiffStruc.getChangedCard().get(0).b);
        list.add(assocDiffStruc.getChangedCard().get(1).b);
      }
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(assocDiffStruc.getAssociation()),
        generateElements(assocDiffStruc.getAssociation(), list, comment),
        null);
      artifactList.add(astodArtifact);
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
        artifactList.add(astodArtifact);
      }
      if (syntaxDiff.helper.srcNotTgtExists(assocDiff.getSrcElem(), assocDiff.getTgtElem())) {
        String comment = "An association between the classes "
          + Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          + " and " + Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          + " has been removed from the diagram.";
        Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), syntaxDiff.helper.getSrcCD());
        if (assocDiff.getSrcElem().getCDAssocDir().isDefinitiveNavigableRight()){
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(pair.a),
            generateElements(pair.a, comment),
            null);
          artifactList.add(astodArtifact);
        }
        if (assocDiff.getSrcElem().getCDAssocDir().isDefinitiveNavigableLeft()){
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(pair.b),
            generateElements(pair.b, comment),
            null);
          artifactList.add(astodArtifact);
        }
      }
    }

    if (staDiff){
      for (ASTCDClass astcdClass : syntaxDiff.getSTADiff()){
        String comment = "The class " + astcdClass.getSymbol().getInternalQualifiedName() + " is part of a different inheritance tree.";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
          generateElements(astcdClass, comment),
          null);
        artifactList.add(astodArtifact);
      }

      for (InheritanceDiff inheritanceDiff : syntaxDiff.mergeInheritanceDiffs()) {
        if (!helper.getNotInstanClassesSrc().contains(inheritanceDiff.getAstcdClasses().a)) {
          ASTCDClass astcdClass = inheritanceDiff.getAstcdClasses().a;
          if (inheritanceDiff.getAstcdClasses().a.getModifier().isAbstract()) {
            astcdClass = helper.minDiffWitness(inheritanceDiff.getAstcdClasses().a);
          }
          String comment = "For the class " + inheritanceDiff.getAstcdClasses().a.getSymbol().getInternalQualifiedName() + " the inheritance relations were changed";
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(inheritanceDiff.getAstcdClasses().a),
            generateElements(inheritanceDiff.getAstcdClasses().a, comment),
            null);
          artifactList.add(astodArtifact);
        }
      }
    }
    return artifactList;
  }

  //TODO: add "diff" and instanceof to stereotype
  //TODO:
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
          if (attribute.getMCType().printType().equals(astcdEnum.getName())) {
            return astcdClass;
          }
        }
      }
    }
    return null;
  }
  public List<ASTODElement> generateElements(ASTCDClass astcdClass,
                                             String comment){
    Set<ASTODElement> elements;
    elements = getObjForOD(astcdClass);
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
                                             String comment){
    Pair<Set<ASTODElement>, ASTODLink> pair = getObjForOD(association, integers.get(0), integers.get(1));
    Set<ASTODElement> elements;
    elements = pair.a;

    ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
    valueBuilder.setName("diff");
    valueBuilder.setContent("diffAssoc");
    pair.b.getStereotype().addValues(valueBuilder.build());

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    pair.b.set_PostCommentList(List.of(commentBuilder.build()));
    return new ArrayList<>(elements);
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

  public String getNameForClass(ASTCDClass astcdClass){
    map.putIfAbsent(astcdClass, 0);
    map.put(astcdClass, map.get(astcdClass) + 1);
    return astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_") + map.get(astcdClass);
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
