<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepClone -->
<#-- this method is used to clone different attributes of the pojo class -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("mCType", "PojoClazzesAsStringList","thisObjectName", "resultObjectName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- create the result object at the very start and fill thisObject and the resultObjects in  the map -->
<#assign newResultObjectName = "newResult" + mCType.hashCode()?replace(".","")?replace(",","")>
<#-- array type -->
<#if (CD4AnalysisTypeDispatcher.isMCArrayTypesASTMCArrayType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  <#assign innerType = (mCType.getMCType())>
  <#assign counter = "counter"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    <#assign bracketString = "[]">
    <#assign arrayInit = "">
    <#list 0..mCType.getDimensions()-1 as i>
        <#assign arrayInit = arrayInit + mCType.getDimT(i)>
      </#if>
    </#list>
    ${resultObjectName} = new ${innerType.printType()}${arrayInit};
    map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
    for(int ${counter}=0;${counter}<${mCType.printType()}.length;${counter}++){
      <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newResultObjectName};
      ${innerType.printType()} ${newInnerType} = ${mCType.printType()}[${counter}];
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultObjectName)}
      ${resultObjectName}[${counter}] = (${newResultObjectName});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
ARRAYTYPE NOT IMPLEMENTED
<#-- Set types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
  <#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = new HashSet<>();
    map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
    java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
    while(${iteratorName}.hasNext()) {
      <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newResultObjectName};
      ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultObjectName)}
      ${resultObjectName}.add(${newResultObjectName});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
  <#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = new ArrayList<>();
    map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
    java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
    while(${iteratorName}.hasNext()) {
      <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newResultObjectName};
      ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultObjectName)}
      ${resultObjectName}.add(${newResultObjectName});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- Map types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCMapType(mCType))>
MAPTYPE NOT IMPLEMENTED YET
<#-- Optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  <#assign optionalResultName = "optionalResult" + mCType.hashCode()?replace(".","")?replace(",","")>
  ${mCType.printType()} ${optionalResultName} = Optional.empty();
  if(map.get(${thisObjectName}) == null) {
    if(${thisObjectName}.isPresent()) {
      <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
      <#assign newInnerType = "newInnerType" + innerType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newInnerType} = ${thisObjectName}.get();
      ${innerType.printType()} ${newResultObjectName};
      if(map.get(${newInnerType}) == null) {
        map.put(${thisObjectName}, new Object[] {${optionalResultName}, true});
        ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultObjectName)}
      }else{
        ${newResultObjectName} = (${innerType.printType()}) map.get(${newInnerType})[0];
      }
      ${resultObjectName} = Optional.of(${newResultObjectName});
    } else {
      ${resultObjectName} = Optional.empty();
      map.put(${thisObjectName}, new Object[] {${optionalResultName}, true});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- primitive types -->
<#-- can not be null -->
<#elseif (CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(mCType))>
${resultObjectName} = ${thisObjectName};
<#-- pojo class types -->
<#else>
<#-- only when the type is present in the class diagram the getDefiningSymbol is present -->
  <#if mCType.getDefiningSymbol().isPresent()>
  <#assign resolvedClassName = mCType.getDefiningSymbol().get().getFullName()>
  <#else>
     <#assign resolvedClassName = mCType.getMCQualifiedName().getQName()>
  </#if>
  <#if (PojoClazzesAsStringList?seq_contains(resolvedClassName))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = ${thisObjectName}.deepClone(map);
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- all other types -->
  <#else>

if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
<#if mCType?has_content && mCType.printType()?has_content && (mCType.printType() == "java.lang.String" || mCType.printType() == "String")>
  ${resultObjectName} = new String(${thisObjectName});
<#else>
<#-- we cannot do this correctly if we land here the user has to implement the deepClone method via the TOP-Mechanism -->
<#-- adding to the map would not contribute, as we will copy the object anyway and multiple references will still be multiple references afterwards -->
  ${resultObjectName} = ${thisObjectName};
}
</#if>
  </#if>
</#if>
