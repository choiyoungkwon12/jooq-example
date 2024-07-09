package com.sight.jooqfirstlook.film;

import org.jooq.generated.tables.pojos.Film;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmRepositoryTest {

    @Autowired
    FilmRepository filmRepository;

    @Test
    @DisplayName("1) 영화 정보 조회")
    void findById() {
        Film film = filmRepository.findById(1L);
        assertThat(film).isNotNull();
    }
}