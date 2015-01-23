/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import javax.annotation.Nullable;

import cd4analysis.symboltable.CD4AnalysisSymbolTableCreator;
import cd4analysis.symboltable.CDAssociationSymbol;
import cd4analysis.symboltable.CDFieldSymbol;
import cd4analysis.symboltable.CDMethodSymbol;
import cd4analysis.symboltable.CDTypeSymbol;

import com.google.common.base.Optional;

import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.AbstractModelingLanguage;
import de.monticore.cocos.CoCoChecker;
import de.monticore.modelloader.ModelNameCalculator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class CD4AnalysisLanguage extends AbstractModelingLanguage {
  public static final String FILE_ENDING = "cd";

  private CD4AnalysisCoCoChecker coCoChecker;
  
  public CD4AnalysisLanguage() {
    super("CD 4 Analysis Language", FILE_ENDING, CDTypeSymbol.KIND);

    addResolver(CommonResolvingFilter.create(CDTypeSymbol.class, CDTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDFieldSymbol.class, CDFieldSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDMethodSymbol.class, CDMethodSymbol.KIND));
    addResolver(CommonResolvingFilter.create(CDAssociationSymbol.class, CDAssociationSymbol
        .KIND));
    
    this.coCoChecker = createCoCos();
  }

  private CD4AnalysisCoCoChecker createCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    // TODO RE<-RH profile.addCoCo(coco);
    return checker;
  }
  
  @Override
  public CDCompilationUnitMCParser getParser() {
    return new CDCompilationUnitMCParser();
  }

  @Override
  public Optional<? extends SymbolTableCreator> getSymbolTableCreator(ResolverConfiguration resolverConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new CD4AnalysisSymbolTableCreator(resolverConfiguration, enclosingScope));
  }

  @Override
  public Optional<? extends CoCoChecker> getContextConditionChecker() {
    return Optional.of(coCoChecker);
  }

  @Override
  public ModelNameCalculator getModelNameCalculator() {
    return new CD4AnalysisModelNameCalculator();
  }
}
