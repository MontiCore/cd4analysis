package de.monticore.literals.prettyprint;

import java.util.Iterator;

import mc.ast.ASTNode;
import mc.ast.ConcreteVisitor;
import mc.helper.IndentPrinter;
import de.monticore.literals._ast.ASTBooleanLiteral;
import de.monticore.literals._ast.ASTCharLiteral;
import de.monticore.literals._ast.ASTDoubleLiteral;
import de.monticore.literals._ast.ASTFloatLiteral;
import de.monticore.literals._ast.ASTIntLiteral;
import de.monticore.literals._ast.ASTLongLiteral;
import de.monticore.literals._ast.ASTNullLiteral;
import de.monticore.literals._ast.ASTSignedDoubleLiteral;
import de.monticore.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals._ast.ASTSignedIntLiteral;
import de.monticore.literals._ast.ASTSignedLongLiteral;
import de.monticore.literals._ast.ASTStringLiteral;

/**
 * 
 * This class is responsible for pretty-printing literals. It is implemented
 * using the Visitor pattern. The Visitor pattern traverses a tree in depth
 * first, the visit and ownVisit-methods are called when a node is traversed,
 * the endVisit methods are called when the whole subtree of a node has been
 * traversed. The ownVisit-Methods stop the automatic traversal order and allow
 * to explictly visit subtrees by calling getVisitor().startVisit(ASTNode)
 *
 * <br>
 * <br>
 * Copyright (c) 2012 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author$,  Martin Schindler
 * @version $Date$<br>
 *          $Revision$
 */
//STATE ? * maybe own project usable as lib
public class LiteralsPrettyPrinterConcreteVisitor extends ConcreteVisitor {
  
  // printer to use
  protected IndentPrinter printer;
  
  /**
   * Constructor.
   * 
   * @param printer the printer to write to.
   */
  public LiteralsPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    this.printer = printer;
  }
  
  /*
   * helping methods
   */

  /**
   * Prints a list of ASTNodes in an ownVisit method
   * 
   * @param iter iterator for the list of ASTNodes
   * @param seperator string for seperating the ASTNodes
   */
  protected void printList(Iterator<? extends ASTNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      printer.print(sep);
      visitor.startVisit(iter.next()); // visit item
      sep = seperator;
    }
  }
  
  /**
   * Prints a list of ASTNodes in an ownVisit method
   * 
   * @param iter iterator for the list of ASTNodes
   * @param seperator string for seperating the ASTNodes
   */
  protected void printStringList(Iterator<String> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      printer.print(sep);
      printer.print(iter.next()); // visit item
      sep = seperator;
    }
  }
  
  /**
   * Prints a "null" literal
   * 
   * @param a null literal
   */
  public void visit(ASTNullLiteral a) {
    printer.print("null");
  }
  
  /**
   * Prints a boolean literal
   * 
   * @param a boolean literal
   */
  public void visit(ASTBooleanLiteral a) {
    printer.print(a.getSource());
  }
  
  /**
   * Prints a char literal
   * 
   * @param a char literal
   */
  public void visit(ASTCharLiteral a) {
    printer.print(a.getSource());
  }
  
  /**
   * Prints a string literal
   * 
   * @param a string literal
   */
  public void visit(ASTStringLiteral a) {
    printer.print(a.getSource());
  }
  
  /**
   * Prints a int literal
   * 
   * @param a int literal
   */
  public void visit(ASTIntLiteral a) {
    printer.print(a.getSource());
  }
  
  /**
   * Prints a long literal
   * 
   * @param a long literal
   */
  public void visit(ASTLongLiteral a) {
    printer.print(a.getSource());
  }
  
  /**
   * Prints a float literal
   * 
   * @param a float literal
   */
  public void visit(ASTFloatLiteral a) {
    printer.print(a.getSource());
  }
  
  /**
   * Prints a double literal
   * 
   * @param a double literal
   */
  public void visit(ASTDoubleLiteral a) {
    printer.print(a.getSource());
  }
  
  /**
   * Prints a signed double literal.
   * 
   * @param ast a signed double literal
   */
  public void visit(ASTSignedDoubleLiteral ast) {
    printer.print(ast.getSource());
  }
  
  /**
   * Prints a signed float literal.
   * 
   * @param ast a signed float literal
   */
  public void visit(ASTSignedFloatLiteral ast) {
    printer.print(ast.getSource());
  }
  
  /**
   * Prints a signed int literal.
   * 
   * @param ast a signed int literal
   */
  public void visit(ASTSignedIntLiteral ast) {
    printer.print(ast.getSource());
  }
  
  /**
   * Prints a signed long literal.
   * 
   * @param ast a signed long literal
   */
  public void visit(ASTSignedLongLiteral ast) {
    printer.print(ast.getSource());
  }
  
}
