/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.similarity;

import de.monticore.cdbasis._ast.ASTCDAttributeTOP;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.caching.StructureCache;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CDTypeEmbeddingWithAttributesSimilarity extends CDEmbeddingSimilarity<ASTCDType> {

  private final StructureCache structureCache;

  public CDTypeEmbeddingWithAttributesSimilarity(StructureCache structureCache) {
    this.structureCache = structureCache;
  }

  @Override
  public Double computeWeight(ASTCDType srcElem, ASTCDType tgtElem) {
    return matchMultipleNamesWithEmbedding(srcElem, tgtElem, this::getClassAndAttributeNames,
        vectorNormalize, vectorConcatenate, vectorNormalize);
  }

  public List<String> getClassAndAttributeNames(ASTCDType elem) {
    List<String> result = new LinkedList<>();
    result.add(elem.getSymbol().getName());
    result.addAll(structureCache.getAttributes(elem).stream().map(ASTCDAttributeTOP::getName)
        .collect(Collectors.toList()));
    return result;
  }

}
