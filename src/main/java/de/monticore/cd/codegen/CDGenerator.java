/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CDGenerator {

  protected static final String JAVA_EXTENSION = ".java";

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;

  public CDGenerator(GeneratorSetup generatorSetup) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    CD4C.init(setup);
    CD4C.getInstance().setEmptyBodyTemplate("cd2java.EmptyBody");
  }

  public void generate(ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition definition = compilationUnit.getCDDefinition();
    for (ASTCDPackage astPackage : definition.getCDPackagesList()) {
      String packageAsPath = String.join(File.separator, astPackage.getMCQualifiedName().getPartsList())
        .toLowerCase(Locale.ROOT);

      this.generateCDClasses(
        packageAsPath,
        astPackage,
        astPackage.getCDElementList().stream()
          .filter(e -> e instanceof ASTCDClass)
          .map(e -> ((ASTCDClass) e))
          .collect(Collectors.toList())
      );

      this.generateCDInterfaces(
        packageAsPath,
        astPackage,
        astPackage.getCDElementList().stream()
          .filter(e -> e instanceof ASTCDInterface)
          .map(e -> ((ASTCDInterface) e))
          .collect(Collectors.toList()));

      this.generateCDEnums(
        packageAsPath,
        astPackage,
        astPackage.getCDElementList().stream()
          .filter(e -> e instanceof ASTCDEnum)
          .map(e -> ((ASTCDEnum) e))
          .collect(Collectors.toList()));
    }
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + JAVA_EXTENSION);
  }

  protected void generateCDClasses(String packageAsPath, ASTCDPackage astcdPackage, List<ASTCDClass> astcdClassList) {
    for (ASTCDClass cdClass : astcdClassList) {
      Path filePath = getAsPath(packageAsPath, cdClass.getName());
      this.generatorEngine.generate(CD2JavaTemplates.CLASS, filePath, cdClass, astcdPackage);
    }
  }

  protected void generateCDInterfaces(String packageAsPath, ASTCDPackage astcdPackage, List<ASTCDInterface> astcdInterfaceList) {
    for (ASTCDInterface cdInterface : astcdInterfaceList) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      this.generatorEngine.generate(CD2JavaTemplates.INTERFACE, filePath, cdInterface, astcdPackage);
    }
  }

  protected void generateCDEnums(String packageAsPath, ASTCDPackage astcdPackage, List<ASTCDEnum> astcdEnumList) {
    for (ASTCDEnum cdEnum : astcdEnumList) {
      Path filePath = getAsPath(packageAsPath, cdEnum.getName());
      this.generatorEngine.generate(CD2JavaTemplates.ENUM, filePath, cdEnum, astcdPackage);
    }
  }
}
