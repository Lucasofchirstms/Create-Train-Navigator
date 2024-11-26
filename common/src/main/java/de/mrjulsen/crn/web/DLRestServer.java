package de.mrjulsen.crn.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.sun.net.httpserver.HttpExchange;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.web.handlers.WebsiteHandler;
import de.mrjulsen.mcdragonlib.util.DLUtils;

public class DLRestServer {

    public static void main(String[] args) {
        start(8080, List.of());
    }

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "PATCH";

    private static HttpServer server;

    public static void start(int port, List<DLRestManager> handlers) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            CreateRailwaysNavigator.LOGGER.error("Cannot start http server on port " + port + ".", e);
            return;
        }
        
        server.createContext("/" + CreateRailwaysNavigator.MOD_ID, new WebsiteHandler(CreateRailwaysNavigator.MOD_ID));
        server.createContext("/" + CreateRailwaysNavigator.SHORT_MOD_ID, new WebsiteHandler(CreateRailwaysNavigator.SHORT_MOD_ID));
        for (DLRestManager handler : handlers) {
            server.createContext("/" + CreateRailwaysNavigator.SHORT_MOD_ID + "/" + handler.path(), handler);
        }

        server.setExecutor(null); // creates a default executor
        server.start();
        CreateRailwaysNavigator.LOGGER.info("Http server has been started on port: " + port);
    }
    
    public static void stop() {
        DLUtils.doIfNotNull(server, x -> {
            x.stop(0);
            CreateRailwaysNavigator.LOGGER.info("The http server has been stopped.");
        });
    }

    private static void startResponse(HttpExchange ex, int code, MediaType contentType, boolean hasBody) throws IOException {
        if (contentType != null) {
            ex.getResponseHeaders().set("Content-Type", contentType.type());
        }
        if (!hasBody) { // No body. Required for HEAD requests
            ex.sendResponseHeaders(code, -1);
        } else { // Chuncked encoding
            ex.sendResponseHeaders(code, 0);
        }
    }

    public static void sendError(HttpExchange ex, int code, String msg) {
        CreateRailwaysNavigator.LOGGER.warn(msg);
        try {
            respond(ex, code, MediaType.PLAIN_TEXT_UTF_8, msg.getBytes());
        } catch (IOException e) {
            CreateRailwaysNavigator.LOGGER.error("Unable to send error response.", e);
        }
    }

    public static void respond(HttpExchange ex, int code, MediaType contentType, byte response[]) throws IOException {
        startResponse(ex, code, contentType, response != null);
        if (response != null) {
            OutputStream responseBody = ex.getResponseBody();
            responseBody.write(response);
            responseBody.flush();
            responseBody.close();
        }
        ex.close();
    }

    public static void sendRedirect(HttpExchange ex, URI location) throws IOException {
        ex.getResponseHeaders().set("Location", location.toString());
        respond(ex, HttpURLConnection.HTTP_SEE_OTHER, null, null);
    }

    public static URI getRequestUri(HttpExchange ex) {
        String host = ex.getRequestHeaders().getFirst("Host");
        if (host == null) { // Client must be using HTTP/1.0
            CreateRailwaysNavigator.LOGGER.warn("Request did not provide host header, using 'localhost' as hostname");
            int port = ex.getHttpContext().getServer().getAddress().getPort();
            host = "localhost:" + port;
        }
        String protocol = (ex.getHttpContext().getServer() instanceof HttpsServer) ? "https" : "http";
        URI base;
        try {
            base = new URI(protocol, host, "/", null, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
        URI requestedUri = ex.getRequestURI();
        requestedUri = base.resolve(requestedUri);
        return requestedUri;
    }

    public static void redirectTo(HttpExchange ex, String redirect) {
        URI base = getRequestUri(ex);
        URI path;
        try {
            path = new URI(redirect);
            sendRedirect(ex, base.resolve(path));
        } catch (URISyntaxException | IOException e) {
            CreateRailwaysNavigator.LOGGER.error("Could not construct URI.", e);
        }
    }

    public static abstract class DLRestManager implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (GET.equals(exchange.getRequestMethod())) {
                ImmutableMap<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());

                try {
                    String response = run(exchange, new QueryContext(queryParams));
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    String response = "Error: " + e.toString();
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else if (POST.equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                String response = "Received: " + requestBody;
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1); // 405 Method Not Allowed
            }
        }

        public abstract String path();
        public abstract String run(HttpExchange exchange, QueryContext query);
    }
    // Query-Parameter parsen
    private static ImmutableMap<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return ImmutableMap.copyOf(params);
    }

    public static record QueryContext(ImmutableMap<String, String> queryParameters) {
        public boolean has(String key) {
            return queryParameters.containsKey(key);
        }

        public Optional<String> getValue(String key) {
            return has(key) ? Optional.of(queryParameters.get(key)) : Optional.empty();
        }

        public int getAsInt(String key, int fallback) {
            return getValue(key).map(x -> {
                try {
                    return Integer.parseInt(x);
                } catch (Exception e) {
                    return fallback;
                }
            }).orElse(fallback);
        }
        
        public float getAsFloat(String key, float fallback) {
            return getValue(key).map(x -> {
                try {
                    return Float.parseFloat(x);
                } catch (Exception e) {
                    return fallback;
                }
            }).orElse(fallback);
        }
        
        public double getAsDouble(String key, double fallback) {
            return getValue(key).map(x -> {
                try {
                    return Double.parseDouble(x);
                } catch (Exception e) {
                    return fallback;
                }
            }).orElse(fallback);
        }
        
        public long getAsLong(String key, long fallback) {
            return getValue(key).map(x -> {
                try {
                    return Long.parseLong(x);
                } catch (Exception e) {
                    return fallback;
                }
            }).orElse(fallback);
        }
        
        public boolean getAsBoolean(String key, boolean fallback) {
            return getValue(key).map(x -> {
                try {
                    return Boolean.parseBoolean(x);
                } catch (Exception e) {
                    return fallback;
                }
            }).orElse(fallback);
        }
        
        public String getAsString(String key, String fallback) {
            return getValue(key).orElse(fallback);
        }

        public <T> ImmutableList<T> getAsArray(String key, Function<String, T> parser) {
            if (!has(key)) {
                return ImmutableList.of();
            }
            String value = getValue(key).get();
            if (!value.startsWith("[") || !value.endsWith("]")) {
                return ImmutableList.of();
            }
            value = value.substring(1, value.length() - 1);
            List<T> list = new ArrayList<T>();
            String[] values = value.split(",");
            for (int i = 0; i < values.length; i++) {
                list.add(parser.apply(values[i]));
            }

            return ImmutableList.copyOf(list);
        }
    }
}