package de.monticore.cdmerge.log;

import de.se_rwth.commons.logging.Log;

public class MCLoggerWrapper extends Log {

  private static MCLoggerWrapper log;

  private boolean disableSystemReporting;

  // system exit on error always disabled, will be controlled by merging framework
  private boolean quickFail = false;

  // terminate with an non-zero exit code
  private boolean isNonZeroExit = true;

  private ErrorLevel minLogLevel;

  public MCLoggerWrapper(ErrorLevel minLogLevel, boolean disableSystemReporting) {
    super();
    this.minLogLevel = minLogLevel;
    this.disableSystemReporting = disableSystemReporting;
  }

  public static void init(ErrorLevel minLogLevel, boolean disableSystemReporting) {
    log = new MCLoggerWrapper(minLogLevel, disableSystemReporting);
    Log.setLog(log);
  }

  @Override
  protected void doErrPrint(String msg) {
    if (!disableSystemReporting) {
      super.doErrPrint(msg);
    }
  }

  @Override
  protected void doPrintStackTrace(Throwable t) {
    if (!disableSystemReporting) {
      super.doPrintStackTrace(t);
    }
  }

  @Override
  protected void doErrPrintStackTrace(Throwable t) {
    if (!disableSystemReporting) {
      super.doErrPrintStackTrace(t);
    }
  }

  @Override
  protected void doPrint(String msg) {
    if (!disableSystemReporting) {
      super.doPrint(msg);
    }
  }

  @Override
  // We don't do MC trace logs
  protected boolean doIsTraceEnabled(String logName) {
    return false;
  }

  @Override
  protected boolean doIsDebugEnabled(String logName) {
    return this.minLogLevel.ordinal() <= ErrorLevel.DEBUG.ordinal();
  }

  @Override
  protected boolean doIsInfoEnabled(String logName) {
    return this.minLogLevel.ordinal() <= ErrorLevel.INFO.ordinal();
  }

  @Override
  protected boolean doIsFailQuickEnabled() {
    return this.quickFail;
  }

  @Override
  protected boolean doIsNonZeroExitEnabled() {
    return this.isNonZeroExit;
  }

  protected void setLevel(ErrorLevel level) {
    this.minLogLevel = level;
  }

  public void setDisableSystemReporting(boolean disableSystemReporting) {
    this.disableSystemReporting = disableSystemReporting;
  }

  public void setQuickFail(boolean quickFail) {
    this.quickFail = quickFail;
  }

  public void setMinLogLevel(ErrorLevel minLogLevel) {
    this.minLogLevel = minLogLevel;
  }
}
