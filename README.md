# Epigram Android App

## About

The mobile Android App for the University of Bristol's Student Newspaper, built from the ground up using Kotlin with a Model View presenter design pattern. The app has many features all tied together with a beautiful user interface.

## Latest Release - 16/12/2020

### Version 1.9
Like and save articles

- saving an article will put the id in a list to be read later
- liking an article will show you articles based on the tags and authors of that article
- added default liked tags and author so that for you page loads
- added 'for you' tab to show personalised articles
- renamed liked to for you where 'personalised' suggestions appear based on liked articles
- kept liked articles for a place to easily unlike (while managing liked tags is not available)
- increased icon size for error messages
- new menu items for saved and liked posts
- added some more string
- renamed adapter for breaking news posts to align with other adapters
- Glide now only loads images into memory rather than on disk cache, drastically reducing disk usage
- Glide memory is cleared on application start
- Added dedicated author tag with increased text size
- added new element to be used when new articles are loading
- added list of authors to the bottom of articles (using the existing tag framework)
- authors are clickable to bring up their articles (using existing section framework)
- if author has a picture this will be displayed at top of the list
- post model now has authors and their slugs
- article placeholders will now have a rolling breath when loading rather than the progress circle
- extracted author tags to its own adapter
- dispose only being called for posts loaded with TabFragment
- slightly increased tag text size

## Previous Releases

### Version 1.8.1

- custom tab indicator
- added time icon back to post date
- removed redundant code
- fixed issue where film & tv was missing space in tag
- reduced custom tab indicator height
- padding fixes
- posts now display time since posted upto "6 days ago" after which defaults back to MMM d, YYYY format
- updated design of wear a mask element
- new search activity, search bar redesign. Now is floating and collapses on scroll with content scrolling under it when scrolling back up
- settings page now displays correct build version number
- minor padding and colour tweaks
- removed excess code
- converted placeholder adapter to kotlin codebase
- prepped search activity for conversion

### Version 1.8
Layout toggle

- fixed an issue where links were broken when reading an article. Related article links within articles still won't work
- added ability to toggle between default and beta channel layouts (effectively making the beta build obsolete)
- fixed an issue where news (previously covid) tab would not reload after initial load due to check for covid adapter being null
- fixed activity not reloading when layout setting changed (blanket fix look out for problems created by onRestart calling recreate)
- fixed an issue where changing layout preference would not properly reload the activity
- streamline dark mode to 2 tone vs 3 tone for a more consistent look
- fixed issue where layout preference was not being adopted by search results
- redesigned the wear a mask element to be more consistent
- mask dropdown is now more consistent, collapses and opens on entire bar not just arrow
- more reliable layout change when expanding and collapsing WAM element
- removed redundant background colour in nav menu
- extracted some string resources
- added extra settings dialog
- updated version code and name

### Version 1.7.1

- changed articles to new beta view

### Version 1.7
Post lockdown

- changed stay alert to stay safe
- moved covid-19 tab to sidebar
- added wear a mask drop down to appear when app is launched, will display on every 3rd app launch if the user collapses, every 5th if the user leaves it (experimental).

### Version 1.6.1

- migrated related to its own get
- updated tag removal of slugs 
- changed padding on copyright text to be less cluttered
- improved related stability

### Version 1.6
Improvements

- updated dependencies
- added get for related posts
- updated post model to include slugs as a pair with tags
- added linear layout around post image and title to add padding more uniformly for first element
- updated tag usage and tag adapter to correctly use the slug
- clicking a tag will display articles with that tag
- removed some excess code
- added related articles to bottom of article view (using same layout as breaking news scroller)

### Version 1.5.1
Breaking news fix

- fixed an issue where most recent post was being swallowed by breaking news
- fixed an issue where the app would crash if clicking the word breaking news
- changed coronavirus message to align to current guidelines
- fixed an issue where padding fix from first post element would get recycled to further elements when scrolling

### Version 1.5
Breaking news update

- updated build information
- changed breaking news request limit from 1 to 5
- added scrollable breaking news posts
- adjusted padding on pages without first element to align
- added placeholder loading to search
- changed snapping behaviour to move breaking news posts one at a time
- changed swipe refresh from red to white in dark mode
- added a whats-new pop up for breaking news scroller

### Version 1.4.2
Late covid-19 update

- toned down alert messages
- updated copyright information
- fixed text not being readable in about section when in light mode
- changed corner radius on placeholder to fit with alert messages
- changed alert message to have a nicer flow

### Version 1.4.1

- tweaks to covid-19 reporting

### Version 1.4

- added covid-19 reporting and advice

### Version 1.3.2

- moved the main tablayout to an MVP codebase
- hopefully a smoother experience when using the app
- added latest news for covid-19

### Version 1.3.1

- refactor api codebase
- refactor tab codebase
- refactor article adapters to fix an issue where breaking news articles would load into position 0 of different tabs

### Version 1.3
Notification update

- new way of handling incoming notifications
- last notification is remembered which should hopefully fix the issue of notification being showed more than once

### Version 1.2
Dark mode update

- added toggle for follow system
- fixed issue where activity was reloaded twice
- fixed issue where activity would break if current setting selected again
- fixed placeholder image not matching theme
- fixed appbar not snapping (especially in article view)
- appbar now only has elevation when scrolling, elevation disappears when at top of scrollview

### Version 1.1
Added the discount code for The Burger Joint. This code will appear the first three times you open the app and can also be found in the navigation view. This will programmatically disappear after the 15th of March when the offer ends

### Version 1.0
Release build of epigram features include:

- Read articles
- Search for articles and authors
- Filter articles by tag or section
