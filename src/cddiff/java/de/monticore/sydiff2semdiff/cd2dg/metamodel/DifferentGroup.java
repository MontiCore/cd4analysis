package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import com.google.common.graph.MutableGraph;
import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * For each CD a corresponding DifferentGroup will be generated.
 *
 * @attribute model:
 *    store original ASTCDCompilationUnit
 * @attribute type:
 *    SINGLE_INSTANCE or MULTI_INSTANCE
 * @attribute diffClassGroup:
 *    store generated DiffClass
 * @attribute diffAssociationGroup:
 *    store generated DiffAssociation
 * @attribute refSetAssociationList:
 *    store generated DiffRefSetAssociation
 * @attribute inheritanceGraph:
 *    store all inheritance informations that presents all the parents and children of a class.
 */
public class DifferentGroup {
  protected ASTCDCompilationUnit model;
  protected CDSemantics type;
  protected Map<String, DiffClass> diffClassGroup;
  protected Map<String, DiffAssociation> diffAssociationGroup;
  protected List<DiffRefSetAssociation> refSetAssociationList = new ArrayList<>();
  protected MutableGraph<String> inheritanceGraph;

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

  public ASTCDCompilationUnit getModel() {
    return model;
  }

  public void setModel(ASTCDCompilationUnit model) {
    this.model = model;
  }

  public CDSemantics getType() {
    return type;
  }

  public void setType(CDSemantics type) {
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

  public List<DiffRefSetAssociation> getRefSetAssociationList() {
    return refSetAssociationList;
  }

  public void setRefSetAssociationList(List<DiffRefSetAssociation> refSetAssociationList) {
    this.refSetAssociationList = refSetAssociationList;
  }

  public MutableGraph<String> getInheritanceGraph() {
    return inheritanceGraph;
  }

  public void setInheritanceGraph(MutableGraph<String> inheritanceGraph) {
    this.inheritanceGraph = inheritanceGraph;
  }
}
