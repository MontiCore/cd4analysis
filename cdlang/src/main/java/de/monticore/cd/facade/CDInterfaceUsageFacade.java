/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDInterfaceUsageFacade {

  /** Class that helps with the creation of ASTCDParameter */
  private static CDInterfaceUsageFacade cdInterfaceUsageFacade;

  private final MCTypeFacade mcTypeFacade;

  private CDInterfaceUsageFacade() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  public static CDInterfaceUsageFacade getInstance() {
    if (cdInterfaceUsageFacade == null) {
      cdInterfaceUsageFacade = new CDInterfaceUsageFacade();
    }
    return cdInterfaceUsageFacade;
  }

  /** delegation methods for a more comfortable usage */
  public ASTCDInterfaceUsage createCDInterfaceUsage(final List<ASTMCObjectType> types) {
    return CD4CodeBasisMill.cDInterfaceUsageBuilder().setInterfaceList(types).build();
  }

  /** base method for creation of a parameter via builder */
  public ASTCDInterfaceUsage createCDInterfaceUsage(final ASTMCObjectType... types) {
    return createCDInterfaceUsage(Arrays.asList(types));
  }

  public ASTCDInterfaceUsage createCDInterfaceUsage(final String... types) {
    return createCDInterfaceUsage(
        Arrays.stream(types).map(mcTypeFacade::createQualifiedType).collect(Collectors.toList()));
  }
}
