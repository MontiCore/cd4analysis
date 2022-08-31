package de.monticore.cd2smt.Helper;

import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;


public class CDHelper {

  public static ASTCDClass getClass(String className, ASTCDDefinition cd) {
    ASTCDClass res = null;
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className)){
        res = myClass;
      }
    }
    if (res == null){
      Log.error("class" + className + "not found in classdiagram" + cd.getName());
    }
    return  res;
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
    switch (att) {
      case "boolean":
        return ctx.mkBoolSort();
      case "int":
        return ctx.mkIntSort();
      case "double":
        return ctx.mkRealSort();
      case "java.lang.String":
        return ctx.mkStringSort();
      default:
        System.out.println("type not support \n interpret like a String");
        return ctx.mkStringSort();
    }
  }


}
