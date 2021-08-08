# frontend# backend
The frontend is React on nextjs. Can be deployed to vercel. Service is dockerized, ready for deployment.
# development
- change `.env.EXAMPLE` to `.env.local`. Change any values necessary
- run `yarn dev`
- # deployment
- create `.env.production.local`
- set environment variables
- deploy (using Docker or whatever)
# notes
- Building image `docker build --tag express-react-nextjs-frontend .`
- Running container `docker run -p 4000:80 express-react-nextjs-frontend`