<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template configures a CD2Pojo generation without any defaults.

  Call it using the CLI: .. -ct cd2java.CD2Java

-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="decConfig" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="genSetup" type="de.monticore.generating.GeneratorSetup" -->

${decConfig.withGetters().applyOnName("getter").ignoreOnName("noGetter")}
${decConfig.withDefaultsForCardinalityAttrs().applyOnName("defaultCardinality").ignoreOnName("noDefaultCardinality")}
${decConfig.withSetters().applyOnName("setter").ignoreOnName("noSetter")}
${decConfig.withNavigableSetters().applyOnName("setter").ignoreOnName("noSetter")}
${decConfig.withAbstractMethodSignatures().applyOnName("abstractMethod").ignoreOnName("nonAbstractMethod").ignoreOnName("impl")}
${decConfig.withBuilders().applyOnName("builder").ignoreOnName("noBuilder")}
${decConfig.withObservers().applyOnName("observable").ignoreOnName("notObservable")}
${decConfig.withMethodImplementations().applyOnName("impl").ignoreOnName("noImpl")}
${decConfig.withVisitors().applyOnName("visitor").ignoreOnName("noVisitor")}
${decConfig.withVisitorImplementations().applyOnName("visitor").ignoreOnName("noVisitor").ignoreOnName("noDefaultVisitor")}
${decConfig.withMethodImplementations().applyOnName("impl").ignoreOnName("noImpl")}

