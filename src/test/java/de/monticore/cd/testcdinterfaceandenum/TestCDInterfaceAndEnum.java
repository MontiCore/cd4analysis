/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.testcdinterfaceandenum;

import de.monticore.cd.TestBasis;
import de.monticore.cd.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cdbasis._ast.ASTCDElement;
import de.monticore.cd.cdbasis._ast.ASTCDType;
import de.monticore.cd.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cd.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDInterfaceAndEnum extends TestBasis {
  TestCDInterfaceAndEnumParser p = new TestCDInterfaceAndEnumParser();

  @Test
  public void parseCDInterface() throws IOException {
    final Optional<ASTCDInterface> astcdInterface = p.parse_StringCDInterface("interface I extends I2 { String a; }");
    checkNullAndPresence(p, astcdInterface);
  }

  @Test
  public void parseCDElement() throws IOException {
    final Optional<ASTCDElement> astcdElement = p.parse_StringCDElement("interface I;");
    checkNullAndPresence(p, astcdElement);
  }

  @Test
  public void parseCDType() throws IOException {
    final Optional<ASTCDType> astcdType = p.parse_StringCDType("interface I;");
    checkNullAndPresence(p, astcdType);
  }

  @Test
  public void parseCDEnum() throws IOException {
    final Optional<ASTCDEnum> astcdEnum = p.parse_StringCDEnum("final enum Enum { ; }");
    checkNullAndPresence(p, astcdEnum);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> parse = p.parseCDCompilationUnit(getFilePath("cdinterfaceandenum/parser/Simple.cd"));
    checkNullAndPresence(p, parse);
  }
}
