package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

//    this API converter shows how the @Vlalid, @Validated can be used alongside
//    with @RequestBody, (HttpMessageConverter)

//    refresher
//     @ModelAttribute is used to resolve HTTP request Parameters( URL / pathvar , queryString / Post form)
//>    @RequestBody is used to resolve HTTP body, typically json.


//    modelattribute can thus act on individual query parameters, so even if 1 param fails,
//    the remaining params can still be processed normally
//    but because requestbody needs to create an ItemSaveForm Object before running validation,
//    a bind failure will result in a completely unprocessed request.
//
//    when the HttpMessageConverter manages to parse the request json into an ItemSaveForm,
//    it will then call the controller
//    if not, the controller itself will not be called at all, giving a 400 bad request exception

    /*
    ```
            .w.s.m.s.DefaultHandlerExceptionResolver : Resolved
[org.springframework.http.converter.HttpMessageNotReadableException: JSON parse
    error: Cannot deserialize value of type `java.lang.Integer` from String "A":
    not a valid Integer value; nested exception is
    com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize
    value of type `java.lang.Integer` from String "A": not a valid Integer value
    at [Source: (PushbackInputStream); line: 1, column: 30] (through reference
    chain: hello.itemservice.domain.item.Item["price"])]
    ```
 */

//    if it parses but gives an error at the validator level
//     the  return bindingResult.getAllErrors();
//    returns all Object erros and fieldErrors.
//    spring will via HTTPMEssageConverter, convert the String into a json format
//    before returning the reply to the client.
//    in real development, only necessary data should be returned, according to
//    preset API requirements

    /*
    ```
    API 컨트롤러 호출
    검증 오류 발생, errors=org.springframework.validation.BeanPropertyBindingResult: 1
    errors
    Field error in object 'itemSaveForm' on field 'quantity': rejected value
[99999]; codes
[Max.itemSaveForm.quantity,Max.quantity,Max.java.lang.Integer,Max]; arguments
[org.springframework.context.support.DefaultMessageSourceResolvable: codes
[itemSaveForm.quantity,quantity]; arguments []; default message
[quantity],9999]; default message [9999 이하여야 합니다]
    ```
     */

    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {

        log.info("API 컨트롤러 호출");

        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors={}", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");
        return form;
    }
}
