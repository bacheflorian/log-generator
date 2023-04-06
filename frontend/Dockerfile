# Instructions to build image and run container
# In GitBash navigate to frontend folder and execute the following command
# For Building Image:
# docker build -t specify_image_name_here .
# For running container:
# winpty docker run -it -p 3000:3000 frontend sh

# Stage 1
FROM node:18.12-alpine3.17 AS BUILD

WORKDIR /app
# COPY [host machine] [container] (first path is relative to the Dockerfile, second is relative to root on the container)
COPY package.json .

RUN npm install

COPY . .

RUN npm run build

# Stage 2
FROM nginx:1-alpine3.17

COPY --from=build /app/build /usr/share/nginx/html

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx/nginx.conf /etc/nginx/conf.d

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]



