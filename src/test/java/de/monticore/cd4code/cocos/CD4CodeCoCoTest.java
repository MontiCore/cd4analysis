/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.cocos;

import de.monticore.cd.cli.CDCLI;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodeCoCoTest extends CD4CodeTestBasis {

  @Test
  public void importModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Import.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final ICD4CodeArtifactScope scope = CD4CodeMill.cD4CodeSymbolTableCreatorDelegator().createFromAST(node);
    checkLogError();

    assertNotNull(scope.resolveCDType("C"));

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }

  @Test
  public void useCLI() throws IOException, ParseException {
    final File otherFile = new File(getFilePath("cdbasis/parser/Simple.cd"));
    assertTrue(otherFile.exists());
    final String otherFileName = otherFile.toString();
    CDCLI.main(new String[] { "-i", otherFileName, "-f", "false", "-p", "src/test/resources", "-o", getTmpAbsolutePath(), "-s", "Simple.cdsym" });

    final File file = new File(getFilePath("cdbasis/parser/Import.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();
    CDCLI.main(new String[] { "-i", fileName, "-f", "false", "-p", "src/test/resources" });
    checkLogError();
  }

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    CD4CodeMill.cD4CodeSymbolTableCreatorDelegator().createFromAST(node);
    checkLogError();

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }
}
