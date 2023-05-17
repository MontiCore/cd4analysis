/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.SMTHelper;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class DistinctSort implements ClassStrategy {
  protected Map<ASTCDType, SMTType> smtTypesMap;
  protected Context ctx;
  protected ASTCDCompilationUnit ast;

  public DistinctSort() {
    smtTypesMap = new HashMap<>();
  }

  @Override
  public Sort getSort(ASTCDType astcdType) {
    return smtTypesMap.get(astcdType).getSort();
  }

  @Override
  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astCdType) {
    String sortName = expr.getSort().getName().toString();
    if (SMTHelper.printSMTCDTypeName(astCdType).equals(sortName)) {
      return ctx.mkTrue();
    }
    return ctx.mkFalse();
  }

  @Override
  public Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    return smtTypesMap
        .get(astCdType)
        .getAttribute(CDHelper.getAttribute(astCdType, attributeName))
        .apply(cDTypeExpr);
  }

  @Override
  public ASTCDCompilationUnit getClassDiagram() {
    return ast;
  }

  @Override
  public Set<IdentifiableBoolExpr> getClassConstraints() {
    return new HashSet<>();
  }

  @Override
  public Context getContext() {
    return ctx;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    this.ast = ast;
    ctx = context;
    ast.getCDDefinition().getCDEnumsList().forEach(this::declareEnum);
    ast.getCDDefinition().getCDClassesList().forEach(this::declareClass);
    ast.getCDDefinition().getCDInterfacesList().forEach(this::declareInterface);
  }

  // enum = uninterpreted sort   , value computed with a function declaration
  private void declareEnum(ASTCDEnum astcdEnum) {
    SMTType smtType = SMTType.mkEnum(astcdEnum);
    List<Constructor<Sort>> constructorList = new ArrayList<>();

    // create constant for the attributes values  and saves im smtType
    for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
      Constructor<Sort> constructor =
          ctx.mkConstructor(constant.getName(), constant.getName(), null, null, null);
      constructorList.add(constructor);
      smtType.addConstant(constant, constructor);
    }

    DatatypeSort<Sort> sort =
        ctx.mkDatatypeSort(astcdEnum.getName(), constructorList.toArray(new Constructor[0]));

    smtType.setSort(sort);

    smtTypesMap.put(astcdEnum, smtType);
  }

  private void declareInterface(ASTCDInterface astcdInterface) {
    SMTType smtType = SMTType.mkInterface(astcdInterface);
    declareCDType(astcdInterface, smtType);
  }

  private void declareClass(ASTCDClass astcdClass) {
    SMTType smtType;
    if (astcdClass.getModifier().isAbstract()) {
      smtType = SMTType.mkAbstractClass(astcdClass);
    } else {
      smtType = SMTType.mkClass(astcdClass);
    }
    declareCDType(astcdClass, smtType);
  }

  private void declareCDType(ASTCDType astcdType, SMTType smtType) {

    // (declare-sort A_obj 0)
    UninterpretedSort typeSort =
        ctx.mkUninterpretedSort(ctx.mkSymbol(SMTHelper.printSMTCDTypeName(astcdType)));
    smtType.setSort(typeSort);

    // (declare-fun a_attrib_something (A_obj) String) declare all attributes
    for (ASTCDAttribute myAttribute : astcdType.getCDAttributeList()) {
      Sort sort;
      if (CDHelper.isPrimitiveType(myAttribute.getMCType())) {
        sort = CDHelper.mcType2Sort(ctx, myAttribute.getMCType());
      } else if (CDHelper.isEnumType(ast.getCDDefinition(), myAttribute.getMCType().printType())) {
        sort =
            smtTypesMap
                .get(CDHelper.getEnum(myAttribute.getMCType().printType(), ast.getCDDefinition()))
                .getSort();
      } else if (CDHelper.isDateType(myAttribute.getMCType())) {
        sort = ctx.mkIntSort();
      } else {
        Log.info(
            "conversion of  Attribute "
                + myAttribute.getName()
                + " of the ASTCDType  "
                + astcdType.getName()
                + " skipped because his type  "
                + myAttribute.getMCType().printType()
                + " is not supported yet ",
            "Warning");
        return;
      }

      FuncDecl<Sort> attributeFunc =
          ctx.mkFuncDecl(SMTHelper.printAttributeNameSMT(astcdType, myAttribute), typeSort, sort);
      smtType.addAttribute(myAttribute, attributeFunc);
    }

    smtTypesMap.put(astcdType, smtType);
  }

  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    // interfaces , abstract and superInstance must be deleted
    for (Sort mySort : model.getSorts()) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(mySort)) {
        SMTType smtType = getSMTType(symbol2CDTypeName(mySort.getName())).orElse(null);
        assert smtType != null;

        MinObject obj =
            new MinObject(CDHelper.mkType(smtType.getType()), smtExpr, smtType.getAstcdType());

        for (Map.Entry<ASTCDAttribute, FuncDecl<? extends Sort>> attribute :
            smtType.getAttributesMap().entrySet()) {
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

  private String symbol2CDTypeName(Symbol symbol) {
    int length = symbol.toString().length();
    StringBuilder stringBuilder = new StringBuilder(symbol.toString());
    stringBuilder.delete(length - 4, length); // remove the 4 last Characters
    return stringBuilder.toString();
  }
}
