/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.prettyprint;

import de.monticore.cdassociation.prettyprint.CDAssociationFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDAssociationPrettyPrinterTest extends CDAssociationTestBasis {
  final TestCDAssociationParser p = new TestCDAssociationParser();
  final CDAssociationFullPrettyPrinter printer = new CDAssociationFullPrettyPrinter(new IndentPrinter());

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
