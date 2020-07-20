
/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;

public class CDBasisPrettyPrinter extends PrettyPrintUtil
    implements CDBasisVisitor {
  protected CDBasisVisitor realThis;

  public CDBasisPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDBasisPrettyPrinter(IndentPrinter printer) {
    super(printer);
    setRealThis(this);
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void traverse(ASTCDCompilationUnit node) {
    printPreComments(node);
    if (node.isPresentCDPackageStatement()) {
      node.getCDPackageStatement().accept(getRealThis());
      printPreComments(node);
    }
    for (ASTMCImportStatement i : node.getMCImportStatementsList()) {
      i.accept(getRealThis());
      println();
    }

    node.getCDTargetImportStatementsList().forEach(i -> i.accept(getRealThis()));
    if (!node.isEmptyCDTargetImportStatements()) {
      println();
    }
    println();

    if (null != node.getCDDefinition()) {
      node.getCDDefinition().accept(getRealThis());
    }
    printPostComments(node);
  }

  @Override
  public void visit(ASTCDPackageStatement node) {
    printPreComments(node);
    print("package " + Names.constructQualifiedName(node.getPackageList()) + ";");
    printPostComments(node);
    println();
  }

  @Override
  public void visit(ASTCDTargetImportStatement node) {
    printPreComments(node);
    print("targetimport ");
  }

  @Override
  public void endVisit(ASTCDTargetImportStatement node) {
    print((node.isStar() ? ".*" : "") + ";");
    printPostComments(node);
    println();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    printPreComments(node);
    node.getModifier().accept(getRealThis());
    print("classdiagram " + node.getName() + " {");
    printPreComments(node);
    println();
    indent();
  }

  @Override
  public void traverse(ASTCDDefinition node) {
    for (ASTCDElement element : node.getCDElementsList()) {
      printPreComments(node);
      element.accept(getRealThis());
      printPostComments(node);
      println();
    }
  }

  @Override
  public void endVisit(ASTCDDefinition node) {
    unindent();
    println("}");
  }

  @Override
  public void visit(ASTCDPackage node) {
    printPreComments(node);
    print("package " + node.getMCQualifiedName().getQName() + " {");
    printPreComments(node);
    println();
    indent();
  }

  @Override
  public void traverse(ASTCDPackage node) {
    node.getCDElementsList().forEach(e -> e.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDPackage node) {
    unindent();
    println("}");
  }

  @Override
  public void visit(ASTCDInterfaceUsage node) {
    print(" implements ");
  }

  @Override
  public void traverse(ASTCDInterfaceUsage node) {
    printList(getRealThis(), node.getInterfaceList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDExtendUsage node) {
    print(" extends ");
  }

  @Override
  public void traverse(ASTCDExtendUsage node) {
    printList(getRealThis(), node.getSuperclassList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDClass node) {
    printPreComments(node);

    node.getModifier().accept(getRealThis());
    print("class " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getRealThis());
    }
    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getRealThis());
    }

    if (!node.isEmptyCDMembers()) {
      print(" {");
      printPostComments(node);
      println();
      indent();
    }

  }

  @Override
  public void traverse(ASTCDClass node) {
    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDClass node) {
    if (!node.isEmptyCDMembers()) {
      unindent();
      println("}");
    }
    else {
      print(";");
      printPostComments(node);
      println();
    }
  }

  @Override
  public void visit(ASTCDAttribute node) {
    printPreComments(node);
    node.getModifier().accept(getRealThis());
    node.getMCType().accept(getRealThis());
    print(" " + node.getName());
  }

  @Override
  public void traverse(ASTCDAttribute node) {
    if (node.isPresentInitial()) {
      print(" = ");
      node.getInitial().accept(getRealThis());
    }
  }

  @Override
  public void endVisit(ASTCDAttribute node) {
    print(";");
    printPostComments(node);
    println();
  }

}
