# --- STAGE 1: Build the React Application ---
# Using the standard Node.js image for better compatibility with native modules.
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build


# --- STAGE 2: Serve the Built Application with Nginx ---
# This stage remains the same.
FROM nginx:1.25-alpine

# Copy the built static files from the 'build' stage.
COPY --from=build /app/dist /usr/share/nginx/html

# Copy the custom Nginx configuration to handle client-side routing.


# Expose port 80.
EXPOSE 80

# The default CMD for the Nginx image will start the server.
CMD ["nginx", "-g", "daemon off;"]