/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._od;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._visitor.CDBasisDelegatorVisitor;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.expressions.expressionsbasis._od.ExpressionsBasis2OD;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.literals.mcliteralsbasis._od.MCLiteralsBasis2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._od.TypeSymbols2OD;
import de.monticore.umlmodifier._od.UMLModifier2OD;
import de.monticore.umlstereotype._od.UMLStereotype2OD;

public class CDBasis2OD extends CDBasis2ODTOP {
  protected CDBasisVisitor realThis;

  public final CDBasisDelegatorVisitor visitor;

  public CDBasis2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = CDBasisMill.cDBasisDelegatorVisitorBuilder().build();
    visitor.setMCLiteralsBasisVisitor(new MCLiteralsBasis2OD(printer, reporting));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasis2OD(printer, reporting));
    visitor.setMCBasicsVisitor(new MCBasics2OD(printer, reporting));
    visitor.setTypeSymbolsVisitor(new TypeSymbols2OD(printer, reporting));
    visitor.setUMLStereotypeVisitor(new UMLStereotype2OD(printer, reporting));
    visitor.setUMLModifierVisitor(new UMLModifier2OD(printer, reporting));
    visitor.setCDBasisVisitor(this);
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  /**
   * @see CDBasis2OD#setRealThis(CDBasisVisitor)
   */
  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
}
