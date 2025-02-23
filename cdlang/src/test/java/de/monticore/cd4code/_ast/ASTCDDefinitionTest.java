/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._ast;

import static org.junit.Assert.assertEquals;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

public final class ASTCDDefinitionTest extends CD4CodeTestBasis {

  @Test
  public void testGetAssociationsListForType() throws IOException {
    Optional<ASTCDCompilationUnit> compilationUnit =
        p.parseCDCompilationUnit(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, compilationUnit);

    ASTCDDefinition definition = compilationUnit.get().getCDDefinition();

    ASTCDType grades =
        definition.getCDClassesList().stream()
            .filter(it -> it.getName().equals("Grades"))
            .findFirst()
            .orElseThrow(NullPointerException::new);

    assertEquals(0, definition.getCDAssociationsListForType(grades).size());

    ASTCDType person =
        definition.getCDClassesList().stream()
            .filter(it -> it.getName().equals("Person"))
            .findFirst()
            .orElseThrow(NullPointerException::new);

    assertEquals(2, definition.getCDAssociationsListForType(person).size());

    ASTCDType student =
        definition.getCDClassesList().stream()
            .filter(it -> it.getName().equals("Student"))
            .findFirst()
            .orElseThrow(NullPointerException::new);

    assertEquals(1, definition.getCDAssociationsListForType(student).size());
  }

  @Test
  public void testCDClassMillDelegation() {
    CD4CodeMill.init();
    // All Mills should return a builder of the cd4code basis cdclass (overriding the cdbasis)
    assertEquals(CD4CodeMill.cDClassBuilder().uncheckedBuild().getClass(),
                 CD4CodeBasisMill.cDClassBuilder().uncheckedBuild().getClass());

    // TODO: #4519
//    assertEquals(CD4CodeMill.cDClassBuilder().uncheckedBuild().getClass(),
//      CD4AnalysisMill.cDClassBuilder().uncheckedBuild().getClass());


    // TODO: #4519
//    assertEquals(CD4CodeMill.cDClassBuilder().uncheckedBuild().getClass(),
//      CDBasisMill.cDClassBuilder().uncheckedBuild().getClass());
  }
}
