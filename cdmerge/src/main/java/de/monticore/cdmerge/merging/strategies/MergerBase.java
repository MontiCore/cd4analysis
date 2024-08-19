/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.Optional;

/**
 * Provides logging, config and access to merge blackboard to all matchers and merging strategies
 */
public abstract class MergerBase {

  private final MergeBlackBoard mergeBlackBoard;

  protected final MergePhase PHASE;

  public MergerBase(MergeBlackBoard mergeBlackBoard, MergePhase phase) {
    this.mergeBlackBoard = mergeBlackBoard;
    this.PHASE = phase;
  }

  public MergeBlackBoard getBlackBoard() {
    return this.mergeBlackBoard;
  }

  protected CDMergeConfig getConfig() {
    return this.mergeBlackBoard.getConfig();
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

  protected void mergeComments(ASTNode merged, ASTNode input1, ASTNode input2) {
    if (mergeBlackBoard.getConfig().mergeComments()) {
      // Clean up anything from possibly cloned AST Nodes
      merged.get_PostCommentList().clear();
      merged.get_PreCommentList().clear();
      // We always assume, that the comments are only prior to an element
      merged.addAll_PreComments(input1.get_PreCommentList());
      merged.addAll_PreComments(input2.get_PreCommentList());
    } else {
      // If we don't merge the comments, they make no sense at all
      merged.get_PreCommentList().clear();
      merged.get_PostCommentList().clear();
    }
  }

  protected Optional<ASTModifier> mergeModifier(ASTModifier modifier1, ASTModifier modifier2) {
    ASTModifier modifier = CD4CodeMill.modifierBuilder().build();

    // This should not happen in regular cases as default match ignores local
    // CDElements
    modifier.setLocal(modifier1.isLocal() && modifier2.isLocal());

    modifier.setDerived(modifier1.isDerived() || modifier2.isDerived());

    modifier.setAbstract(modifier1.isAbstract() && modifier2.isAbstract());
    modifier.setReadonly(modifier1.isReadonly() && modifier2.isReadonly());
    modifier.setPrivate(modifier1.isPrivate() && modifier2.isPrivate());
    modifier.setPublic(modifier1.isPublic() && modifier2.isPublic());
    modifier.setProtected(modifier1.isProtected() && modifier2.isProtected());
    modifier.setStatic(modifier1.isStatic() && modifier2.isStatic());

    // OR CONDITIONS
    modifier.setFinal(modifier1.isFinal() || modifier2.isFinal());

    // STEREOTYPES
    if (modifier1.isPresentStereotype()) {
      modifier.setStereotype(modifier1.getStereotype().deepClone());
      if (modifier2.isPresentStereotype()) {
        if (modifier1.getStereotype().getValuesList().stream()
            .anyMatch(
                sv1 ->
                    modifier2.getStereotype().getValuesList().stream()
                        .anyMatch(
                            sv2 ->
                                sv1.getName().equals(sv2.getName())
                                    && !sv1.getValue().equals(sv2.getValue())))) {
          logError("Cannot match Modifiers: Stereotypes do not match", modifier1, modifier2);
          return Optional.empty();
        }
        modifier.getStereotype().addAllValues(modifier2.getStereotype().getValuesList());
      }
    } else if (modifier2.isPresentStereotype()) {
      modifier.setStereotype(modifier2.getStereotype().deepClone());
    } else {
      modifier.setStereotypeAbsent();
    }

    return Optional.of(modifier1);
  }
}
