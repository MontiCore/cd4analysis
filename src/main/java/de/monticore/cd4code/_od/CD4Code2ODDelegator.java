/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._od;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._ast.ASTCD4CodeNode;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._od.CD4CodeBasis2OD;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._od.CDBasis2OD;
import de.monticore.cdinterfaceandenum._od.CDInterfaceAndEnum2OD;
import de.monticore.expressions.commonexpressions._od.CommonExpressions2OD;
import de.monticore.expressions.expressionsbasis._od.ExpressionsBasis2OD;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.literals.mcliteralsbasis._od.MCLiteralsBasis2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._od.OOSymbols2OD;
import de.monticore.types.mcfullgenerictypes._od.MCFullGenericTypes2OD;
import de.monticore.types.mcsimplegenerictypes._od.MCSimpleGenericTypes2OD;
import de.monticore.umlmodifier._od.UMLModifier2OD;
import de.monticore.umlstereotype._od.UMLStereotype2OD;

public class CD4Code2ODDelegator {
  protected final CD4CodeTraverser traverser;
  protected final IndentPrinter pp;
  protected final ReportingRepository reporting;

  public CD4Code2ODDelegator(IndentPrinter printer, ReportingRepository reporting) {
    this.traverser = CD4CodeMill.traverser();
    this.pp = printer;
    this.reporting = reporting;

    final MCLiteralsBasis2OD mcLiteralsBasis2OD = new MCLiteralsBasis2OD(printer, reporting);
    mcLiteralsBasis2OD.setTraverser(traverser);
    traverser.add4MCLiteralsBasis(mcLiteralsBasis2OD);
    traverser.setMCLiteralsBasisHandler(mcLiteralsBasis2OD);

    final ExpressionsBasis2OD expressionsBasis2OD = new ExpressionsBasis2OD(printer, reporting);
    expressionsBasis2OD.setTraverser(traverser);
    traverser.add4ExpressionsBasis(expressionsBasis2OD);
    traverser.setExpressionsBasisHandler(expressionsBasis2OD);

    final CommonExpressions2OD commonExpressions2OD = new CommonExpressions2OD(printer, reporting);
    commonExpressions2OD.setTraverser(traverser);
    traverser.add4CommonExpressions(commonExpressions2OD);
    traverser.setCommonExpressionsHandler(commonExpressions2OD);

    final MCSimpleGenericTypes2OD mcSimpleGenericTypes2OD = new MCSimpleGenericTypes2OD(printer, reporting);
    mcSimpleGenericTypes2OD.setTraverser(traverser);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypes2OD);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypes2OD);

    final MCFullGenericTypes2OD mcFullGenericTypes2OD = new MCFullGenericTypes2OD(printer, reporting);
    mcFullGenericTypes2OD.setTraverser(traverser);
    traverser.add4MCFullGenericTypes(mcFullGenericTypes2OD);
    traverser.setMCFullGenericTypesHandler(mcFullGenericTypes2OD);

    final MCBasics2OD mcBasics2OD = new MCBasics2OD(printer, reporting);
    mcBasics2OD.setTraverser(traverser);
    traverser.add4MCBasics(mcBasics2OD);
    traverser.setMCBasicsHandler(mcBasics2OD);

    final OOSymbols2OD ooSymbols2OD = new OOSymbols2OD(printer, reporting);
    ooSymbols2OD.setTraverser(traverser);
    traverser.add4OOSymbols(ooSymbols2OD);
    traverser.setOOSymbolsHandler(ooSymbols2OD);

    final UMLStereotype2OD umlStereotype2OD = new UMLStereotype2OD(printer, reporting);
    umlStereotype2OD.setTraverser(traverser);
    traverser.add4UMLStereotype(umlStereotype2OD);
    traverser.setUMLStereotypeHandler(umlStereotype2OD);

    final UMLModifier2OD umlModifier2OD = new UMLModifier2OD(printer, reporting);
    umlModifier2OD.setTraverser(traverser);
    traverser.add4UMLModifier(umlModifier2OD);
    traverser.setUMLModifierHandler(umlModifier2OD);

    final CDBasis2OD cdBasis2OD = new CDBasis2OD(printer, reporting);
    cdBasis2OD.setTraverser(traverser);
    traverser.add4CDBasis(cdBasis2OD);
    traverser.setCDBasisHandler(cdBasis2OD);

    final CDInterfaceAndEnum2OD cdInterfaceAndEnum2OD = new CDInterfaceAndEnum2OD(printer, reporting);
    cdInterfaceAndEnum2OD.setTraverser(traverser);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnum2OD);
    traverser.setCDInterfaceAndEnumHandler(cdInterfaceAndEnum2OD);

    final CD4CodeBasis2OD cd4CodeBasis2OD = new CD4CodeBasis2OD(printer, reporting);
    cd4CodeBasis2OD.setTraverser(traverser);
    traverser.add4CD4CodeBasis(cd4CodeBasis2OD);
    traverser.setCD4CodeBasisHandler(cd4CodeBasis2OD);

    final CD4Code2OD cd4Code2OD = new CD4Code2OD(printer, reporting);
    cd4Code2OD.setTraverser(traverser);
    traverser.add4CD4Code(cd4Code2OD);
    traverser.setCD4CodeHandler(cd4Code2OD);
  }

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  public String printObjectDiagram(String modelName, ASTCD4CodeNode node) {
    pp.clearBuffer();
    pp.setIndentLength(2);
    pp.print("objectdiagram ");
    pp.print(modelName);
    pp.println(" {");
    pp.indent();
    node.accept(getTraverser());
    pp.print(";");
    pp.unindent();
    pp.println("}");
    return pp.getContent();
  }
}
