package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.Collection;

public interface IODBuilder<Obj, Attr, Link> {
  Attr buildAttr(String type, String name, ASTExpression value);
  Obj buildObj(String id, String type, Collection<String> types, Collection<Attr> attrs);
  Link buildLink(Obj srcObj, String roleName, Obj trgObj);
}
