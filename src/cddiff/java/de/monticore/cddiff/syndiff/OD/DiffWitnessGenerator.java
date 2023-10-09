package de.monticore.cddiff.syndiff.OD;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;

public class DiffWitnessGenerator {
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  private final ODBuilder odBuilder = new ODBuilder();
  private Map<ASTCDClass, Integer> map = new HashMap<>();
  private final int maxNumberOfClasses;

  public DiffWitnessGenerator(int maxNumberOfClasses) {
    this.maxNumberOfClasses = 2 * maxNumberOfClasses;
  }

  public int getNumberOfObjects(Set<Package> packages) {
    List<ASTODObject> list = new ArrayList<>();
    for (Package pack : packages) {
      if (pack.getLeftObject() != null
        && !list.contains(pack.getLeftObject())) {
        list.add(pack.getLeftObject());
      }
      if (pack.getRightObject() != null
        && !list.contains(pack.getRightObject())) {
        list.add(pack.getRightObject());
      }
    }
    return list.size();
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

  public Set<Package> createChains(ASTCDAssociation association, int cardinalityLeft, int cardinalityRight,
                                   ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
                                   ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    Set<Package> objectSet = new HashSet<>();
    Pair<ASTCDClass, ASTCDClass> pair = getClassesToUse(association);
    if (pair == null) {
      return null;
    }
    if (cardinalityLeft == 1 && cardinalityRight == 1) {
      Package pack = new Package(pair.a, getNameForClass(pair.a),
        pair.b, getNameForClass(pair.b),
        association, null, false, false);
      addToMaps(pack, mapSrc, mapTgt);
      objectSet.add(pack);
    } else if (cardinalityLeft == 2 && cardinalityRight == 1) {
      Package pack1 = new Package(pair.a, getNameForClass(pair.a),
        pair.b, getNameForClass(pair.b),
        association, null, false, false);
      Package pack2 = new Package(pair.a, getNameForClass(pair.a),
        pack1.getRightObject(),
        association, null, false, false);
      addToMaps(pack1, mapSrc, mapTgt);
      addToMaps(pack2, mapSrc, mapTgt);
      objectSet.add(pack1);
      objectSet.add(pack2);
    } else if (cardinalityLeft == 1 && cardinalityRight == 2) {
      Package pack1 = new Package(pair.a, getNameForClass(pair.a),
        pair.b, getNameForClass(pair.b),
        association, null, false, false);
      Package pack2 = new Package(pack1.getLeftObject(),
        pair.b, getNameForClass(pair.b),
        association, null, false, false);
      addToMaps(pack1, mapSrc, mapTgt);
      addToMaps(pack2, mapSrc, mapTgt);
      objectSet.add(pack1);
      objectSet.add(pack2);
    } else if (cardinalityLeft == 0 || cardinalityRight == 0) {
      Package pack = new Package(pair.a, getNameForClass(pair.a));
      Package pack2 = new Package(pair.b, getNameForClass(pair.b));
      objectSet.add(pack);
      objectSet.add(pack2);
    }
    return objectSet;
  }

  public Pair<ASTCDClass, ASTCDClass> getClassesToUse(ASTCDAssociation association) {
    Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association, helper.getSrcCD());
    Pair<ASTCDClass, ASTCDClass> toUse;
    boolean leftAbstract = pair.a.getModifier().isAbstract();
    boolean rightAbstract = pair.b.getModifier().isAbstract();
    if (leftAbstract && rightAbstract) {
      toUse = new Pair<>(helper.minSubClass(pair.a), helper.minSubClass(pair.b));
    } else if (leftAbstract) {
      toUse = new Pair<>(helper.minSubClass(pair.a), pair.b);
    } else if (rightAbstract) {
      toUse = new Pair<>(pair.a, helper.minSubClass(pair.b));
    } else {
      toUse = pair;
    }
    if (toUse.a == null || toUse.b == null) {
      return null;
    }
    return toUse;
  }

  private void addToMaps(Package pack,
                         ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
                         ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    if (pack.getAstcdAssociation().getCDAssocDir().isDefinitiveNavigableLeft()) {
      mapSrc.put(pack.getRightObject(), new Pair<>(helper.getAssocStrucForClass(
        getConnectedClasses(pack.getAstcdAssociation(), helper.getSrcCD()).b, pack.getAstcdAssociation()), ClassSide.Right));
      mapTgt.put(pack.getLeftObject(), new Pair<>(helper.getAssocStrucForClass(
        getConnectedClasses(pack.getAstcdAssociation(), helper.getSrcCD()).b, pack.getAstcdAssociation()), ClassSide.Left));
    }
    if (pack.getAstcdAssociation().getCDAssocDir().isDefinitiveNavigableRight()) {
      mapSrc.put(pack.getLeftObject(), new Pair<>(helper.getAssocStrucForClass(
        getConnectedClasses(pack.getAstcdAssociation(), helper.getSrcCD()).a, pack.getAstcdAssociation()), ClassSide.Left));
      mapTgt.put(pack.getRightObject(), new Pair<>(helper.getAssocStrucForClass(
        getConnectedClasses(pack.getAstcdAssociation(), helper.getSrcCD()).a, pack.getAstcdAssociation()), ClassSide.Right));
    }
  }

  //Get objects for class
  public Set<ASTODElement> getObjForOD(ASTCDClass astcdClass, Pair<ASTCDAttribute, String> pair) {
    Set<ASTODElement> set = new HashSet<>();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc = ArrayListMultimap.create();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt = ArrayListMultimap.create();
    Set<Package> packages = createChainsForNewClass(astcdClass, new HashSet<>(), mapSrc, mapTgt, pair);
    if (packages == null) {
      return new HashSet<>();
    }
    if (maxNumberOfClasses < getNumberOfObjects(packages)) {
      return new HashSet<>();
    }
    while (!findUnprocessedObjects(packages).isEmpty()) {
      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
        if (maxNumberOfClasses < getNumberOfObjects(packages)) {
          return new HashSet<>();
        }
        Set<Package> toAdd = createChainsForExistingObj(astodObject, packages, mapSrc, mapTgt);
        if (toAdd == null) {
          return new HashSet<>();
        }
        packages.addAll(toAdd);
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
  public Pair<Set<ASTODElement>, ASTODElement> getObjForOD(ASTCDAssociation association, int cardinalityLeft, int cardinalityRight) {
    Set<ASTODElement> set = new HashSet<>();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc = ArrayListMultimap.create();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt = ArrayListMultimap.create();
    Set<Package> packages = createChains(association, cardinalityLeft, cardinalityRight, mapSrc, mapTgt);
    if (packages == null) {
      return new Pair<>(new HashSet<>(), null);
    }
    if (maxNumberOfClasses < getNumberOfObjects(packages)) {
      return new Pair<>(new HashSet<>(), null);
    }
    ASTODElement link = packages.iterator().next().getAssociation();
    if (link == null) {
      link = packages.iterator().next().getLeftObject();
    }
    while (!findUnprocessedObjects(packages).isEmpty()) {
      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
        if (maxNumberOfClasses < getNumberOfObjects(packages)) {
          return new Pair<>(new HashSet<>(), null);
        }
        Set<Package> toAdd = createChainsForExistingObj(astodObject, packages, mapSrc, mapTgt);
        if (toAdd == null) {
          return new Pair<>(new HashSet<>(), null);
        }
        packages.addAll(toAdd);
      }
    }
    map.clear();
    for (Package pack : packages) {
      //unfold packages into set
      if (pack.getAssociation() != null) {
        set.add(pack.getLeftObject());
        set.add(pack.getAssociation());
        set.add(pack.getRightObject());
      }

    }
    return new Pair<>(set, link);
  }

  public Set<Package> createChainsForNewClass(ASTCDClass astcdClass, Set<Package> packages,
                                              ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
                                              ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt,
                                              Pair<ASTCDAttribute, String> pair) {
    //System.out.println("createChainsForNewClass " + astcdClass.getSymbol().getInternalQualifiedName());
    ASTODObject srcObject = odBuilder.buildObj(getNameForClass(astcdClass), astcdClass.getSymbol().getInternalQualifiedName(),
      helper.getSuperClasses(astcdClass),
      helper.getAttributesOD(astcdClass, pair));
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
        && assocStruct.isToBeProcessed()
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        ASTCDClass rightClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
        ASTCDClass subclass = helper.minSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b);
        mustHaveAdded = true;
        if (helper.isLoopStruct(assocStruct)) {
          tgtObject = srcObject;
        }
        if (tgtObject == null && !rightClass.getModifier().isAbstract()) {
          tgtObject = getTgtObject(astcdClass, assocStruct, rightClass, mapSrc, mapTgt);
        }
        if (tgtObject == null) {
          tgtObject = getSubTgtObject(astcdClass, assocStruct, rightClass, mapSrc, mapTgt);
        }
        if (tgtObject == null
          && subclass != null
          && (rightClass.getModifier().isAbstract()
          || (helper.getClassSize(subclass)
          < helper.getClassSize(rightClass)))) {
          if (singletonObj(subclass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(subclass), subclass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(subclass),
            helper.getAttributesOD(subclass, null));
        } else if (tgtObject == null
          && !rightClass.getModifier().isAbstract()) {
          if (singletonObj(rightClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(rightClass), rightClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(rightClass),
            helper.getAttributesOD(rightClass, null));
        }
        if (tgtObject == null) {
          return null;
        }

        hasAdded = true;
        mapSrc.put(srcObject, new Pair<>(assocStruct, ClassSide.Left));
        mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Right));
        Package pack = new Package(srcObject, tgtObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
        packages.add(pack);

      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.isToBeProcessed()
        && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        ASTCDClass leftClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass subclass = helper.minSubClass(leftClass);
        mustHaveAdded = true;
        if (helper.isLoopStruct(assocStruct)) {
          tgtObject = srcObject;
        }
        if (tgtObject == null && !leftClass.getModifier().isAbstract()) {
          tgtObject = getTgtObject(astcdClass, assocStruct, leftClass, mapSrc, mapTgt);
        }
        if (tgtObject == null) {
          tgtObject = getSubTgtObject(astcdClass, assocStruct, leftClass, mapSrc, mapTgt);
        }

        if (tgtObject == null
          && subclass != null
          && (leftClass.getModifier().isAbstract()
          || (helper.getClassSize(subclass)
          < helper.getClassSize(leftClass)))) {
          if (singletonObj(subclass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(subclass), subclass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(subclass),
            helper.getAttributesOD(subclass, null));
        } else if (tgtObject == null
          && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(leftClass), leftClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(leftClass),
            helper.getAttributesOD(leftClass, null));
        }
        if (tgtObject == null) {
          return null;
        }

        hasAdded = true;
        mapSrc.put(srcObject, new Pair<>(assocStruct, ClassSide.Right));
        mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Left));
        Package pack = new Package(tgtObject, srcObject, assocStruct.getAssociation(), ClassSide.Right, false, true);
        packages.add(pack);
      }
    }
    for (AssocStruct assocStruct : getTgtAssocs(astcdClass)) {
      ASTODObject realSrcObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        ASTCDClass leftClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass sub = helper.minSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a);
        if (!leftClass.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(leftClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }
        if (realSrcObject == null) {
          realSrcObject = getSubRealSrc(leftClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }

        if (realSrcObject == null
          && sub != null
          && (leftClass.getModifier().isAbstract()
          || (helper.getClassSize(sub)
          < helper.getClassSize(leftClass)))) {
          if (singletonObj(sub, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject = odBuilder.buildObj(getNameForClass(sub), sub.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(sub),
            helper.getAttributesOD(sub, null));

        } else if (realSrcObject == null
          && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject = odBuilder.buildObj(getNameForClass(leftClass), leftClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(leftClass),
            helper.getAttributesOD(leftClass, null));
        }
        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Left));
        mapTgt.put(srcObject, new Pair<>(assocStruct, ClassSide.Right));
        Package pack = new Package(realSrcObject, srcObject, assocStruct.getAssociation(), ClassSide.Right, false, true);
        packages.add(pack);

      } else {
        ASTCDClass rightClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
        ASTCDClass sub = helper.minSubClass(rightClass);
        if (!rightClass.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(rightClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }
        if (realSrcObject != null) {
          realSrcObject = getSubRealSrc(rightClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }

        if (realSrcObject == null
          && sub != null
          && (rightClass.getModifier().isAbstract()
          || (helper.getClassSize(sub)
          < helper.getClassSize(rightClass)))) {
          if (singletonObj(sub, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject = odBuilder.buildObj(getNameForClass(sub), sub.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(sub),
            helper.getAttributesOD(sub, null));
        } else if (realSrcObject == null
          && !rightClass.getModifier().isAbstract()) {
          if (singletonObj(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject = odBuilder.buildObj(getNameForClass(rightClass), rightClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(rightClass),
            helper.getAttributesOD(rightClass, null));
        }

        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Right));
        mapTgt.put(srcObject, new Pair<>(assocStruct, ClassSide.Left));
        Package pack = new Package(srcObject, realSrcObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
        packages.add(pack);
      }
    }
    if (!hasAdded) {
      Package pack = new Package(srcObject);
      packages.add(pack);
    }
//    for (Package pack : packages) {
//      if (pack.getAstcdAssociation() != null){
//        System.out.println(pack.getLeftObject().getName() + " " + pack.getRightObject().getName());
//      }
//    }
    return packages;
  }

  public Set<Package> createChainsForExistingObj(ASTODObject object, Set<Package> packages,
                                                 ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
                                                 ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
  //  System.out.println("createChainsForExistingObj " + object.getName());
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
    List<Pair<AssocStruct, ClassSide>> createdAssocsSrc = mapSrc.get(object);
    for (Pair<AssocStruct, ClassSide> pair : createdAssocs) {
      for (AssocStruct assocStruct : copy) {
        if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)))
          && (helper.sameAssocStruct(assocStruct, pair.a)
          || helper.isSubAssociationSrcSrc(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        } else if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)))
          && (helper.sameAssocStructInReverse(assocStruct, pair.a)
          || helper.isSubAssociationSrcSrc(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        }
      }
    }
    List<AssocStruct> copy2 = new ArrayList<>(list);
    for (Pair<AssocStruct, ClassSide> pair : createdAssocsSrc) {
      for (AssocStruct assocStruct : copy2) {
        if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)))
          && (helper.sameAssocStruct(assocStruct, pair.a)
          || helper.isSubAssociationSrcSrc(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        } else if (((pair.b.equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))
          || (pair.b.equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)))
          && (helper.sameAssocStructInReverse(assocStruct, pair.a)
          || helper.isSubAssociationSrcSrc(assocStruct, pair.a))) {
          list.remove(assocStruct);
          break;
        }
      }
    }
    boolean mustHaveAdded = false;
    boolean hasAdded = false;
    for (AssocStruct assocStruct : list) {
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.isToBeProcessed()
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        ASTCDClass rightClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
        ASTCDClass sub = helper.minSubClass(rightClass);
        mustHaveAdded = true;
        if (helper.isLoopStruct(assocStruct)) {
          tgtObject = object;
        }
        if (tgtObject == null && !rightClass.getModifier().isAbstract()) {
          tgtObject = getTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, rightClass, mapSrc, mapTgt);
        }
        if (tgtObject == null) {
          tgtObject = getSubTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, mapSrc, mapTgt);
        }

        if (tgtObject == null
          && sub != null
          && (rightClass.getModifier().isAbstract()
          || (helper.getClassSize(sub)
          < helper.getClassSize(rightClass)))) {
          if (singletonObj(sub, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(sub), sub.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(sub),
            helper.getAttributesOD(sub, null));
        } else if (tgtObject == null
          && !rightClass.getModifier().isAbstract()) {
          if (singletonObj(rightClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(rightClass), rightClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(rightClass),
            helper.getAttributesOD(rightClass, null));
        }

        if (tgtObject == null) {
          return null;
        }

        if (tgtObject != null) {
          hasAdded = true;
          mapSrc.put(object, new Pair<>(assocStruct, ClassSide.Left));
          mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Right));
          Package pack = new Package(object, tgtObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
          packages.add(pack);
        }

      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.isToBeProcessed()
        && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        ASTCDClass leftClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass sub = helper.minSubClass(leftClass);
        mustHaveAdded = true;
        if (helper.isLoopStruct(assocStruct)) {
          tgtObject = object;
        }
        if (tgtObject == null && !leftClass.getModifier().isAbstract()) {
          tgtObject = getTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, leftClass, mapSrc, mapTgt);
        }
        if (tgtObject == null) {
          tgtObject = getSubTgtObject(helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()),
            assocStruct, leftClass, mapSrc, mapTgt);
        }
        if (tgtObject == null
          && helper.minSubClass(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a) != null
          && (leftClass.getModifier().isAbstract()
          || (helper.getClassSize(sub)
          < helper.getClassSize(leftClass)))) {
          if (singletonObj(sub, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(sub), sub.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(sub),
            helper.getAttributesOD(sub, null));
        } else if (tgtObject == null
          && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject = odBuilder.buildObj(getNameForClass(leftClass), leftClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(leftClass),
            helper.getAttributesOD(leftClass, null));
        }
        if (tgtObject == null) {
          return null;
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
    for (AssocStruct assocStruct : getTgtAssocsForObject(object, mapSrc, mapTgt)) {
      ASTODObject realSrcObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        ASTCDClass leftClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass sub = helper.minSubClass(leftClass);
        if (!leftClass.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(leftClass,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
        }
        if (realSrcObject != null) {
          realSrcObject = getSubRealSrc(leftClass,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
        }

        if (realSrcObject == null
          && sub != null
          && (leftClass.getModifier().isAbstract()
          || (helper.getClassSize(sub)
          < helper.getClassSize(leftClass)))) {
          if (singletonObj(sub, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject = odBuilder.buildObj(getNameForClass(sub), sub.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(sub),
            helper.getAttributesOD(sub, null));
        } else if (realSrcObject == null
          && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject = odBuilder.buildObj(getNameForClass(leftClass), leftClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(leftClass),
            helper.getAttributesOD(leftClass, null));
        }

        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Left));
        mapTgt.put(object, new Pair<>(assocStruct, ClassSide.Right));
        Package pack = new Package(realSrcObject, object, assocStruct.getAssociation(), ClassSide.Right, false, true);
        packages.add(pack);
      } else {
        ASTCDClass rightClass = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
        ASTCDClass sub = helper.minSubClass(rightClass);
        if (!rightClass.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(rightClass,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
        }
        if (realSrcObject != null) {
          realSrcObject = getSubRealSrc(rightClass,
            assocStruct, helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType()), mapSrc, mapTgt);
        }

        if (realSrcObject == null
          && sub != null
          && (rightClass.getModifier().isAbstract()
          || (helper.getClassSize(sub)
          < helper.getClassSize(rightClass)))) {
          realSrcObject = odBuilder.buildObj(getNameForClass(sub), sub.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(sub),
            helper.getAttributesOD(sub, null));
        } else if (realSrcObject == null
          && !rightClass.getModifier().isAbstract()) {
          realSrcObject = odBuilder.buildObj(getNameForClass(rightClass), rightClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperClasses(rightClass),
            helper.getAttributesOD(rightClass, null));
        }

        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Right));
        mapTgt.put(object, new Pair<>(assocStruct, ClassSide.Left));
        Package pack = new Package(object, realSrcObject, assocStruct.getAssociation(), ClassSide.Left, true, false);
        packages.add(pack);
      }
    }
    if (!hasAdded) {
      Package pack = new Package(object);
      packages.add(pack);
    }
//    for (Package pack : packages) {
//      if (pack.getAstcdAssociation() != null){
//        System.out.println(pack.getLeftObject().getName() + " " + pack.getRightObject().getName());
//      }
//    }
    return packages;
  }

  //add change to srcObject - done
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
    Set<ASTODObject> listToIterate = new HashSet<>();
    listToIterate.addAll(typeObjects);
    listToIterate.addAll(typeObjectsSrc);
    ASTCDClass leftClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
    ASTCDClass rightClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
    for (ASTODObject object : listToIterate) {
      boolean matched = false;
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(object)) {
        ASTCDClass leftClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass rightClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b;
        if (assocStruct.getSide().equals(ClassSide.Left) //not-searched class of assocStruc on the left side
          && assocStructToMatch.b.equals(ClassSide.Left) //searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && ((leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, leftClassAssocStruct)))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right) //src of assocStruc on the right side
          && assocStructToMatch.b.equals(ClassSide.Right) //tgt of assocStructToMatch on the right side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && ((rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, rightClassAssocStruct)))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, leftClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, rightClassAssocStruct))) {
          matched = true;
          break;
        }
      }
      if (matched) {
        //go to next object
        continue;
      }
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(object)) {
        ASTCDClass leftClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass rightClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b;
        if (assocStruct.getSide().equals(ClassSide.Left)//not-searched class on the left side
          && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, leftClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, leftClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, rightClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
          && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, rightClassAssocStruct))) {
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
      for (AssocStruct assocStructFromCLass : helper.getSrcMap().get(subClass)) {
        if (isSubAssociationInReverse(assocStruct, assocStructFromCLass)) {
          assocStruct = assocStructFromCLass;
          changed = true;
        }
      }
      if (!changed) {
        for (AssocStruct otherAssoc : helper.getOtherAssocFromSuper(subClass)) {
          if (helper.isSubAssociationSrcSrc(assocStruct, otherAssoc)) {
            assocStruct = otherAssoc;
          }
        }
      }
      if (!objectsOfType.isEmpty() || !objectsOfTypeSrc.isEmpty()) {
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
      Set<ASTODObject> listToIterate = new HashSet<>();
      listToIterate.addAll(objectsOfType);
      listToIterate.addAll(objectsOfTypeSrc);
      ASTCDClass leftClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
      ASTCDClass rightClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
      for (ASTODObject subObject : listToIterate) {
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> subAssocStruct : tgtMap.get(subObject)) {
          ASTCDClass leftClassToMatch = getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a;
          ASTCDClass rightClassToMatch = getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b;
          if (assocStruct.getSide().equals(ClassSide.Left)//not-searched class of assocStruc on the left side
            && subAssocStruct.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(leftClassAssocStruct, rightClassToMatch)
            || isSubClass(rightClassToMatch, leftClassAssocStruct))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(leftClassAssocStruct, leftClassToMatch)
            || isSubClass(leftClassToMatch, leftClassAssocStruct))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(rightClassAssocStruct, leftClassToMatch)
            || isSubClass(leftClassToMatch, rightClassAssocStruct))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(rightClassAssocStruct, rightClassToMatch)
            || isSubClass(rightClassToMatch, rightClassAssocStruct))) {
            matched = true;
            break;
          }
        }
        if (matched) {
          continue;
        }
        for (Pair<AssocStruct, ClassSide> subAssocStruct : srcMap.get(subObject)) {
          ASTCDClass leftClassToMatch = getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a;
          ASTCDClass rightClassToMatch = getConnectedClasses(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b;
          if (assocStruct.getSide().equals(ClassSide.Left)//not-searched class of assocStruc on the left side
            && subAssocStruct.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(leftClassAssocStruct, rightClassToMatch)
            && isSubClass(leftClassToMatch, rightClassAssocStruct)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(leftClassAssocStruct, leftClassToMatch)
            && isSubClass(leftClassToMatch, leftClassAssocStruct)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(rightClassAssocStruct, leftClassToMatch)
            && isSubClass(leftClassToMatch, rightClassAssocStruct)) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && subAssocStruct.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), subAssocStruct.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), subAssocStruct.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
            && isSubClass(rightClassAssocStruct, rightClassToMatch)
            && isSubClass(rightClassToMatch, rightClassAssocStruct)) {
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
      Set<ASTODObject> listToIterate = new HashSet<>();
      listToIterate.addAll(objectsOfType);
      listToIterate.addAll(objectsOfTypeTgt);
      for (ASTODObject subObject : listToIterate) {
        for (AssocStruct assocStructFromCLass : helper.getSrcMap().get(helper.getCDClass(helper.getSrcCD(), subObject.getMCObjectType().printType()))) {
          if (helper.isSubAssociationSrcSrc(assocStruct, assocStructFromCLass)) {
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
        ASTCDClass leftClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass rightClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(subObject)) {
          ASTCDClass leftClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a;
          ASTCDClass rightClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b;
          if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
            && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, rightClassToMatch)
            || isSubClass(rightClassToMatch, srcToFind))) {//not needed - tgt must be same
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, leftClassToMatch)
            || isSubClass(leftClassToMatch, srcToFind))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, leftClassToMatch)
            || isSubClass(leftClassToMatch, srcToFind))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, rightClassToMatch)
            || isSubClass(rightClassToMatch, srcToFind))) {
            matched = true;
            break;
          }
        }
        if (matched) {
          continue;
        }
        for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(subObject)) {
          ASTCDClass leftClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a;
          ASTCDClass rightClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b;
          if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
            && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, rightClassToMatch)
            || isSubClass(rightClassToMatch, srcToFind))) {//not needed - tgt must be same
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, leftClassToMatch)
            || isSubClass(leftClassToMatch, srcToFind))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, leftClassToMatch)
            || isSubClass(leftClassToMatch, srcToFind))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
            .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
            || isSubClass(srcToFind, rightClassToMatch)
            || isSubClass(rightClassToMatch, srcToFind))) {
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
        if (assocStruct != assocStruct1 && helper.isSubAssociationSrcSrc(assocStruct, assocStruct1)) {
          assocStructs.remove(assocStruct1);
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
    Set<ASTODObject> listToIterate = new HashSet<>();
    listToIterate.addAll(typeObjects);
    listToIterate.addAll(typeObjectsTgt);
    ASTCDClass leftClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a;
    ASTCDClass rightClassAssocStruct = getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b;
    for (ASTODObject object : listToIterate) {
      boolean matched = false;
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(object)) {
        ASTCDClass leftClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass rightClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b;
        if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
          && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, rightClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, rightClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, leftClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, leftClassAssocStruct))) {
          matched = true;
          break;
        }
      }
      if (matched) {
        continue;
      }
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(object)) {
        ASTCDClass leftClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a;
        ASTCDClass rightClassToMatch = getConnectedClasses(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b;
        if (assocStruct.getSide().equals(ClassSide.Left)//searched class of assocStruc on the left side
          && assocStructToMatch.b.equals(ClassSide.Left)//searched class of assocStructToMatch on the left side!!!
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, rightClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (rightClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(rightClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, rightClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Left)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getLeft())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, rightClassToMatch)
          || isSubClass(rightClassToMatch, leftClassAssocStruct))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStructToMatch.b.equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getLeft(), assocStructToMatch.a.getAssociation().getLeft())
          && CDAssociationHelper.matchRoleNames(assocStruct.getAssociation().getRight(), assocStructToMatch.a.getAssociation().getRight())
          && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
          && (leftClassAssocStruct.getSymbol().getInternalQualifiedName()
          .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
          || isSubClass(leftClassAssocStruct, leftClassToMatch)
          || isSubClass(leftClassToMatch, leftClassAssocStruct))) {
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

  //************** Idead for adding "Singletons" to the model **************
  //Singletons allow only one object of that class
  //No change to getTgtObject/getSrcObject needed - if the relation allows many, we have no prob; if it gets matched - we can't take this object
  //Change only to creating new objects - if the class is a singleton, we have to check if there is already an object of that class
  //If there is one, we return null, as we can't instantiate a new object

  public boolean singletonObj(ASTCDClass astcdClass,
                              ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
                              ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    return astcdClass.getModifier().isPresentStereotype() && astcdClass.getModifier().getStereotype().contains("singleton")
      && (!getObjectsOfType(astcdClass, srcMap).isEmpty() || !getObjectsOfType(astcdClass, tgtMap).isEmpty());
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
      subClass.getSymbol().getInternalQualifiedName(), helper.getSrcCD());
  }

  public boolean isSubAssociationInReverse(AssocStruct superAssoc, AssocStruct subAssoc) {
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

  public List<AssocStruct> getTgtAssocsForObject(ASTODObject tgtObject,
                                                 ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
                                                 ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    List<AssocStruct> list = new ArrayList<>(getTgtAssocs(helper.getCDClass(helper.getSrcCD(), tgtObject.getMCObjectType().printType())));
    List<Pair<AssocStruct, ClassSide>> createdAssocs = mapTgt.get(tgtObject);
    List<AssocStruct> copy = new ArrayList<>(list);
    for (AssocStruct assocStruct : copy) {
      for (Pair<AssocStruct, ClassSide> createdAssoc : createdAssocs) {
        if (helper.isSubAssociationSrcSrc(assocStruct, createdAssoc.a)) {
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
