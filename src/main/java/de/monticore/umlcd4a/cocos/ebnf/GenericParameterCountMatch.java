package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._cocos.TypesASTSimpleReferenceTypeCoCo;

/**
 * Checks that references to generic types uses a correct parameter count (w.r.t
 * the generics definition).
 *
 * @author Robert Heim
 */
public class GenericParameterCountMatch implements TypesASTSimpleReferenceTypeCoCo {
  
  @Override
  public void check(ASTSimpleReferenceType type) {
    // note that generics cannot be defined within C4A and only three default
    // default types use generics (Optional, List, Set) and they all have
    // exactly one type parameter.
    Optional<ASTTypeArguments> args = type.getTypeArguments();
    if (args.isPresent()) {
      String typeName = TypesPrinter.printType(type);
      check(typeName, args.get());
    }
  }
  
  private void check(String typeName, ASTTypeArguments typeArguments) {
    // note that "no type arguments" is checked by coco GenericTypeHasParameters
    if (typeArguments.getTypeArguments().size() > 0) {
      String typeWithoutGenerics = typeName;
      if (typeName.indexOf('<') > 0) {
        typeWithoutGenerics = typeName.substring(0, typeName.indexOf('<'));
      }
      
      int actualCount = typeArguments.getTypeArguments().size();
      int expectedCount = 1;
      if (expectedCount != actualCount) {
        CoCoLog.error("0xC4A31",
            String.format(
                "Generic type %s has %d type-parameter, but %d where given ('%s').",
                typeWithoutGenerics,
                expectedCount,
                actualCount,
                typeName),
            typeArguments.get_SourcePositionStart());
      }
    }
  }
}
