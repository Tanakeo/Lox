package lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitLogicalExpr(Logical expr);
    R visitUnaryExpr(Unary expr);
    R visitVariableExpr(Variable expr);
  }

  static class Assign extends Expr {
    final Token name;
    final Expr value;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

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

  static class Logical extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }

    Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
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

  static class Variable extends Expr {
    final Token name;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    Variable(Token name) {
      this.name = name;
    }

  }

  abstract <R> R accept(Visitor<R> visitor);
}
