package de.monticore.cd2smt.context;

import de.monticore.cd2smt.Helper.SMTNameHelper;
import com.microsoft.z3.*;
import de.monticore.cd2smt.context.CDArtifacts.SMTAssociation;
import de.monticore.cd2smt.context.CDArtifacts.SMTClass;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CDContext {


  private final Map<ASTCDAssociation, SMTAssociation> smtAssociations;
  private final Map<ASTCDClass, SMTClass> smtClasses;
  private final Context context;

  private   List<Pair<String,BoolExpr>> oclConstraints;
  private  List<Pair<String,BoolExpr>> associationConstraints;
  private   List<Pair<String,BoolExpr>> inheritanceConstraints;


  public Context getContext() {
    return context;
  }

  public List<Pair<String, BoolExpr>> getAssociationConstraints() {
    return associationConstraints;
  }

  public List<Pair<String, BoolExpr>> getInheritanceConstraints() {
    return inheritanceConstraints;
  }
  public void setAssociationConstraints( List<Pair<String,BoolExpr>> associationConstraints){
    this.associationConstraints = associationConstraints;
  }

  public void setInheritanceConstraints(List<Pair<String,BoolExpr>> inheritanceConstraints) {
    this.inheritanceConstraints = inheritanceConstraints;
  }

  public List<Pair<String,BoolExpr>> getOclConstraints() {
    return oclConstraints;
  }

  public Map<ASTCDAssociation, SMTAssociation> getSMTAssociations() {
    return smtAssociations;
  }

  public Map<ASTCDClass, SMTClass> getSmtClasses() {
    return smtClasses;
  }

  public void setOclConstraints(List<Pair<String,BoolExpr>> oclConstraints) {
    this.oclConstraints = oclConstraints;
  }


  public CDContext(Context context) {
    this.context = context ;
    smtClasses = new HashMap<>();
    smtAssociations = new HashMap<>();
    oclConstraints= new ArrayList<>();
    associationConstraints = new ArrayList<>();
    inheritanceConstraints = new ArrayList<>();
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
    Log.error("attribute " + attr + "not found in the smtclass " + smtClass.getASTCDClass().getName());
    return null ;
  }

  public SMTAssociation getAssocFunc(SMTClass smtClass, String otherRole ){
    assert smtClass != null ;
    for (Map.Entry<ASTCDAssociation, SMTAssociation> entry : smtClass.getSMTAssociations().entrySet()){
      if (entry.getKey().getRight().getCDRole().getName().equals(otherRole) &&
        !entry.getKey().getRight().getName().equals(smtClass.getASTCDClass().getName())) {
        return entry.getValue();
      }
      if (entry.getKey().getLeft().getCDRole().getName().equals(otherRole) &&
        !entry.getKey().getLeft().getName().equals(smtClass.getASTCDClass().getName())) {
        return entry.getValue();
      }
    }
    Log.error("No Associations  Founds for the role  " + otherRole + " in the smt class " + smtClass.getASTCDClass().getName());
    return null ;
  }

  public boolean containsAttribute(SMTClass smtClass, String AttrName ){
    for (ASTCDAttribute attribute: smtClass.getASTCDClass().getCDAttributeList()){
      if (attribute.getName().equals(AttrName)){
        return  true ;
      }
    }
    return false ;
  }

  public  Optional<Model> getModel (Context ctx, List<Pair<String,BoolExpr>> constraints){
    Solver s = ctx.mkSolver();
    int i = 0;  // Names must be unique, hence we have a counter
    for (Pair<String,BoolExpr> expr : constraints){
      s.assertAndTrack(expr.getRight(), ctx.mkBoolConst("inv____" + expr.getLeft() + "____" + i));
      i++;
    }
    if (s.check() == Status.SATISFIABLE)
      return Optional.of(s.getModel());
    else {
      Log.warn("Found no instance. The following invariants lead to a contradiction: \n\t" +
        Arrays.stream(s.getUnsatCore())
          .map(AST::getSExpr)
          .map(name -> name.split("____")[1])
          .collect(Collectors.toSet()));
      return Optional.empty();
    }

  }

}





