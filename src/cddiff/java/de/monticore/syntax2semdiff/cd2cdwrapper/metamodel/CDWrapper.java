package de.monticore.syntax2semdiff.cd2cdwrapper.metamodel;

import com.google.common.graph.MutableGraph;
import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * For each CD a corresponding CDWrapper will be generated.
 *
 * @attribute model: store original ASTCDCompilationUnit
 * @attribute type: SINGLE_INSTANCE or MULTI_INSTANCE
 * @attribute cDTypeWrapperGroup: store generated CDTypeWrapper
 * @attribute cDAssociationWrapperGroup: store generated CDAssociationWrapper
 * @attribute refSetAssociationList: store generated CDRefSetAssociationWrapper
 * @attribute inheritanceGraph: store all inheritance informations that presents all the parents and
 * children of a class.
 */
public class CDWrapper {
  protected ASTCDCompilationUnit model;

  protected CDSemantics type;

  protected Map<String, CDTypeWrapper> cDTypeWrapperGroup;

  protected Map<String, CDAssociationWrapper> cDAssociationWrapperGroup;

  protected List<CDRefSetAssociationWrapper> refSetAssociationList = new ArrayList<>();

  protected MutableGraph<String> inheritanceGraph;

  public enum CDTypeWrapperKind {
    CDWRAPPER_CLASS, CDWRAPPER_ENUM, CDWRAPPER_ABSTRACT_CLASS, CDWRAPPER_INTERFACE
  }

  public enum CDAssociationWrapperKind {
    CDWRAPPER_ASC, CDWRAPPER_INHERIT_ASC
  }

  public enum CDAssociationWrapperDirection {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, UNDEFINED
  }

  public enum CDAssociationWrapperCardinality {
    ONE, ZERO_TO_ONE, ONE_TO_MORE, MORE
  }

  public CDWrapper() {
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

  public Map<String, CDTypeWrapper> getCDTypeWrapperGroup() {
    return cDTypeWrapperGroup;
  }

  public void setCDTypeWrapperGroup(Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    this.cDTypeWrapperGroup = cDTypeWrapperGroup;
  }

  public Map<String, CDAssociationWrapper> getCDAssociationWrapperGroup() {
    return cDAssociationWrapperGroup;
  }

  public void setCDAssociationWrapperGroup(
      Map<String, CDAssociationWrapper> cDAssociationWrapperGroup) {
    this.cDAssociationWrapperGroup = cDAssociationWrapperGroup;
  }

  public List<CDRefSetAssociationWrapper> getRefSetAssociationList() {
    return refSetAssociationList;
  }

  public void setRefSetAssociationList(List<CDRefSetAssociationWrapper> refSetAssociationList) {
    this.refSetAssociationList = refSetAssociationList;
  }

  public MutableGraph<String> getInheritanceGraph() {
    return inheritanceGraph;
  }

  public void setInheritanceGraph(MutableGraph<String> inheritanceGraph) {
    this.inheritanceGraph = inheritanceGraph;
  }

}
