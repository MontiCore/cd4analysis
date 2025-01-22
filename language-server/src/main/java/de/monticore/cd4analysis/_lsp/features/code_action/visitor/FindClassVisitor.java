package de.monticore.cd4analysis._lsp.features.code_action.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;

public class FindClassVisitor implements CDBasisVisitor2 {
  private final ASTCDType find;
  private ASTCDType found;

  public FindClassVisitor(ASTCDType find) {
    this.find = find;
  }

  public static ASTCDType findClass(ASTNode parentAst, ASTCDType find) {
    CD4AnalysisTraverser traverser = CD4AnalysisMill.inheritanceTraverser();
    FindClassVisitor findClassVisitor = new FindClassVisitor(find);
    traverser.add4CDBasis(findClassVisitor);
    parentAst.accept(traverser);

    return findClassVisitor.getFound();
  }

  @Override
  public void visit(ASTCDClass node) {
    if (node.deepEquals(find)) found = node;
  }

  public ASTCDType getFound() {
    return found;
  }
}
