package de.monticore.cddiff.syn2semdiff.odgen;

import static de.monticore.cddiff.syn2semdiff.odgen.Syn2SemDiffHelper.getConnectedTypes;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syn2semdiff.datastructures.AssocStruct;
import de.monticore.cddiff.syn2semdiff.datastructures.ClassSide;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;

public class ODGenerator {
  private final Syn2SemDiffHelper helper;
  private final ODBuilder odBuilder = new ODBuilder();
  private Map<ASTCDClass, Integer> map = new HashMap<>();
  private final int maxNumberOfClasses;

  /**
   * Constructor for DiffWitnessGenerator. Set the maximum number of classes to be used in the
   * witness.
   *
   * @param maxNumberOfClasses maximum number of classes to be used in the witness.
   * @param helper helper for accessing the maps with AssocStructs.
   */
  public ODGenerator(int maxNumberOfClasses, Syn2SemDiffHelper helper) {
    this.maxNumberOfClasses = 2 * maxNumberOfClasses;
    this.helper = helper;
  }

  /**
   * Get number of generated objects in the diagram.
   *
   * @param packages set of packages.
   * @return number of generated objects in the diagram.
   */
  public int getNumberOfObjects(Set<Package> packages) {
    List<ASTODObject> list = new ArrayList<>();
    for (Package pack : packages) {
      if (pack.getLeftObject() != null && !list.contains(pack.getLeftObject())) {
        list.add(pack.getLeftObject());
      }
      if (pack.getRightObject() != null && !list.contains(pack.getRightObject())) {
        list.add(pack.getRightObject());
      }
    }
    return list.size();
  }

  /**
   * Get objects that have yet not been processed. Those are objects that had their attribute
   * isProcessed set to false in all containing packages.
   *
   * @param packages set of packages.
   * @return set of unprocessed objects.
   */
  public static Set<ASTODObject> findUnprocessedObjects(Set<Package> packages) {
    Map<ASTODObject, Set<Boolean>> unprocessedMap = new HashMap<>();

    for (Package pack : packages) {
      if (pack.getLeftObject() != null) {
        unprocessedMap
            .computeIfAbsent(pack.getLeftObject(), k -> new HashSet<>())
            .add(pack.isProcessedLeft());
      }
      if (pack.getRightObject() != null) {
        unprocessedMap
            .computeIfAbsent(pack.getRightObject(), k -> new HashSet<>())
            .add(pack.isProcessedRight());
      }
    }

    Set<ASTODObject> unprocessedObjects = new HashSet<>();
    for (Map.Entry<ASTODObject, Set<Boolean>> entry : unprocessedMap.entrySet()) {
      if (!entry.getValue().contains(true)
          && entry.getValue().contains(false)) { // Object unprocessed in only one side
        unprocessedObjects.add(entry.getKey());
      }
    }
    return unprocessedObjects;
  }

  /**
   * Create the elements for an object diagram starting from a given association. If the difference
   * of the association is in the cardinalities, then the cardinalities are used. Otherwise, the
   * cardinalities are set to 1.
   *
   * @param association association to start from.
   * @param cardinalityLeft cardinality of the left side of the association.
   * @param cardinalityRight cardinality of the right side of the association.
   * @param mapSrc map of objects that are used as source.
   * @param mapTgt map of objects that are used as target.
   * @return set of packages that contain the elements of the object diagram.
   */
  public Set<Package> createChains(
      ASTCDAssociation association,
      int cardinalityLeft,
      int cardinalityRight,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    Set<Package> objectSet = new HashSet<>();
    Pair<ASTCDClass, ASTCDClass> pair = getClassesToUse(association);
    if (pair == null) {
      return null;
    }
    if (cardinalityLeft == 1 && cardinalityRight == 1) {
      Package pack =
          new Package(
              pair.a,
              getNameForClass(pair.a),
              pair.b,
              getNameForClass(pair.b),
              association,
              null,
              false,
              false,
              helper);
      addToMaps(pack, mapSrc, mapTgt);
      objectSet.add(pack);
    } else if (cardinalityLeft == 2 && cardinalityRight == 1) {
      Package pack1 =
          new Package(
              pair.a,
              getNameForClass(pair.a),
              pair.b,
              getNameForClass(pair.b),
              association,
              null,
              false,
              false,
              helper);
      Package pack2 =
          new Package(
              pair.a,
              getNameForClass(pair.a),
              pack1.getRightObject(),
              association,
              null,
              false,
              false,
              helper);
      addToMaps(pack1, mapSrc, mapTgt);
      addToMaps(pack2, mapSrc, mapTgt);
      objectSet.add(pack1);
      objectSet.add(pack2);
    } else if (cardinalityLeft == 1 && cardinalityRight == 2) {
      Package pack1 =
          new Package(
              pair.a,
              getNameForClass(pair.a),
              pair.b,
              getNameForClass(pair.b),
              association,
              null,
              false,
              false,
              helper);
      Package pack2 =
          new Package(
              pack1.getLeftObject(),
              pair.b,
              getNameForClass(pair.b),
              association,
              null,
              false,
              false,
              helper);
      addToMaps(pack1, mapSrc, mapTgt);
      addToMaps(pack2, mapSrc, mapTgt);
      objectSet.add(pack1);
      objectSet.add(pack2);
    } else if (cardinalityLeft == 0 && cardinalityRight == 1) {
      Package pack = new Package(pair.b, getNameForClass(pair.b), helper);
      objectSet.add(pack);
    } else if (cardinalityLeft == 1 && cardinalityRight == 0) {
      Package pack = new Package(pair.a, getNameForClass(pair.a), helper);
      objectSet.add(pack);
    }
    return objectSet;
  }

  /**
   * Get the two non0-abstract class to use for an association.
   *
   * @param association association.
   * @return pair of classes to use (or null if such don't exist).
   */
  public Pair<ASTCDClass, ASTCDClass> getClassesToUse(ASTCDAssociation association) {
    Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(association, helper.getSrcCD());
    Pair<ASTCDClass, ASTCDClass> toUse;
    Optional<ASTCDClass> left;
    Optional<ASTCDClass> right;
    boolean leftAbstract = pair.a.getModifier().isAbstract();
    boolean rightAbstract = pair.b.getModifier().isAbstract();
    if (pair.a instanceof ASTCDClass
        && pair.b instanceof ASTCDClass
        && !(leftAbstract && rightAbstract)) {
      toUse = new Pair<>((ASTCDClass) pair.a, (ASTCDClass) pair.b);
    } else {
      if (leftAbstract || pair.a instanceof ASTCDInterface) {
        left = helper.minSubClass(pair.a);
      } else {
        left = Optional.of((ASTCDClass) pair.a);
      }
      if (rightAbstract || pair.b instanceof ASTCDInterface) {
        right = helper.minSubClass(pair.b);
      } else {
        right = Optional.of((ASTCDClass) pair.b);
      }
      if (left.isPresent() && right.isPresent()) {
        toUse = new Pair<>(left.get(), right.get());
      } else {
        return null;
      }
    }
    return toUse;
  }

  /**
   * Update the two maps based on a created package. This means that the associated objects are
   * updated in the corresponding maps.
   *
   * @param pack package that was created.
   * @param mapSrc map of objects that are used as source.
   * @param mapTgt map of objects that are used as target.
   */
  private void addToMaps(
      Package pack,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    if (pack.getAstcdAssociation().getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (helper.getAssocStrucForClass(
              getConnectedTypes(pack.getAstcdAssociation(), helper.getSrcCD()).b,
              pack.getAstcdAssociation())
          != null) {
        mapSrc.put(
            pack.getRightObject(),
            new Pair<>(
                helper.getAssocStrucForClass(
                    getConnectedTypes(pack.getAstcdAssociation(), helper.getSrcCD()).b,
                    pack.getAstcdAssociation()),
                ClassSide.Right));
        mapTgt.put(
            pack.getLeftObject(),
            new Pair<>(
                helper.getAssocStrucForClass(
                    getConnectedTypes(pack.getAstcdAssociation(), helper.getSrcCD()).b,
                    pack.getAstcdAssociation()),
                ClassSide.Left));
      }
    }
    if (pack.getAstcdAssociation().getCDAssocDir().isDefinitiveNavigableRight()) {
      if (helper.getAssocStrucForClass(
              getConnectedTypes(pack.getAstcdAssociation(), helper.getSrcCD()).a,
              pack.getAstcdAssociation())
          != null) {
        mapSrc.put(
            pack.getLeftObject(),
            new Pair<>(
                helper.getAssocStrucForClass(
                    getConnectedTypes(pack.getAstcdAssociation(), helper.getSrcCD()).a,
                    pack.getAstcdAssociation()),
                ClassSide.Left));
        mapTgt.put(
            pack.getRightObject(),
            new Pair<>(
                helper.getAssocStrucForClass(
                    getConnectedTypes(pack.getAstcdAssociation(), helper.getSrcCD()).a,
                    pack.getAstcdAssociation()),
                ClassSide.Right));
      }
    }
  }

  /**
   * Create the elements for an object diagram starting from a given class. If the difference is
   * based on an added constant, the pair is used.
   *
   * @param astcdType class to start from.
   * @param pair pair of attribute and enum constant.
   * @return set of successfully created elements. If the set is empty, an object diagram for this
   *     class isn't possible.
   */
  public Set<ASTODElement> getObjForOD(ASTCDType astcdType, Pair<ASTCDAttribute, String> pair) {
    Set<ASTODElement> set = new HashSet<>();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc =
        ArrayListMultimap.create();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt =
        ArrayListMultimap.create();
    ASTCDClass classToUse;
    if (astcdType instanceof ASTCDInterface || astcdType.getModifier().isAbstract()) {
      Optional<ASTCDClass> sub = helper.minSubClass(astcdType);
      if (sub.isPresent()) {
        classToUse = sub.get();
      } else {
        return new HashSet<>();
      }
    } else {
      classToUse = (ASTCDClass) astcdType;
    }
    Set<Package> packages =
        createChainsForNewClass(classToUse, new HashSet<>(), mapSrc, mapTgt, pair);
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
      // unfold packages into set
      if (pack.getAssociation() != null) {
        set.add(pack.getAssociation());
        set.add(pack.getRightObject());
      }
      set.add(pack.getLeftObject());
    }
    return set;
  }

  public Set<ASTODElement> getObjForODSpec(ASTCDType astcdClass) {
    Set<ASTODElement> set = new HashSet<>();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc =
        ArrayListMultimap.create();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt =
        ArrayListMultimap.create();
    ASTCDClass classToUse;
    if (astcdClass instanceof ASTCDInterface) {
      Optional<ASTCDClass> sub = helper.minSubClass(astcdClass);
      if (sub.isPresent()) {
        classToUse = sub.get();
      } else {
        return new HashSet<>();
      }
    } else {
      classToUse = (ASTCDClass) astcdClass;
    }
    Set<Package> packages =
        createChainsForNewClass(classToUse, new HashSet<>(), mapSrc, mapTgt, null);
    if (packages == null) {
      return new HashSet<>();
    }
    packages.addAll(createChainsForNewClass(classToUse, packages, mapSrc, mapTgt, null));
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
      // unfold packages into set
      if (pack.getAssociation() != null) {
        set.add(pack.getAssociation());
        set.add(pack.getRightObject());
      }
      set.add(pack.getLeftObject());
    }
    return set;
  }

  /**
   * Create the elements for an object diagram starting from a given class.
   *
   * @param association association to start from.
   * @param cardinalityLeft cardinality of the left side of the association.
   * @param cardinalityRight cardinality of the right side of the association.
   * @return pair of successfully created elements with the link that causes the semantic
   *     difference. If the set is empty, an object diagram for this association isn't possible.
   */
  public Pair<Set<ASTODElement>, ASTODElement> getObjForOD(
      ASTCDAssociation association, int cardinalityLeft, int cardinalityRight) {
    Set<ASTODElement> set = new HashSet<>();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc =
        ArrayListMultimap.create();
    ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt =
        ArrayListMultimap.create();
    Set<Package> packages =
        createChains(association, cardinalityLeft, cardinalityRight, mapSrc, mapTgt);
    if (packages == null) {
      return new Pair<>(new HashSet<>(), null);
    }
    if (maxNumberOfClasses < getNumberOfObjects(packages)) {
      return new Pair<>(new HashSet<>(), null);
    }
    ASTODElement link = null;
    for (Package pack : packages) {
      if (pack.getAssociation() != null && pack.getAstcdAssociation().equals(association)) {
        link = pack.getAssociation();
        break;
      }
    }
    if (link == null && !packages.isEmpty()) {
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
      // unfold packages into set
      if (pack.getAssociation() != null) {
        set.add(pack.getLeftObject());
        set.add(pack.getAssociation());
        set.add(pack.getRightObject());
      }
    }
    return new Pair<>(set, link);
  }

  /**
   * Create a new object for a class. This is only used at the start of the algorithm for creating
   * object diagrams. The function considers only associations that are needed based on the
   * cardinality and their attribute toBeProcessed is set to true. The function works based on the
   * side of the AssocStruct in the map. Because of that the sides of objects in the Packages
   * correspond to the sides of the AssocStructs in the maps. The function first computes the cases
   * where the class is source and in the second part it computes the cases where the class is
   * target. When packages are created, for the other associated objects the attribute isProcessed
   * is set to false, as here we process only the given class.
   *
   * @param astcdClass class to create object for.
   * @param packages current set of packages.
   * @param mapSrc map of objects that are used as source.
   * @param mapTgt map of objects that are used as target.
   * @param pair pair of attribute and enum constant.
   * @return set of packages that contain the current elements of the object diagram. If the set is
   *     null, for a needed association a source or target object couldn't be created.
   */
  public Set<Package> createChainsForNewClass(
      ASTCDClass astcdClass,
      Set<Package> packages,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt,
      Pair<ASTCDAttribute, String> pair) {
    ASTODObject srcObject =
        odBuilder.buildObj(
            getNameForClass(astcdClass),
            astcdClass.getSymbol().getInternalQualifiedName(),
            helper.getSuperTypes(astcdClass),
            helper.getAttributesOD(astcdClass, pair));
    if (helper.getSrcMap().get(astcdClass).isEmpty()) {
      Package pack = new Package(srcObject, helper);
      packages.add(pack);
    }
    boolean hasAdded = false;
    List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
    for (AssocStruct assocStruct : list) {
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStruct.isToBeProcessed()
          && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
              || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        Optional<ASTCDClass> right =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
        if (right.isEmpty()) {
          return null;
        }
        ASTCDClass rightClass = right.get();
        Optional<ASTCDClass> subclass =
            helper.minSubClass(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
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
            && subclass.isPresent()
            && (rightClass.getModifier().isAbstract()
                || (helper.getClassSize(subclass.get()) <= helper.getClassSize(rightClass)))) {
          if (singletonObj(subclass.get(), mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(subclass.get()),
                  subclass.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(subclass.get()),
                  helper.getAttributesOD(subclass.get(), null));
        } else if (tgtObject == null && !rightClass.getModifier().isAbstract()) {
          if (singletonObj(rightClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(rightClass),
                  rightClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(rightClass),
                  helper.getAttributesOD(rightClass, null));
        }
        if (tgtObject == null) {
          return null;
        }

        hasAdded = true;
        mapSrc.put(srcObject, new Pair<>(assocStruct, ClassSide.Left));
        mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Right));
        Package pack =
            new Package(
                srcObject,
                tgtObject,
                assocStruct.getAssociation(),
                ClassSide.Left,
                true,
                false,
                helper);
        packages.add(pack);

      } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStruct.isToBeProcessed()
          && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
              || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        Optional<ASTCDClass> left =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
        if (left.isEmpty()) {
          return null;
        }
        ASTCDClass leftClass = left.get();
        Optional<ASTCDClass> subclass = helper.minSubClass(leftClass);
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
            && subclass.isPresent()
            && (leftClass.getModifier().isAbstract()
                || (helper.getClassSize(subclass.get()) <= helper.getClassSize(leftClass)))) {
          if (singletonObj(subclass.get(), mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(subclass.get()),
                  subclass.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(subclass.get()),
                  helper.getAttributesOD(subclass.get(), null));
        } else if (tgtObject == null && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(leftClass),
                  leftClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(leftClass),
                  helper.getAttributesOD(leftClass, null));
        }
        if (tgtObject == null) {
          return null;
        }

        hasAdded = true;
        mapSrc.put(srcObject, new Pair<>(assocStruct, ClassSide.Right));
        mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Left));
        Package pack =
            new Package(
                tgtObject,
                srcObject,
                assocStruct.getAssociation(),
                ClassSide.Right,
                false,
                true,
                helper);
        packages.add(pack);
      }
    }
    for (AssocStruct assocStruct : getTgtAssocs(astcdClass)) {
      ASTODObject realSrcObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        Optional<ASTCDClass> left =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
        if (left.isEmpty()) {
          return null;
        }
        ASTCDClass leftClass = left.get();
        Optional<ASTCDClass> sub =
            helper.minSubClass(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
        if (!leftClass.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(leftClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }
        if (realSrcObject == null) {
          realSrcObject = getSubRealSrc(leftClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }

        if (realSrcObject == null
            && sub.isPresent()
            && (leftClass.getModifier().isAbstract()
                || (helper.getClassSize(sub.get()) <= helper.getClassSize(leftClass)))) {
          if (singletonObj(sub.get(), mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(sub.get()),
                  sub.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(sub.get()),
                  helper.getAttributesOD(sub.get(), null));

        } else if (realSrcObject == null && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(leftClass),
                  leftClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(leftClass),
                  helper.getAttributesOD(leftClass, null));
        }
        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Left));
        mapTgt.put(srcObject, new Pair<>(assocStruct, ClassSide.Right));
        Package pack =
            new Package(
                realSrcObject,
                srcObject,
                assocStruct.getAssociation(),
                ClassSide.Right,
                false,
                true,
                helper);
        packages.add(pack);

      } else {
        Optional<ASTCDClass> right =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
        if (right.isEmpty()) {
          return null;
        }
        ASTCDClass rightClass = right.get();
        Optional<ASTCDClass> sub = helper.minSubClass(rightClass);
        if (!rightClass.getModifier().isAbstract()) {
          realSrcObject = getRealSrc(rightClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }
        if (realSrcObject != null) {
          realSrcObject = getSubRealSrc(rightClass, assocStruct, astcdClass, mapSrc, mapTgt);
        }

        if (realSrcObject == null
            && sub.isPresent()
            && (rightClass.getModifier().isAbstract()
                || (helper.getClassSize(sub.get()) <= helper.getClassSize(rightClass)))) {
          if (singletonObj(sub.get(), mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(sub.get()),
                  sub.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(sub.get()),
                  helper.getAttributesOD(sub.get(), null));
        } else if (realSrcObject == null && !rightClass.getModifier().isAbstract()) {
          if (singletonObj(rightClass, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(rightClass),
                  rightClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(rightClass),
                  helper.getAttributesOD(rightClass, null));
        }

        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Right));
        mapTgt.put(srcObject, new Pair<>(assocStruct, ClassSide.Left));
        Package pack =
            new Package(
                srcObject,
                realSrcObject,
                assocStruct.getAssociation(),
                ClassSide.Left,
                true,
                false,
                helper);
        packages.add(pack);
      }
    }
    if (!hasAdded) {
      Package pack = new Package(srcObject, helper);
      packages.add(pack);
    }
    return packages;
  }

  /**
   * The function works in the same way as createChainsForNewClass, but here the class was already
   * created for another association. Because of that the created associations are removed from the
   * list of associations that are needed to be processed and only the rest must be considered.
   *
   * @param object object to create associations for.
   * @param packages current set of packages.
   * @param mapSrc map of objects that are used as source.
   * @param mapTgt map of objects that are used as target.
   * @return set of packages that contain the current elements of the object diagram. If the set is
   *     null, for a needed association a source or target object couldn't be created.
   */
  public Set<Package> createChainsForExistingObj(
      ASTODObject object,
      Set<Package> packages,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    List<AssocStruct> list = new ArrayList<>();
    for (AssocStruct assocStruct :
        helper
            .getSrcMap()
            .get(
                Syn2SemDiffHelper.getCDClass(
                    helper.getSrcCD(), object.getMCObjectType().printType()))) {
      if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStruct.isToBeProcessed()
          && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
              || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        list.add(assocStruct);
      } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStruct.isToBeProcessed()
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
                || (pair.b.equals(ClassSide.Right)
                    && assocStruct.getSide().equals(ClassSide.Right)))
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
                || (pair.b.equals(ClassSide.Right)
                    && assocStruct.getSide().equals(ClassSide.Right)))
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
    boolean hasAdded = false;
    for (AssocStruct assocStruct : list) {
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
          && assocStruct.isToBeProcessed()
          && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
              || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        Optional<ASTCDClass> right =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
        if (right.isEmpty()) {
          return null;
        }
        ASTCDClass rightClass = right.get();
        Optional<ASTCDClass> sub = helper.minSubClass(rightClass);
        if (helper.isLoopStruct(assocStruct)) {
          tgtObject = object;
        }
        if (tgtObject == null && !rightClass.getModifier().isAbstract()) {
          tgtObject =
              getTgtObject(
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  assocStruct,
                  rightClass,
                  mapSrc,
                  mapTgt);
        }
        if (tgtObject == null) {
          tgtObject =
              getSubTgtObject(
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  assocStruct,
                  rightClass,
                  mapSrc,
                  mapTgt);
        }

        if (tgtObject == null
            && sub.isPresent()
            && (rightClass.getModifier().isAbstract()
                || (helper.getClassSize(sub.get()) <= helper.getClassSize(rightClass)))) {
          if (singletonObj(sub.get(), mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(sub.get()),
                  sub.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(sub.get()),
                  helper.getAttributesOD(sub.get(), null));
        } else if (tgtObject == null && !rightClass.getModifier().isAbstract()) {
          if (singletonObj(rightClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(rightClass),
                  rightClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(rightClass),
                  helper.getAttributesOD(rightClass, null));
        }

        if (tgtObject == null) {
          return null;
        }

        hasAdded = true;
        mapSrc.put(object, new Pair<>(assocStruct, ClassSide.Left));
        mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Right));
        Package pack =
            new Package(
                object,
                tgtObject,
                assocStruct.getAssociation(),
                ClassSide.Left,
                true,
                false,
                helper);
        packages.add(pack);

      } else if (assocStruct.getSide().equals(ClassSide.Right)
          && assocStruct.isToBeProcessed()
          && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
              || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
        Optional<ASTCDClass> left =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
        if (left.isEmpty()) {
          return null;
        }
        ASTCDClass leftClass = left.get();
        Optional<ASTCDClass> sub = helper.minSubClass(leftClass);
        if (helper.isLoopStruct(assocStruct)) {
          tgtObject = object;
        }
        if (tgtObject == null && !leftClass.getModifier().isAbstract()) {
          tgtObject =
              getTgtObject(
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  assocStruct,
                  leftClass,
                  mapSrc,
                  mapTgt);
        }
        if (tgtObject == null) {
          tgtObject =
              getSubTgtObject(
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  assocStruct,
                  leftClass,
                  mapSrc,
                  mapTgt);
        }
        if (tgtObject == null
            && helper
                .minSubClass(getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a)
                .isPresent()
            && (leftClass.getModifier().isAbstract()
                || (helper.getClassSize(sub.get()) <= helper.getClassSize(leftClass)))) {
          if (singletonObj(sub.get(), mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(sub.get()),
                  sub.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(sub.get()),
                  helper.getAttributesOD(sub.get(), null));
        } else if (tgtObject == null && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          tgtObject =
              odBuilder.buildObj(
                  getNameForClass(leftClass),
                  leftClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(leftClass),
                  helper.getAttributesOD(leftClass, null));
        }
        if (tgtObject == null) {
          return null;
        }

        hasAdded = true;
        mapSrc.put(object, new Pair<>(assocStruct, ClassSide.Right));
        mapTgt.put(tgtObject, new Pair<>(assocStruct, ClassSide.Left));
        Package pack =
            new Package(
                tgtObject,
                object,
                assocStruct.getAssociation(),
                ClassSide.Right,
                false,
                true,
                helper);
        packages.add(pack);
      }
    }
    for (AssocStruct assocStruct : getTgtAssocsForObject(object, mapSrc, mapTgt)) {
      ASTODObject realSrcObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        Optional<ASTCDClass> left =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
        if (left.isEmpty()) {
          return null;
        }
        ASTCDClass leftClass = left.get();
        Optional<ASTCDClass> sub = helper.minSubClass(leftClass);
        if (!leftClass.getModifier().isAbstract()) {
          realSrcObject =
              getRealSrc(
                  leftClass,
                  assocStruct,
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  mapSrc,
                  mapTgt);
        }
        if (realSrcObject == null) {
          realSrcObject =
              getSubRealSrc(
                  leftClass,
                  assocStruct,
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  mapSrc,
                  mapTgt);
        }

        if (realSrcObject == null
            && sub.isPresent()
            && (leftClass.getModifier().isAbstract()
                || (helper.getClassSize(sub.get()) < helper.getClassSize(leftClass)))) {
          if (singletonObj(sub.get(), mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(sub.get()),
                  sub.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(sub.get()),
                  helper.getAttributesOD(sub.get(), null));
        } else if (realSrcObject == null && !leftClass.getModifier().isAbstract()) {
          if (singletonObj(leftClass, mapSrc, mapTgt)) {
            return null;
          }
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(leftClass),
                  leftClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(leftClass),
                  helper.getAttributesOD(leftClass, null));
        }

        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Left));
        mapTgt.put(object, new Pair<>(assocStruct, ClassSide.Right));
        Package pack =
            new Package(
                realSrcObject,
                object,
                assocStruct.getAssociation(),
                ClassSide.Right,
                false,
                true,
                helper);
        packages.add(pack);
      } else {
        Optional<ASTCDClass> right =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
        if (right.isEmpty()) {
          return null;
        }
        ASTCDClass rightClass = right.get();
        Optional<ASTCDClass> sub = helper.minSubClass(rightClass);
        if (!rightClass.getModifier().isAbstract()) {
          realSrcObject =
              getRealSrc(
                  rightClass,
                  assocStruct,
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  mapSrc,
                  mapTgt);
        }
        if (realSrcObject == null) {
          realSrcObject =
              getSubRealSrc(
                  rightClass,
                  assocStruct,
                  Syn2SemDiffHelper.getCDClass(
                      helper.getSrcCD(), object.getMCObjectType().printType()),
                  mapSrc,
                  mapTgt);
        }

        if (realSrcObject == null
            && sub.isPresent()
            && (rightClass.getModifier().isAbstract()
                || (helper.getClassSize(sub.get()) < helper.getClassSize(rightClass)))) {
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(sub.get()),
                  sub.get().getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(sub.get()),
                  helper.getAttributesOD(sub.get(), null));
        } else if (realSrcObject == null && !rightClass.getModifier().isAbstract()) {
          realSrcObject =
              odBuilder.buildObj(
                  getNameForClass(rightClass),
                  rightClass.getSymbol().getInternalQualifiedName(),
                  helper.getSuperTypes(rightClass),
                  helper.getAttributesOD(rightClass, null));
        }

        if (realSrcObject == null) {
          return null;
        }
        hasAdded = true;
        mapSrc.put(realSrcObject, new Pair<>(assocStruct, ClassSide.Right));
        mapTgt.put(object, new Pair<>(assocStruct, ClassSide.Left));
        Package pack =
            new Package(
                object,
                realSrcObject,
                assocStruct.getAssociation(),
                ClassSide.Left,
                true,
                false,
                helper);
        packages.add(pack);
      }
    }
    if (!hasAdded) {
      Package pack = new Package(object, helper);
      packages.add(pack);
    }
    return packages;
  }

  /**
   * Search for a possible target object (strictly of the needed type) for a given class. The
   * function firstly checks, if the target class can be connected to multiple objects of the source
   * type. If yes, the function returns a random object of the source type. If not, the function
   * uses the two maps and compares the created associations with the needed one. If the association
   * is matched with the given one, the function proceeds to the next object (this already has the
   * association).
   *
   * @param srcClass source class in the association.
   * @param assocStruct association that must be created.
   * @param tgtToFind target class in the association to search for.
   * @param srcMap map of objects that are used as source.
   * @param tgtMap map of objects that are used as target.
   * @return target object of the needed type or null.
   */
  public ASTODObject getTgtObject(
      ASTCDClass srcClass,
      AssocStruct assocStruct,
      ASTCDClass tgtToFind,
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
    Optional<ASTCDClass> leftClassAssoc =
        helper.getClassForTypeSrc(
            getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
    Optional<ASTCDClass> rightClassAssoc =
        helper.getClassForTypeSrc(
            getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
    if (leftClassAssoc.isEmpty() || rightClassAssoc.isEmpty()) {
      return null;
    }
    ASTCDClass leftClassAssocStruct = leftClassAssoc.get();
    ASTCDClass rightClassAssocStruct = rightClassAssoc.get();
    for (ASTODObject object : listToIterate) {
      boolean matched = false;
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(object)) {
        Optional<ASTCDClass> leftClass =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a);
        Optional<ASTCDClass> rightClass =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b);
        if (leftClass.isEmpty() || rightClass.isEmpty()) {
          return null;
        }
        ASTCDClass leftClassToMatch = leftClass.get();
        ASTCDClass rightClassToMatch = rightClass.get();
        if (assocStruct
                .getSide()
                .equals(ClassSide.Left) // not-searched class of assocStruc on the left side
            && assocStructToMatch.b.equals(
                ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && ((leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(rightClassToMatch)))) {
          matched = true;
          break;
        } else if (assocStruct
                .getSide()
                .equals(ClassSide.Right) // src of assocStruc on the right side
            && assocStructToMatch.b.equals(
                ClassSide.Right) // tgt of assocStructToMatch on the right side!!!
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && ((rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(leftClassToMatch)))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(leftClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, leftClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(leftClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, rightClassToMatch)
                || isSubClass(rightClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(rightClassToMatch))) {
          matched = true;
          break;
        }
      }
      if (matched) {
        // go to next object
        continue;
      }
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(object)) {
        Optional<ASTCDClass> leftClass =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a);
        Optional<ASTCDClass> rightClass =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b);
        if (leftClass.isEmpty() || rightClass.isEmpty()) {
          return null;
        }
        ASTCDClass leftClassToMatch = leftClass.get();
        ASTCDClass rightClassToMatch = rightClass.get();
        if (assocStruct.getSide().equals(ClassSide.Left) // not-searched class on the left side
            && assocStructToMatch.b.equals(
                ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(leftClassAssocStruct, rightClassToMatch)
                || isSubClass(rightClassToMatch, leftClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(rightClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(leftClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, leftClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(leftClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, rightClassToMatch)
                || isSubClass(rightClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(rightClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalType())
                    .contains(leftClassToMatch))) {
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

  /**
   * Search for a possible target object (subclasses of the needed type) for a given class. This
   * function works in a similar way as getTgtObject, but here objects from the underlying hierarchy
   * are used.
   *
   * @param srcClass source class in the association.
   * @param assocStruct association that must be created.
   * @param tgtToFind target class in the association to search for.
   * @param srcMap map of objects that are used as source.
   * @param tgtMap map of objects that are used as target.
   * @return target object of the needed type or null.
   */
  public ASTODObject getSubTgtObject(
      ASTCDClass srcClass,
      AssocStruct assocStruct,
      ASTCDClass tgtToFind,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    List<ASTCDClass> subClasses = helper.getSrcSubMap().get(tgtToFind);
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
      Optional<ASTCDClass> leftClassAssoc =
          helper.getClassForTypeSrc(
              getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
      Optional<ASTCDClass> rightClassAssoc =
          helper.getClassForTypeSrc(
              getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
      if (leftClassAssoc.isEmpty() || rightClassAssoc.isEmpty()) {
        return null;
      }
      ASTCDClass leftClassAssocStruct = leftClassAssoc.get();
      ASTCDClass rightClassAssocStruct = rightClassAssoc.get();
      for (ASTODObject subObject : listToIterate) {
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> subAssocStruct : tgtMap.get(subObject)) {
          Optional<ASTCDClass> leftClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a);
          Optional<ASTCDClass> rightClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b);
          if (leftClass.isEmpty() || rightClass.isEmpty()) {
            return null;
          }
          ASTCDClass leftClassToMatch = leftClass.get();
          ASTCDClass rightClassToMatch = rightClass.get();
          if (assocStruct
                  .getSide()
                  .equals(ClassSide.Left) // not-searched class of assocStruc on the left side
              && subAssocStruct.b.equals(
                  ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getLeft())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (leftClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(leftClassAssocStruct, rightClassToMatch)
                  || isSubClass(rightClassToMatch, leftClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(rightClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
              && subAssocStruct.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getRight())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (leftClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(leftClassAssocStruct, leftClassToMatch)
                  || isSubClass(leftClassToMatch, leftClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && subAssocStruct.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getLeft())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (rightClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(rightClassAssocStruct, leftClassToMatch)
                  || isSubClass(leftClassToMatch, rightClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && subAssocStruct.b.equals(ClassSide.Left)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getRight())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (rightClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(rightClassAssocStruct, rightClassToMatch)
                  || isSubClass(rightClassToMatch, rightClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(rightClassToMatch))) {
            matched = true;
            break;
          }
        }
        if (matched) {
          continue;
        }
        for (Pair<AssocStruct, ClassSide> subAssocStruct : srcMap.get(subObject)) {
          Optional<ASTCDClass> leftClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(subAssocStruct.a.getAssociation(), helper.getSrcCD()).a);
          Optional<ASTCDClass> rightClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(subAssocStruct.a.getAssociation(), helper.getSrcCD()).b);
          if (leftClass.isEmpty() || rightClass.isEmpty()) {
            return null;
          }
          ASTCDClass leftClassToMatch = leftClass.get();
          ASTCDClass rightClassToMatch = rightClass.get();
          if (assocStruct
                  .getSide()
                  .equals(ClassSide.Left) // not-searched class of assocStruc on the left side
              && subAssocStruct.b.equals(
                  ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getLeft())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (isSubClass(leftClassAssocStruct, rightClassToMatch)
                  || isSubClass(leftClassToMatch, rightClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(rightClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
              && subAssocStruct.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getRight())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (isSubClass(leftClassAssocStruct, leftClassToMatch)
                  || isSubClass(leftClassToMatch, leftClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && subAssocStruct.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getLeft())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (isSubClass(rightClassAssocStruct, leftClassToMatch)
                  || isSubClass(leftClassToMatch, rightClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && subAssocStruct.b.equals(ClassSide.Left)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  subAssocStruct.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  subAssocStruct.a.getAssociation().getRight())
              && helper.matchDirectionInReverse(assocStruct, subAssocStruct)
              && (isSubClass(rightClassAssocStruct, rightClassToMatch)
                  || isSubClass(rightClassToMatch, rightClassAssocStruct)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalType())
                      .contains(rightClassToMatch))) {
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

  /**
   * Search for a possible source object (subclasses of the needed type) for a given class. This
   * function works in a similar way as getRealSrc, but here objects from the underlying hierarchy
   * are used.
   *
   * @param srcToFind source class in the association to search for.
   * @param assocStruct association that must be created.
   * @param tgtClass target class in the association.
   * @param srcMap map of objects that are used as source.
   * @param tgtMap map of objects that are used as target.
   * @return source object of the needed type or null.
   */
  public ASTODObject getSubRealSrc(
      ASTCDClass srcToFind,
      AssocStruct assocStruct,
      ASTCDClass tgtClass,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    List<ASTCDClass> subClasses = helper.getSrcSubMap().get(srcToFind);
    for (ASTCDClass subClass : subClasses) {
      List<ASTODObject> objectsOfType = getObjectsOfType(subClass, srcMap);
      List<ASTODObject> objectsOfTypeTgt = getObjectsOfType(subClass, tgtMap);
      Set<ASTODObject> listToIterate = new HashSet<>();
      listToIterate.addAll(objectsOfType);
      listToIterate.addAll(objectsOfTypeTgt);
      for (ASTODObject subObject : listToIterate) {
        for (AssocStruct assocStructFromCLass :
            helper
                .getSrcMap()
                .get(
                    Syn2SemDiffHelper.getCDClass(
                        helper.getSrcCD(), subObject.getMCObjectType().printType()))) {
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
        Optional<ASTCDClass> leftClassAssoc =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
        Optional<ASTCDClass> rightClassAssoc =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
        if (leftClassAssoc.isEmpty() || rightClassAssoc.isEmpty()) {
          return null;
        }
        ASTCDClass leftClassAssocStruct = leftClassAssoc.get();
        ASTCDClass rightClassAssocStruct = rightClassAssoc.get();
        boolean matched = false;
        for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(subObject)) {
          Optional<ASTCDClass> leftClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a);
          Optional<ASTCDClass> rightClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b);
          if (leftClass.isEmpty() || rightClass.isEmpty()) {
            return null;
          }
          ASTCDClass leftClassToMatch = leftClass.get();
          ASTCDClass rightClassToMatch = rightClass.get();
          if (assocStruct
                  .getSide()
                  .equals(ClassSide.Left) // searched class of assocStruc on the left side
              && assocStructToMatch.b.equals(
                  ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getRight())
              && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
              && (rightClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, rightClassToMatch)
                  || isSubClass(rightClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(rightClassToMatch))) { // not needed - tgt must be same
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
              && assocStructToMatch.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
              && (rightClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, leftClassToMatch)
                  || isSubClass(leftClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && assocStructToMatch.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getRight())
              && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
              && (leftClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, leftClassToMatch)
                  || isSubClass(leftClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && assocStructToMatch.b.equals(ClassSide.Left)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && helper.matchDirectionInReverse(assocStruct, assocStructToMatch)
              && (leftClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, rightClassToMatch)
                  || isSubClass(rightClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(rightClassToMatch))) {
            matched = true;
            break;
          }
        }
        if (matched) {
          continue;
        }
        for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(subObject)) {
          Optional<ASTCDClass> leftClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a);
          Optional<ASTCDClass> rightClass =
              helper.getClassForTypeSrc(
                  getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b);
          if (leftClass.isEmpty() || rightClass.isEmpty()) {
            return null;
          }
          ASTCDClass leftClassToMatch = leftClass.get();
          ASTCDClass rightClassToMatch = rightClass.get();
          if (assocStruct
                  .getSide()
                  .equals(ClassSide.Left) // searched class of assocStruc on the left side
              && assocStructToMatch.b.equals(
                  ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getRight())
              && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
              && (rightClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, rightClassToMatch)
                  || isSubClass(rightClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(rightClassToMatch))) { // not needed - tgt must be same
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Left)
              && assocStructToMatch.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
              && (rightClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, leftClassToMatch)
                  || isSubClass(leftClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && assocStructToMatch.b.equals(ClassSide.Right)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getRight())
              && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
              && (leftClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, leftClassToMatch)
                  || isSubClass(leftClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(leftClassToMatch))) {
            matched = true;
            break;
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && assocStructToMatch.b.equals(ClassSide.Left)
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getLeft(),
                  assocStructToMatch.a.getAssociation().getRight())
              && CDAssociationHelper.matchRoleNames(
                  assocStruct.getAssociation().getRight(),
                  assocStructToMatch.a.getAssociation().getLeft())
              && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
              && (leftClassAssocStruct
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                  || isSubClass(srcToFind, rightClassToMatch)
                  || isSubClass(rightClassToMatch, srcToFind)
                  || helper
                      .getSrcSubMap()
                      .get(assocStruct.getOriginalTgtType())
                      .contains(rightClassToMatch))) {
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
    Set<ASTCDClass> superClassSet =
        CDDiffUtil.getAllSuperclasses(
            astcdClass, helper.getSrcCD().getCDDefinition().getCDClassesList());
    for (ASTCDClass superClass : superClassSet) {
      assocStructs.addAll(helper.getOtherAssocFromSuper(superClass));
    }
    List<AssocStruct> copy = new ArrayList<>(assocStructs);
    for (AssocStruct assocStruct : copy) {
      for (AssocStruct assocStruct1 : copy) {
        if (assocStruct != assocStruct1
            && helper.isSubAssociationSrcSrc(assocStruct, assocStruct1)) {
          assocStructs.remove(assocStruct1);
        }
      }
    }

    return assocStructs;
  }

  /**
   * Search for a possible source object (strictly of the needed type) for a given class. The
   * function firstly checks, if the source class can be connected to multiple objects of the target
   * type. If yes, the function returns a random object of the target type. If not, the function
   * uses the two maps and compares the created associations with the needed one. If the association
   * is matched with the given one, the function proceeds to the next object (this already has the
   * association).
   *
   * @param srcToFind source class in the association to search for.
   * @param assocStruct association that must be created.
   * @param tgtClass target class in the association.
   * @param srcMap map of objects that are used as source.
   * @param tgtMap map of objects that are used as target.
   * @return source object of the needed type or null.
   */
  public ASTODObject getRealSrc(
      ASTCDClass srcToFind,
      AssocStruct assocStruct,
      ASTCDClass tgtClass,
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
    Optional<ASTCDClass> leftClassAssoc =
        helper.getClassForTypeSrc(
            getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).a);
    Optional<ASTCDClass> rightClassAssoc =
        helper.getClassForTypeSrc(
            getConnectedTypes(assocStruct.getAssociation(), helper.getSrcCD()).b);
    if (leftClassAssoc.isEmpty() || rightClassAssoc.isEmpty()) {
      return null;
    }
    ASTCDClass leftClassAssocStruct = leftClassAssoc.get();
    ASTCDClass rightClassAssocStruct = rightClassAssoc.get();
    for (ASTODObject object : listToIterate) {
      boolean matched = false;
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : srcMap.get(object)) {
        Optional<ASTCDClass> leftClassTo =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a);
        Optional<ASTCDClass> rightClassTo =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b);
        if (leftClassTo.isEmpty() || rightClassTo.isEmpty()) {
          return null;
        }
        ASTCDClass leftClassToMatch = leftClassTo.get();
        ASTCDClass rightClassToMatch = rightClassTo.get();
        if (assocStruct
                .getSide()
                .equals(ClassSide.Left) // searched class of assocStruc on the left side
            && assocStructToMatch.b.equals(
                ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, rightClassToMatch)
                || isSubClass(rightClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(rightClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(leftClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(leftClassAssocStruct, rightClassToMatch)
                || isSubClass(rightClassToMatch, leftClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(rightClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(leftClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, leftClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(leftClassToMatch))) {
          matched = true;
          break;
        }
      }
      if (matched) {
        continue;
      }
      for (Pair<AssocStruct, ClassSide> assocStructToMatch : tgtMap.get(object)) {
        Optional<ASTCDClass> leftClassTo =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).a);
        Optional<ASTCDClass> rightClassTo =
            helper.getClassForTypeSrc(
                getConnectedTypes(assocStructToMatch.a.getAssociation(), helper.getSrcCD()).b);
        if (leftClassTo.isEmpty() || rightClassTo.isEmpty()) {
          return null;
        }
        ASTCDClass leftClassToMatch = leftClassTo.get();
        ASTCDClass rightClassToMatch = rightClassTo.get();
        if (assocStruct
                .getSide()
                .equals(ClassSide.Left) // searched class of assocStruc on the left side
            && assocStructToMatch.b.equals(
                ClassSide.Left) // searched class of assocStructToMatch on the left side!!!
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, rightClassToMatch)
                || isSubClass(rightClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(rightClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (rightClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(rightClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, rightClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(leftClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Left)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getRight())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getLeft())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(rightClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(leftClassAssocStruct, rightClassToMatch)
                || isSubClass(rightClassToMatch, leftClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(rightClassToMatch))) {
          matched = true;
          break;
        } else if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStructToMatch.b.equals(ClassSide.Right)
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getLeft(),
                assocStructToMatch.a.getAssociation().getLeft())
            && CDAssociationHelper.matchRoleNames(
                assocStruct.getAssociation().getRight(),
                assocStructToMatch.a.getAssociation().getRight())
            && Syn2SemDiffHelper.matchDirection(assocStruct, assocStructToMatch)
            && (leftClassAssocStruct
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(leftClassToMatch.getSymbol().getInternalQualifiedName())
                || isSubClass(leftClassAssocStruct, leftClassToMatch)
                || isSubClass(leftClassToMatch, leftClassAssocStruct)
                || helper
                    .getSrcSubMap()
                    .get(assocStruct.getOriginalTgtType())
                    .contains(leftClassToMatch))) {
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

  /**
   * Check if the given class is a singleton and if already an object exists.
   *
   * @param astcdClass class to check.
   * @param srcMap map of objects that are used as source.
   * @param tgtMap map of objects that are used as target.
   * @return true, if the class is a singleton and an object already exists.
   */
  public boolean singletonObj(
      ASTCDClass astcdClass,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> srcMap,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> tgtMap) {
    return astcdClass.getModifier().isPresentStereotype()
        && astcdClass.getModifier().getStereotype().contains("singleton")
        && (!getObjectsOfType(astcdClass, srcMap).isEmpty()
            || !getObjectsOfType(astcdClass, tgtMap).isEmpty());
  }

  /**
   * Get all objects of a given type.
   *
   * @param astcdClass type.
   * @param map map to search in for.
   * @return list of objects of the given type.
   */
  public List<ASTODObject> getObjectsOfType(
      ASTCDClass astcdClass, ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> map) {
    List<ASTODObject> objects = new ArrayList<>();
    for (ASTODObject astodObject : map.keySet()) {
      if (astodObject
          .getMCObjectType()
          .printType()
          .equals(astcdClass.getSymbol().getInternalQualifiedName())) {
        objects.add(astodObject);
      }
    }
    return objects;
  }

  public boolean isSubClass(ASTCDClass superClass, ASTCDClass subClass) {
    return CDInheritanceHelper.isSuperOf(
        superClass.getSymbol().getInternalQualifiedName(),
        subClass.getSymbol().getInternalQualifiedName(),
        helper.getSrcCD());
  }

  public boolean isSubAssociationInReverse(AssocStruct superAssoc, AssocStruct subAssoc) {
    if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Left)
        && CDAssociationHelper.matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && CDAssociationHelper.matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && CDInheritanceHelper.isSuperOf(
            getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                .a
                .getSymbol()
                .getInternalQualifiedName(),
            getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                .b
                .getSymbol()
                .getInternalQualifiedName(),
            helper.getSrcCD())
        && CDInheritanceHelper.isSuperOf(
            getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                .b
                .getSymbol()
                .getInternalQualifiedName(),
            getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                .a
                .getSymbol()
                .getInternalQualifiedName(),
            helper.getSrcCD())) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Right)
        && CDAssociationHelper.matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
        && CDAssociationHelper.matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
        && CDInheritanceHelper.isSuperOf(
            getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                .a
                .getSymbol()
                .getInternalQualifiedName(),
            getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                .a
                .getSymbol()
                .getInternalQualifiedName(),
            helper.getSrcCD())
        && CDInheritanceHelper.isSuperOf(
            getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                .b
                .getSymbol()
                .getInternalQualifiedName(),
            getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                .b
                .getSymbol()
                .getInternalQualifiedName(),
            helper.getSrcCD())) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
        && superAssoc.getSide().equals(ClassSide.Left)
        && CDAssociationHelper.matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
        && CDAssociationHelper.matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
        && CDInheritanceHelper.isSuperOf(
            getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                .a
                .getSymbol()
                .getInternalQualifiedName(),
            getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                .a
                .getSymbol()
                .getInternalQualifiedName(),
            helper.getSrcCD())
        && CDInheritanceHelper.isSuperOf(
            getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                .b
                .getSymbol()
                .getInternalQualifiedName(),
            getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                .b
                .getSymbol()
                .getInternalQualifiedName(),
            helper.getSrcCD())) {
      return true;
    } else
      return subAssoc.getSide().equals(ClassSide.Right)
          && superAssoc.getSide().equals(ClassSide.Right)
          && CDAssociationHelper.matchRoleNames(
              superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
          && CDAssociationHelper.matchRoleNames(
              superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
          && CDInheritanceHelper.isSuperOf(
              getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                  .a
                  .getSymbol()
                  .getInternalQualifiedName(),
              getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                  .b
                  .getSymbol()
                  .getInternalQualifiedName(),
              helper.getSrcCD())
          && CDInheritanceHelper.isSuperOf(
              getConnectedTypes(superAssoc.getAssociation(), helper.getSrcCD())
                  .b
                  .getSymbol()
                  .getInternalQualifiedName(),
              getConnectedTypes(subAssoc.getAssociation(), helper.getSrcCD())
                  .a
                  .getSymbol()
                  .getInternalQualifiedName(),
              helper.getSrcCD());
  }

  /**
   * Get all associations that are not created yet for a given class. This is based on comparison of
   * associations.
   *
   * @param tgtObject target class in the associations.
   * @param mapSrc map of objects that are used as source.
   * @param mapTgt map of objects that are used as target.
   * @return list of associations that are not created yet.
   */
  public List<AssocStruct> getTgtAssocsForObject(
      ASTODObject tgtObject,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapSrc,
      ArrayListMultimap<ASTODObject, Pair<AssocStruct, ClassSide>> mapTgt) {
    List<AssocStruct> list =
        new ArrayList<>(
            getTgtAssocs(
                Syn2SemDiffHelper.getCDClass(
                    helper.getSrcCD(), tgtObject.getMCObjectType().printType())));
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

  /**
   * Name the object of a given class. Name: class name + _ + index.
   *
   * @param astcdClass class to name.
   * @return name of the object.
   */
  public String getNameForClass(ASTCDClass astcdClass) {
    map.putIfAbsent(astcdClass, 0);
    map.put(astcdClass, map.get(astcdClass) + 1);
    return astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_")
        + map.get(astcdClass);
  }
}
