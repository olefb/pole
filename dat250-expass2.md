# DAT250 Assignment 2

https://github.com/olefb/pole

The basic polling functionality has been implemented, using `HashMaps` and `AtomicLong` for IDs for users, polls, vote options and votes in order to ensure uniqueness. The `PollManager` class provides the CRUD methods, while the `Controller` classes handle HTTP requests.

I initially had some issues with Gradle, due to previously having used it for a Kotlin project. I ended up having to delete its cache for it to redownload/initialize for this Java project.

I started out using curl for REST requests, but found that setting up Bruno was worth the effort to do them more efficiently.

Current project limitations include relying on in-memory storage (causing data loss on restart), no security, minimal validation/error handling, no multi-choice polls, and no frontend.

The optional steps are not yet attempted.