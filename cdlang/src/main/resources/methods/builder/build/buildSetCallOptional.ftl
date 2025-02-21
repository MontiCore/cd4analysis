<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "withCheck")}

<#if withCheck>

if(this.${attribute.getName()}.isPresent()){
   v.set${attribute.getName()?cap_first}(this.${attribute.getName()}.get());
}else{
  v.set${attribute.getName()?cap_first}Absent();
}

<#else>

if(this.${attribute.getName()}.isPresent()){
  v.set${attribute.getName()?cap_first}(this.${attribute.getName()}.get());
}

</#if>
