/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.fail;

public class CDGeneratorTest extends CD4CodeTestBasis {

  private static final String MODEL_PATH = "src/test/resources/";

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit compUnit;

  @Before
  public void initGlex() {
    compUnit = parse("de.monticore.cd.codegen.GenAuction");
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    this.glex.setGlobalValue("cd4c", CD4C.getInstance());
  }


  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDClass clazz : compUnit.getCDDefinition().getCDClassesList()) {
      StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, clazz, clazz);
      // test parsing
      ParserConfiguration configuration = new ParserConfiguration();
      JavaParser parser = new JavaParser(configuration);
      ParseResult parseResult = parser.parse(sb.toString());
      Assert.assertTrue(parseResult.isSuccessful());
    }
  }

  public ASTCDCompilationUnit parse(String name) {
    String qualifiedName = name.replace(".", "/");

    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> ast = Optional.empty();
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
/*
    ASTCDCompilationUnit comp = ast.get();
    new CD4CodeAfterParseTrafo().transform(ast.get());

    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(comp);
    comp.getEnclosingScope().setAstNode(comp);
    String packageName = Joiners.DOT.join(comp.getCDPackageList());
    scope.getLocalDiagramSymbols().forEach(s -> s.setPackageName(packageName));
    List<ImportStatement> imports = Lists.newArrayList();
    comp.getMCImportStatementList().forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    scope.setImportsList(imports);
    scope.setPackageName(packageName);
    for (ASTMCImportStatement imp : comp.getMCImportStatementList()) {
      if (!CD4CodeMill.globalScope().resolveDiagram(imp.getQName()).isPresent()) {
        parse(imp.getMCQualifiedName().getQName());
      }
    }

 */
    return ast.get();
  }
}
