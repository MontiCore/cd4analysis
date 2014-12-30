package cd4analysis.symboltable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.AbstractSymbol;
import mc.helper.NameHelper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CDAssociationSymbol extends AbstractSymbol {
  
  public static final CDAssociationSymbolKind KIND = new CDAssociationSymbolKind();
  
  private final CDTypeSymbol sourceType;
  private final CDTypeSymbol targetType;
  
  private Cardinality sourceCardinality;
  private Cardinality targetCardinality;

  // TODO PN ASK: Don't we distinguish between left and right qualifier (resp. role)?
  private String qualifier;
  private String role;
  private boolean bidirectional = false;
  
  private Relationship relationship = Relationship.ASSOCIATION;
  
  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  protected CDAssociationSymbol(String assocName, CDTypeSymbol sourceType, CDTypeSymbol targetType) {
    super(requireNonNull(assocName), KIND);
    this.sourceType = requireNonNull(sourceType);
    this.targetType = requireNonNull(targetType);
  }

  @Override
  public String toString() {
    return CDAssociationSymbol.class.getSimpleName() + " " + getName() + "/" + getRole() + ": "
        + "" + getSourceType() .getName() + " -> " + getTargetType().getName();
  }
  
  public CDTypeSymbol getTargetType() {
    return targetType;
  }
  
  public Cardinality getSourceCardinality() {
    return sourceCardinality;
  }

  public void setSourceCardinality(Cardinality sourceCardinality) {
    this.sourceCardinality = sourceCardinality;
  }

  public void setTargetCardinality(Cardinality cardinality) {
    this.targetCardinality = cardinality;
  }

  public CDTypeSymbol getSourceType() {
    return sourceType;
  }

  public Cardinality getTargetCardinality() {
    return targetCardinality;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }
  
  public String getQualifier() {
    return qualifier;
  }
  
  public void setBidirectional(boolean bidirectional) {
    this.bidirectional = bidirectional;
  }
  
  public boolean isBidirectional() {
    return bidirectional;
  }
  
  public void setRelationship(Relationship relationship) {
    this.relationship = relationship;
  }
  
  public Relationship getRelationship() {
    return relationship;
  }
  
  @Override
  public String getName() {

    if (!super.getName().isEmpty()) {
      return super.getName();
    }
    if (getRole() != null) {
      return getRole();
    }
    return NameHelper.firstToLower(NameHelper.getSimplenameFromComplexname(getTargetType().getName())).intern();
  }
  
  public List<Stereotype> getStereotypes() {
    return ImmutableList.copyOf(stereotypes);
  }
  
  public Optional<Stereotype> getStereotype(String name) {
    for (Stereotype stereotype: this.stereotypes) {
      if (stereotype.getName().equals(name)) {
        return Optional.of(stereotype);
      }
    }
    return Optional.absent();
  }

  public boolean containsStereotype(String name, String value) {
    for (Stereotype stereotype: this.stereotypes) {
      if (stereotype.compare(name, value)) {
        return true;
      }
    }
    return false;
    
  }

  public void addStereotype(Stereotype stereotype) {
    this.stereotypes.add(stereotype);
  }


}
