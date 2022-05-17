package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import java.util.Map;

//enum DifferentGroupType {
//  SINGLE_INSTANCE, MULTI_INSTANCE
//}
//
//enum DiffClassKind {
//  CLASS_DIFF_KIND, ENUM_DIFF_KIND, ABSTRACT_CLASS_DIFF_KIND, SUPER_CLASS_DIFF_KIND
//}
//
//enum DiffRelationKind {
//  ASC_DIFF_KIND, INHERIT_ASC_DIFF_KIND
//}
//
//enum DiffRelationNavigation {
//  LEFT_TO_RIGHT, RIGHT_TO_LEFT, MULTI
//}
//
//enum DiffMultiplicities {
//  ONE, ZORE_OR_ONE, ONE_TO_MORE, MORE
//}

public class DifferentGroup {
  private Object model;
  private DifferentGroupType type;
  public Map<String, DiffClass> diffClassGroup;
  public Map<String, DiffRelation> diffRelationGroup;
  public Map<String, DiffSuperClass> diffSuperClassGroup;

  public enum DifferentGroupType {
    SINGLE_INSTANCE, MULTI_INSTANCE
  }

  public enum DiffClassKind {
    CLASS_DIFF_KIND, ENUM_DIFF_KIND, ABSTRACT_CLASS_DIFF_KIND, SUPER_CLASS_DIFF_KIND
  }

  public enum DiffRelationKind {
    ASC_DIFF_KIND, INHERIT_ASC_DIFF_KIND
  }

  public enum DiffRelationNavigation {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT, MULTI
  }

  public enum DiffMultiplicities {
    ONE, ZORE_OR_ONE, ONE_TO_MORE, MORE
  }

  public DifferentGroup() {
  }

  public DifferentGroup(Object model, DifferentGroupType type, Map<String, DiffClass> diffClassGroup, Map<String, DiffRelation> diffRelationGroup, Map<String, DiffSuperClass> diffSuperClassGroup) {
    this.model = model;
    this.type = type;
    this.diffClassGroup = diffClassGroup;
    this.diffRelationGroup = diffRelationGroup;
    this.diffSuperClassGroup = diffSuperClassGroup;
  }

  public Object getModel() {
    return model;
  }

  public void setModel(Object model) {
    this.model = model;
  }

  public DifferentGroupType getType() {
    return type;
  }

  public void setType(DifferentGroupType type) {
    this.type = type;
  }

  public Map<String, DiffClass> getDiffClassGroup() {
    return diffClassGroup;
  }

  public void setDiffClassGroup(Map<String, DiffClass> diffClassGroup) {
    this.diffClassGroup = diffClassGroup;
  }

  public Map<String, DiffRelation> getDiffRelationGroup() {
    return diffRelationGroup;
  }

  public void setDiffRelationGroup(Map<String, DiffRelation> diffRelationGroup) {
    this.diffRelationGroup = diffRelationGroup;
  }

  public Map<String, DiffSuperClass> getDiffSuperClassGroup() {
    return diffSuperClassGroup;
  }

  public void setDiffSuperClassGroup(Map<String, DiffSuperClass> diffSuperClassGroup) {
    this.diffSuperClassGroup = diffSuperClassGroup;
  }
}
