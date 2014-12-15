/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CoCoError {
  
  private String errorCode;
  
  private String errorMessage;
  
  private String sourcePosition;
  
  /**
   * Constructor for cd4analysis.cocos.CoCoError
   * 
   * @param errorCode
   * @param errorMessage
   * @param sourcePosition
   */
  public CoCoError(String errorCode, String errorMessage, String sourcePosition) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.sourcePosition = sourcePosition;
  }
  
  public String buildErrorMsg() {
    return String.format("%s: %s %s",
        getSourcePosition(),
        getErrorCode(),
        getErrorMessage());
  }
  
  /**
   * @return errorCode
   */
  public String getErrorCode() {
    return this.errorCode;
  }
  
  /**
   * @return errorMessage
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }
  
  /**
   * @return sourcePosition
   */
  public String getSourcePosition() {
    return this.sourcePosition;
  }
}
