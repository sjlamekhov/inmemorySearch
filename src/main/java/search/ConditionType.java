package search;

public enum  ConditionType {
    EQ, //equals
    GT, //greater than
    LT, //lower than
    NE, //not equals
    STWITH, //startsWith
    CLOSE_TO, //find all values that have edit distance <= 3
    CONTAINS,
    LENGTH,
    ALL
}
