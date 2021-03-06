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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final Gson GSON = new Gson();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    
    // Maximum number of comments that can be shown on the screen
    int maxShowableComments = Integer.parseInt(request.getParameter("num-comments").trim());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> comments = new ArrayList<String>();
    List<String> emails = new ArrayList<String>();
    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comment-string");
      String email = (String) entity.getProperty("email");
      comments.add(comment);
      emails.add(email);
    }

    // Map to store user emails and comments (to eventually convert to Json object)
    Map<String, List<String>> emailsAndComments = new HashMap<String, List<String>>();

    List<String> userComments = new ArrayList<String>();
    List<String> userEmails = new ArrayList<String>();
    if (comments.size() <= maxShowableComments) {
      //  If there are less than [maxShowableComments] comments already stored
      for (int i = 0; i < comments.size(); i++) {
        userComments.add(comments.get(i).trim());
        userEmails.add(emails.get(i));
      }
    } else {
      // There are more than [maxShowableComments] comments stored already
      for (int i = 0; i < maxShowableComments; i++) {
        userComments.add(comments.get(i).trim());
        userEmails.add(emails.get(i));
      }
    }

    // Store emails and comments into Map
    emailsAndComments.put("emails", userEmails);
    emailsAndComments.put("comments", userComments);

    // Convert comments and emails in Map to Json using Gson
    String json = GSON.toJson(emailsAndComments);
    
    // Send the JSON as the response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    
    UserService userService = UserServiceFactory.getUserService();

    String userCommentString = request.getParameter("user-comment");
    String trimmmedString = userCommentString.trim();
    String email = userService.getCurrentUser().getEmail();
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment-string", trimmmedString);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("timestamp", timestamp);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect user back to the comments page.
    response.sendRedirect("/comments.html");
  }
}
