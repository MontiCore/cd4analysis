/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.base.Preconditions;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd.methodtemplates.CD4C;
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
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

    this.init();

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

      if (cmd.hasOption("c2mc")) {
        initializeClass2MC();
      } else {
        BasicSymbolsMill.initializeString();
        BasicSymbolsMill.initializeObject();
      }
      BasicSymbolsMill.initializePrimitives();

      Log.enableFailQuick(false);
      Collection<ASTCDCompilationUnit> asts =
          this.parse(".cd", this.createModelPath(cmd).getEntries());
      Log.enableFailQuick(true);

      asts.forEach(this::transform);

      if (cmd.hasOption("path")) {
        String[] paths = splitPathEntries(cmd.getOptionValue("path"));
        CD4CodeMill.globalScope().setSymbolPath(new MCPath(paths));
      }

      Collection<ICD4CodeArtifactScope> scopes =
          asts.stream()
              .map(ast -> createSymbolTable(ast, cmd.hasOption("c2mc")))
              .collect(Collectors.toList());
      asts.forEach(this::completeSymbolTable);

      if (cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        asts.forEach(this::runCoCos);
        Log.enableFailQuick(true);
      }

      String fieldFromRole =
          cmd.hasOption("fieldfromrole") ? cmd.getOptionValue("fieldfromrole") : "navigable";
      switch (fieldFromRole) {
        case "all":
          { // add FieldSymbols for all the CDRoleSymbols
            final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromAllRoles =
                new CDAssociationCreateFieldsFromAllRoles();
            final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
            traverser.add4CDAssociation(cdAssociationCreateFieldsFromAllRoles);
            traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromAllRoles);
            asts.forEach(cdAssociationCreateFieldsFromAllRoles::transform);
            break;
          }
        case "navigable":
          { // add FieldSymbols only for navigable CDRoleSymbols
            final CDAssociationCreateFieldsFromNavigableRoles
                cdAssociationCreateFieldsFromNavigableRoles =
                    new CDAssociationCreateFieldsFromNavigableRoles();
            final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
            traverser.add4CDAssociation(cdAssociationCreateFieldsFromNavigableRoles);
            traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromNavigableRoles);
            asts.forEach(cdAssociationCreateFieldsFromNavigableRoles::transform);
            break;
          }
        case "none":
        default:
          Log.error(
              String.format(
                  "0xA7105 Invalid value %s for option --fieldfromrole. Options are all, navigable or none.",
                  fieldFromRole));
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

        // setup default package when generating
        CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
        t.add4CDBasis(new CDBasisDefaultPackageTrafo());
        asts.forEach(ast -> ast.accept(t));

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
          asts.forEach(topDecorator::decorate);
        }

        String outputPath =
            (cmd.hasOption("o")) ? Paths.get(cmd.getOptionValue("o")).toString() : "";

        setup.setGlex(glex);
        setup.setOutputDirectory(new File(outputPath));

        CDGenerator generator = new CDGenerator(setup);
        String configTemplate = cmd.getOptionValue("ct", "cd2java.CD2Java");
        TemplateController tc = setup.getNewTemplateController(configTemplate);
        TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
        List<Object> configTemplateArgs = Arrays.asList(glex, generator);

        asts.forEach(this::mapCD4CImports);

        asts.forEach(ast -> addGettersAndSetters(ast, glex));
        asts.forEach(this::makeMethodsInInterfacesAbstract);

        asts.forEach(ast -> hpp.processValue(tc, ast, configTemplateArgs));
      }

    } catch (ParseException e) {
      CD4CodeMill.globalScope().clear();
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
    CD4CodeMill.globalScope().clear();
  }

  public void addDefaultImports(ICD4CodeArtifactScope scope, boolean java) {
    if (java) scope.addImports(new ImportStatement("java.lang", true));
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
        Option.builder("fieldfromrole")
            .desc("Configures if explicit field symbols should be added for associations")
            .hasArg()
            .build());

    return options;
  }

  /**
   * checks all cocos on the current ast
   *
   * @param ast the current ast
   */
  public void runCoCos(ASTCDCompilationUnit ast) {
    CD4CodeCoCoChecker checker = new CD4CodeCoCosDelegator().getCheckerForAllCoCos();
    checker.checkAll(ast);
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
    } else {
      this.storeSymbols(
          scope,
          Paths.get(path, Names.getPathFromPackage(scope.getFullName()) + ".cdsym").toString());
    }
  }

  /**
   * transforms the ast using th
   *
   * @param ast The input AST
   * @return The transformed AST
   */
  public ASTCDCompilationUnit transform(ASTCDCompilationUnit ast) {
    CD4CodeAfterParseTrafo trafo = new CD4CodeAfterParseTrafo();
    ast.accept(trafo.getTraverser());
    return ast;
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

  /**
   * completes the symboltable for the given ast
   *
   * @param ast the input ast
   */
  public void completeSymbolTable(ASTCDCompilationUnit ast) {
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
  }

  public void initializeClass2MC() {
    CD4CodeMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    CD4CodeMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  /**
   * adds default getter and setter methods to a class for every attribute in case if none have been
   * added so far
   *
   * @param ast the input ast
   */
  public void addGettersAndSetters(ASTCDCompilationUnit ast, GlobalExtensionManagement glex) {
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    for (ASTCDClass c : ast.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : c.getCDAttributeList()) {
        List<ASTCDMethod> result = methodDecorator.decorate(attribute);
        result.stream()
            .filter(
                m ->
                    !c.getCDMethodList().stream()
                        .map(ASTCDMethod::getName)
                        .collect(Collectors.toList())
                        .contains(m.getName()))
            .forEach(c::addCDMember);
      }
    }
  }

  public void makeMethodsInInterfacesAbstract(ASTCDCompilationUnit ast) {
    for (ASTCDInterface cdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDMethod method : cdInterface.getCDMethodList()) {
        method.getModifier().setAbstract(true);
      }
    }
  }

  public String[] splitPathEntries(String composedPath) {
    Preconditions.checkNotNull(composedPath);

    return composedPath.split(Pattern.quote(File.pathSeparator));
  }

  public final String[] splitPathEntries(String[] composedPaths) {
    Preconditions.checkNotNull(composedPaths);
    return Arrays.stream(composedPaths)
        .map(this::splitPathEntries)
        .flatMap(Arrays::stream)
        .toArray(String[]::new);
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

    imports.add(
        MCBasicTypesMill.mCImportStatementBuilder()
            .setMCQualifiedName(
                MCQualifiedNameFacade.createQualifiedName("de.se_rwth.commons.logging.Log"))
            .build());

    for (ASTCDClass cdClass : ast.getCDDefinition().getCDClassesList()) {
      for (ASTMCImportStatement i : imports) {
        String qName = i.getQName();
        cd4c.addImport(cdClass, i.isStar() ? qName + ".*" : qName);
      }
    }
    for (ASTCDInterface cdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTMCImportStatement i : imports) {
        cd4c.addImport(cdInterface, i.getQName());
      }
    }
    for (ASTCDEnum cdEnum : ast.getCDDefinition().getCDEnumsList()) {
      for (ASTMCImportStatement i : imports) {
        cd4c.addImport(cdEnum, i.getQName());
      }
    }
  }

  public MCPath createModelPath(CommandLine cl) {
    if (cl.hasOption("i")) {
      return new MCPath(splitPathEntries(cl.getOptionValues("i")));
    } else {
      return new MCPath();
    }
  }

  public Collection<ASTCDCompilationUnit> parse(String file, Collection<Path> dirs) {
    return dirs.stream()
        .flatMap(directory -> this.parse(file, directory).stream())
        .collect(Collectors.toList());
  }

  public Collection<ASTCDCompilationUnit> parse(String fileExt, Path directory) {
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().endsWith(fileExt))
          .map(Path::toString)
          .map(this::parse)
          .collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error("0xA1063 Error while traversing the file structure `" + directory + "`.", e);
    }
    return Collections.emptySet();
  }
}
