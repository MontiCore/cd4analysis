/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum._od;

import de.monticore.cdbasis._od.CDBasis2OD;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumDelegatorVisitor;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.expressions.expressionsbasis._od.ExpressionsBasis2OD;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.literals.mcliteralsbasis._od.MCLiteralsBasis2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._od.TypeSymbols2OD;
import de.monticore.umlmodifier._od.UMLModifier2OD;
import de.monticore.umlstereotype._od.UMLStereotype2OD;

public class CDInterfaceAndEnum2OD extends CDInterfaceAndEnum2ODTOP {
  public final CDInterfaceAndEnumDelegatorVisitor visitor;
  protected CDInterfaceAndEnumVisitor realThis;

  public CDInterfaceAndEnum2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = CDInterfaceAndEnumMill.cDInterfaceAndEnumDelegatorVisitorBuilder().build();
    visitor.setMCLiteralsBasisVisitor(new MCLiteralsBasis2OD(printer, reporting));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasis2OD(printer, reporting));
    visitor.setMCBasicsVisitor(new MCBasics2OD(printer, reporting));
    visitor.setTypeSymbolsVisitor(new TypeSymbols2OD(printer, reporting));
    visitor.setUMLStereotypeVisitor(new UMLStereotype2OD(printer, reporting));
    visitor.setUMLModifierVisitor(new UMLModifier2OD(printer, reporting));
    visitor.setCDBasisVisitor(new CDBasis2OD(printer, reporting));
    visitor.setCDInterfaceAndEnumVisitor(this);
  }

  @Override
  public CDInterfaceAndEnumVisitor getRealThis() {
    return realThis;
  }

  /**
   * @see CDInterfaceAndEnum2OD#setRealThis(CDBasisVisitor)
   */
  @Override
  public void setRealThis(CDInterfaceAndEnumVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
}
