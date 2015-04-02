package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCDAssociationList;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._ast.ASTStereoValue;
import de.monticore.umlcd4a._ast.ASTStereoValueList;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;

public class AssociationOrderedCardinalityChecker implements
    CD4AnalysisASTCDDefinitionCoCo {

  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "Association %s ([%s] %s %s %s [%s]) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.";

  @Override
  public void check(ASTCDDefinition cdDefinition) {

    ASTCDAssociationList assocList = cdDefinition.getCDAssociations();
    for (ASTCDAssociation assoc : assocList) {

      if (assoc.getLeftModifier().isPresent()
          && isOrdered(assoc.getLeftModifier().get())
          && assoc.getLeftCardinality().isPresent()
          && !assoc.getLeftCardinality().get().isOne()) {


        CoCoLog.error(ERROR_CODE,
            printErrorOnEnum(assoc),
            assoc.get_SourcePositionStart());
      }

      if (assoc.getRightModifier().isPresent()
          && isOrdered(assoc.getRightModifier().get())
          && assoc.getRightCardinality().isPresent()
          && !assoc.getRightCardinality().get().isOne()) {


        CoCoLog.error(ERROR_CODE,
            printErrorOnEnum(assoc),
            assoc.get_SourcePositionStart());
      }

    }
  }

  private boolean isOrdered(ASTModifier optional) {

    if (optional.getStereotype().isPresent()) {
      ASTStereoValueList list = optional.getStereotype().get().getValues();
      for (ASTStereoValue l : list) {
        if (l.getName().equals("ordered")) {
          return true;
        }
      }
    }

    return false;
  }

  private String printErrorOnEnum(ASTCDAssociation assoc) {
    String assocName = "";
    if(assoc.getName().isPresent()) {
      assocName = assoc.getName().get();
    } 
    String typeA = CD4ACoCoHelper.qualifiedNameToString(assoc.getLeftReferenceName());
    String typeB = CD4ACoCoHelper.qualifiedNameToString(assoc.getRightReferenceName());
    
    
    String roleA = "";
    if(assoc.getLeftRole().isPresent()) {
      roleA = assoc.getLeftRole().get();
    }
    
    String roleB = "";
    if(assoc.getRightRole().isPresent()) {
      roleB = assoc.getRightRole().get();
    }
    
    String arrow = "--";
    if(assoc.isLeftToRight()) {
      arrow = "->";
    } else if(assoc.isRightToLeft()) {
      arrow = "<-";
    } else if(assoc.isBidirectional()) {
      arrow = "<->";
    }

    return String.format(ERROR_MSG_FORMAT, assocName, typeA, roleA, arrow,
        roleB, typeB);

  }
  
  

  
}
