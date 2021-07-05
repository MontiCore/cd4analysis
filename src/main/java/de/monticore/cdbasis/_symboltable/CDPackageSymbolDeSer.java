/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;

public class CDPackageSymbolDeSer extends CDPackageSymbolDeSerTOP {
  @Override
  public String serialize(CDPackageSymbol toSerialize, CDBasisSymbols2Json s2j) {
    // override the super behavior:
    // move everything of the spanned scope in the current scope
    // => have CDTypeSymbol in the same scope as the CDPackageSymbol

    // ========== edited copy from parent ========
    // ========== do not edit ====================
    de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();
    p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

    // serialize symbolrule attributes

    // ====== only change: don't iterate the subscopes in the opened object ========
    // serialize spanned scope
//     if (toSerialize.getSpannedScope().isExportingSymbols()
//         && toSerialize.getSpannedScope().getSymbolsSize() > 0) {
//       toSerialize.getSpannedScope().accept(s2j.getTraverser());
//     }

    serializeAddons(toSerialize, s2j);
    p.endObject();
    // ========== end of parent =============

    return p.toString();
  }

  @Override
  public CDPackageSymbol deserialize(JsonObject symbolJson) {
    return super.deserialize(symbolJson);
  }
}
