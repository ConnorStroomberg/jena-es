package org.drugis.rdf.versioning.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.drugis.rdf.versioning.server.messages.BooleanResultMessageConverter;
import org.drugis.rdf.versioning.server.messages.JenaGraphMessageConverter;
import org.drugis.rdf.versioning.server.messages.JenaResultSetMessageConverter;
import org.drugis.rdf.versioning.store.EventSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.hp.hpl.jena.tdb.TDBFactory;

@Configuration
public class Config extends WebMvcAutoConfigurationAdapter {
    public static final String BASE_URI = "http://example.com/"; // FIXME
	@Value("${EVENT_SOURCE_URI_PREFIX}") private String uriPrefix;

    @Bean
    public String datasetHistoryQuery() throws IOException {
    	Resource resource = new ClassPathResource("/org/drugis/rdf/versioning/datasetHistory.sparql");
		return FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
    }
    
    @Bean
    public String datasetInfoQuery() throws IOException {
    	Resource resource = new ClassPathResource("/org/drugis/rdf/versioning/datasetInfo.sparql");
		return FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
    }
    
    @Bean
    public String versionInfoQuery() throws IOException {
    	Resource resource = new ClassPathResource("/org/drugis/rdf/versioning/versionInfo.sparql");
		return FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
    }
	
	@Bean
	public EventSource eventSource() {
		return new EventSource(TDBFactory.createDatasetGraph("DB"), uriPrefix);
	}

	@Bean
	CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		return filter;
	} 
	
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new JenaGraphMessageConverter());
        converters.add(new JenaResultSetMessageConverter());
        converters.add(new BooleanResultMessageConverter());
        super.configureMessageConverters(converters);
    }
}
