package de.monticore.syntax2semdiff.cdsyntaxdiff2od.metamodel;

import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odlink._ast.ASTODLink;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ASTODPack {
  private List<ASTODNamedObject> namedObjects = new LinkedList<>();

  private List<ASTODLink> links = new LinkedList<>();

  public ASTODPack() {
  }

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
    // remove links duplicate
    for (int i = 0; i < this.links.size(); i++) {
      for (int j = i + 1; j < this.links.size(); j++) {
        if (this.links.get(i).deepEquals(this.links.get(j))) {
          this.links.remove(j);
        }
      }
    }
    List<ASTODElement> astodElementList = new ArrayList<>();
    astodElementList.addAll(this.namedObjects);
    astodElementList.addAll(this.links);
    return astodElementList;
  }

}

