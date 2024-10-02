// (c) https://github.com/MontiCore/monticore
package de.monticore;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.stdefinition.STDefinitionMill;
import de.monticore.stdefinition._symboltable.ISTDefinitionArtifactScope;
import de.monticore.stdefinition._symboltable.ISTDefinitionGlobalScope;
import de.monticore.stdefinition._symboltable.STDefinitionFullSymbolTableCompleter;
import de.monticore.stdefinition.cocos.STDefinitionCoCos;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;

public class STDefinitionTool extends de.monticore.stdefinition.STDefinitionTool {

  protected static final String MODEL_FILE_EXTENSION = "stdefinition";

  protected static final String SYMTAB_FILE_EXTENSION = "stdefinitionsym";

  protected static final String OPTION_HELP = "h";
  protected static final String OPTION_VERSION = "v";
  protected static final String OPTION_INPUT = "i";
  protected static final String OPTION_PRETTYPRINT = "pp";
  protected static final String OPTION_SYMBOLTABLE = "s";
  protected static final String OPTION_OOSYMBOLTABLE = "soo";
  protected static final String OPTION_PATH = "path";
  protected static final String OPTION_CLASS2MC = "c2mc";
  protected static final String OPTION_NOCOLLECTIONTYPES = "nt";
  protected static final String OPTION_NOARTIFACTNAME = "na";

  public static void main(String[] args) {
    STDefinitionTool tool = new STDefinitionTool();
    tool.run(args);
  }

  @Override
  public void run(String[] args) {
    init();
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
        Log.error("0xE9B3C option 'i' missing" + ", but an input is required");
        return;
      }

      // get input model
      String modelPath = cmd.getOptionValue(OPTION_INPUT);
      if (!Files.exists(Paths.get(modelPath))) {
        Log.error("0xE9B3D unable to find input \"" + modelPath + "\"");
        return;
      }
      ASTCDCompilationUnit ast = parse(modelPath);
      new CD4CodeAfterParseTrafo().transform(ast);

      // pretty print
      if (cmd.hasOption(OPTION_PRETTYPRINT)) {
        prettyPrint(ast, cmd.getOptionValue(OPTION_PRETTYPRINT));
      }

      // transformations which are necessary to do after parsing
      new CD4CodeDirectCompositionTrafo().transform(ast);

      ISTDefinitionGlobalScope gs = STDefinitionMill.globalScope();
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

      ISTDefinitionArtifactScope artifactScope = createSymbolTable(ast);

      // remove artifact scope name
      if (cmd.hasOption(OPTION_NOARTIFACTNAME)) {
        artifactScope.setName("");
      }

      completeSymbolTable(ast);

      runDefaultCoCos(ast);

      // do not output any symbol table if errors occurred
      if (Log.getErrorCount() > 0) {
        return;
      }

      if (cmd.hasOption(OPTION_SYMBOLTABLE) || cmd.hasOption(OPTION_OOSYMBOLTABLE)) {
        if (cmd.hasOption(OPTION_SYMBOLTABLE) && cmd.hasOption(OPTION_OOSYMBOLTABLE)) {
          Log.error(
              "0xFDB3D incompatible arguments "
                  + OPTION_SYMBOLTABLE
                  + " and "
                  + OPTION_OOSYMBOLTABLE
                  + ".");
          return;
        }
        String optionValue =
            cmd.hasOption(OPTION_SYMBOLTABLE)
                ? cmd.getOptionValue(OPTION_SYMBOLTABLE)
                : cmd.getOptionValue(OPTION_OOSYMBOLTABLE);
        Path symTabOutPath;
        if (optionValue != null) {
          symTabOutPath = Paths.get(getFilePathForFileOrDir(artifactScope, optionValue));
        } else {
          String modelName = ast.getCDDefinition().getName();
          String packagePath = Names.getPathFromPackage(artifactScope.getPackageName());
          if (packagePath.isBlank()) {
            symTabOutPath = Paths.get(modelName + "." + SYMTAB_FILE_EXTENSION);
          } else {
            symTabOutPath =
                Paths.get(packagePath + File.separator + modelName + "." + SYMTAB_FILE_EXTENSION);
          }
        }
        if (cmd.hasOption(OPTION_OOSYMBOLTABLE)) {
          storeOOSymbols(artifactScope, symTabOutPath.toString());
        } else {
          storeSymbols(artifactScope, symTabOutPath.toString());
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
            .argName("file")
            .hasArg()
            .desc("Reads the source file (mandatory) and parses the contents")
            .build());

    // pretty print runner
    options.addOption(
        Option.builder(OPTION_PRETTYPRINT)
            .longOpt("prettyprint")
            .argName("file")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc("Prints the AST to stdout or the specified file (optional)")
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

    // pretty print SC
    options.addOption(
        Option.builder(OPTION_OOSYMBOLTABLE)
            .longOpt("symboltable-oo")
            .argName("file")
            .optionalArg(true)
            .hasArg()
            .desc("Serializes the symbol table of the given artifact for OOSymbols.")
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
  public org.apache.commons.cli.Options addAdditionalOptions(
      org.apache.commons.cli.Options options) {

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

    options.addOption(
        Option.builder(OPTION_NOARTIFACTNAME)
            .longOpt("no-artifact-name")
            .desc("If this option is used, no named artifact scope will exported.")
            .build());

    return options;
  }

  @Override
  public void completeSymbolTable(ASTCDCompilationUnit ast) {
    ast.accept(new STDefinitionFullSymbolTableCompleter().getTraverser());
  }

  @Override
  public void runDefaultCoCos(ASTCDCompilationUnit ast) {
    new STDefinitionCoCos().getCheckerForAllCoCos().checkAll(ast);
  }

  public void storeOOSymbols(
      de.monticore.stdefinition._symboltable.ISTDefinitionArtifactScope scope, String path) {

    OOSymbolsSymbols2Json symbols2Json = new OOSymbolsSymbols2Json();
    symbols2Json.store(scope, path);
  }

  protected void initializeClass2MC() {
    STDefinitionMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    STDefinitionMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
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

  public String getFilePathForFileOrDir(ISTDefinitionArtifactScope scope, String path) {
    if (!isLikelyFolderPath(path)) {
      return path;
    } else {
      return Paths.get(
              path, Names.getPathFromPackage(scope.getFullName()) + "." + SYMTAB_FILE_EXTENSION)
          .toString();
    }
  }
}
