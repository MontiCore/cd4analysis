/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable;

import com.google.common.collect.ImmutableList;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class CDAssociationSymbol extends CDAssociationSymbolTOP {

  private CDTypeSymbolLoader sourceType = null;
  
  private CDTypeSymbolLoader targetType = null;
  
  private Cardinality sourceCardinality;

  private Cardinality targetCardinality;

  private Optional<CDQualifierSymbol> qualifier = Optional.empty();

  private Optional<String> sourceRole = Optional.empty();

  private Optional<String> targetRole = Optional.empty();

  private boolean bidirectional = false;

  private boolean derived = false;

  private Optional<String> assocName = Optional.empty();

  private Relationship relationship = Relationship.ASSOCIATION;

  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  public CDAssociationSymbol(final CDTypeSymbolLoader sourceType, final CDTypeSymbolLoader targetType) {
    super("");
    this.sourceType = requireNonNull(sourceType);
    this.targetType = requireNonNull(targetType);
  }

  public CDAssociationSymbol(String name) {
    super(name);
  }

  public boolean isReadOnly() {
    if (!this.isPresentAstNode())
      return false;
    return ((ASTCDAssociation) (this.getAstNode())).isReadOnly();
  }

  @Override
  public String toString() {
    String sourceRole = isPresentSourceRole() ? getSourceRole() : "";
    String targetRole = isPresentTargetRole() ? getTargetRole() : "";
    return CDAssociationSymbol.class.getSimpleName() + " " + getDerivedName() + "/"
        + ": "
        + "" + getSourceType().getName() + "(" + sourceRole + ")"
        + " -> " + "(" + targetRole + ")" + getTargetType().getName();
  }
  
  public CDTypeSymbolLoader getTargetType() {
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
  
  public CDTypeSymbolLoader getSourceType() {
    return sourceType;
  }

  public Cardinality getTargetCardinality() {
    return targetCardinality;
  }

  public String getSourceRole() {
    if (isPresentSourceRole()) {
      return this.sourceRole.get();
    }
    Log.error("0xU6001 SourceRole can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public boolean isPresentSourceRole() {
    return sourceRole.isPresent();
  }

  public void setSourceRole(String sourceRole) {
    this.sourceRole = Optional.ofNullable(sourceRole);
  }

  public void setSourceRoleAbsent() {
    this.sourceRole = Optional.empty();
  }

  public String getTargetRole() {
    if (isPresentTargetRole()) {
      return this.targetRole.get();
    }
    Log.error("0xU6000 TargetRole can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public boolean isPresentTargetRole() {
    return this.targetRole.isPresent();
  }

  public void setTargetRole(String targetRole) {
    this.targetRole = Optional.ofNullable(targetRole);
  }

  public void setTargetRoleAbsent() {
    this.targetRole = Optional.empty();
  }

  public void setQualifier(final CDQualifierSymbol qualifier) {
    this.qualifier = Optional.ofNullable(qualifier);
  }

  public void setQualifierAbsent() {
    this.qualifier = Optional.empty();
  }

  public CDQualifierSymbol getQualifier() {
    if (isPresentQualifier()) {
      return this.qualifier.get();
    }
    Log.error("0xU6002 Qualifier can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public boolean isPresentQualifier() {
    return this.qualifier.isPresent();
  }

  public void setAssocName(final String assocName) {
    this.assocName = Optional.ofNullable(assocName);
  }

  public void setAssocNameAbsent() {
    this.assocName = Optional.empty();
  }

  public String getAssocName() {
    if (isPresentAssocName()) {
      return this.assocName.get();
    }
    Log.error("0xU6003 AssocName can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public boolean isPresentAssocName() {
    return assocName.isPresent();
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
    if (assocName.isPresent()) {
      return assocName.get();
    }
    if (isPresentTargetRole()) {
      return getTargetRole();
    }
    return StringTransformations.uncapitalize(Names.getSimpleName(getTargetType().getName()))
        .intern();
  }

  // needed for OCL
  public String getDerivedNameSourceRole() {
    if (assocName.isPresent()) {
      return assocName.get();
    }
    if (isPresentSourceRole()) {
      return getSourceRole();
    }
    return StringTransformations.uncapitalize(Names.getSimpleName(getSourceType().getName()))
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

  private CDAssociationSymbol inverseAssoc = null;

  // this returns the opposite association (source and target is switched); needed in OCL
  public CDAssociationSymbol getInverseAssociation() {
    if (inverseAssoc == null) {
      // calculate only on demand
      inverseAssoc = new CDAssociationSymbol(getTargetType(), getSourceType());
      inverseAssoc.setBidirectional(isBidirectional());
      inverseAssoc.setDerived(isDerived());
      if (isPresentQualifier()) {
        inverseAssoc.setQualifier(getQualifier());
      }
      inverseAssoc.setRelationship(getRelationship());
      inverseAssoc.setSourceCardinality(getTargetCardinality());
      if (isPresentTargetRole()) {
        inverseAssoc.setSourceRole(getTargetRole());
      }
      inverseAssoc.setTargetCardinality(getSourceCardinality());
      if (isPresentSourceRole()) {
        inverseAssoc.setTargetRole(getSourceRole());
      }
      if (isPresentAssocName()) {
        inverseAssoc.setAssocName(getAssocName());
      }
      inverseAssoc.setAccessModifier(getAccessModifier());
      if (isPresentAstNode()) {
        inverseAssoc.setAstNode(getAstNode());
      }
      inverseAssoc.setEnclosingScope(getEnclosingScope());
      inverseAssoc.setFullName(getFullName());
      getStereotypes().forEach(inverseAssoc::addStereotype);
    }
    return inverseAssoc;
  }

}
