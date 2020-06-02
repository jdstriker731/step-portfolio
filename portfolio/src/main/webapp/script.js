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
    const factContainer = document.getElementById('fact-container');
    factContainer.innerText = randomFact;
};

/**
 * Fetches a the content from the server and adds it to the DOM.
 */
const getServletContent = () => {
    console.log('Fetching the content from the server.');

    // The fetch() function returns a Promise because the request is asynchronous.
    const responsePromise = fetch('/data');

    // When the request is complete, pass the response into handleResponse().
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

/** Adds a random quote to the DOM. */
const addContentToDOM = content => {
  console.log('Adding content to dom: ' + content);

  const quoteContainer = document.getElementById('servlet-content');
  quoteContainer.innerText = content;
};