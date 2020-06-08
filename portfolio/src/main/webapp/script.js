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

const quoteContainer = document.getElementById('servlet-content');
const factContainer = document.getElementById('servlet-content');
const commentsSection = document.getElementById('comments-section');

const randomFactGenerator = () => {
    const facts = [
      'I used to ride dirtbikes up until 10th grade',
      'I can play the guitar (sort of)',
      'My favorite food is pizza',
      'I love riding my penny board',
      'My favorite city is New York'
    ];

    // Pick a random fact.
    const randomFact = facts[Math.floor(Math.random() * facts.length)];

    // Add it to the page.
    factContainer.innerText = randomFact;
};

/**
 * Fetches a the content from the server and adds it to the DOM.
 */
const getServletContent = () => {
    console.log('Fetching the content from the server.');

    const responsePromise = fetch('/data');
    responsePromise.then(handleResponse);
};

/**
 * Handles response by converting it to text and passing the result to
 * addQuoteToDom().
 */
const handleResponse = response => {
  console.log('Handling the response.');

  // response.text() returns a Promise, because the response is a stream of
  // content and not a simple variable.
  const textPromise = response.text();

  // When the response is converted to text, pass the result into the
  // addQuoteToDom() function.
  textPromise.then(addContentToDOM);
};

/** Prints a message to the DOM. */
const addContentToDOM = content => {
  console.log('Adding content to dom: ' + content);

  quoteContainer.innerText = content;
}; 

/** 
 * Prints a random message to the DOM using JSON sent from DataServlet.java.
 */
const fetchMessageUsingJSON = () => {
  fetch('/data').then(response => response.json()).then(messagesObj => {
    // messagesObj is an object, not a string, so we have to
    // reference its fields to create HTML content
    
    // Get random message from the the "messages" field
    // of messagesObj
    const messagesSize = messagesObj.messages.length;
    const message = messagesObj.messages[Math.floor(Math.random() * messagesSize)];

    // Add message to the page.
    factContainer.innerText = message;
  });
};

const showUserComments = () => {
  fetch('/data').then(response => response.json()).then(comments => {
    // messagesObj is an object, not a string, so we have to
    // reference its fields to create HTML content
    
    const commentsSize = comments.length;

    // If there are no comments
    if (commentsSize === 0)
    {
      return;
    } else {
      // Build the comments setion with all of the user comments, one after the other
      for (let i = 0; i < commentsSize; i++)
      {
        commentsSection.appendChild(createCommentElement(comments[i]));
        commentsSection.appendChild(createHrElement());
      }
    }
  });
};

/** Creates an <h4> element containing text. */
const createCommentElement = text => {
  const commentElement = document.createElement('h4');
  commentElement.innerText = text;
  return commentElement;
};

/** Creates an <hr> element to separate comments */
const createHrElement = () => {
  const hrElement = document.createElement('hr');
  return hrElement;
};
