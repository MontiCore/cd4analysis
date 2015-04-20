/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.RecognitionException;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.cocos.helper.Assert;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a._parser.CDCompilationUnitMCParser;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;

/**
 * TODO: Write me!
 *
 * @author Robert Heim
 */
public abstract class AbstractCoCoTest {
  private final CD4AnalysisLanguage cd4AnalysisLang = new CD4AnalysisLanguage();
  
  private CDCompilationUnitMCParser parser = CD4AnalysisParserFactory
      .createCDCompilationUnitMCParser();
  
  private GlobalScope globalScope;
  
  protected Scope cdScope;
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.AbstractCoCoTest
   */
  public AbstractCoCoTest() {
  }
  
  /**
   * The {@link CD4AnalysisCoCoChecker} to use with a set of CoCos already
   * assigned.
   * 
   * @return
   */
  abstract protected CD4AnalysisCoCoChecker getChecker();
  
  /**
   * Asserts that each of the expectedErrors is found (checking code and msg) in
   * any of the actual produced errors that occurred when the
   * {@link CD4AnalysisCoCoChecker} run on the given modelName. Furthermore, it
   * is asserted that there are not any other errors.
   * 
   * @param model full qualified model path
   * @param expectedErrors
   */
  protected void testModelForErrors(String model,
      Collection<CoCoFinding> expectedErrors) {
    CD4AnalysisCoCoChecker checker = getChecker();
    
    ASTCDCompilationUnit root = loadModel(model);
    checker.checkAll(root);
    Assert.assertEqualErrorCounts(expectedErrors, CoCoLog.getFindings());
    Assert.assertErrorCodeAndMsg(expectedErrors, CoCoLog.getFindings());
  }
  
  /**
   * Asserts that no error occurred when the {@link CD4AnalysisCoCoChecker} run
   * on the given modelName.
   * 
   * @param model full qualified model path
   */
  protected void testModelNoErrors(String model) {
    CD4AnalysisCoCoChecker checker = getChecker();
    ASTCDCompilationUnit root = loadModel(model);
    checker.checkAll(root);
    Assert.assertEqualErrorCounts(new ArrayList<CoCoFinding>(), CoCoLog.getFindings());
  }
  
  private ASTCDCompilationUnit loadModel(String modelFullQualifiedFilename) {
    Path model = Paths.get(modelFullQualifiedFilename);
    
    try {
      Optional<ASTCDCompilationUnit> root = parser.parse(model.toString());
      if (root.isPresent()) {
        
        // create Symboltable
        Set<Path> p = new HashSet<>();
        p.add(model.toAbsolutePath());
        ModelPath modelPath = new ModelPath(p);
        ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
        resolverConfiguration.addTopScopeResolvers(cd4AnalysisLang.getResolvers());
        this.globalScope = new GlobalScope(modelPath, cd4AnalysisLang.getModelLoader(),
            resolverConfiguration);
        Optional<CD4AnalysisSymbolTableCreator> stc = cd4AnalysisLang
            .getSymbolTableCreator(resolverConfiguration, globalScope);
        if (stc.isPresent()) {
          stc.get().createFromAST(root.get());
        }
        cdScope = globalScope.getSubScopes().get(0).getSubScopes().get(0);
        return root.get();
      }
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Error during loading of model " + modelFullQualifiedFilename + ".");
  }
}
