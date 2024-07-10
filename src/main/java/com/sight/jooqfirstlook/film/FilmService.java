package com.sight.jooqfirstlook.film;

import com.sight.jooqfirstlook.web.FileWithActorPagedResponse;
import com.sight.jooqfirstlook.web.PagedResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {
    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public FileWithActorPagedResponse getFilmActorPageResponse(Long page, Long pageSize) {
        List<FilmWithActor> filmWithActorList = filmRepository.findFilmWithActorList(page, pageSize);
        PagedResponse pagedResponse = new PagedResponse(page, pageSize);
        return new FileWithActorPagedResponse(pagedResponse, filmWithActorList);
    }
}
