/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.ASTCDHelper;

import java.util.List;

/**
 * Provides config and merge blackboard to all matchers and merging strategies
 */
public abstract class MatcherBase {

  private final MergeBlackBoard mergeBlackBoard;

  protected final MergePhase PHASE = MergePhase.MATCHING;

  public MatcherBase(MergeBlackBoard mergeBlackBoard) {
    this.mergeBlackBoard = mergeBlackBoard;
  }

  public MergeBlackBoard getBlackBoard() {
    return this.mergeBlackBoard;
  }

  protected CDMergeConfig getConfig() {
    return this.mergeBlackBoard.getConfig();
  }

  protected List<ASTCDDefinition> getCurrentCDs() {
    return this.mergeBlackBoard.getCurrentCDs();
  }

  protected List<ASTCDHelper> getCurrentCDHelper() {
    return this.mergeBlackBoard.getCurrentCDHelper();
  }

  protected ASTCDCompilationUnit getOriginalInputD(int index) {
    return this.mergeBlackBoard.getOriginalInputCd(index);
  }

  protected ASTCDDefinition getOriginalInputCDDefinition(int index) {
    return this.mergeBlackBoard.getOriginalInputCd(index).getCDDefinition();
  }

  protected void log(ErrorLevel level, String message) {
    this.mergeBlackBoard.addLog(level, message, PHASE);
  }

  protected void log(ErrorLevel level, String message, ASTNode astNode) {
    this.mergeBlackBoard.addLog(level, message, PHASE, astNode);
  }

  protected void log(ErrorLevel level, String message, ASTNode astNode1, ASTNode astNode2) {
    this.mergeBlackBoard.addLog(level, message, PHASE, astNode1, astNode2);
  }

  protected void logError(String message) {
    this.mergeBlackBoard.addLog(ErrorLevel.ERROR, message, PHASE);
  }

  protected void logError(String message, ASTNode astNode) {
    this.mergeBlackBoard.addLog(ErrorLevel.ERROR, message, PHASE, astNode);
  }

  protected void logError(String message, ASTNode astNode1, ASTNode astNode2) {
    this.mergeBlackBoard.addLog(ErrorLevel.ERROR, message, PHASE, astNode1, astNode2);
  }

  protected void logError(MergingException e) {
    this.mergeBlackBoard.addLog(e);
  }

  protected void logWarning(String message) {
    this.mergeBlackBoard.addLog(ErrorLevel.WARNING, message, PHASE);
  }

  protected void logWarning(String message, ASTNode astNode) {
    this.mergeBlackBoard.addLog(ErrorLevel.WARNING, message, PHASE, astNode);
  }

  protected void logWarning(String message, ASTNode astNode1, ASTNode astNode2) {
    this.mergeBlackBoard.addLog(ErrorLevel.WARNING, message, PHASE, astNode1, astNode2);
  }

}
