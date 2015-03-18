package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoHelper;
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

public class AssociationSourceTypeNotExternalChecker implements
		CD4AnalysisASTCDDefinitionCoCo {
	

  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "Association %s is invalid, because an association's source may not be an external type.";

	@Override
	public void check(ASTCDDefinition cdDefinition) {
		

		ASTCDAssociationList assocList = cdDefinition.getCDAssociations();
		for(ASTCDAssociation assoc : assocList) {
			
			if(assoc.isLeftToRight() || assoc.isBidirectional()) {
				ASTQualifiedName leftType = assoc.getLeftReferenceName();
				printErrorOnClassOrIface(leftType, assoc,cdDefinition);
			}
			
			if(assoc.isRightToLeft()) {
				ASTQualifiedName rightType = assoc.getRightReferenceName();
				printErrorOnClassOrIface(rightType, assoc,cdDefinition);
			}
		}
	}
	
	
	private void printErrorOnClassOrIface(ASTQualifiedName sourceType, ASTCDAssociation assoc, ASTCDDefinition ast ) {
		
	  String typeName = CD4ACoCoHelper.qualifiedNameToString(sourceType);
	  
		if(isClass(typeName, ast.getCDClasses()) || isIFace(typeName, ast.getCDInterfaces())) {
			
			
			String assocString = CD4ACoCoHelper.printAssociation(assoc);
			
			
      CoCoHelper.buildErrorMsg(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, assocString),
          assoc.get_SourcePositionStart());
		}
	}
	
	private boolean isClass(String className, ASTCDClassList classList) {
	  for(ASTCDClass clazz : classList) {
	    if(clazz.getName().equals(className)) {
	      return true;
	    }
	    
	  }
	  return false;
	}
	
	 private boolean isIFace(String ifName, ASTCDInterfaceList ifList) {
	    for(ASTCDInterface iface : ifList) {
	      if(iface.getName().equals(ifName)) {
	        return true;
	      }
	      
	    }
	    return false;
	  }
	
}
