package lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
    R visitTernaryExpr(Ternary expr);
  }

  static class Binary extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

  }

  static class Grouping extends Expr {
    final Expr expression;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    Grouping(Expr expression) {
      this.expression = expression;
    }

  }

  static class Literal extends Expr {
    final Object value;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    Literal(Object value) {
      this.value = value;
    }

  }

  static class Unary extends Expr {
    final Token operator;
    final Expr right;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

  }

  static class Ternary extends Expr {
    final Expr condition;
    final Expr thenBranch;
    final Expr elseBranch;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitTernaryExpr(this);
    }

    Ternary(Expr condition, Expr thenBranch, Expr elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

  }

  abstract <R> R accept(Visitor<R> visitor);
}
