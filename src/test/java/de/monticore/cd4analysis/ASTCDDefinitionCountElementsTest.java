/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

public class ASTCDDefinitionCountElementsTest extends CD4AnalysisTestBasis {

  @Test
  public void countElements() throws IOException {
    Optional<ASTCDCompilationUnit> optASTCDCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cd4analysis/Count.cd"));
    checkNullAndPresence(p, optASTCDCompilationUnit);
    assertTrue(optASTCDCompilationUnit.isPresent());
    ASTCDDefinition astcdDefinition = optASTCDCompilationUnit.get().getCDDefinition();
    int classCount = astcdDefinition.getCDClassesList().size();
    int interfaceCount = astcdDefinition.getCDInterfacesList().size();
    int enumCount = astcdDefinition.getCDEnumsList().size();
    int associationCount = astcdDefinition.getCDAssociationsList().size();
    assertArrayEquals(
        new int[] {2, 2, 2, 2},
        new int[] {classCount, interfaceCount, enumCount, associationCount});
  }
}
