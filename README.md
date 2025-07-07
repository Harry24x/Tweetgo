TweetGo – A Microblogging App

FEATURES :
User registration
User login/logout 
User search 
Follow/unfollow users 
Create posts 
Like/unlike tweets
View own posts 
View others’ posts

EXTRA FEATURES :
unlike posts
Delete posts
Comment/delete comments
Paginated timelines
Hashtag

Ports and URLs
• Frontend development server: [http://localhost:80](http://localhost:80) , [http://localhost](http://localhost)
• Frontend production build (Docker): [http://localhost:80](http://localhost:80)
• Backend API: [http://localhost:8080](http://localhost:8080)
• Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
• MariaDB (development): localhost:3306, database name “tweet-go”, user “root”, password “asdf1234”

steps:
1. Clone the repo and navigate into backend and frontend directories.
2. Backend:
    mvn clean install
    docker-compose up -d --build
3. Frontend:
    cd myproject-frontend
    npm install
    npm run dev
4. Browse the app at [http://localhost:5173](http://localhost:5173) and 
5. the API docs at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

