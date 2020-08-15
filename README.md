# NewsApp - Native Android

*Created using Java and XML in Android Studio. Backend is the same as [NewsWebsite](https://github.com/anerishah97/NewsWebsite/tree/master/Express-backend)*

Check out the Youtube video of the app:
<a href="https://youtu.be/bOomrXhHyl0"  target="_blank">
<img src = "/readme-images/youtube-video.PNG" alt="NewsApp Youtube"/>
</a>

APIs Used:
- [Guardian](https://open-platform.theguardian.com/)
- [Bing Autosuggest](https://azure.microsoft.com/en-us/services/cognitive-services/autosuggest/)


## Features:

### Current weather, with top news

<img src = "/readme-images/home_screen.png" alt="Home Screen" width="300px"/>

### Category wise news

A swipeable tab layout with category wise news

<img src = "/readme-images/categories.png" alt="Category wise news" width="300px"/>

### Trending chart

A chart implemented using the [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) library

<img src = "/readme-images/trending.png" alt="Trending" width="300px"/>

### Detailed Article View

Clicking on an article will open a detailed view of the article. One can either share or bookmark the article from here. 

<img src = "/readme-images/detailedarticle.png" alt="DetailedArticle" width="300px"/>

### Bookmarks

An article can be bookmarked on the home screen, category wise tabs, detailed articles or search results. All the bookmarked articles appear in the Bookmarks Fragment. The feature is implemented using localStorage

<img src = "/readme-images/bookmarks.png" alt="Bookmarks" width="300px"/>

### Autocomplete

Autocomplete textview for article search, implemented using the Bing Autosuggest API for suggestions

<img src = "/readme-images/autosuggest.png" alt="Autosuggest" width="300px"/>

### Search

Search for a keyword in an article

<img src = "/readme-images/search.png" alt="Search" width="300px"/>
