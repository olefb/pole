https://github.com/olefb/pole

I refactored the repository to contain separate directories for the backend and frontend. I needed to add a DTO on the backend in order to get the objects received from the client to the type expected by the controller. I added environment variables to decide the API base URL depending on run mode.

I employed the use of the LLM Claude 4 Sonnet to help with CSS and some debugging.

# Issues encountered
My form was using `onsubmit={createPoll}`, but the default browser behaviour is to do a form POST while the JavaScript fetch is running. So I had to add `event.preventDefault();` in order for the form to wait for the fetch to finish.

I got weird duplication errors in my `VoteComponent`, solved by adding `#key` to the `poll.id` to force a re-render of the section when it changes. Apparently, this can happen when multiple reactive blocks interfere with each other.