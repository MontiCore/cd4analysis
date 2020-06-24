/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._parser;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class CD4AnalysisParser extends CD4AnalysisParserTOP {
  CD4AnalysisAfterParseDelegatorVisitor afterParseTrafo = new CD4AnalysisAfterParseDelegatorVisitor();

  @Override
  public Optional<ASTCDCompilationUnit> parse(String fileName)
      throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse(fileName);
    parse.ifPresent(p -> p.accept(afterParseTrafo));
    return parse;
  }

  @Override
  public Optional<ASTCDCompilationUnit> parse(Reader reader)
      throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse(reader);
    parse.ifPresent(p -> p.accept(afterParseTrafo));
    return parse;
  }

  @Override
  public Optional<ASTCDCompilationUnit> parse_String(String str)
      throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse_String(str);
    parse.ifPresent(p -> p.accept(afterParseTrafo));
    return parse;
  }
}
