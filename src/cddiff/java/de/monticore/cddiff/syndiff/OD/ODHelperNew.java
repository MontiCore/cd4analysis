package de.monticore.cddiff.syndiff.OD;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;

public class ODHelperNew {
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  private final ODBuilder odBuilder = new ODBuilder();
  private Map<ASTCDClass, Integer> map = new HashMap<>();
  private int maxNumberOfClasses = Integer.MAX_VALUE;

  public ODHelperNew() {
  }

  public ODHelperNew(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
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

  public static Set<ASTODObject> findProcessedObjects(Set<Package> packages) {
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

  public Set<Package> createChains(ASTCDAssociation association, int cardinalityLeft, int cardinalityRight,
                                   ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
                                   ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    Set<Package> objectSet = new HashSet<>();
    if (cardinalityLeft == 1 && cardinalityRight == 1) {

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
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc = ArrayListMultimap.create();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt = ArrayListMultimap.create();
    Set<Package> packages = createChainsForNewClass(astcdClass, new HashSet<>(), mapSrc, mapTgt);
    if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()) {
      return null;
    }
    while (!findUnprocessedObjects(packages).isEmpty()) {
      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
        packages.addAll(createChainsForExistingObj(astodObject, packages, mapSrc, mapTgt));
      }
      if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()) {
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
  public Pair<Set<ASTODElement>, ASTODLink> getObjForOD(ASTCDAssociation association, int cardinalityLeft, int cardinalityRight) {
    Set<ASTODElement> set = new HashSet<>();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc = ArrayListMultimap.create();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt = ArrayListMultimap.create();
    Set<Package> packages = createChains(association, cardinalityLeft, cardinalityRight, mapSrc, mapTgt);
    if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()) {
      return null;
    }
    ASTODLink link = packages.iterator().next().getAssociation();
    while (!findUnprocessedObjects(packages).isEmpty()) {
      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
        packages.addAll(createChainsForExistingObj(astodObject, packages, mapSrc, mapTgt));
      }
      if (maxNumberOfClasses < findUnprocessedObjects(packages).size() + findProcessedObjects(packages).size()) {
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

  public Set<Package> createChainsForNewClass(ASTCDClass astcdClass, Set<Package> packages,
                                              ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc, ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    ASTODObject srcObject = odBuilder.buildObj(getNameForClass(astcdClass), astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"),
      helper.getSuperClasses(astcdClass),
      helper.getAttributesOD(astcdClass));
    if (helper.getSrcMap().get(astcdClass).isEmpty()) {
      Package pack = new Package(srcObject);
      packages.add(pack);
    }
    boolean mustHaveAdded = false;
    boolean hasAdded = false;
    List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
    for (AssocStruct assocStruct : list) {
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        mustHaveAdded = true;
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = getTgtObject(astcdClass, assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, mapSrc , mapTgt);
        }
        if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = getSubTgtObject(astcdClass, assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, mapSrc , mapTgt);
        }
        if (tgtObject == null
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b) != null
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()
          || helper.getClassSize(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b))
          < helper.getClassSize(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b))) {
          tgtObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)));
        } else if (tgtObject == null
          && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b));
        }
        if (tgtObject != null) {
          hasAdded = true;
          mapSrc.put(srcObject, new Pair<>(assocStruct, ClassSide.Left));
          mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Right));
          Package pack = new Package(srcObject, tgtObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
          packages.add(pack);
        }

      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        mustHaveAdded = true;
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = getTgtObject(astcdClass, assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, mapSrc, mapTgt);
        }
        if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = getSubTgtObject(astcdClass, assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, mapSrc ,mapTgt);
        }

        if (tgtObject == null
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a) != null
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()
          || helper.getClassSize(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a))
          < helper.getClassSize(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a))) {
          tgtObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)));
        } else if (tgtObject == null
          && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a));
        }
        if (tgtObject != null) {
          hasAdded = true;
          mapSrc.put(srcObject, new Pair<>(assocStruct, ClassSide.Right));
          mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Left));
          Package pack = new Package(tgtObject, srcObject, assocStruct.getAssociation(), ClassSide.Right, false, true);
          packages.add(pack);
        }
      }
    }
    if (mustHaveAdded && !hasAdded) {
      Package pack = new Package(srcObject);
      packages.add(pack);
    }
    System.out.println("tgtAssocs for " + astcdClass.getName());
    for (AssocStruct assocStruct : getTgtAssocs(astcdClass)) {
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName() + " " + assocStruct.getAssociation().getLeft().getCDRole().getName()
        + " " + assocStruct.getAssociation().getRight().getCDRole().getName() + getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName());
    }
    for (AssocStruct assocStruct : getTgtAssocs(astcdClass)) {
      ASTODObject realSrcObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct, astcdClass, mapSrc, mapTgt);
        } else if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          realSrcObject = getSubRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct, astcdClass, mapSrc, mapTgt);
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
          mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Left));
          mapTgt.put(srcObject, new Pair<>(assocStruct, ClassSide.Right));
          Package pack = new Package(realSrcObject, srcObject, assocStruct.getAssociation(), ClassSide.Right, false, true);
          packages.add(pack);
        }

      } else {
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct, astcdClass, mapSrc, mapTgt);
        } else if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          realSrcObject = getSubRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct, astcdClass, mapSrc, mapTgt);
        }

        if (realSrcObject == null && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
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
          mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Right));
          mapTgt.put(srcObject, new Pair<>(assocStruct, ClassSide.Left));
          Package pack = new Package(srcObject, realSrcObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
          packages.add(pack);
        }
      }
    }
    return packages;
  }

  public Set<Package> createChainsForExistingObj(ASTODObject object, Set<Package> packages,
                                                 ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc, ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    System.out.println("createChainsForExistingObj " + object.getName());
    System.out.println("packages before ");
    for (Package pack : packages) {
      if (pack.getAstcdAssociation() != null){
        System.out.println(pack.getLeftObject().getName() + " " + pack.getAstcdAssociation().getLeft().getCDRole().getName() + " " + pack.getAstcdAssociation().getRight().getCDRole().getName() + " " + pack.getRightObject().getName());
      }
    }
    List<AssocStruct> list = new ArrayList<>();
    for (AssocStruct assocStruct : helper.getSrcMap().get(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()))) {
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        list.add(assocStruct);
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        list.add(assocStruct);
      }
    }
    List<AssocStruct> copy = new ArrayList<>(list);
    List<Pair<AssocStruct, ClassSide>> createdAssocs = mapTgt.get(object);
    List<Pair<AssocStruct, ClassSide>> createdAssocsSrc= mapSrc.get(object);
    System.out.println("createdAssocs");
    for (Pair<AssocStruct, ClassSide> pair : createdAssocs) {
      System.out.println(getConnectedClasses(pair.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName() + " " + pair.a.getAssociation().getLeft().getCDRole().getName() + " "
        + pair.a.getAssociation().getRight().getCDRole().getName() + " " + getConnectedClasses(pair.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName());
    }
    for (Pair<AssocStruct, ClassSide> pair : createdAssocs) {
      for (AssocStruct assocStruct : copy) {
        if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)))
          && (helper.sameAssocStruct(assocStruct, pair.a)
          || isSubAssociation(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        } else if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)))
          && (helper.sameAssocStructInReverse(assocStruct, pair.a)
          || isSubAssociation(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        }
      }
    }
    List<AssocStruct> copy2 = new ArrayList<>(list);
    for (Pair<AssocStruct, ClassSide> pair : createdAssocsSrc){
      for (AssocStruct assocStruct : copy2) {
        if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)))
          && (helper.sameAssocStruct(assocStruct, pair.a)
          || isSubAssociation(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        } else if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)))
          && (helper.sameAssocStructInReverse(assocStruct, pair.a)
          || isSubAssociation(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        }
      }
    }
    System.out.println("list to create for " + object.getName());
    for (AssocStruct assocStruct : list) {
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName() + " " + assocStruct.getAssociation().getLeft().getCDRole().getName()
        + " " + assocStruct.getAssociation().getRight().getCDRole().getName() + getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName());
    }
    boolean mustHaveAdded = false;
    boolean hasAdded = false;
    for (AssocStruct assocStruct : list) {
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        mustHaveAdded = true;
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = getTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, mapSrc, mapTgt);
        }
        if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = getSubTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, mapSrc, mapTgt);
        }

        if (tgtObject == null
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b) != null
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()
          || helper.getClassSize(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b))
          < helper.getClassSize(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b))) {
          tgtObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b)));
        } else if (tgtObject == null
          && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          tgtObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b));
        }
        if (tgtObject != null) {
          hasAdded = true;
          mapSrc.put(object, new Pair<>(assocStruct, ClassSide.Left));
          mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Right));
          Package pack = new Package(object, tgtObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
          packages.add(pack);
        }

      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        mustHaveAdded = true;
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = getTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, mapSrc , mapTgt);
        }
        if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = getSubTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, mapSrc , mapTgt);
        }
        if (tgtObject == null
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()
          || helper.getClassSize(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a))
          < helper.getClassSize(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)))
          && helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a) != null) {
          tgtObject = odBuilder.buildObj(getNameForClass(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)), helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a).getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)),
            helper.getAttributesOD(helper.minDiffWitness(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a)));
        } else if (tgtObject == null
          && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          tgtObject = odBuilder.buildObj(getNameForClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a), getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName().replace(".", "_"),
            helper.getSuperClasses(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a),
            helper.getAttributesOD(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a));
        }
        if (tgtObject != null) {
          hasAdded = true;
          mapSrc.put(object, new Pair<>(assocStruct, ClassSide.Right));
          mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Left));
          Package pack = new Package(tgtObject, object, assocStruct.getAssociation(), ClassSide.Right, false, true);
          packages.add(pack);
        }
      }
    }
    if (mustHaveAdded && !hasAdded) {
      Package pack = new Package(object);
      packages.add(pack);
    } else if (!mustHaveAdded) {
      Package pack = new Package(object);
      packages.add(pack);
    }
    System.out.println("tgtAssocs for " + object.getName());
    for (AssocStruct assocStruct : getTgtAssocsForObject(object, mapTgt)) {
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName() + " " + assocStruct.getAssociation().getLeft().getCDRole().getName()
        + " " + assocStruct.getAssociation().getRight().getCDRole().getName() + getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName());
    }
    for (AssocStruct assocStruct : getTgtAssocsForObject(object, mapTgt)) {
      ASTODObject realSrcObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
        } else if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getModifier().isAbstract()) {
          realSrcObject = getSubRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
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
          mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Left));
          mapTgt.put(object, new Pair<>(assocStruct, ClassSide.Right));
          Package pack = new Package(realSrcObject, object, assocStruct.getAssociation(), ClassSide.Right, false, true);
          packages.add(pack);
        }
      } else {
        if (!getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
        } else if (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
          realSrcObject = getSubRealSrc(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
        }

        if (realSrcObject == null && !getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getModifier().isAbstract()) {
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
          mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Right));
          mapTgt.put(object, new Pair<>(assocStruct, ClassSide.Left));
          Package pack = new Package(object, realSrcObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
          packages.add(pack);
        }
      }
    }
    System.out.println("packages after ");
    for (Package pack : packages) {
      if (pack.getAstcdAssociation() != null){
        System.out.println(pack.getLeftObject().getName() + " " + pack.getAstcdAssociation().getLeft().getCDRole().getName() + " " + pack.getAstcdAssociation().getRight().getCDRole().getName() + " " + pack.getRightObject().getName());
      }
    }
    return packages;
  }

  public ASTODObject getTgtObject(ASTCDClass srcClass, AssocStruct assocStruct, ASTCDClass tgtToFind,
                                  ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
                                  ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    List<ASTODObject> typeObjects = getObjectsOfType(tgtToFind, tgtMap);
    List<ASTODObject> typeObjectsSrc = getObjectsOfType(tgtToFind, srcMap);
    if (assocStruct.getSide().equals(ClassSide.Left)
      && (!typeObjects.isEmpty() || !typeObjectsSrc.isEmpty())
      && !(assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
      || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())
      && (assocStruct.getAssociation().getRight().getCDCardinality().isMult()
      || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
      if (!typeObjects.isEmpty()) {
        return typeObjects.get(0);
      } else {
        return typeObjectsSrc.get(0);
      }
    } else if (assocStruct.getSide().equals(ClassSide.Right)
      && (!typeObjects.isEmpty() || !typeObjectsSrc.isEmpty())
      && !(assocStruct.getAssociation().getRight().getCDCardinality().isOne()
      || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())
      && (assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
      || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
      if (!typeObjects.isEmpty()) {
        return typeObjects.get(0);
      } else {
        return typeObjectsSrc.get(0);
      }
    }
    for (ASTODObject object : typeObjects) {
      boolean matched = false;
      if (tgtToFind.getName().equals("A4")){
        System.out.println("A4 search " );
      }
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(object)) {
        System.out.println("assocStructToMatch " + assocStructToMatch.b + " " + assocStructToMatch.a.getAssociation().getLeft().getCDRole().getName() + " " + assocStructToMatch.a.getAssociation().getRight().getCDRole().getName());
        System.out.println("assocStruct " + assocStruct.getSide() + " " + assocStruct.getAssociation().getLeft().getCDRole().getName() + " " + assocStruct.getAssociation().getRight().getCDRole().getName());
        if (assocStruct.getSide().equals(ClassSide.Left) //not-searched class of assocStruc on the left side
          && assocStructToMatch.b.equals(ClassSide.Left) //searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right) //src of assocStruc on the right side
          && assocStructToMatch.b.equals(ClassSide.Right) //tgt of assocStructToMatch on the right side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        }
      }
      if (!matched) {
        return object;
      }
      typeObjectsSrc.remove(object);
    }
    for (ASTODObject object : typeObjectsSrc){
      boolean matched = false;
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(object)){
        System.out.println("assocStructToMatch " + assocStructToMatch.b + " " + assocStructToMatch.a.getAssociation().getLeft().getCDRole().getName() + " " + assocStructToMatch.a.getAssociation().getRight().getCDRole().getName());
        System.out.println("assocStruct " + assocStruct.getSide() + " " + assocStruct.getAssociation().getLeft().getCDRole().getName() + " " + assocStruct.getAssociation().getRight().getCDRole().getName());
        if (assocStruct.getSide().equals(ClassSide.Left)//not-searched class on the left side
          && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
          matched = true;
          break;
        }
      }
      if (!matched) {
        return object;
      }
    }
    return null;
  }

  public ASTODObject getSubTgtObject(ASTCDClass srcClass, AssocStruct assocStruct, ASTCDClass tgtToFind,
                                     ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
                                     ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    List<ASTCDClass> subClasses = Syn2SemDiffHelper.getSpannedInheritance(helper.getSrcCD(), tgtToFind);
    for (ASTCDClass subClass : subClasses) {
      List<ASTODObject> objectsOfType = getObjectsOfType(subClass, tgtMap);
      List<ASTODObject> objectsOfTypeSrc = getObjectsOfType(subClass, srcMap);
      boolean changed = false;
      for (AssocStruct assocStructFromCLass : helper.getSrcMap().get(subClass)){
        if (isSubAssociationInReverse(assocStruct, assocStructFromCLass)){
          assocStruct = assocStructFromCLass;
          changed = true;
        }
      }
      if (!changed){
        for (AssocStruct otherAssoc : helper.getOtherAssocFromSuper(subClass)){
          if (isSubAssociation(assocStruct, otherAssoc)){
            assocStruct = otherAssoc;
          }
        }
      }
      if (!objectsOfType.isEmpty() || ! objectsOfTypeSrc.isEmpty()) {
        boolean card = false;
        if (!changed
          && assocStruct.getSide().equals(ClassSide.Left)
          && !(assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())
          && (assocStruct.getAssociation().getRight().getCDCardinality().isMult()
          || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
          card = true;
        } else if (!changed
          && assocStruct.getSide().equals(ClassSide.Right)
          && !(assocStruct.getAssociation().getRight().getCDCardinality().isOne()
          || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())
          && (assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
          card = true;
        } else if (changed
          && assocStruct.getSide().equals(ClassSide.Left)
          && !(assocStruct.getAssociation().getRight().getCDCardinality().isOne()
          || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())
          && (assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
          card = true;
        } else if (changed
          && assocStruct.getSide().equals(ClassSide.Right)
          && !(assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())
          && (assocStruct.getAssociation().getRight().getCDCardinality().isMult()
          || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
          card = true;
        }
        if (card) {
          if (!objectsOfType.isEmpty()) {
            return objectsOfType.get(0);
          } else {
            return objectsOfTypeSrc.get(0);
          }
        }
      }
      for (ASTODObject subObject : objectsOfType) {
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> subAssocStruct : tgtMap.get(subObject)) {
          if (assocStruct.getSide().equals(ClassSide.Left)//not-searched class of assocStruc on the left side
            && subAssocStruct.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          }
        }
        if (!matched) {
          return subObject;
        }
        objectsOfTypeSrc.remove(subObject);
      }
      for (ASTODObject subObject : objectsOfTypeSrc){
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> subAssocStruct : srcMap.get(subObject)){
          if (assocStruct.getSide().equals(ClassSide.Left)//not-searched class of assocStruc on the left side
            && subAssocStruct.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          }
        }
        if (!matched) {
          return subObject;
        }
      }
    }
    return null;
  }

  public ASTODObject getSubRealSrc(ASTCDClass srcToFind, AssocStruct assocStruct, ASTCDClass tgtClass,
                                   ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
                                   ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    List<ASTCDClass> subClasses = Syn2SemDiffHelper.getSpannedInheritance(helper.getSrcCD(), srcToFind);
    for (ASTCDClass subClass : subClasses) {
      List<ASTODObject> objectsOfType = getObjectsOfType(subClass, srcMap);
      List<ASTODObject> objectsOfTypeTgt = getObjectsOfType(subClass, tgtMap);
      for (ASTODObject subObject : objectsOfType) {
        for (AssocStruct assocStructFromCLass : helper.getSrcMap().get(helper.getCDClass(helper.getSrcCD(), subObject.getMCObjectType().printType()))) {
          if (isSubAssociation(assocStruct, assocStructFromCLass)) {
            assocStruct = assocStructFromCLass;
          }
        }
        if (!objectsOfType.isEmpty() || !objectsOfTypeTgt.isEmpty()) {
          if (assocStruct.getSide().equals(ClassSide.Left)
            && !(assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())
            && (assocStruct.getAssociation().getRight().getCDCardinality().isMult()
            || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
            if (!objectsOfType.isEmpty()) {
              return subObject;
            } else {
              return objectsOfTypeTgt.get(0);
            }
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && !(assocStruct.getAssociation().getRight().getCDCardinality().isOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())
            && (assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
            if (!objectsOfType.isEmpty()) {
              return subObject;
            } else {
              return objectsOfTypeTgt.get(0);
            }
          }
        }
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(subObject)) {
          if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
            && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirection(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a)) {//not needed - tgt must be same
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirection(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirection(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a)) {
            matched = true;
            break;
          }
        }
        if (!matched) {
          return subObject;
        }
        objectsOfTypeTgt.remove(subObject);
      }
      for (ASTODObject subObject : objectsOfTypeTgt){
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(subObject)) {
          if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
            && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirection(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a)) {//not needed - tgt must be same
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirection(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirection(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirection(assocStruct, assocStructToMatch)
            && getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
            .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName())) {
            //&& isSubClass(srcToFind, getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a)) {
            matched = true;
            break;
          }
        }
        if (!matched) {
          return subObject;
        }
      }
    }
    return null;
  }

  public List<AssocStruct> getTgtAssocs(ASTCDClass astcdClass) {
    List<AssocStruct> assocStructs = new ArrayList<>();
    Set<ASTCDClass> superClassSet = CDDiffUtil.getAllSuperclasses(astcdClass, helper.getSrcCD().getCDDefinition().getCDClassesList());
    for (ASTCDClass superClass : superClassSet) {
      assocStructs.addAll(helper.getOtherAssocFromSuper(superClass));
    }
    List<AssocStruct> copy = new ArrayList<>(assocStructs);
    for (AssocStruct assocStruct : copy) {
      for (AssocStruct assocStruct1 : copy) {
        if (assocStruct != assocStruct1 && isSubAssociation(assocStruct, assocStruct1)) {
          assocStructs.remove(assocStruct);
        }
      }
    }

    return assocStructs;
  }

  public ASTODObject getRealSrc(ASTCDClass srcToFind, AssocStruct assocStruct, ASTCDClass tgtClass,
                                ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
                                ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    List<ASTODObject> typeObjects = getObjectsOfType(srcToFind, srcMap);
    List<ASTODObject> typeObjectsTgt = getObjectsOfType(srcToFind, tgtMap);
    if (assocStruct.getSide().equals(ClassSide.Left)
      && (!typeObjects.isEmpty() || !typeObjectsTgt.isEmpty())
      && (assocStruct.getAssociation().getRight().getCDCardinality().isMult()
      || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
      if (typeObjects.isEmpty()) {
        return typeObjectsTgt.get(0);
      } else {
        return typeObjects.get(0);
      }
    } else if (assocStruct.getSide().equals(ClassSide.Right)
      && (!typeObjects.isEmpty() || !typeObjectsTgt.isEmpty())
      && (assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
      || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
      if (typeObjects.isEmpty()) {
        return typeObjectsTgt.get(0);
      } else {
        return typeObjects.get(0);
      }
    }
    //TODO: if an existing object has only be used as a target object, it can be used as a source object as well - how to add this here?
    for (ASTODObject object : typeObjects) {
      boolean matched = false;
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(object)) {
        if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
          && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        }
      }
      if (!matched) {
        return object;
      }
      typeObjectsTgt.remove(object);
    }
    for (ASTODObject object : typeObjectsTgt) {
      boolean matched = false;
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(object)) {
        if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
          && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirection(assocStruct, assocStructToMatch)
          && (getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()
          .equals(getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a.getSymbol().getInternalQualifiedName()))) {
          matched = true;
          break;
        }
      }
      if (!matched) {
        return object;
      }
    }
    return null;
  }

  public List<ASTODObject> getObjectsOfType(ASTCDClass astcdClass, ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> map) {
    List<ASTODObject> objects = new ArrayList<>();
    for (ASTODObject astodObject : map.keySet()) {
      if (astodObject.getMCObjectType().printType().equals(astcdClass.getSymbol().getInternalQualifiedName())) {
        objects.add(astodObject);
      }
    }
    return objects;
  }

  public boolean isSubClass(ASTCDClass superClass, ASTCDClass subClass) {
    return CDInheritanceHelper.isSuperOf(superClass.getSymbol().getInternalQualifiedName(),
      subClass.getSymbol().getInternalQualifiedName(), helper.getSrcCD()) && subClass != superClass;
  }

  public boolean isSubAssociation(AssocStruct superAssoc, AssocStruct subAssoc) {
    if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Left)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Right)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Left)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Right)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)) {
      return true;
    }
    return false;
  }

  public boolean isSubAssociationInReverse(AssocStruct superAssoc, AssocStruct subAssoc){
    if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Left)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Right)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Left)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Right)
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && CDAssociationHelper.matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).a, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).b)
      && isSubClass(getConnectedClasses(superAssoc.getAssociation(), helper.getSrcCD()).b, getConnectedClasses(subAssoc.getAssociation(), helper.getSrcCD()).a)) {
      return true;
    }
    return false;
  }

  public List<AssocStruct> getTgtAssocsForObject(ASTODObject tgtObject, ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    List<AssocStruct> list = new ArrayList<>(getTgtAssocs(helper.getCDClass(helper.getSrcCD(), tgtObject.getMCObjectType().printType())));
    List<Pair<AssocStruct, ClassSide>> createdAssocs = mapTgt.get(tgtObject);
    List<AssocStruct> copy = new ArrayList<>(list);
    for (AssocStruct assocStruct : copy) {
      for (Pair<AssocStruct, ClassSide> createdAssoc : createdAssocs) {
        if (assocStruct != createdAssoc.a && isSubAssociation(assocStruct, createdAssoc.a)) {
          list.remove(assocStruct);
        }
      }
    }
    return list;
  }

  public String getNameForClass(ASTCDClass astcdClass) {
    map.putIfAbsent(astcdClass, 0);
    map.put(astcdClass, map.get(astcdClass) + 1);
    return astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_") + map.get(astcdClass);
  }
}
