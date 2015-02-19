package de.monticore.umlcd4a.cocos.ebnf;

import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._ast.ASTCDEnumList;
import de.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.cocos.CoCoHelper;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;

public class AssociationSourceTypeChecker implements
    CD4AnalysisASTCDDefinitionCoCo {

  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "Association %s is invalid, because an association's source may not be an enumeration";

  @Override
  public void check(ASTCDDefinition cdDefinition) {

    for (ASTCDAssociation assoc : cdDefinition.getCDAssociations()) {

      if (assoc.isLeftToRight() || assoc.isBidirectional()) {
        ASTQualifiedName leftType = assoc.getLeftReferenceName();
        printErrorOnEnum(leftType, assoc, cdDefinition);
      }

      if (assoc.isRightToLeft()) {
        ASTQualifiedName rightType = assoc.getRightReferenceName();
        printErrorOnEnum(rightType, assoc, cdDefinition);
      }

    }
  }

  private boolean isEnum(ASTCDEnumList enums, String enumName) {

    for (ASTCDEnum e : enums) {
      if (e.getName().equals(enumName)) {
        return true;
      }
    }
    return false;
  }

  private void printErrorOnEnum(ASTQualifiedName sourceType,
      ASTCDAssociation assoc, ASTCDDefinition ast) {

    if (isEnum(ast.getCDEnums(),
        CD4ACoCoHelper.qualifiedNameToString(sourceType))) {

      String assocString = CD4ACoCoHelper.printAssociation(assoc);

      CoCoHelper.buildErrorMsg(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, assocString),
          assoc.get_SourcePositionStart());
    }
  }
}
