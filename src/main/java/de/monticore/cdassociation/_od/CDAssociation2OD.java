/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._od;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._visitor.CDAssociationDelegatorVisitor;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;
import de.monticore.cdbasis._od.CDBasis2OD;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.expressions.expressionsbasis._od.ExpressionsBasis2OD;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.literals.mcliteralsbasis._od.MCLiteralsBasis2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._od.OOSymbols2OD;
import de.monticore.umlmodifier._od.UMLModifier2OD;
import de.monticore.umlstereotype._od.UMLStereotype2OD;

public class CDAssociation2OD extends CDAssociation2ODTOP {
  public final CDAssociationDelegatorVisitor visitor;
  protected CDAssociationVisitor realThis;

  public CDAssociation2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = CDAssociationMill.cDAssociationDelegatorVisitorBuilder().build();
    visitor.setMCLiteralsBasisVisitor(new MCLiteralsBasis2OD(printer, reporting));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasis2OD(printer, reporting));
    visitor.setMCBasicsVisitor(new MCBasics2OD(printer, reporting));
    visitor.setOOSymbolsVisitor(new OOSymbols2OD(printer, reporting));
    visitor.setUMLStereotypeVisitor(new UMLStereotype2OD(printer, reporting));
    visitor.setUMLModifierVisitor(new UMLModifier2OD(printer, reporting));
    visitor.setCDBasisVisitor(new CDBasis2OD(printer, reporting));
    visitor.setCDAssociationVisitor(this);
  }

  @Override
  public CDAssociationVisitor getRealThis() {
    return realThis;
  }

  /**
   * @see CDAssociation2OD#setRealThis(CDBasisVisitor)
   */
  @Override
  public void setRealThis(CDAssociationVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
}
