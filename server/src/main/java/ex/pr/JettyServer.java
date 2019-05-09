package ex.pr;


import ex.pr.handler.GetClientTokenServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Hello world!
 */
public class JettyServer {

    private static ArrayList<URL> providerList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Server server = new Server(8081);
            initProviderList();
            Thread newFeedsUpdate = new Thread(new NewFeedsUpdate(providerList));
            newFeedsUpdate.start();
            //setServlet(server);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setServlet(Server server) throws Exception {
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(GetClientTokenServlet.class, "/GetClientTokenServlet");
        server.setHandler(handler);
        server.start();
        server.join();
    }

    private static void initProviderList() {
        try {
            providerList.add(new URL("http://www.ynet.co.il/Integration/StoryRss2.xml"));
            providerList.add(new URL("http://rss.walla.co.il/feed/1?type=main"));
            providerList.add(new URL("http://rcs.mako.co.il/rss/31750a2610f26110VgnVCM1000005201000aRCRD.xml"));
            providerList.add(new URL("http://www.inn.co.il/Rss.aspx?act=0.1"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
