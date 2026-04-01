package lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitBreakStmt(Break stmt);
    R visitExpressionStmt(Expression stmt);
    R visitFunctionStmt(Function stmt);
    R visitIfStmt(If stmt);
    R visitPrintStmt(Print stmt);
    R visitReturnStmt(Return stmt);
    R visitVarStmt(Var stmt);
    R visitWhileStmt(While stmt);
  }

  static class Block extends Stmt {
    final List<Stmt> statements;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    Block(List<Stmt> statements) {
      this.statements = statements;
    }

  }

  static class Break extends Stmt {


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBreakStmt(this);
    }

    Break() {
    }

  }

  static class Expression extends Stmt {
    final Expr expression;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    Expression(Expr expression) {
      this.expression = expression;
    }

  }

  static class Function extends Stmt {
    final Token name;
    final Expr.Function function;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    Function(Token name, Expr.Function function) {
      this.name = name;
      this.function = function;
    }

  }

  static class If extends Stmt {
    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

  }

  static class Print extends Stmt {
    final Expr expression;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    Print(Expr expression) {
      this.expression = expression;
    }

  }

  static class Return extends Stmt {
    final Token keyword;
    final Expr value;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

  }

  static class Var extends Stmt {
    final Token name;
    final Expr initializer;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

  }

  static class While extends Stmt {
    final Expr condition;
    final Stmt body;


    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

  }

  abstract <R> R accept(Visitor<R> visitor);
}
