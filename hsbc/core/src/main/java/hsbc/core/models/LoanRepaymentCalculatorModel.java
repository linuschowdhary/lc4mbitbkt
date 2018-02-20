/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package hsbc.core.models;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.settings.SlingSettingsService;

import java.util.HashMap;
import java.util.Map;

@Model(adaptables=SlingHttpServletRequest.class)
public class LoanRepaymentCalculatorModel {

    @Inject
    private SlingSettingsService settings;

/*
    @Inject @Named("config") @Default(values="No resourceType")
    protected String configPath;
*/

    @Inject
    protected ResourceResolver resolver;

    @Inject
    protected Resource resource;

    private HashMap hashMap=null;

    @PostConstruct
    protected void init() {
    ValueMap resourceValueMap = ResourceUtil.getValueMap(resource);
    String configPath=resourceValueMap.get("config", "");
    if(StringUtils.isNotEmpty(configPath)) {
        Resource configResource = resolver.resolve(configPath);
        ValueMap valueMap = ResourceUtil.getValueMap(configResource);
        hashMap = new HashMap<String, Boolean>();
        for (Map.Entry<String, Object> e : valueMap.entrySet()) {
            if (e.getKey() != "jcr:primaryType") {
                String key = e.getKey();
                Boolean value = Boolean.parseBoolean(e.getValue().toString());
                hashMap.put(key, value);
            }
        }
    }
    }

    public HashMap getProperties(){
        return hashMap;
    }

}
