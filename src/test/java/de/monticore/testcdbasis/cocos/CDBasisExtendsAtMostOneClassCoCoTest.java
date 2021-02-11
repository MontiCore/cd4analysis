/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis.cocos;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.optional.CDClassExtendsAtMostOneClass;
import de.monticore.testcdbasis.CDBasisTestBasis;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CDBasisExtendsAtMostOneClassCoCoTest extends CDBasisTestBasis {
  @Test
  public void extendsAtMostOneClass() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/ExtendsAtMostOneClass.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    coCoChecker.addCoCo(new CDClassExtendsAtMostOneClass());
    coCoChecker.checkAll(node);

    expectErrorCount(1, Collections.singletonList("ExtendsAtMostOneClass.cd:<14,2>: 0xCDC2F: Class C cannot extend multiple classes, but extends (A, B). A class may only extend one class."));
  }
}
