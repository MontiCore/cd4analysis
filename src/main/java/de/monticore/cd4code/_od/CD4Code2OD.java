/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._od;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cd4codebasis._od.CD4CodeBasis2OD;
import de.monticore.cdbasis._od.CDBasis2OD;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
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

public class CD4Code2OD extends CD4Code2ODTOP {
  public final CD4CodeDelegatorVisitor visitor;
  protected CD4CodeVisitor realThis;

  public CD4Code2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = CD4CodeMill.cD4CodeDelegatorVisitorBuilder().build();
    visitor.setMCLiteralsBasisVisitor(new MCLiteralsBasis2OD(printer, reporting));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasis2OD(printer, reporting));
    visitor.setCommonExpressionsVisitor(new CommonExpressions2OD(printer, reporting));
    visitor.setMCBasicsVisitor(new MCBasics2OD(printer, reporting));
    visitor.setOOSymbolsVisitor(new OOSymbols2OD(printer, reporting));
    visitor.setUMLStereotypeVisitor(new UMLStereotype2OD(printer, reporting));
    visitor.setUMLModifierVisitor(new UMLModifier2OD(printer, reporting));
    visitor.setCDBasisVisitor(new CDBasis2OD(printer, reporting));
    visitor.setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnum2OD(printer, reporting));
    visitor.setCD4CodeBasisVisitor(new CD4CodeBasis2OD(printer, reporting));
    visitor.setCD4CodeVisitor(this);
  }

  @Override
  public CD4CodeVisitor getRealThis() {
    return realThis;
  }

  /**
   * @see CD4Code2OD#setRealThis(CDBasisVisitor)
   */
  @Override
  public void setRealThis(CD4CodeVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
}
