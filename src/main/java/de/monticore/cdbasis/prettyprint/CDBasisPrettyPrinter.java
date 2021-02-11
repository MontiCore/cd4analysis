
/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.se_rwth.commons.Names;

public class CDBasisPrettyPrinter extends PrettyPrintUtil
    implements CDBasisVisitor2, CDBasisHandler {

  protected CDBasisTraverser traverser;

  public CDBasisPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDBasisPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public CDBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CDBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTCDCompilationUnit node) {
    printPreComments(node);
    if (node.isPresentMCPackageDeclaration()) {
      // TODO SVa: remove when implemented in MC (MCBasicTypesPrettyPrinter.java) (#2687)
      visit(node.getMCPackageDeclaration());
      // node.getMCPackageDeclaration().accept(getTraverser());
      printPreComments(node);
    }
    for (ASTMCImportStatement i : node.getMCImportStatementList()) {
      i.accept(getTraverser());
      println();
    }

    node.getCDTargetImportStatementList().forEach(i -> i.accept(getTraverser()));
    if (!node.isEmptyCDTargetImportStatements()) {
      println();
    }
    println();

    if (null != node.getCDDefinition()) {
      node.getCDDefinition().accept(getTraverser());
    }
    printPostComments(node);
  }

  public void visit(ASTMCPackageDeclaration node) {
    printPreComments(node);
    node.accept(getTraverser());
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
    node.getModifier().accept(getTraverser());
    print("classdiagram " + node.getName() + " {");
    printPreComments(node);
    println();
    indent();
  }

  @Override
  public void traverse(ASTCDDefinition node) {
    for (ASTCDElement element : node.getCDElementList()) {
      printPreComments(node);
      element.accept(getTraverser());
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
    node.getCDElementList().forEach(e -> e.accept(getTraverser()));
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
    printList(getTraverser(), node.getInterfaceList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDExtendUsage node) {
    print(" extends ");
  }

  @Override
  public void traverse(ASTCDExtendUsage node) {
    printList(getTraverser(), node.getSuperclassList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDClass node) {
    printPreComments(node);

    node.getModifier().accept(getTraverser());
    print("class " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getTraverser());
    }
    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getTraverser());
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
    node.getCDMemberList().forEach(m -> m.accept(getTraverser()));
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
    node.getModifier().accept(getTraverser());
    node.getMCType().accept(getTraverser());
    print(" " + node.getName());
  }

  @Override
  public void traverse(ASTCDAttribute node) {
    if (node.isPresentInitial()) {
      print(" = ");
      node.getInitial().accept(getTraverser());
    }
  }

  @Override
  public void endVisit(ASTCDAttribute node) {
    print(";");
    printPostComments(node);
    println();
  }

}
