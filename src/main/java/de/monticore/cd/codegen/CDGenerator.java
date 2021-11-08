/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    String packageAsPath = String.join(File.separator, 
        compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getPartsList()).toLowerCase();

    this.generateCDClasses(packageAsPath, definition.getCDClassesList());
    this.generateCDInterfaces(packageAsPath, definition.getCDInterfacesList());
    this.generateCDEnums(packageAsPath, definition.getCDEnumsList());
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + JAVA_EXTENSION);
  }

  protected void generateCDClasses(String packageAsPath, List<ASTCDClass> astcdClassList) {
    for (ASTCDClass cdClass : astcdClassList) {
      Path filePath = getAsPath(packageAsPath, cdClass.getName());
      this.generatorEngine.generate(CD2JavaTemplates.CLASS, filePath, cdClass, cdClass);
    }
  }

  protected void generateCDInterfaces(String packageAsPath, List<ASTCDInterface> astcdInterfaceList) {
    for (ASTCDInterface cdInterface : astcdInterfaceList) {
      Path filePath = getAsPath(packageAsPath, cdInterface.getName());
      this.generatorEngine.generate(CD2JavaTemplates.INTERFACE, filePath, cdInterface, cdInterface);
    }
  }

  protected void generateCDEnums(String packageAsPath, List<ASTCDEnum> astcdEnumList) {
    for (ASTCDEnum cdEnum : astcdEnumList) {
      Path filePath = getAsPath(packageAsPath, cdEnum.getName());
      this.generatorEngine.generate(CD2JavaTemplates.ENUM, filePath, cdEnum, cdEnum);
    }
  }
}
