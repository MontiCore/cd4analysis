package de.monticore.odvalidity;

import de.monticore.odlink._ast.*;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class NormalizeLinksTrafo {

  /**
   * Transforms all links into unidirectional, left to right links. Bidirectional links get split
   * in two unidirectional links. All right to left oriented links get transformed into right to left
   * links. A < - > B = A->B & B-> A; B < - A = A - > B;
   * Undirected links stay as they are.
   * @param links a list of links to be transformed
   * @return A list of transformed LtR links
   */
  public List<ASTODLink> transformLinksToLTR(List<ASTODLink> links) {

    List<ASTODLink> transformed = new ArrayList<>();
    links.forEach(link -> transformed.addAll(transformLink(link)));
    return transformed;
  }

  /**
   * Transforms all links into unidirectional, left to right links. Bidirectional links get split
   * in two unidirectional links. All right to left oriented links get transformed into right to left
   * links. A < - > B = A->B & B-> A; B < - A = A - > B;
   * Undirected links are considered bidirectional.
   * @param link a link to possibly be transformed depending on its direction
   * @return A list of transformed LtR links
   */
  private List<ASTODLink> transformLink(ASTODLink link) {
    List<ASTODLink> links = new ArrayList<>();
    ASTODLinkBuilder b = new ASTODLinkBuilder();

    //set name
    if (link.isPresentName()) {
      b.setName(link.getName());
    }
    //set Type
    if (link.isLink()) {
      b.setLink(true);
    }
    else if (link.isAggregation()) {
      b.setAggregation(true);
    }
    else if (link.isComposition()) {
      b.setComposition(true);
    }

    if (link.getODLinkDirection() instanceof ASTODLeftToRightDir) {
      //nothing to do here
      return List.of(link);
    }
    else if (link.getODLinkDirection() instanceof ASTODRightToLeftDir) {
      links.add(transformRtlToLtr(link, b));
    }
    else if (link.getODLinkDirection() instanceof ASTODBiDir || link.getODLinkDirection() instanceof ASTODUnspecifiedDir) {

      b.setODLinkDirection(new ASTODLeftToRightDirBuilder().build());

      ASTODLinkLeftSideBuilder left = new ASTODLinkLeftSideBuilder();
      ASTODLinkRightSideBuilder right = new ASTODLinkRightSideBuilder();
      ASTODLinkLeftSide l = link.getODLinkLeftSide();
      ASTODLinkRightSide r = link.getODLinkRightSide();

      left.setModifier(l.getModifier());
      right.setModifier(r.getModifier());

      if (l.isPresentRole()) {
        left.setRole(l.getRole());
      }
      if (l.isPresentODLinkQualifier()) {
        left.setODLinkQualifier(l.getODLinkQualifier());
      }
      left.setReferenceNamesList(l.getReferenceNamesList());

      if (r.isPresentRole()) {
        right.setRole(r.getRole());
      }
      if (r.isPresentODLinkQualifier()) {
        right.setODLinkQualifier(r.getODLinkQualifier());
      }
      right.setReferenceNamesList(r.getReferenceNamesList());
      b.setODLinkLeftSide(left.build());
      b.setODLinkRightSide(right.build());

      ASTODLinkBuilder b2 = new ASTODLinkBuilder();

      //set Type
      if (link.isLink()) {
        b2.setLink(true);
      }
      else if (link.isAggregation()) {
        b2.setAggregation(true);
      }
      else if (link.isComposition()) {
        b2.setComposition(true);
      }

      if (b.isPresentName()) {
        String BIDIR_EXTENSION = "_BiDir";
        b2.setName(b.getName() + BIDIR_EXTENSION);
      }

      //add both separated links
      links.add(transformRtlToLtr(link, b2));
      links.add(b.build());
    }
    return links;
  }

  private ASTODLink transformRtlToLtr(ASTODLink link, ASTODLinkBuilder b) {
    b.setODLinkDirection(new ASTODLeftToRightDirBuilder().build());

    ASTODLinkLeftSideBuilder left = new ASTODLinkLeftSideBuilder();
    ASTODLinkRightSideBuilder right = new ASTODLinkRightSideBuilder();
    right.setModifier(link.getODLinkLeftSide().getModifier());
    left.setModifier(link.getODLinkRightSide().getModifier());

    if (link.getODLinkLeftSide().isPresentRole()) {
      right.setRole(link.getODLinkLeftSide().getRole());

    }
    else {
      right.setRoleAbsent();
      Log.error("Link with missing role in target direction.");
    }

    if (link.getODLinkLeftSide().isPresentODLinkQualifier()) {
      right.setODLinkQualifier(link.getODLinkLeftSide().getODLinkQualifier());
    }

    if (link.getODLinkRightSide().isPresentRole()) {
      left.setRole(link.getODLinkRightSide().getRole());
    }
    else {
      left.setRoleAbsent();
    }
    if (link.getODLinkRightSide().isPresentODLinkQualifier()) {
      left.setODLinkQualifier(link.getODLinkLeftSide().getODLinkQualifier());
    }

    //set reference lists
    left.setReferenceNamesList(link.getODLinkRightSide().getReferenceNamesList());
    right.setReferenceNamesList(link.getODLinkLeftSide().getReferenceNamesList());

    b.setODLinkRightSide(right.build())
        .setODLinkLeftSide(left.build())
        .setODLinkDirection(new ASTODLeftToRightDirBuilder().build());
    return b.build();
  }

}
