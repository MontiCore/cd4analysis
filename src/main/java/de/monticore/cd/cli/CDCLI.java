/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cli;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.*;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cdassociation.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDCLI {

  static final Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  protected static final String PARSE_SUCCESSFUL = "Successfully parsed %s\n";
  protected static final String CHECK_SUCCESSFUL = "Successfully checked the CoCos for class diagram %s\n";
  protected static final String CHECK_ERROR = "Error while parsing or CoCo checking";
  protected static final String PLANTUML_SUCCESSFUL = "Creation of plantUML file %s successful\n";
  protected static final String PRETTYPRINT_SUCCESSFUL = "Creation of model file %s successful\n";
  protected static final String DIR_CREATION_ERROR = "Directory '%s' could not be created\n";
  protected static final String STEXPORT_SUCCESSFUL = "Creation of symbol file %s successful\n";
  protected static final String REPORT_SUCCESSFUL = "Reports %s successfully written\n";
  protected static final String INPUT_FILE_NOT_EXISTENT = "Input file '%s' does not exist\n";

  public static final String REPORT_NAME = "report";

  protected String modelName;
  protected String modelFile;
  protected Reader modelReader;
  protected boolean failQuick = false;
  protected String outputPath;
  protected ASTCDCompilationUnit ast;
  protected ICD4CodeArtifactScope artifactScope;
  protected final CDCLIOptions cdcliOptions = new CDCLIOptions(true);
  protected final CDCLIOptions cdcliOptionsForHelp = new CDCLIOptions();
  protected CommandLine cmd;

  protected CDCLI() {
  }

  public static void main(String[] args) throws IOException, ParseException {

    // disable debug messages
    Log.initWARN();

    CDCLI cli = new CDCLI();
    try {
      if (cli.handleArgs(args)) {
        Log.enableFailQuick(cli.failQuick);
        cli.run();
      }
    }
    catch (AmbiguousOptionException e) {
      Log.error(String.format("0xCD010: option '%s' can't match any valid option", e.getOption()));
    }
    catch (UnrecognizedOptionException e) {
      Log.error(String.format("0xCD011: unrecognized option '%s'", e.getOption()));
    }
    catch (MissingOptionException e) {
      Log.error(String.format("0xCD012: options [%s] are missing, but are required", Joiners.COMMA.join(e.getMissingOptions())));
    }
    catch (MissingArgumentException e) {
      Log.error(String.format("0xCD013: option '%s' is missing an argument", e.getOption()));
    }
  }

  protected boolean handleArgs(String[] args)
      throws IOException, ParseException {
    cmd = cdcliOptions.handleArgs(args);

    /*if (cmd.hasOption("log")) {
      root.setLevel(Level.toLevel(cmd.getOptionValue("log", DEFAULT_LOG_LEVEL.levelStr), DEFAULT_LOG_LEVEL));
    }*/

    failQuick = cmd.hasOption("f") && Boolean.parseBoolean(cmd.getOptionValue("f", "true"));

    outputPath = cmd.getOptionValue("o", ".");

    if (cmd.hasOption("h")) {
      if (cmd.hasOption("puml")) {
        printHelp(CDCLIOptions.SubCommand.PLANTUML);
      }
      else {
        printHelp(null);
      }
      return false;
    }
    else {
      if (!cmd.hasOption("i") && !cmd.hasOption("stdin")) {
        printHelp(null);
        Log.error(String.format("0xCD014: option '%s' is missing, but an input is required", "[i, stdin]"));
        return false;
      }

      if (cmd.hasOption("i")) {
        modelFile = cmd.getOptionValue("i");

        if (!modelFileExists()) {
          System.out.printf(INPUT_FILE_NOT_EXISTENT, modelFile);
          return false;
        }
      }
      else {
        modelReader = new BufferedReader(new InputStreamReader(System.in));
      }

      return true;
    }
  }

  protected void run() throws IOException, ParseException {
    CD4CodeMill.reset();
    CD4CodeMill.init();

    parse();

    // don't output to stdout when the prettyprint is output to stdout
    final boolean doPrintToStdOut = !(cmd.hasOption("pp") && cmd.getOptionValue("pp") == null);

    boolean defaultPackage = cmd.hasOption("defaultpackage") && Boolean.parseBoolean(cmd.getOptionValue("defaultpackage", "true"));
    if (defaultPackage) {
      final CD4CodeTraverser traverser = CD4CodeMill.traverser();
      final CDBasisDefaultPackageTrafo cdBasis = new CDBasisDefaultPackageTrafo();
      traverser.add4CDBasis(cdBasis);
      traverser.setCDBasisHandler(cdBasis);
      cdBasis.setTraverser(traverser);

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

    final CD4CodeFullPrettyPrinter cd4CodeFullPrettyPrinter = new CD4CodeFullPrettyPrinter();
    if (cmd.hasOption("pp")) { // pretty print
      ast.accept(cd4CodeFullPrettyPrinter.getTraverser());
    }

    // transformations which are necessary to do after parsing
    {
      new CD4CodeDirectCompositionTrafo().transform(ast);
    }

    boolean useBuiltInTypes = !cmd.hasOption("t") || Boolean.parseBoolean(cmd.getOptionValue("t", "false"));

    // create a symbol table with provided model paths
    String[] modelPath = { "." };
    if (cmd.hasOption("path")) {
      modelPath = cmd.getOptionValues("path");
    }
    createSymTab(useBuiltInTypes, new ModelPath(Arrays.stream(modelPath).map(Paths::get).collect(Collectors.toSet())));

    // transformations that need an already created symbol table
    {
      final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo();
      final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
      traverser.add4CDAssociation(cdAssociationRoleNameTrafo);
      traverser.setCDAssociationHandler(cdAssociationRoleNameTrafo);
      cdAssociationRoleNameTrafo.transform(ast);
    }

    if (cmd.hasOption("fieldfromrole")) {
      switch (cmd.getOptionValue("fieldfromrole")) {
        case "all": { // add FieldSymbols for all the CDRoleSymbols
          final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromAllRoles = new CDAssociationCreateFieldsFromAllRoles();
          final CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
          traverser.add4CDAssociation(cdAssociationCreateFieldsFromAllRoles);
          traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromAllRoles);
          cdAssociationCreateFieldsFromAllRoles.transform(ast);
          break;
        }
        case "navigable": { // add FieldSymbols only for navigable CDRoleSymbols
          final CDAssociationCreateFieldsFromNavigableRoles cdAssociationCreateFieldsFromNavigableRoles = new CDAssociationCreateFieldsFromNavigableRoles();
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

    checkCocos();
    if (doPrintToStdOut) {
      if (Log.getErrorCount() == 0) {
        System.out.printf(CHECK_SUCCESSFUL, modelName);
      }
      else {
        System.out.println(CHECK_ERROR);
        return;
      }
    }

    if (cmd.hasOption("s")) { // symbol table export
      String targetFile = cmd.getOptionValue("s");

      Path symbolPath;
      if (targetFile == null) {
        if (modelFile != null) {
          symbolPath = Paths.get(Names.getQualifier(modelFile) + ".sym");
        }
        else {
          symbolPath = Paths.get(Names.getPathFromPackage(artifactScope.getRealPackageName()) + File.separator + modelName + ".sym");
        }
      }
      else {
        symbolPath = Paths.get(targetFile);
      }
      final CD4CodeSymbols2Json symbols2Json = new CD4CodeSymbols2Json();
      final String path = symbols2Json.store(artifactScope, symbolPath.toString());
      if (doPrintToStdOut) {
        System.out.printf(STEXPORT_SUCCESSFUL, unifyPath(symbolPath));
      }
    }

    // report
    if (cmd.hasOption("r")) {
      report(ast, cmd.getOptionValue("r", outputPath));
    }

    if (cmd.hasOption("pp")) { // pretty print
      String ppTarget = cmd.getOptionValue("pp");

      if (ppTarget == null) {
        System.out.println(cd4CodeFullPrettyPrinter.getPrinter().getContent());
      }
      else {
        final Path outputPath;
        if (Paths.get(ppTarget).isAbsolute()) {
          outputPath = Paths.get(ppTarget);
        }
        else {
          outputPath = Paths.get(this.outputPath, ppTarget);
        }
        final File file = outputPath.toFile();
        if (!file.getParentFile().mkdirs()) {
          System.out.printf(DIR_CREATION_ERROR, file.getAbsolutePath());
          return;
        }

        try (PrintWriter out = new PrintWriter(new FileOutputStream(file), true)) {
          out.println(cd4CodeFullPrettyPrinter.getPrinter().getContent());
        }
        System.out.printf(PRETTYPRINT_SUCCESSFUL, unifyPath(file.toPath()));
      }
    }

    if (cmd.hasOption("puml")) { // if option puml is given, then enable the plantuml options
      final CommandLine plantUMLCmd = cdcliOptions.parse(CDCLIOptions.SubCommand.PLANTUML);
      final String path = createPlantUML(plantUMLCmd, this.outputPath);
      final String dir = System.getProperty("user.dir");
      String relative = new File(dir).toURI().relativize(new File(path).toURI()).getPath();
      System.out.printf(PLANTUML_SUCCESSFUL, unifyPath(relative));
    }
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  protected void parse() throws IOException {
    final CD4CodeParser parser = new CD4CodeParser();
    final Optional<ASTCDCompilationUnit> cu;
    if (modelFile != null) {
      cu = parser.parse(modelFile);
    }
    else {
      cu = parser.parse(modelReader);
    }
    ast = cu.get();

    modelName = ast.getCDDefinition().getName();
  }

  protected void createSymTab(boolean useBuiltInTypes, ModelPath modelPath) {
    final ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    globalScope.clear();
    globalScope.setModelPath(modelPath);
    if (useBuiltInTypes && globalScope instanceof CD4CodeGlobalScope) {
      ((CD4CodeGlobalScope) globalScope).addBuiltInTypes();
    }

    final CD4CodeScopesGenitorDelegator cd4CodeScopesGenitorDelegator = CD4CodeMill.scopesGenitorDelegator();
    artifactScope = cd4CodeScopesGenitorDelegator.createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
  }

  protected void checkCocos() {
    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(ast);
  }

  protected String createPlantUML(CommandLine plantUMLCmd, String outputPath)
      throws IOException {
    final String output = Paths.get(outputPath, cmd.getOptionValue("puml", modelName)).toUri().getPath();

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
      return PlantUMLUtil.printCD2PlantUMLLocally(Optional.ofNullable(ast), output.endsWith(".svg") ? output : output + ".svg", plantUMLConfig);
    }
    else {
      return PlantUMLUtil.printCD2PlantUMLModelFileLocally(Optional.ofNullable(ast), output.endsWith(".puml") ? output : output + ".puml", plantUMLConfig);
    }
  }

  protected boolean modelFileExists() {
    Path filePath = Paths.get(modelFile);
    return Files.exists(filePath);
  }

  protected void printHelp(CDCLIOptions.SubCommand subCommand) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(110);
    formatter.printHelp("Examples in case the CLI file is called CDCLI.jar: " + System.lineSeparator() + "java -jar CDCLI.jar -i Person.cd --path target:src/models -o target/out -t true -s" + System.lineSeparator() + "java -jar CDCLI.jar -i src/Person.cd -pp target/Person.cd", cdcliOptionsForHelp.getOptions());

    if (subCommand != null) {
      formatter.printHelp(subCommand.toString(), cdcliOptionsForHelp.getOptions(subCommand));
    }
  }

  protected void report(ASTCDCompilationUnit ast, String path) {
    StringBuilder sb = new StringBuilder();

    if (modelFile != null) {
      sb.append("File ").append(modelFile).append(" parsed:\n");
    }
    else {
      sb.append("Model ").append(modelName).append(" parsed:\n");
    }

    final List<String> cdPackageList = ast.getCDPackageList();
    sb.append("\nPackages (").append(cdPackageList.size()).append("): [").append(Joiners.COMMA.join(cdPackageList)).append("]");

    final List<ASTCDClass> cdClassesList = ast.getCDDefinition().getCDClassesList();
    sb.append("\nClasses (").append(cdClassesList.size()).append("): [").append(Joiners.COMMA.join(cdClassesList.stream().map(ASTCDClass::getName).collect(Collectors.toList()))).append("]");

    final List<ASTCDInterface> cdInterfacesList = ast.getCDDefinition().getCDInterfacesList();
    sb.append("\nInterface (").append(cdInterfacesList.size()).append("): [").append(Joiners.COMMA.join(cdInterfacesList.stream().map(ASTCDInterface::getName).collect(Collectors.toList()))).append("]");

    final List<ASTCDEnum> cdEnumsList = ast.getCDDefinition().getCDEnumsList();
    sb.append("\nEnum (").append(cdEnumsList.size()).append("): [").append(Joiners.COMMA.join(cdEnumsList.stream().map(ASTCDEnum::getName).collect(Collectors.toList()))).append("]");

    final List<ASTCDAssociation> cdAssociationsList = ast.getCDDefinition().getCDAssociationsList();
    sb.append("\nAssociations (").append(cdAssociationsList.size()).append("): [").append(Joiners.COMMA.join(cdAssociationsList.stream().map(a -> {
      final String name;
      if (a.isPresentName()) {
        name = a.getName();
      }
      else {
        name = a.getPrintableName();
      }
      return name;
    }).collect(Collectors.toList()))).append("]");

    final Path allElementsPath = Paths.get(path, REPORT_NAME + "." + modelName);
    if (Files.notExists(allElementsPath.getParent())) {
      try {
        Files.createDirectories(allElementsPath.getParent());
      }
      catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }
    try (PrintWriter out = new PrintWriter(allElementsPath.toFile())) {
      out.println(sb.toString());
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      return;
    }

    System.out.printf(REPORT_SUCCESSFUL, Joiners.COMMA.join(Collections.singletonList(unifyPath(allElementsPath))));
  }

  private String unifyPath(Path path) {
    return unifyPath(path.toString());
  }

  private String unifyPath(String path) {
    return path.replace("\\", "/");
  }
}
