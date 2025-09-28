<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzName")}
var v = new ${originalClazzName}();
${defineHookPoint("methods.builder.unsafeBuild:Inner")}
return v;
