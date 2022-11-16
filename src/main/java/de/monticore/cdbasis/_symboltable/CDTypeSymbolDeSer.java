/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import java.util.List;

public class CDTypeSymbolDeSer extends CDTypeSymbolDeSerTOP {
  @Override
  public String serialize(CDTypeSymbol toSerialize, CDBasisSymbols2Json s2j) {
    // serialize the fqn of the symbol, not just the name

    // ============== copy from parent ============
    de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();
    p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    // ============== change ============
    p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getFullName());
    // ============== change end ========

    // serialize symbolrule attributes
    serializeIsClass(toSerialize.isIsClass(), s2j);
    serializeIsInterface(toSerialize.isIsInterface(), s2j);
    serializeIsEnum(toSerialize.isIsEnum(), s2j);
    serializeIsAbstract(toSerialize.isIsAbstract(), s2j);
    serializeIsPrivate(toSerialize.isIsPrivate(), s2j);
    serializeIsProtected(toSerialize.isIsProtected(), s2j);
    serializeIsPublic(toSerialize.isIsPublic(), s2j);
    serializeIsStatic(toSerialize.isIsStatic(), s2j);
    serializeIsFinal(toSerialize.isIsFinal(), s2j);
    serializeSuperTypes(toSerialize.getSuperTypesList(), s2j);

    // serialize spanned scope
    if (toSerialize.getSpannedScope().isExportingSymbols()
        && toSerialize.getSpannedScope().getSymbolsSize() > 0) {
      toSerialize.getSpannedScope().accept(s2j.getTraverser());
    }
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());

    serializeAddons(toSerialize, s2j);
    p.endObject();

    return p.toString();
  }

  @Override
  protected void serializeSuperTypes(List<SymTypeExpression> superTypes, CDBasisSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.printer, "superTypes", superTypes);
  }

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson);
  }
}
