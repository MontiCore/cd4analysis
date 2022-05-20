package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffRelation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffSuperClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;

import java.util.*;
import java.util.stream.Collectors;

public class CD2DGGenerator {

  protected Map<String, DiffClass> diffClassGroup = new HashMap<>();
  protected Map<String, DiffRelation> diffRelationGroup = new HashMap<>();
  protected Map<String, DiffSuperClass> diffSuperClassGroup = new HashMap<>();
  protected MutableGraph<String> inheritanceGraph = GraphBuilder.directed().build();
  protected Map<String, Set<String>> enumClassMap = new HashMap<>();

  public DifferentGroup generateDifferentGroup(ASTCDCompilationUnit cd, DifferentGroup.DifferentGroupType type) {
    DifferentGroup differentGroup = new DifferentGroup();
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    createDiffClassForSimpleClassAndAbstractClass(cd, scope);
    createDiffClassForInterface(cd, scope);
    createDiffClassForEnum(cd, scope);

    differentGroup.setModel(cd);
    differentGroup.setType(type);
    differentGroup.setDiffClassGroup(diffClassGroup);
    differentGroup.setDiffRelationGroup(diffRelationGroup);
    differentGroup.setDiffSuperClassGroup(diffSuperClassGroup);
    return differentGroup;
  }

  public Map<String, DiffClass> createDiffClassForSimpleClassAndAbstractClass(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDClass> astcdClassList = cd.getCDDefinition().getCDClassesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdClassList) {
      DiffClass diffClass = new DiffClass();
      diffClass = createDiffClassHelper(diffClass, astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
    return diffClassGroup;
  }

  public Map<String, DiffClass> createDiffClassForInterface(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDInterface> astcdInterfaceList = cd.getCDDefinition().getCDInterfacesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdInterfaceList) {
      DiffClass diffClass = new DiffClass();
      diffClass = createDiffClassHelper(diffClass, astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
    return diffClassGroup;
  }

  public Map<String, DiffClass> createDiffClassForEnum(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdEnumList) {
      DiffClass diffClass = new DiffClass();
      diffClass = createDiffClassHelper(diffClass, astcdType, scope, astcdEnumList);
      diffClassGroup.put(diffClass.getName(), diffClass);
    }
    return diffClassGroup;
  }

  public DiffClass createDiffClassHelper(DiffClass diffClass, ASTCDType astcdType, ICD4CodeArtifactScope scope, List<ASTCDEnum> astcdEnumList) {
    diffClass.setOriginalElement(astcdType);
    diffClass.setDiffKind(distinguishASTCDTypeHelper(astcdType));
    diffClass.setName(getDiffClassKindStrHelper(diffClass.getDiffKind()) + "_" + astcdType.getName());
    diffClass.setDiffClassName(Sets.newHashSet(astcdType.getName()));

    if (!astcdType.getClass().equals(ASTCDEnum.class)) {
      // add diffParents
      List<ASTCDType> superList = CDInheritanceHelper.getAllSuper(astcdType, scope).stream().distinct().collect(Collectors.toList());
      superList.remove(astcdType);
      List<String> parentsList = new ArrayList<>();
      superList.forEach(superClass -> {
        parentsList.add(getDiffClassKindStrHelper(distinguishASTCDTypeHelper(superClass)) + "_" + superClass.getName());
        createDiffSuperClass(superClass, astcdType);
      });
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

  public Map<String, DiffSuperClass> createDiffSuperClass(ASTCDType parent, ASTCDType child) {
    DiffSuperClass diffSuperClass = new DiffSuperClass();
    diffSuperClass.setName("DiffSuperClass_" + parent.getName() + "_" + child.getName());
    diffSuperClass.setDiffKind(DifferentGroup.DiffRelationKind.DIFF_SUPERCLASS);
    diffSuperClass.setDiffParentClass(getDiffClassKindStrHelper(distinguishASTCDTypeHelper(parent)) + "_" + parent.getName());
    diffSuperClass.setDiffChildClass(getDiffClassKindStrHelper(distinguishASTCDTypeHelper(child)) + "_" + child.getName());
    diffSuperClassGroup.put(diffSuperClass.getName(), diffSuperClass);
    return diffSuperClassGroup;
  }

  public MutableGraph<String> createInheritanceGraph(String parentClass, String childClass) {
    inheritanceGraph.addNode(parentClass);
    inheritanceGraph.addNode(childClass);
    inheritanceGraph.putEdge(childClass, parentClass);
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
}
