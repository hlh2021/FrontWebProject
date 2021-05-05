# FrontWebProject
# Get Started

FrontWebProject contains UI implementation to show the data analysis result in our WIRE 2019 publication:

[**On the Dynamics of User Engagement in News Comment Media**](https://www.researchgate.net/publication/337008981_On_the_dynamics_of_user_engagement_in_news_comment_media)

To run the code, the following environment is required:
* tomcat
* Java
* MySQL installed and database connection updated in config.properties. 

# Show Webpages

After tomcat started successfully, open the browser and enter

http://localhost:8080/FrontWebProject/show: show the page of this web application, then choose the topic to see the graphs and data.

http://localhost:8080/FrontWebProject/distribution.jsp: show the result of different data analysis, such as

1) User Continued Interest: the distribution of user's continued interest after the story disappears from Google News,

2) User Response Delay: the delay distribution of user response after article is published,

3) Comments Number over Time: comments number over time for story,

4) Comments Distribution with Story Change: comment distribution when the story appears and disappears in Google News,

5) User Interest Duration: the distribution of user interest duration after the first comment is posted,

6) Article Volume: the article volume for top stories,

7) Comment Volume: comment volume for top stories,

8) User Activity: show user number and comments number for each outlet.
