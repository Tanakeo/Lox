package lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitCallExpr(Call expr);
    R visitGetExpr(Get expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitFunctionExpr(Function expr);
    R visitLogicalExpr(Logical expr);
    R visitSetExpr(Set expr);
    R visitThisExpr(This expr);
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

  static class Call extends Expr {
    final Expr callee;
    final Token paren;
    final List<Expr> arguments;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }

    Call(Expr callee, Token paren, List<Expr> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

  }

  static class Get extends Expr {
    final Expr object;
    final Token name;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGetExpr(this);
    }

    Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
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

  static class Function extends Expr {
    final List<Token> parameters;
    final List<Stmt> body;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionExpr(this);
    }

    Function(List<Token> parameters, List<Stmt> body) {
      this.parameters = parameters;
      this.body = body;
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

  static class Set extends Expr {
    final Expr object;
    final Token name;
    final Expr value;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSetExpr(this);
    }

    Set(Expr object, Token name, Expr value) {
      this.object = object;
      this.name = name;
      this.value = value;
    }

  }

  static class This extends Expr {
    final Token keyword;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitThisExpr(this);
    }

    This(Token keyword) {
      this.keyword = keyword;
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
