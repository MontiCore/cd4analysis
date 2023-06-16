/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.multExpression;

import static de.monticore.cd2smt.Helper.SMTHelper.mkExists;
import static de.monticore.cd2smt.Helper.SMTHelper.mkForAll;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.SMTHelper;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceStrategy;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/***
 * This Strategy converts inheritance-relations in SMT in a multi-instance-way, object and
 * super-object are represent as separately. So for each inheritance-relation between supertype and subtype:
 * 1-A function is declared to ensure the navigation from object to super-objects.
 * 2-A new data-type is created to indicate the possible type of the super object.
 * 3-A Function that maps each super-object to his concrete type.
 * 4-constraints to ensure a one -to one relation between an object and sub-objects.
 * eg.
 * CD: classdiagram {
 *   class Account;
 *   class BusinessAcc extends Account;
 * }
 * =========================================================================================
 * SMT:
 *(declare-fun super_BusinessAcc (S_BusinessAcc) (S_Account)) (1)
 *(declare-datatype ((Account_sub 0))(((T_BusinessAcc) (T_Account)))) (2)
 *(declare-fun Account_type (S_Account) (Account_sub)) (3)
 *...
 * -
 * 5-more generally another constraint is defined to avoid objects with interface/abstract class type
 */

public class MEInheritanceStrategy implements InheritanceStrategy {
  private final Set<IdentifiableBoolExpr> inheritanceConstraints;
  private final Map<ASTCDType, InheritanceFeatures> inheritanceFeaturesMap;

  private ClassData classData;
  private Context ctx;

  private ASTCDDefinition cd;

  public MEInheritanceStrategy() {
    inheritanceConstraints = new HashSet<>();
    inheritanceFeaturesMap = new HashMap<>();
  }

  @Override
  public Expr<? extends Sort> getSuperInstance(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    return inheritanceFeaturesMap
        .get(objType)
        .getConvert2SuperTypeFuncMap()
        .get(superType)
        .apply(objExpr);
  }

  @Override
  public BoolExpr instanceOf(Expr<? extends Sort> obj, ASTCDType objType) {
    Log.error("FunctionInstance of not implemented yet for Single Sort Strategy");
    return null; // TODO: Fixme implement
  }

  @Override
  public BoolExpr filterObject(Expr<? extends Sort> obj, ASTCDType type) {
    return classData.hasType(obj, type);
  }

  @Override
  public Set<IdentifiableBoolExpr> getInheritanceConstraints() {
    return inheritanceConstraints;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit astCd, Context ctx, ClassData classData) {
    this.classData = classData;
    this.ctx = ctx;
    this.cd = classData.getClassDiagram().getCDDefinition();
    astCd.getCDDefinition().getCDInterfacesList().forEach(this::declareInheritanceFeatures);
    astCd.getCDDefinition().getCDClassesList().forEach(this::declareInheritanceFeatures);
    inheritanceConstraints.addAll(buildInheritanceConstraints());
  }
  /** evaluate inheritance functions and get sub-objects. */
  @Override
  public Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet) {

    for (SMTObject obj : objectSet) {
      if (!(obj.getASTCDType() instanceof ASTCDEnum)) {
        Map<ASTCDType, FuncDecl<Sort>> inheritanceFeatures =
            inheritanceFeaturesMap.get(obj.getASTCDType()).getConvert2SuperTypeFuncMap();

        for (FuncDecl<Sort> convert2SuperInterface : inheritanceFeatures.values()) {
          Expr<? extends Sort> superObj =
              model.eval(convert2SuperInterface.apply(obj.getSmtExpr()), true);

          SMTObject superSMTObj =
              objectSet.stream()
                  .filter(o -> o.getSmtExpr().equals(superObj))
                  .findAny()
                  .orElse(null);

          assert superSMTObj != null;
          obj.addSuperInterfaceList(superSMTObj);
          // mark the super-object to delete after merging with the parent
          superSMTObj.setType(CDHelper.ObjType.ABSTRACT_OBJ);
        }
      }
    }
    return objectSet;
  }
  /***
   * declare inheritance features for each inheritance relation with "astcdtype"
   * as direct super-class
   * */
  protected void declareInheritanceFeatures(ASTCDType astcdType) {
    InheritanceFeatures inhrFeatures = new InheritanceFeatures();
    Sort sort = classData.getSort(astcdType);

    // (declare-datatype B_subclasses ((TT_NO_SUBTYPE) (TT_B)))
    buildSubclassConstructorList(astcdType).forEach(inhrFeatures::addSubclassConstructor);

    // declare datatype und function to match each object to the subclass instance
    Constructor<Sort>[] constructors =
        inhrFeatures.getSubClassConstructorList().values().toArray(new Constructor[0]);
    if (constructors.length > 0) {
      inhrFeatures.setSubclassDatatype(
          ctx.mkDatatypeSort(astcdType.getName() + "_subclasses", constructors));
      inhrFeatures.setGetSubClass(
          ctx.mkFuncDecl(
              SMTHelper.printSubclassFuncName(astcdType),
              sort,
              inhrFeatures.getSubclassDatatype()));
    }

    inheritanceFeaturesMap.put(astcdType, inhrFeatures);
  }

  protected Map<ASTCDType, Constructor<Sort>> buildSubclassConstructorList(ASTCDType astcdType) {
    List<ASTCDType> subTypeList = CDHelper.getSubTypeList(cd, astcdType);
    Map<ASTCDType, Constructor<Sort>> res = new HashMap<>();

    // build Constructor for subclasses of the class astcdtype
    for (ASTCDType entry : subTypeList) {
      Constructor<Sort> constructor =
          ctx.mkConstructor("TT_" + entry.getName(), "IS_TT" + entry.getName(), null, null, null);
      res.put(entry, constructor);

      if ((astcdType instanceof ASTCDClass) && !astcdType.getModifier().isAbstract()) {
        res.put(
            astcdType, ctx.mkConstructor("TT_NO_SUBTYPE", "IS_TT_NO_SUBTYPE", null, null, null));
      }
    }

    return res;
  }

  List<IdentifiableBoolExpr> buildInheritanceConstraints() {
    List<IdentifiableBoolExpr> constraints = new LinkedList<>();

    for (ASTCDType astcdType : CDHelper.getASTCDTypes(cd)) {
      for (ASTCDType superC : CDHelper.getSuperTypeList(astcdType, cd)) {
        constraints.addAll(buildCDTypeInheritanceConstraint(astcdType, superC));
      }
    }

    // assert that each Interface/abstract object muss have a parent (sub-object)
    constraints.addAll(mkExistSubInstance4Interfaces());
    constraints.addAll(mkExistSubInstance4AbstClass());
    return constraints;
  }

  /** ensure that each interface expression is inherited */
  protected List<IdentifiableBoolExpr> mkExistSubInstance4Interfaces() {
    List<IdentifiableBoolExpr> constraints = new ArrayList<>();
    for (ASTCDInterface myInterface : cd.getCDInterfacesList()) {
      constraints.add(mkExistSubInstance(myInterface));
    }
    return constraints;
  }

  /** ensure that each abstract class expression is inherited */
  protected List<IdentifiableBoolExpr> mkExistSubInstance4AbstClass() {
    List<IdentifiableBoolExpr> constraints = new ArrayList<>();
    List<ASTCDClass> abstractClassList = CDHelper.getAbstractClassList(cd);

    for (ASTCDClass abstractClass : abstractClassList) {
      constraints.add(mkExistSubInstance(abstractClass));
    }
    return constraints;
  }

  /** ensure each expression of the type astcdType is inherited */
  protected IdentifiableBoolExpr mkExistSubInstance(ASTCDType astcdType) {

    Expr<? extends Sort> abstrObj = ctx.mkConst(astcdType.getName(), classData.getSort(astcdType));

    List<ASTCDType> subTypeList = CDHelper.getSubTypeList(cd, astcdType);

    BoolExpr helpConstr = ctx.mkFalse();
    for (ASTCDType subType : subTypeList) {

      InheritanceFeatures subSMTInstance = inheritanceFeaturesMap.get(subType);
      Expr<Sort> subInstanceObj = ctx.mkConst(subType.getName(), classData.getSort(subType));
      FuncDecl<Sort> convert2Sub = subSMTInstance.getConvert2SuperTypeFuncMap().get(astcdType);
      Set<Pair<ASTCDType, Expr<? extends Sort>>> quanParams =
          Set.of(new ImmutablePair<>(subType, subInstanceObj));
      helpConstr =
          ctx.mkOr(
              helpConstr,
              mkExists(
                  ctx,
                  quanParams,
                  ctx.mkEq(ctx.mkApp(convert2Sub, subInstanceObj), abstrObj),
                  this));
    }
    Set<Pair<ASTCDType, Expr<? extends Sort>>> quanParams =
        Set.of(new ImmutablePair<>(astcdType, abstrObj));

    BoolExpr superInstanceConstraint = mkForAll(ctx, quanParams, helpConstr, this);

    return IdentifiableBoolExpr.buildIdentifiable(
        superInstanceConstraint,
        astcdType.get_SourcePositionStart(),
        Optional.of(astcdType.getName() + "Exist_sub"));
  }

  List<IdentifiableBoolExpr> buildCDTypeInheritanceConstraint(
      ASTCDType subType, ASTCDType superType) {
    InheritanceFeatures smtSubType = inheritanceFeaturesMap.get(subType);
    InheritanceFeatures smtSuperType = inheritanceFeaturesMap.get(superType);

    List<IdentifiableBoolExpr> constraints = new LinkedList<>();

    SourcePosition srcPos = subType.get_SourcePositionStart();
    assert srcPos.getFileName().isPresent();

    // add declare the function to the smt representation of the subclass
    FuncDecl<Sort> convert2Super =
        ctx.mkFuncDecl(
            "Convert_" + subType.getName() + "_to_" + superType.getName(),
            classData.getSort(subType),
            classData.getSort(superType));
    smtSubType.addConvert2SuperTypeFunc(superType, convert2Super);

    // constraints to make sure that the function convert_to will be defined
    Expr<Sort> subclassObj = ctx.mkConst(subType.getName() + "subtype", classData.getSort(subType));
    Expr<Sort> superClassObj =
        ctx.mkConst(superType.getName() + "supertype", classData.getSort(superType));

    BoolExpr constrConvert =
        mkForAll(
            ctx,
            Set.of(new ImmutablePair<>(subType, subclassObj)),
            mkExists(
                ctx,
                Set.of(new ImmutablePair<>(superType, superClassObj)),
                ctx.mkEq(ctx.mkApp(convert2Super, subclassObj), superClassObj),
                this),
            this);

    constraints.add(
        IdentifiableBoolExpr.buildIdentifiable(
            constrConvert, srcPos, Optional.of(subType.getName() + "_inheritance_convert")));

    // the conversion must be a bijection
    Expr<Sort> b1 = ctx.mkConst("b1", classData.getSort(subType));
    Expr<Sort> b2 = ctx.mkConst("b2", classData.getSort(subType));
    BoolExpr bijection =
        mkForAll(
            ctx,
            Set.of(new ImmutablePair<>(subType, b1), new ImmutablePair<>(subType, b2)),
            ctx.mkEq(
                ctx.mkEq(ctx.mkApp(convert2Super, b1), ctx.mkApp(convert2Super, b2)),
                ctx.mkEq(b1, b2)),
            this);
    constraints.add(
        IdentifiableBoolExpr.buildIdentifiable(
            bijection, srcPos, Optional.of(subType.getName() + "_inheritance_bijection")));

    if (smtSuperType.getSubClassConstructorList().size() > 0) {
      // type Constraint
      Expr<Sort> b3 = ctx.mkConst("b3", classData.getSort(subType));
      Expr<? extends Sort> sort =
          ctx.mkConst(smtSuperType.getSubClassConstructorList().get(subType).ConstructorDecl());
      BoolExpr typeConstr =
          mkForAll(
              ctx,
              Set.of(new ImmutablePair<>(subType, b3)),
              ctx.mkEq(ctx.mkApp(smtSuperType.getSubClass(), ctx.mkApp(convert2Super, b1)), sort),
              this);

      // add the constraints to the list
      constraints.add(
          IdentifiableBoolExpr.buildIdentifiable(
              typeConstr, srcPos, Optional.of(subType.getName() + "_inheritance_type_constr")));
    }
    return constraints;
  }
}
