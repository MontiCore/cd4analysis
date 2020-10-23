/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd4code._ast.ASTCD4CodeNode;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4codebasis._ast.ASTCD4CodeBasisNode;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceAndEnumNode;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.mcliteralsbasis._ast.ASTMCLiteralsBasisNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;

public class CD4CodePrettyPrinter extends CD4CodeDelegatorVisitor
    implements ExpressionsBasisVisitor {
  protected IndentPrinter printer;
  protected boolean printComments = true;

  public CD4CodePrettyPrinter() {
    this(new IndentPrinter());
  }

  public CD4CodePrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    setRealThis(this);
    setCDBasisVisitor(new CDBasisPrettyPrinter(printer));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumPrettyPrinter(printer));
    setCDAssociationVisitor(new CDAssociationPrettyPrinter(printer));
    setCD4CodeBasisVisitor(new CD4CodeBasisPrettyPrinter(printer));
    setCD4CodeVisitor(this);

    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setUMLStereotypeVisitor(new UMLStereotypePrettyPrinter(printer));
    setUMLModifierVisitor(new UMLModifierPrettyPrinter(printer));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
    setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypesPrettyPrinter(printer));
    setMCFullGenericTypesVisitor(new MCFullGenericTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
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
    getCDBasisVisitor().ifPresent(v -> ((CDBasisPrettyPrinter) v).setPrintComments(printComments));
    getCDInterfaceAndEnumVisitor().ifPresent(v -> ((CDInterfaceAndEnumPrettyPrinter) v).setPrintComments(printComments));
    getCDAssociationVisitor().ifPresent(v -> ((CDAssociationPrettyPrinter) v).setPrintComments(printComments));
    getCD4CodeBasisVisitor().ifPresent(v -> ((CD4CodeBasisPrettyPrinter) v).setPrintComments(printComments));
  }

  public String prettyprint(ASTCDCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDParameter node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDAssociationNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDInterfaceAndEnumNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCD4AnalysisNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCD4CodeBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCD4CodeNode node) {
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

  public String prettyprint(ASTExpressionsBasisNode node) {
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
