/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.cd.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd.cd4analysis._ast.ASTModifier;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd.prettyprint.AstPrinter.EMPTY_STRING;

/**
 * HW super type for classes, interfaces and enums
 *
 * @author Robert Heim, Galina Volkova
 */
public interface ASTCDType extends ASTCDTypeTOP {

  String getName();

  Optional<ASTModifier> getModifierOpt();

  void setModifier(ASTModifier modifier);

  List<ASTMCObjectType> getInterfaceList();

  List<ASTCDMethod> getCDMethodList();

  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   *
   * @return a string, e.g. abstract private final
   */
  default String printModifier() {
    Optional<ASTModifier> modifier = getModifierOpt();
    if (!modifier.isPresent()) {
      return EMPTY_STRING;
    }

    StringBuilder modifierStr = new StringBuilder();
    if (getModifierOpt().get().isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.get().isPublic()) {
      modifierStr.append(" public ");
    }
    else if (modifier.get().isPrivate()) {
      modifierStr.append(" private ");
    }
    else if (modifier.get().isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.get().isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.get().isStatic()) {
      modifierStr.append(" static ");
    }

    return modifierStr.toString();
  }

}
