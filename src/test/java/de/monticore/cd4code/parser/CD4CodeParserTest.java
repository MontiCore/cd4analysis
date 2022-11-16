/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.parser;

import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.Optional;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

public class CD4CodeParserTest extends CD4CodeTestBasis {

  @Test
  public void testLanguageTeaser() throws RecognitionException, IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }
}
