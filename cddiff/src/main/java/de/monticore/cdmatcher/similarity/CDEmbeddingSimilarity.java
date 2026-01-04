/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.similarity;

import com.github.jfasttext.JFastText;
import de.monticore.cdmatcher.CDSimilarity;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

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

  public static void initialize(String vectorFile) {
    if (!(new File(vectorFile).exists())) {
      Log.error("vector file does not exist, run cddiff:downloadVectors");
    } else {
      jft.loadModel(vectorFile);
      initialized = true;
    }
  }

}
