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

import java.util.Optional;


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

  public static Optional<Sort> parseAttribType2SMT(Context ctx, ASTCDAttribute myAttribute) {
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
        Log.warn("the type " +myAttribute.printType() + " is not supported for Attribute\n this Attribute will be skip");
        return  Optional.empty();
    }
    return Optional.of(res);
  }


}
