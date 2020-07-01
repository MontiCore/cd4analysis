/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.parser;

import de.monticore.cd.TestBasis;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CD4CodeParserTest extends TestBasis {
  CD4CodeParser p = new CD4CodeParser();

  @Test
  public void testLanguageTeaser() throws RecognitionException, IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }

}
