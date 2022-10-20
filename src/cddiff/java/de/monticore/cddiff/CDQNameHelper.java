package de.monticore.cddiff;

import de.monticore.cdassociation._ast.ASTCDAssocSide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CDQNameHelper {

  /**
   * This helper functions processes parts of a name such that they can be used in Alloy
   *
   * @return The processed name
   */
  public static String partHandler(List<String> parts, boolean toRoleName) {
    StringBuilder completeName = new StringBuilder();

    // Process to role name only
    if (toRoleName) {
      char[] roleName = parts.get(parts.size()-1).toCharArray();
      roleName[0] = Character.toLowerCase(roleName[0]);
      return new String (roleName);
    }

    // Combine all parts using "_" as separator instead of "."
    for (String part : parts) {
      completeName.append(part).append("_");
    }
    // Remove last "_"
    completeName = new StringBuilder(completeName.substring(0, completeName.length() - 1));


    return completeName.toString();
  }

  /**
   * This helper functions processes a qualified name such that it can be used in Alloy
   * @return The processed name
   */
  public static String processQName(String qname) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, qname.split("\\."));
    return partHandler(nameList, false);
  }

  /**
   * The default role-name for a referenced type is the (simple) type-name with the first letter
   * in lower case.
   * @param qname is the qualified name of the referenced type
   * @return default role-name
   */
  public static String processQName2RoleName(String qname) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, qname.split("\\."));
    return partHandler(nameList, true);
  }

  public static String inferRole(ASTCDAssocSide assocSide) {
    if (assocSide.isPresentCDRole()){
      return assocSide.getCDRole().getName();
    }
    return CDQNameHelper.processQName2RoleName(assocSide.getMCQualifiedType().getMCQualifiedName().getQName());
  }

}
