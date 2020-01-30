/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.symboltable.IScopeSpanningSymbol;

import java.util.List;
import java.util.Optional;

/**
 * This class prints all non-primitive attributes of CD4A symbol classes. FOr each symbol loader,
 * only the name of the symbol is stored.
 */
public class CD4AnalysisSymbolTablePrinter extends CD4AnalysisSymbolTablePrinterTOP {

  //constants for complex attributes, for which (de)serialization must be realized manually
  public static final String CD_INTERFACES = "cdInterfaces";

  public static final String SUPER_CLASS = "superClass";

  public static final String TYPE = "type";

  public static final String RETURN_TYPE = "returnType";

  public static final String EXCEPTIONS = "exceptions";

  public static final String ASSOCIATION_TARGET = "associationTarget";

  @Override protected void serializeCDTypeCdInterfaces(List<CDTypeSymbolLoader> cdInterfaces) {
    printer.beginArray(CD_INTERFACES);
    for (CDTypeSymbolLoader loader : cdInterfaces) {
      printer.value(loader.getName());
    }
    printer.endArray();
  }

  @Override protected void serializeCDTypeSuperClass(Optional<CDTypeSymbolLoader> superClass) {
    if (superClass.isPresent()) {
      printer.member(SUPER_CLASS, superClass.get().getName());
    }
  }

  @Override protected void serializeCDFieldType(CDTypeSymbolLoader type) {
    printer.member(TYPE, type.getName());
  }

  @Override protected void serializeRoleAssociationTarget(CDTypeSymbolLoader associationTarget) {
    printer.member(ASSOCIATION_TARGET, associationTarget.getName());
  }

  @Override protected void serializeCDMethOrConstrReturnType(CDTypeSymbolLoader returnType) {
    printer.member(RETURN_TYPE, returnType.getName());
  }

  @Override protected void serializeCDMethOrConstrExceptions(List<CDTypeSymbolLoader> exceptions) {
    printer.beginArray(EXCEPTIONS);
    for (CDTypeSymbolLoader loader : exceptions) {
      printer.value(loader.getName());
    }
    printer.endArray();
  }

  /**
   * This method is overriden to add the completre scope spanning symbol (and not just a reference
   * to it) in case the spanning symbol is a method or constructor. This is done to distinguish
   * spanning symbols of methods with the same nam, but different parameters-
   *
   * @param spanningSymbol
   */
  protected void addScopeSpanningSymbol(IScopeSpanningSymbol spanningSymbol) {
    if (spanningSymbol instanceof CDMethOrConstrSymbol) {
      printer
          .beginObject(de.monticore.symboltable.serialization.JsonConstants.SCOPE_SPANNING_SYMBOL);
      printer.member(de.monticore.symboltable.serialization.JsonConstants.KIND,
          "de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol");
      printer.member(de.monticore.symboltable.serialization.JsonConstants.NAME,
          spanningSymbol.getName());
      serializeCDMethOrConstr((CDMethOrConstrSymbol) spanningSymbol);
      printer.endObject();
    }
    else {
      super.addScopeSpanningSymbol(spanningSymbol);
    }
  }

  public void visit(de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol node) {
    // DO NOT serialize all relevant additional attributes here, they are stored in the spanning symbol
  }

  public void endVisit(de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol node) {
    // DO NOT serialize all relevant additional attributes here, they are stored in the spanning symbol
  }

}
