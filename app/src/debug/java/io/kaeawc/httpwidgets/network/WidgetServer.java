package io.kaeawc.httpwidgets.network;

import java.util.logging.Logger;

import io.kaeawc.httpwidgets.core.HttpServer;
import timber.log.Timber;

public class WidgetServer extends HttpServer {

    private static final Logger LOG = Logger.getLogger(WidgetServer.class.getName());

    public WidgetServer() {
        super(8080);
        Timber.v("Running on port 8080");
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        WidgetServer.LOG.info(method + " '" + uri + "' ");
        return newFixedLengthResponse("Hello Server");
    }
}
