package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4TypeHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4InheritanceHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4SearchHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4ConflictHelper.*;

public class CDWrapperGenerator {
  protected Map<String, CDTypeWrapper> cDTypeWrapperGroup = new HashMap<>();
  protected Map<String, CDAssociationWrapper> cDAssociationWrapperGroup = new HashMap<>();
  protected MutableGraph<String> inheritanceGraph = GraphBuilder.directed().build();
  protected Map<String, Set<String>> enumClassMap = new HashMap<>();

  /**
   * generating CDWrapper
   */
  public CDWrapper generateCDWrapper(ASTCDCompilationUnit cd, CDSemantics type) {
    CDWrapper CDWrapper = new CDWrapper();
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    createCDTypeWrapperForSimpleClassAndAbstractClass(cd, scope);
    createCDTypeWrapperForInterface(cd, scope);
    createCDTypeWrapperForEnum(cd, scope);
    createCDAssociationWrapper(cd);
    solveInheritance();
    solveSuperclassesAndSubclasses();
    solveOverlap();
    solveAttributesConflict();
    solveAssociationConflict();

    CDWrapper.setModel(cd);
    CDWrapper.setType(type);
    CDWrapper.setCDTypeWrapperGroup(cDTypeWrapperGroup);
    CDWrapper.setCDAssociationWrapperGroup(cDAssociationWrapperGroup);
    CDWrapper.setInheritanceGraph(inheritanceGraph);
    CDWrapper.setRefSetAssociationList(
        createCDRefSetAssociationWrapper(cDAssociationWrapperGroup, inheritanceGraph));
    return CDWrapper;
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * create CDTypeWrapper object for class and abstract class in AST
   */
  public void createCDTypeWrapperForSimpleClassAndAbstractClass(ASTCDCompilationUnit cd,
      ICD4CodeArtifactScope scope) {
    List<ASTCDClass> astCDClassList = cd.getCDDefinition().getCDClassesList();
    List<ASTCDEnum> astCDEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astCDClassList) {
      CDTypeWrapper cDTypeWrapper = createCDTypeWrapperHelper(astcdType, scope, astCDEnumList);
      cDTypeWrapperGroup.put(cDTypeWrapper.getName(), cDTypeWrapper);
    }
  }

  /**
   * create CDTypeWrapper object for interface in AST
   */
  public void createCDTypeWrapperForInterface(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDInterface> astCDInterfaceList = cd.getCDDefinition().getCDInterfacesList();
    List<ASTCDEnum> astCDEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astCDInterfaceList) {
      CDTypeWrapper cDTypeWrapper = createCDTypeWrapperHelper(astcdType, scope, astCDEnumList);
      cDTypeWrapperGroup.put(cDTypeWrapper.getName(), cDTypeWrapper);
    }
  }

  /**
   * create CDTypeWrapper object for enum in AST
   */
  public void createCDTypeWrapperForEnum(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDEnum> astCDEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astCDEnumList) {
      CDTypeWrapper cDTypeWrapper = createCDTypeWrapperHelper(astcdType, scope, astCDEnumList);
      cDTypeWrapperGroup.put(cDTypeWrapper.getName(), cDTypeWrapper);
    }
  }


  /**
   * all creating CDTypeWrapper functions are based on this helper
   */
  public CDTypeWrapper createCDTypeWrapperHelper(ASTCDType astcdType, ICD4CodeArtifactScope scope, List<ASTCDEnum> astCDEnumList) {
    CDTypeWrapper cDTypeWrapper = new CDTypeWrapper(astcdType);

    if (!(astcdType instanceof ASTCDEnum)) {

      // create InheritanceGraph
      Set<ASTCDType> directSuperSet = CDInheritanceHelper.getDirectSuperClasses(astcdType, scope);
      directSuperSet.addAll(CDInheritanceHelper.getDirectInterfaces(astcdType, scope));
      createInheritanceGraph(astcdType, directSuperSet);

      // add attributes
      for (ASTCDAttribute astcdAttribute : astcdType.getCDAttributeList()) {
        if (astCDEnumList.stream().anyMatch(s -> s.getSymbol().getFullName().equals(astcdAttribute.printType()))) {
          cDTypeWrapper.addAttribute(astcdAttribute);
          creatEnumClassMapHelper("CDWrapperEnum_" + astcdAttribute.printType(), cDTypeWrapper.getName());
        } else {
          cDTypeWrapper.addAttribute(astcdAttribute);
        }
      }
    } else {
      // add EnumClass into InheritanceGraph
      createInheritanceGraph(astcdType);
      // add CDWrapperLink4EnumClass
      cDTypeWrapper.setCDWrapperLink4EnumClass(enumClassMap.get(cDTypeWrapper.getName()));
    }

    return cDTypeWrapper;
  }

  /**
   * After all CDTypeWrapper created, putting the temporary enumClassMap into CDTypeWrapperGroup.
   */
  public void creatEnumClassMapHelper(String enumClass, String baseClass) {
    Set<String> set = enumClassMap.getOrDefault(enumClass, new HashSet<>());
    set.add(baseClass);
    enumClassMap.put(enumClass, set);
  }

  /**
   * create inheritance graph
   * childClass -> parentClass
   */
  public void createInheritanceGraph(ASTCDType child, Collection<ASTCDType> directSuperList) {
    String childClass = getCDTypeWrapperKindStrHelper(distinguishASTCDTypeHelper(child))
        + "_"
        + child.getSymbol().getFullName();
    inheritanceGraph.addNode(childClass);
    directSuperList.forEach(parent -> {
      String parentClass = getCDTypeWrapperKindStrHelper(distinguishASTCDTypeHelper(parent))
          + "_"
          + parent.getSymbol().getFullName();
      inheritanceGraph.putEdge(childClass, parentClass);
    });
  }

  /**
   * create inheritance graph
   * add Enum class
   */
  public void createInheritanceGraph(ASTCDType astcdType) {
    String enumClass = getCDTypeWrapperKindStrHelper(distinguishASTCDTypeHelper(astcdType))
        + "_"
        + astcdType.getSymbol().getFullName();
    inheritanceGraph.addNode(enumClass);
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * create CDAssociationWrapper object for association in AST
   */
  public void createCDAssociationWrapper(ASTCDCompilationUnit cd) {
    List<ASTCDAssociation> astCDAssociationList = cd.getCDDefinition().getCDAssociationsList();
    for (ASTCDAssociation astcdAssociation : astCDAssociationList) {
      createCDAssociationWrapperHelper(astcdAssociation, false);
    }
  }

  /**
   * the creating CDAssociationWrapper functions are based on this helper
   */
  public void createCDAssociationWrapperHelper(ASTCDAssociation astcdAssociation, Boolean isInherited) {
    // add role name if the original ASTCDAssociation has no role name for one side or both side
    generateASTCDAssociationRoleName(astcdAssociation);
    generateASTCDAssociationCardinality(astcdAssociation);
    CDAssociationWrapper currentAssoc = new CDAssociationWrapper(astcdAssociation, isInherited);
    currentAssoc.setCDWrapperLeftClass(
      getCDTypeWrapper4OriginalClassName(cDTypeWrapperGroup, currentAssoc.getLeftOriginalClassName()));
    currentAssoc.setCDWrapperRightClass(
      getCDTypeWrapper4OriginalClassName(cDTypeWrapperGroup, currentAssoc.getRightOriginalClassName()));
    cDAssociationWrapperGroup.put(currentAssoc.getName(), currentAssoc);
  }

  /**
   * solve the inheritance problem:
   *  1. add inherited attributes into corresponding CDTypeWrapper
   *  2. generate inherited associations and put them into CDAssociationWrapperGroup
   */
  private void solveInheritance() {
    List<List<String>> waitList = new ArrayList<>();
    CDWrapper4InheritanceHelper cdWrapper4InheritanceHelper = new CDWrapper4InheritanceHelper();
    getAllBottomCDTypeWrapperNode(inheritanceGraph).forEach(cDTypeWrapperName ->
      waitList.addAll(
          cdWrapper4InheritanceHelper.getAllInheritancePath4CDTypeWrapper(
              cDTypeWrapperGroup.get(cDTypeWrapperName), inheritanceGraph)));
    waitList.forEach(path -> {
      if (path.size() > 1) {

        for (int i = 0; i < path.size() - 1; i++) {
          CDTypeWrapper parent = cDTypeWrapperGroup.get(path.get(i));
          CDTypeWrapper child = cDTypeWrapperGroup.get(path.get(i + 1));

          // for attributes
          parent.getEditedElement().getCDAttributeList().forEach(e -> {
            String type = "CDWrapperEnum_" + e.printType();
            // update enumClassMap
            if (enumClassMap.containsKey(type)) {
              Set<String> set = enumClassMap.get(type);
              set.add(child.getName());
              enumClassMap.put(type, set);
            }
            // add inherited attribute into child CDTypeWrapper
            child.addAttribute(e);

          });

          // update all CDWrapperEnum
          updateCDWrapperEnum();

          // for association
          String parentOriginalName = parent.getOriginalClassName();
          String childOriginalName = child.getOriginalClassName();
          Map<String, CDAssociationWrapper> associationMap =
            fuzzySearchCDAssociationWrapperByClassName(cDAssociationWrapperGroup, parentOriginalName);

          associationMap.forEach((oldName, oldCDAssociationWrapper) -> {

            String prefix = oldName.split("_")[0];
            String leftClass = oldCDAssociationWrapper.getCDWrapperLeftClass().getOriginalClassName();
            String leftRoleName = oldCDAssociationWrapper.getCDWrapperLeftClassRoleName();
            String direction = formatDirection(oldCDAssociationWrapper.getCDAssociationWrapperDirection());
            String rightRoleName = oldCDAssociationWrapper.getCDWrapperRightClassRoleName();
            String rightClass = oldCDAssociationWrapper.getCDWrapperRightClass().getOriginalClassName();
            leftClass = leftClass.equals(parentOriginalName) ? childOriginalName : leftClass;
            rightClass = rightClass.equals(parentOriginalName) ? childOriginalName : rightClass;
            String newName =
              prefix + "_" + leftClass + "_" + leftRoleName + "_" + direction + "_" + rightRoleName + "_" + rightClass;
            if (!cDAssociationWrapperGroup.containsKey(newName)) {
              ASTCDAssociation oldASTAssoc = oldCDAssociationWrapper.getOriginalElement();
              ASTCDAssociation newASTAssoc = oldASTAssoc.deepClone();
              CDTypeWrapper leftCDTypeWrapper = oldCDAssociationWrapper.getCDWrapperLeftClass();
              CDTypeWrapper rightCDTypeWrapper = oldCDAssociationWrapper.getCDWrapperRightClass();
              if (oldCDAssociationWrapper.getCDWrapperLeftClass().getOriginalClassName().contains(parentOriginalName)) {
                newASTAssoc = editASTCDAssociationLeftSideByCDTypeWrapper(newASTAssoc, child);
                leftCDTypeWrapper = child;
              }
              if (oldCDAssociationWrapper.getCDWrapperRightClass().getOriginalClassName().contains(parentOriginalName)) {
                newASTAssoc = editASTCDAssociationRightSideByCDTypeWrapper(newASTAssoc, child);
                rightCDTypeWrapper = child;
              }
              CDAssociationWrapper newCDAssociationWrapper = new CDAssociationWrapper(newASTAssoc, true);
              newCDAssociationWrapper.setCDWrapperLeftClass(leftCDTypeWrapper);
              newCDAssociationWrapper.setCDWrapperRightClass(rightCDTypeWrapper);
              cDAssociationWrapperGroup.put(newCDAssociationWrapper.getName(),
                  newCDAssociationWrapper);
            }
          });
        }
      }
    });
  }

  /**
   * After solving inheritance problem update the Enum CDTypeWrapper in CDTypeWrapperGroup
   */
  private void updateCDWrapperEnum() {
    enumClassMap.forEach((k, v) -> {
      CDTypeWrapper cDWrapperEnum = cDTypeWrapperGroup.get(k);
      cDWrapperEnum.setCDWrapperLink4EnumClass(v);
    });
  }

  /**
   * solve the duplicate association with overlap part
   */
  private void solveOverlap() {

    Map<String, CDAssociationWrapper> clonedCDAssociationWrapperGroup =
        cDAssociationWrapperGroup.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    clonedCDAssociationWrapperGroup.forEach((currentAssocName, currentAssoc) -> {
      if (currentAssoc.getCDWrapperKind() == CDAssociationWrapperKind.CDWRAPPER_ASC) {
        List<CDAssociationWrapperPack> matchedAssocList =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(
              cDAssociationWrapperGroup, currentAssoc);
        if (matchedAssocList.size() > 0) {
          cDAssociationWrapperGroup.remove(currentAssoc.getName());
          AtomicReference<CDAssociationWrapper> newCDAssocWrapper = new AtomicReference<>(currentAssoc);

          matchedAssocList.forEach(e -> {
            CDAssociationWrapper existAssoc = e.getCDAssociationWrapper();
            boolean isReverse = e.isReverse();

            if (!isReverse) {
              String directionResult = cDAssociationWrapperDirectionHelper(
                existAssoc.getCDAssociationWrapperDirection(), newCDAssocWrapper.get().getCDAssociationWrapperDirection());
              CDAssociationWrapperCardinality leftCardinalityResult = cDAssociationWrapperCardinalityHelper(
                existAssoc.getCDWrapperLeftClassCardinality(), newCDAssocWrapper.get().getCDWrapperLeftClassCardinality());
              CDAssociationWrapperCardinality rightCardinalityResult = cDAssociationWrapperCardinalityHelper(
                existAssoc.getCDWrapperRightClassCardinality(), newCDAssocWrapper.get().getCDWrapperRightClassCardinality());
              switch (directionResult) {
                case "current":
                  cDAssociationWrapperGroup.remove(existAssoc.getName());
                  newCDAssocWrapper.get().setCDWrapperLeftClassCardinality(leftCardinalityResult);
                  newCDAssocWrapper.get().setCDWrapperRightClassCardinality(rightCardinalityResult);
                  newCDAssocWrapper.get().setCDWrapperKind(CDAssociationWrapperKind.CDWRAPPER_ASC);
                  break;
                case "exist":
                  cDAssociationWrapperGroup.remove(existAssoc.getName());
                  existAssoc.setCDWrapperLeftClassCardinality(leftCardinalityResult);
                  existAssoc.setCDWrapperRightClassCardinality(rightCardinalityResult);
                  existAssoc.setCDWrapperKind(CDAssociationWrapperKind.CDWRAPPER_ASC);
                  newCDAssocWrapper.set(existAssoc);
                  break;
                default:
                  break;
              }
            } else {
              String directionResult4Current = cDAssociationWrapperDirectionHelper(
                reverseDirection(existAssoc.getCDAssociationWrapperDirection()), newCDAssocWrapper.get().getCDAssociationWrapperDirection());
              CDAssociationWrapperCardinality leftCardinalityResult4Current = cDAssociationWrapperCardinalityHelper(
                existAssoc.getCDWrapperRightClassCardinality(), newCDAssocWrapper.get().getCDWrapperLeftClassCardinality());
              CDAssociationWrapperCardinality rightCardinalityResult4Current = cDAssociationWrapperCardinalityHelper(
                existAssoc.getCDWrapperLeftClassCardinality(), newCDAssocWrapper.get().getCDWrapperRightClassCardinality());
              switch (directionResult4Current) {
                case "current":
                  cDAssociationWrapperGroup.remove(existAssoc.getName());
                  newCDAssocWrapper.get().setCDWrapperLeftClassCardinality(leftCardinalityResult4Current);
                  newCDAssocWrapper.get().setCDWrapperRightClassCardinality(rightCardinalityResult4Current);
                  newCDAssocWrapper.get().setCDWrapperKind(CDAssociationWrapperKind.CDWRAPPER_ASC);
                  break;
                case "exist":
                  cDAssociationWrapperGroup.remove(existAssoc.getName());
                  existAssoc.setCDWrapperLeftClassCardinality(rightCardinalityResult4Current);
                  existAssoc.setCDWrapperRightClassCardinality(leftCardinalityResult4Current);
                  existAssoc.setCDWrapperKind(CDAssociationWrapperKind.CDWRAPPER_ASC);
                  CDAssociationWrapper reversedAssoc = reverseCDAssociationWrapper(existAssoc, existAssoc.getEditedElement().getCDAssocDir());
                  newCDAssocWrapper.set(reversedAssoc);
                  break;
                default:
                  break;
              }
            }
          });
          cDAssociationWrapperGroup.put(newCDAssocWrapper.get().getName(), newCDAssocWrapper.get());
        }
      }

    });
  }

  private void solveSuperclassesAndSubclasses() {
    cDTypeWrapperGroup.forEach((cDTypeWrapperName, cDTypeWrapper) -> {
      cDTypeWrapper.setSuperclasses(getSuperClassSet(inheritanceGraph, cDTypeWrapperName));
      cDTypeWrapper.setSubclasses(getInheritedClassSet(inheritanceGraph, cDTypeWrapperName));
    });
  }

  private void solveAttributesConflict() {
    cDTypeWrapperGroup.forEach((cDTypeWrapperName, cDTypeWrapper) -> {
      if (!cDTypeWrapper.isOpen()) {
        cDTypeWrapper.getSubclasses().forEach(subCDTypeWrapperName ->
            fuzzySearchCDAssociationWrapperByClassName(cDAssociationWrapperGroup,
                subCDTypeWrapperName.split("_")[1])
                .values()
                .forEach(cdAssociationWrapper ->
                    cdAssociationWrapper.setStatus(CDStatus.LOCKED)));
      }
    });
  }

  private void solveAssociationConflict() {
    checkConflict4CDAssociationWrapper(cDAssociationWrapperGroup, inheritanceGraph);
  }

}
