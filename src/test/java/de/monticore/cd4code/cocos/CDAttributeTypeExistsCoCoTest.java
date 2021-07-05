/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.cocos;

import de.monticore.cd.cli.CDCLI;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeTypeExists;
import de.monticore.cdbasis.cocos.optional.CDClassExtendsAtMostOneClass;
import de.monticore.testcdbasis.CDBasisTestBasis;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CDAttributeTypeExistsCoCoTest extends CD4CodeTestBasis {
  @Test
  public void attributeTypeExists() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Types.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());
    checkLogError();

    final CD4CodeCoCoChecker checker = cd4CodeCoCos.createNewChecker();
    checker.addCoCo(new CDAttributeTypeExists());
    checker.checkAll(node);

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }

  @Test
  public void attributeTypeExists2() throws IOException, ParseException {
    final File file = new File(getFilePath("cdbasis/parser/Types.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    CDCLI.main(new String[] { "-i", fileName, 
                              "-pp", getTmpFilePath("Types.cd").replaceAll("\\\\", "/"), 
                              "-s",  "target/symbols/Types.sym"});
  }
}
