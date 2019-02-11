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
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisDelegatorVisitor;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class CD4A2OD extends CD4Analysis2OD {
    
  private CD4AnalysisVisitor realThis = this;
  
  private final CD4AnalysisDelegatorVisitor visitor;
  
  public CD4A2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = new CD4AnalysisDelegatorVisitor();
    visitor.setLiteralsVisitor(new Literals2OD(printer, reporting));
    visitor.setTypesVisitor(new Types2OD(printer, reporting));
    visitor.setCD4AnalysisVisitor(new CD4Analysis2OD(printer, reporting));  }

  
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
