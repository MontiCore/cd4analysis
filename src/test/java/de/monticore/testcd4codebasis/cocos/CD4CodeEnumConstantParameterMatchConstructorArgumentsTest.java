/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.cocos;

import de.monticore.cd4codebasis.cocos.ebnf.CD4CodeEnumConstantParameterMatchConstructorArguments;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CD4CodeEnumConstantParameterMatchConstructorArgumentsTest extends CD4CodeBasisTestBasis {

  @Test
  public void testValid() throws IOException {
    // initialization
    TestCD4CodeBasisMill.init();
    BasicSymbolsMill.initializePrimitives();
    coCoChecker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments());

    //parse + create symtab
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cd4codebasis/cocos/CD4CodeEnumConstantParameterMatchConstructorArgumentsValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();

    TestCD4CodeBasisMill.scopesGenitorDelegator().createFromAST(ast);

    //check coco
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cd4codebasis/cocos/CD4CodeEnumConstantParameterMatchConstructorArgumentsInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC90"));
  }

  @After
  public void after(){}

}
