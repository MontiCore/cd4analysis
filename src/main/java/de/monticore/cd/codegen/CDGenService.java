/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.logging.Log;

public class CDGenService {

  protected final MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  protected int count = 0;

  public String getGeneratedErrorCode(String name) {
    // Use the string representation
    // also use a count to make sure no double codes can appear
    // because sometimes there is not enough information for a unique string
    String codeString = name + count;
    count++;
    //calculate hashCode, but limit the values to have at most 5 digits
    int hashCode = Math.abs(codeString.hashCode() % 100000);
    //use String formatting to add leading zeros to always have 5 digits
    String errorCodeSuffix = String.format("%05d", hashCode);
    return "x" + errorCodeSuffix;
  }

  /**
   * It's possible to overwrite this method if the attribute has prefixes
   */
  public String getNativeAttributeName(String attributeName) {
    return attributeName;
  }

  public ASTMCType getFirstTypeArgument(ASTMCType type) {
    if (type instanceof ASTMCGenericType) {
      ASTMCGenericType genericType = (ASTMCGenericType) type;
      if (!genericType.isEmptyMCTypeArguments()) {
        return genericType.getMCTypeArgument(0).getMCTypeOpt().get();
      }
    }
    Log.error("0x110C11 InternalError: type is not optional");
    return null; // May not happen
  }

  public boolean hasDerivedAttributeName(ASTCDAttribute astcdAttribute) {
    return false;
  }

}
