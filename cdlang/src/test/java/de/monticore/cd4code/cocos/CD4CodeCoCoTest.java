/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests CD4Code (and all super language's) CoCos
 */
public class CD4CodeCoCoTest extends CD4CodeTestBasis {
  
  @Test
  public void importModel() throws IOException {
    // When this test fails: check, that the Simple.cdsym is correct!
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(
        "cdbasis/parser/Import.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.orElseThrow();
    new CD4CodeAfterParseTrafo().transform(node);
    
    final ICD4CodeArtifactScope scope = prepareST(astcdCompilationUnit.orElseThrow());
    checkLogError();
    
    assertNotNull(scope.resolveCDType("C"));
    
    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }
  
  @Test
  public void completeCDBasisModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(
        "cdbasis/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.orElseThrow();
    
    prepareST(astcdCompilationUnit.orElseThrow());
    checkLogError();
    
    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }
  
  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(
        "cd4code/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.orElseThrow();
    
    prepareST(node);
    checkLogError();
    
    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }
  
}
