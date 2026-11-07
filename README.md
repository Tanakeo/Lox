# ☕ Lox: A Tree-Walk Programming Language Interpreter

A complete, zero-dependency interpreter for the **Lox** programming language, written from scratch in Java.

This project implements the full tree-walk interpreter specification from Robert Nystrom's acclaimed book [*Crafting Interpreters*](https://craftinginterpreters.com/), while going several steps further by tackling end-of-chapter challenges—including **first-class anonymous functions (lambdas)**, **statically verified loop `break` statements**, **strict uninitialized variable detection**, and **parser error productions**.

---

## ⚡ Quickstart

### Prerequisites
- Java Development Kit (JDK 11 or higher; tested on JDK 21)
- Git

### Build & Run
```bash
# 1. Clone the repository
git clone https://github.com/Tanakeo/Lox.git
cd Lox

# 2. Compile all source files into the out directory
javac -d out src/lox/*.java

# 3. Start the interactive REPL
java -cp out lox.Lox

# Or execute a Lox script directly
java -cp out lox.Lox scripts/fibonacci.lox
```

---

## 💻 What Does Lox Look Like?

Lox is a dynamically typed, object-oriented, expression-friendly scripting language with C-like syntax and Scheme-inspired lexical closures.

Here is a quick tour of what this interpreter handles:

```lox
// 1. First-Class Functions, Closures & Anonymous Lambdas
fun makeCounter() {
  var count = 0;
  return fun() {
    count = count + 1;
    return count;
  };
}

var counter = makeCounter();
print counter(); // 1
print counter(); // 2

// Higher-order functions with anonymous lambdas (Challenge feature!)
fun thrice(fn) {
  for (var i = 1; i <= 3; i = i + 1) {
    fn(i);
  }
}

thrice(fun(n) {
  print n * 10;
}); // 10, 20, 30

// 2. Control Flow with 'break' support
var sum = 0;
for (var i = 1; i < 100; i = i + 1) {
  sum = sum + i;
  if (sum > 50) break; // Exits loop cleanly
}
print sum; // 55

// 3. Object-Oriented Programming (Classes, Methods, 'this', and Inheritance)
class Pastry {
  init(flavor) {
    this.flavor = flavor;
  }

  describe() {
    print "A delicious " + this.flavor + " pastry.";
  }
}

class Doughnut < Pastry {
  cook() {
    super.describe();
    print "Deep-fried until golden brown.";
  }
}

var snack = Doughnut("Boston Cream");
snack.cook();
```

---

## 🏛 Architecture Overview

Rather than taking shortcuts with third-party parser generators (like ANTLR or Bison), every stage of this interpreter is hand-crafted from the ground up:

```
Source Code (.lox)
       │
       ▼
 ┌────────────┐
 │  Scanner   │  Transforms raw text into a stream of typed Tokens
 └─────┬──────┘
       │  List<Token>
       ▼
 ┌────────────┐
 │   Parser   │  Recursive-descent parsing with Pratt/precedence rules -> AST
 └─────┬──────┘
       │  List<Stmt> (Abstract Syntax Tree)
       ▼
 ┌────────────┐
 │  Resolver  │  Static semantic analysis: binds variable scopes & validates AST
 └─────┬──────┘
       │  AST + Scope Depth Map (Map<Expr, Integer>)
       ▼
 ┌────────────┐
 │Interpreter │  Tree-walk evaluation via the Visitor Pattern
 └────────────┘
```

---

## 🔍 Step-by-Step Subsystem Walkthrough

If you are walking someone through this codebase during a technical interview or code review, here is the tour path:

### 1. Lexical Analysis (`src/lox/Scanner.java`)
- **What it does**: Ingests raw UTF-8 source strings and groups characters into meaningful lexical units (`Token`).
- **Key implementation details**:
  - Uses a classic loop with `peek()`, `peekNext()`, and `advance()` to implement maximal munch.
  - Handles string literals with newline tracking, numeric literals (integer and floating-point), and single-line `//` comments.
  - Reserved keywords (like `class`, `fun`, `var`, `if`, `break`) are matched efficiently using a `HashMap<String, TokenType>`.

### 2. AST Representation & Metaprogramming (`src/tool/GenerateAst.java`)
- **The Problem**: An AST for a language with statements and expressions requires dozens of node classes (`Binary`, `Unary`, `Literal`, `If`, `While`, `Function`, etc.). Writing and maintaining boilerplate getters, constructors, and visitor hooks by hand is error-prone.
- **The Solution**: A lightweight standalone Java script (`GenerateAst.java`) that auto-generates type-safe AST classes (`Expr.java` and `Stmt.java`).
- **The Visitor Pattern**: Instead of scattering interpretation logic across every AST node class, the AST defines an `accept(Visitor<R>)` method. This gives us clean **double-dispatch**: operations like printing (`AstPrinter`), static resolution (`Resolver`), and execution (`Interpreter`) live in their own dedicated classes.

### 3. Recursive Descent Parser (`src/lox/Parser.java`)
- **Grammar & Precedence**: Implements a top-down recursive descent parser following context-free grammar rules:
  $$\text{expression} \to \text{assignment} \to \text{logic\_or} \to \text{logic\_and} \to \text{equality} \to \text{comparison} \to \text{term} \to \text{factor} \to \text{unary} \to \text{call} \to \text{primary}$$
- **Panic-Mode Error Recovery**: When a syntax error occurs, `synchronize()` discards tokens until it finds a statement boundary (`class`, `fun`, `var`, `for`, `if`, `while`, `print`, `return`, or `;`). This prevents a single typo from generating a cascading wall of spurious error messages.
- **Syntactic Sugar**: The `for` loop is entirely desugared at parse time into a `while` loop wrapped in an initializer block with an increment expression appended to the body.

### 4. Static Resolution Pass (`src/lox/Resolver.java`)
- **Why this is critical**: In a tree-walk interpreter with closures, dynamic scope lookup causes subtle bugs. If a global variable has the same name as a local variable declared later, an unresolved closure might resolve to the wrong scope at runtime.
- **How it works**: Before the interpreter executes a single line of code, the `Resolver` walks the AST exactly once:
  - Maintains a stack of local scope maps (`Stack<Map<String, Boolean>>`).
  - Counts the exact number of environment hops between where a variable is referenced and where it was declared.
  - Stores this distance in `Interpreter.locals` (`Map<Expr, Integer>`).
- **Semantic Guardrails**: Catching errors *before* runtime:
  - Disallows referencing a variable inside its own initializer (`var a = a;`).
  - Flags duplicate variable declarations in the same local scope.
  - Validates that `return` statements are only placed inside functions.
  - Ensures `this` and `super` are only used inside class methods (and `super` only within subclasses).
  - Validates that `break` statements are only located inside active loops via `loopDepth` tracking.

### 5. Runtime Evaluation & Environments (`src/lox/Interpreter.java`, `src/lox/Environment.java`)
- **Tree-Walk Evaluation**: The `Interpreter` implements `Expr.Visitor<Object>` and `Stmt.Visitor<Void>`. Expressions evaluate post-order (evaluating subexpressions first).
- **Environment Chains**: Lexical scopes are modeled as an linked chain of `Environment` instances, where each child environment holds a reference to its `enclosing` outer scope.
- **Fast Local Lookup**: Thanks to the `Resolver`, local variable lookups bypass slow dynamic scope searching; the interpreter jumps directly to the target ancestor environment via `environment.getAt(distance, name)`.
- **Control Flow via Stack Unwinding**:
  - Function returns throw a lightweight `Return` exception (disabling JVM stack trace generation for efficiency) that unwinds the Java call stack directly to the calling site.
  - The `break` statement unwinds nested loop statements using a dedicated `BreakException`.

### 6. Functions, Closures & OOP (`src/lox/LoxFunction.java`, `src/lox/LoxClass.java`, `src/lox/LoxInstance.java`)
- **First-Class Functions**: Functions are ordinary objects implementing `LoxCallable`. They can be assigned to variables, passed into other functions, or returned.
- **True Closures**: A `LoxFunction` captures its declaration environment (`closure`). When invoked, it creates a fresh environment childed to that captured environment, preserving closed-over state even after the parent function has exited.
- **Object-Oriented Core**:
  - `LoxClass` is both a callable constructor and a method dictionary. Calling `ClassName(...)` creates a `LoxInstance`, looks up `init()`, and binds `this`.
  - Property access is dynamic: fields live on the instance in a `Map<String, Object>`, while methods live on the class.
  - Single inheritance looks up methods recursively up the `superclass` hierarchy. `super.method()` correctly resolves the parent method bound to the current `this` instance.

---

## 🌟 Going Beyond the Book (Challenge Implementations)

Many implementations of *Crafting Interpreters* stop strictly at the book's narrative code. This project solves several of the challenging open exercises:

| Feature | How It Was Solved | Files Modified |
| :--- | :--- | :--- |
| **Anonymous Functions (Lambdas)** | Added `Expr.Function` node. The parser detects `fun` in expression position (`primary`), parses parameters and body without requiring a name identifier, and returns an anonymous callable instance. | `Parser.java`, `Expr.java`, `Interpreter.java`, `Resolver.java` |
| **Loop `break` Statement** | Added `Stmt.Break`. Tracked `loopDepth` in both `Parser` and `Resolver` so invalid `break` usages outside of loops are rejected at compile/resolution time. Runtime unwinds via `BreakException`. | `Parser.java`, `Resolver.java`, `Interpreter.java`, `Stmt.java` |
| **Strict Uninitialized Variables** | In standard Lox, `var a;` defaults to `nil`. Here, an explicit sentinel object (`UNINITIALIZED`) is assigned; attempting to read an unassigned variable throws a clear runtime error. | `Environment.java` |
| **Parser Error Productions** | Catches binary operations written without a left-hand operand (e.g. `+ 5;` or `* 10;`) and produces actionable, beginner-friendly error messages rather than confusing cascade errors. | `Parser.java` |
| **Division by Zero Protection** | Added explicit operand verification in `visitBinaryExpr` for `TokenType.SLASH` to catch zero denominators cleanly with a Lox `RuntimeError`. | `Interpreter.java` |

---

## 🗣 Live Interview / Code Walkthrough Guide

When presenting this project to an interviewer or colleague, here is a recommended 3-minute structured walkthrough:

1. **The Hook (30s)**:
   > *"I wanted to understand how programming languages work beneath high-level abstractions like Java or Python. Rather than using an off-the-shelf parser generator like ANTLR, I wrote this tree-walk interpreter for Lox in vanilla Java. It implements full lexical analysis, recursive-descent parsing, static semantic analysis, closures, and object-oriented inheritance."*

2. **The Hardest Engineering Challenge (60s)**:
   > *"The most illuminating challenge was variable resolution and lexical scoping. In early versions, a naive dynamic lookup meant that closures could accidentally read modified variables from an outer scope if variable names shadowed each other. Implementing `Resolver.java` taught me how compilers perform a static semantic pass before execution: it walks the AST, tracks lexical block depth on a stack, and pre-computes the exact environment ancestor distance for every local variable."*

3. **Custom Additions (45s)**:
   > *"I also tackled several of the book's advanced challenges. I extended the grammar to support anonymous lambda expressions, added a `break` statement that validates loop depth during static analysis, and added strict runtime checks for uninitialized variables."*

4. **Code Tour Highlights (45s)**:
   - Open `src/lox/Scanner.java` to show character-by-character tokenization.
   - Open `src/lox/Parser.java` to explain recursive descent and precedence rules.
   - Open `src/lox/Resolver.java` to showcase semantic analysis and scope depth calculation.
   - Open `src/lox/Interpreter.java` to demonstrate the Visitor pattern and environment lookup.

---

## 📁 Repository Structure

```
Lox/
├── .gitignore
├── README.md                      # Project documentation and walkthrough
├── scripts/
│   ├── fibonacci.lox              # Benchmark/demo script (loops & variables)
│   └── test.lox                   # OOP & inheritance demo script
└── src/
    ├── lox/
    │   ├── Environment.java       # Lexical scope bindings & enclosing chains
    │   ├── Expr.java              # Auto-generated AST Expression hierarchy
    │   ├── Interpreter.java       # AST evaluator (Visitor implementation)
    │   ├── Lox.java               # CLI entrypoint, REPL, and error reporting
    │   ├── LoxCallable.java       # Interface for callables (functions, classes)
    │   ├── LoxClass.java          # Runtime representation of classes & constructors
    │   ├── LoxFunction.java       # User functions, closures & lambdas
    │   ├── LoxInstance.java       # Runtime object instances & properties
    │   ├── Parser.java            # Recursive-descent parser & error recovery
    │   ├── Resolver.java          # Static semantic analysis & scope resolution
    │   ├── Return.java            # Control-flow exception for return statements
    │   ├── RuntimeError.java      # Runtime error with line & token tracking
    │   ├── Scanner.java           # Lexer / Tokenizer
    │   ├── Stmt.java              # Auto-generated AST Statement hierarchy
    │   ├── Token.java             # Token representation (type, lexeme, literal, line)
    │   └── TokenType.java         # Enum of all 40+ lexical token types
    └── tool/
        └── GenerateAst.java       # Metaprogramming tool to emit Expr and Stmt classes
```

---

## 📚 Acknowledgments & References

- **Robert Nystrom** for writing [*Crafting Interpreters*](https://craftinginterpreters.com/) — one of the clearest and most enjoyable software engineering books ever written.
- Built by [Tanakeo](https://github.com/Tanakeo).
