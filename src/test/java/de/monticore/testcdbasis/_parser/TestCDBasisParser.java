/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis._parser;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.io.IOException;
import java.util.Optional;

public class TestCDBasisParser extends TestCDBasisParserTOP {
  @Override
  public Optional<ASTCDCompilationUnit> parse(String fileName)
      throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse(fileName);
    parse.ifPresent(p -> CD4AnalysisParser.checkFileAndPackageName(fileName, p));
    return parse;
  }
}
