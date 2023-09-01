package de.monticore.cddiff.syndiff.OD;

import de.monticore.ast.CommentBuilder;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteralBuilder;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import de.monticore.umlstereotype._ast.ASTStereoValueBuilder;
import de.monticore.umlstereotype._ast.ASTStereotypeBuilder;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;

public class ODHelper {
  private int indexClass = 1;
  private int indexAssoc = 1;
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  private final ODBuilder ODBuilder = new ODBuilder();
  private Map<ASTCDClass, Integer> map = new HashMap<>();

  private final int maxNumberOfClasses = Math.max(helper.getSrcCD().getCDDefinition().getCDClassesList().size(), helper.getTgtCD().getCDDefinition().getCDClassesList().size());

  public static Set<ASTODObject> findUnprocessedObjects(Set<Package> packages) {
    Map<ASTODObject, Set<Boolean>> unprocessedMap = new HashMap<>();

    for (Package pack : packages) {
      if (pack.getSrcClass() != null) {
        unprocessedMap.computeIfAbsent(pack.getSrcClass(), k -> new HashSet<>()).add(pack.isProcessedLeft());
      }
      if (pack.getTgtClass() != null) {
        unprocessedMap.computeIfAbsent(pack.getTgtClass(), k -> new HashSet<>()).add(pack.isProcessedRight());
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
      if (pack.getSrcClass() != null) {
        processedMap.computeIfAbsent(pack.getSrcClass(), k -> new HashSet<>()).add(pack.isProcessedLeft());
      }
      if (pack.getTgtClass() != null) {
        processedMap.computeIfAbsent(pack.getTgtClass(), k -> new HashSet<>()).add(pack.isProcessedRight());
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
      if (pack.getSrcClass() == astodObject) {
        containingPackages.add(new Pair<>(pack, ClassSide.Left));
      } else if (pack.getTgtClass() == astodObject) {
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
        pack1.getTgtClass(),
        association, null, false, false);
      objectSet.add(pack1);
      objectSet.add(pack2);
    } else if (cardinalityLeft == 1 && cardinalityRight == 2) {
      Package pack1 = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a),
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b),
        association, null, false, false);
      Package pack2 = new Package(pack1.getSrcClass(),
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
    Set<Package> packages = createChainsForNewClass(astcdClass, new HashSet<>());
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
        set.add(pack.getTgtClass());
      }
      set.add(pack.getSrcClass());
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
        set.add(pack.getTgtClass());
      }
      set.add(pack.getSrcClass());
    }
    return new Pair<>(set, link);
  }

  public Set<Package> createChainsForNewClass(ASTCDClass astcdClass, Set<Package> objectSet) {
    List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
    ASTODObject srcObject = ODBuilder.buildObj(getNameForClass(astcdClass), astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"),
      helper.getSuperClasses(astcdClass),
      helper.getAttributesOD(astcdClass));
    for (AssocStruct assocStruct : list) {
      boolean mustBeLinked = false;
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        mustBeLinked = true;
        tgtObject = getObjectForTgt(astcdClass, srcObject, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct.getAssociation(), objectSet);
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
        mustBeLinked = true;
        tgtObject = getObjectForTgt(astcdClass, srcObject, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct.getAssociation(), objectSet);
      }
      if (mustBeLinked) {
        Package pack;
        if (tgtObject != null && srcObject != tgtObject) {
          if (assocStruct.getSide().equals(ClassSide.Left)) {
            pack = new Package(srcObject,
              tgtObject,
              assocStruct.getAssociation(), ClassSide.Left, true, false);
          } else {
            pack = new Package(tgtObject,
              srcObject,
              assocStruct.getAssociation(), ClassSide.Right, false, true);
          }
        } else {
          if (assocStruct.getSide().equals(ClassSide.Left)) {
            pack = new Package(srcObject,
              Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b,
              getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
              assocStruct.getAssociation(), ClassSide.Left, true, false);
          } else {
            pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a,
              getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
              srcObject,
              assocStruct.getAssociation(), ClassSide.Right, false, true);
          }
        }
        objectSet.add(pack);
      }
    }
    for (AssocStruct assocStruct : getOtherAssoc(astcdClass)){
      ASTODObject realSrc;
      if (assocStruct.getSide().equals(ClassSide.Left)){
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      else {
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      Package pack;
      if (realSrc != null && srcObject != realSrc){
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(realSrc,
            srcObject,
            assocStruct.getAssociation(), ClassSide.Left, true, false);
        }
        else {
          pack = new Package(srcObject,
            realSrc,
            assocStruct.getAssociation(), ClassSide.Right, false, true);
        }
        objectSet.add(pack);
      }
      else {
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            srcObject,
            assocStruct.getAssociation(), ClassSide.Left, true, false);
        }
        else {
          pack = new Package(srcObject,
            Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            assocStruct.getAssociation(), ClassSide.Right, false, true);
        }
        objectSet.add(pack);
      }
    }
    return objectSet;
  }
  public ASTODObject getObjectForTgt(ASTCDClass srcClass, ASTODObject srcObject, ASTCDClass tgtClass, ASTCDAssociation association, Set<Package> objectSet){
    Set<ASTODObject> processedObjects = findProcessedObjects(objectSet);
    processedObjects.remove(srcObject);
    for (ASTODObject object : processedObjects){
      if (tgtClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssoc(true, object, srcClass, association, getContainingPackages(object, objectSet))){
          return object;
        }
      }
    }
    for (ASTODObject object : findUnprocessedObjects(objectSet)){
      if (tgtClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssoc(false, object, srcClass, association, getContainingPackages(object, objectSet))){
          return object;
        }
      }
    }
    return null;
  }

  public ASTODObject getObjectForSrc(ASTCDClass srcClass, ASTCDClass tgtClass, ASTCDAssociation association, Set<Package> objectSet){
    Set<ASTODObject> processedObjects = findProcessedObjects(objectSet);
    for (ASTODObject object : processedObjects){
      if (srcClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssoc(true, object, tgtClass, association, getContainingPackages(object, objectSet))){
          return object;
        }
      }
    }
    for (ASTODObject object : findUnprocessedObjects(objectSet)){
      if (srcClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssoc(false, object, tgtClass, association, getContainingPackages(object, objectSet))){
          return object;
        }
      }
    }
    return null;
  }

  public boolean objectUsesAssoc(boolean processed, ASTODObject srcObject, ASTCDClass tgt, ASTCDAssociation association, Set<Pair<Package, ClassSide>> containingSet) {
    if (getConnectedClasses(association, helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType())) {
      if (association.getRight().getCDCardinality().isMult() || association.getRight().getCDCardinality().isAtLeastOne()) {
        return false;
      }
    } else if ( getConnectedClasses(association, helper.getSrcCD()).b == helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType())
      && (association.getLeft().getCDCardinality().isMult() || association.getLeft().getCDCardinality().isAtLeastOne())) {
      return false;
    }
    if (!processed) {
      for (Pair<Package, ClassSide> pair : containingSet) {
        if (CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), association) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), association)) {
          if (pair.b == ClassSide.Left && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).b == helper.getCDClass(helper.getSrcCD(), tgt.getName())) {
            return true;
          } else if (pair.b == ClassSide.Right && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), tgt.getName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public Set<Package> createChainsForExistingObj(ASTODObject object, Set<Package> objectSet){
    List<AssocStruct> list = helper.getSrcMap().get(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()));
    Set<Pair<Package, ClassSide>> containingPackages = getContainingPackages(object, objectSet);
    for (Pair<Package, ClassSide> pair : containingPackages){
      list.removeIf(assocStruct -> CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), assocStruct.getAssociation()) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), assocStruct.getAssociation()));
    }
    Iterator<AssocStruct> iterator = list.iterator();
    while (iterator.hasNext()){
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if (assocStruct.getAssociation().getRight().getCDCardinality().isOpt() || assocStruct.getAssociation().getRight().getCDCardinality().isMult()){
          iterator.remove();
        }
      }
      else {
        if (assocStruct.getAssociation().getLeft().getCDCardinality().isOpt() || assocStruct.getAssociation().getLeft().getCDCardinality().isMult()){
          iterator.remove();
        }
      }
    }
    ASTCDClass astcdClass = helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType());
    List<AssocStruct> otherAssoc = getOtherAssoc(astcdClass);
    for (Pair<Package, ClassSide> pair : containingPackages){
      otherAssoc.removeIf(assocStruct -> CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), assocStruct.getAssociation()) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), assocStruct.getAssociation()));
    }
    for (AssocStruct assocStruct : otherAssoc){
      ASTODObject realSrc;
      if (assocStruct.getSide().equals(ClassSide.Left)){
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      else {
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      Package pack;
      if (realSrc != null && object != realSrc){
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(realSrc,
            object,
            assocStruct.getAssociation(), ClassSide.Left, true, false);
        }
        else {
          pack = new Package(object,
            realSrc,
            assocStruct.getAssociation(), ClassSide.Right, false, true);
        }
        objectSet.add(pack);
      }
      else {
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            object,
            assocStruct.getAssociation(), ClassSide.Left, true, false);
        }
        else {
          pack = new Package(object,
            Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            assocStruct.getAssociation(), ClassSide.Right, false, true);
        }
        objectSet.add(pack);
      }
    }
    if (list.isEmpty() && otherAssoc.isEmpty()){
      Package pack = new Package(object);
      objectSet.add(pack);
    } else {
      objectSet.addAll(createChainsHelper(object, list, objectSet));
    }
    return objectSet;
  }

  //TODO: add other assocs
  public Set<Package> createChainsHelper(ASTODObject object, List<AssocStruct> list, Set<Package> objectSet){
    for (AssocStruct assocStruct : list) {
      boolean mustBeLinked = false;
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        mustBeLinked = true;
        tgtObject = getObjectForTgtSpec(object, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct.getAssociation(), objectSet);
      } else if (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
        mustBeLinked = true;
        tgtObject = getObjectForTgtSpec(object, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct.getAssociation(), objectSet);
      }
      if (mustBeLinked) {
        Package pack;
        if (tgtObject != null && object != tgtObject) {
          if (assocStruct.getSide().equals(ClassSide.Left)) {
            pack = new Package(object,
              tgtObject,
              assocStruct.getAssociation(), ClassSide.Left, true, false);
          } else {
            pack = new Package(tgtObject,
              object,
              assocStruct.getAssociation(), ClassSide.Right, false, true);
          }
        } else {
          if (assocStruct.getSide().equals(ClassSide.Left)) {
            pack = new Package(object,
              Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
              assocStruct.getAssociation(), ClassSide.Left, true, false);
          } else {
            pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
              object,
              assocStruct.getAssociation(), ClassSide.Right, false, true);
          }
        }
        objectSet.add(pack);
      }
    }
    return objectSet;
  }

  public ASTODObject getObjectForTgtSpec(ASTODObject src, ASTCDClass tgtClass, ASTCDAssociation association, Set<Package> objectSet){
    Set<ASTODObject> processedObjects = findProcessedObjects(objectSet);
    for (ASTODObject object : processedObjects){
      if (helper.getCDClass(helper.getSrcCD(), tgtClass.getName()) == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssocSpec(true, object, src, helper.getCDClass(helper.getSrcCD(), src.getMCObjectType().printType()), association, getContainingPackages(object, objectSet))){
          return object;
        }
      }
    }
    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
    for (ASTODObject object : unprocessedObjects){
      assert object != null;
      assert helper.getCDClass(helper.getSrcCD(), tgtClass.getName()) != null;
      if (helper.getCDClass(helper.getSrcCD(), tgtClass.getName()) == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssocSpec(false, object, src, helper.getCDClass(helper.getSrcCD(), src.getMCObjectType().printType()), association, getContainingPackages(object, objectSet))){
          return object;
        }
      }
    }
    return null;
  }

  public boolean objectUsesAssocSpec(boolean processed, ASTODObject srcObject, ASTODObject tgtObject, ASTCDClass tgt, ASTCDAssociation association, Set<Pair<Package, ClassSide>> containingSet) {
    if (getConnectedClasses(association, helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType())) {
      if (association.getRight().getCDCardinality().isMult() || association.getRight().getCDCardinality().isAtLeastOne()) {
        return false;
      }
    } else if (association.getLeft().getCDCardinality().isMult() || association.getLeft().getCDCardinality().isAtLeastOne()) {
      return false;
    }
    if (!processed) {
      for (Pair<Package, ClassSide> pair : containingSet) {
        if (CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), association) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), association)) {
          if (pair.b == ClassSide.Left && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).b == helper.getCDClass(helper.getSrcCD(), tgt.getName())) {
            return true;
          } else if (pair.b == ClassSide.Right && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), tgt.getName())) {
            return true;
          }
        }
      }
    }
    return false;
  }
  public List<AssocStruct> getOtherAssoc(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDClass classToCheck : helper.getSrcMap().keySet()) {
      if (classToCheck != astcdClass) {
        for (AssocStruct assocStruct : helper.getSrcMap().get(classToCheck)) {
          if (assocStruct.getSide().equals(ClassSide.Left)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b == astcdClass) {
            list.add(assocStruct);
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a == astcdClass) {
            list.add(assocStruct);
          }
        }
      }
    }
    return list;
  }

  public List<ASTODArtifact> generateODs(
    ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, boolean staDiff){
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
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
          + "\nThis association allows a new relation between the classes" + leftClass.getSymbol().getInternalQualifiedName() + "and" + rightClass.getSymbol().getInternalQualifiedName() + "and their subclasses";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(association),
          generateElements(association, Arrays.asList(1, 1) , "", "", "added association", comment),
          null);
        artifactList.add(astodArtifact);
      }
    }

    for (ASTCDClass astcdClass1 : syntaxDiff.addedClassList()){
      ASTCDClass astcdClass = astcdClass1;
      if (astcdClass.getModifier().isAbstract()){
        astcdClass = helper.minDiffWitness(astcdClass);
      }
      String comment = "A new class " + astcdClass.getSymbol().getInternalQualifiedName() + " has been added to the diagram that is not abstract and couldn't be matched with any of the old classes.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, "", "", "", comment),
        null);
      artifactList.add(astodArtifact);
    }

    for (Pair<ASTCDAssociation, ASTCDClass> pair : syntaxDiff.deletedAssocList()){
      ASTCDClass astcdClass = pair.b;
      if (astcdClass.getModifier().isAbstract()){
        astcdClass = helper.minDiffWitness(astcdClass);
      }
      String comment = "An association for the class " + pair.b.getSymbol().getInternalQualifiedName()  + " has been removed from the diagram.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, "", "", "", comment),
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
          generateElements(astcdClass, "", "", "", comment),
          null);
        artifactList.add(astodArtifact);
      }
    }

    for (ASTCDClass astcdClass : syntaxDiff.srcExistsTgtNot()){
      String comment = "In tgtCD the class" + astcdClass.getSymbol().getInternalQualifiedName() + " cannot be instantiated because of overlapping associations, but it can be instantiated in srcCD.";
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
        generateElements(astcdClass, "", "", "", comment),
        null);
      artifactList.add(astodArtifact);
    }

    //implement a function that
    for (TypeDiffStruc typeDiffStruc : syntaxDiff.changedTypes()){
      if (!typeDiffStruc.getAstcdType().getModifier().isAbstract()) {
        StringBuilder comment = new StringBuilder("In the class " + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName() + " the following is changed: ");
        if (!typeDiffStruc.getAddedAttributes().b.isEmpty()) {
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
        if (!typeDiffStruc.getDeletedAttributes().b.isEmpty()) {
          comment.append("\ndeleted attributes - ");
          for (ASTCDAttribute attribute : typeDiffStruc.getDeletedAttributes().b) {
            comment.append(attribute.getName());
          }
        }
        ASTODArtifact astodArtifact;
          astodArtifact = generateArtifact(oDTitleForClass((ASTCDClass)typeDiffStruc.getAstcdType()),
            generateElements((ASTCDClass) typeDiffStruc.getAstcdType(), "", "", "", comment.toString()),
            null);
        artifactList.add(astodArtifact);
      }
      else {
         ASTCDClass subClass = helper.minDiffWitness((ASTCDClass) typeDiffStruc.getAstcdType());
         if (subClass != null){
           StringBuilder comment = new StringBuilder("For the abstract class "
             + typeDiffStruc.getAstcdType().getSymbol().getInternalQualifiedName()
             + " the following is changed: ");
           if (!typeDiffStruc.getAddedAttributes().b.isEmpty()) {
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
                 .append(getOldAtt(attribute, typeDiffStruc).getMCType().printType())
                 .append(" to ")
                 .append(attribute.getMCType().printType());
             }
           }
           if (!typeDiffStruc.getDeletedAttributes().b.isEmpty()) {
             comment.append("\ndeleted attributes - ");
             for (ASTCDAttribute attribute : typeDiffStruc.getDeletedAttributes().b) {
               comment.append(attribute.getName());
             }
           }
           ASTODArtifact astodArtifact;
             astodArtifact = generateArtifact(oDTitleForClass(subClass),
               generateElements(subClass, "", "", "", comment.toString()), null);
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
              generateElements(astcdClass, "", "", "", comment),
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
      if (!assocDiffStruc.getChangedCard().isEmpty()){
        comment = comment + "\ncardinalities - " + assocDiffStruc.getChangedCard().toString();
      }
      if (!assocDiffStruc.getChangedRoleNames().isEmpty()){
        comment = comment + "\nrole name - " + assocDiffStruc.getChangedRoleNames().toString();
      }
      if (assocDiffStruc.getChangedTgt() != null){
        comment = comment + "\nchanged target - " + assocDiffStruc.getChangedTgt().getSymbol().getInternalQualifiedName();
      }
      ArrayList<Integer> list = new ArrayList<>();
      if (assocDiffStruc.getChangedCard().isEmpty()){
        list.add(1);
        list.add(1);
      }
      else if (assocDiffStruc.getChangedCard().size() == 1){
        list.add(assocDiffStruc.getChangedCard().get(0).b);
        list.add(1);
      }
      else {
        list.add(assocDiffStruc.getChangedCard().get(0).b);
        list.add(assocDiffStruc.getChangedCard().get(1).b);
      }
      ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(assocDiffStruc.getAssociation()),
        generateElements(assocDiffStruc.getAssociation(), list, "", "", "", comment),
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
          generateElements(assocDiff.getSrcElem(), Arrays.asList(1, 1), "", "", "", comment),
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
            generateElements(pair.a, "", "", "", comment),
            null);
          artifactList.add(astodArtifact);
        }
        if (assocDiff.getSrcElem().getCDAssocDir().isDefinitiveNavigableLeft()){
          ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(pair.b),
            generateElements(pair.b, "", "", "", comment),
            null);
          artifactList.add(astodArtifact);
        }
      }
    }

    if (staDiff){
      for (ASTCDClass astcdClass : syntaxDiff.getSTADiff()){
        String comment = "The class " + astcdClass.getSymbol().getInternalQualifiedName() + " is part of a different inheritance tree.";
        ASTODArtifact astodArtifact = generateArtifact(oDTitleForClass(astcdClass),
          generateElements(astcdClass, "", "", "", comment),
          null);
        artifactList.add(astodArtifact);
      }
    }
    return artifactList;
  }
  //add function for STA semantics - done
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
                                             String content,
                                             String name,
                                             String text,
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

    //elements.remove(matchedObject);
    ASTModifierBuilder modifierBuilder = new ASTModifierBuilder();
    ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
    ASTStereotypeBuilder stereotypeBuilder = new ASTStereotypeBuilder();
    ASTStringLiteralBuilder literalBuilder = new ASTStringLiteralBuilder();

    valueBuilder.setContent(content);
    valueBuilder.setName(name);
    valueBuilder.setText(literalBuilder.setSource(text).build());

    stereotypeBuilder.addValues(valueBuilder.build());
    modifierBuilder.setStereotype(stereotypeBuilder.build());
    assert matchedObject != null;
    matchedObject.setModifier(modifierBuilder.build());

    CommentBuilder commentBuilder = new CommentBuilder();
    commentBuilder.setText(comment);
    matchedObject.set_PostCommentList(List.of(commentBuilder.build()));

    return new ArrayList<>(elements);
  }

  public List<ASTODElement> generateElements(ASTCDAssociation association,
                                             List<Integer> integers,
                                             String content,
                                             String name,
                                             String text,
                                             String comment){
    Pair<Set<ASTODElement>, ASTODLink> pair = getObjForOD(association, integers.get(0), integers.get(1));
    Set<ASTODElement> elements;
    elements = pair.a;

    //associations.remove(association);
    ASTStereoValueBuilder valueBuilder = new ASTStereoValueBuilder();
    ASTStereotypeBuilder stereotypeBuilder = new ASTStereotypeBuilder();
    ASTStringLiteralBuilder literalBuilder = new ASTStringLiteralBuilder();

    valueBuilder.setContent(content);
    valueBuilder.setName(name);
    valueBuilder.setText(literalBuilder.setSource(text).build());

    stereotypeBuilder.addValues(valueBuilder.build());
    pair.b.setStereotype(stereotypeBuilder.build());

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
