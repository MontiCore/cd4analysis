/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testtypeimporter._symboltable;

import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.testtypeimporter._ast.ASTElement;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;

public class TestTypeImporterScopesGenitor
    extends TestTypeImporterScopesGenitorTOP {

  public TestTypeImporterScopesGenitor() {
  }

  @Override
  public void endVisit(ASTElement node) {
    super.endVisit(node);

    final TypeCheckResult typeResult = new DeriveSymTypeOfCDBasis().synthesizeType(node.getMCType());
    if (!typeResult.isPresentCurrentResult()) {
      Log.error(String.format(
          "0xCDE00: The type (%s) of the element (%s) could not be calculated",
          node.getMCType().printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()),
          node.getName()),
          node.getMCType().get_SourcePositionStart());
    }
    else {
      node.getSymbol().setType(typeResult.getCurrentResult());
    }
  }
}
