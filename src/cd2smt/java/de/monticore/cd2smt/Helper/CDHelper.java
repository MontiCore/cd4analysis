/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.Helper;

import static de.monticore.cd2smt.Helper.CDHelper.ObjType.ABSTRACT_OBJ;
import static de.monticore.cd2smt.Helper.CDHelper.ObjType.NORMAL_OBJ;

import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd2smt.Helper.visitor.RemoveAssocCardinality;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumTOP;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CDHelper {
  public static final Map<String, ASTMCType> javaTypeMap = buildJavaTypeMap();

  public static List<ASTCDType> getSubclassList(ASTCDDefinition cd, ASTCDType astcdType) {
    List<ASTCDType> subclasses = new LinkedList<>();
    for (ASTCDType entry : cd.getCDClassesList()) {
      for (ASTMCObjectType entry2 : entry.getSuperclassList()) {
        if (entry2.printType().equals(astcdType.getName())) subclasses.add(entry);
      }
      for (ASTMCObjectType entry2 : entry.getInterfaceList()) {
        if (entry2.printType().equals(astcdType.getName())) subclasses.add(entry);
      }
    }
    return subclasses;
  }

  public static List<ASTCDType> getSubTypeList(ASTCDDefinition cd, ASTCDType astcdType) {
    List<ASTCDType> subclasses = new LinkedList<>();
    for (ASTCDType entry : CDHelper.getASTCDTypes(cd)) {
      for (ASTMCObjectType entry2 : entry.getSuperclassList()) {
        if (entry2.printType().equals(astcdType.getName())) subclasses.add(entry);
      }
      for (ASTMCObjectType entry2 : entry.getInterfaceList()) {
        if (entry2.printType().equals(astcdType.getName())) subclasses.add(entry);
      }
    }
    return subclasses;
  }

  private static void getSubTypeAllDeepHelper(
      ASTCDType astcdType, ASTCDDefinition cd, Set<ASTCDType> res) {
    if (CDHelper.getSubTypeList(cd, astcdType).isEmpty()) {
      return;
    }
    List<ASTCDType> subTypeList = getSubTypeList(cd, astcdType);
    res.add(astcdType);
    for (ASTCDType astcType1 : subTypeList) {
      res.add(astcType1);
      getSubTypeAllDeepHelper(astcType1, cd, res);
    }
  }

  public static Set<ASTCDType> getSubTypeAllDeep(ASTCDType astcdType, ASTCDCompilationUnit ast) {
    Set<ASTCDType> res = new HashSet<>();
    getSubTypeAllDeepHelper(astcdType, ast.getCDDefinition(), res);
    res.remove(astcdType);
    return res;
  }

  public static List<ASTCDType> getSubInterfaceList(
      ASTCDDefinition cd, ASTCDInterface astcdInterface) {
    List<ASTCDType> subInterfaces = new LinkedList<>();
    for (ASTCDInterface entry : cd.getCDInterfacesList()) {
      for (ASTMCObjectType entry2 : entry.getInterfaceList()) {
        if (entry2.printType().equals(astcdInterface.getName())) subInterfaces.add(entry);
      }
    }
    return subInterfaces;
  }

  public static ASTCDType getASTCDType(String className, ASTCDDefinition cd) {
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className)) {
        return myClass;
      }
    }
    for (ASTCDInterface astcdInterface : cd.getCDInterfacesList()) {
      if (astcdInterface.getName().equals(className)) {
        return astcdInterface;
      }
    }

    for (ASTCDEnum astcdEnum : cd.getCDEnumsList()) {
      if (astcdEnum.getName().equals(className)) {
        return astcdEnum;
      }
    }
    Log.error(" class " + className + " not found in classdiagram " + cd.getName());
    return null;
  }

  public static ASTCDClass getClass(String className, ASTCDDefinition cd) {
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className)) {
        return myClass;
      }
    }
    Log.error(" class " + className + " not found in classdiagram " + cd.getName());
    return null;
  }

  public static ASTCDInterface getInterface(String className, ASTCDDefinition cd) {
    for (ASTCDInterface astcdInterface : cd.getCDInterfacesList()) {
      if (astcdInterface.getName().equals(className)) {
        return astcdInterface;
      }
    }
    Log.error(" Interface " + className + " not found in classdiagram " + cd.getName());
    return null;
  }

  public static ASTCDEnum getEnum(String enumName, ASTCDDefinition cd) {
    for (ASTCDEnum astcdEnum : cd.getCDEnumsList()) {
      if (astcdEnum.getName().equals(enumName)) {
        return astcdEnum;
      }
    }
    Log.error(" Enumeration " + enumName + " not found in classdiagram " + cd.getName());
    return null;
  }

  public static void createCDSymTab(ASTCDCompilationUnit ast) {
    CD4AnalysisMill.scopesGenitorDelegator().createFromAST(ast);
    BuiltInTypes.addBuiltInTypes(CD4AnalysisMill.globalScope());
    CD4AnalysisSymbolTableCompleter c =
        new CD4AnalysisSymbolTableCompleter(
            ast.getMCImportStatementList(), MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());
  }

  public static Sort mcType2Sort(Context ctx, ASTMCType astmcType) {
    String att = astmcType.printType();
    Sort res = null;
    switch (att) {
      case "boolean":
        res = ctx.mkBoolSort();
        break;
      case "int":
        res = ctx.mkIntSort();
        break;
      case "double":
        res = ctx.mkRealSort();
        break;
      case "java.lang.String":
      case "String":
        res = ctx.mkStringSort();
        break;
      default:
        Log.error("the type " + att + " is not supported for Attributes");
    }
    return res;
  }

  public static boolean isPrimitiveType(ASTMCType type) {
    return Set.of("int", "double", "Double", "Integer", "boolean", "Boolean", "String")
        .contains(type.printType());
  }

  public static boolean isDateType(ASTMCType type) {
    return type.printType().equals("Date");
  }

  public static boolean isEnumType(ASTCDDefinition ast, String type) {

    return ast.getCDEnumsList().stream()
        .map(ASTCDEnumTOP::getName)
        .collect(Collectors.toSet())
        .contains(type);
  }

  public static ASTMCType sort2MCType(Sort mySort) {
    if (javaTypeMap.containsKey(mySort.toString())) {
      return javaTypeMap.get(mySort.toString());
    }
    return OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(mySort.toString()))
        .build();
  }

  protected static Map<String, ASTMCType> buildJavaTypeMap() {
    return Map.of(
        "Int",
        OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(6).build(),
        "Real",
        OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(4).build(),
        "Bool",
        OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(1).build(),
        "String",
        OD4ReportMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("String"))
            .build());
  }

  public static ASTCDAssociation getAssociation(
      ASTCDType objType, String otherRole, ASTCDDefinition cd) {
    Set<ASTCDType> objTypes = getSuperTypeAllDeep(objType, cd);
    objTypes.add(objType);
    ASTCDType leftType;
    ASTCDType rightType;
    String leftRole;
    String rightRole;

    for (ASTCDAssociation association : cd.getCDAssociationsList()) {
      leftType = CDHelper.getASTCDType(association.getLeftQualifiedName().getQName(), cd);
      rightType = CDHelper.getASTCDType(association.getRightQualifiedName().getQName(), cd);
      leftRole = association.getLeft().getCDRole().getName();
      rightRole = association.getRight().getCDRole().getName();

      if (objTypes.contains(leftType) && otherRole.equals(rightRole)
          || objTypes.contains(rightType) && otherRole.equals(leftRole)) {
        return association;
      }
    }
    return null;
  }

  public static ASTCDAttribute getAttribute(ASTCDType astcdType, String attrName) {
    Optional<ASTCDAttribute> attr =
        astcdType.getCDAttributeList().stream().filter(a -> a.getName().equals(attrName)).findAny();
    if (attr.isEmpty()) {
      Log.error("attribute " + attrName + " not found in class " + astcdType.getName());
    }
    assert attr.isPresent();
    return attr.get();
  }

  public static boolean containsProperAttribute(ASTCDType astcdType, String attributeName) {
    for (ASTCDAttribute attribute1 : astcdType.getCDAttributeList()) {
      if (attribute1.getName().equals(attributeName)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsAttribute(
      ASTCDType astCdType, String attributeName, ASTCDDefinition cd) {
    if (CDHelper.containsProperAttribute(astCdType, attributeName)) {
      return true;
    }
    List<ASTCDType> superclassList = CDHelper.getSuperTypeList(astCdType, cd);

    for (ASTCDType superType : superclassList) {
      boolean res = containsAttribute(superType, attributeName, cd);

      if (res) {
        return true;
      }
    }

    return false;
  }

  public static List<ASTCDType> getSuperTypeList(ASTCDType astcdType, ASTCDDefinition cd) {
    List<ASTCDType> res =
        astcdType.getSuperclassList().stream()
            .map(mcType -> getASTCDType(mcType.printType(), cd))
            .collect(Collectors.toList());

    res.addAll(
        astcdType.getInterfaceList().stream()
            .map(mcType -> getASTCDType(mcType.printType(), cd))
            .collect(Collectors.toList()));

    return res;
  }

  private static void getSuperTypeAllDeepHelper(
      ASTCDType astcdType, ASTCDDefinition cd, Set<ASTCDType> res) {
    if (astcdType.getInterfaceList().isEmpty() && astcdType.getSuperclassList().isEmpty()) {
      return;
    }
    List<ASTCDType> superClassList = getSuperTypeList(astcdType, cd);
    res.add(astcdType);
    for (ASTCDType astcType1 : superClassList) {
      res.add(astcType1);
      getSuperTypeAllDeepHelper(astcType1, cd, res);
    }
  }

  public static Set<ASTCDType> getSuperTypeAllDeep(ASTCDType astcdType, ASTCDDefinition cd) {
    Set<ASTCDType> res = new HashSet<>();
    getSuperTypeAllDeepHelper(astcdType, cd, res);
    res.remove(astcdType);
    return res;
  }

  public static List<ASTCDType> getASTCDTypes(ASTCDDefinition ast) {
    List<ASTCDType> res = new ArrayList<>(ast.getCDClassesList());
    res.addAll(ast.getCDInterfacesList());
    return res;
  }

  public static void removeAssocCard(ASTCDCompilationUnit ast) {
    // transformations that need an already created symbol table
    createCDSymTab(ast);
    final CDAssociationVisitor2 visitor2 = new RemoveAssocCardinality();
    final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
    traverser.add4CDAssociation(visitor2);
    ast.accept(traverser);
  }

  public static ASTCDType getLeftType(ASTCDAssociation association, ASTCDDefinition cd) {
    return getASTCDType(association.getLeftQualifiedName().getQName(), cd);
  }

  public static ASTCDType getRightType(ASTCDAssociation association, ASTCDDefinition cd) {
    return getASTCDType(association.getRightQualifiedName().getQName(), cd);
  }

  public static boolean isCardinalityOne2One(ASTCDAssociation association) {
    return (association.getLeft().isPresentCDCardinality()
        && association.getLeft().getCDCardinality().isOne()
        && association.getRight().isPresentCDCardinality()
        && association.getRight().getCDCardinality().isOne());
  }

  public static List<ASTCDClass> getAbstractClassList(ASTCDDefinition cd) {
    return cd.getCDClassesList().stream()
        .filter(x -> x.getModifier().isAbstract())
        .collect(Collectors.toList());
  }

  public static String buildDate(int time) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2023, Calendar.JANUARY, 1, 0, 0, 0);
    calendar.add(Calendar.SECOND, time);

    LocalDateTime times =
        LocalDateTime.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND));
    return times.format(DateTimeFormatter.ISO_DATE)
        + "|"
        + times.format(DateTimeFormatter.ISO_LOCAL_TIME);
  }

  public static List<ASTCDType> getClassHierarchy(
      ASTCDType superType, ASTCDType subType, ASTCDDefinition cd) {
    List<ASTCDType> classList = new ArrayList<>();
    classList.add(subType);
    for (ASTCDType currentSuper : getSuperTypeList(subType, cd)) {
      if (currentSuper.getName().equals(superType.getName())) {
        classList.add(superType);
        return classList;
      } else {
        if (CDHelper.getSuperTypeList(currentSuper, cd).contains(superType)) {
          classList.addAll(getClassHierarchy(superType, subType, cd));
          return classList;
        }
      }
    }
    classList.add(superType);
    return classList;
  }

  public enum ObjType {
    ABSTRACT_OBJ,
    NORMAL_OBJ
  }

  public static ObjType mkType(ASTCDType astcdType) {
    ObjType res;
    if (astcdType instanceof ASTCDClass && !astcdType.getModifier().isAbstract()) {
      return NORMAL_OBJ;
    }
    return ABSTRACT_OBJ;
  }
}
