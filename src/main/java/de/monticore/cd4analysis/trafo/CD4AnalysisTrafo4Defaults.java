/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor;

import java.util.Deque;

public class CD4AnalysisTrafo4Defaults extends CDAfterParseHelper
    implements CD4AnalysisVisitor {
  protected CD4AnalysisVisitor realThis;
  protected CD4AnalysisVisitor symbolTableCreator;

  public CD4AnalysisTrafo4Defaults(Deque<ICD4AnalysisScope> scopeStack) {
    this(new CDAfterParseHelper(),
        CD4AnalysisMill.cD4AnalysisSymbolTableCreatorBuilder().setScopeStack(scopeStack).build());
  }

  public CD4AnalysisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    setRealThis(this);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CD4AnalysisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4AnalysisVisitor realThis) {
    this.realThis = realThis;
  }
}
