package org.erp.giahungquan_be.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BatchFetch {
    public static int BATCH_FETCH_SIZE = 2000;

    public static <T, ID> Map<ID, T> fetch(
        List<ID> ids,
        Function<List<ID>, List<T>> fetchFunc,
        Function<T, ID> getIdFunc,
        int batchSize
    ) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        int initialCapacity = (int) (ids.size() / 0.75) + 1;
        Map<ID, T> result = new HashMap<>(initialCapacity);
        
        for (int i = 0 ; i < ids.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ids.size());
            List<ID> batchIds = ids.subList(i, end);

            List<T> batchResults = fetchFunc.apply(batchIds);

            if (batchResults != null) {
                batchResults.forEach(entity -> result.put(getIdFunc.apply(entity), entity));
            }
        }
        return result;
    }
}
