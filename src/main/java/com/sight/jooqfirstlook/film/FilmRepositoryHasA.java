package com.sight.jooqfirstlook.film;

import org.jooq.CacheProvider;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.daos.FilmDao;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

// 컴포지션
@Repository
public class FilmRepositoryHasA {

    private final DSLContext dslContext;
    private final JFilm FILM = JFilm.FILM;
    private final FilmDao filmDao;

    public FilmRepositoryHasA(DSLContext dslContext, Configuration configuration) {
        this.dslContext = dslContext;
        filmDao = new FilmDao(configuration);
    }

    public Film findById(Long id) {
        return filmDao.fetchOneByJFilmId(id);
    }

    public List<Film> findByRangeBetween(Integer from, Integer to) {
        return filmDao.fetchRangeOfJLength(from, to);
    }

    public SimpleFilmInfo findSimpleFilmInfoById(Long id) {
        return dslContext.select(
                        FILM.FILM_ID,
                        FILM.TITLE,
                        FILM.DESCRIPTION
                ).from(FILM)
                .where(FILM.FILM_ID.eq(id))
                .fetchOneInto(SimpleFilmInfo.class);
    }

    public List<FilmWithActor> findFilmWithActorList(Long page, Long pageSize) {
        JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        JActor ACTOR = JActor.ACTOR;

        return dslContext.select(
                        DSL.row(FILM.fields()),
                        DSL.row(FILM_ACTOR.fields()),
                        DSL.row(ACTOR.fields())
                ).from(FILM)
                .join(FILM_ACTOR)
                .on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
                .join(ACTOR)
                .on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
                .offset((page - 1) * pageSize)
                .limit(pageSize)
                .fetchInto(FilmWithActor.class);
    }
}
