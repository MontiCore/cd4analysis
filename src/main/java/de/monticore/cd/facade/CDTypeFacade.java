/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDTypeFacade {

  /**
   * Class that helps with the creation of ASTCDType
   */

  private static CDTypeFacade cdTypeFacade;

  private final MCTypeFacade mcTypeFacade;

  private CDTypeFacade() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  public static CDTypeFacade getInstance() {
    if (cdTypeFacade == null) {
      cdTypeFacade = new CDTypeFacade();
    }
    return cdTypeFacade;
  }

  /**
   * delegation methods for a more comfortable usage
   */

  /******************************************************************************************/
  /*                    CDClass                                                             */
  /******************************************************************************************/

  public ASTCDClass createCDClass(final String name) {
    return CD4CodeMill.cDClassBuilder().setName(name).build();
  }

  public ASTCDClass createCDClass(final String name, List<String> interfaces) {
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(interfaces.stream().map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDClass createCDClass(final String name, String ... interfaces) {
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(Arrays.stream(interfaces).map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDClass createCDClass(final String name, String superClass, List<String> interfaces) {
    ASTCDExtendUsage cdExtend = CD4CodeBasisMill.cDExtendUsageBuilder()
      .addSuperclass(mcTypeFacade.createQualifiedType(superClass)).build();
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(interfaces.stream().map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDExtendUsage(cdExtend).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDClass createCDClass(final String name, String superClass, String ... interfaces) {
    ASTCDExtendUsage cdExtend = CD4CodeBasisMill.cDExtendUsageBuilder()
      .addSuperclass(mcTypeFacade.createQualifiedType(superClass)).build();
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(Arrays.stream(interfaces).map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDExtendUsage(cdExtend).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDClass createCDClass(final String name, final ASTMCObjectType superClass, List<ASTMCObjectType> interfaces) {
    ASTCDExtendUsage cdExtend = CD4CodeBasisMill.cDExtendUsageBuilder()
      .addSuperclass(superClass).build();
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(interfaces).build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDExtendUsage(cdExtend).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDClass createCDClass(final String name, final ASTMCObjectType superClass, ASTMCObjectType ... interfaces) {
    ASTCDExtendUsage cdExtend = CD4CodeBasisMill.cDExtendUsageBuilder()
      .addSuperclass(superClass).build();
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(Arrays.asList(interfaces)).build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDExtendUsage(cdExtend).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDClass createCDClass(final String name, String superClass) {
    ASTCDExtendUsage cdExtend = CD4CodeBasisMill.cDExtendUsageBuilder()
      .addSuperclass(mcTypeFacade.createQualifiedType(superClass))
      .build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDExtendUsage(cdExtend).build();
  }

  public ASTCDClass createCDClass(final String name, final ASTMCObjectType superClass) {
    ASTCDExtendUsage cdExtend = CD4CodeBasisMill.cDExtendUsageBuilder()
      .addSuperclass(superClass)
      .build();
    return CD4CodeMill.cDClassBuilder().setName(name).setCDExtendUsage(cdExtend).build();
  }

  /******************************************************************************************/
  /*                    CDInterface                                                         */
  /******************************************************************************************/

  public ASTCDInterface createCDInterface(final String name) {
    return CD4CodeMill.cDInterfaceBuilder().setName(name).build();
  }

  public ASTCDInterface createCDInterface(final String name, List<String> interfaces) {
    ASTCDExtendUsage extendUsage = CD4CodeMill.cDExtendUsageBuilder().
      addAllSuperclass(interfaces.stream().map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDInterfaceBuilder().setName(name).setCDExtendUsage(extendUsage).build();
  }

  public ASTCDInterface createCDInterface(final String name, String ... interfaces) {
    ASTCDExtendUsage extendUsage = CD4CodeMill.cDExtendUsageBuilder().
      addAllSuperclass(Arrays.stream(interfaces).map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDInterfaceBuilder().setName(name).setCDExtendUsage(extendUsage).build();
  }

  public ASTCDInterface createCDInterface(final String name, ASTMCObjectType ... interfaces) {
    ASTCDExtendUsage cdExtend = CD4CodeBasisMill.cDExtendUsageBuilder()
      .addAllSuperclass(Arrays.asList(interfaces)).build();
    return CD4CodeMill.cDInterfaceBuilder().setName(name).setCDExtendUsage(cdExtend).build();
  }

  /******************************************************************************************/
  /*                    CDEnum                                                              */
  /******************************************************************************************/

  public ASTCDEnum createCDEnum(final String name) {
    return CD4CodeMill.cDEnumBuilder().setName(name).build();
  }

  public ASTCDEnum createCDEnum(final String name, List<String> interfaces) {
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(interfaces.stream().map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDEnumBuilder().setName(name).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDEnum createCDEnum(final String name, String ... interfaces) {
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().
      addAllInterface(Arrays.stream(interfaces).map(x -> mcTypeFacade.createQualifiedType(x)).collect(Collectors.toList())).build();
    return CD4CodeMill.cDEnumBuilder().setName(name).setCDInterfaceUsage(interfaceUsage).build();
  }

  public ASTCDEnum createCDEnum(final String name, ASTMCObjectType ... interfaces) {
    ASTCDInterfaceUsage interfaceUsage = CD4CodeBasisMill.cDInterfaceUsageBuilder()
      .addAllInterface(Arrays.asList(interfaces)).build();
    return CD4CodeMill.cDEnumBuilder().setName(name).setCDInterfaceUsage(interfaceUsage).build();
  }

}
