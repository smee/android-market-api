package com.gc.android.market.api;

import com.gc.android.market.api.model.Market;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Amir Raminfar
 */
public class AndroidMarketApi {
    private Logger logger = Logger.getLogger(AndroidMarketApi.class.toString());
    private final MarketSession session;
    private long lastUsed = 0;
    private String user;
    private boolean enableRateLimiting;

    public AndroidMarketApi(String user, String pass) {
        this(user, pass, true);
    }

    public AndroidMarketApi(String user, String pass, boolean enableRateLimiting) {
        this.session = new MarketSession();
        this.session.login(user, pass);
        this.user = user;
        this.enableRateLimiting = enableRateLimiting;
    }

    public String getUser() {
        return user;
    }

    public boolean isEnableRateLimiting() {
        return enableRateLimiting;
    }

    private synchronized void sleepIfNeeded() {
        if (enableRateLimiting) {
            long diff;
            if ((diff = System.currentTimeMillis() - lastUsed) < 5000) {
                try {
                    long sleep = 5000 - diff;
                    logger.log(Level.INFO, "Waiting {0}ms...", sleep);
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lastUsed = System.currentTimeMillis();
        }
    }

    public synchronized byte[] fetchBytes(Market.GetImageRequest request) {
        sleepIfNeeded();
        final byte[][] bytes = {null};
        logger.log(Level.FINE, "Executing {0}", request.toString().replaceAll("\n", ", "));
        session.append(request, new MarketSession.Callback<Market.GetImageResponse>() {
            @Override
            public void onResult(Market.ResponseContext context, Market.GetImageResponse response) {
                try {
                    bytes[0] = response.getImageData().toByteArray();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        session.flush();
        return bytes[0];
    }

    public synchronized byte[] fetchScreenShot(String appId, int i) throws MarketApiError {
        Market.GetImageRequest request = Market.GetImageRequest.newBuilder().setAppId(appId)
                .setImageUsage(Market.GetImageRequest.AppImageUsage.SCREENSHOT)
                .setImageId(String.valueOf(i))
                .build();
        return fetchBytes(request);
    }

    public synchronized byte[] fetchIcon(String appId) throws MarketApiError {
        Market.GetImageRequest request = Market.GetImageRequest.newBuilder().setAppId(appId)
                .setImageUsage(Market.GetImageRequest.AppImageUsage.ICON)
                .build();
        return fetchBytes(request);
    }


    public synchronized byte[] fetchThumbnail(String appId) throws MarketApiError {
        Market.GetImageRequest request = Market.GetImageRequest.newBuilder().setAppId(appId)
                .setImageUsage(Market.GetImageRequest.AppImageUsage.SCREENSHOT_THUMBNAIL)
                .build();
        return fetchBytes(request);
    }

    public synchronized List<Market.App> executeRequest(Market.AppsRequest request) throws MarketApiError {
        sleepIfNeeded();
        final List<Market.App> apps = new ArrayList<Market.App>();
        logger.log(Level.FINE, "Executing {0}", request.toString().replaceAll("\n", ", "));
        MarketSession.Callback<Market.AppsResponse> callback = new MarketSession.Callback<Market.AppsResponse>() {
            @Override
            public void onResult(Market.ResponseContext context, Market.AppsResponse response) {
                if (response != null) {
                    apps.addAll(response.getAppList());
                }
            }
        };
        session.append(request, callback);
        session.flush();
        return apps;
    }

    public static enum Category {
        ARCADE,
        BRAIN,
        CARDS,
        CASUAL,
        COMICS,
        COMMUNICATION,
        DEMO,
        ENTERTAINMENT,
        FINANCE,
        HEALTH,
        LIBRARIES,
        LIFESTYLE,
        MULTIMEDIA,
        PRODUCTIVITY,
        REFERENCE,
        SHOPPING,
        SOCIAL,
        SPORTS,
        THEMES,
        TOOLS,
        TRAVEL,
        NEWS
    }
}

