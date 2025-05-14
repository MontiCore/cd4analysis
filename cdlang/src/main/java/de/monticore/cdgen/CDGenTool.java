/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import de.monticore.CDGeneratorTool;
import de.monticore.cd._visitor.CDElementVisitor;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.trafo.DefaultVisibilityPublicTrafo;
import de.monticore.cd.codegen.trafo.TOPTrafo;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.*;

/**
 * This class is a further development of the {@link CDGeneratorTool} and meant as a replacement. It
 * provides configurable decorator functionality in addition to generation.
 * This tool is tested via the CDGenGradlePluginTest:
 * cdtool/cdgradle/src/test/java/de/monticore/cdgen/CDGenGradlePluginTest.java
 */
public class CDGenTool extends CDGeneratorTool {

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
    CDGenTool tool = new CDGenTool();
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

    de.monticore.cd4code.CD4CodeMill.reset();
    de.monticore.cd4code.CD4CodeMill.init();

    Options options = initOptions();

    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);

      if (cmd.hasOption("v")) {
        printVersion();
        // do not continue when version is printed
        return;
      } else if (!cmd.hasOption("i") || cmd.hasOption("h")) {
        printHelp(options);
        return;
      }

      BasicSymbolsMill.initializePrimitives();
      MCCollectionSymTypeRelations.init();

      final boolean c2mc = cmd.hasOption("c2mc");
      if (c2mc) {
        initializeClass2MC();
      } else {
        BasicSymbolsMill.initializeString();
        BasicSymbolsMill.initializeObject();
      }

      Log.enableFailQuick(false);
      Collection<ASTCDCompilationUnit> asts =
          this.parse(".cd", this.createModelPath(cmd).getEntries());
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
        String[] paths = splitPathEntries(cmd.getOptionValue("path"));
        CD4CodeMill.globalScope().setSymbolPath(new MCPath(paths));
      }

      // Create the symbol-table (symbol table creation phase 1)
      List<ICD4CodeArtifactScope> scopes = new ArrayList<>(asts.size());
      for (ASTCDCompilationUnit ast : asts) {
        scopes.add(this.createSymbolTable(ast, c2mc));
      }

      // Complete the symbol-table (symbol table creation phase 2)
      for (ASTCDCompilationUnit ast : asts) {
        this.completeSymbolTable(ast);
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
        GlobalExtensionManagement glex = new GlobalExtensionManagement();
        glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
        GeneratorSetup setup = new GeneratorSetup();

        if (cmd.hasOption("fp")) {
          setup.setAdditionalTemplatePaths(
              Arrays.stream(cmd.getOptionValues("fp"))
                  .map(Paths::get)
                  .map(Path::toFile)
                  .collect(Collectors.toList()));
        }

        if (cmd.hasOption("hwc")) {
          setup.setHandcodedPath(new MCPath(Paths.get(cmd.getOptionValue("hwc"))));
        }

        String outputPath =
            (cmd.hasOption("o")) ? Paths.get(cmd.getOptionValue("o")).toString() : "";

        setup.setGlex(glex);
        setup.setOutputDirectory(new File(outputPath));

        CDGenerator generator = new CDGenerator(setup);
        String configTemplate = cmd.getOptionValue("ct", "cd2java.init.CD2Pojo");
        TemplateController tc = setup.getNewTemplateController(configTemplate);
        TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

        DecoratorConfig decSetup = new DecoratorConfig();

        // Setup CLI config overrides
        if (cmd.hasOption("cliconfig")) {
          decSetup.withCLIConfig(Arrays.asList(cmd.getOptionValues("cliconfig")));
        }

        CDAssociationCreateFieldsFromAllRoles roleTrafo =
            new CDAssociationCreateFieldsFromNavigableRoles();
        final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
        traverser.add4CDAssociation(roleTrafo);
        traverser.setCDAssociationHandler(roleTrafo);
        asts.forEach(roleTrafo::transform);

        List<Object> configTemplateArgs = Arrays.asList(glex, decSetup);

        hpp.processValue(tc, configTemplateArgs);

        if (cmd.hasOption("sd")) {
          // Prepare the global scope for decorated symbol table
          this.initDecoratedGlobalScope(c2mc);
        }

        List<ASTCDCompilationUnit> decoratedASTs = new ArrayList<>();
        for (ASTCDCompilationUnit ast : asts) {
          // Prepare
          glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

          var decorated = decSetup.decorate(ast, roleTrafo.getFieldToRoles(), Optional.of(glex));

          if (decorated.isEmpty()) {
            Log.error("0xTODO: Failed generation for " + ast.getCDDefinition().getName());
            continue;
          }

          // Post-Decorate: apply trafos needed for code generation
          CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
          t.add4CDBasis(new CDBasisDefaultPackageTrafo());
          decorated.get().accept(t);
          // Post-Decorate: make methods in interfaces abstract
          this.makeMethodsInInterfacesAbstract(decorated.get());
          // Post-Decorate: map import statements to classes
          this.mapCD4CImports(decorated.get());

          // The following imports (cf. Imports.ftl) have to be added
          decorated.get().addMCImportStatement(CDBasisMill.mCImportStatementBuilder().setMCQualifiedName(MCTypeFacade.getInstance().createQualifiedName("java.util")).setStar(true).build());

          if (cmd.hasOption("sd")) {
            // If required, we also output the symbol table of the *decorated* AST
            this.createAndExportDecoratedSymbolTable(decorated.get(), cmd.getOptionValue("sd"));
          }

          // Post-Decorate: TOP Decorator
          // TODO: #4310 - make this TOP transformation configurable via the config
          // template
          TOPTrafo topTransformer = new TOPTrafo(setup.getHandcodedPath());
          t = CD4CodeMill.inheritanceTraverser();
          topTransformer.addToTraverser(t);
          decorated.get().accept(t);

          generator.generate(decorated.get());
        }
      }
    } catch (ParseException e) {
      CD4CodeMill.globalScope().clear();
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
    CD4CodeMill.globalScope().clear();
  }

  /**
   * Without Class2MC, we have to load symbols used in the generated CD
   * @param c2mc whether Class2MC was loaded
   */
  public void initDecoratedGlobalScope(boolean c2mc) {
    if (!c2mc) {
      // Without Class2MC we must add fake-symbols for field, arg and return types used during decoration
      // Load these symbols from an exported symbol table
      for (Class<?> c : Arrays.asList(List.class, Set.class,
        Collection.class, Iterator.class,
        Spliterator.class, Stream.class, Optional.class)) {
        CDBasisMill.globalScope().add(
          CDBasisMill.typeSymbolBuilder()
            .setName(c.getSimpleName())
            .setFullName(c.getName())
            .setSpannedScope(CDBasisMill.scope())
            .setEnclosingScope(CDBasisMill.globalScope())
            .build());
      }
    }
  }

  /**
   * Create, complete, and export the symbol table of a decorated CD
   * @param decorated the CD
   * @param symbolOutPath the directory into which the ST is exported
   */
  public void createAndExportDecoratedSymbolTable(ASTCDCompilationUnit decorated, String symbolOutPath) {
    // Create the symbol-table (symbol table creation phase 1)
    var decoratedScope = this.createSymbolTable(decorated, true);

    // Complete the symbol-table (symbol table creation phase 2)
    this.completeSymbolTable(decorated);

    // Store the decorated symbol table
    this.storeSymbols(
      decoratedScope,
      Paths.get(symbolOutPath, Names.getPathFromPackage(decoratedScope.getFullName()) + ".deccdsym").toString());
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
        Option.builder("o")
            .longOpt("output")
            .argName("dir")
            .hasArg()
            .desc("Sets the output path.")
            .build());

    options.addOption(
        Option.builder("ct")
            .longOpt("configtemplate")
            .hasArg()
            .argName("template")
            .desc("Sets a template for configuration.")
            .build());

    options.addOption(
        Option.builder("fp")
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
        Option.builder("cliconfig")
            .desc("Configures additional")
            .hasArgs()
            .argName("fqn:key[=value]")
            .build());

    options.addOption(org.apache.commons.cli.Option.builder("sd")
      .longOpt("symboltabledecorated")
      .argName("file")
      .hasArg()
      .desc("Serializes the decorated symbol table of the given artifact.")
      .build());

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
    super.runCoCos(ast);
  }

  @Override
  public Collection<ASTCDCompilationUnit> trafoBeforeSymtab(Collection<ASTCDCompilationUnit> asts) {
    super.trafoBeforeSymtab(asts);
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
}
