package de.monticore;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syntax2semdiff.JavaCDDiff;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CDDiffUtil {

  protected static void computeSemDiff(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2,
      String outputPath, boolean openWorld, boolean toDir) throws NumberFormatException,
      IOException {
    CDSemantics semantics = CDSemantics.SIMPLE_CLOSED_WORLD;

    // determine if open-world should be applied
    if (openWorld) {

      CD4CodeMill.globalScope().clear();
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(ast1, ast2);

      if (toDir) {
        CDDiffUtil.saveDiffCDs2File(ast1, ast2, outputPath);
      }
      semantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    }

    if (toDir){
      JavaCDDiff.printODs2Dir(JavaCDDiff.computeSemDiff(ast1,ast2,semantics),outputPath);
    } else {
      JavaCDDiff.printSemDiff(ast1,ast2,semantics);
    }

  }

  protected static void computeSemDiff(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2,
      String outputPath, int diffsize, int difflimit,
      boolean openWorld, boolean reductionBased) throws NumberFormatException,
      IOException {


    CDSemantics semantics = CDSemantics.SIMPLE_CLOSED_WORLD;

    // determine if open-world should be applied
    if (openWorld) {

      // determine which method should be used to compute the diff-witnesses
      if (reductionBased) {
        CD4CodeMill.globalScope().clear();
        ReductionTrafo trafo = new ReductionTrafo();
        trafo.transform(ast1, ast2);

        CDDiffUtil.saveDiffCDs2File(ast1, ast2, outputPath);
        semantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
      }
      else {

        semantics = CDSemantics.MULTI_INSTANCE_OPEN_WORLD;

        // handle unspecified association directions for open-world
        ReductionTrafo.handleAssocDirections(ast1, ast2);

        // add subclasses to interfaces and abstract classes
        ReductionTrafo.addSubClasses4Diff(ast1);
        ReductionTrafo.addSubClasses4Diff(ast2);

        // add dummy-class for associations
        String dummyClassName = "Dummy4Diff";
        ReductionTrafo.addDummyClass4Associations(ast1,dummyClassName);
        ReductionTrafo.addDummyClass4Associations(ast2,dummyClassName);
      }
    }
    else {
      //handle unspecified association directions for closed-world
      ReductionTrafo.handleAssocDirections(ast1, ast2);
    }


    // compute semDiff(ast,ast2)
    Optional<AlloyDiffSolution> optS = AlloyCDDiff.cddiff(ast1, ast2, diffsize, semantics,
        outputPath);

    // test if solution is present
    if (optS.isEmpty()) {
      Log.error("0xCDD01: Could not compute semdiff.");
      return;
    }
    AlloyDiffSolution sol = optS.get();

    // limit number of generated diff-witnesses
    sol.setSolutionLimit(difflimit);
    sol.setLimited(true);

    // generate diff-witnesses in outputPath
    sol.generateSolutionsToPath(Paths.get(outputPath));
  }

  protected static int getDefaultDiffsize(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2) {
    int diffsize;
    int cd1size = ast1.getCDDefinition().getCDClassesList().size() + ast1.getCDDefinition()
        .getCDInterfacesList()
        .size();

    int cd2size = ast2.getCDDefinition().getCDClassesList().size() + ast2.getCDDefinition()
        .getCDInterfacesList()
        .size();

    diffsize = Math.max(20, 2 * Math.max(cd1size, cd2size));
    return diffsize;
  }

  protected static void saveDiffCDs2File(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2,
      String outputPath) throws IOException {
    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    ast1.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    ast2.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

    String suffix1 = "";
    String suffix2 = "";
    if (ast1.getCDDefinition().getName().equals(ast2.getCDDefinition().getName())) {
      suffix1 = "_new";
      suffix2 = "_old";
    }

    Path outputFile1 = Paths.get(outputPath, ast1.getCDDefinition().getName() + suffix1 + ".cd");
    Path outputFile2 = Paths.get(outputPath, ast2.getCDDefinition().getName() + suffix2 + ".cd");

    // Write results into a file
    FileUtils.writeStringToFile(outputFile1.toFile(), cd1, Charset.defaultCharset());
    FileUtils.writeStringToFile(outputFile2.toFile(), cd2, Charset.defaultCharset());
  }


  /**
   * This method search for a file provided by a relative path in a given git commit.
   * @param commitSha Short ID of the commit (first 8 characters)
   * @param path Relative path inside the repository
   * @return Either the file content or empty string
   */
  public static String findFileInCommit(String commitSha, String path) throws IOException {
    FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
    repositoryBuilder.readEnvironment().findGitDir();

    if (repositoryBuilder.getGitDir() != null) {
      Repository repository = new FileRepository(repositoryBuilder.getGitDir());
      RevWalk revWalk = new RevWalk(repository);
      ObjectId commitId = repository.resolve(commitSha);
      RevCommit commit = revWalk.parseCommit(commitId);
      RevTree tree = commit.getTree();
      TreeWalk treeWalk = new TreeWalk(repository);
      treeWalk.addTree(tree);
      treeWalk.setRecursive(true);

      ObjectId entryId = null;

      // Iterate through all files in current commit and check for relative path
      while (treeWalk.next()) {
        if (treeWalk.getPathString().equals(path)) {
          entryId = treeWalk.getObjectId(0);
          break;
        }
      }
      // Found the file, return its content
      if (entryId != null){
        ObjectLoader loader = repository.open(entryId);
        return new String(loader.getBytes());
      }
    }
    // No file found for given path + commit
    return path;
  }

  public static ASTCDCompilationUnit parseModelFromString(String model) {
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> opt;
    try {
      opt = parser.parse_String(model);
      //assertFalse(parser.hasErrors());
      if(!parser.hasErrors() && opt.isPresent()){
        return opt.get();
      }
    }
    catch (Exception e) {
      Log.error("0xCDD13 Failed to parse model from String", e);
    }
    return null;
  }
}
