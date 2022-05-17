package io.mkdirs.simpleton.model.token;

public enum TokenType {


    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    CHARACTER_LITERAL,
    BOOLEAN_LITERAL,

    VARIABLE_NAME,

    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    AND,
    OR,
    EXCLAMATION_MARK,
    GREATER_THAN,
    SMALLER_THAN,
    EQUALITY,
    INEQUALITY,
    ASSIGN,

    //KW = Key Word
    INT_KW,
    FLOAT_KW,
    STRING_KW,
    CHAR_KW,
    BOOL_KW,

    END_OF_LINE
}
