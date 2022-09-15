package de.monticore.cd2smt;

import com.microsoft.z3.Context;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cd2smt.context.ODContext;
import de.monticore.cd2smt.smt2odgenerator.SMT2ODGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.odbasis._ast.ASTODArtifact;

import java.util.Optional;

public class CD2ODGenerator {
  protected Optional<ASTODArtifact> cd2od(ASTCDCompilationUnit astcdCompilationUnit, Context context) {
    //generate a class Context from the class diagram
    CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator() ;
    CDContext cdContext = cd2SMTGenerator.cd2smt( astcdCompilationUnit,context);

    //build a  ODContext (OD SMTObjectSet) from the CDContext
    ODContext odContext = new ODContext(cdContext,astcdCompilationUnit.getCDDefinition()) ;

    //finally build the OD from the ODContext
    SMT2ODGenerator smt2ODGenerator = new SMT2ODGenerator() ;
    return Optional.of(smt2ODGenerator.buildOd(odContext)) ;
  }

}
