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
}
