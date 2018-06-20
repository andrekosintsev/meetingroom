package io.youngkoss.app;

import javax.cache.CacheManager;
import javax.cache.Caching;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * I ve recognised that in this task I no need to cache any calls or data or save this in db and then grab it from, hence I just lazy to remove this class
 */
@Deprecated
@SuppressWarnings("nls")
@Configuration
@PropertySource("classpath:application.properties")
public class CacheManagerConfig {

   private static final Logger LOGGER = LoggerFactory.getLogger(CacheManagerConfig.class.getName());

   @Bean
   public CacheManager cacheManager() {
      final EhcacheCachingProvider provider = (EhcacheCachingProvider) Caching.getCachingProvider();
      CacheManager manager = null;
      final String xmlClassPath = System.getProperty("jsr107.config.classpath", "ehcache.xml");
      try {
         manager = provider.getCacheManager(Thread.currentThread()
               .getContextClassLoader()
               .getResource(xmlClassPath)
               .toURI(),
               Thread.currentThread()
                     .getContextClassLoader());
      } catch (final Exception e) {
         LOGGER.error(e.getMessage(), e);
         manager = null;
      }
      return manager;
   }

}
