/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.parser;

import de.monticore.cdbasis._ast.*;
import de.monticore.testcdbasis.CDBasisTestBasis;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDBasisParserTest extends CDBasisTestBasis {

  @Test
  public void parseCDCompilationUnit() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse_StringCDCompilationUnit("package blub; classdiagram CD {}");
    checkNullAndPresence(p, astcdCompilationUnit);
  }

  @Test
  public void parseCDDefinition() throws IOException {
    final Optional<ASTCDDefinition> astcdDefinition = p.parse_StringCDDefinition("classdiagram CD {}");
    checkNullAndPresence(p, astcdDefinition);
  }

  @Test
  public void parseCDClass() throws IOException {
    final Optional<ASTCDClass> astcdClass = p.parse_StringCDClass("class A;");
    checkNullAndPresence(p, astcdClass);

    final Optional<ASTCDClass> astcdClass1 = p.parse_StringCDClass("class A {}");
    checkNullAndPresence(p, astcdClass1);
  }

  @Test
  public void parseCDElement() throws IOException {
    final Optional<ASTCDElement> astcdElement = p.parse_StringCDElement("class A;");
    checkNullAndPresence(p, astcdElement);
  }

  @Test
  public void parseCDAttribute() throws IOException {
    final Optional<ASTCDAttribute> astcdAttribute = p.parse_StringCDAttribute("final String name = \"testName\";");
    checkNullAndPresence(p, astcdAttribute);
  }

  @Test
  public void parseCDMember() throws IOException {
    final Optional<ASTCDMember> astcdMember = p.parse_StringCDMember("int x;");
    checkNullAndPresence(p, astcdMember);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdbasis/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }

}
