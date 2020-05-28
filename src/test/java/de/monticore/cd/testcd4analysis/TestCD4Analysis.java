/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.testcd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cdassociation._ast.ASTCDAssociation;
import de.monticore.cd.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cdbasis._ast.ASTCDElement;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCD4Analysis extends TestBasis {
  CD4AnalysisParser p = new CD4AnalysisParser();

  @Test
  public void parseAssociation() throws IOException {
    final Optional<ASTCDAssociation> astcdAssociation = p.parse_StringCDAssociation("association [*] A -> [[id]] S [1];");
    checkNullAndPresents(p, astcdAssociation);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/cd4a.cd"));
    checkNullAndPresents(p, astcdCompilationUnit);
  }

  @Test
  public void parseElement() throws IOException {
    final Optional<ASTCDElement> astcdElement = p.parse_StringCDElement("association [*] A -> [[id]] S [1];");
    checkNullAndPresents(p, astcdElement);
  }
}
