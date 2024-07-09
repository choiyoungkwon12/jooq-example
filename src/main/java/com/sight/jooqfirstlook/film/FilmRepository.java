package com.sight.jooqfirstlook.film;

import org.jooq.DSLContext;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.pojos.Film;
import org.springframework.stereotype.Repository;

@Repository
public class FilmRepository {

    private final DSLContext dslContext;
    private final JFilm FILM = JFilm.FILM;

    public FilmRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Film findById(Long id) {
        return dslContext.select(FILM.fields())
                .from(FILM)
                .where(FILM.FILM_ID.eq(id))
                .fetchOneInto(Film.class);
    }
}
