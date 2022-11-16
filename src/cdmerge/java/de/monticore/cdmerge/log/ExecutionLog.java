/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.log;

import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.cdmerge.exceptions.FailFastException;
import de.se_rwth.commons.logging.Log;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Stores all trace, logs and debug info which occur during a merging process */
public class ExecutionLog {

  private List<LogEntry> theLog;

  private ErrorLevel maxOccuredErrorLevel = ErrorLevel.DEBUG;

  private ErrorLevel minLogable = ErrorLevel.WARNING;

  private boolean failFast = false;

  private boolean failOnWarning = false;

  private boolean traceEnabled = false;

  private boolean propagateLog = false;

  /** Creates a new MergeExecutionLog with specified minimum log level. */
  public ExecutionLog(
      ErrorLevel minLoggable, boolean failFast, boolean failOnWarning, boolean traceEnabled) {
    reset(minLoggable);
    this.failFast = failFast;
    this.failOnWarning = failOnWarning;
    this.traceEnabled = traceEnabled;
    this.propagateLog = false;
  }

  /** Creates a new MergeExecutionLog with default logable level INFO */
  public ExecutionLog() {
    reset(ErrorLevel.WARNING);
    this.failFast = false;
    this.failOnWarning = false;
    this.traceEnabled = false;
    this.propagateLog = false;
  }

  public void SetPropagateLog(boolean enablePropagateLog) {
    this.propagateLog = enablePropagateLog;
  }

  public boolean isEnabledPropagateLog() {
    return this.propagateLog;
  }

  /** Deletes all Logs and resets the max Error level */
  public void reset(ErrorLevel minLoggable) {
    this.theLog = new LinkedList<LogEntry>();
    // We don't consider DEBUG and FINE as severe log entries
    this.maxOccuredErrorLevel = ErrorLevel.INFO;
    this.minLogable = minLoggable;
  }

  /**
   * Get all Warnings
   *
   * @return Merge Log Entries with severity Warning, chronologically. The list is a {@link
   *     ImmutableList}
   */
  public List<LogEntry> getWarnings() {
    return ImmutableList.copyOf(
        this.theLog.stream()
            .filter(l -> l.getLevel() == ErrorLevel.WARNING)
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Get all Errors
   *
   * @return Merge Log Entries with severity Error, chronologically. The list is a {@link
   *     ImmutableList}
   */
  public List<LogEntry> getErrors() {
    return ImmutableList.copyOf(
        this.theLog.stream()
            .filter(l -> l.getLevel() == ErrorLevel.ERROR)
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Get all design Issues
   *
   * @return Merge Log Entries with severity design Issue, chronologically. The list is a {@link
   *     ImmutableList}
   */
  public List<LogEntry> getDesginIssues() {
    return ImmutableList.copyOf(
        this.theLog.stream()
            .filter(l -> l.getLevel() == ErrorLevel.DESIGN_ISSUE)
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Get all Info logs
   *
   * @return Merge Log Entries with log level INFO, chronologically. The list is a {@link
   *     ImmutableList}
   */
  public List<LogEntry> getInfos() {
    return ImmutableList.copyOf(
        this.theLog.stream()
            .filter(l -> l.getLevel() == ErrorLevel.INFO)
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Get all Debug logs
   *
   * @return Merge Log Entries with log level DEBUG, chronologically. The list is a {@link
   *     ImmutableList}
   */
  public List<LogEntry> getDebug() {
    return ImmutableList.copyOf(
        this.theLog.stream()
            .filter(l -> l.getLevel() == ErrorLevel.DEBUG)
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Get all Fine logs
   *
   * @return Merge Log Entries with log level FINE, chronologically. The list is a {@link
   *     ImmutableList}
   */
  public List<LogEntry> getFine() {
    return ImmutableList.copyOf(
        this.theLog.stream()
            .filter(l -> l.getLevel() == ErrorLevel.FINE)
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Get all Log entries in chronological order. Logs will not be filtered by minimum logable level
   * of this log. Hence, this function return the full trace
   *
   * @return Merge Log Entries with log level Warning, chronologically
   */
  public List<LogEntry> getAllLogs() {
    return this.getAllLogs(false);
  }

  /**
   * Get all Log entries in chronological order. Logs will be filtered by minimum logable level of
   * this log if parameter is true
   *
   * @return Merge Log Entries with log level Warning, chronologically
   */
  public List<LogEntry> getAllLogs(boolean filterMinimumLog) {
    theLog.sort(LogEntry::compareTo);
    return ImmutableList.copyOf(theLog);
  }

  /**
   * Get all Log entries in chronological order for the specified phase. Logs will be filtered by
   * minimum logable level of this log if parameter is true
   *
   * @return Merge Log Entries with log level Warning, chronologically
   */
  public List<LogEntry> getAllLogs(MergePhase phase) {

    return ImmutableList.copyOf(
        theLog.stream()
            .filter(l -> l.getPhase().equals(phase))
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Get all Log entries in chronological order for the specified phase. Logs will not be filtered
   * by minimum logable level, thus result will contain full trace
   *
   * @return Merge Log Entries with log level Warning, chronologically
   */
  public List<LogEntry> getAllLogs(MergePhase phase, boolean filterMinimumLog) {
    Stream<LogEntry> stream = theLog.stream().filter(l -> l.getPhase().equals(phase));
    if (filterMinimumLog) {
      stream.filter(l -> l.getLevel().ordinal() >= this.minLogable.ordinal());
    }
    return ImmutableList.copyOf(stream.sorted(LogEntry::compareTo).collect(Collectors.toList()));
  }

  /**
   * Get all Log entries in chronological order containing the messagePart. Logs will not be
   * filtered by minimum logable level
   *
   * @return Merge Log Entries matching the messagePart as regular expression, sorted
   *     chronologically
   */
  public List<LogEntry> getLogsWithMessageContaining(String rxMessagePart) {
    return ImmutableList.copyOf(
        theLog.stream()
            .filter(log -> log.getMessage().matches(rxMessagePart))
            .sorted(LogEntry::compareTo)
            .collect(Collectors.toList()));
  }

  /**
   * Check if Log contains an entry with the messagePart.
   *
   * @return True if Merge Log contains at least one Entry matching the messagePart as regular
   *     expression
   */
  public boolean hasLogWithMessageContaining(String rxMessagePart) {

    return theLog.stream().anyMatch(log -> log.getMessage().matches(rxMessagePart));
  }

  /**
   * Creates a log entry
   *
   * @param level - The severity
   * @param message - The log message
   * @param phase - Denote the phase during the merge process
   * @param left - the left ASTNode which was affected during this merger
   * @param right- the right ASTNode which was affected during this merger
   */
  public LogEntry log(
      ErrorLevel level, String message, MergePhase phase, ASTNode left, ASTNode right) {
    LogEntry entry =
        new LogEntry(level, message, phase, Optional.ofNullable(left), Optional.ofNullable(right));
    log(entry);
    return entry;
  }

  /**
   * Creates a log entry
   *
   * @param level - The severity
   * @param message - The log message
   * @param phase - Denote the phase during the merge process
   * @param astNode - the AST ASTNode which was affected during this merger
   */
  public LogEntry log(ErrorLevel level, String message, MergePhase phase, ASTNode astNode) {
    LogEntry entry =
        new LogEntry(level, message, phase, Optional.ofNullable(astNode), Optional.empty());
    log(entry);
    return entry;
  }

  /**
   * Adds all log entries of the other log to this log whose loglevel is equal or higher to this
   * minLogable
   *
   * @param otherLog
   */
  public void addLog(ExecutionLog otherLog) {
    this.theLog.addAll(
        otherLog.getAllLogs().stream()
            .filter(log -> log.getLevel().ordinal() >= this.minLogable.ordinal())
            .collect(Collectors.toList()));
    this.maxOccuredErrorLevel =
        otherLog.getMaxErrorLevel().compareTo(this.maxOccuredErrorLevel) > 0
            ? otherLog.getMaxErrorLevel()
            : this.maxOccuredErrorLevel;
  }

  /**
   * Creates a log entry
   *
   * @param level - The severity
   * @param message - The log message
   * @param phase - Denote the phase during the merge process
   */
  public LogEntry log(ErrorLevel level, String message, MergePhase phase) {
    LogEntry entry = new LogEntry(level, message, phase);
    log(entry);
    return entry;
  }

  public boolean hasWarnings() {
    return this.theLog.stream()
        .anyMatch(l -> l.getLevel().ordinal() == ErrorLevel.WARNING.ordinal());
  }

  public boolean hasErrors() {
    return this.theLog.stream().anyMatch(l -> l.getLevel().ordinal() == ErrorLevel.ERROR.ordinal());
  }

  public boolean hasDesignIssues() {
    return this.theLog.stream()
        .anyMatch(l -> l.getLevel().ordinal() == ErrorLevel.DESIGN_ISSUE.ordinal());
  }

  /**
   * Reports the highest degree of severity of any recorded log entry
   *
   * @return the max error level for the recorded log entries
   */
  public ErrorLevel getMaxErrorLevel() {
    return this.maxOccuredErrorLevel;
  }

  private synchronized void log(LogEntry logEntry) {
    // We always add all logs to the internal log, not considering min log level
    this.theLog.add(logEntry);
    checkReportToCLI(logEntry);
    if (isLogable(logEntry) && isEnabledPropagateLog()) {
      switch (logEntry.getLevel()) {
        case DEBUG:
        case DESIGN_ISSUE:
          if (Log.isDebugEnabled("CDMerge")) {
            Log.debug(logEntry.getPhase().name() + " " + logEntry.getMessage(), "CDMerge");
          }
          break;
        case ERROR:
          Log.error(logEntry.getPhase().name() + " " + logEntry.getMessage());
          break;
        case FINE:
          if (Log.isTraceEnabled("CDMerge")) {
            Log.trace(logEntry.getPhase().name() + " " + logEntry.getMessage(), "CDMerge");
          }
          break;
        case INFO:
          if (Log.isInfoEnabled("CDMerge")) {
            Log.info(logEntry.getPhase().name() + " " + logEntry.getMessage(), "CDMerge");
          }
          break;
        case WARNING:
          Log.warn(logEntry.getPhase().name() + " " + logEntry.getMessage());
          break;
        default:
          Log.warn(logEntry.getPhase().name() + " " + logEntry.getMessage());
          break;
      }
    }
    checkRaiseMaxErrorLevel(logEntry.getLevel());
    checkFailFast(logEntry);
  }

  private void checkRaiseMaxErrorLevel(ErrorLevel level) {
    if (level.compareTo(this.maxOccuredErrorLevel) > 0) {
      this.maxOccuredErrorLevel = level;
    }
  }

  private void checkFailFast(LogEntry logEntry) {
    if (this.isFailFast()) {
      if (logEntry.getLevel().equals(ErrorLevel.ERROR)) {
        throw new FailFastException(
            "Errors occurred while merging:		 "
                + logEntry.getMessage()
                + "\nClass diagrams cannot be merged into a sound class diagram.");
      } else if (logEntry.getLevel().equals(ErrorLevel.WARNING) && this.cancelOnWarnings()) {
        throw new FailFastException(
            "Warnings occurred while merging: "
                + logEntry.getMessage()
                + "\n Merged class diagram is sound but could possibly misbehave when used in other "
                + "tools. If you would still like to use it, please uncheck the strict-flag and "
                + "execute the program once again.");
      }
    }
  }

  private boolean isFailFast() {
    return this.failFast;
  }

  private boolean cancelOnWarnings() {
    return this.failOnWarning;
  }

  public ErrorLevel getMinimumLogableLevel() {
    return this.minLogable;
  }

  private boolean isLogable(LogEntry logEntry) {
    if (logEntry == null) {
      return false;
    }
    if (logEntry.getLevel().ordinal() >= getMinimumLogableLevel().ordinal()) {
      return true;
    }
    return false;
  }

  private void checkReportToCLI(LogEntry logEntry) {
    if (this.traceEnabled && (isLogable(logEntry))) {
      System.out.println(logEntry);
    }
  }

  @Override
  public String toString() {
    return toString(this.minLogable);
  }

  public String toString(ErrorLevel minLevel) {
    StringBuilder sb = new StringBuilder();
    List<LogEntry> logs = getAllLogs();
    for (LogEntry logEntry : logs) {
      if (logEntry.getLevel().ordinal() >= minLevel.ordinal()) {
        sb.append(logEntry.toString() + "\n");
      }
    }
    return sb.toString();
  }
}
