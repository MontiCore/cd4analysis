/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.cocos;

import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cocos.helper.Assert;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;

/** Tests for the detection of illegal symbols */
public class IllegalSymbolsTest extends CDDiffTestBasis {

  @Test
  public void invalidInputSymbolTest() {
    ASTCDCompilationUnit a =
        parseModel("src/test/resources/de/monticore/cddiff/InvalidCoCos/cd3.cd");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(a);

    CD4AnalysisCoCoChecker checker = new CD2AlloyCoCos().getCheckerForAllCoCos();
    checker.checkAll(a);

    Collection<Finding> expectedErrors =
        Arrays.asList(
            Finding.error(
                "Symbol $ is not allowed, as it is already defined in alloy.",
                new SourcePosition(5, 2)),
            Finding.error(
                "Symbol _ is not allowed, as it is already defined in alloy.",
                new SourcePosition(10, 4)));

    Assert.assertErrors(expectedErrors, Log.getFindings());
  }
}
