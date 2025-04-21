/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cd._visitor.CDMemberVisitor;
import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd._visitor.CDMemberVisitor.Options.*;

public class ASTCDClass extends ASTCDClassTOP {

  @Override
  public List<ASTMCObjectType> getSuperclassList() {
    if (!isPresentCDExtendUsage()) {
      // Return an empty, immutable (!!) list to not skip some updates without knowledge
      return Collections.emptyList();
    }
    return getCDExtendUsage().getSuperclassList();
  }

  /**
   * Prints the name of the superclass(es) as a comma-separated string
   *
   * @return String representation of the superclasses
   */
  @Override
  public String printSuperclasses() {
    if (!isPresentCDExtendUsage()) {
      return PrettyPrintUtil.EMPTY_STRING;
    }

    return getCDExtendUsage().getSuperclassList().stream()
        .map(ASTMCObjectType::printType)
        .collect(Collectors.joining(","));
  }

  @Override
  public List<ASTMCObjectType> getInterfaceList() {
    if (!isPresentCDInterfaceUsage()) {
      // Return an empty, immutable (!!) list to not skip some updates without knowledge
      return Collections.emptyList();
    }
    return getCDInterfaceUsage().getInterfaceList();
  }

  /**
   * Prints the name of the interfaces as a comma-separated string
   *
   * @return String representation of the interfaces
   */
  @Override
  public String printInterfaces() {
    if (!isPresentCDInterfaceUsage()) {
      return PrettyPrintUtil.EMPTY_STRING;
    }
    return getCDInterfaceUsage().getInterfaceList().stream()
        .map(ASTMCObjectType::printType)
        .collect(Collectors.joining(","));
  }

  /**
   * Since the CD Generator calls this method a lot, a handwritten, much faster version is implemented, which does not
   * use the generated visitors.
   */
  @Override
  public <T extends ASTCDMember> List<T> getCDMemberList(CDMemberVisitor.Options option, CDMemberVisitor.Options... options) {
    List<CDMemberVisitor.Options> allOptions = new ArrayList<>();
    allOptions.add(option);
    allOptions.addAll(Arrays.asList(options));

    Set<T> res = new LinkedHashSet<>();

    if(allOptions.contains(ALL)){
      res.addAll((Collection<? extends T>) getCDMemberList());
    }

    if(allOptions.contains(FIELDS)){
      for (ASTCDMember m : getCDMemberList()) {
        if (m.isField()) {
          res.add((T) m);
        }
      }
    }

    if(allOptions.contains(ATTRIBUTES)){
      for (ASTCDMember m : getCDMemberList()) {
        if (m.isAttribute()) {
          res.add((T) m);
        }
      }
    }

    if(allOptions.contains(ROLES)){
      for (ASTCDMember m : getCDMemberList()) {
        if (m.isRole()) {
          res.add((T) m);
        }
      }
    }

    if(allOptions.contains(METHOD_SIGNATURES)){
      for (ASTCDMember m : getCDMemberList()) {
        if (m.isMethodSignature()) {
          res.add((T) m);
        }
      }
    }

    if(allOptions.contains(CONSTRUCTORS)){
      for (ASTCDMember m : getCDMemberList()) {
        if (m.isConstructor()) {
          res.add((T) m);
        }
      }
    }

    if(allOptions.contains(METHODS)){
      for (ASTCDMember m : getCDMemberList()) {
        if (m.isMethod()) {
          res.add((T) m);
        }
      }
    }

    return new ArrayList<>(res);
  }
}
