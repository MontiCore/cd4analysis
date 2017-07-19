/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.odprint;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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

/**
 * @author (last commit) Marita Breuer
 */
public class ODReportingTest {
  
  final static String CD_FQN_SocNet = "de.monticore.umlcd4a.prettyprint.SocNet";
      
  final static String CD_FQN_Simple = "de.monticore.umlcd4a.prettyprint.Simple";

  protected void createSTAndAST(String name) {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    CDSymbol cdSymbol = globalScope.<CDSymbol> resolve(name, CDSymbol.KIND).orElse(null);
    assertNotNull(cdSymbol);
    assertTrue(cdSymbol.getAstNode().isPresent());
    ASTNode node = cdSymbol.getAstNode().get();
    assertTrue(node instanceof ASTCDDefinition);
    ASTCDDefinition cdDef = (ASTCDDefinition) node;    
    ReportingRepository reporting = new ReportingRepository(new CD4ANodeIdentHelper());
    
    // Report AST
    AST2ODReporter reporter = new AST2ODReporter("target", name, reporting);
    reporter.flush(cdDef);
    
    // Report ST
    SymbolTableReporter stReporter = new CD4ASymbolTableReporter("target", name, reporting);
    stReporter.flush(cdSymbol.getAstNode().get());
  }
  
  @Test
  public void checkODsOfSocNet() throws IOException {
    createSTAndAST(CD_FQN_SocNet);
  }
  
  @Test
  public void checkODsOfSimple() throws IOException {
    createSTAndAST(CD_FQN_Simple);
  } 
}
