package de.monticore.cd2smt.Helper;

import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class CDHelper {
 public static final Map<String, ASTMCType> javaTypeMap = buildJavaTypeMap();
  public static List<ASTCDClass> getSubclassList(ASTCDDefinition cd, ASTCDType astcdType) {
    List<ASTCDClass> subclasses = new LinkedList<>();
    for (ASTCDClass entry : cd.getCDClassesList()) {
      for (ASTMCObjectType entry2 : entry.getSuperclassList()) {
        if (entry2.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())).equals(astcdType.getName()))
          subclasses.add(entry);
      }
      for (ASTMCObjectType entry2 : entry.getInterfaceList()) {
        if (entry2.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())).equals(astcdType.getName()))
          subclasses.add(entry);
      }
    }
    return subclasses;
  }
  public static List<ASTCDInterface> getSubInterfaceList(ASTCDDefinition cd, ASTCDInterface astcdInterface) {
    List<ASTCDInterface> subInterfaces= new LinkedList<>();
    for (ASTCDInterface entry : cd.getCDInterfacesList()) {
      for (ASTMCObjectType entry2 : entry.getInterfaceList()) {
        if (entry2.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())).equals(astcdInterface.getName()))
          subInterfaces.add(entry);
      }
    }
    return subInterfaces;
  }
  public static ASTCDType getASTCDType(String className, ASTCDDefinition cd) {
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className)){
        return myClass;
      }
    }
    for (ASTCDInterface astcdInterface: cd.getCDInterfacesList()) {
      if (astcdInterface.getName().equals(className)){
        return astcdInterface;
      }
    }
    Log.error(" class " + className + " not found in classdiagram " + cd.getName());
    return  null;
  }
  public static ASTCDClass getClass(String className, ASTCDDefinition cd) {
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className)){
        return myClass;
      }
    }
      Log.error(" class " + className + " not found in classdiagram " + cd.getName());
    return  null;
  }
  public static ASTCDInterface getInterface(String className, ASTCDDefinition cd) {
    for (ASTCDInterface astcdInterface: cd.getCDInterfacesList()) {
      if (astcdInterface.getName().equals(className)){
        return astcdInterface;
      }
    }
    Log.error(" Interface " + className + " not found in classdiagram " + cd.getName());
    return  null;
  }

  public static void createCDSymTab(ASTCDCompilationUnit ast) {
    CD4AnalysisMill.scopesGenitorDelegator().createFromAST(ast);
    BuiltInTypes.addBuiltInTypes(CD4AnalysisMill.globalScope());
    CD4AnalysisSymbolTableCompleter c = new CD4AnalysisSymbolTableCompleter(
      ast.getMCImportStatementList(), MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());

  }

  public static void setAssociationsRoles(ASTCDCompilationUnit ast){
    // transformations that need an already created symbol table
     createCDSymTab(ast);
      final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo =
        new CDAssociationRoleNameTrafo();
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
    if (!javaTypeMap.containsKey(mySort.toString())){
      Log.error("the type" + mySort + "is not supported for attributes");
    }
    return javaTypeMap.get(mySort.toString());
  }


  protected static Map<String, ASTMCType> buildJavaTypeMap(){
    Map<String, ASTMCType> typeMap = new HashMap<>();
    typeMap.put("Int", OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(6).build());
    typeMap.put("Real", OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(4).build());
    typeMap.put("Bool",OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(1).build());
    typeMap.put("String",OD4ReportMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("String")).build());
    return typeMap;
  }
}
