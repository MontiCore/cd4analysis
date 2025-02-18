<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "withCheck", "capitalizedAttributeName")}

<#if withCheck>

v.set${capitalizedAttributeName}(this.${attribute.getName()});

<#else>

v.set${capitalizedAttributeName}(this.${attribute.getName()});

</#if>
