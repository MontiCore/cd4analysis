package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.distinguishASTCDTypeHelper;
import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.getDiffClassKindStrHelper;

/**
 * The class, abstract class, interface and enum in AST will be converted to the corresponding DiffClass
 * The kind of DiffClass are DIFF_CLASS, DIFF_ENUM, DIFF_ABSTRACT_CLASS, DIFF_INTERFACE
 *
 * @attribute originalElement:
 * store the original AST Class
 * @attribute editedElement:
 * if the class has inherited attributes, they will be added into editedElement.
 * @attribute diffLink4EnumClass:
 * This attribute is only suitable for DIFF_ENUM class.
 * If someone DiffClass uses Enum in attributes,
 * then this DiffClass name will be added into this diffLink4EnumClass attribute of corresponding DIFF_ENUM class.
 */
public class DiffClass implements Cloneable {
  protected final ASTCDType originalElement;
  protected ASTCDType editedElement;
  protected Set<String> diffLink4EnumClass = new HashSet<>();

  public DiffClass(ASTCDType originalElement) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
  }

  public String getName() {
    return getDiffClassKindStrHelper(getDiffKind()) + "_" + this.originalElement.getSymbol().getFullName();
  }

  public DifferentGroup.DiffClassKind getDiffKind() {
    return distinguishASTCDTypeHelper(originalElement);
  }

  public String getOriginalClassName() {
    return this.originalElement.getSymbol().getFullName();
  }

  public Set<String> getDiffLink4EnumClass() {
    return diffLink4EnumClass;
  }

  public void setDiffLink4EnumClass(Set<String> diffLink4EnumClass) {
    this.diffLink4EnumClass = diffLink4EnumClass;
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
  public DiffClass clone() throws CloneNotSupportedException {
    DiffClass cloned = (DiffClass) super.clone();
    cloned.editedElement = editedElement.deepClone();
    return cloned;
  }
}



