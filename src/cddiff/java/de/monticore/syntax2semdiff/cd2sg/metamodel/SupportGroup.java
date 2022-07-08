package de.monticore.syntax2semdiff.cd2sg.metamodel;

import com.google.common.graph.MutableGraph;
import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * For each CD a corresponding SupportGroup will be generated.
 *
 * @attribute model:
 *    store original ASTCDCompilationUnit
 * @attribute type:
 *    SINGLE_INSTANCE or MULTI_INSTANCE
 * @attribute supportClassGroup:
 *    store generated SupportClass
 * @attribute supportAssociationGroup:
 *    store generated SupportAssociation
 * @attribute refSetAssociationList:
 *    store generated SupportRefSetAssociation
 * @attribute inheritanceGraph:
 *    store all inheritance informations that presents all the parents and children of a class.
 */
public class SupportGroup {
  protected ASTCDCompilationUnit model;
  protected CDSemantics type;
  protected Map<String, SupportClass> supportClassGroup;
  protected Map<String, SupportAssociation> supportAssociationGroup;
  protected List<SupportRefSetAssociation> refSetAssociationList = new ArrayList<>();
  protected MutableGraph<String> inheritanceGraph;

  public enum SupportClassKind {
    SUPPORT_CLASS, SUPPORT_ENUM, SUPPORT_ABSTRACT_CLASS, SUPPORT_INTERFACE
  }

  public enum SupportAssociationKind {
    SUPPORT_ASC, SUPPORT_INHERIT_ASC
  }

  public enum SupportAssociationDirection {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, UNDEFINED
  }

  public enum SupportAssociationCardinality {
    ONE, ZERO_TO_ONE, ONE_TO_MORE, MORE
  }

  public SupportGroup() {
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

  public Map<String, SupportClass> getSupportClassGroup() {
    return supportClassGroup;
  }

  public void setSupportClassGroup(Map<String, SupportClass> supportClassGroup) {
    this.supportClassGroup = supportClassGroup;
  }

  public Map<String, SupportAssociation> getSupportAssociationGroup() {
    return supportAssociationGroup;
  }

  public void setSupportAssociationGroup(Map<String, SupportAssociation> supportAssociationGroup) {
    this.supportAssociationGroup = supportAssociationGroup;
  }

  public List<SupportRefSetAssociation> getRefSetAssociationList() {
    return refSetAssociationList;
  }

  public void setRefSetAssociationList(List<SupportRefSetAssociation> refSetAssociationList) {
    this.refSetAssociationList = refSetAssociationList;
  }

  public MutableGraph<String> getInheritanceGraph() {
    return inheritanceGraph;
  }

  public void setInheritanceGraph(MutableGraph<String> inheritanceGraph) {
    this.inheritanceGraph = inheritanceGraph;
  }
}
