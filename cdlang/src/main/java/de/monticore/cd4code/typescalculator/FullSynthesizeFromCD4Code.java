/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.typescalculator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.types.check.*;

public class FullSynthesizeFromCD4Code extends AbstractSynthesize {

  public FullSynthesizeFromCD4Code() {
    this(CD4CodeMill.inheritanceTraverser());
  }

  public FullSynthesizeFromCD4Code(CD4CodeTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(CD4CodeTraverser traverser) {
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

    final SynthesizeSymTypeFromMCSimpleGenericTypes synthesizeSymTypeFromMCSimpleGenericTypes =
        new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synthesizeSymTypeFromMCSimpleGenericTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCSimpleGenericTypes(synthesizeSymTypeFromMCSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(synthesizeSymTypeFromMCSimpleGenericTypes);

    final SynthesizeSymTypeFromMCFullGenericTypes synthesizeSymTypeFromMCFullGenericTypes =
        new SynthesizeSymTypeFromMCFullGenericTypes();
    synthesizeSymTypeFromMCFullGenericTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCFullGenericTypes(synthesizeSymTypeFromMCFullGenericTypes);
    traverser.setMCFullGenericTypesHandler(synthesizeSymTypeFromMCFullGenericTypes);
  }
}
