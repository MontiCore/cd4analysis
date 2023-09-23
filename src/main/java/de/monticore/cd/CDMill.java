/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import de.monticore.cd._symboltable.ModifierHandler;
import de.monticore.cd._visitor.CDElementVisitor;
import de.monticore.cd._visitor.CDMemberVisitor;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.prettyprint.CDTypeKindPrinter;

/**
 * @deprecated not part of the hierachy of languages
 * thus, the visitors breack in some scenarios
 * use other Mills of the CD4A-family.
 */
@Deprecated
public class CDMill {
  protected static CDMill mill;
  protected static CDMill millCDElementVisitor;
  protected static CDMill millCDMemberVisitor;
  protected static CDMill millCDTypeKindPrinter;
  protected static CDMill millModifierHandler;
  protected static CDMill millCDAttributeFacade;
  protected static CDMill millCDConstructorFacade;
  protected static CDMill millCDMethodFacade;
  protected static CDMill millCDParameterFacade;

  protected static CDMill getMill() {
    if (mill == null) {
      mill = new CDMill();
    }
    return mill;
  }

  public static CDElementVisitor cDElementVisitor(CDElementVisitor.Options... options) {
    if (millCDElementVisitor == null) {
      millCDElementVisitor = getMill();
    }
    return millCDElementVisitor._cDElementVisitor(options);
  }

  public static CDMemberVisitor cDMemberVisitor(CDMemberVisitor.Options... options) {
    if (millCDMemberVisitor == null) {
      millCDMemberVisitor = getMill();
    }
    return millCDMemberVisitor._cDMemberVisitor(options);
  }

  public static CDTypeKindPrinter cDTypeKindPrinter() {
    return cDTypeKindPrinter(false);
  }

  public static CDTypeKindPrinter cDTypeKindPrinter(boolean followingSpace) {
    if (millCDTypeKindPrinter == null) {
      millCDTypeKindPrinter = getMill();
    }
    return millCDTypeKindPrinter._cDTypeKindPrinter(followingSpace);
  }

  public static ModifierHandler modifierHandler() {
    if (millModifierHandler == null) {
      millModifierHandler = getMill();
    }
    return millModifierHandler._modifierHandler();
  }

  public static CDAttributeFacade cDAttributeFacade() {
    if (millCDAttributeFacade == null) {
      millCDAttributeFacade = getMill();
    }
    return millCDAttributeFacade._cDAttributeFacade();
  }

  public static CDConstructorFacade cDConstructorFacade() {
    if (millCDConstructorFacade == null) {
      millCDConstructorFacade = getMill();
    }
    return millCDConstructorFacade._cDConstructorFacade();
  }

  public static CDMethodFacade cDMethodFacade() {
    if (millCDMethodFacade == null) {
      millCDMethodFacade = getMill();
    }
    return millCDMethodFacade._cDMethodFacade();
  }

  public static CDParameterFacade cDParameterFacade() {
    if (millCDParameterFacade == null) {
      millCDParameterFacade = getMill();
    }
    return millCDParameterFacade._cDParameterFacade();
  }

  public CDElementVisitor _cDElementVisitor(CDElementVisitor.Options... options) {
    return new CDElementVisitor(options);
  }

  public CDMemberVisitor _cDMemberVisitor(CDMemberVisitor.Options... options) {
    return new CDMemberVisitor(options);
  }

  public CDTypeKindPrinter _cDTypeKindPrinter(boolean followingSpace) {
    return new CDTypeKindPrinter(followingSpace);
  }

  public ModifierHandler _modifierHandler() {
    return new ModifierHandler();
  }

  public CDAttributeFacade _cDAttributeFacade() {
    return CDAttributeFacade.getInstance();
  }

  public CDConstructorFacade _cDConstructorFacade() {
    return CDConstructorFacade.getInstance();
  }

  public CDMethodFacade _cDMethodFacade() {
    return CDMethodFacade.getInstance();
  }

  public CDParameterFacade _cDParameterFacade() {
    return CDParameterFacade.getInstance();
  }
}
