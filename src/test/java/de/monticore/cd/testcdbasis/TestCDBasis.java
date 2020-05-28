/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.testcdbasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd.cdbasis._ast.*;
import de.monticore.cd.testcdbasis._parser.TestCDBasisParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDBasis extends TestBasis {
  TestCDBasisParser p = new TestCDBasisParser();

  @Test
  public void parseCompilationUnit() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse_StringCDCompilationUnit("package blub; classdiagram CD {}");
    checkNullAndPresents(p, astcdCompilationUnit);
  }

  @Test
  public void parseCompilationCDDefinition() throws IOException {
    final Optional<ASTCDDefinition> astcdDefinition = p.parse_StringCDDefinition("classdiagram CD {}");
    checkNullAndPresents(p, astcdDefinition);
  }

  @Test
  public void parseCDClass() throws IOException {
    final Optional<ASTCDClass> astcdClass = p.parse_StringCDClass("class A;");
    checkNullAndPresents(p, astcdClass);

    final Optional<ASTCDClass> astcdClass1 = p.parse_StringCDClass("class A {}");
    checkNullAndPresents(p, astcdClass1);
  }

  @Test
  public void parseModifier() throws IOException {
    final Optional<ASTCDModifier> astcdModifier = p.parse_StringCDModifier("abstract final <<blub>>");
    checkNullAndPresents(p, astcdModifier);
  }

  @Test
  public void parseCDAttribute() throws IOException {
    final Optional<ASTCDAttribute> astcdAttribute = p.parse_StringCDAttribute("final String name = \"testName\";");
    checkNullAndPresents(p, astcdAttribute);
  }

  @Test
  public void checkParser() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdbasis/parser/cdbasis.cd"));
    checkNullAndPresents(p, astcdCompilationUnit);
  }

}
