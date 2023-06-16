## validation manual for thymeleaf

https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#validation-and-error-messages

## safe Navigation Operator SpringEL

https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions-operator-safe-navigation
```
use case : errors?. in addForm.html -> if 'errors' == null, 
should NullPointerException occur, returns null instead of an exception
```