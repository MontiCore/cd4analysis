/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort;

import com.microsoft.z3.*;
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
 * This class creates transform classes and interfaces of a class diagram in SMT.
 *1- For each class and interface a new Sort is declared
 *2- For each Enum a new datatype is defined
 *3- For each Attribute a new function is declared
 */
public class DSClassStrategy implements ClassStrategy {
  protected Map<ASTCDType, Sort> typeMap;
  private final Map<ASTCDAttribute, FuncDecl<? extends Sort>> attributeMap = new HashMap<>();
  protected Map<ASTCDEnumConstant, Constructor<? extends Sort>> enumConstantMap = new HashMap<>();

  protected Context ctx;

  protected ASTCDCompilationUnit ast;

  public DSClassStrategy() {
    typeMap = new HashMap<>();
  }

  @Override
  public Sort getSort(ASTCDType astcdType) {
    return typeMap.get(astcdType);
  }

  @Override
  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astCdType) {
    String sortName = expr.getSort().getName().toString();
    if (printSMTCDTypeName(astCdType).equals(sortName)) {
      return ctx.mkTrue();
    }
    return ctx.mkFalse();
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
    return new HashSet<>();
  }

  @Override
  public Context getContext() {
    return ctx;
  }

  @Override
  public Expr<? extends Sort> getEnumConstant(ASTCDEnum astcdEnum, ASTCDEnumConstant enumConstant) {
    return ctx.mkConst(enumConstantMap.get(enumConstant).ConstructorDecl());
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    this.ast = ast;
    ctx = context;
    ast.getCDDefinition().getCDEnumsList().forEach(this::declareEnum);
    ast.getCDDefinition().getCDClassesList().forEach(this::declareCDType);
    ast.getCDDefinition().getCDInterfacesList().forEach(this::declareCDType);
  }

  /***
   * Transform Enumeration in smt by defining a new datatype containing the enum's constant as constructor.
   * CD:  enum Color {RED, YELLOW, GREEN}
   * SMT: (declare-datatype ((RED) (YELLOW) (GREEN))
   */
  private void declareEnum(ASTCDEnum astcdEnum) {

    List<Constructor<Sort>> constructorList = new ArrayList<>();

    // create a constructor for each enum constant
    for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
      Constructor<Sort> constructor =
          ctx.mkConstructor(constant.getName(), constant.getName(), null, null, null);
      constructorList.add(constructor);
      enumConstantMap.put(constant, constructor);
    }

    DatatypeSort<Sort> sort =
        ctx.mkDatatypeSort(astcdEnum.getName(), constructorList.toArray(new Constructor[0]));

    typeMap.put(astcdEnum, sort);
  }

  /***
   * Convert a class or an interface in SMT
   * CD:
   * class Auction {
   *      String name;
   * }
   * =====================================================
   * SMT:
   * (declare-sort Auction_obj 0)
   * (declare-fun auction_attr_name (Auction_Obj) String)
   */
  private void declareCDType(ASTCDType astcdType) {

    // declare the sort for the type
    UninterpretedSort typeSort =
        ctx.mkUninterpretedSort(ctx.mkSymbol(printSMTCDTypeName(astcdType)));
    typeMap.put(astcdType, typeSort);

    // declare a function for each attribute
    for (ASTCDAttribute myAttribute : astcdType.getCDAttributeList()) {
      Sort sort;
      if (CDHelper.isPrimitiveType(myAttribute.getMCType())) {
        sort = CDHelper.mcType2Sort(ctx, myAttribute.getMCType());
      } else if (CDHelper.isEnumType(ast.getCDDefinition(), myAttribute.getMCType().printType())) {
        sort =
            typeMap.get(
                CDHelper.getEnum(myAttribute.getMCType().printType(), ast.getCDDefinition()));
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
      attributeMap.put(myAttribute, attributeFunc);
    }

    typeMap.put(astcdType, typeSort);
  }

  /**
   * evaluate the model produce by the SMT-Solver to get the attribute values of Objects. if partial
   * = true, on ly the attribute with non-trivial value will appear on the Object-diagram.
   */
  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    for (Sort mySort : model.getSorts()) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(mySort)) {

        ASTCDType astcdType =
            CDHelper.getASTCDType(getType(mySort.getName()), ast.getCDDefinition());
        assert astcdType != null;
        MinObject obj = new MinObject(CDHelper.mkType(astcdType), smtExpr, astcdType);

        // evaluate each attribute
        for (ASTCDAttribute attribute : astcdType.getCDAttributeList()) {
          FuncDecl<? extends Sort> attrFunc = attributeMap.get(attribute);
          Expr<? extends Sort> attrExpr = model.eval(attrFunc.apply(smtExpr), !partial);
          if (attrExpr.getNumArgs() == 0) {
            obj.addAttribute(attribute, attrExpr);
          }
        }

        objectSet.add(obj);
      }
    }
    return objectSet;
  }

  private String getType(Symbol symbol) {
    int length = symbol.toString().length();
    StringBuilder stringBuilder = new StringBuilder(symbol.toString());
    stringBuilder.delete(length - 4, length); // remove the 4 last Characters
    return stringBuilder.toString();
  }

  private String printSMTCDTypeName(ASTCDType myClass) {
    return myClass.getName() + "_obj";
  }
}
