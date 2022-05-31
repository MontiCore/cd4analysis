package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
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

  public DifferentGroup generateDifferentGroup(ASTCDCompilationUnit cd, DifferentGroup.DifferentGroupType type) {
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
    differentGroup.setRefSetAssociationList(createDiffRefSetAssociation(diffAssociationGroup));
    return differentGroup;
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  public Map<String, DiffClass> createDiffClassForSimpleClassAndAbstractClass(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDClass> astcdClassList = cd.getCDDefinition().getCDClassesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdClassList) {
      DiffClass diffClass = createDiffClassHelper(astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
    return diffClassGroup;
  }

  public Map<String, DiffClass> createDiffClassForInterface(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDInterface> astcdInterfaceList = cd.getCDDefinition().getCDInterfacesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdInterfaceList) {
      DiffClass diffClass = createDiffClassHelper(astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
    return diffClassGroup;
  }

  public Map<String, DiffClass> createDiffClassForEnum(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdEnumList) {
      DiffClass diffClass = createDiffClassHelper(astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
    return diffClassGroup;
  }

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
          diffClass.addAttribute(astcdAttribute, true, false);
          creatEnumClassMapHelper("DiffEnum_" + astcdAttribute.printType(), diffClass.getName());
        }
        else {
          diffClass.addAttribute(astcdAttribute, false, false);
        }
      }
    }
    else {
      // add diffLink4EnumClass
      diffClass.setDiffLink4EnumClass(enumClassMap.get(diffClass.getName()));
    }

    return diffClass;
  }

  public Map<String, Set<String>> creatEnumClassMapHelper(String enumClass, String baseClass) {
    Set<String> set = enumClassMap.getOrDefault(enumClass, new HashSet<>());
    set.add(baseClass);
    enumClassMap.put(enumClass, set);
    return enumClassMap;
  }

  public MutableGraph<String> createInheritanceGraph(ASTCDType child, Collection<ASTCDType> directSuperList) {
    String childClass = getDiffClassKindStrHelper(distinguishASTCDTypeHelper(child)) + "_" + child.getName();
    inheritanceGraph.addNode(childClass);
    directSuperList.forEach(parent -> {
      String parentClass = getDiffClassKindStrHelper(distinguishASTCDTypeHelper(parent)) + "_" + parent.getName();
      inheritanceGraph.putEdge(childClass, parentClass);
    });
    return inheritanceGraph;
  }

  public DiffClass findDiffClass4OriginalClassName(String originalClassName) {
    if (diffClassGroup.containsKey("DiffClass_" + originalClassName)) {
      return diffClassGroup.get("DiffClass_" + originalClassName);
    }
    else if (diffClassGroup.containsKey("DiffAbstractClass_" + originalClassName)) {
      return diffClassGroup.get("DiffAbstractClass_" + originalClassName);
    }
    else if (diffClassGroup.containsKey("DiffInterface_" + originalClassName)) {
      return diffClassGroup.get("DiffInterface_" + originalClassName);
    }
    else {
      return diffClassGroup.get("DiffEnum_" + originalClassName);
    }
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  public Map<String, DiffAssociation> createDiffAssociation(ASTCDCompilationUnit cd) {
    List<ASTCDAssociation> astcdAssociationList = cd.getCDDefinition().getCDAssociationsList();
    for (ASTCDAssociation astcdAssociation : astcdAssociationList) {
      DiffAssociation diffAssociation = createDiffAssociationHelper(astcdAssociation, false);
      diffAssociationGroup.put(diffAssociation.getName(), diffAssociation);
    }
    return diffAssociationGroup;
  }

  public DiffAssociation createDiffAssociationHelper(ASTCDAssociation astcdAssociation, Boolean isInherited) {
    // add role name if the original ASTCDAssociation has no role name for one side or both side
    astcdAssociation = generateASTCDAssociationRoleName(astcdAssociation);
    DiffAssociation diffAssociation = new DiffAssociation(astcdAssociation, isInherited, false);
    diffAssociation.setDiffLeftClass(findDiffClass4OriginalClassName(diffAssociation.getLeftOriginalClassName()));
    diffAssociation.setDiffRightClass(findDiffClass4OriginalClassName(diffAssociation.getRightOriginalClassName()));
    return diffAssociation;
  }

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  public List<List<String>> getAllInheritancePath4DiffClass(DiffClass diffClass) {
    String root = diffClass.getName();
    List<List<String>> pathList = new ArrayList<>();
    getAllInheritancePath4DiffClassHelper(root, new LinkedList<>(), pathList);
    return pathList;
  }

  private void getAllInheritancePath4DiffClassHelper(String root, LinkedList<String> path, List<List<String>> pathList) {
    if (inheritanceGraph.successors(root).isEmpty()) {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      pathList.add(newPath);
      return;
    }
    else {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      Iterator iterator = inheritanceGraph.successors(root).iterator();
      while (iterator.hasNext()) {
        String parentNode = iterator.next().toString();
        getAllInheritancePath4DiffClassHelper(parentNode, newPath, pathList);
      }
    }
  }

  public Set<String> getAllBottomNode() {
    Set<String> result = new HashSet<>();
    inheritanceGraph.nodes().forEach(s -> {
      if (inheritanceGraph.predecessors(s).isEmpty()) {
        result.add(s);
      }
    });
    return result;
  }

  public void solveInheritance() {
    List<List<String>> waitList = new ArrayList<>();
    getAllBottomNode().forEach(diffClassName -> waitList.addAll(getAllInheritancePath4DiffClass(diffClassGroup.get(diffClassName))));
    waitList.forEach(path -> {
      if (path.size() > 1) {

        for (int i = 0; i < path.size() - 1; i++) {
          DiffClass parent = diffClassGroup.get(path.get(i));
          DiffClass child = diffClassGroup.get(path.get(i + 1));

          // for attributes
          parent.getEditedElement().getCDAttributeList().forEach(e -> {
            String type = parent.getAttributeByASTCDAttribute(e).get("type");

            // update enumClassMap
            boolean isEnumType = false;
            if (enumClassMap.containsKey(type)) {
              isEnumType = true;
              Set<String> set = enumClassMap.get(type);
              set.add(child.getName());
              enumClassMap.put(type, set);
            }
            // add inherited attribute into child diffClass
            child.addAttribute(e, isEnumType, true);

          });

          // update all DiffEnum
          updateDiffEnum();

          // for association
          String parentOriginalName = parent.getOriginalClassName();
          String childOriginalName = child.getOriginalClassName();
          Map<String, DiffAssociation> associationMap = parseMapForFilter(diffAssociationGroup, parentOriginalName);

          associationMap.forEach((oldName, oldDiffAssociation) -> {

            String prefix = oldName.split("_")[0];
            String leftClass = oldDiffAssociation.getDiffLeftClass().getOriginalClassName();
            String leftRoleName = oldDiffAssociation.getDiffLeftClassRoleName();
            String rightRoleName = oldDiffAssociation.getDiffRightClassRoleName();
            String rightClass = oldDiffAssociation.getDiffRightClass().getOriginalClassName();
            leftClass = leftClass.equals(parentOriginalName) ? childOriginalName : leftClass;
            rightClass = rightClass.equals(parentOriginalName) ? childOriginalName : rightClass;
            String newName = prefix + "_" + leftClass + "_" + leftRoleName + "_" + rightRoleName + "_" + rightClass;
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
              DiffAssociation newDiffAssociation = new DiffAssociation(newASTAssoc, true, false);
              newDiffAssociation.setDiffLeftClass(leftDiffClass);
              newDiffAssociation.setDiffRightClass(rightDiffClass);
              diffAssociationGroup.put(newDiffAssociation.getName(), newDiffAssociation);
            }
          });
        }
      }
    });
  }

  private void updateDiffEnum() {
    enumClassMap.forEach((k, v) -> {
      DiffClass diffEnum = diffClassGroup.get(k);
      diffEnum.setDiffLink4EnumClass(v);
    });
  }
}
