/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CheckLanguage {
  @Test
  public void checkParser() throws IOException {
    final Optional<ASTCDCompilationUnit> parse = new CD4AnalysisParser().parse("simple.cd");
    System.out.println(parse);
  }
}
