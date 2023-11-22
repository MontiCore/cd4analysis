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
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;

/** Tests to detect currently not supported modifiers. */
public class IllegalAttributeModifierTest extends CDDiffTestBasis {

  @Test
  public void invalidInputSymbolTest() {
    ASTCDCompilationUnit a =
        parseModel("src/test/resources/de/monticore/cddiff/InvalidCoCos/cd2.cd");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(a);

    CD4AnalysisCoCoChecker checker = new CD2AlloyCoCos().getCheckerForAllCoCos();
    checker.checkAll(a);

    Collection<Finding> expectedErrors =
        Collections.singletonList(
            Finding.error(
                "Attribute kind has invalid modifiers. No modifiers are allowed for CD4Analysis.",
                new SourcePosition(6, 4)));

    Assert.assertErrors(expectedErrors, Log.getFindings());
  }
}
