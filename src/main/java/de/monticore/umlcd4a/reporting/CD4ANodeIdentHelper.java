/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.reporting;

import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.types.TypesNodeIdentHelper;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCardinality;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereotype;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @since TODO: add version number
 */
public class CD4ANodeIdentHelper extends TypesNodeIdentHelper {

  public String getIdent(ASTCDDefinition a) {
    String type = Layouter.nodeName(a);
    String name = a.getName();
    return format(name, type);
  }

  public String getIdent(ASTCDMethod a) {
    String type = Layouter.nodeName(a);
    String name = a.getName();
    return format(name, type);
  }

  public String getIdent(ASTCDConstructor a) {
    String type = Layouter.nodeName(a);
    String name = a.getName();
    return format(name, type);
  }

  public String getIdent(ASTCDInterface a) {
    String type = Layouter.nodeName(a);
    String name = a.getName();
    return format(name, type);
  }

  public String getIdent(ASTCDQualifier a) {
    // TODO AR <- GV: default value?
    String type = Layouter.nodeName(a);
    String name = a.getName().isPresent() ? a.getName().get() : "";
    return format(name, type);
  }

  public String getIdent(ASTCDParameter a) {
    String type = Layouter.nodeName(a);
    String name = a.getName();
    return format(name, type);
  }

  public String getIdent(ASTCardinality a) {
    String cardinality = "";

    if (a.isOne()) {
      cardinality = "1";
    }
    else if (a.isMany()) {
      cardinality = "*";
    }
    else if (a.isOneToMany()) {
      cardinality = "1..*";
    }
    else if (a.isOptional()) {
      cardinality = "0..1";
    }

    return format(cardinality, Layouter.nodeName(a));
  }

  public String getIdent(ASTCDAttribute a) {
    return format(a.getName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTStereotype a) {
	List<ASTStereoValue> l = a.getValues();
    String n = "-";
    if (l != null && l.size() > 0) {
      n = l.get(0).getName();
    }
    if (l != null && n != null && l.size() > 1) {
      n += "..";
    }
    return format(n, Layouter.nodeName(a));
  }

  public String getIdent(ASTStereoValue a) {
    return format(a.getName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTModifier a) {
    return format("_", Layouter.nodeName(a));
  }

  public String getIdent(ASTCDClass a) {
    return format(a.getName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTCDAssociation a) {
    String ident = "";
    java.util.Optional<String> n = a.getName();
    if (n.isPresent() && a.getLeftReferenceName() != null
        && a.getRightReferenceName() != null) {
      ident = unqualName(a.getLeftReferenceName()) + "-"
          + Layouter.unqualName(a.getRightReferenceName().toString());
    }
    return format(ident, Layouter.nodeName(a));
  }


  public String getIdent(ASTCDEnum a) {
    return format(a.getName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTCDEnumConstant a) {
    return format(a.getName(), Layouter.nodeName(a));
  }



  @Override
  public String getIdent(ASTNode a) {
    if (a instanceof ASTCDDefinition) {
      return getIdent((ASTCDDefinition) a);
    }
    else if (a instanceof ASTCDMethod) {
      return getIdent((ASTCDMethod) a);
    }
    else if (a instanceof ASTCDConstructor) {
      return getIdent((ASTCDConstructor) a);
    }
    else if (a instanceof ASTCDInterface) {
      return getIdent((ASTCDInterface) a);
    }
    else if (a instanceof ASTCDQualifier) {
      return getIdent((ASTCDQualifier) a);
    }
    else if (a instanceof ASTCDParameter) {
      return getIdent((ASTCDParameter) a);
    }
    else if (a instanceof ASTPrimitiveType) {
      return getIdent((ASTPrimitiveType) a);
    }
    else if (a instanceof ASTCardinality) {
      return getIdent((ASTCardinality) a);
    }
    else if (a instanceof ASTQualifiedName) {
      return getIdent((ASTQualifiedName) a);
    }
    else if (a instanceof ASTCDAttribute) {
      return getIdent((ASTCDAttribute) a);
    }
    else if (a instanceof ASTSimpleReferenceType) {
      return getIdent((ASTSimpleReferenceType) a);
    }
    else if (a instanceof ASTStereotype) {
      return getIdent((ASTStereotype) a);
    }
    else if (a instanceof ASTStereoValue) {
      return getIdent((ASTStereoValue) a);
    }
    else if (a instanceof ASTModifier) {
      return getIdent((ASTModifier) a);
    }
    else if (a instanceof ASTTypeParameters) {
      return getIdent((ASTTypeParameters) a);
    }
    else if (a instanceof ASTCDClass) {
      return getIdent((ASTCDClass) a);
    }
    else if (a instanceof ASTCDAssociation) {
      return getIdent((ASTCDAssociation) a);
    }
    else if (a instanceof ASTCDEnum) {
      return getIdent((ASTCDEnum) a);
    }
    else if (a instanceof ASTCDEnumConstant) {
      return getIdent((ASTCDEnumConstant) a);
    }
    else if (a instanceof ASTIntLiteral) {
      return getIdent((ASTIntLiteral) a);
    }
    else if (a instanceof ASTIntLiteral) {
      return getIdent((ASTIntLiteral) a);
    }
    else {
      String type = Layouter.className(a);
      return format(type);
    }
  }

}
