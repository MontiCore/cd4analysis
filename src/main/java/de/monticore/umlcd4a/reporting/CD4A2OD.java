/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.reporting;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.literals.literals._od.Literals2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._od.Types2OD;
import de.monticore.umlcd4a.cd4analysis._od.CD4Analysis2OD;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.umlcd4a.cd4analysis._visitor.CommonCD4AnalysisDelegatorVisitor;


public class CD4A2OD extends CD4Analysis2OD {
    
  private CD4AnalysisVisitor realThis = this;
  
  private final CommonCD4AnalysisDelegatorVisitor visitor;
  
  public CD4A2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = new CommonCD4AnalysisDelegatorVisitor();
    visitor.set_de_monticore_literals_literals__visitor_LiteralsVisitor(new Literals2OD(printer, reporting));
    visitor.set_de_monticore_types_types__visitor_TypesVisitor(new Types2OD(printer, reporting));
    visitor.set_de_monticore_umlcd4a_cd4analysis__visitor_CD4AnalysisVisitor(new CD4Analysis2OD(printer, reporting));  }

  
  /**
   * @see de.monticore.umlcd4a.cd4analysis._od.CD4Analysis2OD#setRealThis(de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor)
   */
  @Override
  public void setRealThis(CD4AnalysisVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }


  /**
   * @see de.monticore.umlcd4a.cd4analysis._od.CD4Analysis2OD#getRealThis()
   */
  @Override
  public CD4AnalysisVisitor getRealThis() {
    return realThis;
  }

}
