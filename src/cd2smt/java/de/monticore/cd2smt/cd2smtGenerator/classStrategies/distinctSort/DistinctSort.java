package de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class DistinctSort implements ClassStrategy {
  Map<ASTCDType, SMTType> smtTypesMap;

  public DistinctSort() {
    smtTypesMap = new HashMap<>();
  }

  @Override
  public Sort getSort(ASTCDType astcdType) {
    return smtTypesMap.get(astcdType).getSort();
  }

  @Override
  public Expr<? extends  Sort> getSortFilter(ASTCDType astcdType) {
    Log.error("function getSortFilter() not implemented for the class-strategy DistinctSort");
    return null;
  } //TODO:implement and introduce it

  @Override
  public Expr<? extends Sort> getAttribute(ASTCDType astCdType, ASTCDAttribute astCdAttribute, Expr<? extends Sort> cDTypeExpr) {
    return smtTypesMap.get(astCdType).getAttribute(astCdAttribute).apply(cDTypeExpr);
  }


  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    ast.getCDDefinition().getCDClassesList().forEach(Class -> declareCDType(Class, context, false));
    ast.getCDDefinition().getCDInterfacesList().forEach(Interface -> declareCDType(Interface, context, true));
  }

  private void declareCDType(ASTCDType astcdType, Context ctx, boolean isInterface) {
    SMTType smtType = new SMTType(isInterface);

    //(declare-sort A_obj 0)
    UninterpretedSort typeSort = ctx.mkUninterpretedSort(ctx.mkSymbol(SMTNameHelper.printSMTCDTypeName(astcdType)));
    smtType.setSort(typeSort);

    //(declare-fun a_attrib_something (A_obj) String) declare all attributes
    for (ASTCDAttribute myAttribute : astcdType.getCDAttributeList()) {
      Sort sort = CDHelper.parseAttribType2SMT(ctx, myAttribute);
      FuncDecl<Sort> attributeFunc = ctx.mkFuncDecl(SMTNameHelper.printAttributeNameSMT(astcdType, myAttribute), typeSort, sort);
      smtType.addAttribute(myAttribute, attributeFunc);
    }

    smtTypesMap.put(astcdType, smtType);
  }

  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    //interfaces , abstract and superInstance must be deleted
    for (Sort mySort : model.getSorts()) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(mySort)) {
        SMTType smtType = getSMTType(SMTNameHelper.sort2CDTypeName(mySort)).orElse(null);
        assert smtType != null;
        boolean isAbstract = smtType.isInterface();
        MinObject obj = new MinObject(isAbstract, smtExpr, smtType);

        for (Map.Entry<ASTCDAttribute, FuncDecl<? extends Sort>> attribute : smtType.getAttributesMap().entrySet()) {
          Expr<? extends Sort> attrExpr = model.eval(attribute.getValue().apply(smtExpr), !partial);
          if (attrExpr.getNumArgs() == 0) {
            obj.addAttribute(attribute.getKey(), attrExpr);
          }
        }
        objectSet.add(obj);
      }
    }
    return objectSet;
  }

  private Optional<SMTType> getSMTType(String className) {
    for (Map.Entry<ASTCDType, SMTType> entry : smtTypesMap.entrySet()) {
      if (entry.getKey().getName().equals(className)) {
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }
}


