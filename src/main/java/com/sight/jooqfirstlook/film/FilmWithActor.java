package com.sight.jooqfirstlook.film;

import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.generated.tables.pojos.FilmActor;

public class FilmWithActor {

    private final Film film;
    private final FilmActor filmActor;
    private final Actor actor;

    public FilmWithActor(Film film, FilmActor filmActor, Actor actor) {
        this.film = film;
        this.filmActor = filmActor;
        this.actor = actor;
    }

    public Film getFilm() {
        return film;
    }

    public FilmActor getFilmActor() {
        return filmActor;
    }

    public Actor getActor() {
        return actor;
    }

    public String getTitle() {
        return film.getTitle();
    }

    public String getActorFullName() {
        return actor.getFirstName() + " " + actor.getLastName();
    }

    public Long getFilmId() {
        return film.getFilmId();
    }
}
