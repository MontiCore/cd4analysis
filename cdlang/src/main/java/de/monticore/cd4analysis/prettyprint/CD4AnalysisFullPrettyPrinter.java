/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceAndEnumNode;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPrettyPrinter;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.prettyprint.MCArrayTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

@Deprecated(forRemoval = true)
public class CD4AnalysisFullPrettyPrinter {

  protected CD4AnalysisTraverser traverser;
  protected IndentPrinter printer;
  protected boolean printComments = true;

  protected CDBasisPrettyPrinter cdBasis;
  protected CDInterfaceAndEnumPrettyPrinter cdInterface;
  protected CDAssociationPrettyPrinter cdAssoc;

  public CD4AnalysisFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CD4AnalysisFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.traverser = CD4AnalysisMill.inheritanceTraverser();

    cdBasis = new CDBasisPrettyPrinter(printer);
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);

    cdInterface = new CDInterfaceAndEnumPrettyPrinter(printer);
    traverser.add4CDInterfaceAndEnum(cdInterface);
    traverser.setCDInterfaceAndEnumHandler(cdInterface);

    cdAssoc = new CDAssociationPrettyPrinter(printer);
    traverser.add4CDAssociation(cdAssoc);
    traverser.setCDAssociationHandler(cdAssoc);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer);
    traverser.add4UMLStereotype(umlStereotype);
    traverser.setUMLStereotypeHandler(umlStereotype);

    UMLModifierPrettyPrinter umlModifier = new UMLModifierPrettyPrinter(printer);
    traverser.add4UMLModifier(umlModifier);
    traverser.setUMLModifierHandler(umlModifier);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.add4MCCollectionTypes(collectionTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);

    MCArrayTypesPrettyPrinter arrayTypes = new MCArrayTypesPrettyPrinter(printer);
    traverser.add4MCArrayTypes(arrayTypes);
    traverser.setMCArrayTypesHandler(arrayTypes);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer);
    traverser.add4BitExpressions(bitExpressions);
    traverser.setBitExpressionsHandler(bitExpressions);

    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    traverser.add4CommonExpressions(commonExpressions);
    traverser.setCommonExpressionsHandler(commonExpressions);
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
    cdBasis.setPrintComments(printComments);
    cdInterface.setPrintComments(printComments);
    cdAssoc.setPrintComments(printComments);
  }

  public void setTraverser(CD4AnalysisTraverser traverser) {
    this.traverser = traverser;
  }

  public CD4AnalysisTraverser getTraverser() {
    return traverser;
  }

  public String prettyprint(ASTCDCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDBasisNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDAssociationNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCDInterfaceAndEnumNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTCD4AnalysisNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
