/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
}
