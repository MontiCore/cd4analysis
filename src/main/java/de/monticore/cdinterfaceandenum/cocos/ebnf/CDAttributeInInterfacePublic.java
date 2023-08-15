/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.monticore.cdinterfaceandenum._util.CDInterfaceAndEnumTypeDispatcher;
import de.se_rwth.commons.logging.Log;

public class CDAttributeInInterfacePublic implements CDInterfaceAndEnumASTCDInterfaceCoCo {

  public static final String ERROR_CODE = "0xCDCF7";

  public static final String ERROR_MSG = "attributes in interface at %s must be public";

  @Override
  public void check(ASTCDInterface node) {
    CDInterfaceAndEnumTypeDispatcher typeDispatcher = CDInterfaceAndEnumMill.typeDispatcher();
    for (ASTCDMember member : node.getCDMemberList()) {
      if (typeDispatcher.isASTCDAttribute(member)) {
        ASTCDAttribute attribute = typeDispatcher.asASTCDAttribute(member);
        if (attribute.getModifier().isProtected() || attribute.getModifier().isPrivate()) {
          Log.error(String.format(ERROR_CODE + " " + ERROR_MSG, member.get_SourcePositionStart()));
        }
      }
    }
  }

}
