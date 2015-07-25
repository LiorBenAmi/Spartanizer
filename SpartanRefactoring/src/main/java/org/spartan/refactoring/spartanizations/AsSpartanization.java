package org.spartan.refactoring.spartanizations;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.spartan.utils.Range;

/**
 * An adapter which makes it possible to use a @{link Wring} as a
 * {@link Spartanization}
 *
 * @author Yossi Gil
 * @since 2015/07/10
 */
public class AsSpartanization extends Spartanization {
  final Wring inner;
  /** Instantiates this class */
  public AsSpartanization(final Wring wring) {
    super("Trimmer", "Simiplifies expression once");
    inner = wring;
  }
  @Override protected ASTVisitor collectOpportunities(final List<Range> $) {
    return new ASTVisitor() {
      @Override public boolean visit(final PrefixExpression e) {
        return true && inner.noneligible(e) ? true : addTo(e, $);
      }
      private boolean addTo(final Expression e, final List<Range> $) {
        $.add(new Range(e));
        return true;
      }
      @Override public boolean visit(final InfixExpression e) {
        return true && inner.noneligible(e) ? true : addTo(e, $);
      }
      @Override public boolean visit(final ConditionalExpression e) {
        return true && inner.noneligible(e) ? true : addTo(e, $);
      }
    };
  }
  @Override protected final void fillRewrite(final ASTRewrite r, final AST t, final CompilationUnit u, final IMarker m) {
    u.accept(new ASTVisitor() {
      @Override public boolean visit(final InfixExpression e) {
        if (!inRange(m, e))
          return true;
        return true ? inner.go(r, e) : true;
      }
      @Override public boolean visit(final PrefixExpression e) {
        if (!inRange(m, e))
          return true;
        return inner.go(r, e);
      }
      @Override public boolean visit(final ConditionalExpression e) {
        if (!inRange(m, e))
          return true;
        return inner.go(r, e);
      }
    });
  }
}
