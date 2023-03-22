/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.base.Preconditions;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
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
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

      if (!cmd.hasOption("i") || cmd.hasOption("h")) {
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
      Collection<ASTCDCompilationUnit> asts = this.parse(".cd", this.createModelPath(cmd).getEntries());
      Log.enableFailQuick(true);

      asts.forEach(this::transform);

      if (cmd.hasOption("path")) {
        String[] paths = splitPathEntries(cmd.getOptionValue("path"));
        CD4CodeMill.globalScope().setSymbolPath(new MCPath(paths));
      }

      Collection<ICD4CodeArtifactScope> scopes = asts.stream()
        .map(ast -> createSymbolTable(ast, cmd.hasOption("c2mc")))
        .collect(Collectors.toList());

      if (cmd.hasOption("v")) {
        printVersion();
      }

      if (cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        asts.forEach(this::runCoCos);
        Log.enableFailQuick(true);
      }

      String outputPath = (cmd.hasOption("o")) ? cmd.getOptionValue("o") : "";
      if (cmd.hasOption("s")) {
        for (ICD4CodeArtifactScope scope: scopes) {
          Optional<ASTCDCompilationUnit> optAST = asts.stream()
            .filter(a -> a.getCDDefinition().getName().equals(scope.getName()))
            .findFirst();
          if(optAST.isEmpty()) {
            continue;
          }
          ASTCDCompilationUnit ast = optAST.get();
          String symbolOutput = cmd.getOptionValue("s") +
            (ast.isPresentMCPackageDeclaration()
              ? ast.getMCPackageDeclaration().getMCQualifiedName().getQName().replaceAll("\\.", "/")
              : "") +
            "/" +
            ast.getCDDefinition().getName() +
            ".cdsym";
          storeSymTab(scope, symbolOutput);
        }
      }

      if (cmd.hasOption("o")) {
        GlobalExtensionManagement glex = new GlobalExtensionManagement();
        glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
        GeneratorSetup setup = new GeneratorSetup();

        // setup default package when generating
        CD4CodeTraverser t = CD4CodeMill.traverser();
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

        setup.setGlex(glex);
        setup.setOutputDirectory(new File(outputPath));

        CDGenerator generator = new CDGenerator(setup);
        String configTemplate = cmd.getOptionValue("ct", "cd2java.CD2Java");
        TemplateController tc = setup.getNewTemplateController(configTemplate);
        TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
        List<Object> configTemplateArgs = Arrays.asList(glex, generator);

        asts.forEach(this::mapCD4CImports);

        asts.forEach(ast -> addGettersAndSetters(ast, glex));

        asts.forEach(ast -> hpp.processValue(tc, ast, configTemplateArgs));
      }

    } catch (ParseException e) {
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
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
   * prints the symboltable of the given ast out to a file
   *
   * @param scope symboltable of the current ast
   * @param path location of the file containing the printed table
   */
  public void storeSymTab(ICD4CodeArtifactScope scope, String path) {
    CD4CodeSymbols2Json s2j = new CD4CodeSymbols2Json();
    s2j.store(scope, path);
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
   * creates a symboltable for the current ast using the CD4CodeScopesGenitor
   *
   * @param ast the input ast
   * @return the symboltable of the ast
   */
  public ICD4CodeArtifactScope createSymbolTable(ASTCDCompilationUnit ast, boolean java) {
    CD4CodeScopesGenitorDelegatorTOP genitor = CD4CodeMill.scopesGenitorDelegator();
    ICD4CodeArtifactScope scope = genitor.createFromAST(ast);
     this.addDefaultImports(scope, java);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    return scope;
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
    for (ASTCDClass c: ast.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute: c.getCDAttributeList()) {
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
   * Updates the map of cd types to import statement in the given cd4c object,
   * adding the imports for each cd type (classes, enums, and interfaces)
   * defined in the given ast.
   *
   * @param ast the input ast
   */
  public void mapCD4CImports(ASTCDCompilationUnit ast) {
    CD4C cd4c = CD4C.getInstance();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();
    for (ASTCDClass cdClass: ast.getCDDefinition().getCDClassesList()) {
      for (ASTMCImportStatement i: imports) {
        String qName = i.getQName();
        cd4c.addImport(cdClass, i.isStar() ? qName + ".*" : qName);
      }
    }
    for (ASTCDInterface cdInterface: ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTMCImportStatement i: imports) {
        cd4c.addImport(cdInterface, i.getQName());
      }
    }
    for (ASTCDEnum cdEnum: ast.getCDDefinition().getCDEnumsList()) {
      for (ASTMCImportStatement i: imports) {
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
    return dirs.stream().flatMap(directory -> this.parse(file, directory).stream()).collect(Collectors.toList());
  }

  public Collection<ASTCDCompilationUnit> parse(String fileExt, Path directory) {
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(fileExt)).map(Path::toString)
        .map(this::parse)
        .collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error("0xA1063 Error while traversing the file structure `" + directory + "`.", e);
    }
    return Collections.emptySet();
  }


}
