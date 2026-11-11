/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.mctype;

import de.monticore.cdconformance.CDConformanceContext;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class MCTypeUtil {
  
  private MCTypeUtil() {
  }
  
  public static boolean isVoidType(ASTMCType type) {
    return type.printType().equals("void");
  }
  
  public static boolean isVoidType(ASTMCReturnType type) {
    return type.printType().equals("void");
  }
  
  public static boolean isUnderspecified(CDConformanceContext context, ASTMCType type) {
    return isUnderspecified(context.getUnderspecifiedPlaceholderTypeName(), type);
  }
  
  public static boolean isUnderspecified(String underspecifiedType, ASTMCType type) {
    return type.printType().equals(underspecifiedType);
  }
  
  /**
   * Checks if the return type is underspecified.<br>
   * Use this as {@link #isUnderspecified(CDConformanceContext, ASTMCType)} throws an exception
   * if the type is 'void'.
   */
  public static boolean isUnderspecified(CDConformanceContext context, ASTMCReturnType type) {
    return type.printType().equals(context.getUnderspecifiedPlaceholderTypeName());
  }
  
}
