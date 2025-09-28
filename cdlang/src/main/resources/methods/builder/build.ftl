<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzName")}
if(!isValid()){
  throw new IllegalStateException("build called on an incomplete object of type ${originalClazzName}.");
}
var v = new ${originalClazzName}();
${defineHookPoint("methods.builder.build:Inner")}
return v;
