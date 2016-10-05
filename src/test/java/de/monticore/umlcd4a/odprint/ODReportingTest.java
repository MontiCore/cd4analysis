/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.odprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._od.CD4Analysis2OD;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.reporting.CD4ANodeIdentHelper;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ODReportingTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  

  @Test
  public void printCD() throws IOException {
    // Parsing input
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/prettyprint/Example1.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
    
    IndentPrinter printer = new IndentPrinter();
    ReportingRepository reporting = new ReportingRepository(new CD4ANodeIdentHelper());
    CD4Analysis2OD visitor = new CD4Analysis2OD(printer, reporting);
    
    // prettyprinting input
    String output = visitor.printObjectDiagram("Example1", cdDef.get());
    
    // TODO MB after next release: Parse the output 
  }
  
}
