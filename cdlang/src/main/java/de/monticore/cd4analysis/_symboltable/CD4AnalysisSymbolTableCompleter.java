/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.typescalculator.FullSynthesizeFromCD4Analysis;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

public class CD4AnalysisSymbolTableCompleter {
  
  protected CD4AnalysisTraverser traverser;
  
  public CD4AnalysisSymbolTableCompleter(ASTCDCompilationUnit ast) {
    this(ast.getMCImportStatementList(), ast.isPresentMCPackageDeclaration() ? ast
        .getMCPackageDeclaration().getMCQualifiedName() : MCQualifiedNameFacade.createQualifiedName(
            ""));
  }
  
  public CD4AnalysisSymbolTableCompleter(List<ASTMCImportStatement> imports,
      ASTMCQualifiedName packageDeclaration) {
    this.traverser = CD4AnalysisMill.inheritanceTraverser();
    
    final CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter(
        new FullSynthesizeFromCD4Analysis());
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDAssociationSymbolTableCompleter cDAssociationVisitor =
        new CDAssociationSymbolTableCompleter(new FullSynthesizeFromCD4Analysis());
    traverser.add4CDAssociation(cDAssociationVisitor);
    traverser.setCDAssociationHandler(cDAssociationVisitor);
    final CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor =
        new CDInterfaceAndEnumSymbolTableCompleter(new FullSynthesizeFromCD4Analysis());
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
  }
  
  public CD4AnalysisTraverser getTraverser() { return traverser; }
  
}
