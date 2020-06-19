/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdassociation.parser;

import de.monticore.cd.TestBasis;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationDirection;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDAssociationParserTest extends TestBasis {
  TestCDAssociationParser p = new TestCDAssociationParser();

  @Test
  public void parseCDAssociation() throws IOException {
    final Optional<ASTCDAssociation> astcdAssociation = p.parse_StringCDAssociation("association [*] A -> [[id]] S [1];");
    checkNullAndPresence(p, astcdAssociation);
  }

  @Test
  public void parseCDElement() throws IOException {
    final Optional<ASTCDElement> astcdElement = p.parse_StringCDElement("association a [*] A -> [[id]] S [1];");
    checkNullAndPresence(p, astcdElement);
  }

  @Test
  public void parseCDMember() throws IOException {
    final Optional<ASTCDMember> astcdMember = p.parse_StringCDMember("-> (r) B [*];");
    checkNullAndPresence(p, astcdMember);
  }

  @Test
  public void parseCDAssociationDirection() throws IOException {
    final Optional<ASTCDAssociationDirection> leftToRightDir = p.parse_StringCDAssociationDirection("->");
    checkNullAndPresence(p, leftToRightDir);
    final Optional<ASTCDAssociationDirection> rightToLeftDir = p.parse_StringCDAssociationDirection("<-");
    checkNullAndPresence(p, rightToLeftDir);
    final Optional<ASTCDAssociationDirection> biDir = p.parse_StringCDAssociationDirection("<->");
    checkNullAndPresence(p, biDir);
    final Optional<ASTCDAssociationDirection> unspecifiedDir = p.parse_StringCDAssociationDirection("--");
    checkNullAndPresence(p, unspecifiedDir);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> parse = p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, parse);
  }
}
