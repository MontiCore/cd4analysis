package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.handleSymAssociation;

public class CDAssociationSymbolDeSer extends CDAssociationSymbolDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;

  public CDAssociationSymbolDeSer() {
    init();
  }

  protected void init() {
    this.symbolTablePrinterHelper = new CDSymbolTablePrinterHelper();
    setSymAssociations(new HashMap<>());
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void setSymAssociations(Map<Integer, SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
  }

  @Override
  protected void serializeAssoc(Optional<SymAssociation> assoc, CDAssociationSymbols2Json s2j) {
    if (assoc != null && assoc.isPresent()) {
      s2j.printer.member("association", handleSymAssociation(symbolTablePrinterHelper, assoc.get()));
    }
  }

  @Override
  protected Optional<SymAssociation> deserializeAssoc(JsonObject symbolJson) {
    return symbolJson.getIntegerMemberOpt("association")
        .flatMap(a -> Optional.ofNullable(symAssociations.get(a)));
  }
}
