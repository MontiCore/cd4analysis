package de.monticore.odvalidity;

import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;

import java.util.ArrayList;
import java.util.List;

public class ODHelper {

  public static List<ASTODObject> getAllObjects(ASTObjectDiagram od) {
    List<ASTODObject> objectList = new ArrayList<>();
    for (ASTODElement element : od.getODElementList()) {
      if (element instanceof ASTODNamedObject || element instanceof ASTODAnonymousObject) {
        objectList.add((ASTODObject) element);
      }
    }
    return objectList;
  }

  public static List<ASTODLink> getAllLinks(ASTObjectDiagram od) {
    List<ASTODLink> linkList = new ArrayList<>();
    for (ASTODElement element : od.getODElementList()) {
      if (element instanceof ASTODLink) {
        linkList.add((ASTODLink) element);
      }
    }
    return linkList;
  }

}
