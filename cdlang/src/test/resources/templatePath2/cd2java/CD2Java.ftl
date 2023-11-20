<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template replaces the default empty body template
-->
${tc.signature("glex", "cdGenerator")}

<#assign hp = glex.templateHP("cd2java.EmptyBodyFromConfigTemplate")>
${glex.replaceTemplate("cd2java.EmptyBody", hp)}

${cdGenerator.generate(ast)}
