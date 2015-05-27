/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.AbstractModelingLanguage;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.umlcd4a._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a._parser.CDCompilationUnitMCParser;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDMethodSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.CommonCD4AnalysisSymbolTableCreator;

public class CD4AnalysisLanguage extends AbstractModelingLanguage {
  public static final String FILE_ENDING = "cd";
  
  public CD4AnalysisLanguage() {
    super("CD 4 Analysis Language", FILE_ENDING, CDTypeSymbol.KIND);
    
    addResolver(CommonResolvingFilter.create(CDSymbol.class, CDSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDTypeSymbol.class, CDTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDFieldSymbol.class, CDFieldSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDMethodSymbol.class, CDMethodSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDAssociationSymbol.class, CDAssociationSymbol.KIND));

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
