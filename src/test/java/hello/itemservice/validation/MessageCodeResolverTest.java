package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.assertThat;


/** this test will be used to identify what the hell is going on with MessageCodeResolver
 * interface
 *
 * messageCodeResolver can be seen to work in the following ways to generate default message
 * for ObjectError
 * 1.: code + "." + object_name
 * 2.: code
 *
 * ie) error_code: required, object_name: item
 * 1.: required.item
 * 2.: required
 *
 *
 * for FieldError
 * 1.: code + "." + object name + "." + field
 * 2.: code + "." + field
 * 3.: code + "." + field type
 * 4.: code
 * ie) error_code: typeMismatch, object_name "user", field "age", field type: int
 * 1. "typeMismatch.user.age"
 * 2. "typeMismatch.age"
 * 3. "typeMismatch.int"
 * 4. "typeMismatch"
 *
 * method call procedure
 *
 * rejectvalue(), reject() internally uses MessageCodeResolver to generate error message codes
 * FieldError and ObjectError constructors take and store all of the error codes generated
 * and stored by MessageCodesResolver
 * -> can be seen through BindingResult's log
 * log.info("errorCodes = {}", bindingResult.getAllErrors());
 * -> gives [required.item.itemName,required.itemName,required.java.lang.String,required];
 */
public class MessageCodeResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodeResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required",
                "item", "itemName", String.class);
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
