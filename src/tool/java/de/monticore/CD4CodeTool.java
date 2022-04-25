/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.fasterxml.jackson.databind.JsonNode;
import de.monticore.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.alloycddiff.classDifference.ClassDifference;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.json.CD2JsonUtil;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.cocos.CDAssociationUniqueInHierarchy;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.ow2cw.ReductionTrafo;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4CodeTool extends de.monticore.cd4code.CD4CodeTool {

  protected static final String PARSE_SUCCESSFUL = "Successfully parsed %s\n";
  protected static final String CHECK_SUCCESSFUL = "Successfully checked the CoCos for class diagram %s\n";
  protected static final String CHECK_ERROR = "Error while parsing or CoCo checking";
  protected static final String PLANTUML_SUCCESSFUL = "Creation of plantUML file %s successful\n";
  protected static final String PRETTYPRINT_SUCCESSFUL = "Creation of model file %s successful\n";
  protected static final String JSON_SUCCESSFUL = "Creation of json file %s successful\n";
  protected static final String FILE_EXISTS_INFO = "File '%s' already exists and will be overwritten\n";
  protected static final String DIR_CREATION_ERROR = "Directory '%s' could not be created\n";
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

  @Override
  public void run(String[] args) {
    try{
      if(handleArgs(args)){
        init();

        if (cmd.hasOption("semdiff")){
          computeSemDiff();
          CD4CodeMill.globalScope().clear();
        }

        if(!modelFile.isEmpty()) {
          ast = parse(modelFile);
        }else{
          ast = parse(modelReader);
        }
        new CD4CodeAfterParseTrafo().transform(ast);
        modelName = ast.getCDDefinition().getName();

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

        if (cmd.hasOption("pp")) { // pretty print
          String ppTarget = cmd.getOptionValue("pp");
          prettyPrint(ast, ppTarget);
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

        artifactScope = createSymbolTable(ast);
        final ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
        for (String path : modelPath) {
          globalScope.getSymbolPath().addEntry(Paths.get(path));
        }
        if (useBuiltInTypes) {
          BuiltInTypes.addBuiltInTypes(globalScope);
        }
        completeSymbolTable();

        // transformations that need an already created symbol table
        {
          final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo();
          final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
          traverser.add4CDAssociation(cdAssociationRoleNameTrafo);
          traverser.setCDAssociationHandler(cdAssociationRoleNameTrafo);
          cdAssociationRoleNameTrafo.transform(ast);
        }

        runDefaultCoCos(ast);

        if (cmd.hasOption("fieldfromrole")) {

          // This CoCo checks whether a subclass redefines a role of an association, which is
          // allowed for analysis (e.g. semdiff) but NOT for code-generation
          runGeneratorCoCo();

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
              symbolPath = Paths.get(Names.getQualifier(modelFile) + ".cdsym");
            }
            else {
              symbolPath = Paths.get(Names.getPathFromPackage(artifactScope.getRealPackageName()) + File.separator + modelName + ".cdsym");
            }
          }
          else {
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

          if (cmd.hasOption("fp")) { // Template path
            generatorSetup.setAdditionalTemplatePaths(
              Arrays.stream(cmd.getOptionValues("fp")).map(Paths::get).map(Path::toFile).collect(Collectors.toList()));
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
    }catch (AmbiguousOptionException e) {
      Log.error(String.format("0xCD0E2: option '%s' can't match any valid option", e.getOption()));
    }
    catch (UnrecognizedOptionException e) {
      Log.error(String.format("0xCD0E3: unrecognized option '%s'", e.getOption()));
    }
    catch (MissingOptionException e) {
      Log.error(String.format("0xCD0E4: options [%s] are missing, but are required", Joiners.COMMA.join(e.getMissingOptions())));
    }
    catch (MissingArgumentException e) {
      Log.error(String.format("0xCD0E5: option '%s' is missing an argument", e.getOption()));
    }
    catch (NumberFormatException e) {
      Log.error("0xCD0E6: options --diffsize and --difflimit each require an "
        + "integer as argument");
    }
    catch (Exception e) {
      Log.error(String.format("0xCD0E7: an error occurred: %s", e.getMessage()));
    }
  }

  @Override
  public void init() {
    super.init();
    Log.initWARN();
  }

  protected boolean handleArgs(String[] args)
    throws IOException, ParseException {
    cmd = cdToolOptions.handleArgs(args);

    /*if (cmd.hasOption("log")) {
      root.setLevel(Level.toLevel(cmd.getOptionValue("log", DEFAULT_LOG_LEVEL.levelStr), DEFAULT_LOG_LEVEL));
    }*/

    outputPath = cmd.getOptionValue("o", ".");

    if (cmd.hasOption("h")) {
      if (cmd.hasOption("puml")) {
        printHelp(CDToolOptions.SubCommand.PLANTUML);
      }
      else {
        printHelp((CDToolOptions.SubCommand) null);
      }
      return false;
    }
    else {

      if (!cmd.hasOption("i") && !cmd.hasOption("stdin")) {
        printHelp((CDToolOptions.SubCommand) null);
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

  public ASTCDCompilationUnit parse(Reader reader) {
    try {
      de.monticore.cd4code._parser.CD4CodeParser parser = de.monticore.cd4code.CD4CodeMill.parser() ;
      Optional<ASTCDCompilationUnit> optAst = parser.parse(reader);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xCD0E0 Model could not be parsed.");
    }
    catch (NullPointerException | java.io.IOException e) {
      Log.error("0xCD0E1 Failed to parse from stdin", e);
    }
    // should never be reached (unless failquick is off)
    return null;
  }

  public void completeSymbolTable(){
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

  @Override
  public void prettyPrint(ASTCDCompilationUnit ast, String ppTarget) {
    CD4CodeFullPrettyPrinter cd4CodeFullPrettyPrinter = new CD4CodeFullPrettyPrinter();
    ast.accept(cd4CodeFullPrettyPrinter.getTraverser());
    if (ppTarget == null) {
      System.out.println(cd4CodeFullPrettyPrinter.getPrinter().getContent());
    }
    else {
      final Path outputPath;
      if (Paths.get(ppTarget).isAbsolute()) {
        outputPath = Paths.get(ppTarget);
      }
      else {
        if(this.outputPath.isEmpty()){
          outputPath = Paths.get(ppTarget);
        }else {
          outputPath = Paths.get(this.outputPath, ppTarget);
        }
      }
      final File file = outputPath.toFile();
      if (file.exists()) {
        System.out.printf(FILE_EXISTS_INFO, unifyPath(file.getPath()));
      } else if (!file.getParentFile().mkdirs()) {
        System.out.printf(DIR_CREATION_ERROR, unifyPath(file.getAbsolutePath()));
        return;
      }

      try (PrintWriter out = new PrintWriter(new FileOutputStream(file), true)) {
        out.println(cd4CodeFullPrettyPrinter.getPrinter().getContent());
      }catch(FileNotFoundException e){
        System.out.println(cd4CodeFullPrettyPrinter.getPrinter().getContent());
      }
      System.out.printf(PRETTYPRINT_SUCCESSFUL, unifyPath(file.toPath()));
    }
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

  protected void printHelp(CDToolOptions.SubCommand subCommand) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(110);
    formatter.printHelp("Examples in case the Tool file is called MCCD.jar: " + System.lineSeparator() + "java -jar MCCD.jar -i Person.cd --path target:src/models -o target/out -t true -s" + System.lineSeparator() + "java -jar MCCD.jar -i src/Person.cd -pp target/Person.cd" + System.lineSeparator() + "", cdToolOptionsForHelp.getOptions());

    if (subCommand != null) {
      formatter.printHelp(subCommand.toString(), cdToolOptionsForHelp.getOptions(subCommand));
    }
    System.out.println("Further details: https://www.se-rwth.de/topics/");
  }

  protected void computeSemDiff() throws NumberFormatException{

    // parse the first CD
    ASTCDCompilationUnit ast1;

    if(!modelFile.isEmpty()) {
      ast1 = parse(modelFile);
    }else{
      ast1 = parse(modelReader);
    }

    // parse the second CD
    ASTCDCompilationUnit ast2 = parse(cmd.getOptionValue("semdiff"));

    // check if open-to-closed world reduction should be performed
    boolean ow2cw = cmd.hasOption("open-world");

    if(ow2cw){
      CD4CodeMill.globalScope().clear();
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(ast1,ast2);
    }

    // determine the diffsize, default is max(20,2*(|Classes|+|Interfaces|))
    int diffsize;
    if (cmd.hasOption("diffsize") && cmd.getOptionValue("diffsize") != null) {
      diffsize = Integer.parseInt(cmd.getOptionValue("diffsize", "20"));
    } else {
      int cd1size = ast1.getCDDefinition().getCDClassesList().size()
        + ast1.getCDDefinition().getCDInterfacesList().size();

      int cd2size = ast2.getCDDefinition().getCDClassesList().size()
        + ast2.getCDDefinition().getCDInterfacesList().size();

      diffsize = Math.max(20,2*Math.max(cd1size, cd2size));
    }

    int difflimit = Integer.parseInt(cmd.getOptionValue("difflimit", "1"));

    // compute semDiff(ast,ast2)
    Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(ast1, ast2, diffsize,
        ow2cw, outputPath);

    // test if solution is present
    if (!optS.isPresent()) {
      Log.error("0xCDD01: Could not compute semdiff.");
      return;
    }
    AlloyDiffSolution sol = optS.get();

    // limit number of generated diff-witnesses
    sol.setSolutionLimit(difflimit);
    sol.setLimited(true);

    // generate diff-witnesses in outputPath
    sol.generateSolutionsToPath(Paths.get(outputPath));
  }

  /**
   * Additional CoCo-Check for Code-Generation
   */
  protected void runGeneratorCoCo(){
    CD4AnalysisCoCos generalCoCos = new CD4AnalysisCoCos();
    CD4AnalysisCoCoChecker checker = generalCoCos.createNewChecker();

    checker.addCoCo(new CDAssociationUniqueInHierarchy());
    checker.checkAll(ast);
  }

  /* generated by template core.Method*/
  public  static  void main (String[] args)

  {
    /* generated by template _cli.Main*/


    CD4CodeTool tool = new CD4CodeTool();
    tool.run(args);

  }
}
