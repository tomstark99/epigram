# epigram
---
# Update 1.04
This update brings a fresh look to epigram, the red has been changed to a white to give a cleaner and modern look with to match the current direction of android development
## Post model
- the post model now has a list of tags rather than singular tags to be able to display them all in the post
- small changes
## Added navigation drawer
- removed some firebase notification subscriptions
- added simple navigation drawer to main activity
- added a section activity to be used when clicking on a navigation drawer item
- new strings and dimensions
## Functional sections
sections are now functional
- the section you are in will be displayed in place of epigram in the app-bar
- tags within the article view now display all the tags added to the article not just one
- (beta) article list to do the same
## Fixed issues
- fixed various issues and app crashes
- added settings page
- added about page
## Post adapter refactor
- fixed an issue where the breaking news element would show up on multiple tabs on the main activity. This issue would be random from only the next tab to sometimes all of them adding in the breaking news element. This update completely refactors the code to break any contact the other tabs have with the breaking news element.

# Update 0.9
## search
- introduced a new way for updating the list using DiffUtil, gives smoother transition between searches
- search results now load faster (changed limit from 50 to 200)
- loading posts now more stable (added .retry() to the post manager call)
- introduced a new system to limit a GET request to 10 retries once no new articles are fetched
- added a search results tag to the top element of the search results
- cleaned up a previous issue of not loading next page when reaching the bottom

## UI changes
- minor padding fixes

## misc
- added layouts and placeholder code for experimental search feature
"search epigram" and "no results found"
- getting error 403 errors
- added icons for search placeholders

# Update 0.8
## Search
- search now finds correct posts
- fixed an issue where posts would disappear when typing out your search term
- fixed an issue where new posts would not be fetched when you reached the end of the list
- changed limit on getting search ids from all to 50 to fix an issue where a 414 error would occur
- search list now returns to the top when the search icon is pressed
- changed padding on search button on main screen from 10 to 12dp
- added possible redundant check for duplicate posts

## articles
- when viewing an article clicking epigram in app bar now returns article back to the top

## misc
- added showKeyboard to Utils.java
- added some more icons to drawables folder
- changed placeholder padding
