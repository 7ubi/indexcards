# Get node
FROM node:leatest

# Make working directory
RUN mkdir -p /app
WORKDIR /app

# Download Angular
RUN npm install -g @angular/cli

# Copy the app package and package-lock.json file
COPY package*.json ./

# Install packages and run on Port 4200
RUN npm install

# Copy or project directory (locally) in the current directory of our docker image (/app)
COPY ./ .

CMD ng serve --host 0.0.0.0 --port 4200
