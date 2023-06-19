package hello.itemservice.domain.item;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

//controllerV1 - controllerV3
//do not use the scriptassert as nashhorn got deprecated from spring 3.0.1 onwards and code is not clean (js - java mixup)
//add nashhorn to dependency in build.gradle to still use this feature
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
//@Data
//public class Item {
//
//    private Long id;
//    @NotBlank
//    private String itemName;
//
////    @NotBlank(message = "공백은 입력할 수 없습니다.")
////    private String itemName;
//
//    @NotNull
//    @Range(min = 1000, max = 1000000)
//    private Integer price;
//
//    @NotNull
//    @Max(9999)
//    private Integer quantity;
//
//    public Item() {
//    }
//
//    public Item(String itemName, @NotNull Integer price, Integer quantity) {
//        this.itemName = itemName;
//        this.price = price;
//        this.quantity = quantity;
//    }
//}

@Data
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;
}
