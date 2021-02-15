package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDDeSerHelper;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Map;

public class CDAssociationDeSer extends CDAssociationDeSerTOP {
  public static final String FURTHER_OBJECTS_MAP = "furtherObjects";
  public static final String SYM_ASSOCIATION_TYPE = "de.monticore.cdassociation._symboltable.SymAssociation";


  @Override
  public void serializeAddons(ICDAssociationScope toSerialize, CDAssociationSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
  }

  @Override
  public void serializeAddons(ICDAssociationArtifactScope toSerialize, CDAssociationSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(s2j.printer);
  }

  @Override
  public void deserializeAddons(ICDAssociationScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(scopeJson);
  }

  @Override
  public void deserializeAddons(ICDAssociationArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(scopeJson);
  }

  public static void serializeFurtherObjects(JsonPrinter printer) {
    if (!CDDeSerHelper.getInstance().getSymAssocForSerialization().isEmpty()) {
      printer.beginObject(FURTHER_OBJECTS_MAP);
      serializeSymAssociations(printer);
      printer.endObject();

      CDDeSerHelper.getInstance().getSymAssocForSerialization().clear();
    }
  }

  public static void deserializeFurtherObjects(JsonObject scopeJson) {
    if (scopeJson.hasObjectMember(FURTHER_OBJECTS_MAP)) {
      final JsonObject objectJson = scopeJson.getObjectMember(FURTHER_OBJECTS_MAP);
      objectJson.getMembers().entrySet().forEach(CDAssociationDeSer::deserializeSymAssociations);
    }
  }

  public static void serializeSymAssociations(JsonPrinter printer) {
    CDDeSerHelper.getInstance().getSymAssocForSerialization().forEach(a -> CDAssociationDeSer.serializeSymAssociation(printer, a));
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

  public static int handleSymAssociation(SymAssociation association) {
    // don't serialize the SymAssociation, just add it to the list and generate an identifier to be placed in the CDAssociation or CDRoleSymbol
    CDDeSerHelper.getInstance().addSymAssociationForSerialization(association);
    return association.hashCode();
  }

  public static void deserializeSymAssociation(String name, JsonObject symbolJson) {
    JsonDeSers.checkCorrectDeSerForKind(SYM_ASSOCIATION_TYPE, symbolJson);

    // don't use the builder, because the symAssociation is just partial
    final SymAssociation symAssociation = new SymAssociation();
    symAssociation.setIsAssociation(symbolJson.getBooleanMemberOpt("isAssociation").orElse(false));
    symAssociation.setIsComposition(symbolJson.getBooleanMemberOpt("isComposition").orElse(false));

    CDDeSerHelper.getInstance().addSymAssociationForDeserialization(Integer.parseInt(name), symAssociation);
  }

  public static void deserializeSymAssociations(Map.Entry<String, JsonElement> entry) {
    final String member = entry.getValue().getAsJsonObject().getStringMember(JsonDeSers.KIND);
    if (member.equals(SYM_ASSOCIATION_TYPE)) {
      deserializeSymAssociation(entry.getKey(), entry.getValue().getAsJsonObject());
    }
  }
}
