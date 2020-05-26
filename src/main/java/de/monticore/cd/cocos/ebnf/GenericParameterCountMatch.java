/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes._cocos.MCCollectionTypesASTMCGenericTypeCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that references to generic types uses a correct parameter count (w.r.t
 * the generics definition).
 *
 */
public class GenericParameterCountMatch implements MCCollectionTypesASTMCGenericTypeCoCo {

  @Override
  public void check(ASTMCGenericType type) {
    // note that generics cannot be defined within C4A and only three default
    // default types use generics (Optional, List, Set) and they all have
    // exactly one type parameter.
    List<ASTMCTypeArgument> args = type.getMCTypeArgumentList();
    if (!args.isEmpty()) {
      String typeName = new AstPrinter().printType(type);
      check(typeName, args);
    }
  }
  
  private void check(String typeName, List<ASTMCTypeArgument> typeArguments) {
    // note that "no type arguments" is checked by coco GenericTypeHasParameters
    if (!typeArguments.isEmpty()) {
      String typeWithoutGenerics = typeName;
      if (typeName.indexOf('<') > 0) {
        typeWithoutGenerics = typeName.substring(0, typeName.indexOf('<'));
      }

      int actualCount = typeArguments.size();
      int expectedCount = 1;
      if (typeName.startsWith("Map")) {
        expectedCount = 2;
      }
      if (expectedCount != actualCount) {
        Log.error(String.format(
            "0xC4A31 Generic type %s has %d type-parameter, but %d where given ('%s').",
            typeWithoutGenerics,
            expectedCount,
            actualCount,
            typeName),
            typeArguments.get(0).get_SourcePositionStart());
      }
    }
  }
}
