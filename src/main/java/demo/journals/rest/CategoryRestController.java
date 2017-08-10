package demo.journals.rest;

import demo.journals.model.Category;
import demo.journals.repository.CategoryRepository;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/rest/category")
public class CategoryRestController {

    private final CategoryRepository repository;

    public CategoryRestController(CategoryRepository repository) {
        this.repository = repository;
    }


    @RequestMapping(value = "")
    public List<Category> getCategories() {
        return repository.findAll();
    }

}
