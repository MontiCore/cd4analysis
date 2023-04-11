/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.prettyprint;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceAndEnumNode;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.mcliteralsbasis._ast.ASTMCLiteralsBasisNode;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;

@Deprecated(forRemoval = true)
public class CD4CodeBasisFullPrettyPrinter {

  protected CD4CodeBasisTraverser traverser;
  protected IndentPrinter printer;
  protected boolean printComments = true;

  public CD4CodeBasisFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CD4CodeBasisFullPrettyPrinter(IndentPrinter printer) {
    this.traverser = CD4CodeBasisMill.traverser();
    this.printer = printer;

    CD4CodeBasisPrettyPrinter cdCode = new CD4CodeBasisPrettyPrinter(printer);
    traverser.add4CD4CodeBasis(cdCode);
    traverser.setCD4CodeBasisHandler(cdCode);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer);
    traverser.add4UMLStereotype(umlStereotype);
    traverser.setUMLStereotypeHandler(umlStereotype);

    UMLModifierPrettyPrinter umlModifier = new UMLModifierPrettyPrinter(printer);
    traverser.add4UMLModifier(umlModifier);
    traverser.setUMLModifierHandler(umlModifier);

    CDBasisPrettyPrinter cdBasis = new CDBasisPrettyPrinter(printer);
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);

    CDInterfaceAndEnumPrettyPrinter cdInterface = new CDInterfaceAndEnumPrettyPrinter(printer);
    traverser.add4CDInterfaceAndEnum(cdInterface);
    traverser.setCDInterfaceAndEnumHandler(cdInterface);

    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressions);
    traverser.add4CommonExpressions(commonExpressions);
  }

  public CD4CodeBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeBasisTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public String prettyprint(ASTCDBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCLiteralsBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpressionsBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicTypesNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTUMLStereotypeNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTUMLModifierNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDInterfaceAndEnumNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
