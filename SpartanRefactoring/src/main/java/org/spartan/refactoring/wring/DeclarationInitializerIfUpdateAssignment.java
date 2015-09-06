package org.spartan.refactoring.wring;

import static org.spartan.refactoring.utils.Funcs.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEditGroup;
import org.spartan.refactoring.utils.Extract;
import org.spartan.refactoring.utils.Subject;
import org.spartan.refactoring.wring.LocalNameReplacer.LocalNameReplacerWithValue;

/**
 * A {@link Wring} to convert <code>int a = 2;
 * if (b)
 *   a = 3;</code> into <code>int a = b ? 3 : 2;</code>
 *
 * @author Yossi Gil
 * @since 2015-08-07
 */
public final class DeclarationInitializerIfUpdateAssignment extends Wring.VariableDeclarationFragementAndStatement {
  @Override ASTRewrite go(final ASTRewrite r, final VariableDeclarationFragment f, final SimpleName n, final Expression initializer, final Statement nextStatement,
      final TextEditGroup g) {
    if (initializer == null)
      return null;
    final IfStatement s = asIfStatement(nextStatement);
    if (s == null || !Wrings.emptyElse(s))
      return null;
    s.setElseStatement(null);
    final Expression condition = s.getExpression();
    final Assignment a = Extract.assignment(then(s));
    if (a == null || !same(left(a), n) || doesUseForbiddenSiblings(f, condition, right(a)))
      return null;
    final Operator o = a.getOperator();
    if (o == Assignment.Operator.ASSIGN)
      return null;
    final ConditionalExpression newInitializer = Subject.pair(assignmentAsExpression(a), initializer).toCondition(condition);
    final LocalNameReplacerWithValue i = new LocalNameReplacer(n, r, g).usingInitializer(initializer);
    if (!i.canInlineInto(newInitializer))
      return null;
    r.replace(initializer, newInitializer, g);
    i.inlineInto(then(newInitializer), newInitializer.getExpression());
    r.remove(s, g);
    return r;
  }
  @Override public String description(final VariableDeclarationFragment f) {
    return "Consolidate initialization of " + f.getName() + " with the subsequent conditional assignment to it";
  }
}