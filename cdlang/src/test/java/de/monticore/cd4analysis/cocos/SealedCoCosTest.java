/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDPermitsTargetsResolvable;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDPermittedTypesAreSubtypes;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDSealedModifierCombinationsCoCo;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SealedCoCosTest extends CD4AnalysisTestBasis {
  
  private void addBothCoCos() {
    coCoChecker.addCoCo((CDBasisASTCDClassCoCo) new CDPermitsTargetsResolvable());
    coCoChecker.addCoCo((CDInterfaceAndEnumASTCDInterfaceCoCo) new CDPermitsTargetsResolvable());
    coCoChecker.addCoCo((CDBasisASTCDClassCoCo) new CDPermittedTypesAreSubtypes());
    coCoChecker.addCoCo((CDInterfaceAndEnumASTCDInterfaceCoCo) new CDPermittedTypesAreSubtypes());
  }
  
  private void addSealedCoCo(boolean topLevel) {
    coCoChecker.addCoCo((CDBasisASTCDClassCoCo) new CDSealedModifierCombinationsCoCo(topLevel));
    coCoChecker.addCoCo((CDInterfaceAndEnumASTCDInterfaceCoCo) new CDSealedModifierCombinationsCoCo(
        topLevel));
  }
  
  private ASTCDCompilationUnit parseAndBuild(String model) throws IOException {
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(model));
    assertTrue(optAST.isPresent(), () -> "Failed to parse " + model + ": " + Log.getFindings());
    Log.getFindings().clear();
    CD4AnalysisMill.scopesGenitorDelegator().createFromAST(optAST.get());
    return optAST.get();
  }
  
  @Test
  public void testValid() throws IOException {
    addBothCoCos();
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/SealedCoCosValid.cd"));
    assertEquals(0, Log.getFindings().size());
  }
  
  @Test
  public void testTopLevelSealedClassesValid() throws IOException {
    addSealedCoCo(true);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDTopLevelSealedClassesValid.cd"));
    assertEquals(0, Log.getFindings().size());
  }
  
  @Test
  public void testTopLevelSealedInterfacesValid() throws IOException {
    addSealedCoCo(true);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDTopLevelSealedInterfacesValid.cd"));
    assertEquals(0, Log.getFindings().size());
  }
  
  @Test
  public void testNestedSealedClassesValid() throws IOException {
    addSealedCoCo(false);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDNestedSealedClassesValid.cd"));
    assertEquals(0, Log.getFindings().size());
  }
  
  @Test
  public void testNestedSealedInterfacesValid() throws IOException {
    addSealedCoCo(false);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDNestedSealedInterfacesValid.cd"));
    assertEquals(0, Log.getFindings().size());
  }
  
  @Test
  public void testTopLevelSealedClassesInvalid() throws IOException {
    addSealedCoCo(true);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDTopLevelSealedClassesInvalid.cd"));
    assertOnlySealedFindings();
  }
  
  @Test
  public void testTopLevelSealedInterfacesInvalid() throws IOException {
    addSealedCoCo(true);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDTopLevelSealedInterfacesInvalid.cd"));
    assertOnlySealedFindings();
  }
  
  @Test
  public void testNestedSealedClassesInvalid() throws IOException {
    addSealedCoCo(false);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDNestedSealedClassesInvalid.cd"));
    assertOnlySealedFindings();
  }
  
  @Test
  public void testNestedSealedInterfacesInvalid() throws IOException {
    addSealedCoCo(false);
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDNestedSealedInterfacesInvalid.cd"));
    assertOnlySealedFindings();
  }
  
  @Test
  public void testPermitsTargetsUnresolvable() throws IOException {
    addBothCoCos();
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDPermitsTargetsResolvableInvalid.cd"));
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith(CDPermitsTargetsResolvable.ERROR_CODE));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith(CDPermitsTargetsResolvable.ERROR_CODE));
  }
  
  @Test
  public void testPermittedTypesNotSubtypes() throws IOException {
    addBothCoCos();
    coCoChecker.checkAll(parseAndBuild("cd4analysis/cocos/CDPermittedTypesAreSubtypesInvalid.cd"));
    assertEquals(3, Log.getFindings().size());
    for (int i = 0; i < 3; i++) {
      assertTrue(Log.getFindings().get(i).getMsg().startsWith(
          CDPermittedTypesAreSubtypes.ERROR_CODE));
    }
  }
  
  private void assertOnlySealedFindings() {
    assertFalse(Log.getFindings().isEmpty(), () -> "Expected sealed-modifier findings, got none");
    assertTrue(Log.getFindings().stream().allMatch(f -> f.getMsg().startsWith(
        CDSealedModifierCombinationsCoCo.ERROR_CODE)), () -> "Unexpected findings: " + Log
            .getFindings());
  }
  
  @AfterEach
  public void after() {}
  
}
