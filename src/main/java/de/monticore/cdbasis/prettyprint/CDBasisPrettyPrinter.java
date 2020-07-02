
/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
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
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentCDPackageStatement()) {
      node.getCDPackageStatement().accept(getRealThis());
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    for (ASTMCImportStatement i : node.getMCImportStatementList()) {
      i.accept(getRealThis());
      println();
    }
    if (node.isPresentCDTargetPackageStatement()) {
      if (!node.isEmptyMCImportStatements()) {
        println();
      }
      node.getCDTargetPackageStatement().accept(getRealThis());
    }

    node.getCDTargetImportStatementList().forEach(i -> i.accept(getRealThis()));
    if (!node.isEmptyCDTargetImportStatements()) {
      println();
    }
    println();

    if (null != node.getCDDefinition()) {
      node.getCDDefinition().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void visit(ASTCDPackageStatement node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    print("package " + Names.getQualifiedName(node.getPackageList()) + ";");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
    println();
  }

  @Override
  public void visit(ASTCDTargetPackageStatement node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    print("targetpackage " + Names.getQualifiedName(node.getTargetpackageList()) + ";");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
    println();
  }

  @Override
  public void visit(ASTCDTargetImportStatement node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    print("targetimport ");
  }

  @Override
  public void endVisit(ASTCDTargetImportStatement node) {
    print((node.isStar() ? ".*" : "") + ";");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
    println();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getModifier().accept(getRealThis());
    print("classdiagram " + node.getName() + " {");
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    println();
    indent();
  }

  @Override
  public void traverse(ASTCDDefinition node) {
    for (ASTCDElement element : node.getCDElementList()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
      element.accept(getRealThis());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
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
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    print("package " + node.getMCQualifiedName().getQName() + " {");
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    println();
    indent();
  }

  @Override
  public void traverse(ASTCDPackage node) {
    node.getCDElementList().forEach(e -> e.accept(getRealThis()));
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
    printList(getRealThis(), node.getSuperclasList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDClass node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());

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
      CommentPrettyPrinter.printPostComments(node, getPrinter());
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
      CommentPrettyPrinter.printPostComments(node, getPrinter());
      println();
    }
  }

  @Override
  public void visit(ASTCDAttribute node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
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
    CommentPrettyPrinter.printPostComments(node, getPrinter());
    println();
  }

}
