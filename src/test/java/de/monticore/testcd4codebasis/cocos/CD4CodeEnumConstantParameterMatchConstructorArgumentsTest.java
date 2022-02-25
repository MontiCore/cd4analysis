/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.cocos;

import com.sun.xml.internal.bind.v2.TODO;
import de.monticore.cd4codebasis.cocos.ebnf.CD4CodeEnumConstantParameterMatchConstructorArguments;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CD4CodeEnumConstantParameterMatchConstructorArgumentsTest extends CD4CodeBasisTestBasis {

  @Before
  public void init() {
    TestCD4CodeBasisMill.globalScope().clear();
    TestCD4CodeBasisMill.init();
    BasicSymbolsMill.initializePrimitives();
    coCoChecker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments());
  }

  @Ignore // TODO Die Implementierung der CoCo ist fehlerhaft!
  @Test
  public void testValid() throws IOException {
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
    //parse + create symtab
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cd4codebasis/cocos/CD4CodeEnumConstantParameterMatchConstructorArgumentsInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();

    TestCD4CodeBasisMill.scopesGenitorDelegator().createFromAST(ast);

    //check coco
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDCD2")); //for FOO
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDCD2")); //for BAR
  }

  @After
  public void after(){}

}
