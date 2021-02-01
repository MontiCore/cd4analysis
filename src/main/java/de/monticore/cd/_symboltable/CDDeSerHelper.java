/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.cdassociation._symboltable.SymAssociation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CDDeSerHelper {
  protected static CDDeSerHelper INSTANCE;
  protected Set<SymAssociation> symAssocForSerialization;
  protected Map<Integer, SymAssociation> symAssocForDeserialization;

  protected CDDeSerHelper() {
    this(new HashSet<>());
  }

  protected CDDeSerHelper(Set<SymAssociation> symAssociations) {
    this.symAssocForSerialization = symAssociations;
  }

  public static CDDeSerHelper getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CDDeSerHelper();
    }

    return INSTANCE;
  }

  public Set<SymAssociation> getSymAssocForSerialization() {
    return symAssocForSerialization;
  }

  public Map<Integer, SymAssociation> getSymAssocForDeserialization() {
    return symAssocForDeserialization;
  }

  public void setSymAssocForSerialization(Set<SymAssociation> symAssocForSerialization) {
    this.symAssocForSerialization = symAssocForSerialization;
  }

  public void setSymAssocForDeserialization(Map<Integer, SymAssociation> symAssocForDeserialization) {
    this.symAssocForDeserialization = symAssocForDeserialization;
  }

  public boolean addSymAssociationForSerialization(SymAssociation symAssociation) {
    return this.symAssocForSerialization.add(symAssociation);
  }

  public SymAssociation addSymAssociationForDeserialization(int hash, SymAssociation symAssociation) {
    return this.symAssocForDeserialization.put(hash, symAssociation);
  }
}
