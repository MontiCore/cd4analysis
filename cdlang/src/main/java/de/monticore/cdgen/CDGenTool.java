/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.trafo.DefaultVisibilityPublicTrafo;
import de.monticore.cd.codegen.trafo.TOPTrafo;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis._util.CD4AnalysisTypeDispatcher;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTool;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._symboltable.CD4CodeScopesGenitorDelegatorTOP;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.*;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.*;

/**
 * This tool provides configurable decorator functionality in addition to generation. This tool is
 * tested via
 * the CDGenGradlePluginTest:
 * cdtool/cdgradle/src/test/java/de/monticore/cdgen/CDGenGradlePluginTest.java
 */
public class CDGenTool extends CD4CodeTool {
  
  /**
   * Gradle main method of the CDGenTool
   *
   * @param args array of the command line arguments
   */
  public static void gradleMain(String[] args) {
    CDGenTool tool = new CDGenTool();
    tool.run(args);
  }
  
  /**
   * main method of the CDGenTool
   *
   * @param args array of the command line arguments
   */
  public static void main(String[] args) {
    Log.init();
    try {
      CD4CodeTool tool = new CDGenTool();
      tool.run(args);
    }
    catch (Exception exception) {
      // ensure a sane exit
      Log.error("0xEEEEE an internal error occurred" + " during the execution of the CD4CodeTool."
          + System.lineSeparator() + "This error is unexpected"
          + " and does not indicate an issue with any provided models.", exception);
    }
    // properly exit with a code
    System.exit(Log.getErrorCount() == 0 ? 0 : 1);
  }
  
  /**
   * executes the arguments stated in the command line like parsing a given model to an ast,
   * creating and printing out a corresponding symbol table, checking cocos or generating java files
   * based of additional configuration templates or handwritten code
   *
   * @param args array of the command line arguments
   */
  public void run(String[] args) {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    
    Options options = initOptions();
    
    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);
      
      if (cmd.hasOption("v")) {
        printVersion();
        // do not continue when version is printed
        return;
      }
      else if (!cmd.hasOption("i") || cmd.hasOption("h")) {
        printHelp(options);
        return;
      }
      
      final boolean c2mc = cmd.hasOption("c2mc");
      
      initializeSymbolTable(c2mc, cmd.hasOption("class2mc-no-jdk"));
      
      Log.enableFailQuick(false);
      Collection<ASTCDCompilationUnit> asts = this.parse(".cd", this.createModelPath(cmd)
          .getEntries());
      Log.enableFailQuick(true);
      
      // Run CoCos
      if (cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        asts.forEach(this::runBeforeSTCoCos);
        Log.enableFailQuick(true);
      }
      
      // apply trafos needed for symbol table creation
      asts = this.trafoBeforeSymtab(asts);
      
      if (cmd.hasOption("path")) {
        String[] paths = splitPathEntries(cmd.getOptionValues("path"));
        CD4CodeMill.globalScope().setSymbolPath(new MCPath(paths));
      }
      
      // Create the symbol-table (symbol table creation phase 1)
      List<ICD4CodeArtifactScope> scopes = new ArrayList<>(asts.size());
      for (ASTCDCompilationUnit ast : asts) {
        Log.enableFailQuick(false); // ST creation might report multiple errors
        scopes.add(this.createSymbolTable(ast, c2mc));
        Log.enableFailQuick(true);
      }
      
      // Complete the symbol-table (symbol table creation phase 2)
      for (ASTCDCompilationUnit ast : asts) {
        Log.enableFailQuick(false); // ST completition might report multiple errors
        this.completeSymbolTable(ast);
        Log.enableFailQuick(true);
      }
      
      // Run CoCos
      if (cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        asts.forEach(this::runCoCos);
        Log.enableFailQuick(true);
      }
      
      // Export original symbol table
      if (cmd.hasOption("s")) {
        for (ICD4CodeArtifactScope scope : scopes) {
          this.storeSymTab(scope, cmd.getOptionValue("s"));
        }
      }
      
      if (cmd.hasOption("o")) {
        // Where to load additional templates from
        List<File> additionalTemplatePaths = cmd.hasOption("fp") ? Arrays.stream(cmd
            .getOptionValues("fp")).map(Paths::get).map(Path::toFile).collect(Collectors.toList())
            : Collections.emptyList();
        // Where handwritten code can be found
        Optional<MCPath> handcodedPath = cmd.hasOption("hwc") ? Optional.of(new MCPath(Paths.get(cmd
            .getOptionValue("hwc")))) : Optional.empty();
        // output directory
        String outputPath = (cmd.hasOption("o")) ? Paths.get(cmd.getOptionValue("o")).toString()
            : "";
        
        GlobalExtensionManagement glex = new GlobalExtensionManagement();
        GeneratorSetup generatorSetup = newConfiguredGeneratorSetup(additionalTemplatePaths,
            handcodedPath, outputPath, glex);
        
        // Finally, invoke the decorating generator
        decorateAndGenerate(glex,
            // Initialize the decorator config
            decoratorConfig -> initializeDecConf(glex, decoratorConfig, cmd, generatorSetup),
            generatorSetup, () -> {
              // Just before decorating:
              if (cmd.hasOption("sd")) {
                // Prepare the global scope for decorated symbol table
                this.initDecoratedGlobalScope(c2mc);
              }
            }, decorated -> {
              // After each decoration, but before generation
              if (cmd.hasOption("sd")) {
                // If required, we also output the symbol table of the *decorated* AST
                this.createAndExportDecoratedSymbolTable(decorated, cmd.getOptionValue("sd"));
              }
            }, asts);
      }
    }
    catch (ParseException e) {
      CD4CodeMill.globalScope().clear();
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
    CD4CodeMill.globalScope().clear();
  }
  
  public void initializeSymbolTable(boolean c2mc, boolean c2mcNoJdk) {
    BasicSymbolsMill.initializePrimitives();
    MCCollectionSymTypeRelations.init();
    
    if (c2mc) {
      CD4CodeMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver(!c2mcNoJdk));
      CD4CodeMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver(!c2mcNoJdk));
    }
    else {
      BasicSymbolsMill.initializeString();
      BasicSymbolsMill.initializeObject();
    }
  }
  
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig decConfig,
      CommandLine cmd, GeneratorSetup setup) {
    // Setup CLI config overrides
    if (cmd.hasOption("cliconfig")) {
      decConfig.withCLIConfig(Arrays.asList(cmd.getOptionValues("cliconfig")));
    }
    // Avoid a signature with parameters, as any changes will break compatibility
    glex.setGlobalValue("glex", glex);
    glex.setGlobalValue("decConfig", decConfig);
    glex.setGlobalValue("genSetup", setup);
    String configTemplate = cmd.getOptionValue("ct", "cd2java.init.CD2Pojo");
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
    hpp.processValue(tc, new ArrayList<>());
  }
  
  public void decorateAndGenerate(GlobalExtensionManagement glex,
      Consumer<DecoratorConfig> initializeDecConf, GeneratorSetup setup,
      Runnable initDecoratedGlobalScope, Consumer<ASTCDCompilationUnit> postDecorate,
      Collection<ASTCDCompilationUnit> asts) {
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    glex.setGlobalValue("mcTypeFacade", MCTypeFacade.getInstance()); // TODO: Remove from templates
    glex.setGlobalValue("cdGenService", new CDGenService());
    glex.setGlobalValue("cd4AnalysisTypeDispatcher", new CD4AnalysisTypeDispatcher()); // TODO: Remove from templates
    
    CDGenerator generator = new CDGenerator(setup);
    DecoratorConfig decSetup = new DecoratorConfig();
    
    CDAssociationCreateFieldsFromAllRoles roleTrafo = performFieldsFromRolesTrafo(asts);
    
    // Load the initial decorator config
    initializeDecConf.accept(decSetup);
    
    // e.g., prepare the global scope for decorated symbol table
    initDecoratedGlobalScope.run();
    
    for (ASTCDCompilationUnit ast : asts) {
      var decorated = decSetup.decorate(ast, roleTrafo.getFieldToRoles(), Optional.of(glex));
      
      if (decorated.isEmpty()) {
        Log.error("0xCDD12: Failed generation for " + ast.getCDDefinition().getName());
        continue;
      }
      
      // Post-Decorate: apply trafos needed for code generation
      CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
      t.add4CDBasis(new CDBasisDefaultPackageTrafo());
      decorated.get().accept(t);
      // Post-Decorate: map import statements to classes
      this.mapCD4CImports(decorated.get());
      
      // The following imports (cf. Imports.ftl) have to be added
      decorated.get().addMCImportStatement(CDBasisMill.mCImportStatementBuilder()
          .setMCQualifiedName(MCTypeFacade.getInstance().createQualifiedName("java.util")).setStar(
              true).build());
      
      // If required, we can also output the symbol table of the *decorated* AST
      postDecorate.accept(decorated.get());
      
      // Post-Decorate: TOP Decorator
      // TODO: #4310 - make this TOP transformation configurable via the config
      // template
      boolean qf = Log.isFailQuickEnabled();
      Log.enableFailQuick(false); // Disable quick-fail during post decoration
      TOPTrafo topTransformer = new TOPTrafo(setup.getHandcodedPath());
      t = CD4CodeMill.inheritanceTraverser();
      topTransformer.addToTraverser(t);
      decorated.get().accept(t);
      
      generator.generate(decorated.get());
      Log.enableFailQuick(qf); // reset quick-fail
    }
  }
  
  public GeneratorSetup newConfiguredGeneratorSetup(List<File> additionalTemplatePaths,
      Optional<MCPath> handcodedPath, String outputPath, GlobalExtensionManagement glex) {
    GeneratorSetup setup = new GeneratorSetup();
    
    setup.setAdditionalTemplatePaths(additionalTemplatePaths);
    handcodedPath.ifPresent(setup::setHandcodedPath);
    setup.setGlex(glex);
    setup.setOutputDirectory(new File(outputPath));
    return setup;
  }
  
  public CDAssociationCreateFieldsFromAllRoles performFieldsFromRolesTrafo(
      Collection<ASTCDCompilationUnit> asts) {
    CDAssociationCreateFieldsFromAllRoles roleTrafo =
        new CDAssociationCreateFieldsFromNavigableRoles();
    final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDAssociation(roleTrafo);
    traverser.setCDAssociationHandler(roleTrafo);
    asts.forEach(roleTrafo::transform);
    return roleTrafo;
  }
  
  /**
   * Without Class2MC, we have to load symbols used in the generated CD
   *
   * @param c2mc whether Class2MC was loaded
   */
  public void initDecoratedGlobalScope(boolean c2mc) {
    if (!c2mc) {
      // Without Class2MC we must add fake-symbols for field, arg and return types used during
      // decoration
      // Load these symbols from an exported symbol table
      for (Class<?> c : Arrays.asList(List.class, Set.class, Collection.class, Iterator.class,
          ListIterator.class, Spliterator.class, Stream.class, Optional.class)) {
        registerFakeType(c.getSimpleName(), c.getName());
      }
      registerFakeType("ICDObservable", "de.monticore.cd.ICDObservable");
      registerFakeType("ICDObserver", "de.monticore.cd.ICDObserver");
    }
  }
  
  protected void registerFakeType(String simplename, String fullName) {
    CDBasisMill.globalScope().add(CDBasisMill.typeSymbolBuilder().setName(simplename).setFullName(
        fullName).setSpannedScope(CDBasisMill.scope()).setEnclosingScope(CDBasisMill.globalScope())
        .build());
  }
  
  /**
   * Create, complete, and export the symbol table of a decorated CD
   *
   * @param decorated the CD
   * @param symbolOutPath the directory into which the ST is exported
   */
  public void createAndExportDecoratedSymbolTable(ASTCDCompilationUnit decorated,
      String symbolOutPath) {
    // Create the symbol-table (symbol table creation phase 1)
    var decoratedScope = this.createSymbolTable(decorated, true);
    
    // Complete the symbol-table (symbol table creation phase 2)
    this.completeSymbolTable(decorated);
    
    // Store the decorated symbol table
    this.storeSymbols(decoratedScope, Paths.get(symbolOutPath, Names.getPathFromPackage(
        decoratedScope.getFullName()) + ".deccdsym").toString());
  }
  
  /**
   * adds additional options to the cli tool
   *
   * @param options collection of all the possible options
   */
  public Options addAdditionalOptions(Options options) {
    
    options.addOption(Option.builder("c").longOpt("checkcococs").desc(
        "Checks all CoCos on the given mode.").build());
    
    options.addOption(Option.builder("o").longOpt("output").argName("dir").hasArg().desc(
        "Sets the output path.").build());
    
    options.addOption(Option.builder("ct").longOpt("configtemplate").hasArg().argName("template")
        .desc("Sets a template for configuration.").build());
    
    options.addOption(Option.builder("fp").longOpt("template").hasArg().argName("path").desc(
        "Sets the path for additional templates.").build());
    
    options.addOption(Option.builder("hwc").longOpt("handwrittencode").hasArg().argName("hwcpath")
        .desc("Sets the path for additional, handwritten classes.").build());
    
    options.addOption(Option.builder("c2mc").longOpt("class2mc").desc(
        "Enables to resolve java classes in the model path").build());
    
    options.addOption(Option.builder().longOpt("class2mc-no-jdk").desc(
        "Does not resolve types from the installed JDK's standard library. Only the symbolpath is used.")
        .build());
    
    options.addOption(Option.builder("cliconfig").desc("Configures additional").hasArgs().argName(
        "fqn:key[=value]").build());
    
    options.addOption(org.apache.commons.cli.Option.builder("sd").longOpt("symboltabledecorated")
        .argName("file").hasArg().desc(
            "Serializes the decorated symbol table of the given artifact.").build());
    
    return options;
  }
  
  /**
   * checks all cocos on the original ast before the symbol table is created
   *
   * @param ast the original ast, without ST
   */
  public void runBeforeSTCoCos(ASTCDCompilationUnit ast) {
    // Nothing yet, decide how we expose them
  }
  
  /**
   * checks all cocos on the original ast
   *
   * @param ast the original ast
   */
  public void runCoCos(ASTCDCompilationUnit ast) {
    CD4CodeCoCoChecker checker = new CD4CodeCoCosDelegator().getCheckerForAllCoCos();
    checker.checkAll(ast);
  }
  
  public Collection<ASTCDCompilationUnit> trafoBeforeSymtab(Collection<ASTCDCompilationUnit> asts) {
    CD4CodeAfterParseTrafo trafo = new CD4CodeAfterParseTrafo();
    asts.forEach(ast -> ast.accept(trafo.getTraverser()));
    // TODO: Have this be done via the config-options (#4310)
    var t = CD4CodeMill.inheritanceTraverser();
    t.add4UMLModifier(new DefaultVisibilityPublicTrafo());
    asts.forEach(ast -> ast.accept(t));
    return asts;
  }
  
  /**
   * Updates the map of cd types to import statement in the given cd4c object, adding the imports
   * for each cd type (classes, enums, and interfaces) defined in the given ast.
   *
   * @param ast the input ast
   */
  public void mapCD4CImports(ASTCDCompilationUnit ast) {
    CD4C cd4c = CD4C.getInstance();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();
    
    for (ASTCDClass cdClass : ast.getCDDefinition().getCDClassesList()) {
      for (ASTMCImportStatement i : imports) {
        String qName = i.getQName();
        cd4c.addImport(cdClass, i.isStar() ? qName + ".*" : qName);
      }
    }
    for (ASTCDInterface cdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTMCImportStatement i : imports) {
        String qName = i.getQName();
        cd4c.addImport(cdInterface, i.isStar() ? qName + ".*" : qName);
      }
    }
    for (ASTCDEnum cdEnum : ast.getCDDefinition().getCDEnumsList()) {
      for (ASTMCImportStatement i : imports) {
        String qName = i.getQName();
        cd4c.addImport(cdEnum, i.isStar() ? qName + ".*" : qName);
      }
    }
  }
  
  public MCPath createModelPath(CommandLine cl) {
    if (cl.hasOption("i")) {
      return new MCPath(splitPathEntries(cl.getOptionValues("i")));
    }
    else {
      return new MCPath();
    }
  }
  
  public String[] splitPathEntries(String composedPath) {
    Objects.requireNonNull(composedPath);
    
    return composedPath.split(Pattern.quote(File.pathSeparator));
  }
  
  public final String[] splitPathEntries(String[] composedPaths) {
    Objects.requireNonNull(composedPaths);
    return Arrays.stream(composedPaths).map(this::splitPathEntries).flatMap(Arrays::stream).toArray(
        String[]::new);
  }
  
  public Collection<ASTCDCompilationUnit> parse(String fileExt, Collection<Path> filesAndDirs) {
    return filesAndDirs.stream().flatMap(dirOrFile -> this.parse(fileExt, dirOrFile).stream())
        .collect(Collectors.toList());
  }
  
  /**
   * Parses all class diagrams in the given path.
   * In case the path is a file, the file is parsed regardless of its extension
   * Otherwise, all files within the path-directory are parsed if their extension matches
   *
   * @param fileExt recursively parses all files with this extension in a directory
   * @param fileOrDir directory or file
   * @return a collection of nested files
   */
  public Collection<ASTCDCompilationUnit> parse(String fileExt, Path fileOrDir) {
    if (Files.isRegularFile(fileOrDir)) {
      // In case a file is within the ModelPath: parse the file
      return Collections.singleton(this.parse(fileOrDir.toString()));
    }
    // Otherwise: Traverse the directory & parse all matching files
    try (
        Stream<Path> paths = Files.walk(fileOrDir)
    ) {
      return paths.filter(Files::isRegularFile).filter(file -> file.getFileName().toString()
          .endsWith(fileExt)).map(Path::toString).map(this::parse).collect(Collectors.toSet());
    }
    catch (IOException e) {
      Log.error("0xA1063 Error while traversing the file structure `" + fileOrDir + "`.", e);
    }
    return Collections.emptySet();
  }
  
  /**
   * creates the symboltable for the given ast
   *
   * @param ast the input ast
   * @param java whether to add java default imports
   * @return the symbol-table of the ast
   */
  public ICD4CodeArtifactScope createSymbolTable(ASTCDCompilationUnit ast, boolean java) {
    CD4CodeScopesGenitorDelegatorTOP genitor = CD4CodeMill.scopesGenitorDelegator();
    ICD4CodeArtifactScope scope = genitor.createFromAST(ast);
    this.addDefaultImports(scope, java);
    return scope;
  }
  
  public void addDefaultImports(ICD4CodeArtifactScope scope, boolean java) {
    if (java)
      scope.addImports(new ImportStatement("java.lang", true));
  }
  
  /**
   * prints the symboltable of the given scope out to a file
   *
   * @param scope symboltable to store
   * @param path location of the file or directory containing the printed table
   */
  public void storeSymTab(ICD4CodeArtifactScope scope, String path) {
    if (Path.of(path).toFile().isFile()) {
      this.storeSymbols(scope, path);
    }
    else {
      this.storeSymbols(scope, Paths.get(path, Names.getPathFromPackage(scope.getFullName())
          + ".cdsym").toString());
    }
  }
  
  /**
   * completes the symboltable for the given ast
   *
   * @param ast the input ast
   */
  public void completeSymbolTable(ASTCDCompilationUnit ast) {
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
  }
  
}
