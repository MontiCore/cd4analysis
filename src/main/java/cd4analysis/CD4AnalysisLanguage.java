/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import cd4analysis.symboltable.CD4AnalysisSymbolTableCreator;
import cd4analysis.symboltable.CDTypeSymbol;
import com.google.common.base.Optional;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.AbstractModelingLanguage;
import de.monticore.cocos.ContextConditionProfile;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.ScopeManipulationApi;
import de.monticore.symboltable.SymbolTableCreator;

import javax.annotation.Nullable;

public class CD4AnalysisLanguage extends AbstractModelingLanguage {
  public static final String FILE_ENDING = "cd";

  public CD4AnalysisLanguage() {
    super("CD 4 Analysis Language", FILE_ENDING, CDTypeSymbol.KIND);
  }

  @Override
  public CDCompilationUnitMCParser getParser() {
    return new CDCompilationUnitMCParser();
  }

  @Override
  public Optional<? extends SymbolTableCreator> getSymbolTableCreator(ResolverConfiguration resolverConfiguration, @Nullable ScopeManipulationApi enclosingScope) {
    return Optional.of(new CD4AnalysisSymbolTableCreator(resolverConfiguration, enclosingScope));
  }

  @Override
  public Optional<? extends ContextConditionProfile> getContextConditionProfile() {
    return Optional.absent();
  }
}
