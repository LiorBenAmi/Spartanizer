package il.ac.technion.cs.ssdl.spartan.refactoring;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Boris van Sosin
 * 
 * @since 2013/07/01
 */
public class SpartanizationFactory {
  /**
   * adds all the spartanization types to the factory
   */
  public static void initialize() {
    final BasicSpartanization redundantEquality = new BasicSpartanization(new RedundantEqualityRefactoring(), "Redundant Equality",
        "Convert reduntant comparison to boolean constant");
    spartanizations.put(redundantEquality.toString(), redundantEquality);
    final BasicSpartanization convertToTernary = new BasicSpartanization(new ConvertToTernaryRefactoring(), "Convert to Ternary",
        "Convert condition to ternary expression");
    spartanizations.put(convertToTernary.toString(), convertToTernary);
    final BasicSpartanization shortestBranchFirst = new BasicSpartanization(new ShortestBranchRefactoring(), "Shortest Branch",
        "Shortest branch in condition first");
    spartanizations.put(shortestBranchFirst.toString(), shortestBranchFirst);
    final BasicSpartanization inlineSinlgeUse = new BasicSpartanization(new InlineSingleUseRefactoring(), "Inline Single Use",
        "Inline single use of variable");
    spartanizations.put(inlineSinlgeUse.toString(), inlineSinlgeUse);
    final BasicSpartanization forwardDeclaration = new BasicSpartanization(new ForwardDeclarationRefactoring(),
        "Forward Declaration", "Forward declaration of variable to first use");
    spartanizations.put(forwardDeclaration.toString(), forwardDeclaration);
    final BasicSpartanization changeReturnToDollar = new BasicSpartanization(new ChangeReturnToDollarRefactoring(),
        "Change Return Variable to $", "Change return variable to $");
    spartanizations.put(changeReturnToDollar.toString(), changeReturnToDollar);
  }
  
  /**
   * @param SpartanizationName
   *          the name of the spartanization
   * @return an instance of the spartanization
   */
  public static BasicSpartanization getSpartanizationByName(final String SpartanizationName) {
    return spartanizations.get(SpartanizationName);
  }
  
  /**
   * @return all the registered spartanization refactoring objects
   */
  public static Iterable<BasicSpartanization> all() {
    return spartanizations.values();
  }
  
  private static final Map<String, BasicSpartanization> spartanizations = new HashMap<String, BasicSpartanization>();
}
