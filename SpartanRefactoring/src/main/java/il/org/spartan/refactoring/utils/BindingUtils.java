package il.org.spartan.refactoring.utils;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.dom.Bindings;

/**
 * Some useful utility functions used for binding manipulations.
 * 
 * @author Ori Roth <code><ori.rothh [at] gmail.com></code>
 * @since 2016-04-24
 */
@SuppressWarnings("restriction")
public class BindingUtils {
  /**
   * @return current package. Uses the {@link CompilationUnit} saved at
   *         {@link Source}
   */
  public static IPackageBinding getPackage() {
    return Source.getCompilationUnit().getPackage().resolveBinding();
  }
  /**
   * @param n
   *          an {@link ASTNode}
   * @return the type in which n is placed, or null if there is none
   */
  public static ITypeBinding getClass(ASTNode n) {
    while (n != null && !(n instanceof TypeDeclaration))
      n = n.getParent();
    return n == null ? null : ((TypeDeclaration) n).resolveBinding();
  }
  /**
   * Determines whether an invocation of a method is legal in a specific
   * context.
   * 
   * @param m
   *          a method
   * @param n
   *          the context in which the method is invoked.
   * @return
   */
  public static boolean isVisible(IMethodBinding m, ASTNode n) {
    int ms = m.getModifiers();
    if (Modifier.isPublic(ms))
      return true;
    ITypeBinding mc = m.getDeclaringClass();
    if (Modifier.isProtected(ms) && mc.getPackage().equals(getPackage()))
      return true;
    ITypeBinding nc = getClass(n);
    if (nc != null && nc.equals(mc))
      return true;
    return false;
  }
  /**
   * Finds visible method in hierarchy.
   * 
   * @param t
   *          base type
   * @param mn
   *          method name
   * @param ps
   *          method parameters
   * @param n
   *          original {@link ASTNode} containing the method invocation. Used in
   *          order to determine the context in which the method is being used
   * @return the method's binding if it is visible from context, else null
   */
  public static IMethodBinding getVisibleMethod(ITypeBinding t, String mn, ITypeBinding[] ps, ASTNode n) {
    IMethodBinding $ = Bindings.findMethodInHierarchy(t, mn, ps);
    return $ == null || !isVisible($, n) ? null : $;
  }
}