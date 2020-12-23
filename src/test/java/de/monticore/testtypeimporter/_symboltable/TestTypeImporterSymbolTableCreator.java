/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testtypeimporter._symboltable;

import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.testtypeimporter._ast.ASTElement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;

public class TestTypeImporterSymbolTableCreator
    extends TestTypeImporterSymbolTableCreatorTOP {

  public TestTypeImporterSymbolTableCreator() {
  }

  public TestTypeImporterSymbolTableCreator(ITestTypeImporterScope enclosingScope) {
    super(enclosingScope);
  }

  public TestTypeImporterSymbolTableCreator(Deque<? extends ITestTypeImporterScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public void visit(ASTElement node) {
    super.visit(node);

    node.getMCType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    final Optional<SymTypeExpression> typeResult = new DeriveSymTypeOfCDBasis().calculateType(node.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDE00: The type (%s) of the element (%s) could not be calculated",
          node.getMCType().printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()),
          node.getName()),
          node.getMCType().get_SourcePositionStart());
    }
    else {
      node.getSymbol().setType(typeResult.get());
    }
  }
}
