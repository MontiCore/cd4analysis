/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.cdassociation._symboltable.SymAssociation;

import java.util.HashSet;
import java.util.Set;

public class CDSymbolTablePrinterHelper {
  protected Set<SymAssociation> symAssociations;
  protected Set<String> visitedSymbols;

  public CDSymbolTablePrinterHelper() {
    this(new HashSet<>());
  }

  public CDSymbolTablePrinterHelper(Set<SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
    this.visitedSymbols = new HashSet<>();
  }

  public Set<SymAssociation> getSymAssociations() {
    return symAssociations;
  }

  public void setSymAssociations(Set<SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
  }

  public boolean addSymAssociation(SymAssociation symAssociation) {
    return this.symAssociations.add(symAssociation);
  }

  public boolean visit(String name) {
    return visitedSymbols.add(name);
  }
}