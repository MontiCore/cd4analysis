/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.List;
import static de.monticore.cd.prettyprint.AstPrinter.EMPTY_STRING;

/**
 * HW super type for classes, interfaces and enums
 *
 * @author Robert Heim, Galina Volkova
 */
public interface ASTCDType extends ASTCDTypeTOP {

  String getName();

  ASTModifier getModifier();

  boolean isPresentModifier();

  void setModifier(ASTModifier modifier);

  List<ASTMCObjectType> getInterfaceList();

  List<ASTCDMethod> getCDMethodList();

  List<ASTCDAttribute> getCDAttributeList();

  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   *
   * @return a string, e.g. abstract private final
   */
  default String printModifier() {
    if (!isPresentModifier()) {
      return EMPTY_STRING;
    }
    ASTModifier modifier = getModifier();
    StringBuilder modifierStr = new StringBuilder();
    if (modifier.isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.isPublic()) {
      modifierStr.append(" public ");
    }
    else if (modifier.isPrivate()) {
      modifierStr.append(" private ");
    }
    else if (modifier.isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.isStatic()) {
      modifierStr.append(" static ");
    }

    return modifierStr.toString();
  }

}
