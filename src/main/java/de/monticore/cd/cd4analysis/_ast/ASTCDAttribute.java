/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

import static de.monticore.cd.prettyprint.CD4CodePrinter.EMPTY_STRING;

public class ASTCDAttribute extends ASTCDAttributeTOP
    implements ASTCD4AnalysisNode {

  protected ASTCDAttribute() {
  }


  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   *
   * @return a string, e.g. abstract private final
   */
  public String printModifier() {
    if (!isPresentModifier()) {
      return EMPTY_STRING;
    }

    StringBuilder modifierStr = new StringBuilder();
    if (modifier.get().isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.get().isPublic()) {
      modifierStr.append(" public ");
    } else if (modifier.get().isPrivate()) {
      modifierStr.append(" private ");
    } else if (modifier.get().isProtected()) {
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

  /**
   * Prints a value of an attribute
   *
   * @return a string representing the ASTValue
   */
  public String printValue() {
    if (!isPresentValue()) {
      return EMPTY_STRING;
    }

    return (new CD4CodePrinter().printValue(value));
  }

  /**
   * Prints an attribute type
   *
   * @return String representation of the ASTType
   */
  public String printType() {
    return new CD4CodePrinter().printType(mCType);
  }

  public String printAnnotation() {
    if (isPresentModifier()) {
      if (getModifier().isPresentStereotype()) {
        StringBuffer sb = new StringBuffer();
        for (ASTCDStereoValue s : getModifier().getStereotype().values) {
          sb.append(s.getName());
          sb.append("\n");
        }
        return sb.toString();
      }
    }
    return "";
  }

}
