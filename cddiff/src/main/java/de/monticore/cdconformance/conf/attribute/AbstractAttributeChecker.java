package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;

public abstract class AbstractAttributeChecker implements CDAttributeChecker {

  protected final String mapping;
  protected final TypeIncarnationHelper typeHelper;
  protected ASTCDType concreteType;
  protected ASTCDType referenceType;

  protected AbstractAttributeChecker(String mapping, TypeIncarnationHelper typeHelper) {
    this.mapping = mapping;
    this.typeHelper = typeHelper;
  }

  @Override
  public boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref) {
    /*
     * An attribute conforms to the reference attribute if one of the following holds:
     * - the concrete attribute has the exact same type as the reference attribute
     * - the concrete attribute type is an incarnation of the reference attribute type
     * - the reference attribute type is underspecified
     */
    return typeHelper.isMCTypeMatched(concrete.getMCType(), ref.getMCType());
  }

  @Override
  public ASTCDType getConcreteType() {
    return concreteType;
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.concreteType = conType;
  }

  @Override
  public ASTCDType getReferenceType() {
    return referenceType;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.referenceType = refType;
  }
}
