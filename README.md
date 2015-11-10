# Week Three Assignment - Simple Twitter Client - **AYTweets**

**AYTweets** is an android Twitter client  that supports viewing a Twitter timeline and composing a new tweet.

Submitted by: **Andrey Yegorov**

Time spent: **10** hours spent in total

## User Stories

The following **required** functionality is completed:

* [X] User can sign in to Twitter using OAuth login
* [X] User can view the tweets from their home timeline
  - User should be displayed the username, name, and body for each tweet
  - User should be displayed the relative timestamp for each tweet "8m", "7h"
  - User can view more tweets as they scroll with infinite pagination
* [X] User can compose a new tweet:
  - User can click a “Compose” icon in the Action Bar on the top right
  - User can then enter a new tweet and post this to twitter
  - User is taken back to home timeline with new tweet visible in timeline

The following **additional** features are implemented:

* [X] Advanced: While composing a tweet, user can see a character counter with characters remaining for tweet out of 140
* [X] Advanced: Links in tweets are clickable and will launch the web browser (see autolink)
* [X] Advanced: User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

![Video Walkthrough](demo//AndroidGoogleImageSearchDemo.gif)

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
