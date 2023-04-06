# Instructions to build image and run container
# In GitBash navigate to frontend folder and execute the following command
# For Building Image:
# docker build -t specify_image_name_here .
# For running container:
# winpty docker run -it -p 3000:3000 frontend sh

FROM node:18.12-alpine3.17

# COPY [host machine] [container] (first path is relative to the Dockerfile, second is relative to root on the container)
COPY package.json package-lock.json ./

RUN npm install

COPY . .

EXPOSE 3000

CMD ["npx", "serve", "build"]



