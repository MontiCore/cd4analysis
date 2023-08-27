package de.monticore.cddiff.syndiff.OD;

import de.monticore.ast.CommentBuilder;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.datastructures.TypeDiffStruc;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteralBuilder;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import de.monticore.umlstereotype._ast.ASTStereoValueBuilder;
import de.monticore.umlstereotype._ast.ASTStereotypeBuilder;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;

public class ODHelper {
  private int indexClass = 1;
  private int indexAssoc = 1;
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  private final Builder builder = new Builder();

  //TODO: change ODBuilder with value
  //TODO: tests from ValidationAndPerfornce

//  /**
//   * Create a minimal set of associations and classes that are needed for deriving
//   * an object diagram for a given class or association
//   * @param astcdAssociation optional
//   * @return minimal set of objects
//   */
//  public Pair<Set<ASTODElement>, ASTODLink> getObjectsForOD(ASTCDAssociation astcdAssociation, int cardinalityLeft, int cardinalityRight){
//    Scanner scanner = new Scanner(System.in);
//    boolean cont = true;
//    Set<ASTODElement> set = new HashSet<>();
//    Set<Package> packages = createChains(astcdAssociation, cardinalityLeft, cardinalityRight);
//    Iterator<Package> iterator = packages.iterator();
//    ASTODLink link = iterator.next().getAssociation();
//    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(packages);
//    while (cont){
//      for (ASTODObject astodObject : unprocessedObjects){
//        packages.addAll(createChainsNew(helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()), astodObject, packages));
//      }
//      unprocessedObjects = findUnprocessedObjects(packages);
//      System.out.println(unprocessedObjects.size());
//      cont = scanner.nextBoolean();
//    }
//    for (Package pack : packages) {
//      //unfold packages into set
//      set.add(pack.getAssociation());
//      set.add(pack.getSrcClass());
//      set.add(pack.getTgtClass());
//    }
//    return new Pair<>(set, link);
//  }
//
//  public Set<ASTODElement> getObjectsForOD(ASTCDClass astcdClass){
//    Set<ASTODElement> set = new HashSet<>();
//    Set<Package> packages = createChains(new HashSet<>(), helper.getSrcMap().get(astcdClass));
//    System.out.println("packages = "+packages.size());
//    List<Package> objects = new ArrayList<>(packages);
//    System.out.println(objects.get(0).getSrcClass().getMCObjectType().printType() + objects.get(0).isProcessedLeft() + " " + objects.get(0).getTgtClass().getMCObjectType().printType() + objects.get(0).isProcessedRight());
//    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(packages);
//    System.out.println("unprocessed = " + unprocessedObjects.size());
//    boolean continiue = true;
//    while (continiue) {
//      for (ASTODObject astodObject : unprocessedObjects) {
//        assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
//        packages.addAll(createChainsNew(helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()), astodObject, packages));
//      }
//      continiue = (!findUnprocessedObjects(packages).isEmpty());
//    }
////      System.out.println(unprocessedObjects);
////      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
////        System.out.println("Object has type " + astodObject.getMCObjectType().printType());
////      }
////
////      System.out.println(findUnprocessedObjects(packages));
////      i--;
////    for (ASTODObject astodObject : unprocessedObjects) {
////      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
////      packages.addAll(createChainsNew(helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()), astodObject, packages));
////      System.out.println("2 ====" + findUnprocessedObjects(packages));
////    }
////    for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
////      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
////      packages.addAll(createChainsNew(helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()), astodObject, packages));
////      System.out.println("2l ====" + new ArrayList<>(findUnprocessedObjects(packages)).isEmpty());
////    }
////    for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
////      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
////      packages.addAll(createChainsNew(helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()), astodObject, packages));
////      System.out.println("3 ====" + findUnprocessedObjects(packages));
////    }
////    for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
////      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
////      packages.addAll(createChainsNew(helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()), astodObject, packages));
////      System.out.println("4 ===" + findUnprocessedObjects(packages));
////    }
//    for (Package pack : packages) {
//      //unfold packages into set
//      set.add(pack.getAssociation());
//      set.add(pack.getSrcClass());
//      System.out.println(pack.getSrcClass().getMCObjectType().printType());
//      set.add(pack.getTgtClass());
//      System.out.println(pack.getTgtClass().getMCObjectType().printType());
//    }
//    return set;
//  }

  //function that creates List<Pair<ASTODObject, List<Package>>> that orders the packages of each object from a set of packages
  //function that creates List<Pair<ASTODObject, List<Package>>> that orders the packages of each object from a set of packages
  public static Map<ASTODObject, Integer> findUniqueASTODObjects(Set<Package> packages) {
    Map<ASTODObject, Integer> objectCountMap = new HashMap<>();

    // Count occurrences of ASTODObjects in the packages
    for (Package pack : packages) {
      countASTODObjects(pack, objectCountMap);
    }

    // Remove non-unique entries
    objectCountMap.entrySet().removeIf(entry -> entry.getValue() <= 1);

    return objectCountMap;
  }

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
    if (unprocessedObjects.isEmpty()) {
      return null;
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

  private static void countASTODObjects(Package pack, Map<ASTODObject, Integer> objectCountMap) {
    objectCountMap.put(pack.getSrcClass(), objectCountMap.getOrDefault(pack.getSrcClass(), 0) + 1);
    objectCountMap.put(pack.getTgtClass(), objectCountMap.getOrDefault(pack.getTgtClass(), 0) + 1);
//    objectCountMap.put(pack.getAssociation(), objectCountMap.getOrDefault(pack.getAssociation(), 0) + 1);
//    objectCountMap.put(pack.getAstcdAssociation(), objectCountMap.getOrDefault(pack.getAstcdAssociation(), 0) + 1);
  }

  public static Pair<Package, ClassSide> findContainingPackage(Set<Package> packages, ASTODObject astodObject) {
    for (Package pack : packages) {
      if (pack.getSrcClass() == astodObject) {
        return new Pair<>(pack, ClassSide.Left);
      } else if (pack.getTgtClass() == astodObject) {
        return new Pair<>(pack, ClassSide.Right);
      }
    }
    return null;
  }

/*
  public Set<Package> createChains(ASTCDClass astcdClass, ASTODObject astodObject, Set<Package> objectSet) {
    List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
    if (astodObject == null) {
      for (AssocStruct pair : list) { //Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair
        switch (pair.getSide()) {
          case Left:
            if (pair.getAssociation().getRight().getCDCardinality().isAtLeastOne()
              || pair.getAssociation().getRight().getCDCardinality().isOne()) {
              ASTODObject srcClass = builder.buildObj("", Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a.getName(),
                splitStringByCharacter(helper.getSuperClasses(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a), ','),
                helper.getAttributesOD(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a));
//              if (!Syn2SemDiffHelper.isPackageInSet(new Package(srcClass,
//                Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b,
//                pair.getAssociation(), null, false, false), objectSet)) {
              List<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
              ASTODObject toFind = null;
              for (ASTODObject object : unprocessedObjects) {
                Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
                if (containingPackage != null) {
                  if (containingPackage.b == ClassSide.Left) {
                    if (pair.getSide().equals(ClassSide.Left)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b) {
                      toFind = containingPackage.a.getSrcClass();
                      break;
                    } else if (pair.getSide().equals(ClassSide.Right)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a) {
                      toFind = containingPackage.a.getSrcClass();
                      break;
                    }
                  } else if (containingPackage.b == ClassSide.Right) {
                    if (pair.getSide().equals(ClassSide.Left)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b) {
                      toFind = containingPackage.a.getTgtClass();
                      break;
                    } else if (pair.getSide().equals(ClassSide.Right)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a) {
                      toFind = containingPackage.a.getTgtClass();
                      break;
                    }
                  }
                }
              }
              Package pack;
              if (toFind == null) {
                pack = new Package(srcClass,
                  Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b,
                  pair.getAssociation(), ClassSide.Left, true, false);
              } else {
                pack = new Package(srcClass,
                  toFind,
                  pair.getAssociation(), ClassSide.Left, true, false);
              }
              objectSet.add(pack);
              //createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b, pack.getTgtClass(), objectSet);
            }
          case Right:
            if (pair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
              || pair.getAssociation().getLeft().getCDCardinality().isOne()) {
              ASTODObject srcClass = builder.buildObj("", Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b.getName(),
                splitStringByCharacter(helper.getSuperClasses(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b), ','),
                helper.getAttributesOD(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b));
              List<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
              ASTODObject toFind = null;
              for (ASTODObject object : unprocessedObjects) {
                Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
                if (containingPackage != null) {
                  if (containingPackage.b == ClassSide.Left) {
                    if (pair.getSide().equals(ClassSide.Left)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b) {
                      toFind = containingPackage.a.getSrcClass();
                      break;
                    } else if (pair.getSide().equals(ClassSide.Right)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a) {
                      toFind = containingPackage.a.getSrcClass();
                      break;
                    }
                  } else if (containingPackage.b == ClassSide.Right) {
                    if (pair.getSide().equals(ClassSide.Left)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b) {
                      toFind = containingPackage.a.getTgtClass();
                      break;
                    } else if (pair.getSide().equals(ClassSide.Right)
                      && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a) {
                      toFind = containingPackage.a.getTgtClass();
                      break;
                    }
                  }
                }
              }
              Package pack;
              if (toFind == null) {
                pack = new Package(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a,
                  srcClass,
                  pair.getAssociation(), ClassSide.Right, false, true);
              } else {
                pack = new Package(toFind,
                  srcClass,
                  pair.getAssociation(), ClassSide.Right, false, true);

              }
              objectSet.add(pack);
              //createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a, pack.getSrcClass(), objectSet);
            }
        }
      }
    } else {
      boolean newPackages = false;
      for (AssocStruct pair : list) { //Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair
        if (!Syn2SemDiffHelper.isPackageInSet(new Package(astodObject,
          Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b,
          pair.getAssociation(), null, false, false), objectSet)) {
          switch (pair.getSide()) {
            case Left:
              if (pair.getAssociation().getRight().getCDCardinality().isAtLeastOne()
                || pair.getAssociation().getRight().getCDCardinality().isOne()) {
                //Map<ASTODObject, Integer> objectCountMap = findUniqueASTODObjects(objectSet);
                List<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
                ASTODObject toFind = null;
                for (ASTODObject object : unprocessedObjects) {
                  Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
                  if (containingPackage != null) {
                    if (containingPackage.b == ClassSide.Left) {
                      if (pair.getSide().equals(ClassSide.Left)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b
                        && containingPackage.a.getSrcClass() != astodObject) {
                        toFind = containingPackage.a.getSrcClass();
                        break;
                      } else if (pair.getSide().equals(ClassSide.Right)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a
                        && containingPackage.a.getSrcClass() != astodObject) {
                        toFind = containingPackage.a.getSrcClass();
                        break;
                      }
                    } else if (containingPackage.b == ClassSide.Right) {
                      if (pair.getSide().equals(ClassSide.Left)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b
                        && containingPackage.a.getTgtClass() != astodObject) {
                        toFind = containingPackage.a.getTgtClass();
                        break;
                      } else if (pair.getSide().equals(ClassSide.Right)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a
                        && containingPackage.a.getTgtClass() != astodObject) {
                        toFind = containingPackage.a.getTgtClass();
                        break;
                      }
                    }
                  }
                }
                Package pack;
                if (toFind == null) {
                  pack = new Package(astodObject,
                    Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b,
                    pair.getAssociation(), ClassSide.Left, true, false);
                } else {
                  pack = new Package(astodObject,
                    toFind,
                    pair.getAssociation(), ClassSide.Left, true, false);
                }
                objectSet.add(pack);
                //createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b, pack.getTgtClass(), objectSet);
              }
            case Right:
              if (pair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
                || pair.getAssociation().getLeft().getCDCardinality().isOne()) {
                //Map<ASTODObject, Integer> objectCountMap = findUniqueASTODObjects(objectSet);
                List<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
                ASTODObject toFind = null;
                for (ASTODObject object : unprocessedObjects) {
                  Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
                  if (containingPackage != null) {
                    if (containingPackage.b == ClassSide.Left) {
                      if (pair.getSide().equals(ClassSide.Left)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b
                        && containingPackage.a.getSrcClass() != astodObject) {
                        toFind = containingPackage.a.getSrcClass();
                        break;
                      } else if (pair.getSide().equals(ClassSide.Right)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).a == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a
                        && containingPackage.a.getSrcClass() != astodObject) {
                        toFind = containingPackage.a.getSrcClass();
                        break;
                      }
                    } else if (containingPackage.b == ClassSide.Right) {
                      if (pair.getSide().equals(ClassSide.Left)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).b
                        && containingPackage.a.getTgtClass() != astodObject) {
                        toFind = containingPackage.a.getTgtClass();
                        break;
                      } else if (pair.getSide().equals(ClassSide.Right)
                        && getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD()).b == getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a
                        && containingPackage.a.getTgtClass() != astodObject) {
                        toFind = containingPackage.a.getTgtClass();
                        break;
                      }
                    }
                  }
                }
                Package pack;
                if (toFind == null) {
                  pack = new Package(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a,
                    astodObject,
                    pair.getAssociation(), ClassSide.Right, false, true);
                } else {
                  pack = new Package(toFind,
                    astodObject,
                    pair.getAssociation(), ClassSide.Right, false, true);
                }
                newPackages = true;
                objectSet.add(pack);
                //createChains(Syn2SemDiffHelper.getConnectedClasses(pair.getAssociation(), helper.getSrcCD()).a, pack.getSrcClass(), objectSet);
              }
          }
        }
      }
      if (!newPackages) {
        Package pack = new Package(astodObject);
        objectSet.add(pack);
      }
    }
    return objectSet;
  }

 */

//  /**
//   * Create remaining chains for existing object
//   * @param astcdClass type of object
//   * @param astodObject existing object
//   * @param objectSet existing set
//   * @return set with new chains
//   */
//  public Set<Package> createChainsNew(ASTCDClass astcdClass, ASTODObject astodObject, Set<Package> objectSet){
//    assert astcdClass == helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType());
//    assert objectSet != null;
//    List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
//    Set<Pair<Package, ClassSide>> createdPackages = getContainingPackages(astodObject, objectSet);
//    for (Pair<Package, ClassSide> pack : createdPackages) {
//      //assert list.contains(getAssocStrucForClass(astcdClass, pack.a.getAstcdAssociation()));
//      list.removeIf(assocStruct -> CDAssociationHelper.sameAssociation(pack.a.getAstcdAssociation(), assocStruct.getAssociation()) || CDAssociationHelper.sameAssociationInReverse(pack.a.getAstcdAssociation(), assocStruct.getAssociation()));
//    }
//    if (list.isEmpty()){
//      //add an empty package with true
//      Package pack = new Package(astodObject);
//      objectSet.add(pack);
//    } else {
//      objectSet.addAll(createChainsForObject(astodObject, objectSet, list));
//    }
//    return objectSet;
//  }
//
//  public Set<Package> createChainsForObject(ASTODObject object, Set<Package> objectSet, List<AssocStruct> list){
//    for (AssocStruct assocStruct : list) {
//      if (assocStruct.getSide().equals(ClassSide.Left)) {
//        if (assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
//          || assocStruct.getAssociation().getRight().getCDCardinality().isOne()) {
//          ASTODObject tgtObject = getObjForLink(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, object, assocStruct.getAssociation(), objectSet);
//          Package pack;
//          if (tgtObject != null && tgtObject != object) {
//            pack = new Package(object,
//              tgtObject,
//              assocStruct.getAssociation(), ClassSide.Left, true, false);
//          } else {
//            pack = new Package(object,
//              Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b,
//              assocStruct.getAssociation(), ClassSide.Left, true, false);
//          }
//          objectSet.add(pack);
//        }
//      } else if (assocStruct.getSide().equals(ClassSide.Right)) {
//        if (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
//          || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
//          ASTODObject tgtObject = getObjForLink(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, object, assocStruct.getAssociation(), objectSet);
//          Package pack;
//          if (tgtObject != null && tgtObject != object) {
//            pack = new Package(tgtObject,
//              object,
//              assocStruct.getAssociation(), ClassSide.Right, false, true);
//          } else {
//            pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a,
//              object,
//              assocStruct.getAssociation(), ClassSide.Right, false, true);
//          }
//          objectSet.add(pack);
//        }
//      }
//    }
//    return objectSet;
//  }
//
//  /**
//   * Create chains for a new object
//   * @param objectSet existing set
//   * @return set with new chains
//   */
//  public Set<Package> createChains(Set<Package> objectSet, List<AssocStruct> list) {
//    for (AssocStruct assocStruct : list) {
//      if (assocStruct.getSide().equals(ClassSide.Left)) {
//        if (assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
//          || assocStruct.getAssociation().getRight().getCDCardinality().isOne()) {
//          ASTODObject srcClass = builder.buildObj("", Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getName(),
//            splitStringByCharacter(helper.getSuperClasses(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a), ','),
//            helper.getAttributesOD(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a));
//          ASTODObject tgtObject = getObjectForLink(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct.getAssociation(), objectSet);
//          Package pack;
//          if (tgtObject != null && tgtObject != srcClass) {
//            pack = new Package(srcClass,
//              tgtObject,
//              assocStruct.getAssociation(), ClassSide.Left, true, false);
//          } else {
//            pack = new Package(srcClass,
//              Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b,
//              assocStruct.getAssociation(), ClassSide.Left, true, false);
//          }
//          objectSet.add(pack);
//        }
//      } else if (assocStruct.getSide().equals(ClassSide.Right)) {
//        if (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
//          || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
//          ASTODObject srcClass = builder.buildObj("", Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getName(),
//            splitStringByCharacter(helper.getSuperClasses(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b), ','),
//            helper.getAttributesOD(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b));
//          ASTODObject tgtObject = getObjectForLink(getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct.getAssociation(), objectSet);
//          Package pack;
//          if (tgtObject != null && tgtObject != srcClass) {
//            pack = new Package(tgtObject,
//              srcClass,
//              assocStruct.getAssociation(), ClassSide.Right, false, true);
//          } else {
//            pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a,
//              srcClass,
//              assocStruct.getAssociation(), ClassSide.Right, false, true);
//          }
//          objectSet.add(pack);
//        }
//      }
//    }
//    return objectSet;
//  }
//
//  public ASTODObject getObjForLink(ASTCDClass tgt, ASTODObject srcObject, ASTCDAssociation association, Set<Package> objectSet){
//    Set<ASTODObject> procesedObjects = findProcessedObjects(objectSet);
//    ASTODObject astodObject = null;
//    for (ASTODObject object : procesedObjects){
//      if (object == srcObject){
//        continue;
//      }
//      Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
//      assert containingPackage != null;
//      if (containingPackage.a.getAstcdAssociation() != null) {
//        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD());
//        ASTCDClass classToCheck;
//        if (containingPackage.b == ClassSide.Left) {
//          classToCheck = pair.a;
//        } else {
//          classToCheck = pair.b;
//        }
//        if (classToCheck == tgt && !doesObjectUseAssociationMult(object, association, objectSet)) {
//          astodObject = object;
//        }
//      }
//    }
//    if (astodObject != null){
//      return astodObject;
//    }
//    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
//    for (ASTODObject object : unprocessedObjects) {
//      Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
//      assert containingPackage != null;
//      if (containingPackage.a.getAstcdAssociation() != null) {
//        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD());
//        ASTCDClass classToCheck;
//        if (containingPackage.b == ClassSide.Left) {
//          classToCheck = pair.a;
//        } else {
//          classToCheck = pair.b;
//        }
//        if (classToCheck == tgt && !doesObjectUseAssociation(object, association, objectSet)) {
//          return object;
//        }
//      }
//    }
//    return null;
//  }
//
//  /**
//   * Find object that uses the association
//   * @param tgt target class
//   * @param association association
//   * @param objectSet current set
//   * @return object that uses the association
//   */
//  public ASTODObject getObjectForLink(ASTCDClass tgt, ASTCDAssociation association, Set<Package> objectSet) {
//    Set<ASTODObject> procesedObjects = findProcessedObjects(objectSet);
//    ASTODObject astodObject = null;
//    for (ASTODObject object : procesedObjects){
//      Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
//      assert containingPackage != null;
//      Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD());
//      ASTCDClass classToCheck;
//      if (containingPackage.b == ClassSide.Left) {
//        classToCheck = pair.a;
//      } else {
//        classToCheck = pair.b;
//      }
//      if (classToCheck == tgt && !doesObjectUseAssociationMult(object, association, objectSet)) {
//        astodObject = object;
//      }
//    }
//    if (astodObject != null){
//      return astodObject;
//    }
//    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
//    for (ASTODObject object : unprocessedObjects) {
//      Pair<Package, ClassSide> containingPackage = findContainingPackage(objectSet, object);
//      assert containingPackage != null;
//      Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(containingPackage.a.getAstcdAssociation(), helper.getSrcCD());
//      ASTCDClass classToCheck;
//      if (containingPackage.b == ClassSide.Left) {
//        classToCheck = pair.a;
//      } else {
//        classToCheck = pair.b;
//      }
//      if (classToCheck == tgt && !doesObjectUseAssociation(object, association, objectSet)) {
//        return object;
//      }
//    }
//    return null;
//  }
//
//  //for unprocessed packages
//  public boolean doesObjectUseAssociation(ASTODObject astodObject, ASTCDAssociation association, Set<Package> objectSet) {
//    Set<Pair<Package, ClassSide>> containingPackages = getContainingPackages(astodObject, objectSet);
//    for (Pair<Package, ClassSide> pair : containingPackages){
//      if (CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), association) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), association)){
//        if (getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType())) {
//          if (pair.a.getAstcdAssociation().getRight().getCDCardinality().isMult() || pair.a.getAstcdAssociation().getRight().getCDCardinality().isAtLeastOne()) {
//            return false;// it can have arbitrary number of objects
//          } else if (pair.a.getAstcdAssociation().getRight().getCDCardinality().isOne() || pair.a.getAstcdAssociation().getRight().getCDCardinality().isOpt()) {
//            return true;
//          }
//        } else {
//          if (pair.a.getAstcdAssociation().getLeft().getCDCardinality().isMult() || pair.a.getAstcdAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
//            return false;
//          } else if (pair.a.getAstcdAssociation().getLeft().getCDCardinality().isOne() || pair.a.getAstcdAssociation().getLeft().getCDCardinality().isOpt()) {
//            return true;
//          }
//        }
//      }
//    }
////    if (getConnectedClasses(association, helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType())) {
////      if (association.getRight().getCDCardinality().isMult() || association.getRight().getCDCardinality().isAtLeastOne()) {
////        if (!containingPackages.isEmpty()){
////          Pair<Package, ClassSide> pair = containingPackages.iterator().next();
////          if (getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType())) {
////            return pair.a.getAstcdAssociation().getRight().getCDCardinality().isMult() || pair.a.getAstcdAssociation().getRight().getCDCardinality().isAtLeastOne();
////          } else {
////            return pair.a.getAstcdAssociation().getLeft().getCDCardinality().isMult() || pair.a.getAstcdAssociation().getLeft().getCDCardinality().isAtLeastOne();
////          }
////        }
////      }
////    } else {
////      for (Pair<Package, ClassSide> pair : containingPackages) {
////        if (CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), association) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), association)) {
////          return true;
////        }
////      }
////    }
//    return false;
//  }
//
//  //for processed packages
//  public boolean doesObjectUseAssociationMult(ASTODObject astodObject, ASTCDAssociation association, Set<Package> objectSet) {
//    Set<Pair<Package, ClassSide>> containingPackages = getContainingPackages(astodObject, objectSet);
//    for (Pair<Package, ClassSide> pair : containingPackages) {
//      if (CDAssociationHelper.sameAssociation(pair.a.getAstcdAssociation(), association) || CDAssociationHelper.sameAssociationInReverse(pair.a.getAstcdAssociation(), association)) {
//        if (getConnectedClasses(pair.a.getAstcdAssociation(), helper.getSrcCD()).a == helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType())) {
//          if (pair.a.getAstcdAssociation().getRight().getCDCardinality().isMult() || pair.a.getAstcdAssociation().getRight().getCDCardinality().isAtLeastOne()) {
//            return false;
//          }
//        } else {
//          if (pair.a.getAstcdAssociation().getLeft().getCDCardinality().isMult() || pair.a.getAstcdAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
//            return false;
//          }
//        }
//      }
//    }
//    return true;
//  }

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
      Package pack = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a,
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b,
        association, null, false, false);
      objectSet.add(pack);
    } else if (cardinalityLeft == 2 && cardinalityRight == 1) {
      Package pack1 = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a,
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b,
        association, null, false, false);
      Package pack2 = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a,
        pack1.getTgtClass(),
        association, null, false, false);
      objectSet.add(pack1);
      objectSet.add(pack2);
    } else if (cardinalityLeft == 1 && cardinalityRight == 2) {
      Package pack1 = new Package(Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).a,
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b,
        association, null, false, false);
      Package pack2 = new Package(pack1.getSrcClass(),
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD()).b,
        association, null, false, false);
      objectSet.add(pack1);
      objectSet.add(pack2);
    }
    return objectSet;
  }

  public boolean moreObjects(Set<Package> objectSet) {
    return findUnprocessedObjects(objectSet) != null;
  }

  public Set<ASTODElement> getObjForOD(ASTCDClass astcdClass) {
    Set<ASTODElement> set = new HashSet<>();
    Set<Package> packages = createChainsForNewClass(astcdClass, new HashSet<>());
    System.out.println("Created packages are:");
    for (Package pack : packages) {
      System.out.println(pack.getSrcClass().getMCObjectType().printType());
      System.out.println(pack.getTgtClass().getMCObjectType().printType());
      System.out.println("====================================");
    }
    System.out.println("END");
    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(packages);
    System.out.println("Unprocessed objects are:");
    assert unprocessedObjects != null;
    for (ASTODObject astodObject : unprocessedObjects) {
      System.out.println(astodObject.getMCObjectType().printType());
    }
    System.out.println("END");
    while (moreObjects(packages)) {
      System.out.println("BEGINNING");
      for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
        packages.addAll(createChainsForExistingObj(astodObject, packages));
      }
//      for (Package pack : packages) {
//        System.out.println(pack.getSrcClass().getMCObjectType().printType());
//        if (pack.getTgtClass() != null) {
//          System.out.println(pack.getTgtClass().getMCObjectType().printType());
//        }
//        System.out.println("====================================");
//      }
//      System.out.println(findUnprocessedObjects(packages));
//      List<ASTODObject> list = new ArrayList<>(findProcessedObjects(packages));
//      System.out.println(list.get(0).getMCObjectType().printType());
//      System.out.println("Unprocessed objects?" + (findUnprocessedObjects(packages) == null));
    }
//    System.out.println("BEGINNING");
//    for (ASTODObject astodObject : unprocessedObjects) {
//      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
//      packages.addAll(createChainsForExistingObj(astodObject, packages));
//    }
//    System.out.println("Created packages are:");
//    for (Package pack: packages){
//      System.out.println(pack.getSrcClass().getMCObjectType().printType());
//      System.out.println(pack.getTgtClass().getMCObjectType().printType());
//      System.out.println("====================================");
//    }
//    System.out.println("END");
//    System.out.println("Unprocessed objects are:");
//    for (ASTODObject astodObject : findUnprocessedObjects(packages)){
//      System.out.println(astodObject.getMCObjectType().printType());
//    }
//    System.out.println("END");
//    System.out.println("BEGINNING 2");
//    for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
//      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
//      packages.addAll(createChainsForExistingObj(astodObject, packages));
//    }
//    System.out.println("Created packages are:");
//    for (Package pack: packages){
//      System.out.println(pack.getSrcClass().getMCObjectType().printType());
//      if (pack.getTgtClass() != null){
//        System.out.println(pack.getTgtClass().getMCObjectType().printType());
//      }
////      System.out.println(pack.getTgtClass().getMCObjectType().printType());
//      System.out.println("====================================");
//    }
//    System.out.println("END");
//    System.out.println("Unprocessed objects are:");
//    System.out.println("Unprocessed objects?" + (findUnprocessedObjects(packages) == null));
//    System.out.println("END");


//    for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
//      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
//      packages.addAll(createChainsForExistingObj(astodObject, packages));
//    }
//    for (ASTODObject astodObject : findUnprocessedObjects(packages)) {
//      assert helper.getCDClass(helper.getSrcCD(), astodObject.getMCObjectType().printType()) != null;
//      packages.addAll(createChainsForExistingObj(astodObject, packages));
//    }
    for (Package pack : packages) {
      //unfold packages into set
      set.add(pack.getAssociation());
      set.add(pack.getSrcClass());
      set.add(pack.getTgtClass());
    }
    return set;
  }

  public Set<Package> createChainsForNewClass(ASTCDClass astcdClass, Set<Package> objectSet) {
    List<AssocStruct> list = helper.getSrcMap().get(astcdClass);
    for (AssocStruct assocStruct : list) {
      ASTODObject srcObject = null;
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        srcObject = builder.buildObj("", Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.getName(),
          splitStringByCharacter(helper.getSuperClasses(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a), ','),
          helper.getAttributesOD(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a));
        tgtObject = getObjectForTgt(astcdClass, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct.getAssociation(), objectSet);
      } else if (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
        srcObject = builder.buildObj("", Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.getName(),
          splitStringByCharacter(helper.getSuperClasses(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b), ','),
          helper.getAttributesOD(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b));
        tgtObject = getObjectForTgt(astcdClass, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct.getAssociation(), objectSet);
      }
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
            assocStruct.getAssociation(), ClassSide.Left, true, false);
        } else {
          pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a,
            srcObject,
            assocStruct.getAssociation(), ClassSide.Right, false, true);
        }
      }
      objectSet.add(pack);
    }
    return objectSet;
  }
  public ASTODObject getObjectForTgt(ASTCDClass srcClass, ASTCDClass tgtClass, ASTCDAssociation association, Set<Package> objectSet){
    Set<ASTODObject> processedObjects = findProcessedObjects(objectSet);
    for (ASTODObject object : processedObjects){
      if (helper.getCDClass(helper.getSrcCD(), tgtClass.getName()) == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssoc(true, object, srcClass, association, getContainingPackages(object, objectSet))){
          return object;
        }
      }
    }
    Set<ASTODObject> unprocessedObjects = findUnprocessedObjects(objectSet);
    if (unprocessedObjects == null){
      return null;
    }
    for (ASTODObject object : unprocessedObjects){
      if (helper.getCDClass(helper.getSrcCD(), tgtClass.getName()) == helper.getCDClass(helper.getSrcCD(), object.getMCObjectType().printType())){
        if (!objectUsesAssoc(false, object, srcClass, association, getContainingPackages(object, objectSet))){
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
    if (list.isEmpty()){
      Package pack = new Package(object);
      objectSet.add(pack);
    } else {
      objectSet.addAll(createChainsHelper(object, list, objectSet));
    }
    return objectSet;
  }

  public Set<Package> createChainsHelper(ASTODObject object, List<AssocStruct> list, Set<Package> objectSet){
    for (AssocStruct assocStruct : list) {
      ASTODObject tgtObject = null;
      if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
        || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
        tgtObject = getObjectForTgtSpec(object, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b, assocStruct.getAssociation(), objectSet);
      } else if (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
        tgtObject = getObjectForTgtSpec(object, Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a, assocStruct.getAssociation(), objectSet);
      }
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
            Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b,
            assocStruct.getAssociation(), ClassSide.Left, true, false);
        } else {
          pack = new Package(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a,
            object,
            assocStruct.getAssociation(), ClassSide.Right, false, true);
        }
      }
      objectSet.add(pack);
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

  //number of associations from class to class - done
  //TODO: add checks if class is abstract - search for subclass (some functions already do this) - done for changedTypes and changedAssociations
  /*public List<ASTODArtifact> generateODs(
    ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD){
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
          generateElements(association, null, "", "", "added association", comment),
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

//    for (Pair<ASTCDAssociation, ASTCDClass> pair : syntaxDiff.deletedAssocList()){
//      Pair<ASTCDClass, ASTCDClass> classes = Syn2SemDiffHelper.getConnectedClasses(pair.a, srcCD);
//      if (!helper.getNotInstanClassesSrc().contains(classes.a) && !helper.getNotInstanClassesSrc().contains(classes.b)) {
//        ASTCDClass leftClass = classes.a;
//        ASTCDClass rightClass = classes.b;
//        if (pair.a.getModifier().isAbstract()) {
//          leftClass = helper.minDiffWitness(classes.a);
//        }
//        if (pair.b.getModifier().isAbstract()) {
//          rightClass = helper.minDiffWitness(classes.b);
//        }
//        String comment = "The association between the classes" + association.b.getSymbol().getInternalQualifiedName() + "and" + association.b.getSymbol().getInternalQualifiedName() + "has been removed from the diagram.";
//        ASTODArtifact astodArtifact = generateArtifact(oDTitleForAssoc(association.a),
//          generateElements(association.a, null, "", "", "deleted association", comment),
//          null);
//        artifactList.add(astodArtifact);
//      }
//    }

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
  }*/
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
    //elements = getObjectsForOD(astcdClass);
    elements = null;
    ASTODObject matchedObject = null;
    for (ASTODElement element : elements) {
      if (element instanceof ASTODObject) {
        if (((ASTODObject) element).getMCObjectType().printType().equals(astcdClass.getName())) {
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
    Pair<Set<ASTODElement>, ASTODLink> pair = null;
      //getObjectsForOD(association, integers.get(0), integers.get(1));
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
  public ASTODAttribute createAttribute(String type, String name, ASTExpression value){
    return builder.buildAttr(type, name, null);
  }
  public ASTODObject createObject(String id, String type, Collection<String> types, Collection<ASTODAttribute> attrs){
    return builder.buildObj(id, type, types, attrs);
  }
  public ASTODLink createLink(ASTODObject srcObj, String roleName, ASTODObject trgObj, String direction){
   return builder.buildLink(srcObj, roleName, "", trgObj, direction);
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
