package de.monticore.cd4code._symboltable;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

public class CD4CodeSymbolTableCompleter {
  protected CD4CodeTraverser traverser;
  protected final List<ASTMCImportStatement> imports;
  protected final ASTMCQualifiedName packageDeclaration;

  public CD4CodeSymbolTableCompleter(ASTCDCompilationUnit ast) {
    this(ast.getMCImportStatementList(),
        ast.isPresentMCPackageDeclaration() ?
            ast.getMCPackageDeclaration().getMCQualifiedName() :
            MCQualifiedNameFacade.createQualifiedName(""));
  }

  public CD4CodeSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.imports = imports;
    this.packageDeclaration = packageDeclaration;
    this.traverser = CD4CodeMill.traverser();

    final CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter(imports, packageDeclaration);
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDAssociationSymbolTableCompleter cDAssociationVisitor = new CDAssociationSymbolTableCompleter(imports, packageDeclaration);
    traverser.add4CDAssociation(cDAssociationVisitor);
    final CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor = new CD4CodeBasisSymbolTableCompleter(imports, packageDeclaration);
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
  }

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }
}
