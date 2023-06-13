package de.monticore.conformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.conf.AttributeChecker;
import java.util.ArrayList;
import java.util.List;

public class CompAttributeChecker implements AttributeChecker {
  protected String mapping;

  protected EqNameAttributeChecker eqNameAttrChecker;
  protected STNamedAttributeChecker stNameAttrChecker;

  public CompAttributeChecker(String mapping) {
    eqNameAttrChecker = new EqNameAttributeChecker(mapping);
    stNameAttrChecker = new STNamedAttributeChecker(mapping);
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    List<ASTCDAttribute> refElements =
        new ArrayList<>(stNameAttrChecker.getMatchedElements(concrete));
    if (refElements.isEmpty()) {
      refElements.addAll(eqNameAttrChecker.getMatchedElements(concrete));
    }
    return refElements;
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return getMatchedElements(concrete).contains(ref);
  }

  @Override
  public ASTCDType getReferenceType() {
    return eqNameAttrChecker.getReferenceType();
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    eqNameAttrChecker.setReferenceType(refType);
    stNameAttrChecker.setReferenceType(refType);
  }

  @Override
  public ASTCDType getConcreteType() {
    return eqNameAttrChecker.getConcreteType();
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    eqNameAttrChecker.setConcreteType(conType);
    stNameAttrChecker.setConcreteType(conType);
  }
}
