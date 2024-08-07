package com.sight.jooqfirstlook;

import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JOoqFirstLookApplicationTest {
    @Autowired
    private DSLContext dslContext;
    @Test
    void test() {
        dslContext.selectFrom(JActor.ACTOR)
                .limit(10)
                .fetch();
    }

}