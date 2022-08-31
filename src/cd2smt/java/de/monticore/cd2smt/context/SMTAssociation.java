package de.monticore.cd2smt.context;

import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;

public class SMTAssociation {
  private final SMTClass left;
  private final String leftRole;
  private final String rightRole;
  private final SMTClass right;
  private final FuncDecl<BoolSort> assocFunc;
  private String name;


  public SMTAssociation(ASTCDDefinition cd, CDContext cdContext, ASTCDAssociation association) {
    String assocName = SMTNameHelper.printSMTAssociationName(association);
    //get the link and the right class of the Association
    ASTCDClass leftClass = CDHelper.getClass(association.getRightQualifiedName().getQName(), cd);
    ASTCDClass rightClass = CDHelper.getClass(association.getLeftQualifiedName().getQName(), cd);

    //set the name and the role
      this.leftRole = association.getLeft().getCDRole().getName();
      this.rightRole = association.getRight().getCDRole().getName();

    assert cdContext.getSmtClasses().containsKey(rightClass);
    this.left = cdContext.getSmtClasses().get(leftClass);
    assert cdContext.getSmtClasses().containsKey(leftClass);
    this.right = cdContext.getSmtClasses().get(rightClass);

    //set the name of the Association
    if (association.isPresentName()) {
      this.name = association.getName();
    }


    //set the Association function
    Sort rightSortSMT = cdContext.getSmtClasses().get(leftClass).getSort();
    Sort leftSortSMT = cdContext.getSmtClasses().get(rightClass).getSort();
    this.assocFunc = cdContext.getContext().mkFuncDecl(assocName,
      new Sort[]{leftSortSMT, rightSortSMT}, cdContext.getContext().getBoolSort());

  }

  public FuncDecl<BoolSort> getAssocFunc() {
    return assocFunc;
  }

  boolean isPresentName() {
    return name != null;
  }

  public SMTClass getLeft() {
    return left;
  }

  public SMTClass getRight() {
    return right;
  }

  public String getRightRole() {
    return rightRole;
  }

  public String getLeftRole() {
    return leftRole;
  }

  public String getName() {
    return name;
  }
}
