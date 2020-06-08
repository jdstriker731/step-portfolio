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

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  private ArrayList<String> messages;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    messages = new ArrayList<String>();
    messages.add("Hello, world! You\'re visiting my portfolio site");
    messages.add("It's a pretty cloudy day in Brooklyn, isn\'t it?");
    messages.add("This is my first summer as a Google Intern! I\'m so excited!");
    messages.add("I wonder what cool web development skills I\'ll gain this summer");
    messages.add("Serious question now: pineapple on pizza?");

    // Turn the messages ArrayList into a JSON string
    String json = convertToJson(messages);
    
    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Converts the messages ArrayList into a JSON string using manual String concatentation.
   */
  private String convertToJson(ArrayList<String> array) {
    String json = "{";
    json += "\"messages\": [";
    json += "\"" + array.get(0) + "\"";
    json += ", ";
    json += "\"" + array.get(1) + "\"";
    json += ", ";
    json += "\"" + array.get(2) + "\"";
    json += ", ";
    json += "\"" + array.get(3) + "\"";
    json += ", ";
    json += "\"" + array.get(4) + "\""; 
    json += "]}";
    return json;
  }
}
