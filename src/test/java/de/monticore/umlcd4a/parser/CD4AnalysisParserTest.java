/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import mc.ast.ASTNode;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import com.google.common.base.Optional;

import de.monticore.umlcd4a._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a._parser.CDCompilationUnitMCParser;

public class CD4AnalysisParserTest {
  
  private int getDepth(ASTNode n) {
    ArrayList<Integer> depths = new ArrayList<>();
    for (ASTNode c : n.get_Children()) {
      depths.add(getDepth(c));
    }
    if (depths.isEmpty()) {
      return 0;
    }

    return Collections.max(depths) + 1;
  }

  private int getWidth(ASTNode n) {
    int width = 0;
    for (ASTNode c : n.get_Children()) {
      width+=1;
      int w  = getWidth(c);
      if (w>width) {
        width = w;
      }
    }
    return width;
  }

  @Test
  public void testSocNet() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/SocNet.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
    System.out.println();
    System.out.println(getDepth(cdDef.get()));
    System.out.println();
    System.out.println(getWidth(cdDef.get()));
    
  }
  
  @Test
  public void testExample1() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example1.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample2() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example2.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
}
