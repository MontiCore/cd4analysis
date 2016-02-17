/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import javax.annotation.Nullable;
import java.util.Optional;

import de.monticore.CommonModelingLanguage;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDMethodSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.CommonCD4AnalysisSymbolTableCreator;

public class CD4AnalysisLanguage extends CommonModelingLanguage {
  public static final String FILE_ENDING = "cd";
  
  public CD4AnalysisLanguage() {
    super("CD 4 Analysis Language", FILE_ENDING);
    
    addResolver(CommonResolvingFilter.create(CDSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDFieldSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDMethodSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDAssociationSymbol.KIND));

    setModelNameCalculator(new CD4AnalysisModelNamerCalculator());

  }

  @Override
  public CD4AnalysisParser getParser() {
    return new CD4AnalysisParser();
  }
  
  @Override
  public Optional<CD4AnalysisSymbolTableCreator> getSymbolTableCreator(
      ResolverConfiguration resolverConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new CommonCD4AnalysisSymbolTableCreator(resolverConfiguration, enclosingScope));
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
