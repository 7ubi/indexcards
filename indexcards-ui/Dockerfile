# --- Build stage ---
FROM node:26.12.1-alpine AS builder
WORKDIR /app

# Install dependencies in their own layer so they are cached until the lockfile changes
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund

COPY . .
RUN npm run build

# --- Runtime stage ---
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/indexcards-ui/browser/ /usr/share/nginx/html/
