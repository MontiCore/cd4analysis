package de.monticore.cddiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odlink._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import net.sourceforge.plantuml.Log;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JoinLinksTrafo {

  protected ASTCDCompilationUnit cd;

  protected ICD4CodeArtifactScope scope;

  protected final MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(
      new IndentPrinter());

  public JoinLinksTrafo(ASTCDCompilationUnit cd) {
    this.cd = cd;
    this.scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(this.cd);
  }

  public void transform(ASTODArtifact od) {
    for (ASTCDAssociation assoc : cd.getCDDefinition().getCDAssociationsList()) {
      if (assoc.getCDAssocDir().isBidirectional() || !(
          assoc.getCDAssocDir().isDefinitiveNavigableLeft() || assoc.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
        transformLinks4Assoc(od, assoc);
      }
    }
  }

  protected void transformLinks4Assoc(ASTODArtifact od, ASTCDAssociation assoc) {
    Set<ASTODLink> links = new HashSet<>();
    for (ASTODElement element : od.getObjectDiagram().getODElementList()) {
      if (element instanceof ASTODLink) {
        links.add((ASTODLink) element);
      }
    }
    for (ASTODLink link : links) {
      if (matchLink2Assoc(link, assoc, od)) {
        if (link.getODLinkDirection() instanceof ASTODLeftToRightDir) {
          link.setODLinkDirection(OD4ReportMill.oDBiDirBuilder().build());
          if (!link.getODLinkLeftSide().isPresentRole()) {
            link.getODLinkLeftSide().setRole(CDQNameHelper.inferRole(assoc.getLeft()));
          }
        }
        else if (link.getODLinkDirection() instanceof ASTODRightToLeftDir) {
          od.getObjectDiagram().removeODElement(link);
        }
      }
      else if (matchLink2AssocInReverse(link, assoc, od)) {
        if (link.getODLinkDirection() instanceof ASTODRightToLeftDir) {
          link.setODLinkDirection(OD4ReportMill.oDBiDirBuilder().build());
          if (!link.getODLinkRightSide().isPresentRole()) {
            link.getODLinkRightSide().setRole(CDQNameHelper.inferRole(assoc.getLeft()));
          }
        }
        else if (link.getODLinkDirection() instanceof ASTODLeftToRightDir) {
          od.getObjectDiagram().removeODElement(link);
        }
      }
    }
  }

  protected boolean matchLink2Assoc(ASTODLink link, ASTCDAssociation assoc, ASTODArtifact od) {
    Optional<ASTODNamedObject> leftObj = findObjectInOD(link.getLeftReferenceNames().get(0), od);
    Optional<ASTODNamedObject> rightObj = findObjectInOD(link.getRightReferenceNames().get(0), od);

    if (leftObj.isEmpty() || rightObj.isEmpty()) {
      Log.error(
          "0xCDD11: Could not find named objects: " + link.getLeftReferenceNames().get(0) + ", "
              + link.getRightReferenceNames().get(0));
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(assoc.getLeftQualifiedName().getQName(),
        leftObj.get().getMCObjectType().printType(pp), scope)) {
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(assoc.getRightQualifiedName().getQName(),
        rightObj.get().getMCObjectType().printType(pp), scope)) {
      return false;
    }

    return ((!link.getODLinkLeftSide().isPresentRole() || link.getODLinkLeftSide()
        .getRole()
        .equals(CDQNameHelper.inferRole(assoc.getLeft())))
        && (!link.getODLinkRightSide().isPresentRole() || link.getODLinkRightSide()
        .getRole()
        .equals(CDQNameHelper.inferRole(assoc.getRight()))));
  }


  protected boolean matchLink2AssocInReverse(ASTODLink link, ASTCDAssociation assoc,
      ASTODArtifact od) {
    Optional<ASTODNamedObject> leftObj = findObjectInOD(link.getLeftReferenceNames().get(0), od);
    Optional<ASTODNamedObject> rightObj = findObjectInOD(link.getRightReferenceNames().get(0), od);

    if (leftObj.isEmpty() || rightObj.isEmpty()) {
      Log.error(
          "0xCDD12: Could not find named objects: " + link.getLeftReferenceNames().get(0) + ", "
              + link.getRightReferenceNames().get(0));
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(assoc.getRightQualifiedName().getQName(),
        leftObj.get().getMCObjectType().printType(pp), scope)) {
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(assoc.getLeftQualifiedName().getQName(),
        rightObj.get().getMCObjectType().printType(pp), scope)) {
      return false;
    }

    return ((!link.getODLinkLeftSide().isPresentRole() || link.getODLinkLeftSide()
        .getRole()
        .equals(CDQNameHelper.inferRole(assoc.getRight())))
        && (!link.getODLinkRightSide().isPresentRole() || link.getODLinkRightSide()
        .getRole()
        .equals(CDQNameHelper.inferRole(assoc.getLeft()))));
  }

  protected Optional<ASTODNamedObject> findObjectInOD(String name, ASTODArtifact od) {
    Set<ASTODNamedObject> objects = new HashSet<>();
    for (ASTODElement element : od.getObjectDiagram().getODElementList()) {
      if (element instanceof ASTODNamedObject) {
        objects.add((ASTODNamedObject) element);
      }
    }
    return objects.stream().filter(obj -> obj.getName().equals(name)).findAny();
  }





}
