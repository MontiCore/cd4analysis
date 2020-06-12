/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdbasis.prettyprint;

import de.monticore.ast.Comment;
import de.monticore.cd.cdbasis._ast.*;
import de.monticore.cd.cdbasis._visitor.CDBasisVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;

import java.util.Iterator;
import java.util.List;

public class CDBasisPrettyPrinter implements CDBasisVisitor {
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

  public void printPreComments(Iterator<Comment> commentIterator) {
    if (commentIterator.hasNext()) {
      getPrinter().println("/*");
      getPrinter().indent();
      commentIterator.forEachRemaining(c -> getPrinter().println(c.getText()));
      getPrinter().unindent();
      getPrinter().println("*/");
    }
  }

  public void printPostComments(Iterator<Comment> commentIterator) {
    if (commentIterator.hasNext()) {
      // TODO SVa: what todo here?
      getPrinter().print(" // ");
      commentIterator.forEachRemaining(c -> getPrinter().print(c));
    }
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
    printModifier(node.getCDModifierList());
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

  private void printModifier(List<ASTCDModifier> cdModifierList) {
    printSeparator(cdModifierList.iterator(), " ");
    if (!cdModifierList.isEmpty()) {
      getPrinter().print(" ");
    }
  }

  @Override
  public void visit(ASTCDAbstractModifier node) {
    getPrinter().print("abstract");
  }

  @Override
  public void visit(ASTCDFinalModifier node) {
    getPrinter().print("final");
  }

  @Override
  public void visit(ASTCDStaticModifier node) {
    getPrinter().print("final");
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

    printModifier(node.getCDModifierList());
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
    printModifier(node.getCDModifierList());
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

  /**
   * Prints a list of ASTQualifiedNames in an ownVisit method
   *
   * @param iter      iterator for the list of ASTQualifiedNames
   * @param seperator string for seperating the ASTQualifiedNames
   */
  protected void printSeparator(Iterator<? extends ASTCDBasisNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }

  /**
   * Prints a list
   *
   * @param iter      iterator for the list
   * @param separator string for separating list
   */
  protected void printList(Iterator<? extends ASTMCBasicTypesNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }
}
