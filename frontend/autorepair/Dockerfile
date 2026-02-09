# Build Stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm cache clean --force && npm ci
COPY . .
RUN npm run build

# Serve Stage
FROM nginx:stable-alpine
# Remove default nginx config (optional)
RUN rm /etc/nginx/conf.d/default.conf
# Copy built static files
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
