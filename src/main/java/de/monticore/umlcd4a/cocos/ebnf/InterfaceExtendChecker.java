package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.types.TypesPrinter;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDClassList;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._ast.ASTCDInterfaceList;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;

public class InterfaceExtendChecker implements CD4AnalysisASTCDDefinitionCoCo {

  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "Interaface %s cannot extend %s %s. An interface may only extend interfaces";

  @Override
  public void check(ASTCDDefinition cdDefinition) {

    ASTCDInterfaceList interfaces = cdDefinition.getCDInterfaces();

    for (ASTCDInterface iface : interfaces) {

      // get all referencedTypes the iface extends
      ASTReferenceTypeList referencedTypes = iface.getInterfaces();

      for (ASTReferenceType refType : referencedTypes) {
        String superKindName = TypesPrinter.printReferenceType(refType)
            .intern();

        boolean isIFace = isIFace(superKindName, cdDefinition.getCDInterfaces());

        if (!isIFace) {
          String name = iface.getName();

          String superKindType = "Class";
          if (!isClass(superKindType, cdDefinition.getCDClasses())) {
            superKindType = "Enum";
          }

          CoCoLog.error(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT, name, superKindType, superKindName),
              iface.get_SourcePositionStart());

        }
      }
    }

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
    for(ASTCDInterface iface : ifList) {
      if(iface.getName().equals(ifName)) {
        return true;
      }
      
    }
    return false;
  }
}
