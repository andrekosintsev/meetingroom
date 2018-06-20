package io.youngkoss.app.mapper;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

import io.youngkoss.app.utils.DateUtils;
import io.youngkoss.app.utils.MeettingConstant;

/**
 * Structure to keep day and 24 hours in this day
 */
@SuppressWarnings({ "hiding", "unchecked" })
public class TwentyTwoHours<E> {
   private final E a[];

   public TwentyTwoHours(Class<E> c) {
      final E[] a = (E[]) Array.newInstance(c, 24);
      this.a = a;
   }

   public E get(int i) {
      return a[i];
   }

   public void set(int i, E value) {
      a[i] = value;
   }

   public void removeAllEntries(String value) {
      for (E element : a) {
         if ((null != element) && element.equals(value)) {
            element = null;
         }
      }
   }

   public void removeAllEntries(int indexStart, int indexEnd) {
      for (int i = indexStart; i < indexEnd; i++) {
         a[i] = null;
      }
   }

   public boolean isSubarrayNotNull(int indexStart, int indexEnd) {
      for (int i = indexStart; i < indexEnd; i++) {
         if (null != a[i]) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      final StringBuilder builder = new StringBuilder();
      new HashSet<>(Arrays.asList(a)).forEach(new Consumer<E>() {
         @Override
         public void accept(E name) {
            if (null != name) {
               if (name instanceof String) {
                  final String[] resultPrintArray = ((String) name).split(MeettingConstant.SPACE_STRING);
                  final String endMeetingTime = DateUtils.df_HH_MM.print(DateUtils.df_HH_MM.parseDateTime(resultPrintArray[0])
                        .plusHours(Integer.valueOf(resultPrintArray[1])));
                  builder.append(resultPrintArray[0] + MeettingConstant.SPACE_STRING + endMeetingTime + MeettingConstant.SPACE_STRING + resultPrintArray[4] + MeettingConstant.ENDOFLINE_STRING);
               }
            }
         }
      });
      return builder.toString();
   }
}
