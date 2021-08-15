# backend
The backend is a express server written in Typescript.
The backend uses graphql to serve requests thru Apollo server. 
The database is a postgresql with typeorm interfacing with express.
The app is also dockerized. Set any ENV variables in .env or Docker.
# development
- change `.env.EXAMPLE` to `.env`. Set environment variables.
- run `yarn start`. will start nodemon allowing hot changes.
# deploy
- set up postgres db somewhere
- set environment variables
- deploy using Docker or whatever
# notes
- typeorm is on synchronize. In prod, this is not recommended; Use migrations instead.
- Use `host.docker.internal` instead of `localhost` to communicate from the container to localhost service (ex. postgres)
- Building image `docker build --tag express-react-nextjs-backend .`
- Running container `docker run -p 4000:4000 express-react-nextjs-backend`