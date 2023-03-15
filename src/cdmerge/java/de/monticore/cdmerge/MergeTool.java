/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.exceptions.ConfigurationException;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.CDMatcher;
import de.monticore.cdmerge.matching.DefaultMatchStrategyFactory;
import de.monticore.cdmerge.matching.MatchStrategyFactory;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.merging.CDMerger;
import de.monticore.cdmerge.merging.DefaultCDMergeStrategyFactory;
import de.monticore.cdmerge.merging.MergeStrategyFactory;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import de.monticore.cdmerge.merging.mergeresult.MergeStepResult;
import de.monticore.cdmerge.refactor.PostMergeRefactoring;
import de.monticore.cdmerge.util.CDMergeAfterParseTrafo;
import de.monticore.cdmerge.util.CDMergeInheritanceHelper;
import de.monticore.cdmerge.util.CDUtils;
import de.monticore.cdmerge.validation.CDMergeCD4ACoCos;
import de.monticore.cdmerge.validation.PostMergeValidation;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/** Merges a list of class diagrams according to a provided configuration and returns the results */
public class MergeTool {

  // The globally used black board used in each Merging Process
  private MergeBlackBoard mergeBlackBoard;

  // Preconfigured Matching and Merging Strategies
  private CDMatcher cdMatcher;

  private CDMerger cdMerger;

  private PostMergeValidation postMergeValidation;

  private PostMergeRefactoring postMergeRefactoring;

  /**
   * Constructor. The CDMerger is configured with a set of Parameters and corresponding values in
   *
   * @see {@link CDMergeConfig}. It will utilize the default matching and merging strategies
   */
  public MergeTool(CDMergeConfig config) {
    this(config, new DefaultMatchStrategyFactory(), new DefaultCDMergeStrategyFactory());
  }

  /**
   * Constructor. The CDMerger is configured with a set of Parameters and corresponding values in
   *
   * @see {@link CDMergeConfig}. It will utilize the provided factories to create the matching and
   *     merging strategies
   * @throws MergingException
   */
  public MergeTool(
      CDMergeConfig config,
      MatchStrategyFactory matcherFactory,
      MergeStrategyFactory mergeFactory) {
    this.mergeBlackBoard = new MergeBlackBoard(config);
    this.cdMatcher = matcherFactory.createCDMatcher(this.mergeBlackBoard);
    this.cdMerger = mergeFactory.createCDMerger(this.mergeBlackBoard);

    this.postMergeRefactoring = new PostMergeRefactoring(mergeBlackBoard);
    config.getModelRefactorings().forEach(r -> postMergeRefactoring.addRefactoring(r));

    this.postMergeValidation = new PostMergeValidation(mergeBlackBoard);
    config.getModelValidators().forEach(v -> postMergeValidation.addValidator(v));
  }

  private void mergeImports(
      MergeBlackBoard mergeBlackBoard, ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2) {
    Map<String, ASTMCImportStatement> newImports = new HashMap<>();
    for (ASTMCImportStatement impStatement : cd1.getMCImportStatementList()) {
      newImports.put(impStatement.getQName(), impStatement);
    }
    for (ASTMCImportStatement impStatement : cd2.getMCImportStatementList()) {
      newImports.put(impStatement.getQName(), impStatement);
    }

    mergeBlackBoard.setImportStatementList(new ArrayList<>(newImports.values()));
  }

  private void setPackage(
      MergeBlackBoard mergeBlackBoard, ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2) {
    if (!getConfig().getTargetPackage().isEmpty()) {
      List<String> _package = Arrays.asList(getConfig().getTargetPackage().split("\\."));
      mergeBlackBoard.setPackageList(_package);
    } else {
      if (cd1.isPresentMCPackageDeclaration() && cd2.isPresentMCPackageDeclaration()) {
        // We have to cut out the CD Name as last entry of the package
        if (cd1.sizePackage() > 0 && cd2.sizePackage() > 0) {
          if (cd1.getCDPackageList().equals(cd1.getCDPackageList())) {
            mergeBlackBoard.addLog(
                ErrorLevel.FINE,
                "Both class diagram have same package declaration '"
                    + CDUtils.prettyPrintInline(cd1.getMCPackageDeclaration())
                    + "', will use this package for  merged CD.",
                MergePhase.CD_MERGING);
            mergeBlackBoard.setPackageList(cd1.getCDPackageList());
          } else {
            mergeBlackBoard.addLog(
                ErrorLevel.WARNING,
                "Different  package declarations specified in input class diagrams - merged class"
                    + " diagram will not contain a package information!",
                MergePhase.CD_MERGING);
          }
        }

      } else {
        mergeBlackBoard.addLog(
            ErrorLevel.WARNING,
            "No target package specified and neither of the input CDs have a package declaration "
                + "- merged class diagram will not contain a package information!",
            MergePhase.PREPARING);
      }
    }
  }

  /**
   * Merges all classdiagrams which are either specified explicitly as input models or are resolved
   * on the provided model path
   *
   * @return the @see {@link MergeStepResult} contains the lists of all merged cds and their
   *     corresponding execution log. The final result is the last element of the list.
   */
  public MergeResult mergeCDs() throws MergingException {
    List<ASTCDCompilationUnit> cds = getConfig().getInputCDs();
    Preconditions.checkElementIndex(
        1,
        cds.size(),
        "Insufficient number of class diagrams, at least two class diagrams are necessary to "
            + "execute merge.");

    return mergeCDs(getConfig().getInputCDs());
  }

  /**
   * Merges all classdiagrams which are provided as CD4Code AST instances
   *
   * @return the @see {@link MergeStepResult} containing the lists of all merged cds and their
   *     corresponding execution log. The final result is the last element of the list.
   */
  public MergeResult mergeCDs(List<ASTCDCompilationUnit> cds) throws MergingException {
    Preconditions.checkElementIndex(
        1,
        cds.size(),
        "Insuficient number of input models: at least two class diagramms must be provided");

    Iterator<ASTCDCompilationUnit> cdIterator = cds.iterator();
    MergeResult mergeResult = new MergeResult();
    MergeStepResult mergeStepResult;
    int step = 0;
    ASTCDCompilationUnit cd1 = cdIterator.next();
    ASTCDCompilationUnit cd2;
    while (cdIterator.hasNext()) {
      step++;
      cd2 = cdIterator.next();
      String intermediateName =
          "Merge_"
              + step
              + "_"
              + cd1.getCDDefinition().getName()
              + "_"
              + cd2.getCDDefinition().getName();

      mergeStepResult = mergeCDs(cd1, cd2, Optional.of(intermediateName));

      // Write output to file if necessary
      if (getConfig().printIntermediateToFile()) {
        try {
          FileUtils.writeStringToFile(
              new File(Paths.get(getConfig().getOutputPath(), intermediateName + ".cd").toString()),
              CDUtils.prettyPrint(mergeStepResult.getMergedCD()));
        } catch (IOException e) {
          mergeBlackBoard.addLog(
              ErrorLevel.ERROR,
              "Unable to write intermediate CDs to file '"
                  + Paths.get(getConfig().getOutputPath(), intermediateName + ".cd").toString()
                  + "' "
                  + e.getMessage(),
              MergePhase.NONE);
          mergeBlackBoard.addLog(e, MergePhase.NONE);
        }
      }
      // nextCD1 is the result of previous merge
      cd1 = mergeStepResult.getMergedCD();
      mergeResult.add(mergeStepResult);
      if (!mergeStepResult.isSuccessful()) {
        throw new MergingException(
            "Errors during merge step "
                + step
                + ". Will cancel further processing here. Check log for details",
            mergeStepResult);
      }
    }

    // Last Merge set correct output CD Name
    mergeResult.getMergedCD().get().getCDDefinition().setName(getConfig().getOutputName());

    if (cds.size() > 2 && getConfig().assertAssociativity()) {
      if (!assertAssociativityForInputModels(getConfig().getInputCDs())) {
        throw new MergingException(
            "Input CDs are NOT associative with configured merge parameters and strategies: "
                + "Different merge results for different merge sequences!",
            mergeBlackBoard.getExecutionLog());
      }
    }

    return mergeResult;
  }

  /**
   * This methods checks all permutations of all input cds. This is the simplest and most secure
   * method, though a little brute force. Despite only associations can cause problems, one needs
   * always the corresponding merged class diagram to validate the associations
   */
  private boolean assertAssociativityForInputModels(ImmutableList<ASTCDCompilationUnit> inputCDs) {

    if (inputCDs.size() < 3) {
      mergeBlackBoard.addLog(
          ErrorLevel.INFO,
          "Less than three input diagrams are always associative",
          MergePhase.VALIDATION);
      return true;
    }

    List<List<ASTCDCompilationUnit>> allMergeSequences =
        generatePermutations(new ArrayList<>(inputCDs));
    Iterator<ASTCDCompilationUnit> cdIterator;
    ASTCDCompilationUnit cd1, cd2;
    MergeStepResult result;
    List<ASTCDCompilationUnit> results = new ArrayList<>(allMergeSequences.size());
    int errorCount = 0;
    int i;
    final StringBuilder sb = new StringBuilder();
    for (List<ASTCDCompilationUnit> sequence : allMergeSequences) {
      sb.setLength(0);
      sb.append("Testing merge sequence: ");
      sequence.forEach(e -> sb.append(e.getCDDefinition().getName() + " "));
      mergeBlackBoard.addLog(ErrorLevel.INFO, sb.toString(), MergePhase.VALIDATION);
      try {
        i = 0;
        cdIterator = sequence.iterator();
        cd1 = cdIterator.next();
        while (cdIterator.hasNext()) {
          i++;
          cd2 = cdIterator.next();

          result = mergeCDs(cd1, cd2, Optional.of("Tmp_" + i));
          cd1 = result.getMergedCD();
        }
        results.add(cd1);
      } catch (Exception e) {
        if (e.getCause() != null) {
          mergeBlackBoard.addLog(
              ErrorLevel.WARNING,
              "Sequence produced Error during merge: " + e.getCause().getMessage(),
              MergePhase.VALIDATION);
        } else {
          mergeBlackBoard.addLog(
              ErrorLevel.WARNING,
              "Sequence produced Error during merge: " + e.getMessage(),
              MergePhase.VALIDATION);
        }

        errorCount++;
      }
    }
    // every constellation produces an error
    if (errorCount == allMergeSequences.size()) {
      mergeBlackBoard.addLog(
          ErrorLevel.WARNING,
          "All input Diagrams cause a merging exception in all possible sequences. ",
          MergePhase.VALIDATION);
      return false;
    } else if (errorCount > 0) {
      mergeBlackBoard.addLog(
          ErrorLevel.ERROR,
          "Different merge results for different merge sequences, at least one sequence caused an"
              + " error while others work",
          MergePhase.PREPARING);
      return false;
    }

    Optional<ASTCDCompilationUnit> cd1Opt, cd2Opt;
    for (int j = 0; j < results.size() - 1; j++) {

      try {
        CD4CodeParser parser = new CD4CodeParser();
        cd1Opt = parser.parse_String(CDUtils.prettyPrint(results.get(j)));
        cd2Opt = parser.parse_String(CDUtils.prettyPrint(results.get(j + 1)));
        if (cd1Opt.isPresent() && cd2Opt.isPresent()) {
          if (!cd1Opt.get().deepEquals(cd2Opt.get(), false)) {
            // Different Result for different order
            mergeBlackBoard.addLog(
                ErrorLevel.ERROR,
                "Different merge results for different merge sequence",
                MergePhase.PREPARING,
                cd1Opt.get(),
                cd2Opt.get());
            return false;
          }
        } else if ((cd1Opt.isEmpty() && cd2Opt.isPresent())
            || (cd1Opt.isPresent() && cd2Opt.isEmpty())) {
          mergeBlackBoard.addLog(
              ErrorLevel.ERROR,
              "Different merge results (one empty) for different merge sequence",
              MergePhase.PREPARING,
              cd1Opt.get(),
              cd2Opt.get());
          return false;
        }
      } catch (IOException e) {
        mergeBlackBoard.addLog(
            ErrorLevel.ERROR,
            "IOExcpetion while parsing the merged cds: " + e.getMessage(),
            MergePhase.PREPARING);
        return false;
      }
    }
    return true;
  }

  private <E> List<List<E>> generatePermutations(List<E> original) {
    if (original.isEmpty()) {
      List<List<E>> result = new ArrayList<>();
      result.add(new ArrayList<>());
      return result;
    }
    E firstElement = original.remove(0);
    List<List<E>> returnValue = new ArrayList<>();
    List<List<E>> permutations = generatePermutations(original);
    for (List<E> smallerPermutated : permutations) {
      for (int index = 0; index <= smallerPermutated.size(); index++) {
        List<E> temp = new ArrayList<>(smallerPermutated);
        temp.add(index, firstElement);
        returnValue.add(temp);
      }
    }
    return returnValue;
  }

  /**
   * Merges two classdiagrams which are provided as CD4Code AST instances
   *
   * @return the @see {@link MergeStepResult} containing the merged cd and the execution log
   * @throws ConfigurationException
   */
  public MergeStepResult mergeCDs(
      ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2, Optional<String> mergedName)
      throws MergingException {

    List<ASTCDDefinition> inputCds = new ArrayList<>();
    inputCds.add(cd1.getCDDefinition());
    inputCds.add(cd2.getCDDefinition());

    mergeBlackBoard.initOrReset(cd1, cd2, mergedName);

    mergeBlackBoard.addLog(
        ErrorLevel.INFO, "Starting to merge CDs '", MergePhase.PREPARING, cd1, cd2);

    mergeBlackBoard.addLog(
        ErrorLevel.FINE, "Building match graph for CDs '", MergePhase.MATCHING, cd1, cd2);

    CDMatch matchResult = this.cdMatcher.createCDMatch(inputCds);

    mergeBlackBoard.addLog(ErrorLevel.FINE, "Merging CD Imports", MergePhase.CD_MERGING, cd1, cd2);
    mergeImports(mergeBlackBoard, cd2, cd1);

    mergeBlackBoard.addLog(ErrorLevel.FINE, "Merging CD Packages", MergePhase.CD_MERGING, cd1, cd2);
    setPackage(mergeBlackBoard, cd1, cd2);

    // Perfom the Merge of the CD Definitions
    cdMerger.mergeCDs(cd1.getCDDefinition(), cd2.getCDDefinition(), matchResult);

    mergeBlackBoard.addLog(ErrorLevel.INFO, "Merging completed.", MergePhase.CD_MERGING, cd1, cd2);

    Optional<ASTCDCompilationUnit> mergeResult = mergeBlackBoard.getCleanMergedCD();
    if (mergeResult.isPresent()) {
      mergeBlackBoard.addLog(
          ErrorLevel.FINE,
          "Merged Class Diagramm:\n\n= = = = = = =\n"
              + CDUtils.prettyPrint(mergeResult.get())
              + "\n= = = = = = =\n",
          MergePhase.CD_MERGING);
    } else {
      mergeBlackBoard.addLog(
          ErrorLevel.ERROR,
          "Unable to get clean merged CD, not able to parse AST of merged CD",
          MergePhase.CD_MERGING);
      return mergeBlackBoard.finalizeMerge(false);
    }

    ASTCDCompilationUnit mergedCD =
        CDMergeInheritanceHelper.mergeRedundantAttributes(
            mergeResult.get(), getConfig().allowPrimitiveTypeConversion());

    // Set the final name
    mergedCD.getCDDefinition().setName(mergedName.orElseGet(() -> getConfig().getOutputName()));

    // First see if already some severe errors occurred during the merging
    // process
    try {
      checkForErrors();
      mergeBlackBoard.addLog(
          ErrorLevel.INFO,
          "Merging completed wihtout errors, performing additional checks now",
          MergePhase.FINALIZING,
          cd1,
          cd2);
    } catch (MergingException e) {
      mergeBlackBoard.addLog(e);
      return mergeBlackBoard.finalizeMerge(false);
    }

    // Execute Post Merge Refactorings
    if (!getConfig().disabledModelRefactorings()) {
      postMergeRefactoring(mergedCD);
    }

    if (!getConfig().disabledPostMergeValidation()) {
      mergeBlackBoard.addLog(
          ErrorLevel.FINE, "Validating merged result.", MergePhase.VALIDATION, cd1, cd2);
      postMergeValidate(mergedCD);
    }

    if (!(getConfig().disabledCheckCoCo() || getConfig().disabledPostMergeValidation())) {
      // Initialize Symboltable
      CDUtils.RefreshSymbolTable(mergedCD);
      checkCoCos(mergedCD);
    }

    // The CoCos will also report to the error log, therefore we check the
    // logs again for errors after executing CoCos
    try {
      checkForErrors();
      mergeBlackBoard.addLog(
          ErrorLevel.INFO, "Post Merge Checks passed", MergePhase.FINALIZING, cd1, cd2);
    } catch (MergingException e) {
      mergeBlackBoard.addLog(e);
      return mergeBlackBoard.finalizeMerge(false);
    }

    mergeBlackBoard.addLog(
        ErrorLevel.INFO, "Merge completed succesfully", MergePhase.FINALIZING, cd1, cd2);

    return mergeBlackBoard.finalizeMerge(mergedCD, true);
  }

  private void postMergeRefactoring(ASTCDCompilationUnit mergedCD) {
    mergeBlackBoard.addLog(
        ErrorLevel.FINE,
        "Cleaning up and refactoring merged result.",
        MergePhase.MODEL_REFACTORING);

    this.postMergeRefactoring.execute(mergedCD);

    new CDMergeAfterParseTrafo().transform(mergedCD);
    // TODO this is likley not needed. Maybe Provide a paramater if default CD4A TraFos should be
    // applied on merged CD
    // CD4CodeMill.globalScope().clear();
    // CD4CodeMill.scopesGenitorDelegator().createFromAST(mergedCD);
    // new CD4CodeTrafo4Defaults().transform(mergedCD);
    //  mergedCD.accept(new CD4CodeSymbolTableCompleter(mergedCD).getTraverser());

    mergeBlackBoard.addLog(ErrorLevel.FINE, "Refactoring completed.", MergePhase.MODEL_REFACTORING);
  }

  /** Executes some integrity checks on the merged model */
  private void postMergeValidate(ASTCDCompilationUnit mergedCD) {
    mergeBlackBoard.addLog(
        ErrorLevel.FINE, "Performing post merge model validation.", MergePhase.MODEL_REFACTORING);
    this.postMergeValidation.execute(mergedCD.getCDDefinition());
    mergeBlackBoard.addLog(
        ErrorLevel.FINE, "Finished post merge model validation.", MergePhase.MODEL_REFACTORING);
  }

  private void checkForErrors() throws MergingException {
    ErrorLevel errors = mergeBlackBoard.getMaxErrorLevel();
    String errorMsg =
        "Errors occured during merging: Class diagrams could not be merged into a "
            + "sound class diagram.";
    String warningMsg =
        "Warnings occurred  occured during merging:  Merged class diagram is "
            + "sound but could possibly misbehave when used in other Tools. If you would still like "
            + "to use it, uncheck the strict-flag and execute the program once again.";
    if (errors == ErrorLevel.ERROR) {
      throw new MergingException(errorMsg);
    } else if ((errors == ErrorLevel.WARNING) && getConfig().cancelOnWarnings()) {
      throw new MergingException(warningMsg);
    }
  }

  private void checkCoCos(ASTCDCompilationUnit cd) {
    mergeBlackBoard.addLog(
        ErrorLevel.FINE,
        "Checking CD4Code context conditions for merged class diagram.",
        MergePhase.VALIDATION);
    // Ensure that the CoCos won't terminate the program
    boolean failQuick = Log.isFailQuickEnabled();
    Log.enableFailQuick(false);

    long findingCount = Log.getFindingsCount();
    // FIXME Workaround for faulty CoCos CD4CodeCoCoChecker checker = new
    // CD4CodeCoCosDelegator().getCheckerForAllCoCos();
    // We should check all default cocos here as this is the merge result
    CD4CodeCoCoChecker checker = new CDMergeCD4ACoCos().getCheckerForMergedCDs();

    // check for errors and register them in our log
    try {
      checker.checkAll(cd);
    } catch (Throwable e) {
      mergeBlackBoard.addLog(
          ErrorLevel.ERROR, "CoCo-Error: " + e.getMessage(), MergePhase.VALIDATION, cd);
    }

    List<Finding> logs = Log.getFindings().stream().skip(findingCount).collect(Collectors.toList());

    for (Finding found : logs) {
      if (found.isError()) {
        mergeBlackBoard.addLog(
            ErrorLevel.ERROR, "CoCo-Error: " + found.getMsg(), MergePhase.VALIDATION, cd);
      } else if (found.isWarning()) {
        mergeBlackBoard.addLog(
            ErrorLevel.WARNING, "CoCo-Warning: " + found.getMsg(), MergePhase.VALIDATION, cd);
      }
    }
    mergeBlackBoard.addLog(
        ErrorLevel.FINE, "Context conditions check completed.", MergePhase.VALIDATION);
    Log.enableFailQuick(failQuick);
  }

  public CDMergeConfig getConfig() {
    return this.mergeBlackBoard.getConfig();
  }
}
