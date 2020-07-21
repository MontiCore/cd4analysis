/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._od;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisDelegatorVisitor;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;
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

public class CD4CodeBasis2OD extends CD4CodeBasis2ODTOP {
  public final CD4CodeBasisDelegatorVisitor visitor;
  protected CD4CodeBasisVisitor realThis;

  public CD4CodeBasis2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = CD4CodeBasisMill.cD4CodeBasisDelegatorVisitorBuilder().build();
    visitor.setMCLiteralsBasisVisitor(new MCLiteralsBasis2OD(printer, reporting));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasis2OD(printer, reporting));
    visitor.setCommonExpressionsVisitor(new CommonExpressions2OD(printer, reporting));
    visitor.setMCBasicsVisitor(new MCBasics2OD(printer, reporting));
    visitor.setOOSymbolsVisitor(new OOSymbols2OD(printer, reporting));
    visitor.setUMLStereotypeVisitor(new UMLStereotype2OD(printer, reporting));
    visitor.setUMLModifierVisitor(new UMLModifier2OD(printer, reporting));
    visitor.setCDBasisVisitor(new CDBasis2OD(printer, reporting));
    visitor.setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnum2OD(printer, reporting));
    visitor.setCD4CodeBasisVisitor(this);
  }

  @Override
  public CD4CodeBasisVisitor getRealThis() {
    return realThis;
  }

  /**
   * @see CD4CodeBasis2OD#setRealThis(CDBasisVisitor)
   */
  @Override
  public void setRealThis(CD4CodeBasisVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
}
