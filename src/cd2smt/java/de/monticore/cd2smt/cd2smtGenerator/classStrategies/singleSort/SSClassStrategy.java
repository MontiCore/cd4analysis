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

/***
 * This class converts the classes and the interfaces of a class diagram in SMT.
 * 1-A unique Sort :"Object" is declared as the common type of all objects.
 * 2-A datatype "CDType" is defined and represents the real type of objects.
 * 3-A function "has_type" is declared that assigns each function to his type.
 * 4- For each Enum a new datatype is defined
 * 5- For each Attribute a new function is declared
 * 6-add define the function "has_type" with a boolean constraint: ensure type uniqueness of objects
 * CD:
 *    classdiagram SingleSort {
 *       class Car {
 *         String name;
 *       }
 *       class Wheel ;
 *   }
 *SMT:
 *   (declare-sort Object 0)                                 (1)
 *   (declare-datatype ((CDType 0)) (((SS_Car) (SS_Wheel) )) (2)
 *   (declare-fun has_type (Object CDType) Bool)             (3)
 *   (declare-fun Car_attr_name (Object ) String)            (5)
 *
 */
// @formatter:on
public class SSClassStrategy implements ClassStrategy {
  protected Context ctx;
  protected DatatypeSort<? extends Sort> types;
  protected ASTCDCompilationUnit ast;
  protected FuncDecl<BoolSort> hasTypeFunc;
  protected Sort sort;

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

    // declare the unique sort (declare-sort Object 0) (1)
    this.sort = ctx.mkUninterpretedSort("Object");

    ast.getCDDefinition().getCDEnumsList().forEach(this::declareEnum);
    ast.getCDDefinition().getCDClassesList().forEach(this::declareCDType);
    ast.getCDDefinition().getCDInterfacesList().forEach(this::declareCDType);

    // declare the datatype which indicate the real type of Each Expr
    types = ctx.mkDatatypeSort("CDType", collectTypeConstructors());

    // declare the function that maps each expression to his Type
    hasTypeFunc = ctx.mkFuncDecl("has_type", new Sort[] {sort, types}, ctx.mkBoolSort());
    classConstraints.add(buildTypeUniquenessConstraint());
  }

  /***
   * declare a boolean constraint which ensures that each Object has a unique Type. (6)
   */
  private IdentifiableBoolExpr buildTypeUniquenessConstraint() {
    Expr<? extends Sort> obj = ctx.mkConst("obj", sort);

    Expr<? extends Sort> type1 = ctx.mkConst("type1", types);
    Expr<? extends Sort> type2 = ctx.mkConst("type2", types);
    BoolExpr body =
        ctx.mkImplies(
            ctx.mkAnd(ctx.mkApp(hasTypeFunc, obj, type1), ctx.mkApp(hasTypeFunc, obj, type2)),
            ctx.mkEq(type1, type2));

    BoolExpr res = ctx.mkForall(new Expr[] {obj, type1, type2}, body, 0, null, null, null, null);
    return IdentifiableBoolExpr.buildIdentifiable(
        res, ast.get_SourcePositionStart(), Optional.of("Object_type_unique"));
  }

  /** this function declared a "CDType" constructor for each ASTCDType */
  protected void declareCDType(ASTCDType astcdType) {

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

  /***
   * Transform Enumeration in smt by defining a new datatype containing the enum's constant as constructor.(4)
   * CD:  enum Color {RED, YELLOW, GREEN}
   * SMT: (declare-datatype ((RED) (YELLOW) (GREEN))
   */
  private void declareEnum(ASTCDEnum astcdEnum) {
    SMTEnum smtEnum = new SMTEnum();
    List<Constructor<Sort>> constructorList = new ArrayList<>();

    // create constant for the attribute values and saves im smtType
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

  /***
   * evaluate the model produce by the SMT-Solver to get the attribute values of Objects. if partial
   * = true, on ly the attribute with non-trivial value will appear on the Object-diagram.
   */
  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    Sort[] sorts = model.getSorts();
    Sort Object =
        Arrays.stream(sorts).filter(x -> x.toString().equals("Object")).findAny().orElse(null);
    if (Object != null) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(Object)) {
        for (ASTCDType astcdType : CDHelper.getASTCDTypes(ast.getCDDefinition())) {

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

  /** collect the CDType constructor of each class and interface. */
  private Constructor<Sort>[] collectTypeConstructors() {
    Set<Constructor<? extends Sort>> constructors = new HashSet<>(typeMap.values());

    // constructors.add(ctx.mkConstructor("SS_NO_TYPE", "SS_IS_" + "NO_TYPE", null, null, null));

    return (Constructor<Sort>[]) constructors.toArray(new Constructor[0]);
  }

  protected boolean hasType(Expr<? extends Sort> expr, ASTCDType astcdType, Model model) {
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
