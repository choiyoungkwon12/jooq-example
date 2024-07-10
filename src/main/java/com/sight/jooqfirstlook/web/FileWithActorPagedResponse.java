package com.sight.jooqfirstlook.web;

import com.sight.jooqfirstlook.film.FilmWithActor;

import java.util.List;

public class FileWithActorPagedResponse {
    private PagedResponse page;
    private List<FilmActorResponse> filmActorResponses;

    public FileWithActorPagedResponse(PagedResponse page, List<FilmWithActor> filmWithActorList) {
        this.page = page;
        this.filmActorResponses = filmWithActorList.stream()
                .map(FilmActorResponse::new)
                .toList();
    }

    public PagedResponse getPage() {
        return page;
    }

    public List<FilmActorResponse> getFilmActorResponses() {
        return filmActorResponses;
    }

    public static class FilmActorResponse {

        private final String filmTitle;
        private final String actorFullName;
        private final Long filmId;

        public String getFilmTitle() {
            return filmTitle;
        }

        public String getActorFullName() {
            return actorFullName;
        }

        public Long getFilmId() {
            return filmId;
        }

        public FilmActorResponse(FilmWithActor filmWithActor) {
            this.filmTitle = filmWithActor.getTitle();
            this.actorFullName = filmWithActor.getActorFullName();
            this.filmId = filmWithActor.getFilmId();
        }
    }
}
