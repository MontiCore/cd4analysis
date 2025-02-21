<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "withCheck")}

<#if withCheck>

if(this.${attribute.getName()}!=null){
  v.add${attribute.getName()?cap_first}(this.${attribute.getName()})
}

<#else>

v.add${attribute.getName()?cap_first}(this.${attribute.getName()});

</#if>
