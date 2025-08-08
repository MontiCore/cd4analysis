package de.monticore.cdmatcher.similarity;

import de.monticore.cdmatcher.CDSimilarity;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isNestedType;

public class MCGenericTypeSimilarity implements CDSimilarity<ASTMCGenericType> {
  // Computes the similarity between two MCGenericType, the inner types will be ignored unless they are also MCGenericTypes.
  // Supported types are: List, Set, Optional. Map is not supported as well as other generic types.

  // similarities not included are considered to be 0.0
  private static final Map<Pair<String, String>, Double> similarities = Map.of(
    new Pair<>("List", "List"), 1.0,
    new Pair<>("Set", "Set"), 1.0,
    new Pair<>("Optional", "Optional"), 1.0,
    new Pair<>("List", "Set"), 0.8,
    new Pair<>("Set", "List"), 0.8
  );

  @Override
  public Double computeWeight(ASTMCGenericType srcElem, ASTMCGenericType tgtElem) {
    if (srcElem.getMCTypeArgumentList().size() != 1 || tgtElem.getMCTypeArgumentList().size() != 1) {
      // All the supported types have exactly one type argument.
      return 0.0;
    }

    String srcName = srcElem.getName(0);
    String tgtName = tgtElem.getName(0);
    Double similatity = similarities.getOrDefault(new Pair<>(srcName, tgtName), 0.0);

    ASTMCType srcInnerType = srcElem.getMCTypeArgument(0).getMCTypeOpt().orElse(null);
    ASTMCType tgtInnerType = tgtElem.getMCTypeArgument(0).getMCTypeOpt().orElse(null);
    if (srcInnerType == null || tgtInnerType == null) {
      return 0.0;
    }

    if (isNestedType(srcInnerType) != isNestedType(tgtInnerType)) {
      return 0.0;
    }

    if (!isNestedType(srcInnerType)) {
      return similatity;
    } else {
      return similatity * computeWeight((ASTMCGenericType) srcInnerType, (ASTMCGenericType) tgtInnerType);
    }
  }
}
