package com.sight.jooqfirstlook.film;

import com.sight.jooqfirstlook.config.converter.PriceCategoryConverter;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.JInventory;
import org.jooq.generated.tables.JRental;
import org.jooq.generated.tables.daos.FilmDao;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static com.sight.jooqfirstlook.utils.jooq.JooqStringConditionUtil.containsIfNotBlank;
import static org.jooq.impl.DSL.*;
import static org.jooq.impl.DSL.case_;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectCount;

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
                        row(FILM.fields()),
                        row(FILM_ACTOR.fields()),
                        row(ACTOR.fields())
                ).from(FILM)
                .join(FILM_ACTOR)
                .on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
                .join(ACTOR)
                .on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
                .offset((page - 1) * pageSize)
                .limit(pageSize)
                .fetchInto(FilmWithActor.class);
    }

    public List<FilmPriceSummary> findFilmPriceSummaryByFilmTitleLike(String title) {
        final JInventory INVENTORY = JInventory.INVENTORY;
        return dslContext.select(
                        FILM.FILM_ID,
                        FILM.TITLE,
                        FILM.RENTAL_RATE,
                        case_()
                                .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(1.0)), FilmPriceSummary.PriceCategory.CHEAP.getCode())
                                .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(3.0)), FilmPriceSummary.PriceCategory.MODERATE.getCode())
                                .else_(FilmPriceSummary.PriceCategory.EXPENSIVE.getCode()).as("price_category").convert(new PriceCategoryConverter()),
                        selectCount().from(INVENTORY).where(INVENTORY.FILM_ID.eq(FILM.FILM_ID)).asField("total_inventory")
                ).from(FILM)
                .where(FILM.TITLE.like("%" + title + "%"))
                .fetchInto(FilmPriceSummary.class);
    }

    public List<FilmRentalSummary> findFilmRentalSummaryByFilmTitleLike(String filmTitle) {
        JInventory INVENTORY = JInventory.INVENTORY;
        JRental RENTAL = JRental.RENTAL;

        var rentalDurationInfoSubquery = select(
                INVENTORY.FILM_ID,
                avg(localDateTimeDiff(DatePart.DAY, RENTAL.RENTAL_DATE, RENTAL.RETURN_DATE)).as("average_rental_duration"))
                .from(RENTAL)
                .join(INVENTORY)
                .on(RENTAL.INVENTORY_ID.eq(INVENTORY.INVENTORY_ID))
                .where(RENTAL.RENTAL_DATE.isNotNull())
                .groupBy(INVENTORY.FILM_ID)
                .asTable("rental_duration_info");

        return dslContext.select(FILM.FILM_ID, FILM.TITLE, rentalDurationInfoSubquery.field("average_rental_duration"))
                .from(FILM)
                .join(rentalDurationInfoSubquery)
                .on(FILM.FILM_ID.eq(rentalDurationInfoSubquery.field(INVENTORY.FILM_ID)))
                .where(containsIfNotBlank(FILM.TITLE, filmTitle))
                .orderBy(field(name("average_rental_duration")).desc())
                .fetchInto(FilmRentalSummary.class);
    }

    /**
     * ### 3. 서브쿼리
     * 대여된 기록이 있는 영화가 있는 영화만 조회
     *
     * <pre>
     * SELECT
     *   film.*
     * FROM
     *   film
     * WHERE EXISTS (
     *   SELECT 1
     *   FROM inventory
     *   JOIN rental ON inventory.inventory_id = rental.inventory_id
     *   WHERE inventory.film_id = film.film_id
     * )
     * </pre>
     */
    public List<Film> findRentedFilmByTitle(String filmTitle) {
        JInventory INVENTORY = JInventory.INVENTORY;
        JRental RENTAL = JRental.RENTAL;

        return dslContext.selectFrom(FILM)
                .whereExists(selectOne()
                        .from(INVENTORY)
                        .join(RENTAL).on(INVENTORY.INVENTORY_ID.eq(RENTAL.INVENTORY_ID))
                        .where(INVENTORY.FILM_ID.eq(FILM.FILM_ID))
                ).and(containsIfNotBlank(FILM.TITLE, filmTitle))
                .fetchInto(Film.class);
    }
}
