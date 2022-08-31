package de.monticore.cd2smt.context;

import de.monticore.cd2smt.Helper.SMTNameHelper;
import com.microsoft.z3.*;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class CDContext {


  private final Map<ASTCDAssociation, SMTAssociation> smtAssociations;
  private final Map<ASTCDClass, SMTClass> smtClasses;
  private Context context;

  private List<BoolExpr> classConstrs;
  private List<BoolExpr> assocConstr;
  private List<BoolExpr> inherConstr;


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

  public Map<ASTCDAssociation, SMTAssociation> getSMTAssociations() {
    return smtAssociations;
  }

  public Map<ASTCDClass, SMTClass> getSmtClasses() {
    return smtClasses;
  }

  public void setAssocConstr(List<BoolExpr> assocConstr) {
    this.assocConstr = assocConstr;
  }



  public void setClassConstrs(List<BoolExpr> classConstrs) {
    this.classConstrs = classConstrs;
  }


  public void setInherConstr(List<BoolExpr> inherConstr) {
    this.inherConstr = inherConstr;
  }



  public CDContext(Context context) {
    this.context = context ;
    smtClasses = new HashMap<>();
    smtAssociations = new HashMap<>();

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

  public Optional <SMTClass> getSMTClass(String className){
    for (Map.Entry<ASTCDClass,SMTClass> entry : smtClasses.entrySet()){
      if (entry.getKey().getName().equals(className)){
        return Optional.of(entry.getValue()) ;
      }
    }
    return Optional.empty() ;
  }
  public Optional <SMTClass> getSMTClass(Expr<?extends  Sort> obj){
    String className = obj.getSort().toString().split("_")[0] ;
    return  getSMTClass(className) ;
  }

  public FuncDecl<?extends Sort> getAttributeFunc(SMTClass smtClass, String attr ){
    assert smtClass != null ;
    for (FuncDecl<? extends Sort> entry : smtClass.getAttributes()){
      if (entry.getName().toString().equals(SMTNameHelper.printAttributeNameSMT(smtClass.getASTCDClass(), attr))) {
        return entry;
      }
    }
    Log.error("attribute" + attr + "not found in the smtclass" + smtClass.getASTCDClass().getName());
    return null ;
  }

}





