package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;

import java.util.*;

import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.*;

public class CD2DGGenerator {
  protected Map<String, DiffClass> diffClassGroup = new HashMap<>();
  protected Map<String, DiffAssociation> diffAssociationGroup = new HashMap<>();
  protected MutableGraph<String> inheritanceGraph = GraphBuilder.directed().build();
  protected Map<String, Set<String>> enumClassMap = new HashMap<>();

  /**
   * generating DifferentGroup
   */
  public DifferentGroup generateDifferentGroup(ASTCDCompilationUnit cd, CDSemantics type) {
    DifferentGroup differentGroup = new DifferentGroup();
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    createDiffClassForInterface(cd, scope);
    createDiffClassForEnum(cd, scope);
    createDiffAssociation(cd);
    solveInheritance();

    differentGroup.setModel(cd);
    differentGroup.setType(type);
    differentGroup.setDiffClassGroup(diffClassGroup);
    differentGroup.setDiffAssociationGroup(diffAssociationGroup);
    differentGroup.setInheritanceGraph(inheritanceGraph);
    differentGroup.setRefSetAssociationList(createDiffRefSetAssociation(diffAssociationGroup, inheritanceGraph));
    return differentGroup;
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * create DiffClass object for class and abstract class in AST
   */
  public void createDiffClassForSimpleClassAndAbstractClass(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDClass> astcdClassList = cd.getCDDefinition().getCDClassesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdClassList) {
      DiffClass diffClass = createDiffClassHelper(astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
  }

  /**
   * create DiffClass object for interface in AST
   */
  public void createDiffClassForInterface(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDInterface> astcdInterfaceList = cd.getCDDefinition().getCDInterfacesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdInterfaceList) {
      DiffClass diffClass = createDiffClassHelper(astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
  }

  /**
   * create DiffClass object for enum in AST
   */
  public void createDiffClassForEnum(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdEnumList) {
      DiffClass diffClass = createDiffClassHelper(astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
  }


  /**
   * all creating DiffClass functions are based on this helper
   */
  public DiffClass createDiffClassHelper(ASTCDType astcdType, ICD4CodeArtifactScope scope, List<ASTCDEnum> astcdEnumList) {
    DiffClass diffClass = new DiffClass(astcdType);

    if (!astcdType.getClass().equals(ASTCDEnum.class)) {

      // create InheritanceGraph
      Set<ASTCDType> directSuperSet = CDInheritanceHelper.getDirectSuperClasses(astcdType, scope);
      directSuperSet.addAll(CDInheritanceHelper.getDirectInterfaces(astcdType, scope));
      createInheritanceGraph(astcdType, directSuperSet);

      // add attributes
      for (ASTCDAttribute astcdAttribute : astcdType.getCDAttributeList()) {
        if (astcdEnumList.stream().anyMatch(s -> s.getName().equals(astcdAttribute.printType()))) {
          diffClass.addAttribute(astcdAttribute);
          creatEnumClassMapHelper("DiffEnum_" + astcdAttribute.printType(), diffClass.getName());
        } else {
          diffClass.addAttribute(astcdAttribute);
        }
      }
    } else {
      // add diffLink4EnumClass
      diffClass.setDiffLink4EnumClass(enumClassMap.get(diffClass.getName()));
    }

    return diffClass;
  }

  /**
   * After all DiffClass created, putting the temporary enumClassMap into DiffClassGroup.
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
    String childClass = getDiffClassKindStrHelper(distinguishASTCDTypeHelper(child)) + "_" + child.getSymbol().getFullName();
    inheritanceGraph.addNode(childClass);
    directSuperList.forEach(parent -> {
      String parentClass = getDiffClassKindStrHelper(distinguishASTCDTypeHelper(parent)) + "_" + parent.getSymbol().getFullName();
      inheritanceGraph.putEdge(childClass, parentClass);
    });
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * create DiffAssociation object for association in AST
   */
  public void createDiffAssociation(ASTCDCompilationUnit cd) {
    List<ASTCDAssociation> astcdAssociationList = cd.getCDDefinition().getCDAssociationsList();
    for (ASTCDAssociation astcdAssociation : astcdAssociationList) {
      createDiffAssociationHelper(astcdAssociation, false);
    }
  }

  /**
   * the creating DiffAssociation functions are based on this helper
   */
  public void createDiffAssociationHelper(ASTCDAssociation astcdAssociation, Boolean isInherited) {
    // add role name if the original ASTCDAssociation has no role name for one side or both side
    astcdAssociation = generateASTCDAssociationRoleName(astcdAssociation);
    DiffAssociation currentAssoc = new DiffAssociation(astcdAssociation, isInherited);
    currentAssoc.setDiffLeftClass(findDiffClass4OriginalClassName(diffClassGroup, currentAssoc.getLeftOriginalClassName()));
    currentAssoc.setDiffRightClass(findDiffClass4OriginalClassName(diffClassGroup, currentAssoc.getRightOriginalClassName()));

    List<Map<String, Object>> matchedAssocList = fuzzySearchDiffAssociationByDiffAssociationWithoutDirection(diffAssociationGroup, currentAssoc);
    if (matchedAssocList.size() > 0) {
      matchedAssocList.forEach(e -> {
        DiffAssociation existAssoc = (DiffAssociation) e.get("diffAssociation");
        boolean isReverse = (boolean) e.get("isReverse");

        if (!isReverse) {
          String directionResult = diffAssociationDirectionHelper(existAssoc.getDiffDirection(), currentAssoc.getDiffDirection());
          DifferentGroup.DiffAssociationCardinality leftCardinalityResult =
            diffAssociationCardinalityHelper(existAssoc.getDiffLeftClassCardinality(), currentAssoc.getDiffLeftClassCardinality());
          DifferentGroup.DiffAssociationCardinality rightCardinalityResult =
            diffAssociationCardinalityHelper(existAssoc.getDiffRightClassCardinality(), currentAssoc.getDiffRightClassCardinality());
          switch (directionResult) {
            case "current":
              diffAssociationGroup.remove(existAssoc.getName());
              currentAssoc.setDiffLeftClassCardinality(leftCardinalityResult);
              currentAssoc.setDiffRightClassCardinality(rightCardinalityResult);
              diffAssociationGroup.put(currentAssoc.getName(), currentAssoc);
              break;
            case "exist":
              existAssoc.setDiffLeftClassCardinality(leftCardinalityResult);
              existAssoc.setDiffRightClassCardinality(rightCardinalityResult);
              diffAssociationGroup.put(existAssoc.getName(), existAssoc);
              break;
            default:
              diffAssociationGroup.put(currentAssoc.getName(), currentAssoc);
              break;
          }
        } else {
          String directionResult4Current = diffAssociationDirectionHelper(reverseDirection(existAssoc.getDiffDirection()), currentAssoc.getDiffDirection());
          DifferentGroup.DiffAssociationCardinality leftCardinalityResult4Current =
            diffAssociationCardinalityHelper(existAssoc.getDiffRightClassCardinality(), currentAssoc.getDiffLeftClassCardinality());
          DifferentGroup.DiffAssociationCardinality rightCardinalityResult4Current =
            diffAssociationCardinalityHelper(existAssoc.getDiffLeftClassCardinality(), currentAssoc.getDiffRightClassCardinality());
          switch (directionResult4Current) {
            case "current":
              diffAssociationGroup.remove(existAssoc.getName());
              currentAssoc.setDiffLeftClassCardinality(leftCardinalityResult4Current);
              currentAssoc.setDiffRightClassCardinality(rightCardinalityResult4Current);
              diffAssociationGroup.put(currentAssoc.getName(), currentAssoc);
              break;
            case "exist":
              existAssoc.setDiffLeftClassCardinality(rightCardinalityResult4Current);
              existAssoc.setDiffRightClassCardinality(leftCardinalityResult4Current);
              diffAssociationGroup.put(existAssoc.getName(), existAssoc);
              break;
            default:
              diffAssociationGroup.put(currentAssoc.getName(), currentAssoc);
              break;
          }
        }
      });
    } else {
      diffAssociationGroup.put(currentAssoc.getName(), currentAssoc);
    }
  }

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  /**
   * solve the inheritance problem:
   *  1. add inherited attributes into corresponding DiffClass
   *  2. generate inherited associations and put them into DiffAssociationGroup
   */
  private void solveInheritance() {
    List<List<String>> waitList = new ArrayList<>();
    DifferentHelper differentHelper = new DifferentHelper();
    getAllBottomDiffClassNode(inheritanceGraph).forEach(diffClassName ->
      waitList.addAll(differentHelper.getAllInheritancePath4DiffClass(diffClassGroup.get(diffClassName), inheritanceGraph)));
    waitList.forEach(path -> {
      if (path.size() > 1) {

        for (int i = 0; i < path.size() - 1; i++) {
          DiffClass parent = diffClassGroup.get(path.get(i));
          DiffClass child = diffClassGroup.get(path.get(i + 1));

          // for attributes
          parent.getEditedElement().getCDAttributeList().forEach(e -> {
            String type = "DiffEnum_" + e.printType();
            // update enumClassMap
            if (enumClassMap.containsKey(type)) {
              Set<String> set = enumClassMap.get(type);
              set.add(child.getName());
              enumClassMap.put(type, set);
            }
            // add inherited attribute into child diffClass
            child.addAttribute(e);

          });

          // update all DiffEnum
          updateDiffEnum();

          // for association
          String parentOriginalName = parent.getOriginalClassName();
          String childOriginalName = child.getOriginalClassName();
          Map<String, DiffAssociation> associationMap = fuzzySearchDiffAssociationByClassName(diffAssociationGroup, parentOriginalName);

          associationMap.forEach((oldName, oldDiffAssociation) -> {

            String prefix = oldName.split("_")[0];
            String leftClass = oldDiffAssociation.getDiffLeftClass().getOriginalClassName();
            String leftRoleName = oldDiffAssociation.getDiffLeftClassRoleName();
            String direction = formatDirection(oldDiffAssociation.getDiffDirection());
            String rightRoleName = oldDiffAssociation.getDiffRightClassRoleName();
            String rightClass = oldDiffAssociation.getDiffRightClass().getOriginalClassName();
            leftClass = leftClass.equals(parentOriginalName) ? childOriginalName : leftClass;
            rightClass = rightClass.equals(parentOriginalName) ? childOriginalName : rightClass;
            String newName = prefix + "_" + leftClass + "_" + leftRoleName + "_" + direction + "_" + rightRoleName + "_" + rightClass;
            if (!diffAssociationGroup.containsKey(newName)) {
              ASTCDAssociation oldASTAssoc = oldDiffAssociation.getOriginalElement();
              ASTCDAssociation newASTAssoc = oldASTAssoc.deepClone();
              DiffClass leftDiffClass = oldDiffAssociation.getDiffLeftClass();
              DiffClass rightDiffClass = oldDiffAssociation.getDiffRightClass();
              if (oldDiffAssociation.getDiffLeftClass().getOriginalClassName().contains(parentOriginalName)) {
                newASTAssoc = editASTCDAssociationLeftSideByDiffClass(newASTAssoc, child);
                leftDiffClass = child;
              }
              if (oldDiffAssociation.getDiffRightClass().getOriginalClassName().contains(parentOriginalName)) {
                newASTAssoc = editASTCDAssociationRightSideByDiffClass(newASTAssoc, child);
                rightDiffClass = child;
              }
              DiffAssociation newDiffAssociation = new DiffAssociation(newASTAssoc, true);
              newDiffAssociation.setDiffLeftClass(leftDiffClass);
              newDiffAssociation.setDiffRightClass(rightDiffClass);
              diffAssociationGroup.put(newDiffAssociation.getName(), newDiffAssociation);
            }
          });
        }
      }
    });
  }

  /**
   * After solving inheritance problem update the Enum DiffClass in DiffClassGroup
   */
  private void updateDiffEnum() {
    enumClassMap.forEach((k, v) -> {
      DiffClass diffEnum = diffClassGroup.get(k);
      diffEnum.setDiffLink4EnumClass(v);
    });
  }
}
