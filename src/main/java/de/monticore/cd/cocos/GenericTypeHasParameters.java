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

import java.util.Optional;

/**
 * Checks that references to generic types use at least one parameter.
 */
public class GenericTypeHasParameters
    implements MCCollectionTypesASTMCGenericTypeCoCo {

  @Override
  public void check(ASTMCGenericType type) {
    final DeriveSymTypeOfCD4CodeBasis deriveSymTypeOfCD4CodeBasis = new DeriveSymTypeOfCD4CodeBasis();
    final Optional<SymTypeExpression> symTypeExpression = deriveSymTypeOfCD4CodeBasis.calculateType(type);
    MCCollectionTypesPrettyPrinter prettyPrinter = new MCCollectionTypesPrettyPrinter(new IndentPrinter());

    type.accept(prettyPrinter);
    String typeName = prettyPrinter.getPrinter().getContent();

    if (!symTypeExpression.isPresent()) {
      Log.error(String
          .format(
              "0xCDCF3: The type %s could not be calculated.",
              typeName));
    }
    else if (symTypeExpression.get().isGenericType()) {
      final SymTypeOfGenerics symTypeOfGenerics = (SymTypeOfGenerics) symTypeExpression.get();
      if (symTypeOfGenerics.getArgumentList().isEmpty()) {
        Log.error(
            String
                .format(
                    "0xCDCF4: Generic type %s has no type-parameter. References to generic types must be parametrized.",
                    typeName));
      }
    }
  }
}
