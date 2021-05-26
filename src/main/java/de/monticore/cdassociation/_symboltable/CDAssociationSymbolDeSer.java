package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDDeSerHelper;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Optional;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.handleSymAssociation;

public class CDAssociationSymbolDeSer extends CDAssociationSymbolDeSerTOP {
  @Override
  protected void serializeAssoc(Optional<SymAssociation> assoc, CDAssociationSymbols2Json s2j) {
    if (assoc != null && assoc.isPresent()) {
      s2j.printer.member("association", handleSymAssociation(assoc.get()));
    }
  }

  @Override
  protected Optional<SymAssociation> deserializeAssoc(JsonObject symbolJson) {
    return symbolJson.getIntegerMemberOpt("association")
        .flatMap(a -> Optional.ofNullable(CDDeSerHelper.getInstance().getSymAssocForDeserialization().get(a)));
  }

  @Override
  public String serialize(CDAssociationSymbol toSerialize, CDAssociationSymbols2Json s2j) {
    // ============== copy from parent ============
    de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();
    p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

    // serialize symbolrule attributes
    if (toSerialize.isPresentAssoc()) {
      serializeAssoc(Optional.of(toSerialize.getAssoc()), s2j);
    }
    // ============== change ============
    // don't serialize the spanned scope
//    if (toSerialize.getSpannedScope().isExportingSymbols()
//        && toSerialize.getSpannedScope().getSymbolsSize() > 0) {
//      toSerialize.getSpannedScope().accept(s2j.getTraverser());
//    }
    // ============== change end ========

    serializeAddons(toSerialize, s2j);
    p.endObject();

    return p.toString();
  }
}
