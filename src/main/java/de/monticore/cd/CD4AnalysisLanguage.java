/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd;

import de.monticore.CommonModelingLanguage;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.symboltable.*;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

import javax.annotation.Nullable;
import java.util.Optional;

public class CD4AnalysisLanguage extends CommonModelingLanguage {
  public static final String FILE_ENDING = "cd";
  
  public CD4AnalysisLanguage() {
    super("CD 4 Analysis Language", FILE_ENDING);
    
    addResolvingFilter(CommonResolvingFilter.create(CDSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(CDTypeSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(CDFieldSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(CDMethodSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(CDAssociationSymbol.KIND));

    setModelNameCalculator(new CD4AnalysisModelNamerCalculator());

  }

  @Override
  public CD4AnalysisParser getParser() {
    return new CD4AnalysisParser();
  }
  
  @Override
  public Optional<CD4AnalysisSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, @Nullable Scope enclosingScope) {
    return Optional.of(new CommonCD4AnalysisSymbolTableCreator(resolvingConfiguration, enclosingScope));
  }
  
  @Override
  public CD4AnalysisModelLoader getModelLoader() {
    return (CD4AnalysisModelLoader) super.getModelLoader();
  }

  @Override
  protected CD4AnalysisModelLoader provideModelLoader() {
    return new CD4AnalysisModelLoader(this);
  }
}
