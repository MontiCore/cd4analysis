/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.prettyprint;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCD4CodeBasisPretterPrinterTest extends CD4CodeBasisTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cd4codebasis/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = CD4CodeBasisMill.prettyPrint(astcdCompilationUnit.get(), true);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
        p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);

    // check deep equals
    assertTrue(astcdCompilationUnit.get().deepEquals(astcdCompilationUnitReParsed.get()));
  }
}
