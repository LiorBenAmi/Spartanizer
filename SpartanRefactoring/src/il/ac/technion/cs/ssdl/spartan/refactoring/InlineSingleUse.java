package il.ac.technion.cs.ssdl.spartan.refactoring;

import static il.ac.technion.cs.ssdl.spartan.utils.Funcs.isFinal;
import static il.ac.technion.cs.ssdl.spartan.utils.Funcs.makeParenthesizedExpression;
import il.ac.technion.cs.ssdl.spartan.utils.Occurrences;
import il.ac.technion.cs.ssdl.spartan.utils.Range;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

/**
 * @author Artium Nihamkin (original)
 * @author Boris van Sosin <code><boris.van.sosin [at] gmail.com></code> (v2)
 * @author Tomer Zeltzer <code><tomerr90 [at] gmail.com></code> (v3)
 */
// TODO: There <b>must</b> be an option to
// * disable this warning in selected places. Consider this example: <pre>
// public
// * static &lt;T&gt; void swap(final T[] ts, final int i, final int j) { final
// T
// * t = ts[i]; ts[i] = ts[j]; ts[j] = t; } </pre> You should not inline the
// * variable t, and you should not move forward its declaration! Some
// * alternatives for disabling the warning are: First, a dedicated annotation
// of
// * name such as $Resident or
// * @Unmovable, or some other word that is single, and easy to understand.
// <pre>
// * public static &lt;T&gt; void swap(final T[] ts, final int i, final int j) {
// * &#064;Resident final T t = ts[i]; ts[i] = ts[j]; ts[j] = t; } </pre>
// Augment
// * the @SuppressWarning annotation <pre> public static &lt;T&gt; void
// swap(final
// * T[] ts, final int i, final int j) {
// * &#064;SuppressWarning(&quot;unmovable&quot;) final T t = ts[i]; ts[i] =
// * ts[j]; ts[j] = t; } </pre> Require comment <pre> public static &lt;T&gt;
// void
// * swap(final T[] ts, final int i, final int j) { final T t = ts[i]; // Don't
// * move! ts[i] = ts[j]; ts[j] = t; } </pre>
// * @since 2013/01/01
// */
public class InlineSingleUse extends Spartanization {
	/** Instantiates this class */
	public InlineSingleUse() {
		super("Inline Single Use", "Inline variable used once");
	}

	@Override protected final void fillRewrite(final ASTRewrite r, final AST t, final CompilationUnit cu, final IMarker m) {
		cu.accept(new ASTVisitor() {
			@Override public boolean visit(final VariableDeclarationFragment n) {
				if (!inRange(m, n) || !(n.getParent() instanceof VariableDeclarationStatement))
					return true;
				final SimpleName varName = n.getName();
				final VariableDeclarationStatement parent = (VariableDeclarationStatement) n.getParent();
				final List<Expression> uses = Occurrences.USES_SEMANTIC.of(varName).in(parent.getParent());
				if (1 == uses.size()
						&& (isFinal(parent) || 1 == numOfOccur(Occurrences.ASSIGNMENTS, varName, parent.getParent()))) {
					r.replace(uses.get(0), makeParenthesizedExpression(t, r, n.getInitializer()), null);
					r.remove(1 != parent.fragments().size() ? n : parent, null);
				}
				return true;
			}
		});
	}

	@Override protected ASTVisitor fillOpportunities(final List<Range> opportunities) {
		return new ASTVisitor() {
			@Override public boolean visit(final VariableDeclarationFragment node) {
				return !(node.getParent() instanceof VariableDeclarationStatement) ? true : go(node, node.getName());
			}

			private boolean go(final VariableDeclarationFragment v, final SimpleName n) {
				final VariableDeclarationStatement parent = (VariableDeclarationStatement) v.getParent();
				if (1 == numOfOccur(Occurrences.USES_SEMANTIC, n, parent.getParent())
						&& (isFinal(parent) || 1 == numOfOccur(Occurrences.ASSIGNMENTS, n, parent.getParent())))
					opportunities.add(new Range(v));
				return true;
			}
		};
	}

	static int numOfOccur(final Occurrences typeOfOccur, final Expression of, final ASTNode in) {
		return typeOfOccur == null || of == null || in == null ? -1 : typeOfOccur.of(of).in(in).size();
	}
}
