package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._cocos.TypesASTSimpleReferenceTypeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that references to generic types use at least one parameter.
 *
 * @author Robert Heim
 */
public class GenericTypeHasParameters implements TypesASTSimpleReferenceTypeCoCo {
  
  @Override
  public void check(ASTSimpleReferenceType type) {
    Optional<ASTTypeArguments> args = type.getTypeArguments();
    if (args.isPresent()) {
      String typeName = TypesPrinter.printType(type);
      check(typeName, args.get());
    }
  }
  
  private void check(String typeName, ASTTypeArguments typeArguments) {
    if (typeArguments.getTypeArguments().isEmpty()) {
      Log.error(
          String
              .format(
                  "0xC4A30 Generic type %s has no type-parameter. References to generic types must be parametrized.",
                  typeName),
          typeArguments.get_SourcePositionStart());
    }
  }
}
