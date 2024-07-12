package com.sight.jooqfirstlook.utils.jooq;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class JooqListConditionUtil {

    public static <T> Condition inIfNotEmpty(Field<T> actorId, List<T> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return DSL.noCondition();
        }
        return actorId.in(idList);
    }

}
