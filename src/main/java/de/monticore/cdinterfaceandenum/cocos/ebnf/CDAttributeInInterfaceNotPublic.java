/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

public class CDAttributeInInterfaceNotPublic implements CDInterfaceAndEnumASTCDInterfaceCoCo {

  public static final String ERROR_CODE = "0xCDCF7";

  public static final String ERROR_MSG = "attributes in interface at %s must not be public";

  @Override
  public void check(ASTCDInterface node) {
    for (ASTCDMember member : node.getCDMemberList()) {
      if (member instanceof ASTCDAttribute) {
        if (((ASTCDAttribute) member).getModifier().isPublic()) {
          Log.error(String.format(ERROR_CODE + " " + ERROR_MSG, member.get_SourcePositionStart()));
        }
      }
    }
  }
}
