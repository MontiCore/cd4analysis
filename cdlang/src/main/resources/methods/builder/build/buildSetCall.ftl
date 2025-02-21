<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "withCheck")}

<#if withCheck>

v.set${attribute.getName()?cap_first}(this.${attribute.getName()});

<#else>

v.set${attribute.getName()?cap_first}(this.${attribute.getName()});

</#if>
