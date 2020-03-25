/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes._cocos.MCCollectionTypesASTMCGenericTypeCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that references to generic types use at least one parameter.
 *
 */
public class GenericTypeHasParameters implements MCCollectionTypesASTMCGenericTypeCoCo {
  
  @Override
  public void check(ASTMCGenericType type) {
    List<ASTMCTypeArgument> args = type.getMCTypeArgumentList();
    if (!args.isEmpty()) {
      String typeName = new AstPrinter().printType(type);
      check(typeName, args);
    }
  }
  
  private void check(String typeName, List<ASTMCTypeArgument> typeArguments) {
    if (typeArguments.isEmpty()) {
      Log.error(
          String
              .format(
                  "0xC4A30 Generic type %s has no type-parameter. References to generic types must be parametrized.",
                  typeName),
          typeArguments.get(0).get_SourcePositionStart());
    }
  }
}
