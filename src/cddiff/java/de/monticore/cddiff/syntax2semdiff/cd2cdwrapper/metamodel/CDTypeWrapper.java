/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4TypeHelper.*;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.se_rwth.commons.SourcePosition;
import java.util.*;

/**
 * The class, abstract class, interface and enum in AST will be converted to the corresponding
 * CDTypeWrapper The kind of CDTypeWrapper are CDWRAPPER_CLASS, CDWRAPPER_ENUM,
 * CDWRAPPER_ABSTRACT_CLASS, CDWRAPPER_INTERFACE
 *
 * @attribute originalElement: store the original AST Class
 * @attribute editedElement: if the class has inherited attributes, they will be added into
 *     editedElement.
 * @attribute cDWrapperLink4EnumClass: This attribute is only suitable for CDWRAPPER_ENUM class. If
 *     someone CDTypeWrapper uses Enum in attributes, then this CDTypeWrapper name will be added
 *     into this cDWrapperLink4EnumClass attribute of corresponding CDWRAPPER_ENUM class.
 * @attribute superclasses: store the superclasses (including itself) of this CDTypeWrapper
 * @attribute subclasses: store the subclasses (including itself) of this CDTypeWrapper
 * @attribute status: OPEN, LOCKED
 */
public class CDTypeWrapper implements Cloneable {
  protected final ASTCDType originalElement;

  protected ASTCDType editedElement;

  protected Set<String> cDWrapperLink4EnumClass = new HashSet<>();

  protected Set<String> superclasses = new LinkedHashSet<>();

  protected Set<String> subclasses = new LinkedHashSet<>();

  protected CDStatus status;

  public CDTypeWrapper(ASTCDType originalElement) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
    this.status = CDStatus.OPEN;
  }

  public String getName() {
    return getCDTypeWrapperKindStrHelper(getCDWrapperKind())
        + "_"
        + this.originalElement.getSymbol().getFullName();
  }

  public CDTypeWrapperKind getCDWrapperKind() {
    return distinguishASTCDTypeHelper(originalElement);
  }

  public String getOriginalClassName() {
    return this.originalElement.getSymbol().getFullName();
  }

  public Set<String> getCDWrapperLink4EnumClass() {
    return cDWrapperLink4EnumClass;
  }

  public void setCDWrapperLink4EnumClass(Set<String> cDWrapperLink4EnumClass) {
    this.cDWrapperLink4EnumClass = cDWrapperLink4EnumClass;
  }

  /** @return attributes: { [attributes name] : [attribute type] } */
  public Map<String, String> getAttributes() {
    Map<String, String> attributesMap = new HashMap<>();
    if (editedElement instanceof ASTCDEnum) {
      ((ASTCDEnum) editedElement)
          .getCDEnumConstantList()
          .forEach(e -> attributesMap.put(e.getName(), null));
    } else {
      editedElement
          .getCDAttributeList()
          .forEach(e -> attributesMap.put(e.getName(), e.printType()));
    }
    return attributesMap;
  }

  public void addAttribute(ASTCDAttribute astcdAttribute) {
    if (this.editedElement.getCDAttributeList().stream()
        .noneMatch(
            e ->
                e.getName().equals(astcdAttribute.getName())
                    && e.printType().equals(astcdAttribute.printType()))) {
      this.editedElement.addCDMember(astcdAttribute);
    }

    // attributes conflict
    if (this.editedElement.getCDAttributeList().stream()
        .anyMatch(
            e ->
                e.getName().equals(astcdAttribute.getName())
                    && !e.printType().equals(astcdAttribute.printType()))) {
      this.status = CDStatus.LOCKED;
    }
  }

  public ASTCDType getOriginalElement() {
    return originalElement;
  }

  public ASTCDType getEditedElement() {
    return editedElement;
  }

  public Set<String> getSuperclasses() {
    return superclasses;
  }

  public void setSuperclasses(Set<String> superclasses) {
    this.superclasses = superclasses;
  }

  public Set<String> getSubclasses() {
    return subclasses;
  }

  public void setSubclasses(Set<String> subclasses) {
    this.subclasses = subclasses;
  }

  public CDStatus getStatus() {
    return status;
  }

  public void setStatus(CDStatus status) {
    this.status = status;
  }

  public SourcePosition getSourcePosition() {
    return this.originalElement.get_SourcePositionStart();
  }

  public boolean isOpen() {
    return status == CDStatus.OPEN;
  }

  @Override
  public CDTypeWrapper clone() throws CloneNotSupportedException {
    CDTypeWrapper cloned = (CDTypeWrapper) super.clone();
    cloned.editedElement = editedElement.deepClone();
    return cloned;
  }
}
