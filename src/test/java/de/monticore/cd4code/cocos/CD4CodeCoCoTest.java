/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.cocos;

import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CD4CodeCoCoTest extends CD4CodeTestBasis {

  @Test
  public void importModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Import.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    symbolTableCreator.createFromAST(node);
    checkLogError();

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    symbolTableCreator.createFromAST(node);
    checkLogError();

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }
}
