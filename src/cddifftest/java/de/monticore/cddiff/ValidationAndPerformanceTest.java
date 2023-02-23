package de.monticore.cddiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odvalidity.MultiInstanceMatcher;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ValidationAndPerformanceTest {

  private static final int diffsize = 3;

  @BeforeEach
  public void init() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  /** Disabled for GitLab pipeline. */
  @Disabled
  @ParameterizedTest
  @MethodSource("performanceSet")
  public void testOWDiffPerformance(String file1, String file2) {
    String path = "src/cddifftest/resources/validation/Performance/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      // alloy-based
      long startTime_alloy = System.currentTimeMillis(); // start time
      CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time

      Log.println("alloy-based: " + (endTime_alloy - startTime_alloy));

      // reduction-based
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time

      Log.println("reduction-based: " + (endTime_reduction - startTime_reduction));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  /** Disabled for GitLab pipeline. */
  @Disabled
  @ParameterizedTest
  @MethodSource("cddiffSet")
  public void testOWDiffPerformance2(String file1, String file2) {
    String path = "src/cddifftest/resources/validation/cddiff/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      // alloy-based
      long startTime_alloy = System.currentTimeMillis(); // start time
      CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time

      Log.println("alloy-based: " + (endTime_alloy - startTime_alloy));

      // reduction-based
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time

      Log.println("reduction-based: " + (endTime_reduction - startTime_reduction));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  /** Disabled for GitLab pipeline. */
  @Disabled
  @ParameterizedTest
  @MethodSource("cd4analysisSet")
  public void testOWDiffPerformance3(String file1, String file2) {
    String path = "src/cddifftest/resources/validation/cd4analysis/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      // alloy-based
      long startTime_alloy = System.currentTimeMillis(); // start time
      CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time

      Log.println("alloy-based: " + (endTime_alloy - startTime_alloy));

      // reduction-based
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time

      Log.println("reduction-based: " + (endTime_reduction - startTime_reduction));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("completeSet")
  public void testNoDiffEmpty(String file1) {
    String path = "src/cddifftest/resources/validation/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + "Performance/Empty.cd");

      List<ASTODArtifact> witnesses;

      // alloy-based
      long startTime_alloy = System.currentTimeMillis(); // start time
      witnesses =
          CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time

      Log.println("alloy-based: " + (endTime_alloy - startTime_alloy));
      Assertions.assertTrue(witnesses.isEmpty());

      // reduction-based
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      witnesses =
          CDDiff.computeAlloySemDiff(
              cd1, cd2, diffsize, 1, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time

      Log.println("reduction-based: " + (endTime_reduction - startTime_reduction));
      Assertions.assertTrue(witnesses.isEmpty());

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("completeSet")
  public void testNoDiffSame(String file1) {
    String path = "src/cddifftest/resources/validation/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);

      List<ASTODArtifact> witnesses;

      // alloy-based
      long startTime_alloy = System.currentTimeMillis(); // start time
      witnesses =
          CDDiff.computeAlloySemDiff(cd1, cd1, diffsize, 1, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time

      Log.println("alloy-based: " + (endTime_alloy - startTime_alloy));
      Assertions.assertTrue(witnesses.isEmpty());

      // reduction-based
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      ASTCDCompilationUnit cd2 = cd1.deepClone();
      trafo.transform(cd1, cd2);
      CDDiff.computeAlloySemDiff(cd1, cd1, diffsize, 1, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time

      Log.println("reduction-based: " + (endTime_reduction - startTime_reduction));
      Assertions.assertTrue(witnesses.isEmpty());

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("performanceSet")
  public void testAlloyBasedOWDiff(String file1, String file2) {
    String path = "src/cddifftest/resources/validation/Performance/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // add subclasses to interfaces and abstract classes
      ReductionTrafo.addSubClasses4Diff(cd1);

      // add dummy-class for associations
      String dummyClassName = "Dummy4Diff";
      ReductionTrafo.addDummyClass4Associations(cd1, dummyClassName);
      ReductionTrafo.addDummyClass4Associations(cd2, dummyClassName);

      List<ASTODArtifact> witnesses =
          CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 5, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      Assertions.assertFalse(witnesses.isEmpty());

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      System.out.println("Objects per OD: " + getMeanNumberOfObjects(witnesses));
      System.out.println("Links per OD: " + getMeanNumberOfLinks(witnesses));
      System.out.println("Types per Object: " + getMeanNumberOfTypePerObject(witnesses));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("performanceSet")
  public void testReductionBasedOWDiff(String file1, String file2) {
    String path = "src/cddifftest/resources/validation/Performance/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // reduction-based
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      List<ASTODArtifact> witnesses =
          CDDiff.computeAlloySemDiff(
              cd1, cd2, diffsize, 5, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      Assertions.assertFalse(witnesses.isEmpty());

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      System.out.println("Objects per OD: " + getMeanNumberOfObjects(witnesses));
      System.out.println("Links per OD: " + getMeanNumberOfLinks(witnesses));
      System.out.println("Types per Object: " + getMeanNumberOfTypePerObject(witnesses));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("cddiffSet")
  public void testAlloyBasedOWDiff2(String file1, String file2, boolean diff) {
    String path = "src/cddifftest/resources/validation/cddiff/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // add subclasses to interfaces and abstract classes
      ReductionTrafo.addSubClasses4Diff(cd1);

      // add dummy-class for associations
      String dummyClassName = "Dummy4Diff";
      ReductionTrafo.addDummyClass4Associations(cd1, dummyClassName);
      ReductionTrafo.addDummyClass4Associations(cd2, dummyClassName);

      List<ASTODArtifact> witnesses =
          CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 5, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      if (diff) {
        Assertions.assertFalse(witnesses.isEmpty());
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      System.out.println("Objects per OD: " + getMeanNumberOfObjects(witnesses));
      System.out.println("Links per OD: " + getMeanNumberOfLinks(witnesses));
      System.out.println("Types per Object: " + getMeanNumberOfTypePerObject(witnesses));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("cddiffSet")
  public void testReductionBasedOWDiff2(String file1, String file2, boolean diff) {
    String path = "src/cddifftest/resources/validation/cddiff/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // reduction-based
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      List<ASTODArtifact> witnesses =
          CDDiff.computeAlloySemDiff(
              cd1, cd2, diffsize, 5, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      if (diff) {
        Assertions.assertFalse(witnesses.isEmpty());
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      System.out.println("Objects per OD: " + getMeanNumberOfObjects(witnesses));
      System.out.println("Links per OD: " + getMeanNumberOfLinks(witnesses));
      System.out.println("Types per Object: " + getMeanNumberOfTypePerObject(witnesses));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("cd4analysisSet")
  public void testAlloyBasedOWDiff3(String file1, String file2, boolean diff) {
    String path = "src/cddifftest/resources/validation/cd4analysis/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // add subclasses to interfaces and abstract classes
      ReductionTrafo.addSubClasses4Diff(cd1);

      // add dummy-class for associations
      String dummyClassName = "Dummy4Diff";
      ReductionTrafo.addDummyClass4Associations(cd1, dummyClassName);
      ReductionTrafo.addDummyClass4Associations(cd2, dummyClassName);

      List<ASTODArtifact> witnesses =
          CDDiff.computeAlloySemDiff(cd1, cd2, diffsize, 5, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      if (diff) {
        Assertions.assertFalse(witnesses.isEmpty());
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      System.out.println("Objects per OD: " + getMeanNumberOfObjects(witnesses));
      System.out.println("Links per OD: " + getMeanNumberOfLinks(witnesses));
      System.out.println("Types per Object: " + getMeanNumberOfTypePerObject(witnesses));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("cd4analysisSet")
  public void testReductionBasedOWDiff3(String file1, String file2, boolean diff) {
    String path = "src/cddifftest/resources/validation/cd4analysis/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // reduction-based
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      List<ASTODArtifact> witnesses =
          CDDiff.computeAlloySemDiff(
              cd1, cd2, diffsize, 5, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      if (diff) {
        Assertions.assertFalse(witnesses.isEmpty());
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      System.out.println("Objects per OD: " + getMeanNumberOfObjects(witnesses));
      System.out.println("Links per OD: " + getMeanNumberOfLinks(witnesses));
      System.out.println("Types per Object: " + getMeanNumberOfTypePerObject(witnesses));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  protected float getMeanNumberOfObjects(Collection<ASTODArtifact> ods) {
    float i = 0;
    for (ASTODArtifact od : ods) {
      i +=
          od.getObjectDiagram().getODElementList().stream()
              .filter(e -> e instanceof ASTODObject)
              .count();
    }
    return i / ods.size();
  }

  protected float getMeanNumberOfLinks(Collection<ASTODArtifact> ods) {
    float i = 0;
    for (ASTODArtifact od : ods) {
      i +=
          od.getObjectDiagram().getODElementList().stream()
              .filter(e -> e instanceof ASTODLink)
              .count();
    }
    return i / ods.size();
  }

  protected float getMeanNumberOfTypePerObject(Collection<ASTODArtifact> ods) {
    float i = 0;
    for (ASTODArtifact od : ods) {
      float j = 0;
      Set<ASTODObject> objects =
          od.getObjectDiagram().getODElementList().stream()
              .filter(e -> e instanceof ASTODObject)
              .map(e -> (ASTODObject) e)
              .collect(Collectors.toSet());
      for (ASTODObject object : objects) {
        j += MultiInstanceMatcher.getSuperSetFromStereotype(object).get().size();
      }
      i += j / objects.size();
    }
    return i / ods.size();
  }

  public static Stream<Arguments> performanceSet() {
    return Stream.of(
        Arguments.of("5A.cd", "5B.cd"),
        Arguments.of("10A.cd", "10B.cd"),
        Arguments.of("15A.cd", "15B.cd"),
        Arguments.of("20A.cd", "20B.cd"),
        Arguments.of("25A.cd", "25B.cd"));
  }

  public static Stream<Arguments> cddiffSet() {
    return Stream.of(
        Arguments.of("DEv2.cd", "DEv1.cd", true),
        Arguments.of("EAv2.cd", "EAv1.cd", true),
        Arguments.of("EMTv1.cd", "EMTv2.cd", true),
        Arguments.of("LibraryV2.cd", "LibraryV1.cd", true),
        Arguments.of("LibraryV3.cd", "LibraryV2.cd", false),
        Arguments.of("LibraryV4.cd", "LibraryV3.cd", true),
        Arguments.of("LibraryV5.cd", "LibraryV4.cd", false));
  }

  public static Stream<Arguments> cd4analysisSet() {
    return Stream.of(
        Arguments.of("ManagementV2.cd", "ManagementV1.cd", false),
        Arguments.of("MyCompanyV2.cd", "MyCompanyV1.cd", false),
        Arguments.of("MyExampleV2.cd", "MyExampleV1.cd", false),
        Arguments.of("MyLifeV2.cd", "MyLifeV1.cd", true),
        Arguments.of("TeachingV2.cd", "TeachingV1.cd", true));
  }

  public static Stream<Arguments> completeSet() {
    return Stream.of(
        Arguments.of("Performance/5A.cd"),
        Arguments.of("Performance/5B.cd"),
        Arguments.of("Performance/10A.cd"),
        Arguments.of("Performance/10B.cd"),
        Arguments.of("Performance/15A.cd"),
        Arguments.of("Performance/15B.cd"),
        Arguments.of("Performance/20A.cd"),
        Arguments.of("Performance/20B.cd"),
        Arguments.of("Performance/25A.cd"),
        Arguments.of("Performance/25B.cd"),
        Arguments.of("cddiff/DEv1.cd"),
        Arguments.of("cddiff/DEv2.cd"),
        Arguments.of("cddiff/EAv1.cd"),
        Arguments.of("cddiff/EAv2.cd"),
        Arguments.of("cddiff/EMTv1.cd"),
        Arguments.of("cddiff/EMTv2.cd"),
        Arguments.of("cddiff/LibraryV1.cd"),
        Arguments.of("cddiff/LibraryV2.cd"),
        Arguments.of("cddiff/LibraryV3.cd"),
        Arguments.of("cddiff/LibraryV4.cd"),
        Arguments.of("cddiff/LibraryV5.cd"),
        Arguments.of("cd4analysis/ManagementV1.cd"),
        Arguments.of("cd4analysis/ManagementV2.cd"),
        Arguments.of("cd4analysis/MyCompanyV1.cd"),
        Arguments.of("cd4analysis/MyCompanyV2.cd"),
        Arguments.of("cd4analysis/MyExampleV1.cd"),
        Arguments.of("cd4analysis/MyExampleV2.cd"),
        Arguments.of("cd4analysis/MyLifeV1.cd"),
        Arguments.of("cd4analysis/MyLifeV2.cd"),
        Arguments.of("cd4analysis/TeachingV1.cd"),
        Arguments.of("cd4analysis/TeachingV2.cd"));
  }
}
