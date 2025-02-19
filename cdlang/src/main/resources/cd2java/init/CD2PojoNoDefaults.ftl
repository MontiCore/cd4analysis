<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template configures a CD2Pojo generation without any defaults.

  Call it using the CLI: .. -ct cd2java.CD2Java

-->
${tc.signature("glex", "deConf")}
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="deConf" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->

${deConf.withGetters().applyOnName("getter").ignoreOnName("noGetter")}
${deConf.withSetters().applyOnName("setter").ignoreOnName("noSetter")}
${deConf.withNavigableSetters().applyOnName("setter").ignoreOnName("noSetter")}
${deConf.withBuilders().applyOnName("builder").ignoreOnName("noBuilder")}
${deConf.withObservers().applyOnName("observable").ignoreOnName("notObservable")}

