/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.mergeresult;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.exceptions.ConfigurationException;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.ExecutionLog;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.util.ASTCDHelper;
import de.monticore.cdmerge.util.CDUtils;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclarationBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A Blackboard like Object serving as central configuration and data exchange point during the
 * merge process. It accumulates the result and log entries when merging two class diagrams and
 * provides some helper for accessing the model elements of a class diagram (i.e. ASTNodes)
 */
public class MergeBlackBoard {

  private final CDMergeConfig config;

  private List<ASTCDCompilationUnit> originalInputCDs;

  private List<ASTCDCompilationUnit> currentCDs;

  private List<ASTCDHelper> currentCDHelpers;

  private ASTCDHelper currentMergedCDHelper;

  private ExecutionLog currentExecutionLog;

  private final List<MergeStepResult> mergeStepResults;

  /**
   * This stores the mergedCD. The class diagram is constructed iteratively during the merge process
   * by different strategies
   */
  private ASTCDCompilationUnit mergedCD;

  /** True if no changes to the mergedCD did happen before refreshing the clone */
  private boolean mergedCDupdated = false;

  private boolean mergeFinished = false;

  public MergeBlackBoard(CDMergeConfig config) {
    this.config = config;
    if (!config.noInputModels()) {
      this.originalInputCDs = new ArrayList<ASTCDCompilationUnit>(config.getInputCDs());
    }
    this.currentExecutionLog =
        new ExecutionLog(
            config.getMinimalLogable(),
            config.isFailFast(),
            config.cancelOnWarnings(),
            config.isTraceEnabled());
    this.mergeStepResults = new ArrayList<MergeStepResult>();
  }

  public CDMergeConfig getConfig() {
    return this.config;
  }

  public MergeStepResult finalizeMerge(ASTCDCompilationUnit finalCd, boolean successful) {
    addLog(ErrorLevel.INFO, "Finalizing Merge Process", MergePhase.FINALIZING);
    this.mergeFinished = true;
    this.mergedCD = finalCd;
    Optional<ASTCDCompilationUnit> cleanMergedCd = getCleanMergedCD();
    if (cleanMergedCd.isPresent()) {
      this.mergedCD = cleanMergedCd.get();
      this.mergeStepResults.add(
          new MergeStepResult(
              getCurrentInputCd1(),
              getCurrentInputCd2(),
              cleanMergedCd.get(),
              this.currentExecutionLog,
              successful));
      return this.mergeStepResults.get(mergeStepResults.size() - 1);
    } else {
      return new MergeStepResult(
          getCurrentInputCd1(),
          getCurrentInputCd2(),
          this.mergedCD,
          this.currentExecutionLog,
          false);
    }
  }

  /**
   * Prepares the merging of the next two class diagrams by resetting the current state
   *
   * @return resulting CD of the respective merge step
   */
  public MergeStepResult finalizeMerge(boolean successful) {
    return finalizeMerge(this.mergedCD, successful);
  }

  /** Must be called prior to each merging process */
  public void initOrReset(
      ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2, Optional<String> mergedCDName) {
    // final ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    // globalScope.clear();
    // CD4CodeScopesGenitorDelegator scopesGenitorDelegator = CD4CodeMill
    // .scopesGenitorDelegator();

    // scopesGenitorDelegator.createFromAST(cd1);
    // cd1.accept(new CD4CodeSymbolTableCompleter(cd1).getTraverser());
    // scopesGenitorDelegator.createFromAST(cd2);
    // cd2.accept(new CD4CodeSymbolTableCompleter(cd2).getTraverser());

    this.currentCDHelpers = new ArrayList<ASTCDHelper>();
    this.currentCDHelpers.add(new ASTCDHelper(cd1));
    this.currentCDHelpers.add(new ASTCDHelper(cd2));

    this.currentCDs = new ArrayList<ASTCDCompilationUnit>();
    this.currentCDs.add(cd1);
    this.currentCDs.add(cd2);

    this.currentExecutionLog =
        new ExecutionLog(
            config.getMinimalLogable(),
            config.isFailFast(),
            config.cancelOnWarnings(),
            config.isTraceEnabled());

    String name1 =
        cd1.getCDDefinition().getName().isEmpty() ? cd1.getCDDefinition().getName() : "CD1";
    String name2 =
        cd2.getCDDefinition().getName().isEmpty() ? cd2.getCDDefinition().getName() : "CD2";
    String resultName =
        mergedCDName.isPresent() && !mergedCDName.get().isEmpty()
            ? mergedCDName.get()
            : name1 + "_" + name2;

    ASTCDDefinition emptyCDDef =
        CD4CodeMill.cDDefinitionBuilder()
            .setName(resultName)
            .setModifier(new ASTModifierBuilder().build())
            .build();
    this.mergedCD = CD4CodeMill.cDCompilationUnitBuilder().setCDDefinition(emptyCDDef).build();

    this.mergedCDupdated = false;
    this.mergeFinished = false;
  }

  public ASTCDHelper getASTCDHelperInputCD1() {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    return this.currentCDHelpers.get(0);
  }

  public ASTCDHelper getASTCDHelperInputCD2() {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    return this.currentCDHelpers.get(1);
  }

  public ASTCDCompilationUnit getIntermediateMergedCD() {

    return this.mergedCD;
  }

  public Optional<ASTCDCompilationUnit> getCleanMergedCD() {

    Optional<ASTCDCompilationUnit> mergedAst = Optional.empty();

    try {
      // We parse from pretty print again to ensure a clean ast with all default refactorings

      mergedAst = CDUtils.parseCDCompilationUnit(CDUtils.prettyPrint(mergedCD), false);

      if (mergedAst.isPresent()) {
        return mergedAst;
      }
      String errors = "\n";

      for (Finding found : Log.getFindings()) {
        if (found.isError()) {
          errors += found.getMsg() + "\n";
        }
      }

      throw new MergingException(
          "Unable to Parse merged AST, Empty parse result" + errors, mergedCD);
    } catch (RuntimeException rtEx) {
      // Runtime Exceptions originate mostly from MontiCore
      addLog(new MergingException("Issues in merged CD: " + rtEx.getMessage(), mergedCD));
    } catch (MergingException e) {
      addLog(e);
    }
    return Optional.empty();
  }

  public ASTCDHelper getASTCDHelperMergedCD() {
    // The merged CD my be changed during the merging process, therefore we
    // check if we got the most recent version but still avoid unnecessary
    // AST traversals
    if (mergedCDupdated) {
      try {
        this.currentMergedCDHelper = new ASTCDHelper(this.mergedCD);
      } catch (ConfigurationException e) {
        addLog(new MergingException(e.getMessage()));
      }
      this.mergedCDupdated = false;
    }
    return this.currentMergedCDHelper;
  }

  public ErrorLevel getMaxErrorLevel() {
    return this.currentExecutionLog.getMaxErrorLevel();
  }

  public ASTCDCompilationUnit getCurrentInputCd1() {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    if (this.currentCDs.size() != 2) {
      throw new IllegalStateException(
          "Unexpected number of current input models. Expexted 2 was " + this.currentCDs.size());
    }

    return this.currentCDs.get(0);
  }

  public ASTCDCompilationUnit getCurrentInputCd2() {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    if (this.currentCDs.size() != 2) {
      throw new IllegalStateException(
          "Unexpected number of current input models. Expexted 2 was " + this.currentCDs.size());
    }
    return this.currentCDs.get(1);
  }

  public List<ASTCDCompilationUnit> getOriginalInputCDs() {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    return this.originalInputCDs;
  }

  public ExecutionLog getExecutionLog() {
    return this.currentExecutionLog;
  }

  private void checkWriteAccess() {
    if (this.mergeFinished) {
      throw new IllegalStateException(
          "The merging process was already finished and the blackboard doesn't allow further "
              + "modifications");
    }
  }

  /** @see ExecutionLog */
  public void addLog(ErrorLevel level, String message, MergePhase phase) {
    // The Method in MergeExecutionLog accepts null
    addLog(level, message, phase, null, null);
  }

  public void addLog(MergingException e) {
    MergePhase phase = MergePhase.NONE;
    if (e.getPhase().isPresent()) {
      phase = e.getPhase().get();
    }
    if (e.getAstNode1().isPresent() && e.getAstNode2().isPresent()) {
      this.addLog(
          ErrorLevel.ERROR, e.getMessage(), phase, e.getAstNode1().get(), e.getAstNode2().get());
    } else if (e.getAstNode1().isPresent()) {
      this.addLog(ErrorLevel.ERROR, e.getMessage(), phase, e.getAstNode1().get());
    } else {
      this.addLog(ErrorLevel.ERROR, e.getMessage(), phase);
    }
  }

  public void addLog(Exception e, MergePhase phase) {
    this.addLog(ErrorLevel.ERROR, e.getMessage(), phase);
  }

  /** @see ExecutionLog */
  public void addLog(ErrorLevel level, String message, MergePhase phase, ASTNode node) {
    // The Method in MergeExecutionLog accepts null
    addLog(level, message, phase, node, null);
  }

  /** @see ExecutionLog */
  public void addLog(
      ErrorLevel level, String message, MergePhase phase, ASTNode node1, ASTNode node2) {
    this.currentExecutionLog.log(level, message, phase, node1, node2);
  }

  public void addMergedClass(Optional<ASTCDClass> astClass, Optional<String> cdPackageName) {
    if (astClass == null) {
      return;
    }
    if (!astClass.isPresent()) {
      return;
    }
    checkWriteAccess();
    this.mergedCDupdated = true;
    if (cdPackageName.isPresent()) {
      mergedCD.getCDDefinition().addCDElementToPackage(astClass.get(), cdPackageName.get());
      addLog(
          ErrorLevel.FINE,
          "Class '"
              + cdPackageName.get()
              + "."
              + astClass.get().getName()
              + "' added to specified package.",
          MergePhase.TYPE_MERGING,
          astClass.get());
    } else {
      mergedCD.getCDDefinition().getCDElementList().add(astClass.get());
      addLog(
          ErrorLevel.FINE,
          "Class '" + astClass.get().getName() + "' added to default Package.",
          MergePhase.TYPE_MERGING,
          astClass.get());
    }
  }

  public void addMergedAssociation(
      Optional<ASTCDAssociation> astAssociation, Optional<String> cdPackageName) {
    if (astAssociation == null) {
      return;
    }
    if (!astAssociation.isPresent()) {
      return;
    }
    checkWriteAccess();
    this.mergedCDupdated = true;

    if (cdPackageName.isPresent()) {
      mergedCD.getCDDefinition().addCDElementToPackage(astAssociation.get(), cdPackageName.get());
    } else {
      mergedCD.getCDDefinition().getCDElementList().add(astAssociation.get());
    }

    String logPname = "to default package.";
    if (cdPackageName.isPresent()) {
      logPname = " to package " + cdPackageName.get() + ".";
    }
    if (astAssociation.get().isPresentName()) {
      if (astAssociation.get().getCDAssocType().isComposition()) {
        addLog(
            ErrorLevel.FINE,
            "Composition '"
                + astAssociation.get().getName()
                + "' between '"
                + astAssociation.get().getLeftReferenceName().toString()
                + "' and '"
                + astAssociation.get().getRightReferenceName().toString()
                + "' added "
                + logPname,
            MergePhase.ASSOCIATION_MERGING,
            astAssociation.get());
      } else {
        addLog(
            ErrorLevel.FINE,
            "Association '"
                + astAssociation.get().getName()
                + "' between '"
                + astAssociation.get().getLeftReferenceName().toString()
                + "' and '"
                + astAssociation.get().getRightReferenceName().toString()
                + "' added "
                + logPname,
            MergePhase.ASSOCIATION_MERGING,
            astAssociation.get());
      }
    } else {
      if (astAssociation.get().getCDAssocType().isComposition()) {
        addLog(
            ErrorLevel.FINE,
            "Composition between '"
                + astAssociation.get().getLeftReferenceName().toString()
                + "' and '"
                + astAssociation.get().getRightReferenceName().toString()
                + "' added "
                + logPname,
            MergePhase.ASSOCIATION_MERGING,
            astAssociation.get());
      } else {
        addLog(
            ErrorLevel.FINE,
            "Association between '"
                + astAssociation.get().getLeftReferenceName().toString()
                + "' and '"
                + astAssociation.get().getRightReferenceName().toString()
                + "' added "
                + logPname,
            MergePhase.ASSOCIATION_MERGING,
            astAssociation.get());
      }
    }
  }

  public void addCDElementFromCD1(ASTCDElement element) {
    Optional<String> cdPackage = this.currentCDHelpers.get(0).getCDPackageName(element);
    addCDElement(element, cdPackage);
  }

  public void addCDElementFromCD2(ASTCDElement element) {
    Optional<String> cdPackage = this.currentCDHelpers.get(1).getCDPackageName(element);
    addCDElement(element, cdPackage);
  }

  public void addCDElement(ASTCDElement element, Optional<String> packageName) {
    if (element instanceof ASTCDClass) {
      addMergedClass(Optional.of((ASTCDClass) element), packageName);
    } else if (element instanceof ASTCDInterface) {
      addMergedInterface(Optional.of((ASTCDInterface) element), packageName);
    } else if (element instanceof ASTCDEnum) {
      addMergedEnum(Optional.of((ASTCDEnum) element), packageName);
    } else if (element instanceof ASTCDAssociation) {
      addMergedAssociation(Optional.of((ASTCDAssociation) element), packageName);
    } else if (element instanceof ASTCDPackage) {
      // IGNORE, Packages are added automatically with the corresponding CDElements
    } else {
      addLog(
          ErrorLevel.ERROR,
          "Unable to add unexpected CD Element: Unknown type",
          MergePhase.CD_MERGING,
          element);
    }
  }

  public void addMergedEnum(Optional<ASTCDEnum> astEnum, Optional<String> cdPackageName) {
    if (astEnum == null) {
      return;
    }
    if (!astEnum.isPresent()) {
      return;
    }
    checkWriteAccess();

    if (cdPackageName.isPresent()) {
      mergedCD.getCDDefinition().addCDElementToPackage(astEnum.get(), cdPackageName.get());
      addLog(
          ErrorLevel.FINE,
          "Enum '"
              + cdPackageName.get()
              + "."
              + astEnum.get().getName()
              + "' added to specified package.",
          MergePhase.TYPE_MERGING,
          astEnum.get());
    } else {
      mergedCD.getCDDefinition().getCDElementList().add(astEnum.get());
      addLog(
          ErrorLevel.FINE,
          "Enum '" + astEnum.get().getName() + "' added to default Package.",
          MergePhase.TYPE_MERGING,
          astEnum.get());
    }

    this.mergedCDupdated = true;
  }

  public void addMergedInterface(
      Optional<ASTCDInterface> astInterface, Optional<String> cdPackageName) {
    if (astInterface == null) {
      return;
    }
    checkWriteAccess();
    this.mergedCDupdated = true;
    if (cdPackageName.isPresent()) {
      mergedCD.getCDDefinition().addCDElementToPackage(astInterface.get(), cdPackageName.get());
      addLog(
          ErrorLevel.FINE,
          "Interface '"
              + cdPackageName.get()
              + "."
              + astInterface.get().getName()
              + "' added to specified package.",
          MergePhase.TYPE_MERGING,
          astInterface.get());
    } else {
      mergedCD.getCDDefinition().getCDElementList().add(astInterface.get());
      addLog(
          ErrorLevel.FINE,
          "Interface '" + astInterface.get().getName() + "' added to default package.",
          MergePhase.TYPE_MERGING,
          astInterface.get());
    }
  }

  public void setImportStatementList(List<ASTMCImportStatement> imports) {
    checkWriteAccess();
    this.mergedCDupdated = true;
    mergedCD.setMCImportStatementList(imports);
    if (imports.size() > 0) {
      addLog(
          ErrorLevel.FINE,
          "Imports added: "
              + String.join(
                  "; ",
                  imports.stream().map(element -> element.getQName()).collect(Collectors.toList())),
          MergePhase.CD_MERGING);
    }
  }

  public void setPackageList(List<String> packages) {
    checkWriteAccess();
    String packageFQN = packages.stream().map(s -> s.toString()).collect(Collectors.joining("."));
    this.mergedCDupdated = true;

    ASTMCPackageDeclaration pckg =
        new ASTMCPackageDeclarationBuilder()
            .setMCQualifiedName(new ASTMCQualifiedNameBuilder().setPartsList(packages).build())
            .build();
    mergedCD.setMCPackageDeclaration(pckg);
    if (packages.size() > 0) {
      addLog(
          ErrorLevel.FINE,
          "Package set for merged CD: '" + packageFQN + "'",
          MergePhase.CD_MERGING);
    }
  }

  public String getCurrentCD1Name() {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    return getCurrentInputCd1().getCDDefinition().getName();
  }

  public String getCurrentCD2Name() {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    return getCurrentInputCd2().getCDDefinition().getName();
  }

  public ASTCDCompilationUnit getOriginalInputCd(int index) {
    if (this.config.noInputModels()) {
      throw new IllegalStateException(
          "No access to import models: CD Merge was configured with no import models paramater. ");
    }
    return this.originalInputCDs.get(index);
  }

  public List<ASTCDDefinition> getCurrentCDs() {
    List<ASTCDDefinition> cds = new ArrayList<ASTCDDefinition>(2);
    this.currentCDs.forEach(c -> cds.add(c.getCDDefinition()));
    return cds;
  }

  public List<ASTCDHelper> getCurrentCDHelper() {
    return this.currentCDHelpers;
  }

  public List<MergeStepResult> getMergeResults() {
    return mergeStepResults;
  }
}
