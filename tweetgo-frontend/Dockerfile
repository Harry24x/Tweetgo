#STAGE 1: Build the React Application

FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build


#STAGE 2: Serve the Built Application with Nginx
FROM nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80.
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]