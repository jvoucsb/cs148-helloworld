FROM node:14-alpine

# Create app directory
WORKDIR /app

# Install app dependencies
# Copy over package.json
# Copy over yarn.lock
COPY package.json ./
COPY yarn.lock ./

RUN yarn

# Bundle app source
COPY . .

RUN yarn build

ENV NODE_ENV production
ENV PORT 3000

EXPOSE 3000
CMD [ "yarn", "start" ]
USER node