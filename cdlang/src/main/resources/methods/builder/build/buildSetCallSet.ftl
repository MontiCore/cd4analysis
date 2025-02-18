<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "withCheck", "capitalizedAttributeName")}

<#if withCheck>

if(this.${attribute.getName()}!=null){
  v.add${capitalizedAttributeName}(this.${attribute.getName()})
}

<#else>

v.add${capitalizedAttributeName}(this.${attribute.getName()});

</#if>
