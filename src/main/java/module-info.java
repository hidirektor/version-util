module me.t3sl4.util.version {
    requires java.net.http;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;

    exports me.t3sl4.util.version;
    exports me.t3sl4.util.version.exception;
    exports me.t3sl4.util.version.model;
}