package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;

import java.util.*;
import java.util.stream.Collectors;

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
    DiffClass diffClass = new DiffClass();
    diffClass.setOriginalElement(astcdType);
    diffClass.setDiffKind(distinguishASTCDTypeHelper(astcdType));
    diffClass.setName(getDiffClassKindStrHelper(diffClass.getDiffKind()) + "_" + astcdType.getName());
    diffClass.setDiffClassName(Sets.newHashSet(astcdType.getName()));

    if (!astcdType.getClass().equals(ASTCDEnum.class)) {
      // add diffParents
      List<ASTCDType> superList = CDInheritanceHelper.getAllSuper(astcdType, scope).stream().distinct().collect(Collectors.toList());
      superList.remove(astcdType);
      List<String> parentsList = new ArrayList<>();
      superList.forEach(superClass -> parentsList.add(getDiffClassKindStrHelper(distinguishASTCDTypeHelper(superClass)) + "_" + superClass.getName()));

      // create InheritanceGraph
      List<ASTCDType> directSuperList = CDInheritanceHelper.getDirectSuperClasses(astcdType, scope);
      directSuperList.addAll(CDInheritanceHelper.getDirectInterfaces(astcdType, scope));
      directSuperList = directSuperList.stream().distinct().collect(Collectors.toList());
      createInheritanceGraph(astcdType, directSuperList);
      diffClass.setDiffParents(parentsList);

      // add attributes
      Map<String, Map<String, String>> attributesMap = new HashMap<>();
      for (ASTCDAttribute astcdAttribute : astcdType.getCDAttributeList()) {
        Map<String, String> itemMap = new HashMap<>();
        if (astcdEnumList.stream().anyMatch(s -> s.getName().equals(astcdAttribute.printType()))) {
          itemMap.put("type", "DiffEnum_" + astcdAttribute.printType());
          itemMap.put("kind", "original");
          creatEnumClassMapHelper(itemMap.get("type"), diffClass.getName());
        }
        else {
          itemMap.put("type", astcdAttribute.printType());
          itemMap.put("kind", "original");
        }
        attributesMap.put(astcdAttribute.getName(), itemMap);
      }
      diffClass.setAttributes(attributesMap);
    }
    else {
      // add diffLink4EnumClass
      diffClass.setDiffLink4EnumClass(enumClassMap.get(diffClass.getName()));

      // add attributes
      Map<String, Map<String, String>> attributesMap = new HashMap<>();
      for (ASTCDEnumConstant astcdEnumConstant : ((ASTCDEnum) astcdType).getCDEnumConstantList()) {
        attributesMap.put(astcdEnumConstant.getName(), null);
      }
      diffClass.setAttributes(attributesMap);
    }

    return diffClass;
  }

  public Map<String, Set<String>> creatEnumClassMapHelper(String enumClass, String baseClass) {
    Set<String> set = enumClassMap.getOrDefault(enumClass, new HashSet<>());
    set.add(baseClass);
    enumClassMap.put(enumClass, set);
    return enumClassMap;
  }

  public MutableGraph<String> createInheritanceGraph(ASTCDType child, List<ASTCDType> directSuperList) {
    String childClass = getDiffClassKindStrHelper(distinguishASTCDTypeHelper(child)) + "_" + child.getName();
    inheritanceGraph.addNode(childClass);
    directSuperList.forEach(parent -> {
      String parentClass = getDiffClassKindStrHelper(distinguishASTCDTypeHelper(parent)) + "_" + parent.getName();
      inheritanceGraph.putEdge(childClass, parentClass);
    });
    return inheritanceGraph;
  }

  public static DifferentGroup.DiffClassKind distinguishASTCDTypeHelper(ASTCDType astcdType) {
    if (astcdType.getClass().equals(ASTCDClass.class)) {
      if (astcdType.getModifier().isAbstract()) {
        return DifferentGroup.DiffClassKind.DIFF_ABSTRACT_CLASS;
      }
      else {
        return DifferentGroup.DiffClassKind.DIFF_CLASS;
      }
    }
    else if (astcdType.getClass().equals(ASTCDEnum.class)) {
      return DifferentGroup.DiffClassKind.DIFF_ENUM;
    }
    else {
      return DifferentGroup.DiffClassKind.DIFF_INTERFACE;
    }
  }

  public static String getDiffClassKindStrHelper(DifferentGroup.DiffClassKind diffClassKind) {
    switch (diffClassKind) {
      case DIFF_CLASS:
        return "DiffClass";
      case DIFF_ENUM:
        return "DiffEnum";
      case DIFF_ABSTRACT_CLASS:
        return "DiffAbstractClass";
      case DIFF_INTERFACE:
        return "DiffInterface";
      default:
        return null;
    }
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
    DiffAssociation diffAssociation = new DiffAssociation();
    String leftOriginalClassName = astcdAssociation.getLeftQualifiedName().getQName();
    String rightOriginalClassName = astcdAssociation.getRightQualifiedName().getQName();
    diffAssociation.setOriginalElement(astcdAssociation);
    diffAssociation.setDiffKind(isInherited ? DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC : DifferentGroup.DiffAssociationKind.DIFF_ASC);
    diffAssociation.setDiffDirection(distinguishAssociationDirectionHelper(astcdAssociation));
    diffAssociation.setDiffLeftClass(findDiffClass4OriginalClassName(leftOriginalClassName));
    diffAssociation.setDiffRightClass(findDiffClass4OriginalClassName(rightOriginalClassName));
    diffAssociation.setDiffLeftClassCardinality(distinguishLeftAssociationCardinalityHelper(astcdAssociation));
    diffAssociation.setDiffRightClassCardinality(distinguishRightAssociationCardinalityHelper(astcdAssociation));
    diffAssociation.setDiffLeftClassRoleName(getLeftClassRoleNameHelper(astcdAssociation));
    diffAssociation.setDiffRightClassRoleName(getRightClassRoleNameHelper(astcdAssociation));
    diffAssociation.setName("DiffAssociation_" + leftOriginalClassName + "_" + diffAssociation.getDiffLeftClassRoleName() + "_" + diffAssociation.getDiffRightClassRoleName() + "_" + rightOriginalClassName);
    return diffAssociation;
  }

  public static DifferentGroup.DiffAssociationDirection distinguishAssociationDirectionHelper(ASTCDAssociation astcdAssociation) {
    Boolean left = astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft();
    Boolean right = astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight();
    Boolean bidirectional = astcdAssociation.getCDAssocDir().isBidirectional();
    if (!left && right && !bidirectional) {
      return DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT;
    }
    else if (left && !right && !bidirectional) {
      return DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT;
    }
    else if (left && right && bidirectional) {
      return DifferentGroup.DiffAssociationDirection.BIDIRECTIONAL;
    }
    else {
      return DifferentGroup.DiffAssociationDirection.UNDEFINED;
    }
  }

  public static DifferentGroup.DiffAssociationCardinality distinguishLeftAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().getCDCardinality().isOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isOpt()) {
      return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isAtLeastOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
    }
    else {
      return DifferentGroup.DiffAssociationCardinality.MORE;
    }
  }

  public static DifferentGroup.DiffAssociationCardinality distinguishRightAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().getCDCardinality().isOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isOpt()) {
      return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isAtLeastOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
    }
    else {
      return DifferentGroup.DiffAssociationCardinality.MORE;
    }
  }

  public static String getLeftClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().isPresentCDRole()) {
      return astcdAssociation.getLeft().getCDRole().getName();
    }
    else {
      return astcdAssociation.getLeftQualifiedName().getQName().toLowerCase();
    }
  }

  public static String getRightClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().isPresentCDRole()) {
      return astcdAssociation.getRight().getCDRole().getName();
    }
    else {
      return astcdAssociation.getRightQualifiedName().getQName().toLowerCase();
    }
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
        boolean isReferenceSetCreated = false;
        for (int i = 0; i < path.size() - 1; i++) {
          DiffClass parent = diffClassGroup.get(path.get(i));
          DiffClass child = diffClassGroup.get(path.get(i + 1));

          // for attributes
          Map<String, Map<String, String>> parentAttributes = parent.getAttributes();
          parentAttributes.forEach((k, v) -> {
            Map<String, String> valueMap = new HashMap<>();
            valueMap.put("type", v.get("type"));
            valueMap.put("kind", "inherited");
            // update enumClassMap
            if(enumClassMap.containsKey(v.get("type"))){
              Set<String> set = enumClassMap.get(v.get("type"));
              set.add(child.getName());
              enumClassMap.put(v.get("type"),set);
            }

            Map<String, Map<String, String>> childAttributes = child.getAttributes();
            childAttributes.put(k, valueMap);
            child.setAttributes(childAttributes);
          });
          // update all DiffEnum
          updateDiffEnum();


          // for association
          String parentOriginalName = parent.getName().split("_")[1];
          String childOriginalName = child.getName().split("_")[1];
          Map<String, DiffAssociation> associationMap = parseMapForFilter(diffAssociationGroup, parentOriginalName);
          associationMap.forEach((oldName, oldDiffAssociation) -> {
            try {
              String prefix = oldName.split("_")[0];
              String leftClass = oldName.split("_")[1];
              String leftRoleName = oldName.split("_")[2];
              String rightRoleName = oldName.split("_")[3];
              String rightClass = oldName.split("_")[4];
              leftClass = leftClass.equals(parentOriginalName) ? childOriginalName : leftClass;
              rightClass = rightClass.equals(parentOriginalName) ? childOriginalName : rightClass;
              String newName = prefix + "_" + leftClass + "_" + leftRoleName + "_" + rightRoleName + "_" + rightClass;
              if (!diffAssociationGroup.containsKey(newName)) {
                DiffAssociation newDiffAssociation = oldDiffAssociation.clone();
                newDiffAssociation.setName(newName);
                newDiffAssociation.setDiffKind(DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC);
                if(newDiffAssociation.getDiffLeftClass().getName().split("_")[1].contains(parentOriginalName)) {
                  newDiffAssociation.setDiffLeftClass(child);
                }
                if (newDiffAssociation.getDiffRightClass().getName().split("_")[1].contains(parentOriginalName)) {
                  newDiffAssociation.setDiffRightClass(child);
                }
                diffAssociationGroup.put(newName,newDiffAssociation);
              }
            }
            catch (CloneNotSupportedException e) {
              throw new RuntimeException(e);
            }
          });
        }
      }
    });
  }

  public static Map<String, DiffAssociation> parseMapForFilter(Map<String, DiffAssociation> map,String filters) {
    if (map == null) {
      return null;
    } else {
      map = map.entrySet().stream()
        .filter((e) -> e.getKey().contains(filters))
        .collect(Collectors.toMap(
          (e) -> (String) e.getKey(),
          (e) -> e.getValue()
        ));
    }
    return map;
  }

  private void updateDiffEnum() {
    enumClassMap.forEach((k, v) -> {
      DiffClass diffEnum = diffClassGroup.get(k);
      diffEnum.setDiffLink4EnumClass(v);
    });
  }
}
