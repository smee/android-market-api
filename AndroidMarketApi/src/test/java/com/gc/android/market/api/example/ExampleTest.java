package com.gc.android.market.api.example;

import com.gc.android.market.api.AndroidApiPool;
import com.gc.android.market.api.AndroidMarketApi;
import com.gc.android.market.api.MarketApiError;
import com.gc.android.market.api.model.Market;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Amir Raminfar
 */
public class ExampleTest {

    @Test
    public void poolExample() {
        List<AndroidMarketApi> list = new ArrayList<AndroidMarketApi>();
        list.add(new AndroidMarketApi("user1@gmail.com", "********"));
        list.add(new AndroidMarketApi("user2@gmail.com", "********"));
        list.add(new AndroidMarketApi("user3@gmail.com", "********"));
        AndroidApiPool apiPool = new AndroidApiPool(list);

        /**
         * The fourth iteration will sleep because there are only three objects in the pool!
         */
        for (int i = 0; i < 4; i++) {
            AndroidMarketApi api = null;
            try {
                api = apiPool.acquire();
                try {
                    List<Market.App> apps = api.executeRequest(Market.AppsRequest.newBuilder()
                            .setCategoryId(AndroidMarketApi.Category.HEALTH.toString())
                            .setStartIndex(i * 10)
                            .setEntriesCount(10)
                            .build());
                    System.out.println(apps);
                } catch (MarketApiError e) {
                    e.printStackTrace();
                }
            } finally {
                apiPool.release(api);
            }
        }

    }
}
