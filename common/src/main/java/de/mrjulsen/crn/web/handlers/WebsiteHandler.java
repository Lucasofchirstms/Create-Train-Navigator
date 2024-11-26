package de.mrjulsen.crn.web.handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Optional;

import com.sun.net.httpserver.HttpHandler;
import com.google.common.net.MediaType;
import com.sun.net.httpserver.HttpExchange;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.config.ModCommonConfig;
import de.mrjulsen.crn.util.ModUtils;
import de.mrjulsen.crn.web.SimpleWebServer;
import de.mrjulsen.crn.web.WebsitePreparableReloadListener;

public class WebsiteHandler implements HttpHandler {

    private final String subUrl;
    private final int subUrlLength;

    public WebsiteHandler(String subUrl) {
        this.subUrl = subUrl;
        this.subUrlLength = subUrl.length() + 1;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        
        String requestedPath = t.getRequestURI().getPath();
        if (requestedPath.startsWith("/" + subUrl) && requestedPath.length() >= subUrlLength) {
            requestedPath = requestedPath.substring(subUrlLength);
        } else {
            SimpleWebServer.sendError(t, HttpURLConnection.HTTP_BAD_REQUEST, "The requested URL is invalid: " + requestedPath);
        }

        if (requestedPath.isBlank()) {
            SimpleWebServer.redirectTo(t, "/" + subUrl + "/");
            return;
        }

        if (requestedPath.equals("/")) {
            requestedPath = "/index.html";  // Set default page to index.html
        }

        if (ModCommonConfig.ADVANCED_LOGGING.get() || true) CreateRailwaysNavigator.LOGGER.info("A web service requested a salz resource: " + requestedPath);
        WebsitePreparableReloadListener manager = ModUtils.getWebsiteResourceManager();
        if (manager == null) {
            SimpleWebServer.sendError(t, HttpURLConnection.HTTP_UNAVAILABLE, "The Website Manager is not available yet.");
            CreateRailwaysNavigator.LOGGER.info("Trying to use the website manager which is not available yet.");
            return;
        }
        Optional<byte[]> fileData = ModUtils.getWebsiteResourceManager().getFileBytesFor(requestedPath);
        if (!fileData.isPresent()) {
            SimpleWebServer.sendError(t, HttpURLConnection.HTTP_NOT_FOUND, "The requested resource does not exist: " + requestedPath);
        } else {
            SimpleWebServer.respond(t, HttpURLConnection.HTTP_OK, MediaType.HTML_UTF_8, fileData.get());
        }
    }
}
