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
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.umlcd4a.symboltable.*;

public class CD4AnalysisLanguage extends CommonModelingLanguage {
  public static final String FILE_ENDING = "cd";
  
  public CD4AnalysisLanguage() {
    super("CD 4 Analysis Language", FILE_ENDING);
    
    addResolver(CommonResolvingFilter.create(CDSymbol.class, CDSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDTypeSymbol.class, CDTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDFieldSymbol.class, CDFieldSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDMethodSymbol.class, CDMethodSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDAssociationSymbol.class, CDAssociationSymbol.KIND));

    setModelNameCalculator(new CD4AnalysisModelNamerCalculator());

  }

  @Override
  public CDCompilationUnitMCParser getParser() {
    return CD4AnalysisParserFactory.createCDCompilationUnitMCParser();
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
