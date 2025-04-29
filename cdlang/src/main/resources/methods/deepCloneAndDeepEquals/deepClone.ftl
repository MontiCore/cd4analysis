<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzName")}
  return this.deepClone(new ${originalClazzName}Builder().unsafeBuild(), new HashMap<>());
