# Dockerfile should be in the frontend-nextjs/frontend directory
FROM node:18-alpine

WORKDIR /app

# Copy package files first for better caching
COPY package*.json ./
COPY tsconfig.json ./
COPY next.config.ts ./

# Copy the lib directory (relative to Dockerfile location)
COPY ./lib ./lib
COPY ./components ./components
COPY ./contexts ./contexts

# Copy other necessary directories
COPY ./public ./public
COPY ./app ./app
COPY ./types ./types

# Install dependencies
RUN npm ci

# Build the application
RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]