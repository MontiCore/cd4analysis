package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCDAssociationList;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDClassList;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._ast.ASTCDInterfaceList;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;

public class AssociationSourceTypeNotExternalCoCo implements
    CD4AnalysisASTCDDefinitionCoCo {
  
  public static final String ERROR_CODE = "0xC4A22";
  
  public static final String ERROR_MSG_FORMAT = "Association %s is invalid, because an association's source may not be an external type.";
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    
    ASTCDAssociationList assocList = cdDefinition.getCDAssociations();
    for (ASTCDAssociation assoc : assocList) {
      
      if (assoc.isLeftToRight() || assoc.isBidirectional()) {
        ASTQualifiedName leftType = assoc.getLeftReferenceName();
        if (isExternal(leftType, assoc, cdDefinition)) {
          error(assoc);
        }
      }
      
      if (assoc.isRightToLeft() || assoc.isBidirectional()) {
        ASTQualifiedName rightType = assoc.getRightReferenceName();
        if (isExternal(rightType, assoc, cdDefinition)) {
          error(assoc);
        }
      }
      
      if (assoc.isUnspecified()) {
        ASTQualifiedName leftType = assoc.getLeftReferenceName();
        ASTQualifiedName rightType = assoc.getRightReferenceName();
        // not both can be external, but one is ok
        if (isExternal(leftType, assoc, cdDefinition) && isExternal(rightType, assoc, cdDefinition)) {
          error(assoc);
        }
      }
    }
  }
  
  private void error(ASTCDAssociation assoc) {
    String assocString = "";
    if (assoc.getName().isPresent()) {
      assocString = assoc.getName().get();
    }
    else {
      assocString = CD4ACoCoHelper.printAssociation(assoc);
    }
    CoCoLog.error(ERROR_CODE,
        String.format(ERROR_MSG_FORMAT, assocString),
        assoc.get_SourcePositionStart());
  }
  
  private boolean isExternal(ASTQualifiedName sourceType, ASTCDAssociation assoc,
      ASTCDDefinition ast) {
    String typeName = CD4ACoCoHelper.qualifiedNameToString(sourceType);
    if (isClass(typeName, ast.getCDClasses()) || isIFace(typeName, ast.getCDInterfaces())) {
      return false;
    }
    return true;
  }
  
  private boolean isClass(String className, ASTCDClassList classList) {
    for (ASTCDClass clazz : classList) {
      if (clazz.getName().equals(className)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isIFace(String ifName, ASTCDInterfaceList ifList) {
    for (ASTCDInterface iface : ifList) {
      if (iface.getName().equals(ifName)) {
        return true;
      }
    }
    return false;
  }
  
}
