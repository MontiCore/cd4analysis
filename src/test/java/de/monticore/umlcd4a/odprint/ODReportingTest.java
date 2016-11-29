/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.odprint;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.SymbolTableReporter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.reporting.AST2ODReporter;
import de.monticore.umlcd4a.reporting.CD4ANodeIdentHelper;
import de.monticore.umlcd4a.reporting.CD4ASymbolTableReporter;
import de.monticore.umlcd4a.symboltable.CD4AGlobalScopeTestFactory;
import de.monticore.umlcd4a.symboltable.CDSymbol;
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
  
  final static String CD_FQN = "de.monticore.umlcd4a.prettyprint.SocNet";
  
  final static String PACKAGE = CD_FQN + ".";
  
  private CDSymbol cdSymbol;
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void createSTAndAST() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    cdSymbol = globalScope.<CDSymbol> resolve(CD_FQN, CDSymbol.KIND).orElse(null);
    assertNotNull(cdSymbol);
  }
  
  @Test
  public void checkODOfAST() throws IOException {
    assertTrue(cdSymbol.getAstNode().isPresent());
    ASTNode node = cdSymbol.getAstNode().get();
    assertTrue(node instanceof ASTCDDefinition);
    ASTCDDefinition cdDef = (ASTCDDefinition) node;
    
    ReportingRepository reporting = new ReportingRepository(new CD4ANodeIdentHelper());
    AST2ODReporter reporter = new AST2ODReporter("target", CD_FQN, reporting);
    reporter.flush(cdDef);
  }
  
  @Test
  public void checkODOfSymbolTable() throws IOException {
    ReportingRepository reporting = new ReportingRepository(new CD4ANodeIdentHelper());
    SymbolTableReporter reporter = new CD4ASymbolTableReporter("target", CD_FQN, reporting);
    reporter.flush(cdSymbol.getAstNode().get());
  }
  
}
