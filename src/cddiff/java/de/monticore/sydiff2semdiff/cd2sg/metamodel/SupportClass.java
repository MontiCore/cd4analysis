package de.monticore.sydiff2semdiff.cd2sg.metamodel;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.monticore.sydiff2semdiff.cd2sg.SupportHelper.distinguishASTCDTypeHelper;
import static de.monticore.sydiff2semdiff.cd2sg.SupportHelper.getSupportClassKindStrHelper;

/**
 * The class, abstract class, interface and enum in AST will be converted to the corresponding SupportClass
 * The kind of SupportClass are SUPPORT_CLASS, SUPPORT_ENUM, SUPPORT_ABSTRACT_CLASS, SUPPORT_INTERFACE
 *
 * @attribute originalElement:
 * store the original AST Class
 * @attribute editedElement:
 * if the class has inherited attributes, they will be added into editedElement.
 * @attribute supportLink4EnumClass:
 * This attribute is only suitable for SUPPORT_ENUM class.
 * If someone SupportClass uses Enum in attributes,
 * then this SupportClass name will be added into this supportLink4EnumClass attribute of corresponding SUPPORT_ENUM class.
 */
public class SupportClass implements Cloneable {
  protected final ASTCDType originalElement;
  protected ASTCDType editedElement;
  protected Set<String> supportLink4EnumClass = new HashSet<>();

  public SupportClass(ASTCDType originalElement) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
  }

  public String getName() {
    return getSupportClassKindStrHelper(getSupportKind()) + "_" + this.originalElement.getSymbol().getFullName();
  }

  public SupportGroup.SupportClassKind getSupportKind() {
    return distinguishASTCDTypeHelper(originalElement);
  }

  public String getOriginalClassName() {
    return this.originalElement.getSymbol().getFullName();
  }

  public Set<String> getSupportLink4EnumClass() {
    return supportLink4EnumClass;
  }

  public void setSupportLink4EnumClass(Set<String> supportLink4EnumClass) {
    this.supportLink4EnumClass = supportLink4EnumClass;
  }

  /**
   * @return attributes:
   * {  [attributes name] : [attribute type] }
   */
  public Map<String, String> getAttributes() {
    Map<String, String> attributesMap = new HashMap<>();
    if (editedElement.getClass().equals(ASTCDEnum.class)) {
      ((ASTCDEnum) editedElement).getCDEnumConstantList().forEach(e ->
        attributesMap.put(e.getName(), null));
    } else {
      editedElement.getCDAttributeList().forEach(e ->
        attributesMap.put(e.getName(), e.printType()));
    }
    return attributesMap;
  }

  public void addAttribute(ASTCDAttribute astcdAttribute) {
    if (!this.editedElement.getCDAttributeList()
      .stream()
      .anyMatch(e -> e.getName().equals(astcdAttribute.getName())
        && e.printType().equals(astcdAttribute.printType()))) {
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
  public SupportClass clone() throws CloneNotSupportedException {
    SupportClass cloned = (SupportClass) super.clone();
    cloned.editedElement = editedElement.deepClone();
    return cloned;
  }
}



