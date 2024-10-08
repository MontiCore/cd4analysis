// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition.parser;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symtabdefinition.SymTabDefinitionTestBasis;
import de.monticore.symtabdefinition._ast.ASTSTDFunction;
import de.monticore.symtabdefinition._ast.ASTSTDVariable;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SymTabDefinitionParserTest extends SymTabDefinitionTestBasis {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "void f();",
        "String f(int x);",
        "String f(int x, float y);",
        "<T> List<T> f(int x, float... y);",
        "<T, U extends List<T>> List<T> f(T x, U... y);",
      })
  public void parseSTDFunction(String model) throws IOException {
    final Optional<ASTSTDFunction> astOpt = parser.parse_StringSTDFunction(model);
    checkNullAndPresence(parser, astOpt);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "int x;",
        "List<float> y;",
      })
  public void parseSTDVariable(String model) throws IOException {
    final Optional<ASTSTDVariable> astOpt = parser.parse_StringSTDVariable(model);
    checkNullAndPresence(parser, astOpt);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> parse =
        parser.parseCDCompilationUnit(getFilePath("stdefinition/cocos/Valid.cd"));
    checkNullAndPresence(parser, parse);
  }
}
