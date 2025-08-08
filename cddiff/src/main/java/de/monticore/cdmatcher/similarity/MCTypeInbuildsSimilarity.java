package de.monticore.cdmatcher.similarity;

import de.monticore.cdmatcher.CDSimilarity;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cddiff.ow2cw.CDAttributeHelper.getPrimitiveType;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.getQualifiedType;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isPrimitiveType;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isQualifiedType;

public class MCTypeInbuildsSimilarity implements CDSimilarity<ASTMCType> {
  // This class is used to match inbuild types like int, boolean, String, etc. and their wrappers
  // it will return 0.0 for all comparisons involving other types except if their names match

  // A match between primitive types and their wrapper types are considered identical
  public static Map<String, String> wrapperTypes = Map.of(
      "int", "Integer",
      "boolean", "Boolean",
      "char", "Character",
      "byte", "Byte",
      "short", "Short",
      "long", "Long",
      "float", "Float",
      "double", "Double"
  );

  // Similarities not included are 0.0
  public Map<Pair<String, String>, Double> inbuildsSimilarity = new HashMap<>();

  public MCTypeInbuildsSimilarity() {
    inbuildsSimilarity.put(new Pair<>("Character", "Byte"), 0.5);
    inbuildsSimilarity.put(new Pair<>("Byte", "Short"), 0.9);
    inbuildsSimilarity.put(new Pair<>("Byte", "Integer"), 0.8);
    inbuildsSimilarity.put(new Pair<>("Byte", "Long"), 0.7);
    inbuildsSimilarity.put(new Pair<>("Byte", "Float"), 0.2);
    inbuildsSimilarity.put(new Pair<>("Byte", "Double"), 0.1);
    inbuildsSimilarity.put(new Pair<>("Short", "Integer"), 0.9);
    inbuildsSimilarity.put(new Pair<>("Short", "Long"), 0.8);
    inbuildsSimilarity.put(new Pair<>("Short", "Float"), 0.3);
    inbuildsSimilarity.put(new Pair<>("Short", "Double"), 0.2);
    inbuildsSimilarity.put(new Pair<>("Integer", "Long"), 0.9);
    inbuildsSimilarity.put(new Pair<>("Integer", "Float"), 0.4);
    inbuildsSimilarity.put(new Pair<>("Integer", "Double"), 0.3);
    inbuildsSimilarity.put(new Pair<>("Long", "Float"), 0.5);
    inbuildsSimilarity.put(new Pair<>("Long", "Double"), 0.4);
    inbuildsSimilarity.put(new Pair<>("Float", "Double"), 0.9);

    Map<Pair<String, String>, Double> reverse = inbuildsSimilarity.entrySet().stream()
      .collect(Collectors.toMap(
        entry -> new Pair<>(entry.getKey().b, entry.getKey().a),
        Map.Entry::getValue
      ));
    inbuildsSimilarity.putAll(reverse);
  }

  @Override
  public Double computeWeight(ASTMCType srcElem, ASTMCType tgtElem) {
    String srcName = null;
    String tgtName = null;

    if(isPrimitiveType(srcElem)) {
      srcName = wrapperTypes.get(getPrimitiveType(srcElem).toString());
    }
    else if(isQualifiedType(srcElem)) {
      srcName = getQualifiedType(srcElem).getMCQualifiedName().getBaseName();
    }
    if(isPrimitiveType(tgtElem)) {
      tgtName = wrapperTypes.get(getPrimitiveType(tgtElem).toString());
    }
    else if(isQualifiedType(tgtElem)) {
      tgtName = getQualifiedType(tgtElem).getMCQualifiedName().getBaseName();
    }

    if (srcName == null || tgtName == null) {
      return 0.0;
    }

    if (srcName.equals(tgtName)) {
      return 1.0;
    }

    return inbuildsSimilarity.getOrDefault(new Pair<>(srcName, tgtName), 0.0);
  }
}
