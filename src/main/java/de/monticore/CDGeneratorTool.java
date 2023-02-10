/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTool;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._symboltable.CD4CodeScopesGenitorDelegatorTOP;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.*;

public class CDGeneratorTool extends CD4CodeTool {

  /**
   * main method of the CDGeneratorTool
   *
   * @param args array of the command line arguments
   */
  public static void main(String[] args) {
    CDGeneratorTool tool = new CDGeneratorTool();
    tool.run(args);
  }

  /**
   * executes the arguments stated in the command line like parsing a given model to an ast,
   * creating and printing out a corresponding symbol table, checking cocos or generating java files
   * based of additional configuration templates or handwritten code
   *
   * @param args array of the command line arguments
   */
  public void run(String[] args) {

    Options options = initOptions();

    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);

      if(!cmd.hasOption("i") || cmd.hasOption("h")) {
        printHelp(options);
        return;
      }

      Log.init();
      CD4CodeMill.init();
      if(cmd.hasOption("c2mc")) {
        initializeClass2MC();
      } else {
        BasicSymbolsMill.initializePrimitives();
      }

      Log.enableFailQuick(false);
      ASTCDCompilationUnit ast = parse(cmd.getOptionValue("i"));
      Log.enableFailQuick(true);

      ast = transform(ast);

      if(cmd.hasOption("sym")) {
        MCPath path = new MCPath(cmd.getOptionValue("sym"));
        CD4CodeMill.globalScope().setSymbolPath(path);
      }

      ICD4CodeArtifactScope scope = createSymbolTable(ast, cmd.hasOption("c2mc"));

      if(cmd.hasOption("v")) {
        printVersion();
      }

      if(cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        runCoCos(ast);
        Log.enableFailQuick(true);
      }

      String outputPath = (cmd.hasOption("o")) ? cmd.getOptionValue("o") : "";
      if(cmd.hasOption("s")) {
        storeSymTab(scope, outputPath + cmd.getOptionValue("s"));
      }

      if(cmd.hasOption("gen")) {
        GlobalExtensionManagement glex = new GlobalExtensionManagement();
        glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
        GeneratorSetup setup = new GeneratorSetup();

        // setup default package when generating
        CD4CodeTraverser t = CD4CodeMill.traverser();
        t.add4CDBasis(new CDBasisDefaultPackageTrafo());
        ast.accept(t);

        if(cmd.hasOption("tp")) {
          setup.setAdditionalTemplatePaths(
            Arrays.stream(cmd.getOptionValues("tp"))
              .map(Paths::get)
              .map(Path::toFile)
              .collect(Collectors.toList()));
        }

        if(cmd.hasOption("hwc")) {
          setup.setHandcodedPath(new MCPath(Paths.get(cmd.getOptionValue("hwc"))));
          TopDecorator topDecorator = new TopDecorator(setup.getHandcodedPath());
          ast = topDecorator.decorate(ast);
        }

        setup.setGlex(glex);
        setup.setOutputDirectory(new File(outputPath));

        String configTemplate = cmd.getOptionValue("ct", "cd2java.CD2Java");
        CDGenerator generator = new CDGenerator(setup);
        TemplateController tc = setup.getNewTemplateController(configTemplate);
        TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
        List<Object> configTemplateArgs = Arrays.asList(glex, generator);

        addGettersAndSetters(ast);

        hpp.processValue(tc, ast, configTemplateArgs);
      }

    } catch(ParseException e) {
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
  }

  public void addDefaultImports(ICD4CodeArtifactScope scope, boolean java) {
    if(java) scope.addImports(new ImportStatement("java.lang", true));
  }

  /**
   * adds additional options to the cli tool
   *
   * @param options collection of all the possible options
   */
  public Options addAdditionalOptions(Options options) {

    options.addOption(
      Option.builder("c")
        .longOpt("checkcococs")
        .desc("Checks all CoCos on the given mode.")
        .build());

    options.addOption(
      Option.builder("sym")
        .longOpt("symbolpath")
        .hasArg()
        .argName("symbolpath")
        .desc("Sets the Symbol Path in the global scope.")
        .build());

    options.addOption(
      Option.builder("o")
        .longOpt("output")
        .argName("dir")
        .hasArg()
        .desc("Sets the output path.")
        .build());

    options.addOption(
      Option.builder("gen")
        .longOpt("generate")
        .desc("Generates the java code of the given artifact.")
        .build());

    options.addOption(
      Option.builder("ct")
        .longOpt("configtemplate")
        .hasArg()
        .argName("template")
        .desc("Sets a template for configuration.")
        .build());

    options.addOption(
      Option.builder("tp")
        .longOpt("template")
        .hasArg()
        .argName("path")
        .desc("Sets the path for additional templates.")
        .build());

    options.addOption(
      Option.builder("hwc")
        .longOpt("handwrittencode")
        .hasArg()
        .argName("hwcpath")
        .desc("Sets the path for additional, handwritten classes.")
        .build());

    options.addOption(
      Option.builder("c2mc")
        .longOpt("class2mc")
        .desc("Enables to resolve java classes in the model path")
        .build());

    return options;
  }

  /**
   * checks all cocos on the current ast
   *
   * @param ast the current ast
   */
  protected void runCoCos(ASTCDCompilationUnit ast) {
    CD4CodeCoCoChecker checker = new CD4CodeCoCosDelegator().getCheckerForAllCoCos();
    checker.checkAll(ast);
  }

  /**
   * prints the symboltable of the given ast out to a file
   *
   * @param scope symboltable of the current ast
   * @param path location of the file containing the printed table
   */
  protected void storeSymTab(ICD4CodeArtifactScope scope, String path) {
    CD4CodeSymbols2Json s2j = new CD4CodeSymbols2Json();
    s2j.store(scope, path);
  }

  /**
   * transforms the ast using th
   *
   * @param ast The input AST
   * @return The transformed AST
   */
  protected ASTCDCompilationUnit transform(ASTCDCompilationUnit ast) {
    CD4CodeAfterParseTrafo trafo = new CD4CodeAfterParseTrafo();
    ast.accept(trafo.getTraverser());
    return ast;
  }

  /**
   * creates a symboltable for the current ast using the CD4CodeScopesGenitor
   *
   * @param ast the input ast
   * @return the symboltable of the ast
   */
  public ICD4CodeArtifactScope createSymbolTable(ASTCDCompilationUnit ast, boolean java) {
    CD4CodeScopesGenitorDelegatorTOP genitor = CD4CodeMill.scopesGenitorDelegator();
    ICD4CodeArtifactScope scope = genitor.createFromAST(ast);
    if (ast.isPresentMCPackageDeclaration()) {
      scope.setPackageName(ast.getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    this.addDefaultImports(scope, java);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    return scope;
  }

  protected void initializeClass2MC() {
    CD4CodeMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    CD4CodeMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  /**
   * adds default getter and setter methods to a class for every attribute in case if none have been
   * added so far
   *
   * @param ast the input ast
   */
  public void addGettersAndSetters(ASTCDCompilationUnit ast) {

    CD4C cd4C = CD4C.getInstance();

    for(ASTCDClass c: ast.getCDDefinition().getCDClassesList()) {
      for(ASTCDAttribute attribute: c.getCDAttributeList()) {

        if(c.getCDMethodList().stream()
          .map(ASTCDMethod::getName)
          .noneMatch(n -> n.equalsIgnoreCase(
            (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType()))
              ? "is" + attribute.getName()
              : "get" + attribute.getName()))) {
          cd4C.addMethods(c,attribute, true, false);
        }

        if(c.getCDMethodList().stream()
          .map(ASTCDMethod::getName)
          .noneMatch(n -> n.equalsIgnoreCase("set" + attribute.getName()))) {
          cd4C.addMethods(c,attribute, false, true);
        }
      }
    }
  }
}
