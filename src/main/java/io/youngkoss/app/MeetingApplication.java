package io.youngkoss.app;

import java.nio.charset.Charset;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedRuntimeException;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * application starter to use a properly jar arguments -Duser.timezone=UTC and -Dfile.encoding=UTF-8 must be provided
 */
@SpringBootApplication(scanBasePackages = { "io.youngkoss.app" })
@EnableScheduling
public class MeetingApplication {

   private static final Logger APP_LOGGER = LoggerFactory.getLogger(MeetingApplication.class);

   public static void main(final String[] args) throws Exception {
      // utc, etc/utc, universal & zulu must be equal. they all have the display name "Coordinated Universal Time" in the jvm.
      if (!TimeZone.getTimeZone("UTC") //$NON-NLS-1$
            .getID()
            .equals(TimeZone.getDefault()
                  .getID())) {

         APP_LOGGER.error("JVM does not have UTC timeZone. It is: '" + TimeZone.getDefault() //$NON-NLS-1$
               .getDisplayName() + "'. please add param to your app: '-Duser.timezone=UTC'"); //$NON-NLS-1$
         System.exit(0);
         return;
      }

      final String defaultCharset = Charset.defaultCharset()
            .displayName();
      if (!defaultCharset.equals("UTF-8")) { //$NON-NLS-1$
         APP_LOGGER.error("We work only with UTF-8))). It has now: " + defaultCharset + //$NON-NLS-1$
               ". please also specify: '-Dfile.encoding=UTF-8'"); //$NON-NLS-1$

         System.exit(0);
      }

      ConfigurableApplicationContext context = null;
      try {
         context = SpringApplication.run(MeetingApplication.class, args);
      } catch (final RuntimeException e) {
         final ExitCodeGen exitCodeGenerator = new ExitCodeGen(getRootCauseThrowable(e));
         if (context != null) {
            final int exit = SpringApplication.exit(context, exitCodeGenerator);
            if (exit == 0) {
               System.exit(exit);
            }
         } else {
            System.exit(0);
         }
      }

   }

   private static Throwable getRootCauseThrowable(final RuntimeException e) {
      if (e instanceof NestedRuntimeException) {
         return ((NestedRuntimeException) e).getRootCause();
      }
      return e;
   }

}