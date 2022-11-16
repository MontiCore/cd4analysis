/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.reporting;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDQualifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.types.MCCollectionTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import java.util.List;

/** TODO: Write me! */
public class CD4ANodeIdentHelper extends MCCollectionTypesNodeIdentHelper {

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
    String type = Layouter.nodeName(a);
    return format(a.getName(), type);
  }

  public String getIdent(ASTCDParameter a) {
    String type = Layouter.nodeName(a);
    String name = a.getName();
    return format(name, type);
  }

  public String getIdent(ASTCDAttribute a) {
    return format(a.getName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTStereotype a) {
    List<ASTStereoValue> l = a.getValuesList();
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
    if (a.isPresentName()
        && a.getLeftReferenceName() != null
        && a.getRightReferenceName() != null) {
      ident =
          a.getLeftReferenceName().toString()
              + "-"
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
    } else if (a instanceof ASTCDMethod) {
      return getIdent((ASTCDMethod) a);
    } else if (a instanceof ASTCDConstructor) {
      return getIdent((ASTCDConstructor) a);
    } else if (a instanceof ASTCDInterface) {
      return getIdent((ASTCDInterface) a);
    } else if (a instanceof ASTCDQualifier) {
      return getIdent((ASTCDQualifier) a);
    } else if (a instanceof ASTCDParameter) {
      return getIdent((ASTCDParameter) a);
    } else if (a instanceof ASTMCPrimitiveType) {
      return getIdent((ASTMCPrimitiveType) a);
    } else if (a instanceof ASTMCQualifiedName) {
      return getIdent((ASTMCQualifiedName) a);
    } else if (a instanceof ASTCDAttribute) {
      return getIdent((ASTCDAttribute) a);
    } else if (a instanceof ASTMCObjectType) {
      return getIdent((ASTMCObjectType) a);
    } else if (a instanceof ASTStereotype) {
      return getIdent((ASTStereotype) a);
    } else if (a instanceof ASTStereoValue) {
      return getIdent((ASTStereoValue) a);
    } else if (a instanceof ASTModifier) {
      return getIdent((ASTModifier) a);
    } else if (a instanceof ASTCDClass) {
      return getIdent((ASTCDClass) a);
    } else if (a instanceof ASTCDAssociation) {
      return getIdent((ASTCDAssociation) a);
    } else if (a instanceof ASTCDEnum) {
      return getIdent((ASTCDEnum) a);
    } else if (a instanceof ASTCDEnumConstant) {
      return getIdent((ASTCDEnumConstant) a);
    } else if (a instanceof ASTNatLiteral) {
      return getIdent((ASTNatLiteral) a);
    } else {
      String type = Layouter.className(a);
      return format(type);
    }
  }
}
