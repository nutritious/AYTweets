# Week Four Assignment - Extended Twitter Client - **AYTweets**

**AYTweets** is an android Twitter client  that supports viewing a Twitter timeline, composing a new tweet, see user profiles and mentions timeline.

Submitted by: **Andrey Yegorov**

Time spent: **10** hours spent in total

## User Stories

The following **required** functionality is completed:
  
* [X] Includes all required user stories from Week 3 Twitter Client
* [X] User can switch between Timeline and Mention views using tabs.
  - User can view their home timeline tweets.
  - User can view the recent mentions of their username.
* [X] User can navigate to view their own profile
  - User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [X] User can click on the profile image in any tweet to see another user's profile.
  - User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
  - Profile view should include that user's timeline
* [X] User can infinitely paginate any of these timelines (home, mentions, user) by scrolling to the bottom


The following **additional** features are implemented:

* [X] Advanced: Robust error handling, check if internet is available, handle error cases, network failures
* [X] Advanced: When a network request is sent, user sees an indeterminate progress indicator

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

![Video Walkthrough](demo//AndroidTwitterDemo2.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

- Android studio is rather buggy with breakpoint - can't set them up inside network event handlers (used to work)

## License

    Copyright [2015] [nutritious]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
