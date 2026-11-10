/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._parser;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.Optional;

public class CD4CodeParser extends CD4CodeParserTOP {
  
  protected boolean _checkFileAndPackageName;
  
  /**
   * The parser for this grammar.
   * {@link de.monticore.cd4code.CD4CodeMill#parser()} should be preferred over this constructor, as
   * this further enables language composition.
   *
   * @deprecated new instances of a parser should be retrieved via a language's mill
   */
  @Deprecated
  public CD4CodeParser() {
    this(true);
  }
  
  @SuppressWarnings("deprecation")
  public CD4CodeParser(boolean _checkFileAndPackageName) {
    this._checkFileAndPackageName = _checkFileAndPackageName;
  }
  
  @Override
  public Optional<ASTCDCompilationUnit> parse(String fileName) throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse(fileName);
    if (_checkFileAndPackageName) {
      parse.ifPresent(p -> CD4AnalysisParser.checkFileAndPackageName(fileName, p));
    }
    return parse;
  }
  
}
