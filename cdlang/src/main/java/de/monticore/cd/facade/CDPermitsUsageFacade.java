/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDPermitsUsage;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDPermitsUsageFacade {
  
  private static CDPermitsUsageFacade cdPermitsUsageFacade;
  
  private final MCTypeFacade mcTypeFacade;
  
  private CDPermitsUsageFacade() { this.mcTypeFacade = MCTypeFacade.getInstance(); }
  
  public static CDPermitsUsageFacade getInstance() {
    if (cdPermitsUsageFacade == null) {
      cdPermitsUsageFacade = new CDPermitsUsageFacade();
    }
    return cdPermitsUsageFacade;
  }
  
  public ASTCDPermitsUsage createCDPermitsUsage(final List<ASTMCObjectType> types) {
    return CD4CodeBasisMill.cDPermitsUsageBuilder().setPermittedList(types).build();
  }
  
  public ASTCDPermitsUsage createCDPermitsUsage(final ASTMCObjectType... types) {
    return createCDPermitsUsage(Arrays.asList(types));
  }
  
  public ASTCDPermitsUsage createCDPermitsUsage(final String... types) {
    return createCDPermitsUsage(Arrays.stream(types).map(mcTypeFacade::createQualifiedType).collect(
        Collectors.toList()));
  }
  
}
