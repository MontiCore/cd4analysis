/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._cocos.CDBasisCoCoChecker;
import de.monticore.cdbasis.cocos.optional.CDClassExtendsAtMostOneClass;
import de.monticore.testcdbasis.CDBasisTestBasis;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class CDBasisExtendsAtMostOneClassCoCoTest extends CDBasisTestBasis {
  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/ExtendsAtMostOneClass.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    symbolTableCreator.createFromAST(node);
    checkLogError();

    final CDBasisCoCoChecker checkerForAllCoCos = cdBasisCoCos.getCheckerForAllCoCos();
    checkerForAllCoCos.addCoCo(new CDClassExtendsAtMostOneClass());
    checkerForAllCoCos.checkAll(node);

    expectErrorCount(1, Collections.singletonList("ExtendsAtMostOneClass.cd:<14,2>: 0xCDC2F: Class C cannot extend multiple classes, but extends (A, B). A class may only extend one class."));
  }
}
