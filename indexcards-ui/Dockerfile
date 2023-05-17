# Get node
FROM node:latest

# Make working directory
RUN mkdir -p /app
WORKDIR /app

COPY . .

RUN npm install
RUN npm run build --prod --host 0.0.0.0

CMD ["npm", "start"]
