/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos;

import java.util.Iterator;

import com.google.common.base.Joiner;

import de.cd4analysis._ast.ASTCDAssociation;
import de.monticore.types._ast.ASTQualifiedName;

/**
 * Helper to print associations in default formats.
 *
 * @author Robert Heim
 */
public class CD4ACoCoHelper {
  /**
   * Builds a string of the following form, where "?" means optional and | means
   * alternative.
   * 
   * <pre>
   * [assocName]? ([typeA] [roleA]? [->|<-|<->|--] [roleB]? [typeB])
   * </pre>
   * 
   * @param assoc
   * @return the formatted string.
   */
  // TODO RH use PrettyPrinter
  public static String printAssociation(ASTCDAssociation assoc) {
    StringBuilder r = new StringBuilder();
    if (assoc.getName().isPresent()) {
      r.append(assoc.getName().get());
      r.append(" ");
    }
    r.append("(");
    r.append(Joiner.on(".").join(assoc.getLeftReferenceName().getParts()));
    r.append(" ");
    if (assoc.getLeftRole().isPresent()) {
      r.append("(" + assoc.getLeftRole().get() + ")");
      r.append(" ");
    }
    if (assoc.isLeftToRight()) {
      r.append("->");
    }
    else if (assoc.isRightToLeft()) {
      r.append("<-");
    }
    else if (assoc.isBidirectional()) {
      r.append("<->");
    }
    else if (assoc.isSimple()) {
      r.append("--");
    }
    r.append(" ");
    if (assoc.getRightRole().isPresent()) {
      r.append("(" + assoc.getRightRole().get() + ")");
      r.append(" ");
    }
    r.append(Joiner.on(".").join(assoc.getRightReferenceName().getParts()));
    r.append(")");
    return r.toString();
  }
  
  public static String qualifiedNameToString(ASTQualifiedName name) {
    StringBuilder sb = new StringBuilder();
    
    Iterator<String> it = name.getParts().iterator();
    while (it.hasNext()) {
      sb.append(it.next());
      if(it.hasNext()) {
        sb.append(".");
      }
    }
    return sb.toString();
  }
}