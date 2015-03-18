package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoHelper;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCDAssociationList;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;

public class AssociationSourceTypeNotGenericChecker implements
    CD4AnalysisASTCDDefinitionCoCo {

  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "Association %s is invalid, because an association's source may not be a generic type";

  public void check(ASTCDDefinition cdDefinition) {

    ASTCDAssociationList assocList = cdDefinition.getCDAssociations();
    for (ASTCDAssociation assoc : assocList) {

      if (assoc.isLeftToRight() || assoc.isBidirectional()) {
        ASTQualifiedName leftType = assoc.getLeftReferenceName();

        printErrorOnGeneric(leftType, assoc);
      }

      if (assoc.isRightToLeft()) {
        ASTQualifiedName rightType = assoc.getRightReferenceName();
        printErrorOnGeneric(rightType, assoc);
      }
    }
  }

  private void printErrorOnGeneric(ASTQualifiedName sourceType,
      ASTCDAssociation assoc) {

    String s = CD4ACoCoHelper.qualifiedNameToString(sourceType);

    if (s.contains("<")) {

      String assocString = CD4ACoCoHelper.printAssociation(assoc);
      CoCoHelper.buildErrorMsg(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, assocString),
          assoc.get_SourcePositionStart());

    }
  }
}
