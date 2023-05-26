package de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.SMTHelper;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.se_rwth.commons.logging.Log;
import java.util.*;
// TODO: Date to smt
// TODO: Enum 2 SMT
// TODO : abstract class 2smt

/**
 * This class creates a unique Sort "Object" as the Sort of all objects in SMT and a dataType
 * "CDType" as the indicator of the Type of the Object in the Class Diagram. A function "hasCDTYpe"
 * (Object => CDType => Bool) is used to define the type constraints(Uniqueness)
 *
 * <p>Example:
 *
 * <p>classdiagram SingleSort { class Car ; class Wheel ; }
 * =====================================SMT==================================================
 *
 * <p>(declare-sort Object 0)
 *
 * <p>(declare-datatypes ((CDType 0)) (((SS_Car) (SS_Wheel) (SS_NO_TYPE))))
 *
 * <p>(declare-fun hasCDType (Object CDType) Bool)
 *
 * <p>(assert (forall ((obj Object) (type1 CDType) (type2 CDType)) (and (or (hasCDType obj SS_Car)
 * (hasCDType obj SS_Wheel)) (=> (and (hasCDType obj type1) (hasCDType obj type2)) (= type1 type2)))
 * ))
 *
 * @author XXXX XXX
 * @version 1.0
 */
// @formatter:on
public class SingleSort implements ClassStrategy {
  protected Context ctx;
  protected DatatypeSort<? extends Sort> types;
  ASTCDCompilationUnit ast;
  private FuncDecl<BoolSort> hasTypeFunc;
  private Sort sort;

  protected Set<IdentifiableBoolExpr> classConstraints = new HashSet<>();

  protected Map<ASTCDType, Constructor<? extends Sort>> typeMap = new HashMap<>();
  protected Map<ASTCDAttribute, FuncDecl<? extends Sort>> attributeMap = new HashMap<>();
  protected Map<ASTCDEnum, SMTEnum> enumMap = new HashMap<>();

  @Override
  public Sort getSort(ASTCDType astCdType) {
    return sort;
  }

  @Override
  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astCdType) {
    return (BoolExpr)
        ctx.mkApp(hasTypeFunc, expr, ctx.mkConst(typeMap.get(astCdType).ConstructorDecl()));
  }

  @Override
  public Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    return attributeMap.get(CDHelper.getAttribute(astCdType, attributeName)).apply(cDTypeExpr);
  }

  @Override
  public ASTCDCompilationUnit getClassDiagram() {
    return ast;
  }

  @Override
  public Set<IdentifiableBoolExpr> getClassConstraints() {
    return classConstraints;
  }

  @Override
  public Context getContext() {
    return ctx;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    this.ast = ast;
    this.ctx = context;

    // declare the unique sort (declare-sort Object 0)
    this.sort = ctx.mkUninterpretedSort("Object");

    ast.getCDDefinition().getCDEnumsList().forEach(this::declareEnum);
    ast.getCDDefinition().getCDClassesList().forEach(this::declareCDType);
    ast.getCDDefinition().getCDInterfacesList().forEach(this::declareCDType);

    types = ctx.mkDatatypeSort("CDType", collectTypeConstructors());

    hasTypeFunc = ctx.mkFuncDecl("hasCDType", new Sort[] {sort, types}, ctx.mkBoolSort());

    classConstraints.add(makeTypeUnique());
  }

  private IdentifiableBoolExpr makeTypeUnique() {
    Expr<? extends Sort> obj = ctx.mkConst("obj", sort);

    // construct a list ( (hasType obj type1), (hasType obj type2) ....)
    List<BoolExpr> typesConst = new ArrayList<>();
    ast.getCDDefinition()
        .getCDClassesList()
        .forEach(astcdClass -> typesConst.add(hasType(obj, astcdClass)));
    ast.getCDDefinition()
        .getCDInterfacesList()
        .forEach(astcdInterface -> typesConst.add(hasType(obj, astcdInterface)));

    BoolExpr body = ctx.mkTrue();

    body = ctx.mkAnd(body, ctx.mkOr(typesConst.toArray(new BoolExpr[0])));

    Expr<? extends Sort> type1 = ctx.mkConst("type1", types);
    Expr<? extends Sort> type2 = ctx.mkConst("type2", types);
    body =
        ctx.mkAnd(
            body,
            ctx.mkImplies(
                ctx.mkAnd(ctx.mkApp(hasTypeFunc, obj, type1), ctx.mkApp(hasTypeFunc, obj, type2)),
                ctx.mkEq(type1, type2)));

    BoolExpr res = ctx.mkForall(new Expr[] {obj, type1, type2}, body, 0, null, null, null, null);
    return IdentifiableBoolExpr.buildIdentifiable(
        res, ast.get_SourcePositionStart(), Optional.of("UNIQUE-TYPE"));
  }

  /** this function declared a "CDType" constructor for each ASTCDType */
  private void declareCDType(ASTCDType astcdType) {

    Constructor<? extends Sort> typeConstructor =
        ctx.mkConstructor(
            "SS_" + astcdType.getName(), "SS_IS_" + astcdType.getName(), null, null, null);
    typeMap.put(astcdType, typeConstructor);

    // (declare-fun a_attrib_something (A_obj) String) declare all attributes
    for (ASTCDAttribute myAttribute : astcdType.getCDAttributeList()) {
      Sort attrSort;
      if (CDHelper.isPrimitiveType(myAttribute.getMCType())) {
        attrSort = CDHelper.mcType2Sort(ctx, myAttribute.getMCType());
      } else if (CDHelper.isEnumType(ast.getCDDefinition(), myAttribute.getMCType().printType())) {
        attrSort =
            enumMap
                .get(CDHelper.getEnum(myAttribute.getMCType().printType(), ast.getCDDefinition()))
                .getSort();
      } else if (CDHelper.isDateType(myAttribute.getMCType())) {
        attrSort = ctx.mkIntSort();
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
          ctx.mkFuncDecl(
              SMTHelper.printAttributeNameSMT(astcdType, myAttribute), this.sort, attrSort);
      attributeMap.put(myAttribute, attributeFunc);
    }
    typeMap.put(astcdType, typeConstructor);
  }

  private void declareEnum(ASTCDEnum astcdEnum) {
    SMTEnum smtEnum = new SMTEnum();
    List<Constructor<Sort>> constructorList = new ArrayList<>();

    // create constant for the attributes values  and saves im smtType
    for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
      Constructor<Sort> constructor =
          ctx.mkConstructor(constant.getName(), constant.getName(), null, null, null);
      constructorList.add(constructor);
      smtEnum.enumConstantMap.put(constant, constructor);
    }

    DatatypeSort<Sort> sort =
        ctx.mkDatatypeSort(astcdEnum.getName(), constructorList.toArray(new Constructor[0]));

    smtEnum.setSort(sort);

    enumMap.put(astcdEnum, smtEnum);
  }

  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    Sort[] sorts = model.getSorts();
    Sort Object =
        Arrays.stream(sorts).filter(x -> x.toString().equals("Object")).findAny().orElse(null);
    if (Object != null) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(Object)) {
        for (ASTCDType astcdType : CDHelper.getASTCDTypes(ast)) {

          if (hasType(smtExpr, astcdType, model)) {

            MinObject obj = new MinObject(CDHelper.mkType(astcdType), smtExpr, astcdType);

            for (ASTCDAttribute attr : astcdType.getCDAttributeList()) {
              Expr<? extends Sort> attrExpr =
                  model.eval(attributeMap.get(attr).apply(smtExpr), !partial);
              if (attrExpr.getNumArgs() == 0) {
                obj.addAttribute(attr, attrExpr);
              }
            }
            objectSet.add(obj);
          }
        }
      }
    }
    return objectSet;
  }

  /** collect the CDType constructor of each class and interface . */
  private Constructor<Sort>[] collectTypeConstructors() {
    Set<Constructor<? extends Sort>> constructors = new HashSet<>(typeMap.values());

    constructors.add(ctx.mkConstructor("SS_NO_TYPE", "SS_IS_" + "NO_TYPE", null, null, null));

    return (Constructor<Sort>[]) constructors.toArray(new Constructor[0]);
  }

  private boolean hasType(Expr<? extends Sort> expr, ASTCDType astcdType, Model model) {
    BoolExpr hasType = (BoolExpr) model.evaluate(hasType(expr, astcdType), true);
    return hasType.getBoolValue() == Z3_lbool.Z3_L_TRUE;
  }

  private static class SMTEnum {
    DatatypeSort<Sort> sort;
    Map<ASTCDEnumConstant, Constructor<? extends Sort>> enumConstantMap = new HashMap<>();

    public void setSort(DatatypeSort<Sort> sort) {
      this.sort = sort;
    }

    public DatatypeSort<Sort> getSort() {
      return sort;
    }
  }
}
