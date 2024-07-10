package com.sight.jooqfirstlook.film;

import com.sight.jooqfirstlook.web.FileWithActorPagedResponse;
import org.jooq.generated.tables.pojos.Film;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmService filmService;

    @Test
    @DisplayName("1) 영화 정보 조회")
    void findById() {
        Film film = filmRepository.findById(1L);
        assertThat(film).isNotNull();
    }


    @Test
    @DisplayName("2) 영화 정보 간략 조회")
    void test2() {
        SimpleFilmInfo simpleFilmInfoById = filmRepository.findSimpleFilmInfoById(1L);
        assertThat(simpleFilmInfoById).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("3) 영화와 영화에 출연한 배우 정보를 페이징하여 조회")
    void test3() {
        FileWithActorPagedResponse filmActorPageResponse = filmService.getFilmActorPageResponse(1L, 20L);

        assertThat(filmActorPageResponse.getFilmActorResponses()).hasSize(20);
    }
}