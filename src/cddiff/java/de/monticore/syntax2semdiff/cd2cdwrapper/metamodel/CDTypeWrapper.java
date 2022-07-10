package de.monticore.syntax2semdiff.cd2cdwrapper.metamodel;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.monticore.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.distinguishASTCDTypeHelper;
import static de.monticore.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.getCDTypeWrapperKindStrHelper;

/**
 * The class, abstract class, interface and enum in AST will be converted to the corresponding
 * CDTypeWrapper
 * The kind of CDTypeWrapper are CDWRAPPER_CLASS, CDWRAPPER_ENUM, CDWRAPPER_ABSTRACT_CLASS,
 * CDWRAPPER_INTERFACE
 *
 * @attribute originalElement: store the original AST Class
 * @attribute editedElement: if the class has inherited attributes, they will be added into
 * editedElement.
 * @attribute cDWrapperLink4EnumClass: This attribute is only suitable for CDWRAPPER_ENUM class. If
 * someone CDTypeWrapper uses Enum in attributes, then this CDTypeWrapper name will be added into
 * this cDWrapperLink4EnumClass attribute of corresponding CDWRAPPER_ENUM class.
 */
public class CDTypeWrapper implements Cloneable {
  protected final ASTCDType originalElement;

  protected ASTCDType editedElement;

  protected Set<String> cDWrapperLink4EnumClass = new HashSet<>();

  public CDTypeWrapper(ASTCDType originalElement) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
  }

  public String getName() {
    return getCDTypeWrapperKindStrHelper(getCDWrapperKind()) + "_" + this.originalElement.getSymbol()
        .getFullName();
  }

  public CDWrapper.CDTypeWrapperKind getCDWrapperKind() {
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

  /**
   * @return attributes: {  [attributes name] : [attribute type] }
   */
  public Map<String, String> getAttributes() {
    Map<String, String> attributesMap = new HashMap<>();
    if (editedElement.getClass().equals(ASTCDEnum.class)) {
      ((ASTCDEnum) editedElement).getCDEnumConstantList()
          .forEach(e -> attributesMap.put(e.getName(), null));
    }
    else {
      editedElement.getCDAttributeList()
          .forEach(e -> attributesMap.put(e.getName(), e.printType()));
    }
    return attributesMap;
  }

  public void addAttribute(ASTCDAttribute astcdAttribute) {
    if (!this.editedElement.getCDAttributeList()
        .stream()
        .anyMatch(e -> e.getName().equals(astcdAttribute.getName()) && e.printType()
            .equals(astcdAttribute.printType()))) {
      this.editedElement.addCDMember(astcdAttribute);
    }
  }

  public ASTCDType getOriginalElement() {
    return originalElement;
  }

  public ASTCDType getEditedElement() {
    return editedElement;
  }

  @Override
  public CDTypeWrapper clone() throws CloneNotSupportedException {
    CDTypeWrapper cloned = (CDTypeWrapper) super.clone();
    cloned.editedElement = editedElement.deepClone();
    return cloned;
  }

}



