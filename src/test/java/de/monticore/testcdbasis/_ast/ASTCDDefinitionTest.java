/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis._ast;

import static org.junit.Assert.assertEquals;

import de.monticore.cd4code.CD4CodeTestBasis;
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
}
