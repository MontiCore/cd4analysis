package de.monticore.cd4analysis.cocos.ebnf;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDAssociationValidRoleSymbolInScopeTest extends CD4AnalysisTestBasis {
  
  @Test
  public void testValid() throws IOException {
    String modelPath = "cd4analysis/cocos/CDAssociationValidRoleSymbolInScope.cd";
    
    coCoChecker.addCoCo(new CDAssociationValidRoleSymbolsInScope());
    ASTCDCompilationUnit ast = parse(modelPath);
    prepareST(ast);
    
    coCoChecker.checkAll(ast);
    assertEquals(0, Log.getFindings().size());
  }
  
  @Test
  public void testInvalid() throws IOException {
    String modelPath = "cd4analysis/cocos/CDAssociationValidRoleSymbolInScopeInvalid.cd";
    
    coCoChecker.addCoCo(new CDAssociationValidRoleSymbolsInScope());
    ASTCDCompilationUnit ast = parse(modelPath);
    prepareST(ast);
    
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDCE5"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDCE4"));
    Log.clearFindings();
  }
  
  @Test
  public void testInvalid2() throws IOException {
    String modelPath = "cd4analysis/cocos/CDAssociationValidRoleSymbolInScopeInvalid2.cd";
    
    coCoChecker.addCoCo(new CDAssociationValidRoleSymbolsInScope());
    ASTCDCompilationUnit ast = parse(modelPath);
    prepareST(ast);
    
    coCoChecker.checkAll(ast);
    assertEquals(4, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDCE5"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDCE5"));
    assertTrue(Log.getFindings().get(2).getMsg().startsWith("0xCDCE4"));
    assertTrue(Log.getFindings().get(3).getMsg().startsWith("0xCDCE4"));
    Log.clearFindings();
  }
}
