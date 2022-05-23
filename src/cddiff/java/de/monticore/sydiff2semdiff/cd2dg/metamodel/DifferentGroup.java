package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import com.google.common.graph.MutableGraph;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DifferentGroup {
  private ASTCDCompilationUnit model;
  private DifferentGroupType type;
  public Map<String, DiffClass> diffClassGroup;
  public Map<String, DiffAssociation> diffAssociationGroup;
  public MutableGraph<String> inheritanceGraph;
  public List<Set<String>> referenceGroup;

  public enum DifferentGroupType {
    SINGLE_INSTANCE, MULTI_INSTANCE
  }

  public enum DiffClassKind {
    DIFF_CLASS, DIFF_ENUM, DIFF_ABSTRACT_CLASS, DIFF_INTERFACE
  }

  public enum DiffAssociationKind {
    DIFF_ASC, DIFF_INHERIT_ASC
  }

  public enum DiffAssociationDirection {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, UNDEFINED
  }

  public enum DiffAssociationCardinality {
    ONE, ZORE_TO_ONE, ONE_TO_MORE, MORE
  }

  public DifferentGroup() {
  }

  public DifferentGroup(ASTCDCompilationUnit model, DifferentGroupType type, Map<String, DiffClass> diffClassGroup, Map<String, DiffAssociation> diffAssociationGroup, MutableGraph<String> inheritanceGraph) {
    this.model = model;
    this.type = type;
    this.diffClassGroup = diffClassGroup;
    this.diffAssociationGroup = diffAssociationGroup;
    this.inheritanceGraph = inheritanceGraph;
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

  public Map<String, DiffAssociation> getDiffAssociationGroup() {
    return diffAssociationGroup;
  }

  public void setDiffAssociationGroup(Map<String, DiffAssociation> diffAssociationGroup) {
    this.diffAssociationGroup = diffAssociationGroup;
  }

  public MutableGraph<String> getInheritanceGraph() {
    return inheritanceGraph;
  }

  public void setInheritanceGraph(MutableGraph<String> inheritanceGraph) {
    this.inheritanceGraph = inheritanceGraph;
  }
}
