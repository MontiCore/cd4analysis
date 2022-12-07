/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeScopesGenitorDelegatorTOP;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.cli.updateChecker.UpdateCheckerRunnable;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class CDGeneratorTool {

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
      if (cmd.hasOption("c2mc")) {
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

      ICD4CodeArtifactScope scope = createSymbolTable(ast);

      if(cmd.hasOption("v")) {
        printVersion(ast);
      }

      if(cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        runCoCos(ast);
        Log.enableFailQuick(true);
      }

      String outputPath = (cmd.hasOption("o")) ? cmd.getOptionValue("o") : "";
      if (cmd.hasOption("s")) {
        storeSymTab(scope, outputPath + cmd.getOptionValue("s"));
      }

      if (cmd.hasOption("gen")) {
        GlobalExtensionManagement glex = new GlobalExtensionManagement();
        glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
        GeneratorSetup setup = new GeneratorSetup();

        if (cmd.hasOption("tp")) {
          setup.setAdditionalTemplatePaths(
              Arrays.stream(cmd.getOptionValues("tp"))
                  .map(Paths::get)
                  .map(Path::toFile)
                  .collect(Collectors.toList()));
        }

        if (cmd.hasOption("hwc")) {
          setup.setHandcodedPath(new MCPath(Paths.get(cmd.getOptionValue("hwc"))));
          TopDecorator topDecorator = new TopDecorator(setup.getHandcodedPath());
          ast = topDecorator.decorate(ast);
        }

        setup.setGlex(glex);
        setup.setOutputDirectory(new File(outputPath));

        CDGenerator generator = new CDGenerator(setup);
        String configTemplate = cmd.getOptionValue("ct", "cd2java.CD2Java");
        TemplateController tc = setup.getNewTemplateController(configTemplate);
        TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
        List<Object> configTemplateArgs = Arrays.asList(glex, generator);
        hpp.processValue(tc, ast, configTemplateArgs);
      }

    } catch (ParseException e) {
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
  }

  /**
   * initializes the input options
   *
   * @return the collection of possible command options
   */
  protected Options initOptions() {
    Options options = new Options();
    addOptions(options);
    return options;
  }

  /**
   * adds additional options to the cli tool
   *
   * @param options collection of all the possible options
   */
  protected void addOptions(org.apache.commons.cli.Options options) {

    options.addOption(
      Option.builder("h")
        .longOpt("help")
        .desc("Prints out the help options")
        .build());

    options.addOption(
      Option.builder("i")
        .longOpt("input")
        .argName("file")
        .hasArg()
        .desc("Reads the source file (mandatory) and parses the contents")
        .build());

    options.addOption(
        Option.builder("c")
            .longOpt("checkcococs")
            .desc("Checks all CoCos on the given mode.")
            .build());

    options.addOption(
        Option.builder("s")
            .longOpt("symboltable")
            .argName("file")
            .hasArg()
            .desc("Serialized the Symbol table of the given artifact.")
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

    options.addOption(
      Option.builder("v")
        .longOpt("version")
        .desc("Prints out the current version")
        .build());
  }

  /**
   * prints out all options as well as their description in case of there bein no input model
   *
   * @param options collection of all options of commands line inputs
   */
  protected void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("CDGeneratorTool", options);
  }

  /**
   * parses the input model into an ast
   *
   * @param model the location of the file containing the model
   * @return an ast representation of the input model
   */
  protected ASTCDCompilationUnit parse(String model) {
    try {
      CD4CodeParser parser = CD4CodeMill.parser();
      Optional<ASTCDCompilationUnit> optAST = parser.parse(model);

      if (!parser.hasErrors() && optAST.isPresent()) {
        return optAST.get();
      }
      Log.error("0xA1051 Model could not be parsed");

    } catch (NullPointerException | IOException e) {
      Log.error("0xA1051 Failed to parse " + model, e);
    }
    return null;
  }

  /**
   * creates a symboltable for the current ast using the CD4CodeScopesGenitor
   *
   * @param ast the current ast
   * @return the symboltable of the ast
   */
  protected ICD4CodeArtifactScope createSymbolTable(ASTCDCompilationUnit ast) {
    CD4CodeScopesGenitorDelegatorTOP genitor = CD4CodeMill.scopesGenitorDelegator();
    ICD4CodeArtifactScope scope = genitor.createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    return scope;
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

  protected ASTCDCompilationUnit transform(ASTCDCompilationUnit ast) {
    CD4CodeAfterParseTrafo trafo = new CD4CodeAfterParseTrafo();
    ast.accept(trafo.getTraverser());
    return ast;
  }

  protected void initializeClass2MC() {
    CD4CodeMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    CD4CodeMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  protected void printVersion(ASTCDCompilationUnit ast) {
    String mcVersion = new UpdateCheckerRunnable().getLocalVersion();
    System.out.println("version " + mcVersion);
  }
}
