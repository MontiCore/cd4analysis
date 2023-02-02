/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import de.monticore.cdassociation.cocos.ebnf.CDAssociationOrderedCardinalityGreaterOne;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDAssociationOrderedCardinalityGreaterOneTest extends CDAssociationTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationOrderedCardinalityGreaterOne());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdassociation/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationOrderedCardinalityGreaterOne());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(
            getFilePath("cdassociation/cocos/CDAssociationOrderedCardinalityGreaterOneInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC65"));
  }

  @After
  @Override
  public void after() {}
}
