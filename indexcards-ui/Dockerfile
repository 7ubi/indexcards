# Get node
FROM node:latest

# Make working directory
RUN mkdir -p /app
WORKDIR /app

RUN npm install
RUN npm run build --prod

CMD ["npm", "start"]
