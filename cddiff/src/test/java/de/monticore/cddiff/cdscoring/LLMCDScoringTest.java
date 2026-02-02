package de.monticore.cddiff.cdscoring;

import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cddiff.syndiff.SynDiffTestBasis;
import de.monticore.cdmatcher.similarity.CDEmbeddingSimilarity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class LLMCDScoringTest extends SynDiffTestBasis {

  static class ScoreFailedTests implements TestWatcher {

    @Override
    public void testFailed(org.junit.jupiter.api.extension.ExtensionContext context, Throwable cause) {
      try {
        File out = new File(dir + "LLMScores.txt");
        Files.writeString(out.toPath(), "Syntax Error\n", out.exists() ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
      } catch (IOException e) {
        System.out.println("Could not write to FailedScores.txt: " + e.getMessage());
      }
    }

  }

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/CDScoring/";
    File out = new File(dir + "LLMScores.txt");
    if (out.exists()) {
      out.delete();
    }
    CDEmbeddingSimilarity.initialize("src/main/resources/crawl-300d-2M-subword.bin");
  }

  public static Stream<Arguments> LLMTestData() throws IOException {
    File root = new File(dir, "GoldenModelset");
    File referenceDir = new File(root, "reference");
    Set<String> generatedFileNames;
    try (Stream<Path> paths = Files.walk(new File(root, "generated1").toPath())) {
      generatedFileNames = paths
        .filter(Files::isRegularFile)
        .map(path -> path.getFileName().toString())
        .collect(Collectors.toSet());
    }
    for(int i = 2; i <=3 ; i++) {
      try (Stream<Path> paths = Files.walk(new File(root, "generated" + i).toPath())) {
        Set<String> currentGeneratedFileNames = paths
          .filter(Files::isRegularFile)
          .map(path -> path.getFileName().toString())
          .collect(Collectors.toSet());
        generatedFileNames.retainAll(currentGeneratedFileNames);
      }
    }

    try (Stream<Path> artifacts = Files.walk(referenceDir.toPath())) {
      // Ensure files exist in all generated dirs
      return artifacts.filter(Files::isRegularFile).map(path -> {
        Assertions.assertTrue(generatedFileNames.contains(path.getFileName().toString()),
          "Reference file " + path.getFileName() + " does not have generated counterparts in all generated directories.");
        return Arguments.of(
          "GoldenModelset/reference/" + path.getFileName(),
          IntStream.rangeClosed(1, 3)
            .mapToObj(i -> "GoldenModelset/generated" + i + "/" + path.getFileName())
            .collect(Collectors.toList())
        );
      }).collect(Collectors.toList()).stream();
    }
  }

  @ParameterizedTest
  @MethodSource("LLMTestData")
  @ExtendWith(ScoreFailedTests.class)
  public void testLLMModels(String referenceFile, List<String> generatedFiles) throws IOException {
    List<Double> scores = new LinkedList<>();
    for(String generatedFile : generatedFiles) {
      parseModels(generatedFile, referenceFile);
      CDScoring llmCDScoring = new CDScoring(src, tgt);
      double score = llmCDScoring.score(5, 0.5);
      scores.add(score);
      System.out.println("Score for " + generatedFile + " - " + referenceFile + ": " + score);
    }
    File out = new File(dir + "LLMScores.txt");

    Files.writeString(out.toPath(), scores.stream().map(Object::toString).map(s -> s.replace(".", ",")).collect(Collectors.joining("\t")) + "\n", out.exists() ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
  }

  @ParameterizedTest
  @MethodSource("syntheticArtifacts")
  public void testSyntheticArtifactsIdentity(String artifact) {
    Assertions.assertTrue(score(artifact, artifact, 5, 0.5, true) > 0.999);
  }

  @ParameterizedTest
  @MethodSource("ArtifactCombinations")
  public void testSyntheticArtifacts(String srcArtifact, String tgtArtifact) {
    double score = score(srcArtifact, tgtArtifact, 5, 0.5, true);
    Assertions.assertTrue(score >= 0.0 && score <= 1.0);
  }

  @Test
  public void testSpecificSyntheticArtifacts() {
    // ToDo: Enum Pre-Matches
    // ToDo: Increase name importance and maybe adjust embedding accordingly
    score("SyntheticArtifacts/unconstrained_code_block_31.cd4c", "SyntheticArtifacts/unconstrained_code_block_23.cd4c", 5, 0.5, true);
    score("SyntheticArtifacts/unconstrained_code_block_29.cd4c", "SyntheticArtifacts/unconstrained_code_block_26.cd4c", 5, 0.5, true);
    score("SyntheticArtifacts/unconstrained_code_block_9.cd4c", "SyntheticArtifacts/unconstrained_code_block_22.cd4c", 5, 0.5, true);
    score("SyntheticArtifacts/unconstrained_code_block_7.cd4c", "SyntheticArtifacts/unconstrained_code_block_64.cd4c", 5, 0.5, true);
  }


  public static Stream<Arguments> ArtifactCombinations() {
    List<Arguments> arguments = syntheticArtifacts().collect(Collectors.toList());
    Random random = new Random();
    return Stream.generate(() -> Arguments.of(
      arguments.get(random.nextInt(arguments.size())).get()[0],
      arguments.get(random.nextInt(arguments.size())).get()[0])).limit(10);
  }


  public static Stream<Arguments> syntheticArtifacts() {
    File root = new File(dir, "SyntheticArtifacts");
    try (
      Stream<Path> artifacts = Files.walk(root.toPath())) {
      return artifacts.filter(Files::isRegularFile)
        .map(artifact -> Arguments.of(
          "SyntheticArtifacts/" + artifact.getFileName()
        )).collect(Collectors.toList()).stream();
    } catch (
      IOException e) {
      fail("Could not read directory: " + root.getAbsolutePath() + " - " + e.getMessage());
      return Stream.empty();
    }
  }

  private double score(String srcFile, String tgtFile, int iterations, double threshold, boolean useEmbedding) {
    try {
      parseModels(srcFile, tgtFile);
      System.out.println("Syntax correct for " + srcFile + " and " + tgtFile);
    } catch (Exception e) {
      System.out.println("Syntax not correct for " + srcFile + " and " + tgtFile + ": " + e.getMessage());
      return -1.0;
    }

    DecimalFormat df = new DecimalFormat("0.0000");

    CDScoring cdScoring = new CDScoring(src, tgt);
    double score = cdScoring.score(iterations, threshold, useEmbedding);
    System.out.println("Iterations: " + iterations + ", Threshold: " + threshold + ", Use Embedding: " + useEmbedding);
    System.out.println("Score for " + srcFile + " and " + tgtFile + ": " + df.format(score));
    System.out.println("----------------------------------------");
    return score;
  }

  private static String printAssociationMatches(Map<ASTCDAssociation, ASTCDAssociation> map) {
    StringBuilder sb = new StringBuilder();
    if (map.isEmpty()) {
      sb.append("No Association Matches found.\n");
    } else {
      sb.append("Association Matches:\n");
      map.forEach((src, tgt) -> {
        sb.append("Source: ").append(printAssociation(src)).append("\n");
        sb.append("Target: ").append(printAssociation(tgt)).append("\n");
        sb.append("--------------------------------------------------\n");
      });
    }
    return sb.toString();
  }

  private static String printAssociation(ASTCDAssociation association) {
    StringBuilder sb = new StringBuilder();
    if (association.isPresentName()) sb.append(association.getName()).append(": ");
    if (association.getLeft().isPresentCDRole()) sb.append("(").append(association.getLeft().getCDRole().getName()).append(") ");
    sb.append(association.getLeft().getMCQualifiedType().getMCQualifiedName().getBaseName());
    if (association.getLeft().isPresentCDCardinality()) sb.append(printCardinality(association.getLeft().getCDCardinality()));
    sb.append(printDirection(association.getCDAssocDir()));
    if (association.getRight().isPresentCDRole()) sb.append("(").append(association.getRight().getCDRole().getName()).append(") ");
    sb.append(association.getRight().getMCQualifiedType().getMCQualifiedName().getBaseName());
    if (association.getRight().isPresentCDCardinality()) sb.append(printCardinality(association.getRight().getCDCardinality()));
    return sb.toString();
  }

  private static String printCardinality(ASTCDCardinality cardinality) {
    int lowerBound = cardinality.getLowerBound();
    int upperBound = cardinality.getUpperBound();

    if(lowerBound == upperBound) return lowerBound == 0 ? "[*]" : "[" + lowerBound + "] ";
    else  return "[" + lowerBound + ".." + (upperBound == 0 ? "*" : upperBound) + "] ";
  }

  private static String printDirection(ASTCDAssocDir direction) {
    if(direction.isBidirectional()) return "<-> ";
    if(direction.isDefinitiveNavigableRight()) return "-> ";
    if(direction.isDefinitiveNavigableLeft()) return "<- ";
    return "";
  }

}
