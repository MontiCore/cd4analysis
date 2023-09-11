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
import de.monticore.umlstereotype._ast.ASTStereotypeBuilder;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;

public class ODHelper {
  private int indexClass = 1;
  private int indexAssoc = 1;
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  private final ODBuilder ODBuilder = new ODBuilder();
  private Map<ASTCDClass, Integer> map = new HashMap<>();

  private CDSyntaxDiff syntaxDiff;

  private int maxNumberOfClasses = Integer.MAX_VALUE;

  public ODHelper() {
  }
  public ODHelper(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
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
    Set<Package> packages = createChainsForNewClass(astcdClass, new HashSet<>());
    if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()){
      return null;
    }
    System.out.print("size " + findUnprocessedObjects(packages).size());
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

  //TODO: if an association must be instantiated, but the class is abstract, a subclass must be found
  //if none exists, the association cannot be instantiated
  //implement a function that searches in a set for a subclass of a given class and checks the standard constraints
  public Set<Package> createChainsForNewClass(ASTCDClass astcdClass, Set<Package> objectSet) {
    List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
    ASTODObject srcObject = ODBuilder.buildObj(getNameForClass(astcdClass), astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"),
      helper.getSuperClasses(astcdClass),
      helper.getAttributesOD(astcdClass));
    if (list.isEmpty()) {
      Package pack = new Package(srcObject);
      objectSet.add(pack);
    }
    for (AssocStruct assocStruct : list) {
      boolean mustBeLinked = false;
      Pair<ASTODObject, ASTCDAssociation> tgtObject = null;
      ASTCDClass tgtClass = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        mustBeLinked = true;
        tgtObject = getObjectForTgt(astcdClass, srcObject, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct.getAssociation(), objectSet);
        tgtClass = Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
        mustBeLinked = true;
        tgtObject = getObjectForTgt(astcdClass, srcObject, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct.getAssociation(), objectSet);
        tgtClass = Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
      }
      if (mustBeLinked) {
        Package pack = null;
        if (tgtObject != null && srcObject != tgtObject.a) {
          if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a == astcdClass) {
            pack = new Package(srcObject,
              tgtObject.a,
              tgtObject.b, ClassSide.Left, true, false);
          } else {
            pack = new Package(tgtObject.a,
              srcObject,
              tgtObject.b, ClassSide.Right, false, true);
          }
        } else if (!tgtClass.getModifier().isAbstract()) {
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
        else if (tgtClass.getModifier().isAbstract() && helper.minDiffWitness(tgtClass) != null) {
          ASTCDAssociation association = getAssocStrucIfOverlapping(astcdClass, helper.minDiffWitness(tgtClass), assocStruct.getAssociation());
          if (getConnectedClasses(association, helper.getSrcCD()).a == astcdClass) {
            pack = new Package(srcObject,
              helper.minDiffWitness(tgtClass),
              getNameForClass(helper.minDiffWitness(tgtClass)),
              association, ClassSide.Left, true, false);
          } else {
            pack = new Package(helper.minDiffWitness(tgtClass),
              getNameForClass(helper.minDiffWitness(tgtClass)),
              srcObject,
              association, ClassSide.Right, false, true);
          }
        } else if (tgtClass.getModifier().isAbstract() && helper.minDiffWitness(tgtClass) == null) {
          pack = new Package(srcObject);
        }
        if (pack != null) {
          objectSet.add(pack);
        }
      }
    }
    for (AssocStruct assocStruct : getOtherAssoc(astcdClass, srcObject, objectSet)) {
      Pair<ASTODObject, ASTCDAssociation> realSrc;
      if (assocStruct.getSide().equals(ClassSide.Left)){
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      else {
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      Package pack;
      if (realSrc != null && srcObject != realSrc.a){
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(realSrc.a,
            srcObject,
            assocStruct.getAssociation(), ClassSide.Left, false, true);
        }
        else {
          pack = new Package(srcObject,
            realSrc.a,
            assocStruct.getAssociation(), ClassSide.Right, true, false);
        }
        objectSet.add(pack);
      }
      else {
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            srcObject,
            assocStruct.getAssociation(), ClassSide.Left, false, true);
        }
        else {
          pack = new Package(srcObject,
            Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            assocStruct.getAssociation(), ClassSide.Right, true, false);
        }
        objectSet.add(pack);
      }
    }
    return objectSet;
  }
  public Pair<ASTODObject, ASTCDAssociation> getObjectForTgt(ASTCDClass srcClass, ASTODObject srcObject, ASTCDClass tgtClass, ASTCDAssociation association, Set<Package> objectSet) {
    Set<ASTODObject> processedObjects = findProcessedObjects(objectSet);
    processedObjects.remove(srcObject);
    for (ASTODObject object : processedObjects) {
      if (tgtClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        //TODO: other function - not objectUsesAssoc for tgt
        if (!objectUsesAssoc(true, object, srcClass, association, getContainingPackages(object, objectSet))) {
          return new Pair<>(object, association);
        }
      } else if (tgtClass.getModifier().isAbstract()
        && CDInheritanceHelper.isSuperOf(tgtClass.getSymbol().getInternalQualifiedName(), helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getSymbol().getInternalQualifiedName(), helper.getSrcCD())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        Pair<Boolean, ASTCDAssociation> pair = objectUsesAssocAbstract(true, srcObject, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), association, getContainingPackages(object, objectSet));
        if (!pair.a) {
          return new Pair<>(object, pair.b);
        }
      }
    }
    for (ASTODObject object : findUnprocessedObjects(objectSet)) {
      if (tgtClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        if (!objectUsesAssoc(false, object, srcClass, association, getContainingPackages(object, objectSet))) {
          return new Pair<>(object, association);
        }
      } else if (tgtClass.getModifier().isAbstract()
        && CDInheritanceHelper.isSuperOf(tgtClass.getSymbol().getInternalQualifiedName(), helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getSymbol().getInternalQualifiedName(), helper.getSrcCD())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        Pair<Boolean, ASTCDAssociation> pair = objectUsesAssocAbstract(false, srcObject, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), association, getContainingPackages(object, objectSet));
        if (!pair.a) {
          return new Pair<>(object, pair.b);
        }
      }

    }
    return null;
  }

  public Pair<ASTODObject, ASTCDAssociation> getObjectForSrc(ASTCDClass srcClass, ASTCDClass tgtClass, ASTCDAssociation association, Set<Package> objectSet){
    Set<ASTODObject> processedObjects = findProcessedObjects(objectSet);
    for (ASTODObject object : processedObjects){
      if (srcClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()){
        if (!objectUsesAssoc(true, object, tgtClass, association, getContainingPackages(object, objectSet))){
          return new Pair<>(object, association);
        }
      } else if (srcClass.getModifier().isAbstract()
        && CDInheritanceHelper.isSuperOf(srcClass.getSymbol().getInternalQualifiedName(), helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getSymbol().getInternalQualifiedName(), helper.getSrcCD())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()){
        Pair<Boolean, ASTCDAssociation> pair = objectUsesAssocAbstract(true, object, tgtClass, association, getContainingPackages(object, objectSet));
        if (!pair.a){
          return new Pair<>(object, pair.b);
        }
      }
    }
    for (ASTODObject object : findUnprocessedObjects(objectSet)){
      if (srcClass == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()){
        if (!objectUsesAssoc(false, object, tgtClass, association, getContainingPackages(object, objectSet))){
          return new Pair<>(object, association);
        }
      } else if (srcClass.getModifier().isAbstract()
        && CDInheritanceHelper.isSuperOf(srcClass.getSymbol().getInternalQualifiedName(), helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getSymbol().getInternalQualifiedName(), helper.getSrcCD())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()){
        Pair<Boolean, ASTCDAssociation> pair = objectUsesAssocAbstract(false, object, tgtClass, association, getContainingPackages(object, objectSet));
        if (!pair.a){
          return new Pair<>(object, pair.b);
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
          if (pair.b == ClassSide.Left && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).b == helper.getCDClass(helper.getSrcCD(), tgt.getSymbol().getInternalQualifiedName().replace(".", "_"))) {
            return true;
          } else if (pair.b == ClassSide.Right && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), tgt.getSymbol().getInternalQualifiedName().replace(".", "_"))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public Pair<Boolean, ASTCDAssociation> objectUsesAssocAbstract(boolean processed, ASTODObject srcObject, ASTCDClass subTgt, ASTCDAssociation association, Set<Pair<Package, ClassSide>> containingSet) {
    AssocStruct assocStruct = helper.findMatchingAssocStructSrc(association, helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType()));
    ASTCDAssociation assocToUse = null;
    for (AssocStruct assocStructToMatch : helper.getSrcMap().get(helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType()))) {
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStructToMatch.getSide().equals(ClassSide.Left)
        && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).b == subTgt
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.getAssociation().getRight())) {
        assocToUse = assocStructToMatch.getAssociation();
        break;
      } else if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStructToMatch.getSide().equals(ClassSide.Right)
        && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).a == subTgt
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.getAssociation().getLeft())) {
        assocToUse = assocStructToMatch.getAssociation();
        break;
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStructToMatch.getSide().equals(ClassSide.Left)
        && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).b == subTgt
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.getAssociation().getRight())) {
        assocToUse = assocStructToMatch.getAssociation();
        break;
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStructToMatch.getSide().equals(ClassSide.Right)
        && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).a == subTgt
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.getAssociation().getLeft())) {
        assocToUse = assocStructToMatch.getAssociation();
        break;
      }
    }
    if (assocToUse == null) {
      assocToUse = association.deepClone();
    }
    if (getConnectedClasses(association, helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType())) {
        assocToUse.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(subTgt.getSymbol().getInternalQualifiedName())).build());
      } else if (getConnectedClasses(association, helper.getSrcCD()).b == helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType())) {
        assocToUse.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(subTgt.getSymbol().getInternalQualifiedName())).build());
      }return new Pair<>(objectUsesAssoc(processed, srcObject, subTgt, assocToUse, containingSet), assocToUse);
  }

  public Set<Package> createChainsForExistingObj(ASTODObject object, Set<Package> objectSet){
    System.out.println("Objects before createChainsForExistingObj");
    for (Package pack : objectSet){
      if (pack.getAstcdAssociation() != null){
        System.out.println(pack.getLeftObject().getMCObjectType().printType());
        System.out.print(" " + pack.getRightObject().getMCObjectType().printType());
      }
    }
    System.out.println("createChainsForExistingObj " + object.getName());
    List<AssocStruct> list = helper.getSrcMap().get(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()));
    Set<Pair<Package, ClassSide>> containingPackages = getContainingPackages(object, objectSet);
    System.out.println("containingPackages " + containingPackages.size());
    for (Pair<Package, ClassSide> pair : containingPackages){
      if (pair.a.getAstcdAssociation() != null){
        System.out.println(pair.a.getLeftObject().getMCObjectType().printType() + " " + pair.a.getRightObject().getMCObjectType().printType());
      }
    }
    System.out.println("list before" + list.size());
    for (AssocStruct assocStruct : list){
      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
    }
    for (Pair<Package, ClassSide> pair : containingPackages){
      if (pair.a.getAstcdAssociation() != null) {
        list.removeIf(assocStruct -> CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), assocStruct.getAssociation()) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), assocStruct.getAssociation()));
      }
    }
    System.out.println("list after 1 " + list.size());
    for (AssocStruct assocStruct : list){
      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
    }
    List<AssocStruct> list1 = new ArrayList<>(list);
    for (AssocStruct assocStruct : list1){
      for (Pair<Package, ClassSide> pair : containingPackages) {
        if (pair.a.getAstcdAssociation() != null) {
          if (Objects.equals(object.getMCObjectType().printType(), "A3")){
            System.out.println("A3");
            System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
            System.out.println(pair.a.getAstcdAssociation().getLeftQualifiedName().getQName() + " " + pair.a.getAstcdAssociation().getRightQualifiedName().getQName());
          }
          if (assocStruct.getSide().equals(ClassSide.Left)
            && pair.b.equals(ClassSide.Left)
            && Syn2SemDiffHelper.sameAssociationType(assocStruct, pair.a.getAstcdAssociation(), pair.b)
            && CDInheritanceHelper.isSuperOf(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            , getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName(), helper.getSrcCD())) {
            list.remove(assocStruct);
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && pair.b.equals(ClassSide.Right)
            && sameAssociationType(assocStruct, pair.a.getAstcdAssociation(), pair.b)
            && CDInheritanceHelper.isSuperOf(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            , getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName(), helper.getSrcCD())) {
            list.remove(assocStruct);
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && pair.b.equals(ClassSide.Right)
            && Syn2SemDiffHelper.sameAssociationType(assocStruct, pair.a.getAstcdAssociation(), pair.b)
            && CDInheritanceHelper.isSuperOf(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            , getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName(), helper.getSrcCD())) {
            list.remove(assocStruct);
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && pair.b.equals(ClassSide.Left)
            && Syn2SemDiffHelper.sameAssociationType(assocStruct, pair.a.getAstcdAssociation(), pair.b)
            && CDInheritanceHelper.isSuperOf(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            , getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName(), helper.getSrcCD())) {
            list.remove(assocStruct);
          }
        }
      }
    }
    System.out.println("list after 2 " + list.size());
    for (AssocStruct assocStruct : list){
      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
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
    System.out.println("list after 3 " + list.size());
    for (AssocStruct assocStruct : list){
      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
    }
    ASTCDClass astcdClass = helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType());
    List<AssocStruct> otherAssoc = getOtherAssoc(astcdClass, object, objectSet);
    List<AssocStruct> copy = new ArrayList<>(otherAssoc);
    for (Pair<Package, ClassSide> pack : containingPackages){
      if (pack.a.getAstcdAssociation() != null) {
        for (AssocStruct assocStruct : copy) {
          if (CDAssociationHelper.sameAssociation(pack.a.getAstcdAssociation(), assocStruct.getAssociation()) || CDAssociationHelper.sameAssociationInReverse(pack.a.getAstcdAssociation(), assocStruct.getAssociation())) {
            otherAssoc.remove(assocStruct);
          }
        }
      }
    }
    System.out.println("otherAssoc");
    for (AssocStruct assocStruct : otherAssoc){
      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
    }
    for (AssocStruct assocStruct : otherAssoc){
      Pair<ASTODObject, ASTCDAssociation> realSrc;
      if (assocStruct.getSide().equals(ClassSide.Left)){
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      else {
        realSrc = getObjectForSrc(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, astcdClass, assocStruct.getAssociation(), objectSet);
      }
      Package pack;
      if (realSrc != null && object != realSrc.a){
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(realSrc.a,
            object,
            assocStruct.getAssociation(), ClassSide.Left, false, true);
        }
        else {
          pack = new Package(object,
            realSrc.a,
            assocStruct.getAssociation(), ClassSide.Right, true, false);
        }
        objectSet.add(pack);
      }
      else {
        if (assocStruct.getSide().equals(ClassSide.Left)){
          pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            object,
            assocStruct.getAssociation(), ClassSide.Left, false, true);
        }
        else {
          pack = new Package(object,
            Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getNameForClass(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            assocStruct.getAssociation(), ClassSide.Right, true, false);
        }
        objectSet.add(pack);
      }
    }
    if (list.isEmpty()){
      Package pack = new Package(object);
      objectSet.add(pack);
    } else {
      objectSet.addAll(createChainsHelper(object, list, objectSet));
    }
    return objectSet;
  }

  //TODO: add other assocs
  public Set<Package> createChainsHelper(ASTODObject object, List<AssocStruct> list, Set<Package> objectSet){
    if (list.isEmpty()){
      Package pack = new Package(object);
      objectSet.add(pack);
      return objectSet;
    }
    for (AssocStruct assocStruct : list) {
      boolean mustBeLinked = false;
      Pair<ASTODObject, ASTCDAssociation> tgtObject = null;
      ASTCDClass tgtClass = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        mustBeLinked = true;
        tgtObject = getObjectForTgtSpec(object, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct.getAssociation(), objectSet);
        tgtClass = Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
      } else if (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
        mustBeLinked = true;
        tgtObject = getObjectForTgtSpec(object, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct.getAssociation(), objectSet);
        tgtClass = Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
      }
      if (mustBeLinked) {
        Package pack = null;
        if (tgtObject != null && object != tgtObject.a) {
          if (getConnectedClasses(tgtObject.b, helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())) {
            pack = new Package(object,
              tgtObject.a,
              tgtObject.b, ClassSide.Left, true, false);
          } else {
            pack = new Package(tgtObject.a,
              object,
              tgtObject.b, ClassSide.Right, false, true);
          }
        } else if (!tgtClass.getModifier().isAbstract()){
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
        else if (tgtClass.getModifier().isAbstract() && helper.minDiffWitness(tgtClass) != null) {
          ASTCDAssociation association = getAssocStrucIfOverlapping(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), helper.minDiffWitness(tgtClass), assocStruct.getAssociation());
          if (getConnectedClasses(association, helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())) {
            pack = new Package(object,
              helper.minDiffWitness(tgtClass),
              getNameForClass(helper.minDiffWitness(tgtClass)),
              association, ClassSide.Left, true, false);
          } else {
            pack = new Package(helper.minDiffWitness(tgtClass),
              getNameForClass(helper.minDiffWitness(tgtClass)),
              object,
              association, ClassSide.Right, false, true);
          }
        } else if (tgtClass.getModifier().isAbstract() && helper.minDiffWitness(tgtClass) == null) {
          pack = new Package(object);
        }
        if (pack != null) {
          objectSet.add(pack);
        }
      }
    }
    return objectSet;
  }

  public Pair<ASTODObject, ASTCDAssociation> getObjectForTgtSpec(ASTODObject src, ASTCDClass tgtClass, ASTCDAssociation association, Set<Package> objectSet) {
    Set<ASTODObject> processedObjects = findProcessedObjects(objectSet);
    for (ASTODObject object : processedObjects) {
      if (helper.getCDClass(helper.getSrcCD(), tgtClass.getSymbol().getInternalQualifiedName().replace(".", "_")) == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        if (!objectUsesAssocSpec(true, object, src, helper.getCDClass(helper.getSrcCD(), src.getMCObjectType().printType()), association, getContainingPackages(object, objectSet))) {
          return new Pair<>(object, association);
        }
      } else if (tgtClass.getModifier().isAbstract()
        && CDInheritanceHelper.isSuperOf(tgtClass.getSymbol().getInternalQualifiedName(), helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getSymbol().getInternalQualifiedName(), helper.getSrcCD())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        Pair<Boolean, ASTCDAssociation> pair = objectUsesAssocSpecAbstract(true, object, src, helper.getCDClass(helper.getSrcCD(), src.getMCObjectType().printType()), association, getContainingPackages(object, objectSet));
        if (!pair.a) {
          return new Pair<>(object, pair.b);
        }
      }
    }
    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
    for (ASTODObject object : unprocessedObjects) {
      if (helper.getCDClass(helper.getSrcCD(), tgtClass.getSymbol().getInternalQualifiedName().replace(".", "_")) == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        if (!objectUsesAssocSpec(false, object, src, helper.getCDClass(helper.getSrcCD(), src.getMCObjectType().printType()), association, getContainingPackages(object, objectSet))) {
          return new Pair<>(object, association);
        }
      } else if (tgtClass.getModifier().isAbstract()
        && CDInheritanceHelper.isSuperOf(tgtClass.getSymbol().getInternalQualifiedName(), helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getSymbol().getInternalQualifiedName(), helper.getSrcCD())
        && !helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()).getModifier().isAbstract()) {
        Pair<Boolean, ASTCDAssociation> pair = objectUsesAssocSpecAbstract(false, object, src, helper.getCDClass(helper.getSrcCD(), src.getMCObjectType().printType()), association, getContainingPackages(object, objectSet));
        if (!pair.a) {
          return new Pair<>(object, pair.b);
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
          if (pair.b == ClassSide.Left && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).b == helper.getCDClass(helper.getSrcCD(), tgt.getSymbol().getInternalQualifiedName().replace(".", "_"))) {
            return true;
          } else if (pair.b == ClassSide.Right && getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), tgt.getSymbol().getInternalQualifiedName().replace(".", "_"))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public Pair<Boolean, ASTCDAssociation> objectUsesAssocSpecAbstract(boolean processed, ASTODObject srcObject, ASTODObject tgtObject, ASTCDClass subTgt, ASTCDAssociation association, Set<Pair<Package, ClassSide>> containingSet) {
    AssocStruct assocStruct = helper.findMatchingAssocStructSrc(association, helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType()));
    ASTCDAssociation assocToUse = null;
    if (assocStruct != null) {
      for (AssocStruct assocStructToMatch : helper.getSrcMap().get(helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType()))) {
        if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.getSide().equals(ClassSide.Left)
          && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).b == subTgt
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.getAssociation().getRight())) {
          assocToUse = assocStructToMatch.getAssociation().deepClone();
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.getSide().equals(ClassSide.Right)
          && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).a == subTgt
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.getAssociation().getLeft())) {
          assocToUse = assocStructToMatch.getAssociation().deepClone();
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.getSide().equals(ClassSide.Left)
          && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).b == subTgt
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.getAssociation().getRight())) {
          assocToUse = assocStructToMatch.getAssociation().deepClone();
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.getSide().equals(ClassSide.Right)
          && getConnectedClasses(assocStructToMatch.getAssociation(), helper.getSrcCD()).a == subTgt
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.getAssociation().getLeft())) {
          assocToUse = assocStructToMatch.getAssociation().deepClone();
          break;
        }
      }
    }
    if (assocToUse == null){
      assocToUse = association.deepClone();
      if (getConnectedClasses(association, helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType())) {
        assocToUse.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(subTgt.getSymbol().getInternalQualifiedName())).build());
      } else if (getConnectedClasses(association, helper.getSrcCD()).b == helper.getCDClass(helper.getSrcCD(), srcObject.getMCObjectType().printType())) {
        assocToUse.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(subTgt.getSymbol().getInternalQualifiedName())).build());
      }
    }
    return new Pair<>(objectUsesAssocSpec(processed, srcObject, tgtObject, subTgt, assocToUse, containingSet), assocToUse);
  }
  public List<AssocStruct> getOtherAssoc(ASTCDClass astcdClass, ASTODObject object, Set<Package> objectSet){
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
    Set<ASTCDClass> superClasses = CDDiffUtil.getAllSuperclasses(astcdClass, helper.getSrcCD().getCDDefinition().getCDClassesList());
    superClasses.remove(astcdClass);
    List<AssocStruct> toCheckFromSuper = new ArrayList<>();
    List<AssocStruct> toCheckCopy = new ArrayList<>(toCheckFromSuper);
    for (ASTCDClass superClass : superClasses) {
      toCheckFromSuper.addAll(helper.getOtherAssocFromSuper(superClass));
    }
    for (AssocStruct assocStruct : list) {
      for (AssocStruct assocStructSuper : toCheckCopy) {
        if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructSuper.getSide().equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructSuper.getAssociation().getRight())) {
          toCheckFromSuper.remove(assocStructSuper);
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructSuper.getSide().equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructSuper.getAssociation().getLeft())) {
          toCheckFromSuper.remove(assocStructSuper);
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructSuper.getSide().equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructSuper.getAssociation().getRight())) {
          toCheckFromSuper.remove(assocStructSuper);
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructSuper.getSide().equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructSuper.getAssociation().getLeft())) {
          toCheckFromSuper.remove(assocStructSuper);
          break;
        }
      }
    }
    List<AssocStruct> toCheckFromSuperCopy = new ArrayList<>(toCheckFromSuper);
    for (AssocStruct assocStruct : toCheckFromSuperCopy){
      if (assocStruct.getSide().equals(ClassSide.Left)){
        assocStruct.getAssociation().getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName())).build());
      }
      else {
        assocStruct.getAssociation().getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName())).build());
      }
      list.add(assocStruct);
    }
    Set<Pair<Package, ClassSide>> containingPacks = getContainingPackages(object, objectSet);
    List<AssocStruct> copy = new ArrayList<>(list);
//    System.out.println("copy");
//    for (AssocStruct assocStruct : copy){
//      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
//    }
    System.out.println("containingPacks");
    for (Pair<Package, ClassSide> pair : containingPacks){
      if (pair.a.getAstcdAssociation() != null) {
        System.out.println(pair.a.getAstcdAssociation().getLeftQualifiedName().getQName() + " " + pair.a.getAstcdAssociation().getRightQualifiedName().getQName());
      }
    }
    List<AssocStruct> copy2 = new ArrayList<>(list);
    System.out.println("copy2");
    for (AssocStruct assocStruct : copy2){
      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
    }
    for (AssocStruct assocStruct : copy2) {
      for (AssocStruct assocStruct1 : copy2) {
        if (assocStruct != assocStruct1) {
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct1.getSide().equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStruct1.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStruct1.getAssociation().getLeft())
            && ((helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a) == getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).a)
            || sameInheritanceTree(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).a))) {
            list.remove(assocStruct);
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct1.getSide().equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStruct1.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStruct1.getAssociation().getRight())
            && ((helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a) == getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).b)
            || sameInheritanceTree(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).b))) {
            list.remove(assocStruct);
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStruct1.getSide().equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStruct1.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStruct1.getAssociation().getRight())
            && ((helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b) == getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).b)
            || sameInheritanceTree(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).b))) {
            list.remove(assocStruct);
            break;
          } else if (CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStruct1.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStruct1.getAssociation().getRight())
            &&(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b) == getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).a
            || sameInheritanceTree(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(assocStruct1.getAssociation(), helper.getSrcCD()).a))) {
            list.remove(assocStruct);
            break;
          }
        }
      }
    }
    System.out.println("list");
    for (AssocStruct assocStruct : list) {
      System.out.println(assocStruct.getAssociation().getLeftQualifiedName().getQName() + " " + assocStruct.getAssociation().getRightQualifiedName().getQName());
    }
    return list;
  }

  public boolean sameInheritanceTree(ASTCDClass astcdClass, ASTCDClass minClass){
    for (ASTCDClass superClass : CDDiffUtil.getAllSuperclasses(astcdClass, helper.getSrcCD().getCDDefinition().getCDClassesList())){
      if (helper.minDiffWitness(superClass) == minClass){
        return true;
      }
    }
    return false;
  }

  public ASTCDAssociation getAssocStrucIfOverlapping(ASTCDClass srcClass, ASTCDClass tgtClass, ASTCDAssociation superAssoc){
    AssocStruct assocStruct = helper.findMatchingAssocStructSrc(superAssoc, srcClass);
    ASTCDAssociation assocToUse = null;
    for (AssocStruct assocStructToMatch : helper.getSrcMap().get(srcClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStructToMatch.getSide().equals(ClassSide.Left)
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.getAssociation().getRight())) {
        assocToUse = assocStructToMatch.getAssociation().deepClone();
        break;
      } else if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStructToMatch.getSide().equals(ClassSide.Right)
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.getAssociation().getLeft())) {
        assocToUse = assocStructToMatch.getAssociation().deepClone();
        break;
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStructToMatch.getSide().equals(ClassSide.Left)
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.getAssociation().getRight())) {
        assocToUse = assocStructToMatch.getAssociation().deepClone();
        break;
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStructToMatch.getSide().equals(ClassSide.Right)
        && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.getAssociation().getLeft())) {
        assocToUse = assocStructToMatch.getAssociation().deepClone();
        break;
      }
    }
    if (assocToUse == null) {
      assocToUse = superAssoc.deepClone();
    }
      if (getConnectedClasses(superAssoc, helper.getSrcCD()).a == srcClass) {
        assocToUse.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(tgtClass.getSymbol().getInternalQualifiedName())).build());
      } else if (getConnectedClasses(superAssoc, helper.getSrcCD()).b == srcClass) {
        assocToUse.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(tgtClass.getSymbol().getInternalQualifiedName())).build());
//        assocToUse.setLeft(CD4CodeMill.cDAssocLeftSideBuilder().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(tgtClass.getSymbol().getInternalQualifiedName())).build()).build());
      System.out.println("assocToUse " + assocToUse.getLeftQualifiedName().getQName() + " " + assocToUse.getRightQualifiedName().getQName());
      }
    return assocToUse;
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
    ODHelperNew helper = new ODHelperNew();
    elements = helper.getObjForOD(astcdClass);
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
