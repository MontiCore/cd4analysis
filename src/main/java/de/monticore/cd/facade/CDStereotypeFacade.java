package de.monticore.cd.facade;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.types.MCTypeFacade;
import de.monticore.umlmodifier._ast.ASTModifier;

public class CDStereotypeFacade {

  /** Class that helps with the creation of ASTCDType */
  private static CDStereotypeFacade stereotypeFacade;

  private final MCTypeFacade mcTypeFacade;

  private CDStereotypeFacade() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  public static CDStereotypeFacade getInstance() {
    if (stereotypeFacade == null) {
      stereotypeFacade = new CDStereotypeFacade();
    }
    return stereotypeFacade;
  }

  /** delegation methods for a more comfortable usage */
  public void addStereotype(ASTModifier modifier, String name) {
    if (!modifier.isPresentStereotype()) {
      modifier.setStereotype(CD4CodeMill.stereotypeBuilder().build());
    }
    modifier.getStereotype().addValues(CD4CodeMill.stereoValueBuilder().setName(name).build());
  }

  public void addStereotype(ASTModifier modifier, String name, String value) {
    if (!modifier.isPresentStereotype()) {
      modifier.setStereotype(CD4CodeMill.stereotypeBuilder().build());
    }
    ASTStringLiteral text = CD4CodeMill.stringLiteralBuilder().setSource(value).build();
    modifier
        .getStereotype()
        .addValues(CD4CodeMill.stereoValueBuilder().setName(name).setText(text).build());
  }
}
