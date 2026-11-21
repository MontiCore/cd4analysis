/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class CDSubclassBuilderPrivateAttributesCoCo implements CDBasisASTCDClassCoCo {
  
  public static final String ERROR_CODE = "0xCDCE8";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + "Class %s has a <<builder>> stereotype, but extends superclass %s which lacks a builder or contains private attributes.";
  
  /**
   * CoCo: Checks for all superclasses of a given subclass (that has the <<builder>> stereotype) if
   * they have the <<builder>> stereotype or only contain public attributes
   *
   * @param node (Class)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node != null) {
      if ((node.getSuperclassList().size() == 1) && (node.getModifier().isPresentStereotype())) {
        if (node.getModifier().getStereotype().contains("builder")) {
          
          boolean superclassBuilder = false;
          boolean hasPrivateAttributes = false;
          String superClassName = "";
          
          List<ASTCDClass> superClasses = CDSymbolTables.getTransitiveSuperClasses(node);
          
          for (ASTCDClass superClass : superClasses) {
            if (superClass != null) {
              
              // Check if all superclass attributes are public
              for (var attribute : superClass.getCDAttributeList()) {
                if (attribute.getModifier().isPrivate()) {
                  //check if there is a public setter
                  boolean hasSetters = superClass.getCDMethodList().stream().filter(c -> c
                      .getModifier().isPublic()).filter(c -> c.getName().contentEquals("set"
                          + attribute.getName())).anyMatch(c -> {
                            var params = c.getCDParameterList();
                            return params.size() == 1 && params.getFirst().getMCType().printType()
                                .equals(attribute.getMCType().printType());
                          });
                  if (!hasSetters) {
                    hasPrivateAttributes = true;
                    break;
                  }
                }
              }
              
              // Check if the superclass has the <<builder>> stereotype
              if (superClass.getModifier().isPresentStereotype()) {
                superclassBuilder = superClass.getModifier().getStereotype().contains("builder");
              }
            }
            if (!superclassBuilder || hasPrivateAttributes) {
              superClassName = superClass.getName() != null ? node.getName() : "unnamed";
              break;
            }
          }
          
          if (hasPrivateAttributes && !superclassBuilder) {
            String name = node.getName() != null ? node.getName() : "unnamed";
            Log.error(String.format(ERROR_MESSAGE, name, superClassName));
          }
        }
      }
    }
  }
  
}
