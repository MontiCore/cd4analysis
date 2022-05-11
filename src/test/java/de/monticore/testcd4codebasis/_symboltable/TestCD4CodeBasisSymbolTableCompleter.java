/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.testcd4codebasis.FullDeriveFromTestCD4CodeBasis;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.monticore.testcd4codebasis._visitor.TestCD4CodeBasisTraverser;
import de.monticore.types.check.FullSynthesizeFromMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

public class TestCD4CodeBasisSymbolTableCompleter {
  protected TestCD4CodeBasisTraverser traverser;
  protected CDSymbolTableHelper symbolTableHelper;

  public TestCD4CodeBasisSymbolTableCompleter(ASTCDCompilationUnit ast) {
    this(ast.getMCImportStatementList(),
        ast.isPresentMCPackageDeclaration() ?
            ast.getMCPackageDeclaration().getMCQualifiedName() :
            MCQualifiedNameFacade.createQualifiedName(""));
  }

  public TestCD4CodeBasisSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper(new FullDeriveFromTestCD4CodeBasis(), new FullSynthesizeFromMCBasicTypes())
        .setPackageDeclaration(packageDeclaration);
    this.traverser = TestCD4CodeBasisMill.traverser();

    final CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDAssociationSymbolTableCompleter cDAssociationVisitor = new CDAssociationSymbolTableCompleter(symbolTableHelper);
    final CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor = new CDInterfaceAndEnumSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    final CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor = new CD4CodeBasisSymbolTableCompleter(symbolTableHelper);
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
  }

  public TestCD4CodeBasisTraverser getTraverser() {
    return traverser;
  }
}
