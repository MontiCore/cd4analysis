package de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.defaultInhrStratregy;

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
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

import static de.monticore.cd2smt.Helper.SMTHelper.mkExists;
import static de.monticore.cd2smt.Helper.SMTHelper.mkForAll;

public class DefaultInhrStrategy implements InheritanceStrategy {
  private final Set<IdentifiableBoolExpr> inheritanceConstraints;
  private final Map<ASTCDType, InheritanceFeatures> inheritanceFeaturesMap;

  private final ClassData classData;

  public DefaultInhrStrategy(ClassData classData) {
    this.classData = classData;
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
  public Set<IdentifiableBoolExpr> getInheritanceConstraints() {
    return inheritanceConstraints;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit astCd, Context ctx, ClassData classData) {
    astCd
        .getCDDefinition()
        .getCDInterfacesList()
        .forEach(i -> declareInheritanceFeatures(astCd.getCDDefinition(), i, classData, ctx));
    astCd
        .getCDDefinition()
        .getCDClassesList()
        .forEach(c -> declareInheritanceFeatures(astCd.getCDDefinition(), c, classData, ctx));
    inheritanceConstraints.addAll(
        buildInheritanceConstraints(astCd.getCDDefinition(), classData, ctx));
  }

  @Override
  public Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet) {

    for (SMTObject obj : objectSet) {
      Map<ASTCDType, FuncDecl<Sort>> convert2SuperInterfaceList =
          inheritanceFeaturesMap.get(obj.getASTCDType()).getConvert2SuperTypeFuncMap();

      for (FuncDecl<Sort> convert2SuperInterface : convert2SuperInterfaceList.values()) {
        Expr<? extends Sort> superObj =
            model.eval(convert2SuperInterface.apply(obj.getSmtExpr()), true);
        SMTObject superSMTObj =
            objectSet.stream().filter(o -> o.getSmtExpr().equals(superObj)).findAny().orElse(null);
        assert superSMTObj != null;
        obj.addSuperInterfaceList(superSMTObj);
        superSMTObj.setAbstract();
      }
    }
    return objectSet;
  }

  protected void declareInheritanceFeatures(
      ASTCDDefinition cd, ASTCDType astcdType, ClassData classData, Context ctx) {
    InheritanceFeatures inheritanceFeatures = new InheritanceFeatures();
    Sort sort = classData.getSort(astcdType);

    // (declare-datatype B_subclasses ((TT_NO_SUBTYPE) (TT_B)))
    buildSubclassConstructorList(astcdType, cd, ctx)
        .forEach(inheritanceFeatures::addSubclassConstructor);

    // declare datatype und function to match each object to the subclass instance
    Constructor<Sort>[] constructors =
        inheritanceFeatures.getSubClassConstructorList().values().toArray(new Constructor[0]);
    if (constructors.length > 0) {
      inheritanceFeatures.setSubclassDatatype(
          ctx.mkDatatypeSort(astcdType.getName() + "_subclasses", constructors));
      inheritanceFeatures.setGetSubClass(
          ctx.mkFuncDecl(
              SMTHelper.printSubclassFuncName(astcdType),
              sort,
              inheritanceFeatures.getSubclassDatatype()));
    }

    inheritanceFeaturesMap.put(astcdType, inheritanceFeatures);
  }

  protected Map<ASTCDType, Constructor<Sort>> buildSubclassConstructorList(
      ASTCDType astcdType, ASTCDDefinition cd, Context ctx) {
    List<ASTCDClass> subClassList = CDHelper.getSubclassList(cd, astcdType);
    Map<ASTCDType, Constructor<Sort>> res = new HashMap<>();

    // add Constructor for subclasses
    for (ASTCDClass entry : subClassList) {
      Constructor<Sort> constructor =
          ctx.mkConstructor("TT_" + entry.getName(), "IS_TT" + entry.getName(), null, null, null);
      res.put(entry, constructor);
    }

    // add constructor for subInterfaces
    if (astcdType instanceof ASTCDInterface) {
      for (ASTCDInterface entry : CDHelper.getSubInterfaceList(cd, (ASTCDInterface) astcdType)) {
        Constructor<Sort> constructor =
            ctx.mkConstructor("TT_" + entry.getName(), "IS_TT" + entry.getName(), null, null, null);
        res.put(entry, constructor);
      }
      return res;

      // add constructor for No subclass
    } else if (astcdType instanceof ASTCDClass) {
      res.put(astcdType, ctx.mkConstructor("TT_NO_SUBTYPE", "IS_TT_NO_SUBTYPE", null, null, null));
      return res;
    } else {
      Log.error(
          "build of subclass Datatype ist not define for the class "
              + astcdType.getClass().getName());
      return res;
    }
  }

  List<IdentifiableBoolExpr> buildInheritanceConstraints(
      ASTCDDefinition cd, ClassData classData, Context ctx) {
    List<IdentifiableBoolExpr> constraints = new LinkedList<>();

    for (ASTCDClass myClass : cd.getCDClassesList()) {

      // add constraint for classes that inherit from another classes
      if (!myClass.getSuperclassList().isEmpty()) {
        Optional<ASTCDClass> superClass =
            Optional.ofNullable(
                CDHelper.getClass(
                    myClass
                        .getSuperclassList()
                        .get(0)
                        .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())),
                    cd));
        assert superClass.isPresent();
        constraints.addAll(
            buildCDTypeInheritanceConstraint(myClass, superClass.get(), classData, ctx));
      }

      // add constraint for  classes that  inherit  from interfacees
      for (ASTMCObjectType superC : myClass.getInterfaceList()) {
        Optional<ASTCDInterface> superInterface =
            Optional.ofNullable(
                CDHelper.getInterface(
                    superC.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())), cd));
        assert superInterface.isPresent();
        constraints.addAll(
            buildCDTypeInheritanceConstraint(myClass, superInterface.get(), classData, ctx));
      }
    }
    // add constraint for  interfaces that  inherit from  interfaces
    for (ASTCDInterface myInterface : cd.getCDInterfacesList()) {
      for (ASTMCObjectType superInterf : myInterface.getInterfaceList()) {
        Optional<ASTCDInterface> superInterface =
            Optional.ofNullable(
                CDHelper.getInterface(
                    superInterf.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())),
                    cd));
        assert superInterface.isPresent();

        constraints.addAll(
            buildCDTypeInheritanceConstraint(myInterface, superInterface.get(), classData, ctx));
      }
    }
    // asset each Interface Obj muss have a super instance Object
    constraints.addAll(mkConstrExistSubclass4Interf(cd, classData, ctx));
    return constraints;
  }

  protected List<IdentifiableBoolExpr> mkConstrExistSubclass4Interf(
      ASTCDDefinition cd, ClassData classData, Context ctx) {
    List<IdentifiableBoolExpr> constraints = new ArrayList<>();
    for (ASTCDInterface myInterface : cd.getCDInterfacesList()) {
      // make constraint for existence of super Instance of each Interface instance
      Expr<? extends Sort> interfObj =
          ctx.mkConst(myInterface.getName(), classData.getSort(myInterface));
      BoolExpr helpConstr = ctx.mkFalse();
      for (ASTCDType subInstance :
          inheritanceFeaturesMap.get(myInterface).getSubClassConstructorList().keySet()) {

        InheritanceFeatures subSMTInstance = inheritanceFeaturesMap.get(subInstance);
        Expr<Sort> subInstanceObj =
            ctx.mkConst(subInstance.getName(), classData.getSort(subInstance));

        helpConstr =
            ctx.mkOr(
                helpConstr,
                mkExists(
                    ctx,
                    Set.of(new ImmutablePair<>(subInstance, subInstanceObj)),
                    ctx.mkEq(
                        ctx.mkApp(
                            subSMTInstance.getConvert2SuperTypeFuncMap().get(myInterface),
                            subInstanceObj),
                        interfObj),
                    classData));
      }

      BoolExpr superInstanceConstraint =
          mkForAll(ctx, Set.of(new ImmutablePair<>(myInterface, interfObj)), helpConstr, classData);
      constraints.add(
          IdentifiableBoolExpr.buildIdentifiable(
              superInstanceConstraint,
              myInterface.get_SourcePositionStart(),
              Optional.of(myInterface.getName() + "Exist_super")));
    }
    return constraints;
  }

  List<IdentifiableBoolExpr> buildCDTypeInheritanceConstraint(
      ASTCDType subType, ASTCDType superType, ClassData classData, Context ctx) {
    InheritanceFeatures smtSubType = inheritanceFeaturesMap.get(subType);
    InheritanceFeatures smtSuperType = inheritanceFeaturesMap.get(superType);

    List<IdentifiableBoolExpr> constraints = new LinkedList<>();

    SourcePosition srcPos = subType.get_SourcePositionStart();
    assert srcPos.getFileName().isPresent();

    // add declare  the function to the smt representation of the subclass
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
                classData),
            classData);

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
            classData);
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
              classData);

      // add the constraints to the list
      constraints.add(
          IdentifiableBoolExpr.buildIdentifiable(
              typeConstr, srcPos, Optional.of(subType.getName() + "_inheritance_type_constr")));
    }
    return constraints;
  }
}
