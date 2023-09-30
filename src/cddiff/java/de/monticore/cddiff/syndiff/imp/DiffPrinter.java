package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiffPrinter extends CDPrintDiff{
  public StringBuilder outputSrc, outputTgt, outputAdded, outputDeleted, outputChanged, outputDiff;

  /**
   * Sets various string outputs for different elements and changes found in a syntax difference between two CDs.
   *
   * @param syntaxDiff The syntax difference between source and target CD.
   */
  public void setStrings(CDSyntaxDiff syntaxDiff) {
    List<Pair<Integer, String>> onlySrcCDSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyTgtCDSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyAddedSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyDeletedSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyChangedSort = new ArrayList<>();
    List<Pair<Integer, String>> diffSort = new ArrayList<>();

    String initialPrintAdd = System.lineSeparator() +
      "The following elements were added to " +
      syntaxDiff.getSrcCD().getCDDefinition().getName() +
      " while comparing it to " +
      syntaxDiff.getTgtCD().getCDDefinition().getName() +
      ":";
    String initialPrintDelete = System.lineSeparator() +
      "The following elements were deleted while comparing " +
      syntaxDiff.getSrcCD().getCDDefinition().getName() +
      " to " +
      syntaxDiff.getTgtCD().getCDDefinition().getName() +
      ":";
    String initialPrintChange = System.lineSeparator() +
      "The following elements were changed while comparing " +
      syntaxDiff.getSrcCD().getCDDefinition().getName() +
      " to " +
      syntaxDiff.getTgtCD().getCDDefinition().getName() +
      ":";
    String initialPrintDiff = System.lineSeparator() +
      "The following diffs were found while comparing " +
      syntaxDiff.getSrcCD().getCDDefinition().getName() +
      " to " +
      syntaxDiff.getTgtCD().getCDDefinition().getName() +
      ":";

    for(CDTypeDiff x : syntaxDiff.getChangedTypes()) {
      if(x.getBaseDiff().contains(DiffTypes.ADDED_ATTRIBUTE) || x.getBaseDiff().contains(DiffTypes.ADDED_CONSTANT)) {
        String tmp = x.printIfAddedAttr();
        onlyAddedSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
      }
      if(x.getBaseDiff().contains(DiffTypes.DELETED_ATTRIBUTE) || x.getBaseDiff().contains(DiffTypes.DELETED_CONSTANT)) {
        String tmp = x.printIfRemovedAttr();
        onlyDeletedSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
      }
      if(!x.getBaseDiff().isEmpty()) {
        onlySrcCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printSrcCD()));
        onlyTgtCDSort.add(new Pair<>(x.getTgtElem().get_SourcePositionStart().getLine(), x.printTgtCD()));
        diffSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printDiffType()));
        onlyChangedSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printChangedType()));
      }
    }

    for(CDAssocDiff x : syntaxDiff.getChangedAssocs()) {
      if(!x.getBaseDiff().isEmpty()) {
        onlySrcCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printDiffAssoc()));
        onlyTgtCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printDiffAssoc()));
        diffSort.add(new Pair<>(x.getTgtElem().get_SourcePositionStart().getLine(), x.printDiffAssoc()));
        onlyChangedSort.add(new Pair<>(x.getTgtElem().get_SourcePositionStart().getLine(), x.printDiffAssoc()));
      }
    }

    if (!syntaxDiff.getAddedClasses().isEmpty()) {
      for (ASTCDClass x : syntaxDiff.getAddedClasses()) {
        CDTypeDiff diff = new CDTypeDiff(x, x, syntaxDiff.getTgtCD());
        String tmp = diff.printAddedType() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        diffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!syntaxDiff.getDeletedClasses().isEmpty()) {
      for (ASTCDClass x : syntaxDiff.getDeletedClasses()) {
        CDTypeDiff diff = new CDTypeDiff(x, x, syntaxDiff.getTgtCD());
        String tmp = diff.printRemovedType() + RESET;
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        diffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!syntaxDiff.getAddedEnums().isEmpty()) {
      for (ASTCDEnum x : syntaxDiff.getAddedEnums()) {
        CDTypeDiff diff = new CDTypeDiff(x, x, syntaxDiff.getTgtCD());
        String tmp = diff.printAddedType() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        diffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!syntaxDiff.getDeletedEnums().isEmpty()) {
      for (ASTCDEnum x : syntaxDiff.getDeletedEnums()) {
        CDTypeDiff diff = new CDTypeDiff(x, x, syntaxDiff.getTgtCD());
        String tmp = diff.printRemovedType() + RESET;
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        diffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!syntaxDiff.getAddedAssocs().isEmpty()) {
      for (ASTCDAssociation x : syntaxDiff.getAddedAssocs()) {
        CDAssocDiff diff = new CDAssocDiff(x, x, syntaxDiff.getSrcCD(), syntaxDiff.getSrcCD());
        String tmp = diff.printAddedAssoc() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        diffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!syntaxDiff.getDeletedAssocs().isEmpty()) {
      for (ASTCDAssociation x : syntaxDiff.getDeletedAssocs()) {
        CDAssocDiff diff = new CDAssocDiff(x, x, syntaxDiff.getTgtCD(), syntaxDiff.getTgtCD());
        String tmp = diff.printDeletedAssoc() + RESET;
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        diffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    //--print src
    onlySrcCDSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outputOnlySrc = new StringBuilder();
    outputOnlySrc.append("classdiagram ").append(syntaxDiff.getSrcCD().getCDDefinition().getName()).append(" {");
    for (Pair<Integer, String> x : onlySrcCDSort) {
      outputOnlySrc.append(System.lineSeparator()).append(x.b);
    }
    outputOnlySrc.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    this.outputSrc = outputOnlySrc;

    //--print tgt
    onlyTgtCDSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outputOnlyTgt = new StringBuilder();
    outputOnlyTgt.append("classdiagram ").append(syntaxDiff.getTgtCD().getCDDefinition().getName()).append(" {");
    for (Pair<Integer, String> x : onlyTgtCDSort) {
      outputOnlyTgt.append(System.lineSeparator()).append(x.b);
    }
    outputOnlyTgt.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    this.outputTgt = outputOnlyTgt;

    //--print added
    onlyAddedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyAdded = new StringBuilder();
    outPutOnlyAdded.append(initialPrintAdd);
    for (Pair<Integer, String> x : onlyAddedSort) {
      outPutOnlyAdded.append(System.lineSeparator()).append(System.lineSeparator()).append(x.b);
    }
    this.outputAdded = outPutOnlyAdded;

    //--print deleted
    onlyDeletedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyDeleted = new StringBuilder();
    outPutOnlyDeleted.append(initialPrintDelete);
    for (Pair<Integer, String> x : onlyDeletedSort) {
      outPutOnlyDeleted.append(System.lineSeparator()).append(System.lineSeparator()).append(x.b);
    }
    this.outputDeleted = outPutOnlyDeleted;

    //--print changed
    onlyChangedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyChanged = new StringBuilder();
    outPutOnlyChanged.append(initialPrintChange);
    for (Pair<Integer, String> x : onlyChangedSort) {
      outPutOnlyChanged.append(System.lineSeparator()).append(System.lineSeparator()).append(x.b);
    }
    this.outputChanged = outPutOnlyChanged;

    //--print diff
    diffSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutDiffString = new StringBuilder();
    outPutDiffString.append(initialPrintDiff).append(System.lineSeparator());
    for (Pair<Integer, String> x : diffSort) {
      outPutDiffString.append(System.lineSeparator()).append(x.b);
    }
    this.outputDiff = outPutDiffString;
  }

}
