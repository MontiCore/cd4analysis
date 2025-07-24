<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzType", "classCreationCall")}
${originalClazzType.printType()} result = ${classCreationCall};
return this.deepClone(result, map);
