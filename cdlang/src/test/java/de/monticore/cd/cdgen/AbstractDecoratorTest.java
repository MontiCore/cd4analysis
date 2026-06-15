/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdgen.CDGenTool;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.runtime.junit.AbstractMCTest;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

/**
 * The setup of a @{@link DecoratorConfig} test. Override the {@link
 * #initializeDecConf(GlobalExtensionManagement, DecoratorConfig, GeneratorSetup)} method and call
 * the {@link #doTest(ASTCDCompilationUnit)} with your CD.
 */
public abstract class AbstractDecoratorTest extends AbstractMCTest {
  
  protected File outputDir;
  protected CDGenTool tool;
  
  @BeforeEach
  public void init() {
    LogStub.initPlusLog();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    tool = new CDGenTool();
    this.outputDir = new File("target/cdGenOutTest/" + getClass().getSimpleName());
  }
  
  /**
   * Initialize the {@link DecoratorConfig}, e.g. using {@link
   * DecoratorConfig#withDecorator(IDecorator)}
   *
   * @param glex the global extension management
   * @param config the {@link DecoratorConfig} used for decorating the CD
   * @param setup the {@link GeneratorSetup} used for generating the decorated CD
   */
  public abstract void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup);
  
  protected List<File> getAdditionalTemplatesPath() { return new ArrayList<>(); }
  
  protected Optional<MCPath> getHandWrittenPath() { return Optional.empty(); }
  
  protected boolean withClass2MC() {
    return false;
  }
  
  public TestResult doTest(ASTCDCompilationUnit cd) {
    outputDir.mkdirs();
    
    tool.trafoBeforeSymtab(Collections.singletonList(cd));
    
    final boolean class2mc = this.withClass2MC();
    tool.initializeSymbolTable(class2mc);
    
    // Create ST
    tool.createSymbolTable(cd);
    
    // Complete ST
    tool.completeSymbolTable(cd);
    
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    GeneratorSetup generatorSetup = tool.newConfiguredGeneratorSetup(getAdditionalTemplatesPath(),
        getHandWrittenPath(), this.outputDir.getAbsolutePath(), glex);
    
    List<TestResult> results = new ArrayList<>();
    
    // Finally, invoke the decorating generator
    tool.decorateAndGenerate(glex,
        // Initialize the decorator config
        decoratorConfig -> initializeDecConf(glex, decoratorConfig, generatorSetup), generatorSetup,
        () -> {
          // Just before decorating:
          // Prepare the global scope for decorated symbol table
          tool.initDecoratedGlobalScope(class2mc);
        }, decorated -> {
          // After each decoration, but before generation
          // If required, we also output the symbol table of the *decorated* AST
          var decoratedScope = tool.createSymbolTable(decorated, true);
          
          // Complete the symbol-table (symbol table creation phase 2)
          tool.completeSymbolTable(decorated);
          
          results.add(new TestResult(decorated, decoratedScope));
        }, List.of(cd),

        true);
    
    System.out.println("Wrote CDGenTest results to " + outputDir.getAbsolutePath());
    
    Assertions.assertFalse(results.isEmpty(), "Did not decorate any CD");
    return results.get(0);
  }
  
  public static class TestResult {
    
    private final ASTCDCompilationUnit decoratedCD;
    private final ICD4CodeArtifactScope decoratedScope;
    
    TestResult(ASTCDCompilationUnit decoratedCD, ICD4CodeArtifactScope decoratedScope) {
      this.decoratedCD = decoratedCD;
      this.decoratedScope = decoratedScope;
    }
    
    public ASTCDCompilationUnit getDecoratedCD() { return decoratedCD; }
    
    public ICD4CodeArtifactScope getDecoratedScope() { return decoratedScope; }
    
  }
  
}
