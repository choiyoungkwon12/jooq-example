package com.sight.jooqfirstlook.actor;


import com.sight.jooqfirstlook.utils.jooq.JooqListConditionUtil;
import com.sight.jooqfirstlook.utils.jooq.JooqStringConditionUtil;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.daos.ActorDao;
import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.generated.tables.records.ActorRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ActorRepository {

    private final DSLContext dslContext;
    private final ActorDao actorDao;
    private final JActor ACTOR = JActor.ACTOR;

    public ActorRepository(DSLContext dslContext, Configuration configuration) {
        this.dslContext = dslContext;
        this.actorDao = new ActorDao(configuration);
    }

    public List<Actor> findByFirstNameAndLastName(String firstName, String lastName) {
        return dslContext.selectFrom(ACTOR)
                .where(
                        ACTOR.FIRST_NAME.eq(firstName).and(ACTOR.LAST_NAME.eq(lastName))
                ).fetchInto(Actor.class);
    }

    public List<Actor> findByFirstNameOrLastName(String firstName, String lastName) {
        return dslContext.selectFrom(ACTOR)
                .where(
                        ACTOR.FIRST_NAME.eq(firstName).or(ACTOR.LAST_NAME.eq(lastName))
                ).fetchInto(Actor.class);
    }

    public List<Actor> findByActorIdIn(List<Long> idList) {
        return dslContext.selectFrom(ACTOR)
                .where(JooqListConditionUtil.inIfNotEmpty(ACTOR.ACTOR_ID, idList))
                .fetchInto(Actor.class);
    }

    public List<ActorFilmography> findActorFilmography(ActorFilmographySearchOption searchOption) {
        final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        final JFilm FILM = JFilm.FILM;
        Map<Actor, List<Film>> actorListMap = dslContext.select(
                        ACTOR, FILM
                ).from(ACTOR)
                .join(FILM_ACTOR)
                .on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
                .join(FILM)
                .on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
                .where(
                        JooqStringConditionUtil.containsIfNotBlank(ACTOR.FIRST_NAME.concat(" ").concat(ACTOR.LAST_NAME), searchOption.getActorName()),
                        JooqStringConditionUtil.containsIfNotBlank(FILM.TITLE, searchOption.getFilmTitle())
                )
                .fetchGroups(
                        record -> record.into(ACTOR).into(Actor.class),
                        record -> record.into(FILM).into(Film.class)
                );
        return actorListMap.entrySet().stream().map(entry -> new ActorFilmography(entry.getKey(), entry.getValue())).toList();
    }

    public Actor saveByDao(Actor actor) {
        actorDao.insert(actor);
        return actor;
    }

    public ActorRecord saveByRecord(Actor actor) {
        ActorRecord actorRecord = dslContext.newRecord(ACTOR, actor);
        actorRecord.insert();
        return actorRecord;
    }

    public Long saveWithReturningPkOnly(Actor actor) {
        return dslContext.insertInto(
                        ACTOR, ACTOR.FIRST_NAME, ACTOR.LAST_NAME
                ).values(
                        actor.getFirstName(),
                        actor.getLastName()
                ).returningResult(ACTOR.ACTOR_ID)
                .fetchOneInto(Long.class);
    }

    public Actor saveWithReturning(Actor actor) {
        return dslContext.insertInto(
                        ACTOR,
                        ACTOR.FIRST_NAME,
                        ACTOR.LAST_NAME
                ).values(
                        actor.getFirstName(),
                        actor.getLastName()
                ).returningResult(ACTOR.fields())
                .fetchOneInto(Actor.class);
    }

    public void bulkInsertWithRows(List<Actor> actorList) {
        var rows = actorList.stream().map(actor -> DSL.row(
                actor.getFirstName(),
                actor.getLastName()
        )).collect(Collectors.toList());

        dslContext.insertInto(
                        ACTOR,
                        ACTOR.FIRST_NAME,
                        ACTOR.LAST_NAME
                ).valuesOfRows(rows)
                .execute();
    }
}
