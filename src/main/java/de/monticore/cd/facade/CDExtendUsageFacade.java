/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDExtendUsageFacade {

  /**
   * Class that helps with the creation of ASTCDParameter
   */

  private static CDExtendUsageFacade cdExtendUsageFacade;

  private final MCTypeFacade mcTypeFacade;

  private CDExtendUsageFacade() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  public static CDExtendUsageFacade getInstance() {
    if (cdExtendUsageFacade == null) {
      cdExtendUsageFacade = new CDExtendUsageFacade();
    }
    return cdExtendUsageFacade;
  }

  /**
   * delegation methods for a more comfortable usage
   */

  public ASTCDExtendUsage createCDExtendUsage(final List<ASTMCObjectType> types) {
    return CD4CodeBasisMill.cDExtendUsageBuilder()
        .setSuperclassList(types)
        .build();
  }

  /**
   * base method for creation of a parameter via builder
   */

  public ASTCDExtendUsage createCDExtendUsage(final ASTMCObjectType... types) {
    return createCDExtendUsage(Arrays.asList(types));
  }

  public ASTCDExtendUsage createCDExtendUsage(final String... types) {
    return createCDExtendUsage(Arrays.stream(types)
        .map(mcTypeFacade::createQualifiedType)
        .collect(Collectors.toList()));
  }
}
