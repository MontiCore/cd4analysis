package de.monticore.umlcd4a.cocos.ebnf;

import com.google.common.base.Optional;

import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDAssociationList;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._ast.ASTStereoValue;
import de.cd4analysis._ast.ASTStereoValueList;
import de.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.cocos.CoCoHelper;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

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


        CoCoHelper.buildErrorMsg(ERROR_CODE,
            printErrorOnEnum(assoc),
            assoc.get_SourcePositionStart());
      }

      if (assoc.getRightModifier().isPresent()
          && isOrdered(assoc.getRightModifier().get())
          && assoc.getRightCardinality().isPresent()
          && !assoc.getRightCardinality().get().isOne()) {


        CoCoHelper.buildErrorMsg(ERROR_CODE,
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
