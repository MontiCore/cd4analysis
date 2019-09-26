/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.parser;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CD4AnalysisParserTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSocNet() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/SocNet.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testAutomaton() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Automaton.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testFeatureModel() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/FeatureModel.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample1() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example1.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample2() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example2.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }

  @Test
  public void testReadOnly() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/ReadOnly.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
    List<ASTCDAssociation> assoc = cdDef.get().getCDDefinition().getCDAssociationList();
    assertEquals(4, assoc.size());
    Set<ASTCDAssociation> set = assoc.stream().filter(a -> a.isReadOnly()).collect(Collectors.toSet());
    assertEquals(3, set.size());
  }
  
}
