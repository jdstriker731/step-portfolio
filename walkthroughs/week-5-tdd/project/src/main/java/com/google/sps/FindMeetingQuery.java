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
    // The Collection that stores the available times for the requested meeting
    Collection<TimeRange> availableTimes = new ArrayList<>();

    // Some type of collection to store all of the (unsorted) unavailable meeting times 
    Collection<TimeRange> unavailableTimesSet = new HashSet<>();

    // Check to see if the duration is longer than a day
    if (request.getDuration() == TimeRange.WHOLE_DAY.duration() + 1) {
      return availableTimes;
    }


    /* For each of the requested attendees, loop through all of the events. For each event,
       If that particular required attendee is attending it, add that meeting time slot to 
       The collection of unavailable meeting times
    */
    // Iterate over the HashSet of attendees in request.attendees 
    for (String requiredAttendee : request.getAttendees()) {
      // Loop through all of the events that are going on that day
      for (Event currEvent : events) {
        // Check to see if requiredAttendee is among the attendees at this particular event
        if (currEvent.getAttendees().contains(requiredAttendee)) {
            unavailableTimesSet.add(currEvent.getWhen());
        }
      }
    } 

    /* Sort all the unavailabe meeting times (i.e TimeRanges) in order of start time
       (earliest start time first)
    */
    List<TimeRange> unfixedSortedUnavailableTimes = new ArrayList<>();
    unfixedSortedUnavailableTimes.addAll(unavailableTimesSet);
    Collections.sort(unfixedSortedUnavailableTimes, TimeRange.ORDER_BY_START);


    /* See if there's overlap in the unavailable times and fix them in new array*/
    List<TimeRange> sortedUnavailableTimes = new ArrayList<>();

    // 0: last unavailable time in unfixedSortedUnavailableTimes didn't overlap with any before it
    int statusFlag = 0; 
    int curr = 0;
    int next = curr + 1;
    TimeRange currRange;
    TimeRange nextRange;
    if (unfixedSortedUnavailableTimes.size() > 0) {
      while (next != unfixedSortedUnavailableTimes.size()) {
        currRange = unfixedSortedUnavailableTimes.get(curr);
        nextRange = unfixedSortedUnavailableTimes.get(next);
    
        if (!currRange.overlaps(nextRange)) {
          sortedUnavailableTimes.add(currRange);
          curr++;
          next++;
          continue;
        }

        while (currRange.overlaps(nextRange)) {
          next++;
          if (next == unfixedSortedUnavailableTimes.size()) {
            break;
          } else {
            nextRange = unfixedSortedUnavailableTimes.get(next);
          }
        }

        // If next is now the size of unfixedSortedUnavailableTimes, currRange overlaps 
        // With all of the following events
        if (next == unfixedSortedUnavailableTimes.size()) {
          statusFlag = 1;
        
          next--;
          // reset back to the most recent range that overlap
          nextRange = unfixedSortedUnavailableTimes.get(next); 
        
          // The current range fully contains the last thing it overlaps with
          if (currRange.contains(nextRange)) {
            sortedUnavailableTimes.add(TimeRange.fromStartEnd(currRange.start(), currRange.end(), false));
            next++;
            continue;
          } else {
            sortedUnavailableTimes.add(TimeRange.fromStartEnd(currRange.start(), nextRange.end(), false));
            next++;
            continue;
          }
        } else {
          next--;
          // reset back to the most recent range that overlap
          nextRange = unfixedSortedUnavailableTimes.get(next); 
        
          // The current range fully contains the last thing it overlaps with
          if (currRange.contains(nextRange)) {
            sortedUnavailableTimes.add(TimeRange.fromStartEnd(currRange.start(), currRange.end(), false));
            curr = next + 1;
            next = curr + 1;
            continue;
          } else {
            sortedUnavailableTimes.add(TimeRange.fromStartEnd(currRange.start(), nextRange.end(), false));
            curr = next + 1;
            next = curr + 1;
            continue;
          }  
        }   
      }
    }

    // If the status flag is still 0, this the last element in unfixedSortedUnavailableTimes didn't
    // Overlap with anything before it 
    if (statusFlag == 0 && unfixedSortedUnavailableTimes.size() >= 1) {
      sortedUnavailableTimes.add(unfixedSortedUnavailableTimes.get(unfixedSortedUnavailableTimes.size() - 1));
    }
   
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

      // Check to see if the first unavailable TimeRange is after START_OF_DAY and there's space to 
      // Fit the requested meeting before then
      TimeRange firstMeeting = sortedUnavailableTimes.get(0);
      
      if ((TimeRange.START_OF_DAY < firstMeeting.start()) 
        && (request.getDuration() <= (firstMeeting.start() - TimeRange.START_OF_DAY))) {
        // Add this time chunk to availableTimes
        availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, firstMeeting.start(), false));
      }
      
      // Loop through the list of sorted unavailable TimeRanges to see if the spaces between them can 
      // Fit the meeting
      for (int i = 0; i < sortedUnavailableTimes.size(); i++) {
        // If current TimeRange in loop is the last unavailable TimeRange
        if (i == (sortedUnavailableTimes.size() - 1)) {
          break;
        }

        // Current unavailable TimeRange being looked at in loop
        TimeRange currentMeeting = sortedUnavailableTimes.get(i);
        // Next TimeRange after the current unavailable TimeRange
        TimeRange nextMeeting = sortedUnavailableTimes.get(i + 1);
        
        // If requested meeting just fits in between these two, break after adding to availableTimes
        if (request.getDuration() == (nextMeeting.start() - currentMeeting.end())) {
          availableTimes.add(TimeRange.fromStartEnd(currentMeeting.end(), nextMeeting.start(), false));
          break;
        } else if (request.getDuration() <= (nextMeeting.start() - currentMeeting.end())) {
          availableTimes.add(TimeRange.fromStartEnd(currentMeeting.end(), nextMeeting.start(), false));
        }
      }

      // Check to see if the last unavailable TimeRange is before END_Of_DAY and there's space to 
      // Fit the requested meeting after it
      TimeRange lastMeeting = sortedUnavailableTimes.get(sortedUnavailableTimes.size() - 1);

      if ((lastMeeting.end() < TimeRange.END_OF_DAY) 
        && (request.getDuration() <= (TimeRange.END_OF_DAY - lastMeeting.end()))) {
        availableTimes.add(TimeRange.fromStartEnd(lastMeeting.end(), TimeRange.END_OF_DAY, true));
      }
    }

    return availableTimes;
  }
}
