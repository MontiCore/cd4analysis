<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template configures a CD2Pojo generation without any defaults.

  Call it using the CLI: .. -ct cd2java.CD2Java

-->
${tc.signature("glex", "setup")}
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="setup" type="de.monticore.cdgen.CDGenSetup" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->

${setup.withDecorator("de.monticore.cdgen.decorators.GetterDecorator").applyOnName("getter").ignoreOnName("noGetter")}
${setup.withDecorator("de.monticore.cdgen.decorators.SetterDecorator").applyOnName("setter").ignoreOnName("noSetter")}
${setup.withDecorator("de.monticore.cdgen.decorators.NavigableSetterDecorator").applyOnName("setter").ignoreOnName("noSetter")}
${setup.withDecorator("de.monticore.cdgen.decorators.BuilderDecorator").applyOnName("builder").ignoreOnName("noBuilder")}
${setup.withDecorator("de.monticore.cdgen.decorators.ObserverDecorator").applyOnName("observable").ignoreOnName("notObservable")}

