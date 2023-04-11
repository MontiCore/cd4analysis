/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel;

import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odlink._ast.ASTODLink;
import java.util.*;

public class ASTODPack {
  private List<ASTODNamedObject> namedObjects = new LinkedList<>();

  private List<ASTODLink> links = new LinkedList<>();

  public ASTODPack() {}

  public ASTODPack(List<ASTODNamedObject> namedObjects, List<ASTODLink> links) {
    this.namedObjects = namedObjects;
    this.links = links;
  }

  public List<ASTODNamedObject> getNamedObjects() {
    return namedObjects;
  }

  public void setNamedObjects(List<ASTODNamedObject> namedObjects) {
    this.namedObjects = namedObjects;
  }

  public List<ASTODLink> getLinks() {
    return links;
  }

  public void setLinks(List<ASTODLink> links) {
    this.links = links;
  }

  public boolean addNamedObject(ASTODNamedObject namedObject) {
    return this.namedObjects.add(namedObject);
  }

  public boolean addLink(ASTODLink link) {
    return this.links.add(link);
  }

  public boolean extendNamedObjects(List<ASTODNamedObject> namedObjects) {
    return this.namedObjects.addAll(namedObjects);
  }

  public boolean extendLinks(List<ASTODLink> links) {
    return this.links.addAll(links);
  }

  public boolean isEmpty() {
    return this.links.isEmpty() && this.namedObjects.isEmpty();
  }

  public List<ASTODElement> getASTODElementList() {
    // remove objects duplicate
    Map<String, ASTODNamedObject> objectsMap = new HashMap<>();
    this.namedObjects.forEach(e -> objectsMap.put(OD4ReportMill.prettyPrint(e, true), e));

    // remove links duplicate
    Map<String, ASTODLink> linksMap = new HashMap<>();
    this.links.forEach(e -> linksMap.put(OD4ReportMill.prettyPrint(e, true), e));

    List<ASTODElement> astODElementList = new ArrayList<>();
    astODElementList.addAll(objectsMap.values());
    astODElementList.addAll(linksMap.values());
    return astODElementList;
  }
}
