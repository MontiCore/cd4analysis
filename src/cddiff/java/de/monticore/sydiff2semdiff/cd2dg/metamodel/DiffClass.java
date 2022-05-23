package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import de.monticore.cdbasis._ast.ASTCDType;

import java.util.List;
import java.util.Map;
import java.util.Set;
public class DiffClass implements Cloneable{
  public String name;
  public DifferentGroup.DiffClassKind diffKind;
  public Set<String> diffClassName;
  public Set<String> diffLink4EnumClass;
  public List<String> diffParents;
  public Map<String, Map<String, String>> attributes;
  public ASTCDType originalElement;

  public DiffClass() {
  }

  public DiffClass(String name, DifferentGroup.DiffClassKind diffKind, Set<String> diffClassName, Set<String> diffLink4EnumClass, List<String> diffParents, Map<String, Map<String, String>> attributes, ASTCDType originalElement) {
    this.name = name;
    this.diffKind = diffKind;
    this.diffClassName = diffClassName;
    this.diffLink4EnumClass = diffLink4EnumClass;
    this.diffParents = diffParents;
    this.attributes = attributes;
    this.originalElement = originalElement;
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

  public Set<String> getDiffClassName() {
    return diffClassName;
  }

  public void setDiffClassName(Set<String> diffClassName) {
    this.diffClassName = diffClassName;
  }

  public Set<String> getDiffLink4EnumClass() {
    return diffLink4EnumClass;
  }

  public void setDiffLink4EnumClass(Set<String> diffLink4EnumClass) {
    this.diffLink4EnumClass = diffLink4EnumClass;
  }

  public List<String> getDiffParents() {
    return diffParents;
  }

  public void setDiffParents(List<String> diffParents) {
    this.diffParents = diffParents;
  }

  public Map<String, Map<String, String>> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Map<String, String>> attributes) {
    this.attributes = attributes;
  }

  public ASTCDType getOriginalElement() {
    return originalElement;
  }

  public void setOriginalElement(ASTCDType originalElement) {
    this.originalElement = originalElement;
  }

  @Override
  public DiffClass clone() throws CloneNotSupportedException {
    return (DiffClass) super.clone();
  }
}



