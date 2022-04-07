<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default CD2Java generation.

  Call it using the CLI: .. -ct cd2java.CD2Java

-->
${tc.signature("glex", "cdGenerator")}

${cdGenerator.generate(ast)}
