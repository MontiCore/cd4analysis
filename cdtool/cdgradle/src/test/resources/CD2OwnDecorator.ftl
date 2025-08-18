<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template configures the default CD2Pojo generation.

  Call it using the CLI: .. -ct cd2java.CD2Pojo

-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="decConfig" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="genSetup" type="de.monticore.generating.GeneratorSetup" -->

<#--Apply the default creator: Copy the original CD and use it as the base-->
${decConfig.withCopyCreator().defaultApply()}

<#--It is possible to add your own decorators via this config template too: -->
<#-- We just have to add the class to the classpath of the CDTool (see CDGenGradlePluginTest class) -->
${decConfig.withDecorator("mc.MyOwnDecorator").defaultApply()}



