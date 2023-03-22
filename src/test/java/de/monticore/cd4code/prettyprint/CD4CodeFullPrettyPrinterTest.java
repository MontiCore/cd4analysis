/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.prettyprint;

import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodeFullPrettyPrinterTest extends CD4CodeTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_String(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);

    final ASTCDCompilationUnit nodeReparsed = astcdCompilationUnitReParsed.get();
    node.deepEquals(nodeReparsed);
  }


}
