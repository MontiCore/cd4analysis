package de.monticore.umlcd4a.symboltable;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import de.monticore.symboltable.CommonSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

public class CDAssociationSymbol extends CommonSymbol {
  
  public static final CDAssociationSymbolKind KIND = new CDAssociationSymbolKind();
  
  private final CDTypeSymbol sourceType;
  
  private final CDTypeSymbol targetType;
  
  private Cardinality sourceCardinality;
  
  private Cardinality targetCardinality;
  
  private Optional<CDQualifierSymbol> qualifier = Optional.empty();
  
  private Optional<String> role = Optional.empty();
  
  private boolean bidirectional = false;
  
  private boolean derived = false;
  
  private Optional<String> assocName = Optional.empty();
  
  private Relationship relationship = Relationship.ASSOCIATION;
  
  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  protected CDAssociationSymbol(final CDTypeSymbol sourceType, final CDTypeSymbol targetType) {
    super("", KIND);
    this.sourceType = requireNonNull(sourceType);
    this.targetType = requireNonNull(targetType);
  }
  
  @Override
  public String toString() {
    return CDAssociationSymbol.class.getSimpleName() + " " + getDerivedName() + "/" + getRole()
        + ": "
        + "" + getSourceType().getName() + " -> " + getTargetType().getName();
  }
  
  public CDTypeSymbol getTargetType() {
    return targetType;
  }
  
  public Cardinality getSourceCardinality() {
    return sourceCardinality;
  }
  
  public void setSourceCardinality(final Cardinality sourceCardinality) {
    this.sourceCardinality = sourceCardinality;
  }
  
  public void setTargetCardinality(final Cardinality cardinality) {
    this.targetCardinality = cardinality;
  }
  
  public CDTypeSymbol getSourceType() {
    return sourceType;
  }
  
  public Cardinality getTargetCardinality() {
    return targetCardinality;
  }
  
  public void setRole(final Optional<String> role) {
    this.role = role;
  }
  
  public Optional<String> getRole() {
    return role;
  }
  
  public void setQualifier(final Optional<CDQualifierSymbol> qualifier) {
    this.qualifier = qualifier;
  }
  
  public Optional<CDQualifierSymbol> getQualifier() {
    return qualifier;
  }
  
  public void setAssocName(final Optional<String> assocName) {
    this.assocName = assocName;
  }
  
  public Optional<String> getAssocName() {
    return assocName;
  }
  
  public void setBidirectional(final boolean bidirectional) {
    this.bidirectional = bidirectional;
  }
  
  public boolean isBidirectional() {
    return bidirectional;
  }
  
  public void setDerived(final boolean derived) {
    this.derived = derived;
  }
  
  public boolean isDerived() {
    return this.derived;
  }
  
  public void setRelationship(final Relationship relationship) {
    this.relationship = relationship;
  }
  
  public Relationship getRelationship() {
    return relationship;
  }
  
  @Override
  public String getName() {
    return assocName.orElse("");
  }
  
  public String getDerivedName() {
    if (role.isPresent()) {
      return role.get();
    }
    if (assocName.isPresent()) {
      return assocName.get();
    }
    return StringTransformations.uncapitalize(Names.getSimpleName(getTargetType().getName()))
        .intern();
  }
  
  public List<Stereotype> getStereotypes() {
    return ImmutableList.copyOf(stereotypes);
  }
  
  public Optional<Stereotype> getStereotype(final String name) {
    for (final Stereotype stereotype : this.stereotypes) {
      if (stereotype.getName().equals(name)) {
        return Optional.of(stereotype);
      }
    }
    return Optional.empty();
  }
  
  public boolean containsStereotype(final String name, final String value) {
    for (final Stereotype stereotype : this.stereotypes) {
      if (stereotype.compare(name, value)) {
        return true;
      }
    }
    return false;
    
  }
  
  public void addStereotype(final Stereotype stereotype) {
    this.stereotypes.add(stereotype);
  }
  
}
