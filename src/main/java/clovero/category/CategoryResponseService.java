package clovero.category;

import clovero.LineMessageHelper;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryResponseService {

    CategoryRepository categoryRepository;
    LineMessageHelper lineMessageHelper;

    public List<Message> getCategoryNames() {
        final List<Category> categories = (List) categoryRepository.findAll();
        final List<String> categoryNames = categories.stream()
                .map(category -> category.getName())
                .collect(Collectors.toList());

        return lineMessageHelper.createButtonResponse("Pilih kategori", categoryNames);
//        return createCategoryCarousel("Pilih Kategori", categories);
    }

    public List<Message> createCategoryCarousel(String title, List<Category> categories) {
        final Template template = new CarouselTemplate(createCategoryCarouselColumn(categories));
        final Message message = new TemplateMessage(title, template);

        final List<Message> messages = new ArrayList<>();
        messages.add(message);

        return messages;
    }

    private List<CarouselColumn> createCategoryCarouselColumn(List<Category> categories) {
        final List<CarouselColumn> carouselColumns = new ArrayList<>();

        categories.forEach(category -> {
            final String description = category.getDescription();

            carouselColumns.add(new CarouselColumn(category.getImageUrl(), null, description,
                    lineMessageHelper.createActionsFromTexts(Arrays.asList(category.getName()))));
        });

        return carouselColumns;
    }

}
