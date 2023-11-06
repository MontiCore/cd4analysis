package de.monticore.cd4analysis._lsp.features.code_action.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;

public class DeleteClassVisitor implements CDBasisVisitor2 {
  private final ASTCDType toDelete;

  public DeleteClassVisitor(ASTCDType toDelete) {
    this.toDelete = toDelete;
  }

  public static void deleteClass(ASTNode parentAst, ASTCDType toDelete) {
    CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
    DeleteClassVisitor visitor = new DeleteClassVisitor(toDelete);
    traverser.add4CDBasis(visitor);
    parentAst.accept(traverser);
  }

  @Override
  public void visit(ASTCDPackage node) {
    node.getCDElementList().removeIf(element -> element.deepEquals(toDelete));
  }

  @Override
  public void visit(ASTCDDefinition definition) {
    definition.getCDElementList().removeIf(element -> element.deepEquals(toDelete));
  }
}
