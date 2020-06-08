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
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  private ArrayList<String> comments = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Turn the messages ArrayList into a JSON string
    String json = convertToJsonUsingGson(comments);
    
    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // Get the new comment posted from the form
    String userCommentString = request.getParameter("user-comment");
    String trimmmedString = userCommentString.trim();

    // Add this new comment to the comments ArrayList
    comments.add(trimmmedString);

    // Redirect user back to the comments page.
    response.sendRedirect("/comments.html");
  }
  
  /**
   * Converts the Java array into a JSON string using Gson
   */
  private String convertToJsonUsingGson(ArrayList<String> comments) {
    return new Gson().toJson(comments);
  }

  /**
   * Converts the messages ArrayList into a JSON string using manual String concatentation.
   */
  private String convertToJson(ArrayList<String> array) {
    String json = "{";
    json += "\"comments\": [";

    if (array.size() == 0) {
      json += "]}";
      return json;
    }

    // Loop through comments array to create JSON string
    for (int i = 0; i < array.size(); i++){
      json += "\"" + array.get(i) + "\"";

      // If currently looking at last (or only item) in list
      if (i == array.size() - 1) {
        json += "]}";
      } else {
        json += ", ";
      }
    }
    return json;
  }
}
