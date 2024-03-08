/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis._symboltable;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.monticore.testcd4codebasis._visitor.TestCD4CodeBasisTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import java.util.List;

public class TestCD4CodeBasisSymbolTableCompleter {
  protected TestCD4CodeBasisTraverser traverser;

  public TestCD4CodeBasisSymbolTableCompleter(ASTCDCompilationUnit ast) {
    this(
        ast.getMCImportStatementList(),
        ast.isPresentMCPackageDeclaration()
            ? ast.getMCPackageDeclaration().getMCQualifiedName()
            : MCQualifiedNameFacade.createQualifiedName(""));
  }

  public TestCD4CodeBasisSymbolTableCompleter(
      List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.traverser = TestCD4CodeBasisMill.traverser();

    final CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter();
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor =
        new CDInterfaceAndEnumSymbolTableCompleter();
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    final CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor =
        new CD4CodeBasisSymbolTableCompleter();
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
    traverser.add4CDBasis(cd4CodeBasisVisitor);
  }

  public TestCD4CodeBasisTraverser getTraverser() {
    return traverser;
  }
}
