<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template replaces the default empty body template
-->
${tc.signature("glex", "cdGenerator")}

<#assign hp = tc.instantiate("de.monticore.generating.templateengine.TemplateHookPoint", ["cd2java.EmptyBodyFromConfigTemplate"])>
${glex.replaceTemplate("cd2java.EmptyBody", hp)}

${cdGenerator.generate(ast)}
