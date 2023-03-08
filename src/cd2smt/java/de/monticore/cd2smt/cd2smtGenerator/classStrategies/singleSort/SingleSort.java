package de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort;

import static de.monticore.cd2smt.Helper.CDHelper.mcType2Sort;

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
import java.util.*;
import java.util.stream.Collectors;

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
  protected Set<IdentifiableBoolExpr> classConstraints = new HashSet<>();
  protected Map<ASTCDType, SMTType> smtTypesMap;
  protected Context ctx;
  protected DatatypeSort<? extends Sort> types;
  ASTCDCompilationUnit ast;
  private FuncDecl<BoolSort> hasTypeFunc;
  private Sort sort;

  public SingleSort() {
    smtTypesMap = new HashMap<>();
  }

  @Override
  public Sort getSort(ASTCDType astCdType) {
    return sort;
  }

  @Override
  public BoolExpr isInstanceOf(Expr<? extends Sort> expr, ASTCDType astCdType) {
    return (BoolExpr)
        ctx.mkApp(
            hasTypeFunc, expr, ctx.mkConst(smtTypesMap.get(astCdType).getType().ConstructorDecl()));
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
    return classConstraints;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    ctx = context;
    sort = ctx.mkUninterpretedSort("Object");
    this.ast = ast;

    ast.getCDDefinition().getCDClassesList().forEach(Class -> declareCDType(Class, context, false));
    ast.getCDDefinition()
        .getCDInterfacesList()
        .forEach(Interface -> declareCDType(Interface, context, true));

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
        .forEach(astcdClass -> typesConst.add(isInstanceOf(obj, astcdClass)));
    ast.getCDDefinition()
        .getCDInterfacesList()
        .forEach(astcdInterface -> typesConst.add(isInstanceOf(obj, astcdInterface)));

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
  private void declareCDType(ASTCDType astcdType, Context ctx, boolean isInterface) {
    SMTType smtType = new SMTType(isInterface, astcdType);
    smtType.setType(
        ctx.mkConstructor(
            "SS_" + astcdType.getName(), "SS_IS_" + astcdType.getName(), null, null, null));

    // (declare-fun a_attrib_something (A_obj) String) declare all attributes
    for (ASTCDAttribute myAttribute : astcdType.getCDAttributeList()) {
      FuncDecl<Sort> attributeFunc =
          ctx.mkFuncDecl(
              SMTHelper.printAttributeNameSMT(astcdType, myAttribute),
              sort,
              mcType2Sort(ctx, myAttribute.getMCType()));
      smtType.addAttribute(myAttribute, attributeFunc);
    }

    smtTypesMap.put(astcdType, smtType);
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
            SMTType smtType = smtTypesMap.get(astcdType);

            MinObject obj =
                new MinObject(
                    CDHelper.mkType(smtType.getClassType()), smtExpr, smtType.getAstcdType());

            for (Map.Entry<ASTCDAttribute, FuncDecl<? extends Sort>> attribute :
                smtType.getAttributesMap().entrySet()) {
              Expr<? extends Sort> attrExpr =
                  model.eval(attribute.getValue().apply(smtExpr), !partial);
              if (attrExpr.getNumArgs() == 0) {
                obj.addAttribute(attribute.getKey(), attrExpr);
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
    List<Constructor<? extends Sort>> constructors =
        smtTypesMap.values().stream().map(SMTType::getType).collect(Collectors.toList());
    constructors.add(ctx.mkConstructor("SS_NO_TYPE", "SS_IS_" + "NO_TYPE", null, null, null));

    return (Constructor<Sort>[]) constructors.toArray(new Constructor[0]);
  }

  private boolean hasType(Expr<? extends Sort> expr, ASTCDType astcdType, Model model) {
    BoolExpr hasType = (BoolExpr) model.evaluate(isInstanceOf(expr, astcdType), true);
    return hasType.getBoolValue() == Z3_lbool.Z3_L_TRUE;
  }
}
