/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdtool.mcp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.monticore.CD4CodeTool;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import de.se_rwth.commons.mcp.McpHttpServer;
import de.se_rwth.commons.mcp.ToolHandler;
import de.se_rwth.commons.mcp.ToolResult;

import javax.annotation.Nullable;

import static de.se_rwth.commons.mcp.SchemaUtil.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MCP server exposing the functionality of the {@link Tool}.
 * Every tool accepts the classdiagram either as model text ({@code model})
 * or as a path to a model file ({@code file}).
 */
public class CD4CodeMcpServer extends McpHttpServer {
  
  protected final CD4CodeTool tool;
  
  public CD4CodeMcpServer(CD4CodeTool tool) {
    this.tool = tool;
    this._version = captureVersion();
    
    addTool("check_classdiagram",
        "Parses a classdiagram model, builds its symbol table and checks all context conditions. "
            + "Returns all errors and warnings found.", modelSchema(), args -> withModel(args,
                ast -> "The classdiagram '" + ast.getCDDefinition().getName()
                    + "' is well-formed."));
    
    addTool("pretty_print_classdiagram",
        "Parses an classdiagram model (same as check_classdiagram) and returns it pretty printed.",
        modelSchema(), args -> withModel(args, ast -> CD4CodeMill.prettyPrint(ast, true)));
    
    addTool("reports_classdiagram",
        "Parses an classdiagram model (same as check_classdiagram) and returns the reports (details) on it.",
        modelSchema(), args -> withModel(args, ast -> {
          StringBuilder sb = new StringBuilder();
          sb.append("Reports of the model ").append(ast.getCDDefinition().getName()).append(":\n");
          tool.doReport(ast, sb);
          return sb.toString();
        }));
    
    addTool("classdiagram_to_plantuml",
        "Parses an classdiagram model (same as check_classdiagram) and returns it pretty printed.",
        modelSchema("svg", booleanProperty(
            "True if the output should be a svg, otherwise puml is returned."), "showAttributes",
            booleanProperty("True if the attributes should be shown (optional)."),
            "showAssociations", booleanProperty(
                "True if the associations should be shown (optional)."), "showRoles",
            booleanProperty("True if the rules should be shown (optional)."), "showCardinality",
            booleanProperty("True if the associations should be shown (optional)."), "showModifier",
            booleanProperty("True if the modifiers should be shown (optional)."), "orthogonal",
            booleanProperty("True if the layout should be orthogonal (optional)."), "shortenWords",
            booleanProperty("True if the words should be shortened (optional)."), "showComments",
            booleanProperty("True if the comments should be shown (optional)."), "nodeSeparator",
            integerProperty("The node seperator integer value (optional)."), "rankSeparator",
            integerProperty("The rank seperator integer value (optional).")), args -> withModel(
                args, ast -> {
                  final PlantUMLConfig plantUMLConfig = new PlantUMLConfig();
                  
                  plantUMLConfig.setShowAtt(optionalBoolean(args, "showAttributes", true));
                  plantUMLConfig.setShowAssoc(optionalBoolean(args, "showAssociations", true));
                  plantUMLConfig.setShowRoles(optionalBoolean(args, "showRoles", true));
                  plantUMLConfig.setShowCard(optionalBoolean(args, "showCardinality", true));
                  plantUMLConfig.setShowModifier(optionalBoolean(args, "showModifier", true));
                  plantUMLConfig.setOrtho(optionalBoolean(args, "orthogonal", true));
                  plantUMLConfig.setShortenWords(optionalBoolean(args, "shortenWords", true));
                  plantUMLConfig.setShowComments(optionalBoolean(args, "showComments", true));
                  plantUMLConfig.setNodesep(optionalInteger(args, "nodeSeparator", -1));
                  plantUMLConfig.setRanksep(optionalInteger(args, "rankSeparator", -1));
                  
                  return PlantUMLUtil.toPlantUmlModelString(Optional.ofNullable(ast),
                      plantUMLConfig);
                }));
  }
  
  @Override
  public String getServerName() { return "cd4code"; }
  
  protected String _version;
  
  protected String captureVersion() {
    // Note to self: Expose "getVersion" via the tool class
    PrintStream originalOut = System.out;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    
    try (
        PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)
    ) {
      System.setOut(capture);
      tool.printVersion();
    }
    finally {
      System.setOut(originalOut);
    }
    
    return output.toString(StandardCharsets.UTF_8);
  }
  
  @Override
  public String getServerVersion() { return this._version; }
  
  @Override
  public String getInstructions() {
    // Note to self: Serve from resources?
    return """
        Tools for the MontiCore CD4Code DSL. Example model:
        package mypackage
        classdiagram MyCD {
          class A extends B {
            String s;
          }
        
          class B implements C {
            B();
            boolean foo(int i);
          }
        
          association [1] A -> B [*];
        
          interface C;
        }""";
  }
  
  protected static boolean hasErrors() {
    return Log.getFindings().stream().anyMatch(Finding::isError);
  }
  
  protected void setErrorHook() {
    Log.setErrorHook(() -> {
      throw new MCFatalError(Log.getFindings().stream().map(Finding::buildMsg).collect(Collectors
          .joining("\n")));
    });
  }
  
  // MCP schema for the model input
  protected static JsonObject modelSchema(Object... nameSchemaPairs) {
    Object[] nameSchemaPairsWithModel = new Object[nameSchemaPairs.length + 4];
    
    nameSchemaPairsWithModel[0] = "model";
    nameSchemaPairsWithModel[1] = stringProperty("The classdiagram model as text");
    nameSchemaPairsWithModel[2] = "file";
    nameSchemaPairsWithModel[3] = stringProperty(
        "Path to a classdiagram model file (alternative to 'model')");
    System.arraycopy(nameSchemaPairs, 0, nameSchemaPairsWithModel, 4, nameSchemaPairs.length);
    return objectSchema(properties(nameSchemaPairsWithModel));
  }
  
  /**
   * Loads and parses the model given in the arguments, builds its symbol
   * table and checks the context conditions. If no errors occurred,
   * {@code action} is applied to the AST. All findings are reported.
   */
  protected ToolResult withModel(JsonObject args, Function<ASTCDCompilationUnit, String> action)
      throws IOException {
    String model = optionalString(args, "model");
    String file = optionalString(args, "file");
    if ((model == null) == (file == null)) {
      throw new ToolHandler.InvalidParamsException(
          "Exactly one of the arguments 'model' or 'file' is required");
    }
    if (file != null) {
      model = Files.readString(Paths.get(file));
    }
    
    setErrorHook();
    Log.enableFailQuick(true);
    Log.getFindings().clear();
    CD4CodeMill.globalScope().clear();
    
    @Nullable
    String result = null;
    Optional<ASTCDCompilationUnit> ast = Optional.empty();
    try {
      // First parse (future work: handle filepath)
      ast = CD4CodeMill.parser().parse_String(model);
      if (ast.isPresent()) {
        new CD4CodeAfterParseTrafo().transform(ast.get());
        
        // ALu: I really dislike the setup of the CD4CodeTool
        
        ICD4CodeArtifactScope artifactScope = tool.createSymbolTable(ast.get());
        artifactScope.addImports(new ImportStatement("java.lang", true));
        
        BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
        
        ast.get().accept(new CD4CodeSymbolTableCompleter(ast.get()).getTraverser());
        
        tool.runDefaultCoCos(ast.get());
        
        result = action.apply(ast.get());
      }
    }
    catch (MCFatalError fatalError) {
      // Log.error was called
    }
    List<Finding> findings = new ArrayList<>(Log.getFindings());
    boolean hasErrors = findings.stream().anyMatch(Finding::isError) || result == null;
    Log.getFindings().clear();
    
    StringBuilder resultText = new StringBuilder();
    if (result != null) {
      resultText.append(result);
    }
    else {
      resultText.append(ast.isPresent() ? "The model contains errors."
          : "The model could not be parsed.");
    }
    for (Finding f : findings) {
      resultText.append('\n').append(f.getType()).append(": ").append(f.buildMsg());
    }
    return new ToolResult(resultText.toString(), hasErrors);
  }
  
  public static JsonObject booleanProperty(String description) {
    JsonObject p = new JsonObject();
    p.addProperty("type", "Bool");
    p.addProperty("description", description);
    return p;
  }
  
  public static JsonObject integerProperty(String description) {
    JsonObject p = new JsonObject();
    p.addProperty("type", "Int");
    p.addProperty("description", description);
    return p;
  }
  
  /**
   * @return the argument {@code name}
   */
  public static boolean optionalBoolean(JsonObject arguments, String name, boolean def) {
    JsonElement e = arguments.get(name);
    if (e == null || e.isJsonNull()) {
      return def;
    }
    if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isBoolean()) {
      throw new ToolHandler.InvalidParamsException("Argument '" + name + "' must be a boolean");
    }
    return e.getAsBoolean();
  }
  
  public static int optionalInteger(JsonObject arguments, String name, int def) {
    JsonElement e = arguments.get(name);
    if (e == null || e.isJsonNull()) {
      return def;
    }
    if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isNumber()) {
      throw new ToolHandler.InvalidParamsException("Argument '" + name + "' must be a boolean");
    }
    return e.getAsNumber().intValue();
  }
  
}
