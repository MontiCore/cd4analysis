/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.similarity;

import com.github.jfasttext.JFastText;
import de.monticore.cdmatcher.CDSimilarity;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CDEmbeddingSimilarity<T> implements CDSimilarity<T> {
  
  private static final JFastText jft = new JFastText();
  private static final String vectorFile = "src/main/resources/crawl-300d-2M-subword.bin";
  private static boolean initialized = false;
  
  public Double matchNameWithEmbedding(T srcElem, T tgtElem, Function<T, String> extractName) {
    if (!initialized) {
      Log.error("Initialize MatchByNameEmbedding before using an Embedding based MatchingStrategy");
    }
    String srcName = extractName.apply(srcElem);
    String tgtName = extractName.apply(tgtElem);
    
    List<Float> srcVector = jft.getVector(srcName);
    List<Float> tgtVector = jft.getVector(tgtName);
    
    if (srcVector.size() != tgtVector.size()) {
      Log.error("Source Vector does not match Target Vector");
    }
    
    return cosineSimilarity(srcVector, tgtVector);
  }
  
  public Double matchMultipleNamesWithEmbedding(T srcElem, T tgtElem,
      Function<T, List<String>> extractNames, Consumer<List<Float>> vectorPreprocessing,
      BiConsumer<List<Float>, List<Float>> vectorAccumulator,
      Consumer<List<Float>> vectorPostprocessing) {
    List<String> srcNames = extractNames.apply(srcElem);
    List<String> tgtNames = extractNames.apply(tgtElem);
    
    List<List<Float>> srcVectors = srcNames.stream().map(jft::getVector).collect(Collectors
        .toList());
    List<List<Float>> tgtVectors = tgtNames.stream().map(jft::getVector).collect(Collectors
        .toList());
    
    srcVectors.forEach(vectorPreprocessing);
    tgtVectors.forEach(vectorPreprocessing);
    
    List<Float> srcAccumulated = srcVectors.stream().collect(LinkedList::new, vectorAccumulator,
        vectorAccumulator);
    List<Float> tgtAccumulated = tgtVectors.stream().collect(LinkedList::new, vectorAccumulator,
        vectorAccumulator);
    
    vectorPostprocessing.accept(srcAccumulated);
    vectorPostprocessing.accept(tgtAccumulated);
    
    return cosineSimilarity(srcAccumulated, tgtAccumulated);
  }
  
  private double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
    Iterator<Float> it1 = vec1.iterator();
    Iterator<Float> it2 = vec2.iterator();
    double dotProduct = 0;
    double sum1 = 0;
    double sum2 = 0;
    
    while (it1.hasNext() && it2.hasNext()) {
      float entry1 = it1.next();
      float entry2 = it2.next();
      dotProduct += entry1 * entry2;
      sum1 += entry1 * entry1;
      sum2 += entry2 * entry2;
    }
    
    return dotProduct / (Math.sqrt(sum1) * Math.sqrt(sum2));
  }
  
  public static void initialize() {
    if (!(new File(vectorFile).exists())) {
      Log.error("vector file does not exist, run cddiff:downloadVectors");
    }
    jft.loadModel(vectorFile);
    initialized = true;
  }
  
  //Both Lists should be the same size, otherwise they will be trimmed to the shorter one
  public static BiConsumer<List<Float>, List<Float>> vectorConcatenate = (List<Float> firstVector,
      List<Float> secondVector) -> {
    for (int i = 0; i < Math.min(firstVector.size(), secondVector.size()); i++) {
      firstVector.set(i, firstVector.get(i) + secondVector.get(i));
    }
  };
  
  public static Consumer<List<Float>> vectorNormalize = (List<Float> vector) -> {
    float sum = vector.stream().reduce(0f, Float::sum);
    vector.replaceAll(aFloat -> aFloat / sum);
  };
  
}
