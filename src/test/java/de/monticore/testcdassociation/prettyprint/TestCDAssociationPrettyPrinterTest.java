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

public class TestCDAssociationPrettyPrinterTest extends TestBasis {
  TestCDAssociationParser p = new TestCDAssociationParser();
  TestCDAssociationPrettyPrinterDelegator printer = new TestCDAssociationPrettyPrinterDelegator();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    System.out.println(printer.prettyprint(astcdCompilationUnit.get()));
  }
}
