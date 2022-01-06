/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the attribute connected by associations exist.
 */
public class CDAssociationByAttributeFieldExist implements
    CDAssociationASTCDAssociationCoCo {

  protected CDAssociationPrettyPrinter prettyPrinter = new CDAssociationPrettyPrinter();

  @Override
  public void check(ASTCDAssociation assoc) {
    checkTypeExists(assoc.getLeft(), assoc);
    checkTypeExists(assoc.getRight(), assoc);
  }

  private void checkTypeExists(ASTCDAssocSide side, ASTCDAssociation assoc) {
    if (side.isPresentCDQualifier() && side.getCDQualifier().isPresentByAttributeName()) {
      final String attributeName = side.getCDQualifier().getByAttributeName();
      final SymTypeExpression type = side.getCDRole().getSymbol().getType();
      if (type.getFieldList(attributeName, false).size() == 0) {
        Log.error(
            String
                .format(
                    "0xCDC6B: The attribute %s does not exists in %s %s.",
                    attributeName,
                    CDMill.cDTypeKindPrinter().print(type),
                    type.print()),
            assoc.get_SourcePositionStart());
      }
    }
  }
}
