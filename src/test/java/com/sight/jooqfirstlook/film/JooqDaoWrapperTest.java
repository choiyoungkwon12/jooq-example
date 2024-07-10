package com.sight.jooqfirstlook.film;

import org.jooq.generated.tables.pojos.Film;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JooqDaoWrapperTest {

    @Autowired
    private FilmRepositoryIsA filmRepositoryIsA; // 상송

    @Autowired
    private FilmRepositoryHasA filmRepositoryHasA; // 컴포지투

    @Test
    void test() {
        Film byId = filmRepositoryIsA.findById(10L);
        Film byId1 = filmRepositoryHasA.findById(10L);
    }

    @Test
    @DisplayName("""
            상속) 자동생성 DAO 사용
             - 영화 길이가 100 ~ 180분 사이인 영화 조회
            """)
    void 상속_DAO_1() {
        var start = 100;
        var end = 180;

        List<Film> films = filmRepositoryIsA.fetchRangeOfJLength(start, end);
        assertThat(films).allSatisfy(film -> assertThat(film.getLength()).isBetween(start, end));
    }

    @Test
    @DisplayName("""
            상속) 컴포지션 DAO 사용
             - 영화 길이가 100 ~ 180분 사이인 영화 조회
            """)
    void 검포지션_DAO_1() {
        var start = 100;
        var end = 180;

        List<Film> films = filmRepositoryHasA.findByRangeBetween(start, end);
        assertThat(films).allSatisfy(film -> assertThat(film.getLength()).isBetween(start, end));
    }

}