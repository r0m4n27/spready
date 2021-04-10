# Lisp

Spready supports most of the [R5RS](https://en.wikipedia.org/wiki/Scheme_(programming_language)#Review_of_standard_forms_and_procedures)
standard. Some functions that were not necessary have not been implemented.
The type `Cell` has been added to use the lisp with the spreadsheet.

## Data Types

- String: `"This is a string"`
- Boolean:
  - `#t` or `#f`
  - Everything except `#f` and `nil` is true
- Function: Can be created with `lambda` or `fun`

### Variables

- Symbol
  - `symbol`
  - Can be bound with `val`
- Cell
  - `#12.45` (row, column)
  - Can only be bound through the UI

### Numbers

All operations support using different number types `(+ 1 1/2) => 3/2`


- Integer: `123`
- Float: `1.23`
- Fraction: `3/2`

### List elements

- Cons:
  - Pair of two values
  - `(3 . 2)`
- Nil
  - `nil`
  - End of list
  - Will be returned if a function has no return value

## Functions

Only the most important functions are listed

- `cell-range`: Creates a rectangular list of cells

### Forms

- `let`: Binds variables for the scope
- `do`: Evaluate the body until the test expression is true
- `if`: If the test is true evaluate the first branch else the second one
- `cond`: Evaluates the first branch which test expression is true
- `case`: Searches the first element in the lists of the branches, evaluates the branch if the elem is in the list
- `run`: Evaluates all expression and returns the value of the last one
- `lambda`: Creates an anonymous function
- `val`: Binds a value to a `symbol` (global)
- `fun`: Defines a function
- `'expr`: Quotes the expression

### Conversions

- `to-list`: String
- `to-str`: Symbol, Number, List element
- `to-int`: String, other number
- `to-float`: String, other number
- `to-fraction`: String, other number
- `to-bool`: All
- `to-symbol`: String
- `to-cell`: String

### Comparing

- `=`, `!=`: All
- `<`, `>`, `<=`, `>=`: String, Number

### Functional

- `map`: Maps function over a list, if multiple lists are provided it will zip the elements together
- `for-each`: Same as `map` but return `nil`
- `apply`: Passes the values to the function

### List

- `list`: Creates a new list. Can be used instead of quoting
- `cons`: Creates a new cons cell. Can be used instead of `'(3 . 2)`
- `append`: Appends the lists to a new one
- `reverse`: Returns the reversed list
- `get`: Gets the element at the specified index
- `sublist`: Creates a sublist beginning at the index
- `member`: Searches for an item in the list, returns the sublist with the item as the head
- `assoc`: Searches the head of list of lists for an element, returns the list with the item


### String

- `string-get`: Returns a String of a single character at the index
- `replace-char`: Returns a new String with the replaced character at the index
- `substring`: Creates a substring beginning at the index
- `string-append`: Appends multiple strings into one
- `string-fill`: Fills the string with a specific character
