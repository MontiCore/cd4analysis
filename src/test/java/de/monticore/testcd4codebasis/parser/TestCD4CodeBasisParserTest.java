/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.parser;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

public class TestCD4CodeBasisParserTest extends CD4CodeBasisTestBasis {

  @Test
  public void parseCDThrowsDeclaration() throws IOException {
    final Optional<ASTCDThrowsDeclaration> astcdThrowsDeclaration =
        p.parse_StringCDThrowsDeclaration("throws Exception1, Exception2");
    checkNullAndPresence(p, astcdThrowsDeclaration);
  }

  @Test
  public void parseCDMethod() throws IOException {
    final Optional<ASTCDMethod> astcdMethod = p.parse_StringCDMethod("String getName();");
    checkNullAndPresence(p, astcdMethod);
  }

  @Test
  public void parseCDConstructor() throws IOException {
    final Optional<ASTCDConstructor> astcdConstructor =
        p.parse_StringCDConstructor("A(String name);");
    checkNullAndPresence(p, astcdConstructor);
  }

  @Test
  public void parseCDParameter() throws IOException {
    final Optional<ASTCDParameter> astcdParameter =
        p.parse_StringCDParameter("String name = \"blub\"");
    checkNullAndPresence(p, astcdParameter);
  }

  @Test
  public void parseCDParameter_withEllipsis() throws IOException {
    final Optional<ASTCDParameter> astcdParameter = p.parse_StringCDParameter("String... names");
    checkNullAndPresence(p, astcdParameter);
  }

  @Test
  public void parseCDEnumConstant() throws IOException {
    final Optional<ASTCDEnumConstant> enumConstant = p.parse_StringCDEnumConstant("ENUM_CONSTANT");
    checkNullAndPresence(p, enumConstant);

    final Optional<ASTCDEnumConstant> enumConstant_withArguments =
        p.parse_StringCDEnumConstant("ENUM_CONSTANT(5, \"param\", a)");
    checkNullAndPresence(p, enumConstant_withArguments);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> parse =
        p.parseCDCompilationUnit(getFilePath("cd4codebasis/parser/Simple.cd"));
    checkNullAndPresence(p, parse);
  }
}
