package de.monticore.cd2smt.Helper;

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
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

public class CDHelper {
  public static final Map<String, ASTMCType> javaTypeMap = buildJavaTypeMap();

  public static List<ASTCDClass> getSubclassList(ASTCDDefinition cd, ASTCDType astcdType) {
    List<ASTCDClass> subclasses = new LinkedList<>();
    for (ASTCDClass entry : cd.getCDClassesList()) {
      for (ASTMCObjectType entry2 : entry.getSuperclassList()) {
        if (entry2
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))
            .equals(astcdType.getName())) subclasses.add(entry);
      }
      for (ASTMCObjectType entry2 : entry.getInterfaceList()) {
        if (entry2
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))
            .equals(astcdType.getName())) subclasses.add(entry);
      }
    }
    return subclasses;
  }

  public static List<ASTCDInterface> getSubInterfaceList(
      ASTCDDefinition cd, ASTCDInterface astcdInterface) {
    List<ASTCDInterface> subInterfaces = new LinkedList<>();
    for (ASTCDInterface entry : cd.getCDInterfacesList()) {
      for (ASTMCObjectType entry2 : entry.getInterfaceList()) {
        if (entry2
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))
            .equals(astcdInterface.getName())) subInterfaces.add(entry);
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

  public static void createCDSymTab(ASTCDCompilationUnit ast) {
    CD4AnalysisMill.scopesGenitorDelegator().createFromAST(ast);
    BuiltInTypes.addBuiltInTypes(CD4AnalysisMill.globalScope());
    CD4AnalysisSymbolTableCompleter c =
        new CD4AnalysisSymbolTableCompleter(
            ast.getMCImportStatementList(), MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());
  }

  public static void setAssociationsRoles(ASTCDCompilationUnit ast) {
    // transformations that need an already created symbol table
    createCDSymTab(ast);
    final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo();
    final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
    traverser.add4CDAssociation(cdAssociationRoleNameTrafo);
    traverser.setCDAssociationHandler(cdAssociationRoleNameTrafo);
    cdAssociationRoleNameTrafo.transform(ast);
  }

  public static Sort parseAttribType2SMT(Context ctx, ASTCDAttribute myAttribute) {
    String att = myAttribute.printType();
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
        Log.error("the type " + myAttribute.printType() + " is not supported for Attributes");
    }
    return res;
  }

  public static ASTMCType sort2MCType(Sort mySort) {
    if (!javaTypeMap.containsKey(mySort.toString())) {
      Log.error("the type" + mySort + "is not supported for attributes");
    }
    return javaTypeMap.get(mySort.toString());
  }

  protected static Map<String, ASTMCType> buildJavaTypeMap() {
    Map<String, ASTMCType> typeMap = new HashMap<>();
    typeMap.put("Int", OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(6).build());
    typeMap.put("Real", OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(4).build());
    typeMap.put("Bool", OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(1).build());
    typeMap.put(
        "String",
        OD4ReportMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("String"))
            .build());
    return typeMap;
  }

  public static ASTCDAssociation getAssociation(
      ASTCDType objType, String otherRole, ASTCDDefinition cd) {
    List<ASTCDType> objTypes = new ArrayList<>();
    objTypes.add(objType);
    getAllSuperType(objType, cd, objTypes);
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
    Log.error(
        "Association with the other-role "
            + otherRole
            + " not found for the ASTCDType"
            + objType.getName());
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

  public static List<ASTCDType> getSuperTypeList(ASTCDType astcdType, ASTCDDefinition cd) {
    List<ASTCDType> res =
        astcdType.getSuperclassList().stream()
            .map(
                mcType ->
                    getASTCDType(
                        mcType.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())),
                        cd))
            .collect(Collectors.toList());

    res.addAll(
        astcdType.getInterfaceList().stream()
            .map(
                mcType ->
                    getASTCDType(
                        mcType.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())),
                        cd))
            .collect(Collectors.toList()));

    return res;
  }

  public static void getAllSuperType(ASTCDType astcdType, ASTCDDefinition cd, List<ASTCDType> res) {
    if (astcdType.getInterfaceList().isEmpty() && astcdType.getSuperclassList().isEmpty()) {
      return;
    }
    List<ASTCDType> superClassList = getSuperTypeList(astcdType, cd);
    res.add(superClassList.get(0));
    for (ASTCDType astcType1 : superClassList) {
      res.add(astcType1);
      getAllSuperType(astcType1, cd, res);
    }
  }

  public static void removeAssocCard(ASTCDCompilationUnit ast) {
    // transformations that need an already created symbol table
    createCDSymTab(ast);
    final CDAssociationVisitor2 visitor2 = new RemoveAssocCardinality();
    final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
    traverser.add4CDAssociation(visitor2);
    ast.accept(traverser);
  }
}
