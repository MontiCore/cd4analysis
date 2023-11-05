package de.monticore.cddiff.cdsyntax2semdiff.odgen;

import de.monticore.cddiff.cdsyntax2semdiff.datastructures.AssocDirection;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis.ODBasisMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink.ODLinkMill;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODLinkBuilder;
import de.monticore.odlink._ast.ASTODLinkLeftSideBuilder;
import de.monticore.odlink._ast.ASTODLinkRightSideBuilder;
import de.se_rwth.commons.logging.Log;
import java.util.*;

/**
 * This class is used to generate elements for object diagrams.
 */
public class ODBuilder implements IODBuilder {
  /**
   * Creates a new attribute with a given value. This is used only when a constant is added
   * to an enumeration.
   * @param type The type of the attribute.
   * @param name The name of the attribute.
   * @param value The value of the attribute.
   * @return The created attribute.
   */
  @Override
  public ASTODAttribute buildAttr(String type, String name, String value) {
    Optional<ASTODAttribute> attribute = Optional.empty();
    try {
      attribute =
          OD4ReportMill.parser().parse_StringODAttribute(type + " " + name + "=" + value + ";");
    } catch (Exception exception) {
      Log.error("Attributes couldn't be created");
    }
    assert Objects.requireNonNull(attribute).isPresent();
    return attribute.get();
  }

  /**
   * Creates a new attribute without a value.
   * @param type The type of the attribute.
   * @param name The name of the attribute.
   * @return The created attribute.
   */
  //TODO: there is a case when an enum uses this function, but it should use the one above
  @Override
  public ASTODAttribute buildAttr(String type, String name) {
    Optional<ASTODAttribute> attribute = Optional.empty();
    try {
      attribute = OD4ReportMill.parser().parse_StringODAttribute(type + " " + name + ";");
    } catch (Exception exception) {
      Log.error("Attributes couldn't be created");
    }
    assert Objects.requireNonNull(attribute).isPresent();
    return attribute.get();
  }

  /**
   * Create a new ASTODObject.
   * @param id The name of the object.
   * @param type The base type of the object.
   * @param types The superclasses of the object.
   * @param attrs All attributes that the class has to contain (from superclasses too).
   * @return The created object.
   */
  @Override
  public ASTODObject buildObj(
      String id, String type, Collection<String> types, Collection<ASTODAttribute> attrs) {
    ASTODNamedObjectBuilder objectBuilder =
        ODBasisMill.oDNamedObjectBuilder()
            .setModifier(ODBasisMill.modifierBuilder().build())
            .setName(id);

    objectBuilder.setName(id);

    objectBuilder.setModifier(
        OD4ReportMill.modifierBuilder()
            .setStereotype(
                OD4ReportMill.stereotypeBuilder()
                    .addValues(
                        OD4ReportMill.stereoValueBuilder()
                            .setName("instanceof")
                            .setContent(String.join(", ", types))
                            .setText(
                                OD4ReportMill.stringLiteralBuilder()
                                    .setSource(String.join(", ", types))
                                    .build())
                            .build())
                    .build())
            .build());

    objectBuilder.setMCObjectType(
        ODBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(
                ODBasisMill.mCQualifiedNameBuilder()
                    .setPartsList(Collections.singletonList(type))
                    .build())
            .build());

    objectBuilder.setODAttributesList(new ArrayList<>(attrs));
    return objectBuilder.build();
  }

  /**
   * Create a new link between two objects.
   * @param srcObj Left object.
   * @param roleNameSrc Role on the left object.
   * @param roleNameTgt Role on the right object.
   * @param trgObj Right object.
   * @param direction The direction of the link.
   * @return The created link.
   */
  @Override
  public ASTODLink buildLink(
      ASTODObject srcObj,
      String roleNameSrc,
      String roleNameTgt,
      ASTODObject trgObj,
      AssocDirection direction) {
    ASTODLinkBuilder linkBuilder = ODLinkMill.oDLinkBuilder();

    ASTODLinkLeftSideBuilder leftSideBuilder =
        ODLinkMill.oDLinkLeftSideBuilder()
            .setModifier(ODBasisMill.modifierBuilder().build())
            .setODLinkQualifierAbsent()
            .setRole(roleNameSrc);
    ASTODLinkRightSideBuilder rightSideBuilder =
        ODLinkMill.oDLinkRightSideBuilder()
            .setModifier(ODBasisMill.modifierBuilder().build())
            .setODLinkQualifierAbsent()
            .setRole(roleNameTgt);

    ASTODNameBuilder nameBuilder = ODBasisMill.oDNameBuilder().setName(srcObj.getName());
    ASTODNameBuilder nameBuilder1 = ODBasisMill.oDNameBuilder().setName(trgObj.getName());

    leftSideBuilder.setReferenceNamesList(Collections.singletonList(nameBuilder.build()));
    rightSideBuilder.setReferenceNamesList(Collections.singletonList(nameBuilder1.build()));

    linkBuilder.setODLinkLeftSide(leftSideBuilder.build());
    linkBuilder.setODLinkRightSide(rightSideBuilder.build());

    if (direction == AssocDirection.BiDirectional)
      linkBuilder.setODLinkDirection(ODLinkMill.oDBiDirBuilder().build()); // bidirektional
    else if (direction == AssocDirection.LeftToRight) {
      linkBuilder.setODLinkDirection(
          ODLinkMill.oDLeftToRightDirBuilder().build());
    } else if (direction == AssocDirection.RightToLeft) {
      linkBuilder.setODLinkDirection(
          ODLinkMill.oDRightToLeftDirBuilder().build());
    }

    linkBuilder.setLink(true); // nur links

    return linkBuilder.build();
  }
}
