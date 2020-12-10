/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cli;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeScopeDeSer;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CDCLI {

  static final Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  protected static final String CHECK_SUCCESSFUL = "Successfully checked the CoCos for ";
  protected static final String CHECK_ERROR = "Error while parsing or CoCo checking";
  protected static final String PLANTUML_SUCCESSFUL = "Creation of plantUML file %s successful\n";
  protected static final String PRETTYPRINT_SUCCESSFUL = "Creation of model file %s successful\n";
  protected static final String STEXPORT_SUCCESSFUL = "Creation of symbol file %s successful\n";
  protected static final String REPORT_SUCCESSFUL = "Reports %s successfully written\n";

  public static final String REPORT_ALL_ELEMENTS = "allElements.txt";

  protected String modelName;
  protected String modelFile;
  protected Reader modelReader;
  protected boolean failQuick = false;
  protected String outputPath;
  protected ASTCDCompilationUnit ast;
  protected ICD4CodeArtifactScope artifactScope;
  protected final CDCLIOptions cdcliOptions = new CDCLIOptions();
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

  protected boolean handleArgs(String[] args) throws IOException, ParseException {
    cmd = cdcliOptions.handleArgs(args);

    /*if (cmd.hasOption("log")) {
      root.setLevel(Level.toLevel(cmd.getOptionValue("log", DEFAULT_LOG_LEVEL.levelStr), DEFAULT_LOG_LEVEL));
    }*/

    failQuick = Boolean.parseBoolean(cmd.getOptionValue("f", "false"));

    outputPath = cmd.getOptionValue("o", ".");

    if (cmd.hasOption("h")) {
      if (cmd.hasOption("prettyprint")) {
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
          throw new NoSuchFileException(modelFile);
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

    System.out.println("Successfully parsed " + ast.getCDDefinition().getName());

    boolean useBuiltInTypes = !cmd.hasOption("t") || Boolean.parseBoolean(cmd.getOptionValue("t", "true"));

    // create a symbol table with provided model paths
    String[] modelPath = cmd.getOptionValue("p", ".").split(";");
    createSymTab(useBuiltInTypes, new ModelPath(Arrays.stream(modelPath).map(Paths::get).collect(Collectors.toSet())));

    // check all the cocos
    checkCocos();
    if (Log.getErrorCount() == 0) {
      System.out.println(CHECK_SUCCESSFUL + ast.getCDDefinition().getName());
    }
    else {
      System.out.println(CHECK_ERROR);
      return;
    }

    if (cmd.hasOption("prettyprint")) { // pretty print
      String ppOptionVal = cmd.getOptionValue("prettyprint");

      if (!cmd.hasOption("plantUML")) {
        // print model

        final CD4CodePrettyPrinter cd4CodePrettyPrinter = CD4CodeMill.cD4CodePrettyPrinter();
        ast.accept(cd4CodePrettyPrinter);

        if (ppOptionVal == null) {
          System.out.println(cd4CodePrettyPrinter.getPrinter().getContent());
        }
        else {
          final Path outputPath = Paths.get(this.outputPath, ppOptionVal);
          final File file = outputPath.toFile();
          file.getParentFile().mkdirs();

          try (PrintWriter out = new PrintWriter(new FileOutputStream(file), true)) {
            out.println(cd4CodePrettyPrinter.getPrinter().getContent());
            System.out.printf(PRETTYPRINT_SUCCESSFUL, file);
          }
        }
      }
      else { // if option puml is given, then enable the plantuml options
        final CommandLine plantUMLCmd = cdcliOptions.parse(CDCLIOptions.SubCommand.PLANTUML);
        final String path = createPlantUML(plantUMLCmd, this.outputPath);
        System.out.printf(PLANTUML_SUCCESSFUL, path);
      }
    }

    if (cmd.hasOption("s")) { // symbol table export
      @SuppressWarnings("UnstableApiUsage") final List<String> artifactPackage = new ArrayList<>(Splitters.DOT.splitToList(artifactScope.getRealPackageName()));

      String targetFile = cmd.getOptionValue("s");

      Path symbolPath;
      if (targetFile == null) {
        symbolPath = Paths.get(Names.getQualifier(modelFile) + ".cdsym");
      }
      else {
        symbolPath = Paths.get(targetFile);
      }
      final CD4CodeScopeDeSer deser = CD4CodeMill.cD4CodeScopeDeSer();
      final String path = deser.store(artifactScope, symbolPath.toString());
      System.out.printf(STEXPORT_SUCCESSFUL, symbolPath.toString().replace("\\","/"));
    }

    // report
    if (cmd.hasOption("r")) {
      report(ast, cmd.getOptionValue("r", outputPath));
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
    final ICD4CodeGlobalScope globalScope = CD4CodeMill.cD4CodeGlobalScope();
    globalScope.clear();
    globalScope.setModelPath(modelPath);
    if (useBuiltInTypes) {
      globalScope.addBuiltInTypes();
    }

    final CD4CodeSymbolTableCreatorDelegator symbolTableCreator = CD4CodeMill.cD4CodeSymbolTableCreatorDelegator();

    artifactScope = symbolTableCreator.createFromAST(ast);
  }

  protected void checkCocos() {
    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(ast);
  }

  protected String createPlantUML(CommandLine plantUMLCmd, String outputPath) throws IOException {
    final String output = Paths.get(outputPath, cmd.getOptionValue("prettyprint", ast.getCDDefinition().getName())).toUri().getPath();

    final PlantUMLConfig plantUMLConfig = new PlantUMLConfig();

    // the following options are set, when they are provided
    if (plantUMLCmd.hasOption("showAtt")) {
      plantUMLConfig.setShowAtt(true);
    }
    if (plantUMLCmd.hasOption("showAssoc")) {
      plantUMLConfig.setShowAssoc(true);
    }
    if (plantUMLCmd.hasOption("showRoles")) {
      plantUMLConfig.setShowRoles(true);
    }
    if (plantUMLCmd.hasOption("showCard")) {
      plantUMLConfig.setShowCard(true);
    }
    if (plantUMLCmd.hasOption("showModifier")) {
      plantUMLConfig.setShowModifier(true);
    }
    if (plantUMLCmd.hasOption("ortho")) {
      plantUMLConfig.setOrtho(true);
    }
    if (plantUMLCmd.hasOption("shortenWords")) {
      plantUMLConfig.setShortenWords(true);
    }
    if (plantUMLCmd.hasOption("showComments")) {
      plantUMLConfig.setShowComments(true);
    }

    if (plantUMLCmd.hasOption("nodesep")) {
      plantUMLConfig.setNodesep(Integer.parseInt(plantUMLCmd.getOptionValue("nodesep", "-1")));
    }
    if (plantUMLCmd.hasOption("ranksep")) {
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
    formatter.printHelp("Examples in case the CLI file is called CDCLI.jar: " + System.lineSeparator() + "java -jar CDCLI.jar -i Person.cd -p target:src/models -o target/out -t true -s" + System.lineSeparator() + "java -jar CDCLI.jar -i Person.cd -pp Person.out.cd -puml --showAtt --showRoles", cdcliOptions.getOptions());

    if (subCommand != null) {
      formatter.printHelp(subCommand.toString(), cdcliOptions.getOptions(subCommand));
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

    final Path allElementsPath = Paths.get(path, REPORT_ALL_ELEMENTS);
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

    System.out.printf(REPORT_SUCCESSFUL, Joiners.COMMA.join(Collections.singletonList(allElementsPath.toString())));
  }
}
