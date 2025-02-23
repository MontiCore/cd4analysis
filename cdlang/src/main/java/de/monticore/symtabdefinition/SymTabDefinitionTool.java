// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.FileReaderWriter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symtabdefinition._symboltable.ISymTabDefinitionArtifactScope;
import de.monticore.symtabdefinition._symboltable.ISymTabDefinitionGlobalScope;
import de.monticore.symtabdefinition._symboltable.SymTabDefinitionArtifactScope;
import de.monticore.symtabdefinition._symboltable.SymTabDefinitionFullSymbolTableCompleter;
import de.monticore.symtabdefinition._symboltable.SymTabDefinitionSymbols2Json;
import de.monticore.symtabdefinition.cocos.SymTabDefinitionCoCos;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymTabDefinitionTool extends SymTabDefinitionToolTOP {

  protected static final String LOG_NAME = "SymTabDefinitionTool";

  protected static final String MODEL_FILE_EXTENSION = "symtabdefinition";

  protected static final String SYMTAB_FILE_EXTENSION = "symtabdefinitionsym";

  protected static final String INPUT_SYMBOL_FILE_EXTENSION_END = "sym";

  protected static final String PRETTYPRINTED_OUT_DIRECTORY =
      "target" + File.separator + "prettyprinted";

  protected static final String SYMBOLS_OUT_DIRECTORY = "target" + File.separator + "symbols";

  protected static final String OPTION_HELP = "h";
  protected static final String OPTION_VERSION = "v";
  protected static final String OPTION_INPUT = "i";
  protected static final String OPTION_PRETTYPRINT = "pp";
  protected static final String OPTION_COCOS = "c";
  protected static final String OPTION_SYMBOLTABLE = "s";
  protected static final String OPTION_PATH = "path";
  protected static final String OPTION_CLASS2MC = "c2mc";
  protected static final String OPTION_NOCOLLECTIONTYPES = "nt";

  public static void main(String[] args) {
    SymTabDefinitionTool tool = new SymTabDefinitionTool();
    tool.run(args);
  }

  @Override
  public void run(String[] args) {
    Log.ensureInitialization();
    SymTabDefinitionMill.globalScope().clear();
    SymTabDefinitionMill.init();
    Options options = initOptions();
    try {
      CommandLine cmd = new DefaultParser().parse(options, args);

      // help: when --help
      if (cmd.hasOption(OPTION_HELP)) {
        printHelp(options);
        // do not continue, when help is printed.
        return;
      }
      // version: when --version
      else if (cmd.hasOption(OPTION_VERSION)) {
        printVersion();
        return;
      }

      if (!cmd.hasOption(OPTION_INPUT)) {
        printHelp(options);
        Log.info("Input Option \"-i\" missing. Exiting", LOG_NAME);
        return;
      }

      // get input files
      List<String> inputNames = getInputFiles(List.of(cmd.getOptionValues(OPTION_INPUT)));
      // split input into models and symbol files
      List<String> modelInputNames = new ArrayList<>();
      List<String> symbolInputNames = new ArrayList<>();
      for (String inputName : inputNames) {
        if (isLikelySymbolInputFilePath(inputName)) {
          symbolInputNames.add(inputName);
        } else {
          modelInputNames.add(inputName);
        }
      }
      if (modelInputNames.isEmpty()) {
        Log.info("No input models found. Exiting", LOG_NAME);
      }

      // parse input files, now known to be available
      List<ASTCDCompilationUnit> inputASTs = new ArrayList<>();
      for (String modelInputName : modelInputNames) {
        ASTCDCompilationUnit ast = parse(modelInputName);
        // transformations which are necessary to do after parsing
        new CD4CodeDirectCompositionTrafo().transform(ast);
        inputASTs.add(ast);
      }

      // -option pretty print
      if (cmd.hasOption(OPTION_PRETTYPRINT)) {
        if (cmd.getOptionValues(OPTION_PRETTYPRINT) == null
            || cmd.getOptionValues(OPTION_PRETTYPRINT).length == 0) {
          for (ASTCDCompilationUnit compilationUnit : inputASTs) {
            prettyPrintInFolder(compilationUnit, PRETTYPRINTED_OUT_DIRECTORY);
          }
        } else if (cmd.getOptionValues(OPTION_PRETTYPRINT).length == 1
            && isLikelyFolderPath(cmd.getOptionValue(OPTION_PRETTYPRINT))) {
          for (ASTCDCompilationUnit compilationUnit : inputASTs) {
            prettyPrintInFolder(compilationUnit, cmd.getOptionValue(OPTION_PRETTYPRINT));
          }
        } else if (cmd.getOptionValues(OPTION_PRETTYPRINT).length == inputASTs.size()
            && cmd.getOptionValues(OPTION_PRETTYPRINT).length
                == cmd.getOptionValues(OPTION_PRETTYPRINT).length) {
          for (int i = 0; i < inputASTs.size(); i++) {
            prettyPrint(inputASTs.get(i), cmd.getOptionValues(OPTION_PRETTYPRINT)[i]);
          }
        } else {
          Log.error(
              String.format(
                  "Received '%s' output files for the prettyprint option. "
                      + "Expected that '%s' many output files are specified. "
                      + "If output files for the prettyprint option are specified, then the number "
                      + "of specified output files must be equal to the number of specified input files, "
                      + "or one outputfolder should be specified.",
                  cmd.getOptionValues(OPTION_PRETTYPRINT).length, inputASTs.size()));
        }
      }

      if (cmd.hasOption(OPTION_COCOS) || cmd.hasOption(OPTION_SYMBOLTABLE)) {
        // setup globalscope
        ISymTabDefinitionGlobalScope gs = SymTabDefinitionMill.globalScope();
        // add default symbols
        BasicSymbolsMill.initializePrimitives();
        // add collection types
        if (!cmd.hasOption(OPTION_NOCOLLECTIONTYPES)) {
          BuiltInTypes.setUpCollectionTypes(gs);
        }
        // add class2mc
        if (cmd.hasOption(OPTION_CLASS2MC)) {
          initializeClass2MC();
        }
        // set symbol path
        String[] symTabInPath = {"."};
        if (cmd.hasOption(OPTION_PATH)) {
          symTabInPath = cmd.getOptionValues(OPTION_PATH);
        }
        for (String path : symTabInPath) {
          gs.getSymbolPath().addEntry(Paths.get(path));
        }

        // load input symbol tables
        for (String symbolInputName : symbolInputNames) {
          ISymTabDefinitionArtifactScope symbolScope = loadSymbols(symbolInputName);
          SymTabDefinitionMill.globalScope().addSubScope(symbolScope);
        }

        // symbol tables
        for (ASTCDCompilationUnit ast : inputASTs) {
          createSymbolTable(ast);
        }
        for (ASTCDCompilationUnit ast : inputASTs) {
          completeSymbolTable(ast);
        }

        // CoCos
        Log.enableFailQuick(false);
        for (ASTCDCompilationUnit ast : inputASTs) {
          runDefaultCoCos(ast);
        }
        // to not proceed if CoCos fail
        if (Log.getErrorCount() > 0) {
          Log.warn("encountered errors, will not proceed");
          return;
        }
        Log.enableFailQuick(true);
        if (cmd.hasOption(OPTION_COCOS)) {
           Log.info("All CoCo checks passed.", LOG_NAME);
        }

        // store symbols
        if (cmd.hasOption(OPTION_SYMBOLTABLE)) {
          if (cmd.getOptionValues(OPTION_SYMBOLTABLE) == null
              || cmd.getOptionValues(OPTION_SYMBOLTABLE).length == 0) {
            for (ASTCDCompilationUnit compilationUnit : inputASTs) {
              storeSymbolsInFolder(compilationUnit, SYMBOLS_OUT_DIRECTORY);
            }
          } else if (cmd.getOptionValues(OPTION_SYMBOLTABLE).length == 1
              && isLikelyFolderPath(cmd.getOptionValue(OPTION_SYMBOLTABLE))) {
            inputASTs.forEach(
                compUnit ->
                    this.storeSymbolsInFolder(compUnit, cmd.getOptionValue(OPTION_SYMBOLTABLE)));
          } else if (cmd.getOptionValues(OPTION_SYMBOLTABLE).length == inputASTs.size()
              && cmd.getOptionValues(OPTION_SYMBOLTABLE).length
                  == cmd.getOptionValues("i").length) {
            for (int i = 0; i < inputASTs.size(); i++) {
              storeSymbols(inputASTs.get(i), cmd.getOptionValues(OPTION_SYMBOLTABLE)[i]);
            }
          } else {
            Log.error(
                String.format(
                    "Received '%s' output files for the storesymbols option. "
                        + "Expected that '%s' many output files are specified. "
                        + "If output files for the storesymbols option are specified, then the number "
                        + "of specified output files must be equal to the number of specified input files, "
                        + "or one outputfolder should be specified.",
                    cmd.getOptionValues(OPTION_SYMBOLTABLE).length, inputASTs.size()));
          }
        }
      }

    } catch (AmbiguousOptionException e) {
      Log.error(String.format("0xCE0E2: option '%s' can't match any valid option", e.getOption()));
    } catch (UnrecognizedOptionException e) {
      Log.error(String.format("0xCE0E3: unrecognized option '%s'", e.getOption()));
    } catch (MissingOptionException e) {
      Log.error(
          String.format(
              "0xCE0E4: options [%s] are missing, but are required",
              Joiners.COMMA.join(e.getMissingOptions())));
    } catch (MissingArgumentException e) {
      Log.error(String.format("0xCE0E5: option '%s' is missing an argument", e.getOption()));
    } catch (Exception e) {
      Log.error("0xCE0E7: an error occurred.", e);
    }
  }

  @Override
  public Options addStandardOptions(Options options) {
    /* generated by template _cli.AddStandardOptions and modified */
    // help
    options.addOption(
        Option.builder(OPTION_HELP).longOpt("help").desc("Prints this help dialog").build());

    // version
    options.addOption(
        Option.builder(OPTION_VERSION)
            .longOpt("version")
            .desc("Prints version information")
            .build());

    // parse input file
    options.addOption(
        Option.builder(OPTION_INPUT)
            .longOpt("input")
            .argName("files")
            .hasArgs()
            .desc("Reads the source file(s) (mandatory) and parses the contents")
            .build());

    // pretty print runner
    options.addOption(
        Option.builder(OPTION_PRETTYPRINT)
            .longOpt("prettyprint")
            .argName("files")
            .optionalArg(true)
            .desc("Prints the AST to stdout or the specified file (optional)")
            .build());

    // check cocos
    options.addOption(
        Option.builder(OPTION_COCOS)
            .longOpt("coco")
            .desc("Checks the CoCos for the input.")
            .build());

    // pretty print SC
    options.addOption(
        Option.builder(OPTION_SYMBOLTABLE)
            .longOpt("symboltable")
            .argName("file")
            .optionalArg(true)
            .hasArg()
            .desc("Serializes the symbol table of the given artifact.")
            .build());

    // model paths
    options.addOption(
        Option.builder(OPTION_PATH)
            .hasArgs()
            .desc("Sets the artifact path for imported symbols, space separated.")
            .build());

    return options;
  }

  @Override
  public Options addAdditionalOptions(
      Options options) {

    options.addOption(
        Option.builder(OPTION_CLASS2MC)
            .longOpt("class2mc")
            .desc("Enables to resolve java classes in the model path")
            .build());

    options.addOption(
        Option.builder(OPTION_NOCOLLECTIONTYPES)
            .longOpt("no-built-in-types")
            .desc("If this option is used, built-in collection types will not be considered.")
            .build());

    return options;
  }

  @Override
  public void completeSymbolTable(ASTCDCompilationUnit ast) {
    ast.accept(new SymTabDefinitionFullSymbolTableCompleter().getTraverser());
  }

  @Override
  public void runDefaultCoCos(ASTCDCompilationUnit ast) {
    new SymTabDefinitionCoCos().getCheckerForAllCoCos().checkAll(ast);
  }

  protected void initializeClass2MC() {
    SymTabDefinitionMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    SymTabDefinitionMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  // helper

  /**
   * heuristic to test if the path seems to be a folder path
   *
   * @param pathStr the path to check
   * @return whether we assume it is a path to a folder
   */
  protected boolean isLikelyFolderPath(String pathStr) {
    // if it already exists, check:
    Path path = Paths.get(pathStr);
    File file = path.toFile();
    if (file.exists()) {
      return file.isDirectory();
    }
    // if it does not exist yet,
    // check if the last part ends with an extension
    // note that "a/b/.c" is expected to be a folder,
    // "a/b/c.d" is not expected to be a folder,
    // so we skip the first character
    return !path.getFileName().toString().substring(1).contains(".");
  }

  public String getFilePathForFileOrDir(ISymTabDefinitionArtifactScope scope, String path) {
    if (!isLikelyFolderPath(path)) {
      return path;
    } else {
      return Paths.get(
              path, Names.getPathFromPackage(scope.getFullName()) + "." + SYMTAB_FILE_EXTENSION)
          .toString();
    }
  }

  protected List<String> getInputFiles(List<String> inputNames) {
    List<String> inputFiles = getInputFileNamesFromInputParameter(inputNames);
    // did we get an input folder?
    if (inputNames.size() == 1 && Paths.get(inputNames.get(0)).toFile().isDirectory()) {
      try {
        inputFiles =
            Files.walk(Paths.get(inputNames.get(0)))
                .filter(
                    path ->
                        path.toString().endsWith("." + MODEL_FILE_EXTENSION)
                            || path.toString().endsWith(INPUT_SYMBOL_FILE_EXTENSION_END))
                .map(Path::toString)
                .collect(Collectors.toList());
      } catch (IOException e) {
        Log.error("0xAC928 Unable to collect SymTabDefinition input files", e);
      }
    }
    return inputFiles;
  }

  /**
   * CLI-parameter "-i" can take file and directory names this function returns only file names
   *
   * @param inputNames the input given to "-i"
   * @return list of file names
   */
  protected List<String> getInputFileNamesFromInputParameter(List<String> inputNames) {
    List<String> fileNames = new ArrayList<>();
    for (String inputName : inputNames) {
      File input = new File(inputName);
      if (input.isDirectory()) {
        List<File> modelFilesinDir =
            List.of(input.listFiles((dir, name) -> name.endsWith(MODEL_FILE_EXTENSION)));
        for (File modelFile : modelFilesinDir) {
          fileNames.add(modelFile.getAbsolutePath());
        }
      } else if (input.isFile()) {
        fileNames.add(input.getAbsolutePath());
      } else {
        Log.error(
            "0xAF341 input provided by -"
                + OPTION_INPUT
                + " does not seem to be a file or directory: "
                + input.getAbsolutePath());
      }
    }
    return fileNames;
  }

  /**
   * heuristic to test if the path seems to be a file containing a symbol
   *
   * @param pathStr the path to check
   * @return whether we assume it is a path to a folder
   */
  protected boolean isLikelySymbolInputFilePath(String pathStr) {
    // if it already exists, check that it is a file:
    Path path = Paths.get(pathStr);
    File file = path.toFile();
    if (file.exists() && !file.isFile()) {
      return false;
    }
    // check if the last part ends with a corresponding extension
    return path.getFileName().toString().endsWith(INPUT_SYMBOL_FILE_EXTENSION_END);
  }

  /**
   * Loads the symbols from the symbol file filename and returns the symbol table.
   *
   * @param filename Name of the symbol file to load.
   * @return the symbol table
   */
  public ISymTabDefinitionArtifactScope loadSymbols(String filename) {
    SymTabDefinitionSymbols2Json symbols2Json = new SymTabDefinitionSymbols2Json();
    return symbols2Json.load(filename);
  }

  /**
   * finds the file (without extension) for ast, given its package and name. E.g.: model with
   * qualified name a.b.c "a/b/c"
   *
   * @param compilationUnit The ast of the model
   */
  protected String getRelativeFilePath(ASTCDCompilationUnit compilationUnit) {
    String packagePath =
        compilationUnit.isPresentMCPackageDeclaration()
            ? compilationUnit
                .getMCPackageDeclaration()
                .getMCQualifiedName()
                .getQName()
                .replace('.', File.separatorChar)
            : "";
    Path relativeFilePath = Paths.get(packagePath, compilationUnit.getCDDefinition().getName());
    return relativeFilePath.toString();
  }

  /**
   * Stores the prettyprinted model for ast in the specified folder.
   *
   * @param compilationUnit The ast of the model
   * @param folderPath The folder to store the symbols in
   */
  protected void prettyPrintInFolder(ASTCDCompilationUnit compilationUnit, String folderPath) {
    String relativeFilePath =
        getRelativeFilePath(compilationUnit).concat(".").concat(MODEL_FILE_EXTENSION);
    Path filePath = Paths.get(folderPath, relativeFilePath);
    prettyPrint(compilationUnit, filePath.toString());
  }

  /**
   * Stores the symbols for ast in the symbol file filename.
   *
   * @param compilationUnit The ast.
   * @param filename The name of the produced symbol file.
   */
  public void storeSymbols(ASTCDCompilationUnit compilationUnit, String filename) {
    SymTabDefinitionSymbols2Json symbols2Json = new SymTabDefinitionSymbols2Json();
    String serialized =
        symbols2Json.serialize((SymTabDefinitionArtifactScope) compilationUnit.getEnclosingScope());
    FileReaderWriter.storeInFile(Paths.get(filename), serialized);
  }

  /**
   * Stores the symbols for ast in the specified folder.
   *
   * @param compilationUnit The ast of the SD
   * @param folderPath The folder to store the symbols in
   */
  protected void storeSymbolsInFolder(ASTCDCompilationUnit compilationUnit, String folderPath) {
    String relativeFilePath =
        getRelativeFilePath(compilationUnit).concat(".").concat(SYMTAB_FILE_EXTENSION);
    Path filePath = Paths.get(folderPath, relativeFilePath);
    storeSymbols(compilationUnit, filePath.toString());
  }
}
