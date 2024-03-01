package com.ag.domain.service;

import com.ag.domain.constant.StatusType;
import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.exception.AgValidationException;
import com.ag.domain.model.Article;
import com.ag.domain.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static com.ag.domain.service.ArticleService.MAX_TITLE_LENGTH;
import static com.ag.domain.service.ArticleService.MAX_WORD_LENGTH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;
    @Mock
    private ArticleRepository articleRepository;

    private static final ArticleDTO article = new ArticleDTO();
    private static final Article deletedAllArticle = new Article();

    @BeforeAll
    public static void setUp() {
        article.setId("123d3489-ef6c-43ab-b86f-099405793e58");
        article.setNo(0);

        deletedAllArticle.setId("97393044-eb88-4a1e-be2e-7552825627a0");
        deletedAllArticle.setNo(0);
        deletedAllArticle.setStatus(StatusType.DELETED);
    }


    @Test
    public void validateWord_should_failed_if_is_blank() {
        // Arrange
        article.setWord("");

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateWord(article);
        });

        // Arrange
        article.setWord(null);

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateWord(article);
        });

    }

    @Test
    public void validateWord_should_success_if_length_in_max_bytes() {
        // Arrange
        article.setWord(generateRandomString(MAX_WORD_LENGTH));

        // Act & Assert
        assertDoesNotThrow(() -> {
            articleService.validateWord(article);
        });
    }

    @Test
    public void validateWord_should_failed_if_length_exceeds_max_bytes() {
        // Arrange
        article.setWord(generateRandomString(MAX_WORD_LENGTH + 1));

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateWord(article);
        });
    }

    @Test
    public void validateTitle_should_failed_if_is_blank() {
        // Arrange
        article.setTitle("");

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateTitle(article);
        });

        // Arrange
        article.setTitle(null);

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateTitle(article);
        });

    }

    @Test
    public void validateTitle_should_success_if_length_in_max_bytes() {
        // Arrange
        article.setTitle(generateRandomString(MAX_TITLE_LENGTH));

        // Act & Assert
        assertDoesNotThrow(() -> {
            articleService.validateTitle(article);
        });
    }

    @Test
    public void validateTitle_should_failed_if_length_exceeds_max_bytes() {
        // Arrange
        article.setTitle(generateRandomString(MAX_TITLE_LENGTH + 1));

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateTitle(article);
        });
    }

    @Test
    public void validateStatus_should_failed_if_status_is_not_normal() {
        // Arrange
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId();
        articleDTO.setNo(0);
        articleDTO.setStatus(StatusType.DELETED);

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateStatus(articleDTO);
        });
    }

    @Test
    public void validateFirstArticleIsNormal_should_failed_if_first_article_status_is_not_normal() {
        // Arrange
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(deletedAllArticle.getId());
        articleDTO.setNo(2);
        when(articleRepository.get(anyString(), anyInt())).thenReturn(deletedAllArticle);

        // Act & Assert
        assertThrows(AgValidationException.class, () -> {
            articleService.validateFirstArticleIsNormal(articleDTO);
        });

    }

    private static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            char randomChar = (char) (random.nextInt(26) + 'a');
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private Article generateArticle(){

    }
}