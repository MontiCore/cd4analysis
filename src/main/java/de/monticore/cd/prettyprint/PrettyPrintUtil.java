/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.prettyprint;

import de.monticore.ast.Comment;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;

import java.util.Iterator;

public abstract class PrettyPrintUtil {
  abstract public IndentPrinter getPrinter();

  abstract public CDBasisVisitor getRealThis();

  public void printPreComments(Iterator<Comment> commentIterator) {
    if (commentIterator.hasNext()) {
      /*getPrinter().println("/*");
      getPrinter().indent();*/
      commentIterator.forEachRemaining(c -> getPrinter().println(c.getText()));
      //getPrinter().unindent();
      //getPrinter().println("*/");
    }
  }

  public void printPostComments(Iterator<Comment> commentIterator) {
    if (commentIterator.hasNext()) {
      //getPrinter().print(" // ");
      commentIterator.forEachRemaining(getPrinter()::print);
    }
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
