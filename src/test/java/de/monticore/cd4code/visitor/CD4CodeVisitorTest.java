/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cd4code.visitor;

import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CD4CodeVisitorTest extends CD4CodeTestBasis {

  @Test
  public void testCDElementVisitor() throws RecognitionException, IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDDefinition node = astcdCompilationUnit.get().getCDDefinition();
    final List<ASTCDPackage> packages = node.getCDPackagesList();
    assertEquals(2, packages.size());

    assertEquals(5, node.getCDClassesList().size());
    assertEquals(1, node.getCDInterfacesList().size());
    assertEquals(1, node.getCDEnumsList().size());
    assertEquals(3, node.getCDAssociationsList().size());

    final Optional<ASTCDPackage> entity = packages.stream().filter(p -> p.getName().equals("entity")).findFirst();
    assertTrue(entity.isPresent());
    assertEquals(1, entity.get().getCDElementList().size());
  }

}
