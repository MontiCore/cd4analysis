/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2alloy.cocos;

import de.monticore.cddiff.AbstractTest;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cocos.helper.Assert;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests for the detection of illegal symbols
 *
 *
 */
public class IllegalSymbolsTest extends AbstractTest {

  @Test
  public void invalidInputSymbolTest(){
    ASTCDCompilationUnit a = parseModel("src/cddifftest/resources/de/monticore/cddiff/InvalidCoCos/cd3.cd");


    CD4AnalysisCoCoChecker checker = new CD2AlloyCoCos().getCheckerForAllCoCos();
    checker.checkAll(a);

    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error("Symbol $ is not allowed, as it is already defined in alloy.",
            new SourcePosition(2, 0)),
        Finding.error("Symbol _ is not allowed, as it is already defined in alloy.",
            new SourcePosition(2, 0))
        );

    Assert.assertErrors(expectedErrors, Log.getFindings());

  }

}
