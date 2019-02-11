/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.CommonModelingLanguage;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDMethodSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.CommonCD4AnalysisSymbolTableCreator;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
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
      ResolvingConfiguration resolvingConfiguration, @Nullable MutableScope enclosingScope) {
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
