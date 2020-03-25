/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.odprint;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._symboltable.CD4AGlobalScopeTestFactory;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.reporting.AST2ODReporter;
import de.monticore.cd.reporting.CD4ANodeIdentHelper;
import de.monticore.cd.reporting.CD4ASymbolTableReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.SymbolTableReporter2;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 */
public class ODReportingTest {
  
  final static String CD_FQN_SocNet = "de.monticore.umlcd4a.prettyprint.SocNet";
      
  final static String CD_FQN_Simple = "de.monticore.umlcd4a.prettyprint.Simple";

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  protected void createSTAndAST(String name) {
    final CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    CDDefinitionSymbol cdSymbol = globalScope.resolveCDDefinition(name).orElse(null);
    assertNotNull(cdSymbol);
    assertTrue(cdSymbol.isPresentAstNode());
    ASTNode node = cdSymbol.getAstNode();
    assertTrue(node instanceof ASTCDDefinition);
    ASTCDDefinition cdDef = (ASTCDDefinition) node;    
    ReportingRepository reporting = new ReportingRepository(new CD4ANodeIdentHelper());
    
    // Report AST
    AST2ODReporter reporter = new AST2ODReporter("target", name, reporting);
    reporter.flush(cdDef);
    
    // Report ST
    SymbolTableReporter2 stReporter = new CD4ASymbolTableReporter("target", name, reporting);
    stReporter.flush(cdSymbol.getAstNode());
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
