/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.cocos;

import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;
import de.monticore.cd4codebasis.cocos.ebnf.CD4CodeEnumConstantParameterMatchConstructorArguments;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.monticore.testcd4codebasis.DeriveSymTypeOfTestCD4CodeBasis;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.monticore.testcd4codebasis._symboltable.TestCD4CodeBasisSymbolTableCompleter;
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
    //parse + create symtab
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cd4codebasis/cocos/CD4CodeEnumConstantParameterMatchConstructorArgumentsValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();

    TestCD4CodeBasisMill.scopesGenitorDelegator().createFromAST(ast);
    TestCD4CodeBasisSymbolTableCompleter completer = new TestCD4CodeBasisSymbolTableCompleter(ast);
    ast.accept(completer.getTraverser());

    CD4CodeBasisCoCoChecker coCoChecker = new CD4CodeBasisCoCoChecker();
    coCoChecker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments(new DeriveSymTypeOfTestCD4CodeBasis()));

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
    TestCD4CodeBasisSymbolTableCompleter completer = new TestCD4CodeBasisSymbolTableCompleter(ast);
    ast.accept(completer.getTraverser());

    //check coco
    CD4CodeBasisCoCoChecker coCoChecker = new CD4CodeBasisCoCoChecker();
    coCoChecker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments(new DeriveSymTypeOfTestCD4CodeBasis()));
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDCD2")); //for FOO
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDCD2")); //for BAR
  }

  @After
  public void after(){}

}
