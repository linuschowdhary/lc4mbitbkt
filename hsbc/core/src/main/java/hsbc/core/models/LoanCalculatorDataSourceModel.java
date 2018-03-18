package hsbc.core.models;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import java.util.*;

@Model(adaptables = SlingHttpServletRequest.class)
public class LoanCalculatorDataSourceModel {

    @Inject
    @Named("sling:resourceType")
    @Default(values = "No resourceType")
    protected String resourceType;

    @Inject
    @ChildResource
    protected Resource datasource;

    private String message;


    @Inject
    protected ResourceResolver resolver;

    @Inject
    protected SlingHttpServletRequest request;


    @PostConstruct
    protected void init() {
        //set fallback
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

        ValueMap dsConfigProperties = ResourceUtil.getValueMap(datasource);

        //String dsPath = dsConfigProperties.get("path", String.class);
        String dsPath = "/content/hsbc/uk/configurations/loan-calculator/jcr:content";

        Resource dsResource = resolver.resolve(dsPath);
        ValueMap dsProperties = ResourceUtil.getValueMap(dsResource);

        List<Resource> resourceList = new ArrayList<>();
        ValueMap valueMap = null;


        Resource pageResource = resolver.resolve("/content/hsbc/uk/configurations/loan-calculator" + "/jcr:content");
        Iterator<Resource> children = pageResource.getChildren().iterator();


        while (children.hasNext()) {
            Resource child = children.next();
            ValueMap dsPropertiesValueMap = ResourceUtil.getValueMap(child);
            valueMap = new ValueMapDecorator(new HashMap<String, Object>());
            valueMap.put("value", child.getPath());
            valueMap.put("text", dsPropertiesValueMap.get("configTitle"));
            resourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", valueMap));
        }



        /*for (Map.Entry<String, Object> e : dsProperties.entrySet()) {
            if(e.getKey()){
                String key = e.getKey();
                String value = e.getValue().toString();
                valueMap = new ValueMapDecorator(new HashMap<String, Object>());

                valueMap.put("value", value);
                valueMap.put("text", key);
                resourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", valueMap));
            }
        }*/

        DataSource dataSource = new SimpleDataSource(resourceList.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);


    }
}
