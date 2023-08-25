package de.monticore.cddiff.syndiff.OD;

import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;

import java.util.Collection;

public interface IODBuilder {
    ASTODAttribute buildAttr(String type, String name, String value);
    ASTODAttribute buildAttr(String type, String name);
    ASTODObject buildObj(String id, String type, Collection<String> types, Collection<ASTODAttribute> attrs);
    ASTODLink buildLink(ASTODObject srcObj, String roleNameSrc, String roleNameTgt, ASTODObject trgObj, String direction);
}
