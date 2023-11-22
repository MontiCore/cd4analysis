/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.prettyprint;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDAssociationPrettyPrinterTest extends CDAssociationTestBasis {
  final TestCDAssociationParser p = new TestCDAssociationParser();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = CDAssociationMill.prettyPrint(astcdCompilationUnit.get(), true);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
        p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
