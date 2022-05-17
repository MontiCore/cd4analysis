package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import java.util.List;
import java.util.Map;
import java.util.Set;
public class DiffClass {
  public String name;
  public DifferentGroup.DiffClassKind diffKind;
  public Set<Object> diffClassName;
  public Set<Object> diffLink4EnumClass;
  public List<Object> diffParents;
  public List<Object> diffChildren;
  public Map<String, Map<String, Object>> attributes;

  public DiffClass() {
  }

  public DiffClass(String name, DifferentGroup.DiffClassKind diffKind, Set<Object> diffClassName, Set<Object> diffLink4EnumClass, List<Object> diffParents, List<Object> diffChildren, Map<String, Map<String, Object>> attributes) {
    this.name = name;
    this.diffKind = diffKind;
    this.diffClassName = diffClassName;
    this.diffLink4EnumClass = diffLink4EnumClass;
    this.diffParents = diffParents;
    this.diffChildren = diffChildren;
    this.attributes = attributes;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DifferentGroup.DiffClassKind getDiffKind() {
    return diffKind;
  }

  public void setDiffKind(DifferentGroup.DiffClassKind diffKind) {
    this.diffKind = diffKind;
  }

  public Set<Object> getDiffClassName() {
    return diffClassName;
  }

  public void setDiffClassName(Set<Object> diffClassName) {
    this.diffClassName = diffClassName;
  }

  public Set<Object> getDiffLink4EnumClass() {
    return diffLink4EnumClass;
  }

  public void setDiffLink4EnumClass(Set<Object> diffLink4EnumClass) {
    this.diffLink4EnumClass = diffLink4EnumClass;
  }

  public List<Object> getDiffParents() {
    return diffParents;
  }

  public void setDiffParents(List<Object> diffParents) {
    this.diffParents = diffParents;
  }

  public List<Object> getDiffChildren() {
    return diffChildren;
  }

  public void setDiffChildren(List<Object> diffChildren) {
    this.diffChildren = diffChildren;
  }

  public Map<String, Map<String, Object>> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Map<String, Object>> attributes) {
    this.attributes = attributes;
  }
}



