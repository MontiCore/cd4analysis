/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.typescalculator;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.types.check.*;

public class FullSynthesizeFromCD4Analysis extends AbstractSynthesize {

  public FullSynthesizeFromCD4Analysis(CD4AnalysisTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public FullSynthesizeFromCD4Analysis() {
    this(CD4AnalysisMill.traverser());
  }

  public void init(CD4AnalysisTraverser traverser) {
    final SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes =
        new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCBasicTypes(synthesizeSymTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(synthesizeSymTypeFromMCBasicTypes);

    final SynthesizeSymTypeFromMCCollectionTypes synthesizeSymTypeFromMCCollectionTypes =
        new SynthesizeSymTypeFromMCCollectionTypes();
    synthesizeSymTypeFromMCCollectionTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCCollectionTypes(synthesizeSymTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(synthesizeSymTypeFromMCCollectionTypes);

    final SynthesizeSymTypeFromMCArrayTypes synthesizeSymTypeFromMCArrayTypes =
        new SynthesizeSymTypeFromMCArrayTypes();
    synthesizeSymTypeFromMCArrayTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCArrayTypes(synthesizeSymTypeFromMCArrayTypes);
    traverser.setMCArrayTypesHandler(synthesizeSymTypeFromMCArrayTypes);
  }
}
