/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.similarity;

import com.github.jfasttext.JFastText;
import de.monticore.cdmatcher.CDSimilarity;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public abstract class CDEmbeddingSimilarity<T> implements CDSimilarity<T> {

  private static final JFastText jft = new JFastText();
  private static boolean initialized = false;

  public Double matchNameWithEmbedding(T srcElem, T tgtElem, Function<T, String> extractName) {
    if (!initialized) {
      Log.error("Initialize MatchByNameEmbedding before using an Embedding based MatchingStrategy");
    }
    String srcName = extractName.apply(srcElem).toLowerCase();
    String tgtName = extractName.apply(tgtElem).toLowerCase();

    List<Float> srcVector = jft.getVector(srcName);
    List<Float> tgtVector = jft.getVector(tgtName);

    if (srcVector.size() != tgtVector.size()) {
      Log.error("Source Vector does not match Target Vector");
    }

    // opposite vectors are considered as not matching
    return Math.max(cosineSimilarity(srcVector, tgtVector), 0.0);
  }

  public Double matchMultipleNamesWithEmbedding(T srcElem, T tgtElem,
      Function<T, List<String>> extractNames, Consumer<List<Float>> vectorPreprocessing,
      Consumer<List<Float>> vectorPostprocessing) {
    List<String> srcNames = extractNames.apply(srcElem);
    List<String> tgtNames = extractNames.apply(tgtElem);

    srcNames = srcNames.stream().map(String::toLowerCase).collect(Collectors.toList());
    tgtNames = tgtNames.stream().map(String::toLowerCase).collect(Collectors.toList());

    List<List<Float>> srcVectors = srcNames.stream().map(jft::getVector).collect(Collectors
        .toList());
    List<List<Float>> tgtVectors = tgtNames.stream().map(jft::getVector).collect(Collectors
        .toList());

    srcVectors.forEach(vectorPreprocessing);
    tgtVectors.forEach(vectorPreprocessing);

    // Ensure all vectors are of the same size
    if(Stream.of(srcVectors, tgtVectors).flatMap(List::stream).mapToInt(List::size).distinct().count() != 1) {
      Log.error("Source and Target Vectors must be of the same size for cosine similarity calculation. Make sure the preprocessing step does not change the size of the vectors.");
    }

    List<Float> srcAccumulated = vectorAverage(srcVectors);
    List<Float> tgtAccumulated = vectorAverage(tgtVectors);

    vectorPostprocessing.accept(srcAccumulated);
    vectorPostprocessing.accept(tgtAccumulated);

    // opposite vectors are considered as not matching
    return Math.max(cosineSimilarity(srcAccumulated, tgtAccumulated), 0.0);
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

  private List<Float> vectorAverage(List<List<Float>> vectors) {
    if(vectors.stream().map(List::size).distinct().count() != 1) {
      Log.error("Vectors must be of the same size for averaging.");
    }
    int vectorSize = vectors.get(0).size();
    List<Float> accumulated = vectors.stream().collect(emptyVectorSupplier(vectorSize), vectorAdd, vectorAdd);
    accumulated.replaceAll(entry -> entry / vectors.size());
    return accumulated;
  }

  private final BiConsumer<List<Float>, List<Float>> vectorAdd = (List<Float> firstVector,
                                                                  List<Float> secondVector) -> {
    // Both Lists should be the same size, otherwise additional entries will be ignored
    for (int i = 0; i < Math.min(firstVector.size(), secondVector.size()); i++) {
      firstVector.set(i, firstVector.get(i) + secondVector.get(i));
    }
  };

  public static void initialize(String vectorFile) {
    if (!(new File(vectorFile).exists())) {
      Log.error("vector file does not exist, run cddiff:downloadVectors");
    } else {
      jft.loadModel(vectorFile);
      initialized = true;
    }
  }

  //Concatenation of embedding vectors is done by appending on to the end of another, when the same amount of vectors are concatenated relative distances are preserved
  public static BiConsumer<List<Float>, List<Float>> vectorConcatenate = List::addAll;

  public static Consumer<List<Float>> vectorNormalize = (List<Float> vector) -> {
    float sum = vector.stream().reduce(0f, Float::sum);
    vector.replaceAll(aFloat -> aFloat / sum);
  };

  public static Consumer<List<Float>> doNothing = (List<Float> vector) -> {};

  private Supplier<List<Float>> emptyVectorSupplier(int size) {
    return () -> DoubleStream.generate(() -> 0.0).limit(size).mapToObj(value -> (float) value).collect(Collectors.toList());
  }

}
