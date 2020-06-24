/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._visitor.CDBasisDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.mcliteralsbasis._ast.ASTMCLiteralsBasisNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;

/**
 * this class can be used to print the underlying elements used in the CDs
 */
public class CDBasisPrettyPrinterDelegator extends CDBasisDelegatorVisitor {
  protected IndentPrinter printer;

  public CDBasisPrettyPrinterDelegator() {
    this(new IndentPrinter());
  }

  public CDBasisPrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    setRealThis(this);
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setUMLStereotypeVisitor(new UMLStereotypePrettyPrinter(printer));
    setUMLModifierVisitor(new UMLModifierPrettyPrinter(printer));
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public String prettyprint(ASTCDBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCLiteralsBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicTypesNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTUMLStereotypeNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTUMLModifierNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
}
