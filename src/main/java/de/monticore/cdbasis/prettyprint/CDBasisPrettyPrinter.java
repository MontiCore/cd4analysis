/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

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
  protected IndentPrinter printer;
  protected CDBasisVisitor realThis;

  public CDBasisPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDBasisPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void traverse(ASTCDCompilationUnit node) {
    printPreComments(node.iterator_PreComments());
    if (node.isPresentCDPackageStatement()) {
      node.getCDPackageStatement().accept(getRealThis());
      printPreComments(node.getCDPackageStatement().iterator_PreComments());
    }
    for (ASTMCImportStatement i : node.getMCImportStatementList()) {
      i.accept(getRealThis());
      getPrinter().println();
    }
    if (node.isPresentCDTargetPackageStatement()) {
      if (!node.isEmptyMCImportStatements()) {
        getPrinter().println();
      }
      node.getCDTargetPackageStatement().accept(getRealThis());
    }

    node.getCDTargetImportStatementList().forEach(i -> i.accept(getRealThis()));
    if (!node.isEmptyCDTargetImportStatements()) {
      getPrinter().println();
    }
    getPrinter().println();

    if (null != node.getCDDefinition()) {
      node.getCDDefinition().accept(getRealThis());
    }
    printPostComments(node.iterator_PostComments());
  }

  @Override
  public void visit(ASTCDPackageStatement node) {
    printPreComments(node.iterator_PreComments());
    getPrinter().print("package " + Names.getQualifiedName(node.getPackageList()) + ";");
    printPostComments(node.iterator_PostComments());
    getPrinter().println();
  }

  @Override
  public void visit(ASTCDTargetPackageStatement node) {
    printPreComments(node.iterator_PreComments());
    getPrinter().print("targetpackage " + Names.getQualifiedName(node.getTargetpackageList()) + ";");
    printPostComments(node.iterator_PostComments());
    getPrinter().println();
  }

  @Override
  public void visit(ASTCDTargetImportStatement node) {
    printPreComments(node.iterator_PreComments());
    getPrinter().print("targetimport ");
  }

  @Override
  public void endVisit(ASTCDTargetImportStatement node) {
    getPrinter().print((node.isStar() ? ".*" : "") + ";");
    printPostComments(node.iterator_PostComments());
    getPrinter().println();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    printPreComments(node.iterator_PreComments());
    node.getModifier().accept(getRealThis());
    getPrinter().print("classdiagram " + node.getName() + " {");
    printPostComments(node.iterator_PostComments());
    getPrinter().println();
    getPrinter().indent();
  }

  @Override
  public void traverse(ASTCDDefinition node) {
    for (ASTCDElement element : node.getCDElementList()) {
      printPreComments(element.iterator_PreComments());
      element.accept(getRealThis());
      printPostComments(element.iterator_PostComments());
      getPrinter().println();
    }
  }

  @Override
  public void endVisit(ASTCDDefinition node) {
    getPrinter().unindent();
    getPrinter().println("}");
  }

  @Override
  public void visit(ASTCDInterfaceUsage node) {
    getPrinter().print(" implements ");
  }

  @Override
  public void traverse(ASTCDInterfaceUsage node) {
    printList(node.getInterfaceList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDExtendUsage node) {
    getPrinter().print(" extends ");
  }

  @Override
  public void traverse(ASTCDExtendUsage node) {
    printList(node.getSuperclassList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDClass node) {
    printPreComments(node.iterator_PreComments());

    node.getModifier().accept(getRealThis());
    getPrinter().print("class " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getRealThis());
    }
    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getRealThis());
    }

    if (!node.isEmptyCDMembers()) {
      getPrinter().print(" {");
      printPostComments(node.iterator_PostComments());
      getPrinter().println();
      getPrinter().indent();
    }

  }

  @Override
  public void traverse(ASTCDClass node) {
    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDClass node) {
    if (!node.isEmptyCDMembers()) {
      getPrinter().unindent();
      getPrinter().println("}");
    }
    else {
      getPrinter().print(";");
      printPostComments(node.iterator_PostComments());
      getPrinter().println();
    }
  }

  @Override
  public void visit(ASTCDAttribute node) {
    printPreComments(node.iterator_PreComments());
    node.getModifier().accept(getRealThis());
    node.getMCType().accept(getRealThis());
    getPrinter().print(" " + node.getName());
  }

  @Override
  public void traverse(ASTCDAttribute node) {
    if (node.isPresentInitial()) {
      getPrinter().print(" = ");
      node.getInitial().accept(getRealThis());
    }
  }

  @Override
  public void endVisit(ASTCDAttribute node) {
    getPrinter().print(";");
    printPostComments(node.iterator_PostComments());
    getPrinter().println();
  }

}
