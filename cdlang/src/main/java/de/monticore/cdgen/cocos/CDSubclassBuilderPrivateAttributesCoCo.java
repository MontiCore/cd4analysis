/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

public class CDSubclassBuilderPrivateAttributesCoCo implements CDBasisASTCDClassCoCo {
  
  public static final String ERROR_CODE = "0xCDCE8";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + "Class %s has a <<builder>> stereotype, but extends superclass %s which lacks a builder and contains private attributes.";
  
  @Override
  public void check(ASTCDClass node) {
    if (node != null) {
      if ((node.getSuperclassList().size() == 1) && (node.getModifier().isPresentStereotype())) {
        if (node.getModifier().getStereotype().contains("builder")) {
          
          boolean superclassBuilder = false;
          boolean hasPrivateAttributes = false;
          
          ASTCDClass superClass = CDSymbolTables.getTransitiveSuperClasses(node).getFirst();
          if (superClass != null) {
            
            // Check if all superclass attributes are public
            for (var attribute : superClass.getCDAttributeList()) {
              if (attribute.getModifier().isPrivate()) {
                hasPrivateAttributes = true;
                break;
              }
            }
            
            // Check if superclass has <<builder>> stereotype
            if (superClass.getModifier().isPresentStereotype()) {
              superclassBuilder = superClass.getModifier().getStereotype().contains("builder");
            }
          }
          
          if (hasPrivateAttributes && !superclassBuilder) {
            String name = node.getName() != null ? node.getName() : "unnamed";
            String superclassName = superClass.getName() != null ? superClass.getName() : "unnamed";
            Log.error(String.format(ERROR_MESSAGE, name, superclassName));
          }
        }
      }
    }
  }
  
}
