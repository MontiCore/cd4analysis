package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Map;

public class CDAssociationDeSer extends CDAssociationDeSerTOP {
  public static final String FURTHER_OBJECTS_MAP = "furtherObjects";
  public static final String SYM_ASSOCIATION_TYPE = "de.monticore.cdassociation._symboltable.SymAssociation";

  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void setSymAssociations(Map<Integer, SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
  }

  @Override
  protected void serializeAddons(ICDAssociationScope toSerialize, CDAssociationSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(symbolTablePrinterHelper, s2j.printer);
  }

  @Override
  protected void serializeAddons(ICDAssociationArtifactScope toSerialize, CDAssociationSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(symbolTablePrinterHelper, s2j.printer);
  }

  @Override
  protected void deserializeAddons(ICDAssociationScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }

  @Override
  protected void deserializeAddons(ICDAssociationArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }

  public static void serializeFurtherObjects(CDSymbolTablePrinterHelper symbolTablePrinterHelper, JsonPrinter printer) {
    if (!symbolTablePrinterHelper.getSymAssociations().isEmpty()) {
      printer.beginObject(FURTHER_OBJECTS_MAP);
      serializeSymAssociations(printer, symbolTablePrinterHelper);
      printer.endObject();

      symbolTablePrinterHelper.getSymAssociations().clear();
    }
  }

  public static void deserializeFurtherObjects(Map<Integer, SymAssociation> symAssociations, JsonObject scopeJson) {
    if (scopeJson.hasObjectMember(FURTHER_OBJECTS_MAP)) {
      final JsonObject objectJson = scopeJson.getObjectMember(FURTHER_OBJECTS_MAP);
      objectJson.getMembers().entrySet().forEach(m -> deserializeSymAssociations(symAssociations, m));
    }
  }

  public static void serializeSymAssociations(JsonPrinter printer, CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    symbolTablePrinterHelper.getSymAssociations().forEach(a -> CDAssociationDeSer.serializeSymAssociation(printer, a));
  }

  /**
   * A SymAssociation is serialized with its hashCode as an identifier
   *
   * @param printer
   * @param symAssociation
   */
  public static void serializeSymAssociation(JsonPrinter printer, SymAssociation symAssociation) {
    printer.beginObject(String.valueOf(symAssociation.hashCode()));

    printer.member(JsonDeSers.KIND, SYM_ASSOCIATION_TYPE);
    printer.member("isAssociation", symAssociation.isAssociation());
    printer.member("isComposition", symAssociation.isComposition());

    printer.endObject();
  }

  public static int handleSymAssociation(CDSymbolTablePrinterHelper symbolTablePrinterHelper, SymAssociation association) {
    // don't serialize the SymAssociation, just add it to the list and generate an identifier to be placed in the CDAssociation or CDRoleSymbol
    symbolTablePrinterHelper.addSymAssociation(association);
    return association.hashCode();
  }

  public static void deserializeSymAssociation(Map<Integer, SymAssociation> symAssociations, String name, JsonObject symbolJson) {
    JsonDeSers.checkCorrectDeSerForKind(SYM_ASSOCIATION_TYPE, symbolJson);

    // don't use the builder, because the symAssociation is just partial
    final SymAssociation symAssociation = new SymAssociation();
    symAssociation.setIsAssociation(symbolJson.getBooleanMemberOpt("isAssociation").orElse(false));
    symAssociation.setIsComposition(symbolJson.getBooleanMemberOpt("isComposition").orElse(false));

    symAssociations.put(Integer.valueOf(name), symAssociation);
  }

  public static void deserializeSymAssociations(Map<Integer, SymAssociation> symAssociations, Map.Entry<String, de.monticore.symboltable.serialization.json.JsonElement> entry) {
    final String member = entry.getValue().getAsJsonObject().getStringMember(JsonDeSers.KIND);
    if (member.equals(SYM_ASSOCIATION_TYPE)) {
      deserializeSymAssociation(symAssociations, entry.getKey(), entry.getValue().getAsJsonObject());
    }
  }
}
