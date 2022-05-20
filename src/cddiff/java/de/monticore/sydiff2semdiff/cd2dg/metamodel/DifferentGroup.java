package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.Map;

public class DifferentGroup {
  private ASTCDCompilationUnit model;
  private DifferentGroupType type;
  public Map<String, DiffClass> diffClassGroup;
  public Map<String, DiffRelation> diffRelationGroup;
  public Map<String, DiffSuperClass> diffSuperClassGroup;

  public enum DifferentGroupType {
    SINGLE_INSTANCE, MULTI_INSTANCE
  }

  public enum DiffClassKind {
    DIFF_CLASS, DIFF_ENUM, DIFF_ABSTRACT_CLASS, DIFF_INTERFACE
  }

  public enum DiffRelationKind {
    DIFF_ASC, DIFF_INHERIT_ASC, DIFF_SUPERCLASS
  }

  public enum DiffRelationNavigation {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT, MULTI
  }

  public enum DiffMultiplicities {
    ONE, ZORE_OR_ONE, ONE_TO_MORE, MORE
  }

  public DifferentGroup() {
  }

  public DifferentGroup(ASTCDCompilationUnit model, DifferentGroupType type, Map<String, DiffClass> diffClassGroup, Map<String, DiffRelation> diffRelationGroup, Map<String, DiffSuperClass> diffSuperClassGroup) {
    this.model = model;
    this.type = type;
    this.diffClassGroup = diffClassGroup;
    this.diffRelationGroup = diffRelationGroup;
    this.diffSuperClassGroup = diffSuperClassGroup;
  }

  public ASTCDCompilationUnit getModel() {
    return model;
  }

  public void setModel(ASTCDCompilationUnit model) {
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
