package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

import com.google.common.graph.MutableGraph;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * For each CD a corresponding CDWrapper will be generated.
 *
 * @attribute model: store original ASTCDCompilationUnit
 * @attribute type: SINGLE_INSTANCE or MULTI_INSTANCE
 * @attribute cDTypeWrapperGroup: store generated CDTypeWrapper
 * @attribute cDAssociationWrapperGroup: store generated CDAssociationWrapper
 * @attribute refSetAssociationList: store generated CDRefSetAssociationWrapper
 * @attribute inheritanceGraph: store all inheritance information that presents all the parents and
 *     children of a class.
 */
public class CDWrapper {
  protected ASTCDCompilationUnit model;

  protected CDSemantics type;

  protected Map<String, CDTypeWrapper> cDTypeWrapperGroup;

  protected Map<String, CDAssociationWrapper> cDAssociationWrapperGroup;

  protected List<CDRefSetAssociationWrapper> refSetAssociationList = new ArrayList<>();

  protected MutableGraph<String> inheritanceGraph;

  public CDWrapper() {}

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

  public Map<String, CDTypeWrapper> getCDTypeWrapperGroupOnlyWithStatusOPEN() {
    return cDTypeWrapperGroup.entrySet().stream()
        .filter(map -> map.getValue().getStatus() == CDStatus.OPEN)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public void setCDTypeWrapperGroup(Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    this.cDTypeWrapperGroup = cDTypeWrapperGroup;
  }

  public Map<String, CDAssociationWrapper> getCDAssociationWrapperGroup() {
    return cDAssociationWrapperGroup;
  }

  public Map<String, CDAssociationWrapper> getCDAssociationWrapperGroupOnlyWithStatusOPEN() {
    return cDAssociationWrapperGroup.entrySet().stream()
        .filter(map -> map.getValue().getStatus() == CDStatus.OPEN)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
