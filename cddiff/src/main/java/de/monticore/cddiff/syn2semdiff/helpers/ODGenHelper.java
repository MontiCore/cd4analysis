package de.monticore.cddiff.syn2semdiff.helpers;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;
import static de.monticore.cddiff.syn2semdiff.odgen.Syn2SemDiffHelper.getConnectedTypes;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.syn2semdiff.datastructures.AssocStruct;
import de.monticore.cddiff.syn2semdiff.datastructures.ClassSide;
import de.monticore.cddiff.syn2semdiff.odgen.ODBuilder;
import de.monticore.cddiff.syn2semdiff.odgen.Syn2SemDiffHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODObject;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ODGenHelper {
  private ASTCDCompilationUnit srcCD;

  private final ODBuilder odBuilder = new ODBuilder();

  private final Syn2SemDiffHelper syn2SemDiffHelper;

  public ODGenHelper(ASTCDCompilationUnit srcCD, Syn2SemDiffHelper syn2SemDiffHelper) {
    this.srcCD = srcCD;
    this.syn2SemDiffHelper = syn2SemDiffHelper;
  }

  /**
   * Compute the String for <<instanceOf>> stereotype.
   *
   * @param astcdClass class from srcCD.
   * @return List of types as Strings.
   */
  public List<String> getSuperTypes(ASTCDClass astcdClass) {
    List<ASTCDType> typeList =
        new ArrayList<>(CDDiffUtil.getAllSuperTypes(astcdClass, srcCD.getCDDefinition()));
    List<String> typesString = new ArrayList<>();
    for (int i = typeList.size() - 1; i >= 0; i--) {
      String type = typeList.get(i).getSymbol().getInternalQualifiedName();
      typesString.add(type);
    }
    return typesString;
  }

  /**
   * Create all attributes for a class.
   *
   * @param astcdClass class from srcCD.
   * @param pair pair if the difference is in an added constant
   * @return List of attributes for object diagram.
   */
  public List<ASTODAttribute> getAttributesOD(
      ASTCDClass astcdClass, Pair<ASTCDAttribute, String> pair) {
    List<ASTCDAttribute> attributes = syn2SemDiffHelper.getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    if (pair != null) {
      odAttributes.add(
          odBuilder.buildAttr(pair.a.getMCType().printType(), pair.a.getName(), pair.b));
      attributes.remove(pair.a);
    }
    for (ASTCDAttribute attribute : attributes) {
      Pair<Boolean, String> attIsEnum = attIsEnum(attribute);
      if (attIsEnum.a) {
        odAttributes.add(
            odBuilder.buildAttr(
                attribute.getMCType().printType(), attribute.getName(), attIsEnum.b));
      } else {
        odAttributes.add(
            odBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
      }
    }
    return odAttributes;
  }

  /**
   * Get a class from a diagram based on its name.
   *
   * @param compilationUnit diagram.
   * @param className name of the class.
   * @return found class.
   */
  public static ASTCDClass getCDClass(ASTCDCompilationUnit compilationUnit, String className) {
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getSymbol().getInternalQualifiedName().equals(className)) {
        return astcdClass;
      }
    }
    return null; // the function is only used in the generator, so this statement is never reached
  }

  public int getClassSize(ASTCDClass astcdClass) {
    int attributeCount = syn2SemDiffHelper.getAllAttr(astcdClass).b.size();
    int associationCount = syn2SemDiffHelper.getAssociationCount(astcdClass, true);
    int otherAssocsCount = syn2SemDiffHelper.getOtherAssocs(astcdClass, true, false).size();
    return attributeCount + associationCount + otherAssocsCount;
  }

  /**
   * Check if an attribute from srcCD is an enum.
   *
   * @param attribute attribute to check.
   * @return pair of boolean and random constant.
   */
  public Pair<Boolean, String> attIsEnum(ASTCDAttribute attribute) {
    for (ASTCDEnum enum_ : srcCD.getCDDefinition().getCDEnumsList()) {
      if (enum_.getSymbol().getInternalQualifiedName().equals(attribute.getMCType().printType())) {
        return new Pair<>(true, enum_.getCDEnumConstant(0).getName());
      }
    }
    return new Pair<>(false, "");
  }

  /**
   * Get the AssocStruc that has the same type
   *
   * @param astcdClass class to search in
   * @param association association to match with
   * @return matched association, if found
   */
  public AssocStruct getAssocStrucForClass(ASTCDType astcdClass, ASTCDAssociation association) {
    for (AssocStruct assocStruct : syn2SemDiffHelper.getSrcMap().get(astcdClass)) {
      if (syn2SemDiffHelper.sameAssociation(assocStruct.getAssociation(), association, srcCD)) {
        return assocStruct;
      }
    }
    return null;
  }

  public Optional<ASTCDClass> getClassForTypeSrc(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      return Optional.of((ASTCDClass) astcdType);
    } else {
      return syn2SemDiffHelper.minSubClass(astcdType, true);
    }
  }

  /**
   * Check if an association is a superassociation of another one. For this, the direction and the
   * role names must be matched in the target direction. The associated classes of the
   * superassociation must be superclasses of the associated classes of the subAssoc.
   *
   * @param superAssoc superassociation as AssocStruct
   * @param subAssoc subassociation as AssocStruct
   * @return true if condition is fulfilled
   */
  public boolean isSubAssociationSrcSrc(AssocStruct superAssoc, AssocStruct subAssoc) {
    if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Left)
        && Syn2SemDiffHelper.matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
        && syn2SemDiffHelper.isSubclassWithSuper(
            Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
            Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).a)
        && syn2SemDiffHelper.isSubclassWithSuper(
            Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
            Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Right)
        && Syn2SemDiffHelper.matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && syn2SemDiffHelper.isSubclassWithSuper(
            Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
            Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).b)
        && syn2SemDiffHelper.isSubclassWithSuper(
            Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
            Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
        && superAssoc.getSide().equals(ClassSide.Left)
        && Syn2SemDiffHelper.matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && syn2SemDiffHelper.isSubclassWithSuper(
            Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
            Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).b)
        && syn2SemDiffHelper.isSubclassWithSuper(
            Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
            Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else
      return subAssoc.getSide().equals(ClassSide.Right)
          && superAssoc.getSide().equals(ClassSide.Right)
          && Syn2SemDiffHelper.matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
          && matchRoleNames(
              superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
          && matchRoleNames(
              superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
          && syn2SemDiffHelper.isSubclassWithSuper(
              Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
              Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).a)
          && syn2SemDiffHelper.isSubclassWithSuper(
              Syn2SemDiffHelper.getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
              Syn2SemDiffHelper.getConnectedTypes(subAssoc.getAssociation(), srcCD).b);
  }

  public List<AssocStruct> getTgtAssocs(ASTCDClass astcdClass) {
    List<AssocStruct> assocStructs = new ArrayList<>();
    Set<ASTCDClass> superClassSet =
        CDDiffUtil.getAllSuperclasses(astcdClass, srcCD.getCDDefinition().getCDClassesList());
    for (ASTCDClass superClass : superClassSet) {
      assocStructs.addAll(syn2SemDiffHelper.getOtherAssocs(superClass, true, false));
    }
    List<AssocStruct> copy = new ArrayList<>(assocStructs);
    for (AssocStruct assocStruct : copy) {
      for (AssocStruct assocStruct1 : copy) {
        if (assocStruct != assocStruct1 && isSubAssociationSrcSrc(assocStruct, assocStruct1)) {
          assocStructs.remove(assocStruct1);
        }
      }
    }

    return assocStructs;
  }

  public boolean isSubAssociationInReverse(AssocStruct superAssoc, AssocStruct subAssoc) {
    ClassSide subSide = subAssoc.getSide();
    ClassSide superSide = superAssoc.getSide();
    ASTCDAssociation superAssociation = superAssoc.getAssociation();
    ASTCDAssociation subAssociation = subAssoc.getAssociation();
    Pair<ASTCDType, ASTCDType> connectedTypesSuper =
        getConnectedTypes(superAssoc.getAssociation(), srcCD);
    Pair<ASTCDType, ASTCDType> connectedTypesSub =
        getConnectedTypes(subAssoc.getAssociation(), srcCD);

    // Check conditions based on ClassSide values
    if (superSide.equals(ClassSide.Left) && subSide.equals(ClassSide.Left)
        || superSide.equals(ClassSide.Right) && subSide.equals(ClassSide.Right)) {
      return rolesMatchAndInheritance(
          superAssociation.getLeft(),
          superAssociation.getRight(),
          subAssociation.getRight(),
          subAssociation.getLeft(),
          connectedTypesSuper,
          new Pair<>(connectedTypesSub.b, connectedTypesSub.a));
    } else if (superSide.equals(ClassSide.Right) && subSide.equals(ClassSide.Left)
        || superSide.equals(ClassSide.Left) && subSide.equals(ClassSide.Right)) {
      return rolesMatchAndInheritance(
          superAssociation.getLeft(),
          superAssociation.getRight(),
          subAssociation.getLeft(),
          subAssociation.getRight(),
          connectedTypesSuper,
          connectedTypesSub);
    } else {
      return false;
    }
  }

  public boolean rolesMatchAndInheritance(
      ASTCDAssocSide leftRoleSuper,
      ASTCDAssocSide rightRoleSuper,
      ASTCDAssocSide leftRoleSub,
      ASTCDAssocSide rightRoleSub,
      Pair<ASTCDType, ASTCDType> connectedTypesSuper,
      Pair<ASTCDType, ASTCDType> connectedTypesSub) {
    return CDAssociationHelper.matchRoleNames(leftRoleSuper, leftRoleSub)
        && CDAssociationHelper.matchRoleNames(rightRoleSuper, rightRoleSub)
        && syn2SemDiffHelper.getSrcSubMap().get(connectedTypesSuper.a).contains(connectedTypesSub.a)
        && syn2SemDiffHelper
            .getSrcSubMap()
            .get(connectedTypesSuper.b)
            .contains(connectedTypesSub.b);
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
            getTgtAssocs(ODGenHelper.getCDClass(srcCD, tgtObject.getMCObjectType().printType())));
    List<Pair<AssocStruct, ClassSide>> createdAssocs = mapTgt.get(tgtObject);
    List<AssocStruct> copy = new ArrayList<>(list);
    for (AssocStruct assocStruct : copy) {
      for (Pair<AssocStruct, ClassSide> createdAssoc : createdAssocs) {
        if (isSubAssociationSrcSrc(assocStruct, createdAssoc.a)) {
          list.remove(assocStruct);
        }
      }
    }
    return list;
  }
}
