/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdassociation.parser;

import de.monticore.cd4analysis.trafo.CD4AnalysisDirectCompositionTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDAssocType;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDAssociationParserTest extends CDAssociationTestBasis {

  @Test
  public void parseCDAssociation() throws IOException {
    final Optional<ASTCDAssociation> astcdAssociation = p.parse_StringCDAssociation("association [*] A -> [[id]] S [1];");
    checkNullAndPresence(p, astcdAssociation);
  }

  @Test
  public void parseCDElement() throws IOException {
    final Optional<ASTCDElement> astcdElement = p.parse_StringCDElement("composition a [*] A -> [[id]] S [1];");
    checkNullAndPresence(p, astcdElement);
  }

  @Test
  public void parseCDMember() throws IOException {
    final Optional<ASTCDMember> astcdMember = p.parse_StringCDMember("-> (r) B [*];");
    checkNullAndPresence(p, astcdMember);
  }

  @Test
  public void parseCDAssociationDirection() throws IOException {
    final Optional<ASTCDAssocDir> leftToRightDir = p.parse_StringCDAssocDir("->");
    checkNullAndPresence(p, leftToRightDir);
    final Optional<ASTCDAssocDir> rightToLeftDir = p.parse_StringCDAssocDir("<-");
    checkNullAndPresence(p, rightToLeftDir);
    final Optional<ASTCDAssocDir> biDir = p.parse_StringCDAssocDir("<->");
    checkNullAndPresence(p, biDir);
    final Optional<ASTCDAssocDir> unspecifiedDir = p.parse_StringCDAssocDir("--");
    checkNullAndPresence(p, unspecifiedDir);
  }

  @Test
  public void parseCDAssocType() throws IOException {
    final Optional<ASTCDAssocType> association = p.parse_StringCDAssocType("association");
    checkNullAndPresence(p, association);
    final Optional<ASTCDAssocType> composition = p.parse_StringCDAssocType("composition");
    checkNullAndPresence(p, composition);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> parse = p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, parse);
  }

  @Test
  public void directCompositionTrafoTest() throws IOException {
    final Optional<ASTCDCompilationUnit> parse = p.parseCDCompilationUnit(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, parse);

    final ASTCDCompilationUnit node = parse.get();

    new CD4AnalysisDirectCompositionTrafo().transform(node);

    CD4CodeMill.init();
    final ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();
  }
}
