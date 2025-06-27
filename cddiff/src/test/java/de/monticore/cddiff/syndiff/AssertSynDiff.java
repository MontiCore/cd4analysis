package de.monticore.cddiff.syndiff;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertSynDiff {
  CDSyntaxDiff diff;
  Map<String, Function<Integer, AssertSynDiff>> remainingFunctions = new HashMap<>();

  public AssertSynDiff(CDSyntaxDiff diff) {
    this.diff = diff;
    remainingFunctions.put("assertAddedClasses", this::assertAddedClasses);
    remainingFunctions.put("assertDeletedClasses", this::assertDeletedClasses);
    remainingFunctions.put("assertMatchedClasses", this::assertMatchedClasses);
    remainingFunctions.put("assertChangedTypes", this::assertChangedTypes);
    remainingFunctions.put("assertAddedEnums", this::assertAddedEnums);
    remainingFunctions.put("assertDeletedEnums", this::assertDeletedEnums);
    remainingFunctions.put("assertMatchedEnums", this::assertMatchedEnums);
    remainingFunctions.put("assertChangedAssocs", this::assertChangedAssocs);
    remainingFunctions.put("assertAddedAssocs", this::assertAddedAssocs);
    remainingFunctions.put("assertDeletedAssocs", this::assertDeletedAssocs);
    remainingFunctions.put("assertMatchedAssocs", this::assertMatchedAssocs);
    remainingFunctions.put("assertAddedInterfaces", this::assertAddedInterfaces);
    remainingFunctions.put("assertDeletedInterfaces", this::assertDeletedInterfaces);
    remainingFunctions.put("assertMatchedInterfaces", this::assertMatchedInterfaces);
  }

  public AssertSynDiff assertAddedClasses(int expected) {
    assertEquals(expected, diff.getAddedClasses().size(), "Expected " + expected + " added classes, but found " + diff.getAddedClasses().size());
    remainingFunctions.remove("assertAddedClasses");
    return this;
  }

  public AssertSynDiff assertDeletedClasses(int expected) {
    assertEquals(expected, diff.getDeletedClasses().size(), "Expected " + expected + " deleted classes, but found " + diff.getDeletedClasses().size());
    remainingFunctions.remove("assertDeletedClasses");
    return this;
  }

  public AssertSynDiff assertMatchedClasses(int expected) {
    assertEquals(expected, diff.getMatchedClasses().size(), "Expected " + expected + " matched classes, but found " + diff.getMatchedClasses().size());
    remainingFunctions.remove("assertMatchedClasses");
    return this;
  }

  public AssertSynDiff assertChangedTypes(int expected) {
    assertEquals(expected, diff.getChangedTypes().size(), "Expected " + expected + " changed types, but found " + diff.getChangedTypes().size());
    remainingFunctions.remove("assertChangedTypes");
    return this;
  }

  public AssertSynDiff assertAddedEnums(int expected) {
    assertEquals(expected, diff.getAddedEnums().size(), "Expected " + expected + " added enums, but found " + diff.getAddedEnums().size());
    remainingFunctions.remove("assertAddedEnums");
    return this;
  }

  public AssertSynDiff assertDeletedEnums(int expected) {
    assertEquals(expected, diff.getDeletedEnums().size(), "Expected " + expected + " deleted enums, but found " + diff.getDeletedEnums().size());
    remainingFunctions.remove("assertDeletedEnums");
    return this;
  }

  public AssertSynDiff assertMatchedEnums(int expected) {
    assertEquals(expected, diff.getMatchedEnums().size(), "Expected " + expected + " matched enums, but found " + diff.getMatchedEnums().size());
    remainingFunctions.remove("assertMatchedEnums");
    return this;
  }

  public AssertSynDiff assertChangedAssocs(int expected) {
    assertEquals(expected, diff.getChangedAssocs().size(), "Expected " + expected + " changed associations, but found " + diff.getChangedAssocs().size());
    remainingFunctions.remove("assertChangedAssocs");
    return this;
  }

  public AssertSynDiff assertAddedAssocs(int expected) {
    assertEquals(expected, diff.getAddedAssocs().size(), "Expected " + expected + " added associations, but found " + diff.getAddedAssocs().size());
    remainingFunctions.remove("assertAddedAssocs");
    return this;
  }

  public AssertSynDiff assertDeletedAssocs(int expected) {
    assertEquals(expected, diff.getDeletedAssocs().size(), "Expected " + expected + " deleted associations, but found " + diff.getDeletedAssocs().size());
    remainingFunctions.remove("assertDeletedAssocs");
    return this;
  }

  public AssertSynDiff assertMatchedAssocs(int expected) {
    assertEquals(expected, diff.getMatchedAssocs().size(), "Expected " + expected + " matched associations, but found " + diff.getMatchedAssocs().size());
    remainingFunctions.remove("assertMatchedAssocs");
    return this;
  }

  public AssertSynDiff assertAddedInterfaces(int expected) {
    assertEquals(expected, diff.getAddedInterfaces().size(), "Expected " + expected + " added interfaces, but found " + diff.getAddedInterfaces().size());
    remainingFunctions.remove("assertAddedInterfaces");
    return this;
  }

  public AssertSynDiff assertDeletedInterfaces(int expected) {
    assertEquals(expected, diff.getDeletedInterfaces().size(), "Expected " + expected + " deleted interfaces, but found " + diff.getDeletedInterfaces().size());
    remainingFunctions.remove("assertDeletedInterfaces");
    return this;
  }

  public AssertSynDiff assertMatchedInterfaces(int expected) {
    assertEquals(expected, diff.getMatchedInterfaces().size(), "Expected " + expected + " matched interfaces, but found " + diff.getMatchedInterfaces().size());
    remainingFunctions.remove("assertMatchedInterfaces");
    return this;
  }

  public void assertRemainingEmpty() {
    Collection<Function<Integer, AssertSynDiff>> functionsToCall = new LinkedList<>(remainingFunctions.values());
    functionsToCall.forEach(func -> func.apply(0));
  }
}
