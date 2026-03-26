package lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing;

    private static final Object UNINITIALIZED = new Object();

    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            Object value = values.get(name.lexeme);

            if (value == UNINITIALIZED) {
                throw new RuntimeError(name,
                        "Variable '" + name.lexeme + "' not initialized.");
            }

            return value;
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void define(String name, Object value) {
        if (value == null) {
            values.put(name, UNINITIALIZED);
        } else {
            values.put(name, value);
        }
    }
}