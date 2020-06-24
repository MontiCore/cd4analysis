/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos;

import de.monticore.cd4codebasis.typescalculator.DeriveSymTypeOfCD4CodeBasis;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._cocos.MCCollectionTypesASTMCGenericTypeCoCo;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that references to generic types uses a correct parameter count (w.r.t
 * the generics definition).
 */
public class GenericParameterCountMatch
    implements MCCollectionTypesASTMCGenericTypeCoCo {

  @Override
  public void check(ASTMCGenericType type) {
    final DeriveSymTypeOfCD4CodeBasis deriveSymTypeOfCD4CodeBasis = new DeriveSymTypeOfCD4CodeBasis();
    final Optional<SymTypeExpression> symTypeExpression = deriveSymTypeOfCD4CodeBasis.calculateType(type);
    MCCollectionTypesPrettyPrinter prettyPrinter = new MCCollectionTypesPrettyPrinter(new IndentPrinter());

    type.accept(prettyPrinter);
    String typeName = prettyPrinter.getPrinter().getContent();

    if (!symTypeExpression.isPresent()) {
      Log.error(String.format(
          "0xCDCF0: The type %s could not be calculated.",
          typeName));
    }
    else if (symTypeExpression.get().isGenericType()) {
      final SymTypeOfGenerics symTypeOfGenerics = (SymTypeOfGenerics) symTypeExpression.get();
      int expectedArgumentCount;
      final String typeWithoutGenerics = symTypeOfGenerics.getTypeConstructorFullName();
      switch (typeWithoutGenerics) {
        case "List":
        case "Set":
          expectedArgumentCount = 1;
          break;
        case "Map":
          expectedArgumentCount = 2;
          break;
        default:
          expectedArgumentCount = 0;
      }

      final List<SymTypeExpression> typeArguments = symTypeOfGenerics.getArgumentList();
      final int actualArgumentCount = typeArguments.size();
      if (expectedArgumentCount != actualArgumentCount) {

        Log.error(String.format(
            "0xCDCF1: Generic type %s has %d type-parameter, but %d where given ('%s').",
            typeWithoutGenerics,
            expectedArgumentCount,
            actualArgumentCount,
            typeName
            ),
            typeArguments.get(0).getTypeInfo().getSourcePosition());
      }
    }
  }
}
