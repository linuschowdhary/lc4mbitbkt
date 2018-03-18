package hsbc.core.servlets;

import com.day.cq.commons.jcr.JcrUtil;
import com.fasterxml.jackson.databind.SerializationFeature;
import hsbc.core.pojo.CSV;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.util.NodeUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths=/bin/servlet/loancalculatorconfig"
        })
public class LoanCalculatorConfigServlet extends SlingAllMethodsServlet {


    private static final long serialVersionUid = 1L;
    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/plain");
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource pageResource = resourceResolver.resolve(request.getParameter("pagePath") + "/jcr:content");
            returnJsonResponse(resp, pageResource);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void returnJsonResponse(SlingHttpServletResponse resp, Resource pageResource) throws JSONException, RepositoryException, IOException {
        Iterator<Resource> children = pageResource.getChildren().iterator();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        while (children.hasNext()) {
            JSONObject jObject = new JSONObject();
            Node node = children.next().adaptTo(Node.class);
            jObject.put("configName", node.getName());
            jObject.put("property_showCurrencyInput", node.hasProperty("property_showCurrencyInput") ? node.getProperty("property_showCurrencyInput").getBoolean() : false);
            jObject.put("csvFileUpload", node.hasProperty("csvFileUpload") ? "Uploaded" : "No file uploaded yet");
            jsonArray.put(jObject);
        }
        jsonObject.put("data", jsonArray);
        resp.getWriter().write(jsonObject.toString());
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request,
                          final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            Node configNode = null;
            ResourceResolver resourceResolver = request.getResourceResolver();
            final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
            PrintWriter out = response.getWriter();
            if (isMultipart) {
                final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
                final String action = IOUtils.toString(params.get("action")[0].getInputStream());
                final String pagePath = IOUtils.toString(params.get("pagePath")[0].getInputStream());
                Resource pageResource = resourceResolver.resolve(pagePath + "/jcr:content");
                if ("create".equals(action)) {
                    saveProperties(resourceResolver, params, pageResource);
                } else if ("edit".equals(action)) {
                    saveProperties(resourceResolver, params, pageResource);
                } else if ("delete".equals(action)) {
                    deleteConfig(resourceResolver, params, pageResource);
                }
                returnJsonResponse(response, pageResource);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void deleteConfig(ResourceResolver resourceResolver, Map<String, RequestParameter[]> params, Resource pageResource) throws IOException, RepositoryException {
        final String configName = IOUtils.toString(params.get("configName")[0].getInputStream());
        Node pageNode = pageResource.adaptTo(Node.class);
        String validNodeName = JcrUtil.createValidName(configName);
        Node node = pageNode.getNode(validNodeName);
        if (node != null) {
            node.remove();
            resourceResolver.commit();
        }

    }

    private void saveProperties(ResourceResolver resourceResolver, Map<String, RequestParameter[]> params, Resource pageResource) throws IOException, RepositoryException {
        Node configNode=null;
        final String configName = IOUtils.toString(params.get("configName")[0].getInputStream());
        String validNodeName = JcrUtil.createValidName(configName);
        Node pageNode = pageResource.adaptTo(Node.class);
        //write all key value pairs to node
        for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
            final String k = pairs.getKey();
            final RequestParameter[] pArr = params.get(k);
            final RequestParameter param = pArr[0];
            final InputStream stream = param.getInputStream();
            String content = IOUtils.toString(stream);
            if (pageNode.hasNode(validNodeName)) {
                configNode = pageNode.getNode(validNodeName);
            } else {
                configNode = pageNode.addNode(validNodeName);
            }



            configNode.setProperty(k, content);
            if(StringUtils.equals(k,"csvFileUpload")){
                //convertcsvtojson
                InputStream streams = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
                try (InputStream in = streams) {
                    CSV csv = new CSV(true, ',', in );
                    List< String > fieldNames = null;
                    if (csv.hasNext())
                        fieldNames = new ArrayList< >(csv.next());
                    List < Map < String, String >> list = new ArrayList < > ();
                    while (csv.hasNext()) {
                        List < String > x = csv.next();
                        Map < String, String > obj = new LinkedHashMap< >();
                        for (int i = 0; i < fieldNames.size(); i++) {
                            obj.put(fieldNames.get(i), x.get(i));
                        }
                        list.add(obj);
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    //mapper.writeValue(System.out, list);
                    configNode.setProperty("csvFileUploadJSON",mapper.writeValueAsString(list));
                }
            }
            resourceResolver.commit();
        }
    }
}
