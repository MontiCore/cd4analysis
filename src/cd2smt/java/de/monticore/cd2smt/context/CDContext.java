package de.monticore.cd2smt.context;

import com.microsoft.z3.*;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.*;

public class CDContext {


  private Map<ASTCDAssociation, FuncDecl<BoolSort>> assocFunctions;
  private Map<ASTCDClass, SMTClass> smtClasses;
  private Context context;

  private List<BoolExpr> classConstrs ;
  private List<BoolExpr>  assocConstr  ;
  private List<BoolExpr> inherConstr ;

  public Context getContext() {
    return context;
  }

  public List<BoolExpr> getAssocConstr() {
    return assocConstr;
  }

  public List<BoolExpr> getClassConstrs() {
    return classConstrs;
  }

  public List<BoolExpr> getInherConstr() {
    return inherConstr;
  }

  public Map<ASTCDAssociation, FuncDecl<BoolSort>> getAssocFunctions() {
    return assocFunctions;
  }

  public Map<ASTCDClass, SMTClass> getSmtClasses() {
    return smtClasses;
  }

  public void setAssocConstr(List<BoolExpr> assocConstr) {
    this.assocConstr = assocConstr;
  }

  public void setAssocFunctions(Map<ASTCDAssociation, FuncDecl<BoolSort>> assocFunctions) {
    this.assocFunctions = assocFunctions;
  }

  public void setClassConstrs(List<BoolExpr> classConstrs) {
    this.classConstrs = classConstrs;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public void setInherConstr(List<BoolExpr> inherConstr) {
    this.inherConstr = inherConstr;
  }

  public void setSmtClasses(Map<ASTCDClass, SMTClass> smtClasses) {
    this.smtClasses = smtClasses;
  }

  public CDContext() {
    smtClasses = new HashMap<>();
    assocFunctions = new HashMap<>();


  }

  public List<ASTCDClass> getSubclassList(ASTCDDefinition cd, ASTCDClass myClass) {
    List<ASTCDClass> subclasses = new LinkedList<>();
    for (ASTCDClass entry : cd.getCDClassesList()) {
      for (ASTMCObjectType entry2 : entry.getSuperclassList()) {
        if (entry2.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())).equals(myClass.getName()))
          subclasses.add(entry);
      }
    }
    return subclasses;
  }


  public String printAttributeNameSMT(ASTCDClass myClass, ASTCDAttribute myAttribute) {
    return fCharToLowerCase(myClass.getName()) + "_attrib_" + myAttribute.getName();
  }

  public String printSubclassFuncName(ASTCDClass myClass) {
    return fCharToLowerCase(myClass.getName()) + "_get_subclass";
  }

  public String printSMTClassName(ASTCDClass myClass) {
    return myClass.getName() + "_obj";
  }

  public String printSMTAssociationName(ASTCDAssociation myAssociation) {
    String right = myAssociation.getRight().getName();
    String left = myAssociation.getLeft().getName();
    return fCharToLowerCase(left) + "_" + fCharToLowerCase(right) + "_assoc";
  }

  public String fCharToLowerCase(String str) {
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  public Sort parseAttribType2SMT(Context ctx, ASTCDAttribute myAttribute) {
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

  public Optional<List<ASTCDAttribute>> getAttributeList(String className, ASTCDDefinition cd) {
    List<ASTCDAttribute> attributes = new LinkedList<>();
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className))
        attributes = myClass.getCDAttributeList();
    }
    return Optional.of(attributes);
  }

  public Optional<ASTCDClass> getClass(String className, ASTCDDefinition cd) {
    ASTCDClass res = new ASTCDClass();
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className))
        res = myClass;
    }
    return Optional.of(res);
  }

}





