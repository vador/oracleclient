package org.dador.paddingOracleClient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Simple helper to perform queries on http server and get Good or Bad padding return info
 * Created by dame on 18/10/2016.
 */
public class PaddingOracleQuery {
    protected static final String TARGET_IP = "10.100.26.231"; //"78.236.213.102"; //"192.168.0.18"; //"localhost";
    protected static final Integer TARGET_PORT = 8080;
    protected static final String TARGET_PATH = "/cbc/po";
    protected static final String TARGET_PARAMETER_NAME = "path";


    private final CloseableHttpClient hgOraclePadClient = HttpClients.createDefault();

    /**
     * Sends query to Http server
     *
     * @param q String passed as argument to uri
     * @return Server Response Status Code
     */
    public boolean query(String q) throws IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(TARGET_IP)
                .setPort(TARGET_PORT)
                .setPath(TARGET_PATH)
                .setParameter(TARGET_PARAMETER_NAME, q)
                .build();

        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = hgOraclePadClient.execute(httpget);

        int statuscode = response.getStatusLine().getStatusCode();
        response.close();

        boolean isPaddingGood;
        switch (statuscode) {
            case 200: // legitimate message
                isPaddingGood = true;
                break;
            case 404: // good padding, bad request
                isPaddingGood = true;
                break;
            case 403: // wrong padding
                isPaddingGood = false;
                break;
            default: // server issue
                isPaddingGood = true;
        }
        return isPaddingGood;
    }

}
