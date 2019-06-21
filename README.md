# epigram


# Update 1.5
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

# Update 1.4
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
