package de.monticore.cddiff.syndiff.imp;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.matcher.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyntaxDiffBuilder {
  ASTCDCompilationUnit srcCD;
  ASTCDCompilationUnit tgtCD;

  public SyntaxDiffBuilder(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD){
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
  }
}
