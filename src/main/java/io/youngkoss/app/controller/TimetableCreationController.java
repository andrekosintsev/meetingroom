package io.youngkoss.app.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.youngkoss.app.mapper.TwentyTwoHours;
import io.youngkoss.app.utils.DateUtils;
import io.youngkoss.app.utils.MeettingConstant;

/**
 * Main huge godclass, cause actually nothing is required to save data or to handle previous request, so I ve made a decision to write almost everything in one controller one method
 */
@SuppressWarnings("nls")
@RestController
public class TimetableCreationController {

   private static final Logger LOGGER = LoggerFactory.getLogger(TimetableCreationController.class.getName());

   private final Map<String, TwentyTwoHours<String>> dateSchedulings = new ConcurrentHashMap<>();

   // just to check if another process is make a calculation right now to prevent additional calls
   private final AtomicBoolean isProcess = new AtomicBoolean(false);

   @SuppressWarnings("null")
   @RequestMapping(method = RequestMethod.POST, value = "/timetable-creation")
   public ResponseEntity<String> timeTable(HttpEntity<String> httpEntity) throws Exception {
      if (isProcess.get()) {
         return new ResponseEntity<>(HttpStatus.IM_USED);
      }
      isProcess.set(true);
      // get raw string from request body plain/text
      final String rawString = httpEntity.getBody();
      final String[] arrayRequestParam = rawString.split(MeettingConstant.ENDOFLINE_STRING);

      // just simple validation to prevent crazy data in
      if ((null == arrayRequestParam) || (arrayRequestParam.length == 0)) {
         isProcess.set(false);
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      final String[] range = arrayRequestParam[0].split(MeettingConstant.SPACE_STRING);

      final DateTime startDt = DateUtils.df_HHMM.parseLocalTime(range[0])
            .toDateTimeToday();
      final DateTime endDt = DateUtils.df_HHMM.parseLocalTime(range[1])
            .toDateTimeToday();
      int k = 1;
      String firstLine = null;
      for (int i = 1; i < arrayRequestParam.length; i++) {

         // First string in block of request
         if (k % 2 != 0) {
            firstLine = arrayRequestParam[i];
            k++;
            continue;
         }

         final String[] request = arrayRequestParam[i].split(MeettingConstant.SPACE_STRING);
         if ((request == null) || request.length < 3) {
            LOGGER.error("Invalid request parameters provided");
            isProcess.set(false);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }

         // to do not make everytime request[0]
         final String dateOfMeetingString = request[0];

         // to do not make everytime request[1]
         final String startHoursString = request[1];

         // to do not make everytime request[2] and conversion to int
         final int duration = Integer.valueOf(request[2]);

         // startTime and endTime of meeting
         final DateTime startDtLocal = DateUtils.df_HH_MM.parseLocalTime(startHoursString)
               .toDateTimeToday();
         final DateTime endDtLocal = startDtLocal.plusHours(duration);

         // if value in request is not match office hours we just skip this
         if (startDtLocal.isBefore(startDt) || endDtLocal.isAfter(endDt) || startDtLocal.isAfter(endDt) || endDtLocal.isBefore(startDt)) {
            LOGGER.warn("We need to skip this value, cause requested booking time is not valid {} {}", firstLine, request);
            firstLine = null;
            k = 1;
            continue;
         }
         // if date of meeting not present in current array we just create a new key and populate all values in array
         if (dateSchedulings.get(dateOfMeetingString) == null) {
            final TwentyTwoHours<String> value = new TwentyTwoHours<>(String.class);
            for (int hour = 0; hour < duration; hour++) {
               value.set(startDtLocal.getHourOfDay() + hour, startHoursString + MeettingConstant.SPACE_STRING + duration + MeettingConstant.SPACE_STRING + firstLine);
            }
            dateSchedulings.put(dateOfMeetingString, value);
            firstLine = null;
            k = 1;
            continue;
         }

         final TwentyTwoHours<String> value = dateSchedulings.get(dateOfMeetingString);

         // check if time slot is still empty, then just populate it
         if (!value.isSubarrayNotNull(startDtLocal.getHourOfDay(), startDtLocal.getHourOfDay() + duration)) {
            for (int hour = 0; hour < duration; hour++) {
               value.set(startDtLocal.getHourOfDay() + hour, startHoursString + MeettingConstant.SPACE_STRING + duration + MeettingConstant.SPACE_STRING + firstLine);
            }
            firstLine = null;
            k = 1;
            continue;
         }

         // check if the time slot consists of values where request are early than current value
         boolean isRequestEarlyThenCurrent = false;
         for (int hour = startDtLocal.getHourOfDay(); hour < startDtLocal.getHourOfDay() + duration; hour++) {
            if (value.get(hour) != null) {
               // get request hours from result in array
               final String[] arrayInside = value.get(hour)
                     .split(MeettingConstant.SPACE_STRING);
               // get request hours from raw data for current value
               final String[] firstLineSplit = firstLine.split(MeettingConstant.SPACE_STRING);
               if (DateUtils.df_YYYY_MM_DD_HH_MM_SS.parseDateTime(arrayInside[2] + MeettingConstant.SPACE_STRING + arrayInside[3])
                     .isBefore(DateUtils.df_YYYY_MM_DD_HH_MM_SS.parseDateTime(firstLineSplit[0] + MeettingConstant.SPACE_STRING + firstLineSplit[1]))) {
                  isRequestEarlyThenCurrent = true;
               }
            }
         }
         // if something exist then do not populate such call at all and skip it
         if (isRequestEarlyThenCurrent) {
            firstLine = null;
            k = 1;
            continue;
         }

         for (int hour = startDtLocal.getHourOfDay(); hour < startDtLocal.getHourOfDay() + duration; hour++) {
            if (value.get(hour) != null) {
               // get request hours from result in array
               final String[] arrayInside = value.get(hour)
                     .split(MeettingConstant.SPACE_STRING);

               final int startIndexLocal = Integer.valueOf(arrayInside[0].split(MeettingConstant.DDT_REGEX)[0]);
               final int durationLocal = Integer.valueOf(arrayInside[1]);

               value.removeAllEntries(startIndexLocal, startIndexLocal + durationLocal);

            }

         }
         // once subarray clear we just populate value
         for (int hour = 0; hour < duration; hour++) {
            value.set(startDtLocal.getHourOfDay() + hour, startHoursString + MeettingConstant.SPACE_STRING + duration + MeettingConstant.SPACE_STRING + firstLine);
         }
         firstLine = null;
         k = 1;

      }

      final StringBuilder builderResponse = new StringBuilder();

      dateSchedulings.forEach((key, value) -> {
         builderResponse.append(key + MeettingConstant.ENDOFLINE_STRING);
         builderResponse.append(value.toString());
      });
      dateSchedulings.clear();
      isProcess.set(false);

      return new ResponseEntity<>(builderResponse.toString(), HttpStatus.OK);
   }

   /**
    * @param httpEntity
    * @return pong for test only endpoint
    * @throws Exception
    */
   @RequestMapping(method = RequestMethod.GET, value = "/ping")
   public ResponseEntity<String> index(HttpEntity<String> httpEntity) throws Exception {
      return new ResponseEntity<>("pong", HttpStatus.OK);
   }

}
