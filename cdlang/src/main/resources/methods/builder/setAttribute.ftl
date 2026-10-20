<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "hasSetter")}
<#assign attributeType = TypeCheck3.symTypeFromAST(attribute.getMCType())>

<#if SymTypeRelations.isBoolean(attributeType)>
  <#if hasSetter>
v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
  <#else>
v.${attribute.getName()} = this.${attribute.getName()};
  </#if>
<#------------------------------------>
  <#else>
    <#if (MCCollectionSymTypeRelations.isList(attributeType) || MCCollectionSymTypeRelations.isSet(attributeType))>
      <#if hasSetter>
if(this.${attribute.getName()}!=null){
  v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
}
      <#else>
if(this.${attribute.getName()}!=null){
  v.${attribute.getName()} = this.${attribute.getName()};
}
      </#if>
<#------------------------------------>
    <#else>
      <#if MCCollectionSymTypeRelations.isOptional(attributeType)>
        <#if hasSetter>
if(this.${attribute.getName()} != null && this.${attribute.getName()}.isPresent()){
  v.set${attribute.getName()?cap_first}(this.${attribute.getName()}.get());
}else{
  v.set${attribute.getName()?cap_first}(null);
}
      <#else>
if(this.${attribute.getName()} != null && this.${attribute.getName()}.isPresent()){
  v.${attribute.getName()} = this.${attribute.getName()};
}else{
  v.${attribute.getName()} = Optional.empty();
}
      </#if>
<#------------------------------------>
    <#else>
      <#if hasSetter>
v.set${attribute.getName()?cap_first}(this.${attribute.getName()});
      <#else>
v.${attribute.getName()} = this.${attribute.getName()};
      </#if>
<#------------------------------------>
    </#if>
  </#if>
</#if>
