package com.futurepress.staticserver;

import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.SimpleWebServer;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class WebServer
  extends SimpleWebServer
{
  public WebServer(String paramString, int paramInt, File paramFile)
    throws IOException
  {
    super(paramString, paramInt, paramFile, true, "*");
    SimpleWebServer.mimeTypes().put("xhtml", "application/xhtml+xml");
    SimpleWebServer.mimeTypes().put("opf", "application/oebps-package+xml");
    SimpleWebServer.mimeTypes().put("ncx", "application/xml");
    SimpleWebServer.mimeTypes().put("epub", "application/epub+zip");
    SimpleWebServer.mimeTypes().put("otf", "application/x-font-otf");
    SimpleWebServer.mimeTypes().put("ttf", "application/x-font-ttf");
    SimpleWebServer.mimeTypes().put("js", "application/javascript");
    SimpleWebServer.mimeTypes().put("svg", "image/svg+xml");
  }
  
  protected boolean useGzipWhenAccepted(NanoHTTPD.Response paramResponse)
  {
    return (super.useGzipWhenAccepted(paramResponse)) && (paramResponse.getStatus() != NanoHTTPD.Response.Status.NOT_MODIFIED);
  }
}
