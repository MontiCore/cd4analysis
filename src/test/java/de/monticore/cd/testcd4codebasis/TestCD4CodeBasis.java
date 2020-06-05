/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.testcd4codebasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.cd.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cdbasis._ast.ASTCDModifier;
import de.monticore.cd.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cd.testcd4codebasis._parser.TestCD4CodeBasisParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestCD4CodeBasis extends TestBasis {
  TestCD4CodeBasisParser p = new TestCD4CodeBasisParser();

  @Test
  public void parseCDThrowsDeclaration() throws IOException {
    final Optional<ASTCDThrowsDeclaration> astcdThrowsDeclaration = p.parse_StringCDThrowsDeclaration("throws Exception1, Exception2");
    checkNullAndPresence(p, astcdThrowsDeclaration);
  }

  @Test
  public void parseCDMethod() throws IOException {
    final Optional<ASTCDMethod> astcdMethod = p.parse_StringCDMethod("String getName();");
    checkNullAndPresence(p, astcdMethod);
  }

  @Test
  public void parseCDConstructor() throws IOException {
    final Optional<ASTCDConstructor> astcdConstructor = p.parse_StringCDConstructor("A(String name);");
    checkNullAndPresence(p, astcdConstructor);
  }

  @Test
  public void parseCDParameter() throws IOException {
    final Optional<ASTCDParameter> astcdParameter = p.parse_StringCDParameter("String name = \"blub\"");
    checkNullAndPresence(p, astcdParameter);
  }

  @Test
  public void parseCDParameter_withEllipsis() throws IOException {
    final Optional<ASTCDParameter> astcdParameter = p.parse_StringCDParameter("String... names");
    checkNullAndPresence(p, astcdParameter);
  }

  @Test
  public void parseCDModifier() throws IOException {
    List<String> modifier = Arrays.asList("final", "private", "public", "protected", "derived", "readonly", "static", "abstract", "<<stereo>>");

    for (String m : modifier) {
      final Optional<ASTCDModifier> astcdModifier = p.parse_StringCDModifier(m);
      checkNullAndPresence(p, astcdModifier);
    }
  }

  @Test
  public void parseCDEnumConstant() throws IOException {
    final Optional<ASTCDEnumConstant> enumConstant = p.parse_StringCDEnumConstant("ENUM_CONSTANT");
    checkNullAndPresence(p, enumConstant);

    final Optional<ASTCDEnumConstant> enumConstant_withArguments = p.parse_StringCDEnumConstant("ENUM_CONSTANT(5, \"param\", a)");
    checkNullAndPresence(p, enumConstant_withArguments);
  }

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> parse = p.parseCDCompilationUnit(getFilePath("cd4codebasis/parser/Simple.cd"));
    checkNullAndPresence(p, parse);
  }
}
