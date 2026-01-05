/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine;

import de.monticore.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SourceMapData {
  
  protected Stack<SourceMapInfo> sourceMapInfo = new Stack<>();
  protected List<SourceMapInfo> infos = new ArrayList<>();
  
  public void report(String text) {
    sourceMapInfo.peek().text += text;
  }
  
  protected void enterNewTemplate(int pos, String text) {
    sourceMapInfo.add(new SourceMapInfo(pos, text));
  }
  
  protected void popNewTemplate(int endPos) {
    SourceMapInfo info = sourceMapInfo.pop();
    info.end = endPos;
    infos.add(info);
  }
  
  Stack<StringBuilder> buffers = new Stack<>();
  
  void pushBuffer(StringBuilder stringBuilder) {
    buffers.push(stringBuilder);
  }
  
  public void popBuffer() {
    buffers.pop();
  }
  
  public static class SourceMapInfo {
    
    int before;
    int end = 0; // in generated
    ASTNode modelSource; // might not be the original node!
    String templateFile; // kind,name
    String decorator;
    
    String text;
    
    SourceMapInfo(int before, String text) {
      this.before = before;
      this.text = text;
    }
    
    // TODO: A second mapping
    
  }
  
}
