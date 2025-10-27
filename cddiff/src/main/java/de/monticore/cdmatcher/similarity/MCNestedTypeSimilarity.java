package de.monticore.cdmatcher.similarity;

import de.monticore.cdmatcher.CDSimilarity;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isArrayType;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isNestedType;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isGenericType;

public class MCNestedTypeSimilarity implements CDSimilarity<ASTMCType> {
  // Computes the similarity between two MCGenericType or MCArrayTypes, the inner types will be ignored unless they are also MCGenericTypes.
  // Supported types are: Array, List, Set, Optional. Map is not supported as well as other generic types.
  // Using MCNestedTypeSimilarity a Class that is not MCGenericType or MCArrayType will always have similarity 0.0 with any other type.

  // similarities not included are considered to be 0.0
  private static final Map<Pair<String, String>, Double> similarities = Map.of(
    new Pair<>("List", "List"), 1.0,
    new Pair<>("Set", "Set"), 1.0,
    new Pair<>("Optional", "Optional"), 1.0,
    new Pair<>("List", "Set"), 0.8,
    new Pair<>("Set", "List"), 0.8,
    new Pair<>("Array", "List"), 0.9,
    new Pair<>("List", "Array"), 0.9,
    new Pair<>("Array", "Set"), 0.7,
    new Pair<>("Set", "Array"), 0.7
  );


  @Override
  public Double computeWeight(ASTMCType srcElem, ASTMCType tgtElem) {
    if(isNestedType(srcElem) != isNestedType(tgtElem)) {
      return 0.0;
    }
    if(!isNestedType(srcElem)) {
      return 1.0;
    }
    if(isGenericType(srcElem) && isGenericType(tgtElem)) {
      return computeWeight((ASTMCGenericType) srcElem, (ASTMCGenericType) tgtElem);
    } else if (isArrayType(srcElem) && isArrayType(tgtElem)) {
      return computeWeight((ASTMCArrayType) srcElem, (ASTMCArrayType) tgtElem);
    } else {
      ASTMCType srcInnerElem;
      ASTMCType tgtInnerElem;
      String srcName;
      String tgtName;
      // one is array type, the other is generic type, still check for later extensions
      if(isArrayType(srcElem) && isGenericType(tgtElem)){
        srcInnerElem = ((ASTMCArrayType) srcElem).getMCType();
        if (((ASTMCGenericType) tgtElem).getMCTypeArgumentList().size() != 1) {
          // All the supported types have exactly one type argument.
          return 0.0;
        }
        tgtInnerElem = ((ASTMCGenericType) tgtElem).getMCTypeArgument(0).getMCTypeOpt().orElse(null);
        srcName = "Array";
        tgtName = ((ASTMCGenericType) tgtElem).getName(0);
      } else if(isGenericType(srcElem) && isArrayType(tgtElem)) {
        if (((ASTMCGenericType) srcElem).getMCTypeArgumentList().size() != 1) {
          // All the supported types have exactly one type argument.
          return 0.0;
        }
        srcInnerElem = ((ASTMCGenericType) srcElem).getMCTypeArgument(0).getMCTypeOpt().orElse(null);
        tgtInnerElem = ((ASTMCArrayType) tgtElem).getMCType();
        srcName = ((ASTMCGenericType) srcElem).getName(0);
        tgtName = "Array";
      }
      else {
        Log.error("MCNestedTypeSimilarity: Unsupported nested types: " + srcElem.getClass().getSimpleName() + " and " + tgtElem.getClass().getSimpleName() + "only MCGenericType and MCArrayType are supported.");
        return 0.0;
      }
      if(srcInnerElem == null || tgtInnerElem == null) {
        return 0.0;
      }
      if(isNestedType(srcInnerElem) != isNestedType(tgtInnerElem)) {
        return 0.0;
      }
      if(!isNestedType(srcInnerElem)) {
        return 1.0;
      } else {
        Double similarity = similarities.getOrDefault(new Pair<>(srcName, tgtName), 0.0);
        return similarity * computeWeight(srcInnerElem, tgtInnerElem);
      }
    }

  }

  private Double computeWeight(ASTMCArrayType srcArray, ASTMCArrayType tgtArray) {
    ASTMCType srcInnerType = srcArray.getMCType();
    ASTMCType tgtInnerType = tgtArray.getMCType();
    if (isNestedType(srcInnerType) != isNestedType(tgtInnerType)) {
      return 0.0;
    }

    if (!isNestedType(srcInnerType)) {
      return 1.0;
    } else {
      return computeWeight(srcInnerType, tgtInnerType);
    }
  }

  private Double computeWeight(ASTMCGenericType srcElem, ASTMCGenericType tgtElem) {
    if (srcElem.getMCTypeArgumentList().size() != 1 || tgtElem.getMCTypeArgumentList().size() != 1) {
      // All the supported types have exactly one type argument.
      return 0.0;
    }

    String srcName = srcElem.getName(0);
    String tgtName = tgtElem.getName(0);
    Double similarity = similarities.getOrDefault(new Pair<>(srcName, tgtName), 0.0);

    ASTMCType srcInnerType = srcElem.getMCTypeArgument(0).getMCTypeOpt().orElse(null);
    ASTMCType tgtInnerType = tgtElem.getMCTypeArgument(0).getMCTypeOpt().orElse(null);
    if (srcInnerType == null || tgtInnerType == null) {
      return 0.0;
    }

    if (isNestedType(srcInnerType) != isNestedType(tgtInnerType)) {
      return 0.0;
    }

    if (!isNestedType(srcInnerType)) {
      return similarity;
    } else {
      return similarity * computeWeight(srcInnerType, tgtInnerType);
    }
  }
}
