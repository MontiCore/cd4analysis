/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine;

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import static com.google.common.collect.Lists.newArrayList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SourceMapAwareTemplateController extends TemplateController {
  
  public SourceMapAwareTemplateController(GeneratorSetup setup, String templatename,
      SourceMapData sourceMapInfo) {
    super(setup, templatename);
    this.sourceMapInfo = sourceMapInfo;
  }
  
  protected final SourceMapData sourceMapInfo;
  
  public void report(String text) {
    sourceMapInfo.report(text);
  }
  
  public void reportTemplateName(String templateFile) {
    sourceMapInfo.sourceMapInfo.peek().templateFile = templateFile;
  }
  
  public void reportSource(ASTNode modelSource) {
    sourceMapInfo.sourceMapInfo.peek().modelSource = modelSource;
  }
  
  public void reportDecorator(String dec) {
    sourceMapInfo.sourceMapInfo.peek().decorator = dec;
  }
  
  protected int bufferOffset() {
    return sourceMapInfo.buffers.stream().mapToInt(f -> f.length()).sum();
  }
  
  protected void enterNewTemplate(int pos, String text) {
    sourceMapInfo.enterNewTemplate(pos + bufferOffset(), text);
  }
  
  protected void popNewTemplate(int endPos) {
    sourceMapInfo.popNewTemplate(endPos + bufferOffset());
  }
  
  public void writeArgs(final String templateName, final Path filePath, final ASTNode ast,
      final List<Object> templateArguments) {
    final String qualifiedTemplateName = completeQualifiedName(templateName);
    
    this.enterNewTemplate(0, "writeArgs," + templateName);
    this.report("," + ast.get_SourcePositionStart());
    
    StringBuilder content = new StringBuilder();
    // add trace to source-model:
    if (config.isTracing() && config.getModelName().isPresent()) {
      content.append(config.getCommentStart()).append(" generated from model ").append(config
          .getModelName().get()).append(" ").append(config.getCommentEnd()).append("\n");
    }
    
    int beforeTemplatesLength = content.length();
    
    List<HookPoint> templateForwardings = config.getGlex().getTemplateForwardings(templateName,
        ast);
    int contentLengthBefore = content.length();
    for (HookPoint tn : templateForwardings) {
      this.enterNewTemplate(contentLengthBefore, "writeArgsF");
      content.append(tn.processValue(this, ast, templateArguments));
      int contentLengthAfter = content.length();
      this.report("##" + tn);
      this.popNewTemplate(contentLengthAfter);
      
      contentLengthBefore = contentLengthAfter;
    }
    
    if (content.length() == beforeTemplatesLength) {
      Log.warn("0xA4057 Template " + qualifiedTemplateName + " produced no content for.");
    }
    this.popNewTemplate(contentLengthBefore);
    
    Path completeFilePath;
    if (filePath.isAbsolute()) {
      completeFilePath = filePath;
    }
    else {
      completeFilePath = Paths.get(config.getOutputDirectory().getAbsolutePath(), filePath
          .toString());
    }
    
    Reporting.reportFileCreation(qualifiedTemplateName, filePath, ast);
    
    FileReaderWriter.storeInFile(completeFilePath, content.toString());
    
    Log.debug(completeFilePath + " written successfully!", this.getClass().getName());
    
    Reporting.reportFileFinalization(qualifiedTemplateName, filePath, ast);
    
    if (!sourceMapInfo.sourceMapInfo.isEmpty()) {
      Log.warn("0xTODO: Source map not popped?");
    }
    
    System.err.println("---");
    
    List<Integer> newLines = new LinkedList<>();
    newLines.add(0);
    for (int i = 0, l = content.length(); i < l; i++) {
      if (content.charAt(i) == '\n') {
        newLines.add(i + 1);
      }
    }
    
    IdentityHashMap<ASTNode, Integer> mostLikelyDecoratedASTNodeNumbers = new IdentityHashMap<>();
    mostLikelyDecoratedASTNodeNumbers.put(null, 0);
    
    System.err.println(completeFilePath.toAbsolutePath());
    for (var i : sourceMapInfo.infos) {
      int nr = mostLikelyDecoratedASTNodeNumbers.computeIfAbsent(i.modelSource,
          astNode -> mostLikelyDecoratedASTNodeNumbers.size());
      System.err.println(asSourcePosition(i.before, newLines) + "->" + asSourcePosition(i.end,
          newLines) + ":\"" + i.text + "\" (dec: " + i.decorator + ") (t:" + i.templateFile + ")"
          + " astIndex:" + nr);
    }
    System.err.println(contentLengthBefore);
    System.err.println("---");
    System.err.println();
    System.err.println();
    sourceMapInfo.buffers.clear();
    sourceMapInfo.infos.clear();
    
    for (var e : mostLikelyDecoratedASTNodeNumbers.entrySet()) {
      if (e.getKey() == null)
        continue;
      // TODO: Also add mapping between ast nodes??
    }
    
  }
  
  protected String asSourcePosition(int index, List<Integer> newLines) {
    int line = Collections.binarySearch(newLines, index);
    if (line >= 0) // found an exact line-end
    {
      return (line + 1) + ":" + 1;
    }
    line = (-line) - 1; // insertion point
    
    int column = (line == 0) ? index + 1 : index - newLines.get(line - 1) + 1;
    return (line) + ":" + column;
  }
  
  /*
   *  [generatedColumn, sourceFileIndex, sourceLine, sourceColumn, nameIndex]
   *
   *  [generatedColumn, generatedLength,
   *   modelSourceFileIndex, modelSourceLine, modelSourceColumn,
   *   templateFileIndex,
   *   decoratorIndex,
   *   decoratorDataIndex
   * ]
   *
   * // TODO: Decorated CD vs original CD?
   *
   */
  
  @Override
  public StringBuilder include(List<String> templatenames, List<ASTNode> astlist) {
    StringBuilder ret = new StringBuilder();
    for (String template : templatenames) {
      for (ASTNode ast : astlist) {
        List<HookPoint> templateForwardings = config.getGlex().getTemplateForwardings(template,
            ast);
        for (HookPoint templateHp : templateForwardings) {
          this.enterNewTemplate(ret.length(), "include," + template + "/" + templateHp);
          ret.append(templateHp.processValue(this, ast));
          this.popNewTemplate(ret.length());
        }
      }
    }
    
    return ret;
  }
  
  @Override
  StringBuilder runInEngine(List<Object> passedArguments, Template template, ASTNode ast) {
    StringBuilder ret = new StringBuilder();
    sourceMapInfo.pushBuffer(ret);
    
    // add trace to template:
    if (config.isTracing() && isTemplateNoteGenerated(template)) {
      ret.append(config.getCommentStart()).append(" generated by template ").append(template
          .getName()).append(config.getCommentEnd()).append("\n");
    }
    
    if (template != null) {
      // Initialize standard-data for template
      
      // get ast
      if (ast == null) {
        ast = getAST();
      }
      
      TemplateController tc = config.getNewTemplateController(template.getName());
      
      SimpleHash d = config.getGlex().getGlobalData();
      Optional<TemplateModel> oldAst = Optional.empty();
      
      try {
        if (d.containsKey(AST)) {
          oldAst = Optional.ofNullable(d.get(AST));
        }
        for (var key : d.toMap().keySet()) {
          if (key instanceof String) {
            tc.data.put((String) key, d.get((String) key));
          }
        }
      }
      catch (TemplateModelException e) {
        
        String usage = this.templatename != null ? " (" + this.templatename + ")" : "";
        Log.error("0xA0128 Globally defined data could not be passed to the called template "
            + usage + ". ## This is an internal"
            + "error that should not happen. Try to remove all global data. ##");
      }
      
      d.put(TC, tc);
      d.put(AST, ast);
      
      tc.data.put(AST, ast);
      tc.data.put(TC, tc);
      tc.data.put(GLEX, config.getGlex());
      tc.arguments = newArrayList(passedArguments);
      
      // Run template with data to create output
      config.getFreeMarkerTemplateEngine().run(ret, tc.data, template);
      
      if (oldAst.isPresent()) {
        d.put(AST, oldAst.get());
      }
      
    }
    else {
      // no template
      String usage = this.templatename != null ? " (used in " + this.templatename + ")" : "";
      Log.error("0xA0127 Missing template " + usage + " ## You have tried to use a template that "
          + "doesn't exist. It may be in another package? The name printed is the qualified version, "
          + "but you may have used the unqualified name. ##");
    }
    sourceMapInfo.popBuffer();
    return ret;
  }
  
}
