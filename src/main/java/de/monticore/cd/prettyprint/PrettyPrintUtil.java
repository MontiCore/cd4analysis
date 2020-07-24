/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCD4CodeBasisNode;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;

import java.util.Iterator;

public abstract class PrettyPrintUtil {
  protected IndentPrinter printer;

  protected boolean printComments = true;

  public static final String EMPTY_STRING = "";

  public PrettyPrintUtil() {
    this(new IndentPrinter());
  }

  public PrettyPrintUtil(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public boolean isPrintComments() {
    return printComments;
  }

  public void setPrintComments(boolean printComments) {
    this.printComments = printComments;
  }

  public void println() {
    this.printer.println();
  }

  public void println(Object o) {
    this.printer.println(o);
  }

  public void print(Object o) {
    this.printer.print(o);
  }

  public void indent() {
    this.printer.indent();
  }

  public void indent(int i) {
    this.printer.indent(i);
  }

  public void unindent() {
    this.printer.unindent();
  }

  public void printPreComments(ASTNode node) {
    if (printComments) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
  }

  public void printPostComments(ASTNode node) {
    if (printComments) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  /**
   * Prints a list of CDBasisNode in an ownVisit method
   *
   * @param iter      iterator for the list of {@link ASTCDBasisNode}s
   * @param seperator string for seperating the ASTCDBasisNodes
   */
  public void printSeparatorCDBasis(CDBasisVisitor visitor, Iterator<? extends ASTCDBasisNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      print(sep);
      iter.next().accept(visitor);
      sep = seperator;
    }
  }

  /**
   * Prints a list of ASTQualifiedNames in an ownVisit method
   *
   * @param iter      iterator for the list of {@link ASTCD4CodeBasisNode}s
   * @param seperator string for seperating the ASTCD4CodeBasisNodes
   */
  public void printSeparatorCD4CodeBasis(CD4CodeBasisVisitor visitor, Iterator<? extends ASTCD4CodeBasisNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      print(sep);
      iter.next().accept(visitor);
      sep = seperator;
    }
  }

  /**
   * Prints a list
   *
   * @param iter      iterator for the list
   * @param separator string for separating list
   */
  public void printList(MCBasicTypesVisitor visitor, Iterator<? extends ASTMCBasicTypesNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      print(sep);
      iter.next().accept(visitor);
      sep = separator;
    }
  }
}
