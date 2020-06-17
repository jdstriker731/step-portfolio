// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method.");

    // The Collection that stores the available times for the requested meeting
    Collection<TimeRange> availableTimes = new ArrayList<>();

    // Some type of collection to store all of the (unsorted) unavailable meeting times 
    Collection<TimeRange> unavailableTimesSet = new HashSet<>();

    // Check to see if the duration is longer than a day
    if (request.getDuration() == TimeRange.WHOLE_DAY.duration() + 1) {
      return availableTimes;
    }


    /* For each of the requested attendees, loop through all of the events. For each event if that
       particular required attendee is attending it, add that meeting time slot to the collection
       of unavailable meeting times
    */
    // Iterate over the HashSet of attendees in request.attendees 
    for (String requiredAttendee : request.getAttendees()) {
      // Loop through all of the events that are going on that day
      for (Event currEvent : events) {
        // Check to see if requiredAttendee is among the attendees at this particular event
        if (currEvent.getAttendees().contains(requiredAttendee)) {
            unavailableTimesSet.add(currEvent.getWhen());
        }
        /*
        // Check to see if requiredAttendee is among the attendees at this particular event
        Event currentEvent = events.get(i);
        for (int j = 0; j < currentEvent.attendees.size(); j++) {
          if ((currentEvent.attendees.get(j).equals(requiredAttendee)) && (!(unsortedUnavailableTimes.contains(currentEvent.when)))) {
            unsortedUnavailableTimes.add(currentEvent.when);
          }
        }
        */
      }
    } 

    /* Sort all the unavailabe meeting times (i.e TimeRanges) in order of start time
       (earliest start time first)
    */
    List<TimeRange> sortedUnavailableTimes = new ArrayList<>();
    sortedUnavailableTimes.addAll(unavailableTimesSet);
    Collections.sort(sortedUnavailableTimes, TimeRange.ORDER_BY_START);



    /* Take sorted unavailable TimeRanges and see if any overlap. If they overlap, merge them properly */
    

   
    /* From the earliest possible time to the latest possible time, check to see if what spaces in between 
       unavailable time slots can fit the requested meeting's duration. If that gap can fit the meeting,
       then put the entire gap in the Collection<TimeRange> to be returned 
    */
    if (sortedUnavailableTimes.isEmpty())
    {
      // The whole day is available
      availableTimes.add(TimeRange.WHOLE_DAY);
    } else {
      // There are times that the required attendees are unavailable; 
      // Find chuncks of time that can fit the requested meeting

      // Check to see if the first unavailable TimeRange is after START_OF_DAY and there's space to fit the requested meeting before then
      TimeRange firstMeeting = sortedUnavailableTimes.get(0);
      
      if ((TimeRange.START_OF_DAY < firstMeeting.start()) && (request.getDuration() <= (firstMeeting.start() - TimeRange.START_OF_DAY))) {
        // Add this time chunk to availableTimes
        availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, firstMeeting.start(), false));
      }

      // Check to see if the last unavailable TimeRange is before END_Of_DAY and there's space to fit the requested meeting after it
      TimeRange lastMeeting = sortedUnavailableTimes.get(sortedUnavailableTimes.size() - 1);

      if ((lastMeeting.end() < TimeRange.END_OF_DAY) && (request.getDuration() <= (TimeRange.END_OF_DAY - lastMeeting.end()))) {
        availableTimes.add(TimeRange.fromStartEnd(lastMeeting.end(), TimeRange.END_OF_DAY, true));
      }
    }

    return availableTimes;
  }
}
