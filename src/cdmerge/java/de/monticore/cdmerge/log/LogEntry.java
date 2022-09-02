/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.log;

import de.monticore.ast.ASTNode;
import de.monticore.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.util.CDUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * A log entry for recording events during a merging process
 */
public class LogEntry implements Comparable<LogEntry> {
  private final LocalDateTime timestamp = LocalDateTime.now();

  private final ErrorLevel level;

  private final String message;

  private final Optional<ASTNode> node1;

  private final Optional<ASTNode> node2;

  private final MergePhase phase;

  public LogEntry(ErrorLevel level, String message, MergePhase phase) {
    this(level, message, phase, Optional.empty(), Optional.empty());
  }

  public LogEntry(ErrorLevel level, String message, MergePhase phase, Optional<ASTNode> left,
      Optional<ASTNode> right) {
    this.level = level;
    this.message = message;
    this.node1 = left;
    this.node2 = right;
    this.phase = phase;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public ErrorLevel getLevel() {
    return level;
  }

  public String getMessage() {
    String logMessage = message;
    if (this.node1.isPresent()) {
      logMessage += " ast element 1 " + CDUtils.prettyPrintInline(node1.get());

    }
    if (this.node2.isPresent()) {
      logMessage += " ast element 1 " + CDUtils.prettyPrintInline(node2.get());
    }
    return logMessage;
  }

  public Optional<ASTNode> getLeftNode() {
    return node1;
  }

  public Optional<ASTNode> getRightNode() {
    return node2;
  }

  public MergePhase getPhase() {
    return phase;
  }

  /**
   * Compares this log entry with the other chronologically
   */
  public int compareTo(LogEntry other) {
    if (this.timestamp.compareTo(other.timestamp) == 0) {
      return this.phase.compareTo(other.phase);
    }
    else {
      return this.timestamp.compareTo(other.timestamp);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[" + this.level + "]\t<" + this.phase + ">" + " : " + this.message);
    if (node1.isPresent()) {
      sb.append(" {" + astToString(node1.get()) + "}");
    }
    if (node2.isPresent()) {
      sb.append(" {" + astToString(node2.get()) + "}");
    }
    return sb.toString();
  }

  public String toStringWithTimeStamp() {
    StringBuilder sb = new StringBuilder();
    String datetime = this.timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    sb.append(
        "(" + datetime + ") [" + this.level + "]  <" + this.phase + ">" + " : " + this.message);
    if (node1.isPresent()) {
      sb.append(" {" + astToString(node1.get()) + "}");
    }
    if (node2.isPresent()) {
      sb.append(" {" + astToString(node2.get()) + "}");
    }
    return sb.toString();
  }

  private String astToString(ASTNode node) {
    if (node instanceof ASTCDCompilationUnit) {
      return ((ASTCDCompilationUnit) node).getCDDefinition().getName();
    }
    else if (node instanceof ASTCDDefinition) {
      return ((ASTCDDefinition) node).getName();
    }
    else if (node instanceof ASTCDAssociationNode) {
      return CDUtils.prettyPrintInline((ASTCDAssociationNode) node);
    }
    else if (node instanceof ASTCD4AnalysisNode) {
      return CDUtils.prettyPrintInline((ASTCD4AnalysisNode) node);
    }
    else if (node instanceof ASTCDBasisNode) {
      return CDUtils.prettyPrintInline((ASTCDBasisNode) node);
    }
    return "";

  }

}
