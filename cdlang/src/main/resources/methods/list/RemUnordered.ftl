<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
${defineHookPoint("Setter:Before")}
var __ret = this.${attribute.getName()}.remove(${attribute.getName()});
${defineHookPoint("Setter:After")}
return __ret;
