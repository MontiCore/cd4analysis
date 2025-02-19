/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import de.monticore.CDGeneratorTool;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cd.codegen.trafo.DefaultVisibilityPublicTrafo;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a further development of the {@link CDGeneratorTool}
 * and meant as a replacement.
 * It provides configurable decorator functionality in addition to generation
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

      if (cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        asts.forEach(this::runCoCos);
        Log.enableFailQuick(true);
      }



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

        CDAssociationCreateFieldsFromAllRoles roleTrafo = new CDAssociationCreateFieldsFromNavigableRoles();
        final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
        traverser.add4CDAssociation(roleTrafo);
        traverser.setCDAssociationHandler(roleTrafo);
        asts.forEach(roleTrafo::transform);

        List<Object> configTemplateArgs = Arrays.asList(glex, decSetup);

        hpp.processValue(tc, configTemplateArgs);

        for (ASTCDCompilationUnit ast : asts) {
          // Prepare
          glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

          var decorated = decSetup.decorate(ast, roleTrafo.getFieldToRoles(), Optional.of(glex));

          System.err.println(CD4CodeMill.prettyPrint(decorated, true));

          // Post-Decorate: apply trafos needed for code generation
          CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
          t.add4CDBasis(new CDBasisDefaultPackageTrafo());
          decorated.accept(t);
          // Post-Decorate: make methods in interfaces abstract
          this.makeMethodsInInterfacesAbstract(decorated);

          // Post-Decorate: TOP Decorator
          // TODO: #4310

          generator.generate(decorated);
        }
      }

    } catch (ParseException e) {
      CD4CodeMill.globalScope().clear();
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
    CD4CodeMill.globalScope().clear();
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

    return options;
  }

  /**
   * checks all cocos on the current ast
   *
   * @param ast the current ast
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
}
