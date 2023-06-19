package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    /**
     *  검증 오류 결과를 보관  -> replaced from a HashMap to BindingResult
     *  BindingResult takes over the use of Model(for error) and the HashMap<K,V> below
     *  Map<String, String> errors = new HashMap<>();
     */
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    /**global ver
     *
     * @SpringBootApplication
     * public class ItemServiceApplication implements WebMvcConfigurer {
     *  public static void main(String[] args) {
     *  SpringApplication.run(ItemServiceApplication.class, args);
     *  }
     *  @Override
     *  public Validator getValidator() {
     *  return new ItemValidator();
     *  }
     * }
     *
     * as can be seen from the above code, the main class needs to implement WebMvcConfigurer then override
     * the getValidator object.
     *
     * but this comes at the expense of sacrificing the original getValidator -> which includes beanValidator
     *
     * this is thus rarely used
     */

    /**
     * @param item now has the @Validated tag.
     * This now means that "item" will automatically pass through all applicable validator classes
     * that supports(return true) its class, which were registered on WebDataBinder.
     *
     *             both @Validated(spring) and @Valid(jakarta EE) works
     *
     *             but to use the jakarta EE version, there needs to be an additional
     *             dependency written on build.gradle
     *
     *             which is:
     *             implementation 'org.springframework.boot:spring-boot-starter-validation'
     */


    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult
            bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

