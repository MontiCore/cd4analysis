/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static de.monticore.cdconformance.CDConfParameter.*;
import static de.monticore.cdconformance.CDConfParameter.ALLOW_CARD_RESTRICTION;

import com.fasterxml.jackson.databind.JsonNode;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.json.CD2JsonUtil;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.cocos.CDAssociationUniqueInHierarchy;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisCombinePackagesTrafo;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdconformance.CDConformanceChecker;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.CDFullNameTrafo;
import de.monticore.cddiff.syndiff.SyntaxDiffPrinter;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.io.FileUtils;

public class CD4CodeTool extends de.monticore.cd4code.CD4CodeTool {

  protected static final String PARSE_SUCCESSFUL = "Successfully parsed %s\n";

  protected static final String CHECK_SUCCESSFUL =
      "Successfully checked the CoCos for class " + "diagram %s\n";

  protected static final String CHECK_ERROR = "Error while parsing or CoCo checking";

  protected static final String PLANTUML_SUCCESSFUL = "Creation of plantUML file %s successful\n";

  protected static final String PRETTYPRINT_SUCCESSFUL = "Creation of model file %s successful\n";

  protected static final String JSON_SUCCESSFUL = "Creation of json file %s successful\n";

  protected static final String FILE_EXISTS_INFO =
      "File '%s' already exists and will be " + "overwritten\n";

  protected static final String FILE_CREATION_ERROR = "File '%s' could not be created\n";

  protected static final String STEXPORT_SUCCESSFUL = "Creation of symbol file %s successful\n";

  protected static final String REPORT_SUCCESSFUL = "Reports %s successfully written\n";

  protected static final String INPUT_FILE_NOT_EXISTENT = "Input file '%s' does not exist\n";

  public static final String REPORT_NAME = "report";

  protected String modelName;

  protected String modelFile;

  protected Reader modelReader;

  protected String outputPath;

  protected ASTCDCompilationUnit ast;

  protected ICD4CodeArtifactScope artifactScope;

  protected final CDToolOptions cdToolOptions = new CDToolOptions(true);

  protected final CDToolOptions cdToolOptionsForHelp = new CDToolOptions();

  protected CommandLine cmd;

  protected boolean stopRunEarly;

  @Override
  public void run(String[] args) {
    try {
      if (handleArgs(args)) {
        init();

        if (modelFile != null) {
          ast = parse(modelFile);
        } else {
          ast = parse(modelReader);
        }

        final ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
        boolean useBuiltInTypes = !cmd.hasOption("nt");

        if (cmd.hasOption("merge")) {
          if (useBuiltInTypes) {
            BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
          }
          mergeCDs();
          CD4CodeMill.globalScope().clear();
        }

        if (cmd.hasOption("semdiff")) {
          CDDiffUtil.setUseJavaTypes(true);
          if (useBuiltInTypes) {
            BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
          }
          computeSemDiff();
          CD4CodeMill.globalScope().clear();
        }

        if (cmd.hasOption("syntaxdiff")) {
          CDDiffUtil.setUseJavaTypes(true);
          if (useBuiltInTypes) {
            BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
          }
          computeSyntaxDiff();
          CD4CodeMill.globalScope().clear();
        }

        if (cmd.hasOption("reference")) {
          if (useBuiltInTypes) {
            BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
          }
          checkConformance();
          CD4CodeMill.globalScope().clear();
        }

        new CD4CodeAfterParseTrafo().transform(ast);
        modelName = ast.getCDDefinition().getName();

        // don't output to stdout when the prettyprint is output to stdout
        final boolean doPrintToStdOut = !(cmd.hasOption("pp") && cmd.getOptionValue("pp") == null);

        boolean defaultPackage =
            cmd.hasOption("defaultpackage")
                && Boolean.parseBoolean(cmd.getOptionValue("defaultpackage", "true"));
        if (defaultPackage) {
          final CD4CodeTraverser traverser = CD4CodeMill.traverser();
          final CDBasisCombinePackagesTrafo cdBasis = new CDBasisCombinePackagesTrafo();
          traverser.add4CDBasis(cdBasis);

          ast.accept(traverser);
        }

        if (doPrintToStdOut) {
          String model = modelFile;
          // is null when we read from stdin
          if (model == null) {
            model = "class diagram " + modelName + " from stdin";
          }
          System.out.printf(PARSE_SUCCESSFUL, model);
        }

        if (cmd.hasOption("pp")) { // pretty print
          String ppTarget = cmd.getOptionValue("pp");
          prettyPrint(ast, ppTarget);
        }

        // transformations which are necessary to do after parsing
        {
          new CD4CodeDirectCompositionTrafo().transform(ast);
        }

        // create a symbol table with provided model paths
        String[] modelPath = {"."};
        if (cmd.hasOption("path")) {
          modelPath = cmd.getOptionValues("path");
        }

        artifactScope = createSymbolTable(ast);
        artifactScope.addImports(new ImportStatement("java.lang", true));

        for (String path : modelPath) {
          globalScope.getSymbolPath().addEntry(Paths.get(path));
        }
        if (useBuiltInTypes) {
          BuiltInTypes.addBuiltInTypes(globalScope);
        }
        completeSymbolTable();

        runDefaultCoCos(ast);

        if (cmd.hasOption("fieldfromrole")) {

          // This CoCo checks whether a subclass redefines a role of an association, which is
          // allowed for analysis (e.g. semdiff) but NOT for code-generation
          runGeneratorCoCo();

          switch (cmd.getOptionValue("fieldfromrole")) {
            case "all":
              { // add FieldSymbols for all the CDRoleSymbols
                final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromAllRoles =
                    new CDAssociationCreateFieldsFromAllRoles();
                final CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
                traverser.add4CDAssociation(cdAssociationCreateFieldsFromAllRoles);
                traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromAllRoles);
                cdAssociationCreateFieldsFromAllRoles.transform(ast);
                break;
              }
            case "navigable":
              { // add FieldSymbols only for navigable CDRoleSymbols
                final CDAssociationCreateFieldsFromNavigableRoles
                    cdAssociationCreateFieldsFromNavigableRoles =
                        new CDAssociationCreateFieldsFromNavigableRoles();
                final CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
                traverser.add4CDAssociation(cdAssociationCreateFieldsFromNavigableRoles);
                traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromNavigableRoles);
                cdAssociationCreateFieldsFromNavigableRoles.transform(ast);
                break;
              }
            case "none":
            default:
              // do nothing
          }
        }

        if (doPrintToStdOut) {
          if (Log.getErrorCount() == 0) {
            System.out.printf(CHECK_SUCCESSFUL, modelName);
          } else {
            System.out.println(CHECK_ERROR);
            return;
          }
        }

        if (cmd.hasOption("s")) { // symbol table export
          String targetFile = cmd.getOptionValue("s");

          Path symbolPath;
          if (targetFile == null) {
            if (modelFile != null) {
              symbolPath = Paths.get(Names.getQualifier(modelFile) + ".cdsym");
            } else {
              symbolPath =
                  Paths.get(
                      Names.getPathFromPackage(artifactScope.getPackageName())
                          + File.separator
                          + modelName
                          + ".cdsym");
            }
          } else {
            symbolPath = Paths.get(targetFile);
          }
          storeSymbols(artifactScope, symbolPath.toString());
          if (doPrintToStdOut) {
            System.out.printf(STEXPORT_SUCCESSFUL, unifyPath(symbolPath));
          }
        }

        // report
        if (cmd.hasOption("r")) {
          report(ast, cmd.getOptionValue("r", outputPath));
        }

        if (cmd.hasOption("puml")) { // if option puml is given, then enable the plantuml options
          final CommandLine plantUMLCmd = cdToolOptions.parse(CDToolOptions.SubCommand.PLANTUML);
          final String path = createPlantUML(plantUMLCmd, this.outputPath);
          final String dir = System.getProperty("user.dir");
          String relative = new File(dir).toURI().relativize(new File(path).toURI()).getPath();
          System.out.printf(PLANTUML_SUCCESSFUL, unifyPath(relative));
        }

        // generate .java-files in outputPath
        if (cmd.hasOption("gen")) {

          GlobalExtensionManagement glex = new GlobalExtensionManagement();
          glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

          GeneratorSetup generatorSetup = new GeneratorSetup();

          // setup default package when generating
          CD4CodeTraverser t = CD4CodeMill.traverser();
          t.add4CDBasis(new CDBasisDefaultPackageTrafo());
          ast.accept(t);

          if (cmd.hasOption("fp")) { // Template path
            generatorSetup.setAdditionalTemplatePaths(
                Arrays.stream(cmd.getOptionValues("fp"))
                    .map(Paths::get)
                    .map(Path::toFile)
                    .collect(Collectors.toList()));
          }

          if (cmd.hasOption("hwc")) {
            generatorSetup.setHandcodedPath(new MCPath(Paths.get(cmd.getOptionValue("hwc"))));
            TopDecorator topDecorator = new TopDecorator(generatorSetup.getHandcodedPath());
            ast = topDecorator.decorate(ast);
          }

          generatorSetup.setGlex(glex);
          generatorSetup.setOutputDirectory(new File(outputPath));

          CDGenerator generator = new CDGenerator(generatorSetup);
          String configTemplate = cmd.getOptionValue("ct", "cd2java.CD2Java");
          TemplateController tc = generatorSetup.getNewTemplateController(configTemplate);
          TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
          List<Object> configTemplateArgs = Arrays.asList(glex, generator);
          hpp.processValue(tc, ast, configTemplateArgs);
        }

        if (cmd.hasOption("json")) {
          JsonNode schema = CD2JsonUtil.run(ast, globalScope);

          String filename = "Schema.json";
          {
            File output = Paths.get(this.outputPath, filename).toFile();
            output.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            writer.write(schema.toPrettyString());
            writer.close();
          }
          System.out.printf(JSON_SUCCESSFUL, filename);
        }
      }
    } catch (AmbiguousOptionException e) {
      Log.error(String.format("0xCD0E2: option '%s' can't match any valid option", e.getOption()));
    } catch (UnrecognizedOptionException e) {
      Log.error(String.format("0xCD0E3: unrecognized option '%s'", e.getOption()));
    } catch (MissingOptionException e) {
      Log.error(
          String.format(
              "0xCD0E4: options [%s] are missing, but are required",
              Joiners.COMMA.join(e.getMissingOptions())));
    } catch (MissingArgumentException e) {
      Log.error(String.format("0xCD0E5: option '%s' is missing an argument", e.getOption()));
    } catch (NumberFormatException e) {
      Log.error(
          "0xCD0E6: options --diffsize and --difflimit each require an " + "integer as argument");
    } catch (Exception e) {
      Log.error(String.format("0xCD0E7: an error occurred: %s", e.getMessage()));
    }
  }

  @Override
  public void init() {
    super.init();
    Log.initWARN();
  }

  protected boolean handleArgs(String[] args) throws ParseException {
    cmd = cdToolOptions.handleArgs(args);

    /*if (cmd.hasOption("log")) {
      root.setLevel(Level.toLevel(cmd.getOptionValue("log", DEFAULT_LOG_LEVEL.levelStr),
      DEFAULT_LOG_LEVEL));
    }*/

    outputPath = cmd.getOptionValue("o", ".");

    if (cmd.hasOption("h")) {
      if (cmd.hasOption("puml")) {
        printHelp(CDToolOptions.SubCommand.PLANTUML);
      } else {
        printHelp((CDToolOptions.SubCommand) null);
      }
      return false;
    } else {

      if (!cmd.hasOption("i") && !cmd.hasOption("stdin")) {
        printHelp((CDToolOptions.SubCommand) null);
        Log.error(
            String.format(
                "0xCD014: option '%s' is missing, but an input is required", "[i, stdin]"));
        return false;
      }

      stopRunEarly = false;

      if (cmd.hasOption("i")) {
        modelFile = cmd.getOptionValue("i");

        if (!modelFileExists()) {
          System.out.printf(INPUT_FILE_NOT_EXISTENT, modelFile);
          return false;
        }
      } else {
        modelReader = new BufferedReader(new InputStreamReader(System.in));
      }

      return true;
    }
  }

  public ASTCDCompilationUnit parse(Reader reader) {
    try {
      de.monticore.cd4code._parser.CD4CodeParser parser = de.monticore.cd4code.CD4CodeMill.parser();
      Optional<ASTCDCompilationUnit> optAst = parser.parse(reader);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xCD0E0 Model could not be parsed.");
    } catch (NullPointerException | java.io.IOException e) {
      Log.error("0xCD0E1 Failed to parse from stdin", e);
    }
    // should never be reached (unless failquick is off)
    return null;
  }

  public void completeSymbolTable() {
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
  }

  @Override
  public void runDefaultCoCos(ASTCDCompilationUnit ast) {
    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(ast);
  }

  @Override
  public void report(ASTCDCompilationUnit ast, String path) {
    StringBuilder sb = new StringBuilder();

    if (modelFile != null) {
      sb.append("File ").append(modelFile).append(" parsed:\n");
    } else {
      sb.append("Model ").append(modelName).append(" parsed:\n");
    }

    final List<String> cdPackageList = ast.getCDPackageList();
    sb.append("\nPackages (")
        .append(cdPackageList.size())
        .append("): [")
        .append(Joiners.COMMA.join(cdPackageList))
        .append("]");

    final List<ASTCDClass> cdClassesList = ast.getCDDefinition().getCDClassesList();
    sb.append("\nClasses (")
        .append(cdClassesList.size())
        .append("): [")
        .append(
            Joiners.COMMA.join(
                cdClassesList.stream().map(ASTCDClass::getName).collect(Collectors.toList())))
        .append("]");

    final List<ASTCDInterface> cdInterfacesList = ast.getCDDefinition().getCDInterfacesList();
    sb.append("\nInterface (")
        .append(cdInterfacesList.size())
        .append("): [")
        .append(
            Joiners.COMMA.join(
                cdInterfacesList.stream()
                    .map(ASTCDInterface::getName)
                    .collect(Collectors.toList())))
        .append("]");

    final List<ASTCDEnum> cdEnumsList = ast.getCDDefinition().getCDEnumsList();
    sb.append("\nEnum (")
        .append(cdEnumsList.size())
        .append("): [")
        .append(
            Joiners.COMMA.join(
                cdEnumsList.stream().map(ASTCDEnum::getName).collect(Collectors.toList())))
        .append("]");

    final List<ASTCDAssociation> cdAssociationsList = ast.getCDDefinition().getCDAssociationsList();
    sb.append("\nAssociations (")
        .append(cdAssociationsList.size())
        .append("): [")
        .append(
            Joiners.COMMA.join(
                cdAssociationsList.stream()
                    .map(
                        a -> {
                          final String name;
                          if (a.isPresentName()) {
                            name = a.getName();
                          } else {
                            name = a.getPrintableName();
                          }
                          return name;
                        })
                    .collect(Collectors.toList())))
        .append("]");

    final Path allElementsPath = Paths.get(path, REPORT_NAME + "." + modelName);
    if (Files.notExists(allElementsPath.getParent())) {
      try {
        Files.createDirectories(allElementsPath.getParent());
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }
    try (PrintWriter out = new PrintWriter(allElementsPath.toFile())) {
      out.println(sb);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return;
    }

    System.out.printf(
        REPORT_SUCCESSFUL,
        Joiners.COMMA.join(Collections.singletonList(unifyPath(allElementsPath))));
  }

  private String unifyPath(Path path) {
    return unifyPath(path.toString());
  }

  private String unifyPath(String path) {
    return path.replace("\\", "/");
  }

  @Override
  public void prettyPrint(ASTCDCompilationUnit ast, String ppTarget) {
    CD4CodeFullPrettyPrinter cd4CodeFullPrettyPrinter =
        new CD4CodeFullPrettyPrinter(new IndentPrinter());
    ast.accept(cd4CodeFullPrettyPrinter.getTraverser());
    if (ppTarget == null) {
      System.out.println(cd4CodeFullPrettyPrinter.getPrinter().getContent());
    } else {
      final Path outputPath;
      if (Paths.get(ppTarget).isAbsolute()) {
        outputPath = Paths.get(ppTarget);
      } else {
        if (this.outputPath.isEmpty()) {
          outputPath = Paths.get(ppTarget);
        } else {
          outputPath = Paths.get(this.outputPath, ppTarget);
        }
      }
      final File file = outputPath.toFile();
      if (file.exists()) {
        System.out.printf(FILE_EXISTS_INFO, unifyPath(file.getPath()));
      }
      try {
        // Write results into a file
        FileUtils.writeStringToFile(
            file, cd4CodeFullPrettyPrinter.getPrinter().getContent(), Charset.defaultCharset());
      } catch (IOException e) {
        System.out.println(cd4CodeFullPrettyPrinter.getPrinter().getContent());
        System.out.printf(FILE_CREATION_ERROR, file.getAbsolutePath());
      }
      System.out.printf(PRETTYPRINT_SUCCESSFUL, file.getAbsolutePath());
    }
  }

  protected String createPlantUML(CommandLine plantUMLCmd, String outputPath) throws IOException {
    final String output =
        Paths.get(outputPath, cmd.getOptionValue("puml", modelName)).toUri().getPath();

    final PlantUMLConfig plantUMLConfig = new PlantUMLConfig();

    // the following options are set, when they are provided
    if (plantUMLCmd.hasOption("showAttributes")) {
      plantUMLConfig.setShowAtt(true);
    }
    if (plantUMLCmd.hasOption("showAssociations")) {
      plantUMLConfig.setShowAssoc(true);
    }
    if (plantUMLCmd.hasOption("showRoles")) {
      plantUMLConfig.setShowRoles(true);
    }
    if (plantUMLCmd.hasOption("showCardinality")) {
      plantUMLConfig.setShowCard(true);
    }
    if (plantUMLCmd.hasOption("showModifier")) {
      plantUMLConfig.setShowModifier(true);
    }
    if (plantUMLCmd.hasOption("orthogonal")) {
      plantUMLConfig.setOrtho(true);
    }
    if (plantUMLCmd.hasOption("shortenWords")) {
      plantUMLConfig.setShortenWords(true);
    }
    if (plantUMLCmd.hasOption("showComments")) {
      plantUMLConfig.setShowComments(true);
    }

    if (plantUMLCmd.hasOption("nodeSeparator")) {
      plantUMLConfig.setNodesep(Integer.parseInt(plantUMLCmd.getOptionValue("nodesep", "-1")));
    }
    if (plantUMLCmd.hasOption("rankSeparator")) {
      plantUMLConfig.setRanksep(Integer.parseInt(plantUMLCmd.getOptionValue("ranksep", "-1")));
    }

    if (plantUMLCmd.hasOption("svg")) {
      return PlantUMLUtil.printCD2PlantUMLLocally(
          Optional.ofNullable(ast),
          output.endsWith(".svg") ? output : output + ".svg",
          plantUMLConfig);
    } else {
      return PlantUMLUtil.printCD2PlantUMLModelFileLocally(
          Optional.ofNullable(ast),
          output.endsWith(".puml") ? output : output + ".puml",
          plantUMLConfig);
    }
  }

  protected boolean modelFileExists() {
    Path filePath = Paths.get(modelFile);
    return Files.exists(filePath);
  }

  protected void printHelp(CDToolOptions.SubCommand subCommand) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(110);
    formatter.printHelp(
        "Examples in case the Tool file is called MCCD.jar: "
            + System.lineSeparator()
            + "java -jar MCCD.jar -i Person.cd --path target:src/models -o target/out -t true -s"
            + System.lineSeparator()
            + "java -jar MCCD.jar -i src/Person.cd -pp target/Person.cd"
            + System.lineSeparator()
            + "",
        cdToolOptionsForHelp.getOptions());

    if (subCommand != null) {
      formatter.printHelp(subCommand.toString(), cdToolOptionsForHelp.getOptions(subCommand));
    }
    System.out.println("Further details: https://www.se-rwth.de/topics/");
  }

  protected void computeSemDiff() throws NumberFormatException, IOException {

    // clone the current CD
    ASTCDCompilationUnit ast1 = ast.deepClone();

    // parse the second .cd-file
    ASTCDCompilationUnit ast2 = parse(cmd.getOptionValue("semdiff"));

    if (ast2 == null) {
      Log.error("0xCDD14: Failed to load CDs for `--semdiff`.");
      return;
    }

    // use fully qualified names for attributes and associations
    new CDFullNameTrafo().transform(ast1);
    new CDFullNameTrafo().transform(ast2);

    ast1 = ast1.deepClone();
    ast2 = ast2.deepClone();

    // determine the diffsize, default is max(20,2*(|Classes|+|Interfaces|))
    int diffsize = CDDiff.getDefaultDiffsize(ast1, ast2);
    String defaultVal = Integer.toString(diffsize);
    if (cmd.hasOption("diffsize") && cmd.getOptionValue("diffsize") != null) {
      diffsize = Integer.parseInt(cmd.getOptionValue("diffsize", defaultVal));
    }
    int difflimit = Integer.parseInt(cmd.getOptionValue("difflimit", "1"));

    boolean openWorld = cmd.hasOption("open-world");

    if (cmd.hasOption("rule-based")) {
      CDDiff.computeRuleBasedSemDiff(ast1, ast2, outputPath, openWorld, cmd.hasOption("o"));
    } else {
      boolean reductionBased =
          !(cmd.hasOption("open-world")
              && cmd.getOptionValue("open-world", "reduction-based").equals("alloy-based"));
      CDDiff.computeSemDiff(
          ast1,
          ast2,
          outputPath,
          diffsize,
          difflimit,
          openWorld,
          reductionBased,
          cmd.hasOption("o"));
    }
  }

  /** perform syntactic comparison analysis of 2 CDs */
  protected void computeSyntaxDiff() {

    // clone the current CD
    ASTCDCompilationUnit ast1 = ast.deepClone();

    // parse the second .cd-file
    ASTCDCompilationUnit ast2 = parse(cmd.getOptionValue("syntaxdiff"));

    if (ast2 == null) {
      Log.error("0xCDD15: Failed to load CDs for `--syntaxdiff`.");
      return;
    }

    ast1 = ast1.deepClone();
    ast2 = ast2.deepClone();

    new CD4CodeDirectCompositionTrafo().transform(ast1);
    new CD4CodeDirectCompositionTrafo().transform(ast2);
    CDDiffUtil.refreshSymbolTable(ast1);
    CDDiffUtil.refreshSymbolTable(ast2);

    SyntaxDiffPrinter syntaxDiff = new SyntaxDiffPrinter(ast1, ast2);

    String printOption = cmd.getOptionValue("show", "diff");
    if (printOption.equals("added")) {
      System.out.println(syntaxDiff.printOnlyAdded());
    }
    if (printOption.equals("diff")) {
      System.out.println(syntaxDiff.printDiff());
    }
    if (printOption.equals("new")) {
      System.out.println(syntaxDiff.printSrcCD());
    }
    if (printOption.equals("old")) {
      System.out.println(syntaxDiff.printTgtCD());
    }
    if (printOption.equals("deleted")) {
      System.out.println(syntaxDiff.printOnlyDeleted());
    }
    if (printOption.equals("changed")) {
      System.out.println(syntaxDiff.printOnlyChanged());
    }
    if (printOption.equals("both")) {
      System.out.println(syntaxDiff.printSrcCD());
      System.out.println(syntaxDiff.printTgtCD());
    }
  }

  /** perform a conformance check */
  protected void checkConformance() {
    ASTCDCompilationUnit con = ast.deepClone();
    new CD4CodeDirectCompositionTrafo().transform(con);
    CDDiffUtil.refreshSymbolTable(con);
    ASTCDCompilationUnit ref = parse(cmd.getOptionValue("reference"));
    List<String> mappings = List.of("incarnates");
    if (cmd.hasOption("map")) {
      mappings = List.of(cmd.getOptionValues("map"));
    }
    if (ref != null) {
      new CD4CodeDirectCompositionTrafo().transform(ref);
      CDDiffUtil.refreshSymbolTable(ref);
      new CDConformanceChecker(
              Set.of(
                  STEREOTYPE_MAPPING,
                  NAME_MAPPING,
                  SRC_TARGET_ASSOC_MAPPING,
                  INHERITANCE,
                  ALLOW_CARD_RESTRICTION))
          .checkConformance(con, ref, new LinkedHashSet<>(mappings));
    }
  }

  /** perform merge of 2 CDs */
  public void mergeCDs() {
    Set<ASTCDCompilationUnit> mergeSet = new HashSet<>();
    mergeSet.add(ast);
    mergeSet.add(parse(cmd.getOptionValue("merge")));

    String cdName = "Merge.cd";
    if (cmd.hasOption("pp")
        && cmd.getOptionValue("pp") != null
        && cmd.getOptionValue("pp").endsWith(".cd")) {
      cdName = Path.of(cmd.getOptionValue("pp")).getFileName().toString();
    }
    cdName = cdName.substring(0, cdName.length() - 3);

    Set<MergeParameter> paramSet = new HashSet<>();
    paramSet.add(MergeParameter.LOG_TO_CONSOLE);
    ASTCDCompilationUnit mergeResult = CDMerge.merge(mergeSet, cdName, paramSet);

    if (mergeResult != null) {
      ast = mergeResult;
    } else {
      Log.error("0xCDD16 Could not merge CDs.");
    }
  }

  /** Additional CoCo-Check for Code-Generation */
  protected void runGeneratorCoCo() {
    CD4AnalysisCoCos generalCoCos = new CD4AnalysisCoCos();
    CD4AnalysisCoCoChecker checker = generalCoCos.createNewChecker();

    checker.addCoCo(new CDAssociationUniqueInHierarchy());
    checker.checkAll(ast);
  }

  /* generated by template core.Method*/
  public static void main(String[] args) {
    /* generated by template _cli.Main*/

    CD4CodeTool tool = new CD4CodeTool();
    tool.run(args);
  }
}
