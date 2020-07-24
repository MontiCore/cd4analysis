/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdassociation.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDAssociationPrettyPrinterTest extends TestBasis {
  final TestCDAssociationParser p = new TestCDAssociationParser();
  final TestCDAssociationPrettyPrinterDelegator printer = new TestCDAssociationPrettyPrinterDelegator();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
