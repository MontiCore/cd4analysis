package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;

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
 *    store the original AST Class
 * @attribute editedElement:
 *    if the class has inherited attributes, they will be added into editedElement.
 * @attribute attributes:
 *    {
 *      [attributes name] : {
 *        "type" : [attribute type] // if the type is enum, then it will be "DiffEnum_[DIFF_ENUM class name]".
 *        "kind" : ["original", "inherited"]
 *      }
 *    }
 * @attribute diffLink4EnumClass:
 *    This attribute is only suitable for DIFF_ENUM class.
 *    If someone DiffClass uses Enum in attributes,
 *    then this DiffClass name will be added into this diffLink4EnumClass attribute of corresponding DIFF_ENUM class.
 */
public class DiffClass implements Cloneable{
  protected final ASTCDType originalElement;
  protected ASTCDType editedElement;

  protected Map<String, Map<String, String>> attributes = new HashMap<>();
  protected Set<String> diffLink4EnumClass = new HashSet<>();

  public DiffClass(ASTCDType originalElement) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();

    if (originalElement.getClass().equals(ASTCDEnum.class)) {
      Map<String, Map<String, String>> attributesMap = new HashMap<>();
      for (ASTCDEnumConstant astcdEnumConstant : ((ASTCDEnum) originalElement).getCDEnumConstantList()) {
        attributesMap.put(astcdEnumConstant.getName(), null);
      }
      this.attributes = attributesMap;
    }
  }

  public String getName() {
    return getDiffClassKindStrHelper(getDiffKind()) + "_" + this.originalElement.getName();
  }

  public DifferentGroup.DiffClassKind getDiffKind() {
    return distinguishASTCDTypeHelper(originalElement);
  }

  public String getOriginalClassName() {
    return this.originalElement.getName();
  }

  public Set<String> getDiffLink4EnumClass() {
    return diffLink4EnumClass;
  }

  public void setDiffLink4EnumClass(Set<String> diffLink4EnumClass) {
    this.diffLink4EnumClass = diffLink4EnumClass;
  }

  public Map<String, Map<String, String>> getAttributes() {
    return attributes;
  }

  public Map<String, String> getAttributeByASTCDAttribute(ASTCDAttribute astcdAttribute) {
    return attributes.get(astcdAttribute.getName());
  }

  public void addAttribute(ASTCDAttribute astcdAttribute, boolean isEnumType, boolean isInherited) {
    if (!this.editedElement.getCDAttributeList()
      .stream()
      .anyMatch(e -> e.getName().equals(astcdAttribute.getName())
        && e.printType().equals(astcdAttribute.printType()))) {
      this.editedElement.addCDMember(astcdAttribute);
    }

    Map<String, String> valueMap = new HashMap<>();
    String type = isEnumType ? "DiffEnum_" + astcdAttribute.printType() : astcdAttribute.printType();
    String kind = isInherited ? "inherited" : "original";
    valueMap.put("type", type);
    valueMap.put("kind", kind);

    this.attributes.put(astcdAttribute.getName(), valueMap);
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



