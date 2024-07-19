/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._od;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdbasis._od.CDBasis2OD;
import de.monticore.cdinterfaceandenum._od.CDInterfaceAndEnum2OD;
import de.monticore.expressions.commonexpressions._od.CommonExpressions2OD;
import de.monticore.expressions.expressionsbasis._od.ExpressionsBasis2OD;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.literals.mcliteralsbasis._od.MCLiteralsBasis2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._od.OOSymbols2OD;
import de.monticore.umlmodifier._od.UMLModifier2OD;
import de.monticore.umlstereotype._od.UMLStereotype2OD;

public class CD4Analysis2ODDelegator {
  public final CD4AnalysisTraverser traverser;
  protected final IndentPrinter pp;
  protected final ReportingRepository reporting;

  public CD4Analysis2ODDelegator(IndentPrinter printer, ReportingRepository reporting) {
    this.traverser = CD4AnalysisMill.inheritanceTraverser();
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

    final CDInterfaceAndEnum2OD cdInterfaceAndEnum2OD =
        new CDInterfaceAndEnum2OD(printer, reporting);
    cdInterfaceAndEnum2OD.setTraverser(traverser);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnum2OD);
    traverser.setCDInterfaceAndEnumHandler(cdInterfaceAndEnum2OD);

    final CD4Analysis2OD cd4Analysis2OD = new CD4Analysis2OD(printer, reporting);
    cd4Analysis2OD.setTraverser(traverser);
    traverser.add4CD4Analysis(cd4Analysis2OD);
    traverser.setCD4AnalysisHandler(cd4Analysis2OD);
  }

  public CD4AnalysisTraverser getTraverser() {
    return traverser;
  }

  public String printObjectDiagram(String modelName, ASTCD4AnalysisNode node) {
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
