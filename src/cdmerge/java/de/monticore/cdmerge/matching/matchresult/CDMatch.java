package de.monticore.cdmerge.matching.matchresult;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

import java.util.List;
import java.util.Map;

/**
 * This data structure stores all matching ASTNodes of two class diagrams
 */
public class CDMatch {

  private List<ASTCDDefinition> cds;

  private ASTMatchGraph<ASTCDType, ASTCDDefinition> matchedTypes;

  private ASTMatchGraph<ASTCDClass, ASTCDDefinition> matchedClasses;

  private ASTMatchGraph<ASTCDEnum, ASTCDDefinition> matchedEnums;

  private ASTMatchGraph<ASTCDInterface, ASTCDDefinition> matchedInterfaces;

  private ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> matchedAssociations;

  private Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> matchedAttributes;

  public CDMatch(List<ASTCDDefinition> list) {
    this.cds = list;
  }

  public ASTMatchGraph<ASTCDType, ASTCDDefinition> getMatchedTypes() {
    return matchedTypes;
  }

  public void setMatchedTypes(ASTMatchGraph<ASTCDType, ASTCDDefinition> matchedTypes) {
    this.matchedTypes = matchedTypes;
  }

  public ASTMatchGraph<ASTCDClass, ASTCDDefinition> getMatchedClasses() {
    return matchedClasses;
  }

  public void setMatchedClasses(ASTMatchGraph<ASTCDClass, ASTCDDefinition> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  public ASTMatchGraph<ASTCDEnum, ASTCDDefinition> getMatchedEnums() {
    return matchedEnums;
  }

  public void setMatchedEnums(ASTMatchGraph<ASTCDEnum, ASTCDDefinition> matchedEnums) {
    this.matchedEnums = matchedEnums;
  }

  public ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> getMatchedAssociations() {
    return matchedAssociations;
  }

  public void setMatchedAssociations(
      ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> matchedAssociations) {
    this.matchedAssociations = matchedAssociations;
  }

  public ASTMatchGraph<ASTCDAttribute, ASTCDClass> getMatchedAttributes(String classname) {
    if (this.matchedAttributes != null && this.matchedAttributes.containsKey(classname)) {
      return this.matchedAttributes.get(classname);
    }
    throw new IllegalArgumentException(
        "No class with name " + classname + " found in any of the classdiagrams");

  }

  public void setMatchedAttributes(
      Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> matchedAttributes) {
    this.matchedAttributes = matchedAttributes;
  }

  public Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> getMatchedAttributes() {
    return this.matchedAttributes;
  }

  public List<ASTCDDefinition> getCdDefinitions() {
    return this.cds;
  }

  public void setMatcheInterfaces(
      ASTMatchGraph<ASTCDInterface, ASTCDDefinition> matchedInterfaces) {
    this.matchedInterfaces = matchedInterfaces;
  }

  public ASTMatchGraph<ASTCDInterface, ASTCDDefinition> getMatchedInterfaces() {
    return this.matchedInterfaces;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== BEGIN CD Match Result ===\n");
    sb.append("== Type Matches: ==\n");
    if (this.matchedTypes != null)
      sb.append(this.matchedTypes.toString());
    sb.append("== Attribute Matches: ==\n");
    if (this.matchedAttributes != null)
      this.matchedAttributes.values().forEach(mg -> sb.append(mg));
    sb.append("== Association Matches: ==\n");
    if (this.matchedAssociations != null)
      sb.append(this.matchedAssociations.toString());
    sb.append("=== END CD Match Result ===\n");
    return sb.toString();
  }

}
