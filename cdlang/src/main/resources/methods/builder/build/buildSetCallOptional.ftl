<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "withCheck", "capitalizedAttributeName")}

<#if withCheck>


if(this.${attribute.getName()}.isPresent()){
   v.set${capitalizedAttributeName}(this.${attribute.getName()}.get());
}else{
  v.set${capitalizedAttributeName}Absent)}();
}

<#else>

if(this.${attribute.getName()}.isPresent()){
  v.set${capitalizedAttributeName}(this.${attribute.getName()}.get());
}

</#if>
