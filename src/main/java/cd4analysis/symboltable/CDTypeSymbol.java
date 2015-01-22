package cd4analysis.symboltable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import mc.helper.NameHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CDTypeSymbol extends CommonJTypeSymbol<CDTypeSymbol, CDFieldSymbol, CDMethodSymbol> {
  
  public static final CDTypeSymbolKind KIND = new CDTypeSymbolKind();

  private final List<CDAssociationSymbol> associations = new ArrayList<>();
  private final List<Stereotype> stereotypes = new ArrayList<>();


  protected CDTypeSymbol(String name) {
    super(name, KIND, CDFieldSymbol.KIND, CDMethodSymbol.KIND);
  }

  public String getExtendedName() {
    return "CD type " + getName();  
  }
  
  public void addAssociation(CDAssociationSymbol assoc) {
    this.associations.add(assoc);
  }
  
  public List<CDAssociationSymbol> getAssociations() {
    return ImmutableList.copyOf(associations);
  }

  public List<CDFieldSymbol> getEnumConstants() {
    final List<CDFieldSymbol> enums = getFields().stream()
        .filter(CDFieldSymbol::isEnumConstant)
        .collect(Collectors.toList());
    return ImmutableList.copyOf(enums);
  }
  
  public List<Stereotype> getStereotypes() {
    return stereotypes;
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
  
  public String getModelName() {
    return NameHelper.getQualifier(getName());
  }


}
